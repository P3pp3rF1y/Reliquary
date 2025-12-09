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
	public static final PrimitiveCodec<Integer> STRING_ENCODED_INT = new PrimitiveCodec<Integer>() {
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

	public static final Codec<Map<Integer, Integer>> PARTIAL_CHARGES_CODEC =
			Codec.unboundedMap(STRING_ENCODED_INT, ExtraCodecs.NON_NEGATIVE_INT);

	public static final StreamCodec<FriendlyByteBuf, Map<Integer, Integer>> PARTIAL_CHARGES_STREAM_CODEC =
			StreamCodec.of((buf, map) -> buf.writeMap(map, ByteBufCodecs.INT, ByteBufCodecs.INT),
					buf -> buf.readMap(ByteBufCodecs.INT, ByteBufCodecs.INT));

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
		int size = Math.max(containerStack.has(ModDataComponents.OVERSIZED_ITEM_CONTAINER_CONTENTS) ? containerStack.get(ModDataComponents.OVERSIZED_ITEM_CONTAINER_CONTENTS).getSlots() : getContainerInitialSize(), getContainerInitialSize());
		return new OversizedComponentItemHandler(containerStack, ModDataComponents.OVERSIZED_ITEM_CONTAINER_CONTENTS.get(), size, this::getContainerSlotLimit, (slot, stack) -> isItemValidForContainerSlot(containerStack, slot, stack));
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

	protected void consumeAndCharge(ItemStack containerStack, int slot, Player player, int freeCapacity, int chargePerItem, int maxCount, Predicate<ItemStack> itemMatches) {
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

	protected int getTotalCharge(ItemStack containerStack) {
		return getTotalCharge(containerStack, FIRST_SLOT);
	}

	protected int getTotalCharge(ItemStack containerStack, int slot) {
		return getMigratedStoredCharge(containerStack, slot) * getSlotWorth(slot) + getPartialCharge(containerStack, slot);
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

	protected boolean addPartialCharge(ItemStack containerStack, int slot, int chargeToAdd) {
		boolean updatedEitherCharge = false;
		int currentCharge = getPartialCharge(containerStack, slot);
		int worth = getSlotWorth(slot);
		int updatedPartialCharge = currentCharge + chargeToAdd;
		if (updatedPartialCharge >= worth) {
			int fullCharges = Math.min(updatedPartialCharge / worth, getContainerSlotLimit(containerStack, slot) - getMigratedStoredCharge(containerStack, slot));
			if (fullCharges > 0) {
				updatedEitherCharge = true;
				addStoredCharge(containerStack, slot, fullCharges, null);
				updatedPartialCharge = Math.min(updatedPartialCharge - (fullCharges * worth), worth - 1);
			}
		}
		setPartialCharge(containerStack, slot, updatedPartialCharge);
		updatedEitherCharge |= updatedPartialCharge != currentCharge;
		return updatedEitherCharge;
	}

	private void setPartialCharges(ItemStack containerStack, Map<Integer, Integer> charges) {
		containerStack.set(ModDataComponents.PARTIAL_CHARGES, ImmutableMap.copyOf(charges));
	}

	public abstract void addStoredCharge(ItemStack containerStack, int slot, int chargeToAdd, @Nullable ItemStack chargeStack);

	protected void extractStoredCharge(ItemStack containerStack, int slot, int chargeToExtract) {
		addStoredCharge(containerStack, slot, -chargeToExtract, null);
	}

	protected int getMigratedStoredCharge(ItemStack containerStack, int slot) {
		int storedCharge = getStoredCharge(containerStack, slot);
		if (!containerStack.has(ModDataComponents.PARTIAL_CHARGES) && storedCharge > 0) {
			storedCharge = migrateLegacyChargeData(containerStack, slot, storedCharge);
		}

		return storedCharge;
	}

	public abstract int getStoredCharge(ItemStack containerStack, int slot);

	protected boolean removeSlotWhenEmpty(int slot) {
		return false;
	}

	protected void removeSlot(ItemStack containerStack, int slot) {
		//noop
	}

	private int migrateLegacyChargeData(ItemStack containerStack, int slot, int storedCharge) {
		int newStoredCharge = storedCharge / getSlotWorth(slot);
		extractStoredCharge(containerStack, slot, storedCharge - newStoredCharge);
		setPartialCharge(containerStack, slot, storedCharge % getSlotWorth(slot));
		return newStoredCharge;
	}

	public boolean useCharge(ItemStack containerStack, int chargeToUse) {
		return useCharge(containerStack, FIRST_SLOT, chargeToUse);
	}

	public boolean useCharge(ItemStack containerStack, int slot, int chargeToUse) {
		int partialCharge = getPartialCharge(containerStack, slot);

		if (partialCharge >= chargeToUse) {
			if (partialCharge == chargeToUse) {
				removePartialCharge(containerStack, slot);
			} else {
				setPartialCharge(containerStack, slot, partialCharge - chargeToUse);
			}
			return true;
		}

		int charge = getMigratedStoredCharge(containerStack, slot);
		int worth = getSlotWorth(slot);

		if (charge * worth + partialCharge >= chargeToUse) {
			int additionalPartialChargeNeeded = chargeToUse - partialCharge;
			int actualChargeToRemove = additionalPartialChargeNeeded / worth + (additionalPartialChargeNeeded % worth > 0 ? 1 : 0);
			int partialChargeToAdd = actualChargeToRemove * worth - chargeToUse;
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
