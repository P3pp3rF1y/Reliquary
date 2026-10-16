package reliquary.item;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import reliquary.init.ModDataComponents;
import reliquary.item.component.OversizedComponentItemHandler;
import reliquary.util.InventoryHelper;

import javax.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class ChargeableItem extends ToggleableItem {
	public static final PrimitiveCodec<Integer> STRING_ENCODED_INT = new PrimitiveCodec<>() {
		@Override
		public <T> DataResult<Integer> read(final DynamicOps<T> ops, final T input) {
			return ops.getStringValue(input).map(s -> {
				if (s.startsWith("i")) {
					return Integer.parseInt(s.substring(1));
				} else {
					return Integer.parseInt(s);
				}
			});
		}

		@Override
		public <T> T write(final DynamicOps<T> ops, final Integer value) {
			return ops.createString("i" + value);
		}

		@Override
		public String toString() {
			return "Int";
		}
	};

	public static final Codec<Map<Integer, Integer>> PARTIAL_CHARGES_CODEC = Codec.unboundedMap(STRING_ENCODED_INT, ExtraCodecs.NON_NEGATIVE_INT);
	private static final int MAX_PARTIAL_CHARGES = 64;

	public static final StreamCodec<FriendlyByteBuf, Map<Integer, Integer>> PARTIAL_CHARGES_STREAM_CODEC = ByteBufCodecs.map(HashMap::new, ByteBufCodecs.INT,
			ByteBufCodecs.INT, MAX_PARTIAL_CHARGES);

	protected static final int FIRST_SLOT = 0;

	protected ChargeableItem(Properties properties, Supplier<Boolean> isDisabled) {
		super(properties, isDisabled);
	}

	protected ChargeableItem(Properties properties) {
		this(properties, () -> false);
	}

	protected <T> T getFromHandler(ItemStack stack, Function<OversizedComponentItemHandler, T> getter) {
		return getter.apply(createHandler(stack));
	}

	protected void runOnHandler(ItemStack stack, Consumer<OversizedComponentItemHandler> runner) {
		runner.accept(createHandler(stack));
	}

	public OversizedComponentItemHandler createHandler(ItemStack containerStack) {
		int size = Math.max(containerStack.has(ModDataComponents.OVERSIZED_ITEM_CONTAINER_CONTENTS)
				? containerStack.get(ModDataComponents.OVERSIZED_ITEM_CONTAINER_CONTENTS).getSlots()
				: getContainerInitialSize(), getContainerInitialSize());
		return new OversizedComponentItemHandler(containerStack, ModDataComponents.OVERSIZED_ITEM_CONTAINER_CONTENTS.get(), size, this::getContainerSlotLimit,
				(slot, stack) -> isItemValidForContainerSlot(containerStack, slot, stack));
	}

	protected int getSlotWorth(int slot) {
		return 1;
	}

	protected void removeContainerContents(ItemStack stack) {
		stack.remove(ModDataComponents.OVERSIZED_ITEM_CONTAINER_CONTENTS);
	}

	protected int getContainerInitialSize() {
		return 1;
	}

	protected int getContainerSlotLimit(ItemStack stack, int slot) {
		return getContainerSlotLimit(slot);
	}

	protected int getContainerSlotLimit(int slot) {
		return 64;
	}

	protected abstract boolean isItemValidForContainerSlot(ItemStack containerStack, int slot, ItemStack stack);

	protected void consumeAndCharge(ItemStack containerStack, int slot, Player player, int freeCapacity, int chargePerItem, int maxCount) {
		Predicate<ItemStack> isValidStack = stack -> isItemValidForContainerSlot(containerStack, slot, stack);
		consumeAndCharge(containerStack, slot, player, freeCapacity, chargePerItem, maxCount, isValidStack);
	}

	protected void consumeAndCharge(ItemStack containerStack, int slot, Player player, int freeCapacity, int chargePerItem, int maxCount,
			Predicate<ItemStack> itemMatches) {
		int maximumToConsume = Math.min(freeCapacity / chargePerItem, maxCount);
		if (maximumToConsume == 0) {
			return;
		}
		ItemStack consumedStack = InventoryHelper.consumeItemStack(itemMatches, player, maximumToConsume);
		if (consumedStack.getCount() > 0) {
			addStoredCharge(containerStack, slot, consumedStack.getCount() * chargePerItem, consumedStack);
			if (!containerStack.has(ModDataComponents.PARTIAL_CHARGES)) {
				HashMap<Integer, Integer> charges = new HashMap<>();
				charges.put(slot, 0);
				setPartialCharges(containerStack, charges);
			}
		}
	}

	private int getPartialCharge(ItemStack containerStack, int slot) {
		Map<Integer, Integer> charges = containerStack.get(ModDataComponents.PARTIAL_CHARGES);
		if (charges != null) {
			return charges.getOrDefault(slot, 0);
		}
		return 0;
	}

	public int getTotalCharge(ItemStack containerStack) {
		return getTotalCharge(containerStack, FIRST_SLOT);
	}

	public int getTotalCharge(ItemStack containerStack, int slot) {
		long totalCharge = (long) getMigratedStoredCharge(containerStack, slot) * getSlotWorth(slot) + getPartialCharge(containerStack, slot);
		return (int) Math.min(totalCharge, Integer.MAX_VALUE);
	}

	private void setPartialCharge(ItemStack containerStack, int slot, int charge) {
		Map<Integer, Integer> charges = containerStack.get(ModDataComponents.PARTIAL_CHARGES);
		charges = charges != null ? new HashMap<>(charges) : new HashMap<>();
		charges.put(slot, charge);
		setPartialCharges(containerStack, charges);
	}

	private void removePartialCharge(ItemStack containerStack, int slot) {
		Map<Integer, Integer> charges = containerStack.get(ModDataComponents.PARTIAL_CHARGES);
		if (charges != null && charges.containsKey(slot)) {
			charges = new HashMap<>(charges);
			charges.remove(slot);
			setPartialCharges(containerStack, charges);
		}
	}

	protected boolean addPartialCharge(ItemStack containerStack, int slot, int chargeToAdd, int pointLimit, ItemStack chargeStack) {
		int worth = getSlotWorth(slot);
		int storedCharge = getMigratedStoredCharge(containerStack, slot);
		int currentCharge = getPartialCharge(containerStack, slot);
		long totalCharge = (long) storedCharge * worth + currentCharge;
		int remainingCapacity = Math.toIntExact(Math.max(pointLimit - totalCharge, 0));
		int acceptedCharge = Math.min(Math.max(chargeToAdd, 0), remainingCapacity);
		int updatedPartialCharge = currentCharge + acceptedCharge;
		if (updatedPartialCharge >= worth) {
			int maxPhysicalCharge = pointLimit / worth;
			int fullCharges = Math.min(updatedPartialCharge / worth, Math.max(maxPhysicalCharge - storedCharge, 0));
			if (fullCharges > 0) {
				ItemStack storedChargeStack = chargeStack.copy();
				storedChargeStack.setCount(fullCharges);
				addStoredCharge(containerStack, slot, fullCharges, storedChargeStack);
				updatedPartialCharge -= fullCharges * worth;
			}
		}
		setPartialCharge(containerStack, slot, updatedPartialCharge);
		return acceptedCharge > 0;
	}

	private void setPartialCharges(ItemStack containerStack, Map<Integer, Integer> charges) {
		containerStack.set(ModDataComponents.PARTIAL_CHARGES, ImmutableMap.copyOf(charges));
	}

	public abstract void addStoredCharge(ItemStack containerStack, int slot, int chargeToAdd, @Nullable ItemStack chargeStack);

	protected void extractStoredCharge(ItemStack containerStack, int slot, int chargeToExtract) {
		addStoredCharge(containerStack, slot, -chargeToExtract, null);
	}

	protected int getMigratedStoredCharge(ItemStack containerStack, int slot) {
		if (!containerStack.has(ModDataComponents.PARTIAL_CHARGES)) {
			migrateLegacyChargeData(containerStack);
		}

		return getStoredCharge(containerStack, slot);
	}

	public abstract int getStoredCharge(ItemStack containerStack, int slot);

	protected boolean removeSlotWhenEmpty(int slot) {
		return false;
	}

	protected void removeSlot(ItemStack containerStack, int slot) {
		// noop
	}

	private void migrateLegacyChargeData(ItemStack containerStack) {
		Map<Integer, Integer> charges = new HashMap<>();
		int slots = createHandler(containerStack).getSlots();
		for (int slot = 0; slot < slots; slot++) {
			int worth = getSlotWorth(slot);
			int storedCharge = getStoredCharge(containerStack, slot);
			if (storedCharge <= 0 || worth <= 1) {
				continue;
			}

			int physicalCharge = storedCharge / worth;
			extractStoredCharge(containerStack, slot, storedCharge - physicalCharge);
			int partialCharge = storedCharge % worth;
			if (partialCharge > 0) {
				charges.put(slot, partialCharge);
			}
		}
		setPartialCharges(containerStack, charges);
	}

	public boolean useCharge(ItemStack containerStack, int chargeToUse) {
		return useCharge(containerStack, FIRST_SLOT, chargeToUse);
	}

	public boolean useCharge(ItemStack containerStack, int slot, int chargeToUse) {
		int charge = getMigratedStoredCharge(containerStack, slot);
		int partialCharge = getPartialCharge(containerStack, slot);

		if (partialCharge >= chargeToUse) {
			if (partialCharge == chargeToUse) {
				removePartialCharge(containerStack, slot);
			} else {
				setPartialCharge(containerStack, slot, partialCharge - chargeToUse);
			}
			return true;
		}

		int worth = getSlotWorth(slot);

		if ((long) charge * worth + partialCharge >= chargeToUse) {
			long additionalPartialChargeNeeded = (long) chargeToUse - partialCharge;
			int actualChargeToRemove = (int) (additionalPartialChargeNeeded / worth + (additionalPartialChargeNeeded % worth > 0 ? 1 : 0));
			int partialChargeToAdd = (int) ((long) actualChargeToRemove * worth - chargeToUse);
			if (actualChargeToRemove == charge && removeSlotWhenEmpty(slot)) {
				removeSlot(containerStack, slot);
			} else {
				extractStoredCharge(containerStack, slot, actualChargeToRemove);
			}

			int newPartialCharge = partialCharge + partialChargeToAdd;
			if (newPartialCharge > 0) {
				setPartialCharge(containerStack, slot, newPartialCharge);
			} else {
				removePartialCharge(containerStack, slot);
			}
			return true;
		}

		return false;
	}

	protected boolean addItemToContainer(ItemStack container, Item item, int chargeToAdd) {
		ItemStack stack = new ItemStack(item);
		stack.setCount(chargeToAdd);
		return getFromHandler(container, handler -> handler.insertItemOrAddIntoNewSlotIfNoStackMatches(stack)).isEmpty();
	}

	public boolean removeItemFromInternalStorage(ItemStack stack, int slot, int quantityToRemove, boolean simulate, Player player) {
		if (player.isCreative()) {
			return true;
		}

		return getFromHandler(stack, handler -> handler.extractItemAndRemoveSlotIfEmpty(slot, quantityToRemove, simulate)).getCount() == quantityToRemove;
	}
}
