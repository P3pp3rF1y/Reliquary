package reliquary.item;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import reliquary.block.PedestalBlock;
import reliquary.init.ModDataComponents;
import reliquary.init.ModItems;
import reliquary.item.util.IScrollableItem;
import reliquary.reference.Config;
import reliquary.util.*;

import javax.annotation.Nullable;

public class VoidTearItem extends ChargeableItem implements IScrollableItem {
	public VoidTearItem() {
		super(new Properties());
		NeoForge.EVENT_BUS.addListener(this::onItemPickup);
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		return isEmpty(stack) ? 16 : 1;
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return !(Minecraft.getInstance().options.keyShift.isDown()) && super.isFoil(stack);
	}

	@Override
	protected void addMoreInformation(ItemStack voidTear, @Nullable HolderLookup.Provider registries, TooltipBuilder tooltipBuilder) {
		ItemStack contents = getTearContents(voidTear);

		if (isEmpty(voidTear)) {
			return;
		}

		tooltipBuilder
				.data(this, ".tooltip.mode", Component.translatable(TranslationHelper.transl(this) + ".mode." + getMode(voidTear).getSerializedName().toLowerCase()).withStyle(ChatFormatting.YELLOW),
						Component.translatable(TranslationHelper.translTooltip(this) + ".mode." + getMode(voidTear).getSerializedName().toLowerCase()).withStyle(ChatFormatting.GRAY))
				.description(TranslationHelper.translTooltip(this) + ".mode_change")
				.charge(this, ".tooltip.tear_quantity", contents.getHoverName().getString(), contents.getCount());
		if (isEnabled(voidTear)) {
			tooltipBuilder.absorbActive(contents.getHoverName().getString());
		} else {
			tooltipBuilder.absorb();
		}
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return !isEmpty(stack);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack voidTear = player.getItemInHand(hand);

		if (!level.isClientSide) {
			BlockHitResult rayTraceResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);

			//not letting logic go through if player was sneak clicking inventory or was trying to place a block
			//noinspection ConstantConditions
			if (rayTraceResult != null && rayTraceResult.getType() == HitResult.Type.BLOCK &&
					(InventoryHelper.hasItemHandler(level, rayTraceResult.getBlockPos()) && player.isShiftKeyDown() || hasPlaceableBlock(voidTear))) {
				return new InteractionResultHolder<>(InteractionResult.PASS, voidTear);
			}

			if (isEmpty(voidTear)) {
				return rightClickEmpty(voidTear, player);
			}

			if (getItemQuantity(voidTear) == 0) {
				setEmpty(voidTear);
				return new InteractionResultHolder<>(InteractionResult.SUCCESS, voidTear);
			}

			if (player.isShiftKeyDown()) {
				return super.use(level, player, hand);
			}

			IItemHandler playerInventory = InventoryHelper.getMainInventoryItemHandlerFrom(player);
			if (attemptToEmptyIntoInventory(voidTear, player, playerInventory)) {
				player.level().playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.1F, 0.5F * (RandHelper.getRandomMinusOneToOne(player.level().random) * 0.7F + 1.2F));
				setEmpty(voidTear);
				return new InteractionResultHolder<>(InteractionResult.SUCCESS, voidTear);
			}
		}
		return new InteractionResultHolder<>(InteractionResult.PASS, voidTear);
	}

	private boolean hasPlaceableBlock(ItemStack voidTear) {
		return !isEmpty(voidTear) && getTearContents(voidTear).getItem() instanceof BlockItem;
	}

	private InteractionResultHolder<ItemStack> rightClickEmpty(ItemStack emptyVoidTear, Player player) {
		IItemHandler playerInventory = InventoryHelper.getMainInventoryItemHandlerFrom(player);
		ItemStack target = InventoryHelper.getTargetItem(emptyVoidTear, playerInventory);
		if (!target.isEmpty()) {
			ItemStack filledTear;
			if (emptyVoidTear.getCount() > 1) {
				emptyVoidTear.shrink(1);
				filledTear = new ItemStack(this);
			} else {
				filledTear = emptyVoidTear;
			}
			buildTear(filledTear, target, player, playerInventory, true);
			player.level().playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.1F, 0.5F * (RandHelper.getRandomMinusOneToOne(player.level().random) * 0.7F + 1.2F));
			if (emptyVoidTear.getCount() == 1) {
				return new InteractionResultHolder<>(InteractionResult.SUCCESS, filledTear);
			} else {
				InventoryHelper.addItemToPlayerInventory(player, filledTear);
				return new InteractionResultHolder<>(InteractionResult.SUCCESS, emptyVoidTear);
			}
		}
		return new InteractionResultHolder<>(InteractionResult.PASS, emptyVoidTear);
	}

	private void buildTear(ItemStack voidTear, ItemStack target, Player player, IItemHandler inventory, boolean isPlayerInventory) {
		int quantity = InventoryHelper.getItemQuantity(target, inventory);
		if (isPlayerInventory) {
			if ((quantity - target.getMaxStackSize()) > 0) {
				InventoryHelper.consumeItem(target, player, target.getMaxStackSize(), quantity - target.getMaxStackSize());
				quantity = quantity - target.getMaxStackSize();
			} else {
				InventoryHelper.consumeItem(target, player, 0, 1);
				quantity = 1;
			}
		} else {
			quantity = InventoryHelper.tryToRemoveFromInventory(target, inventory, Config.COMMON.items.voidTear.itemLimit.get());
		}
		setItemStack(voidTear, target);
		setItemQuantity(voidTear, quantity);

		//configurable auto-drain when created.
		if (Config.COMMON.items.voidTear.absorbWhenCreated.get()) {
			toggleEnabled(voidTear);
		}
	}

	@Override
	public void inventoryTick(ItemStack voidTear, Level level, Entity entity, int slotNumber, boolean isSelected) {
		if (level.isClientSide || !(entity instanceof Player player) || player.isSpectator() || level.getGameTime() % 5 != 0) {
			return;
		}
		if (isEnabled(voidTear)) {
			if (isEmpty(voidTear)) {
				setEmpty(voidTear); //fixes issue when creative inventory is opened and active tear gets messed up
				return;
			}

			ItemStack contents = getTearContents(voidTear);

			if (!contents.isEmpty()) {
				fillTear(voidTear, player, contents);
			} else {
				setEmpty(voidTear);
			}
		}
	}

	private void fillTear(ItemStack voidTear, Player player, ItemStack contents) {
		IItemHandler playerInventory = InventoryHelper.getMainInventoryItemHandlerFrom(player);
		int itemQuantity = InventoryHelper.getItemQuantity(contents, playerInventory);

		//doesn't absorb in creative mode. this is mostly for testing, it prevents the item from having unlimited *whatever* for eternity.
		if (getItemQuantity(voidTear) <= Config.COMMON.items.voidTear.itemLimit.get() && itemQuantity > getKeepQuantity(voidTear) && InventoryHelper.consumeItem(contents, player, getKeepQuantity(voidTear), itemQuantity - getKeepQuantity(voidTear)) && !player.isCreative()) {
			setItemQuantity(voidTear, getItemQuantity(voidTear) + itemQuantity - getKeepQuantity(voidTear));
		}
		if (getMode(voidTear) != Mode.NO_REFILL) {
			attemptToReplenish(player, voidTear);
		}
	}

	private void attemptToReplenish(Player player, ItemStack voidTear) {
		IItemHandler playerInventory = InventoryHelper.getMainInventoryItemHandlerFrom(player);
		if (fillFirstFirstStackFound(voidTear, playerInventory)) {
			return;
		}

		int slot;
		while (getItemQuantity(voidTear) > 1 && (slot = player.getInventory().getFreeSlot()) != -1) {
			ItemStack newStack = getTearContents(voidTear).copy();
			int quantityToDecrease = Math.min(newStack.getMaxStackSize(), getItemQuantity(voidTear) - 1);
			newStack.setCount(quantityToDecrease);
			player.getInventory().setItem(slot, newStack);
			setItemQuantity(voidTear, getItemQuantity(voidTear) - quantityToDecrease);
			if (getMode(voidTear) != Mode.FULL_INVENTORY) {
				return;
			}
		}
	}

	private boolean fillFirstFirstStackFound(ItemStack voidTear, IItemHandler h) {
		for (int slot = 0; slot < h.getSlots(); slot++) {
			ItemStack stackFound = h.getStackInSlot(slot);

			if (ItemStack.isSameItemSameComponents(stackFound, getTearContents(voidTear))) {
				int quantityToDecrease = Math.min(stackFound.getMaxStackSize() - stackFound.getCount(), getItemQuantity(voidTear) - 1);
				stackFound.grow(quantityToDecrease);
				setItemQuantity(voidTear, getItemQuantity(voidTear) - quantityToDecrease);
				if (getMode(voidTear) != Mode.FULL_INVENTORY) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		Player player = context.getPlayer();
		if (player == null) {
			return InteractionResult.PASS;
		}
		InteractionHand hand = context.getHand();
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		ItemStack voidTear = player.getItemInHand(hand);
		if (level.getBlockState(pos).getBlock() instanceof PedestalBlock) {
			return InteractionResult.PASS;
		}

		IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
		if (handler != null) {
			return processItemHandlerInteraction(player, hand, level, voidTear, handler);
		} else if (!level.isClientSide && hasPlaceableBlock(voidTear) && getItemQuantity(voidTear) > 0) {
			ItemStack containerItem = getTearContents(voidTear);
			BlockItem itemBlock = (BlockItem) containerItem.getItem();

			Direction face = context.getClickedFace();
			NoPlayerBlockItemUseContext noPlayerBlockItemUseContext = new NoPlayerBlockItemUseContext(level, pos, new ItemStack(itemBlock), face);
			if (noPlayerBlockItemUseContext.canPlace() && itemBlock.place(noPlayerBlockItemUseContext).consumesAction()) {
				setItemQuantity(voidTear, getItemQuantity(voidTear) - 1);
			}
		}
		return InteractionResult.PASS;
	}

	private InteractionResult processItemHandlerInteraction(Player player, InteractionHand hand, Level level, ItemStack voidTear, IItemHandler itemHandler) {
		if (!level.isClientSide) {
			if (isEmpty(voidTear)) {
				return onItemUseFirstEmpty(voidTear, itemHandler, player, hand);
			}

			//enabled == drinking mode, we're going to drain the inventory of items.
			if (isEnabled(voidTear)) {
				drainInventory(voidTear, player, itemHandler);
			} else {
				emptyIntoInventory(player, hand, voidTear, itemHandler);
			}
		}
		return InteractionResult.SUCCESS;
	}

	private void emptyIntoInventory(Player player, InteractionHand hand, ItemStack voidTear, IItemHandler itemHandler) {
		if (attemptToEmptyIntoInventory(voidTear, player, itemHandler)) {
			setEmpty(voidTear);
			player.setItemInHand(hand, voidTear);
		}
	}

	private InteractionResult onItemUseFirstEmpty(ItemStack emptyVoidTear, IItemHandler inventory, Player player, InteractionHand hand) {
		ItemStack target = InventoryHelper.getTargetItem(emptyVoidTear, inventory);
		if (!target.isEmpty()) {
			ItemStack filledTear;
			if (emptyVoidTear.getCount() > 1) {
				emptyVoidTear.shrink(1);
				filledTear = new ItemStack(this);
			} else {
				filledTear = emptyVoidTear;
			}
			buildTear(filledTear, target, player, inventory, false);

			player.level().playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.1F, 0.5F * (RandHelper.getRandomMinusOneToOne(player.level().random) * 0.7F + 1.2F));
			if (emptyVoidTear.getCount() == 1) {
				player.setItemInHand(hand, filledTear);
			} else {
				InventoryHelper.addItemToPlayerInventory(player, filledTear);
			}
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	private boolean attemptToEmptyIntoInventory(ItemStack stack, Player player, IItemHandler inventory) {
		ItemStack contents = getTearContents(stack).copy();
		contents.setCount(1);

		int quantity = getItemQuantity(stack);
		int maxNumberToEmpty = player.isShiftKeyDown() ? quantity : Math.min(contents.getMaxStackSize(), quantity);

		quantity -= InventoryHelper.tryToAddToInventory(contents, inventory, maxNumberToEmpty);

		if (quantity == 0) {
			player.level().playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.1F, 0.5F * (RandHelper.getRandomMinusOneToOne(player.level().random) * 0.7F + 1.8F));
			return true;
		} else {
			setItemQuantity(stack, quantity);
			player.level().playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.1F, 0.5F * (RandHelper.getRandomMinusOneToOne(player.level().random) * 0.7F + 1.2F));
			return false;
		}
	}

	private void drainInventory(ItemStack stack, Player player, IItemHandler inventory) {
		ItemStack contents = getTearContents(stack);
		int quantity = getItemQuantity(stack);

		int quantityDrained = InventoryHelper.tryToRemoveFromInventory(contents, inventory, Config.COMMON.items.voidTear.itemLimit.get() - quantity);

		if (quantityDrained <= 0) {
			return;
		}

		player.level().playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.1F, 0.5F * (RandHelper.getRandomMinusOneToOne(player.level().random) * 0.7F + 1.2F));

		setItemQuantity(stack, quantity + quantityDrained);
	}

	public static ItemStack getTearContents(ItemStack voidTear) {
		return ModItems.VOID_TEAR.get().getFromHandler(voidTear, handler -> handler.getStackInSlot(FIRST_SLOT));
	}

	@Override
	protected int getContainerSlotLimit(ItemStack stack, int slot) {
		return getItemQuantity(stack) == 0 ? 0 : Config.COMMON.items.voidTear.itemLimit.get();
	}

	@Override
	protected boolean isItemValidForContainerSlot(ItemStack containerStack, int slot, ItemStack stack) {
		return getFromHandler(containerStack, handler -> handler.getStackInSlot(FIRST_SLOT).isEmpty() || ItemStack.isSameItemSameComponents(getTearContents(containerStack), stack));
	}

	@Override
	public void addStoredCharge(ItemStack containerStack, int slot, int chargeToAdd, @Nullable ItemStack chargeStack) {
		if (slot > FIRST_SLOT) {
			return;
		}
		runOnHandler(containerStack, handler -> {
			ItemStack currentStack = handler.getStackInSlot(FIRST_SLOT);
			if (currentStack.isEmpty()) {
				if (chargeStack != null) {
					handler.setStackInSlot(slot, chargeStack);
				}
				return;
			}

			currentStack.setCount(Math.min(Config.COMMON.items.voidTear.itemLimit.get(), currentStack.getCount() + chargeToAdd));
			handler.setStackInSlot(FIRST_SLOT, currentStack);
		});
	}

	@Override
	public int getStoredCharge(ItemStack containerStack, int slot) {
		return slot == 0 ? getItemQuantity(containerStack) : 0;
	}

	private void setItemStack(ItemStack voidTear, ItemStack stack) {
		runOnHandler(voidTear, handler -> handler.setStackInSlot(FIRST_SLOT, stack));
	}

	private void setItemQuantity(ItemStack voidTear, int quantity) {
		runOnHandler(voidTear, handler -> {
			ItemStack stack = handler.getStackInSlot(FIRST_SLOT);
			stack.setCount(quantity);
			handler.setStackInSlot(FIRST_SLOT, stack);
		});
	}

	private int getItemQuantity(ItemStack voidTear) {
		return getFromHandler(voidTear, handler -> handler.getCountInSlot(FIRST_SLOT));
	}

	@Override
	public InteractionResult onMouseScrolled(ItemStack voidTear, Player player, double scrollDelta) {
		if (player.level().isClientSide) {
			return InteractionResult.PASS;
		}
		cycleMode(voidTear, scrollDelta > 0);
		return InteractionResult.SUCCESS;
	}

	public enum Mode implements StringRepresentable {
		ONE_STACK, FULL_INVENTORY, NO_REFILL;

		public static final Codec<Mode> CODEC = StringRepresentable.fromEnum(Mode::values);
		public static final StreamCodec<FriendlyByteBuf, Mode> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(Mode.class);

		@Override
		public String getSerializedName() {
			return name();
		}

		public Mode next() {
			return VALUES[(ordinal() + 1) % VALUES.length];
		}

		public Mode previous() {
			return VALUES[Math.floorMod(ordinal() - 1, VALUES.length)];
		}

		private static final Mode[] VALUES;

		static {
			ImmutableMap.Builder<String, Mode> builder = new ImmutableMap.Builder<>();
			for (Mode value : Mode.values()) {
				builder.put(value.getSerializedName(), value);
			}
			VALUES = values();
		}
	}

	public Mode getMode(ItemStack voidTear) {
		return voidTear.getOrDefault(ModDataComponents.VOID_TEAR_MODE, Mode.ONE_STACK);
	}

	private void setMode(ItemStack voidTear, Mode mode) {
		voidTear.set(ModDataComponents.VOID_TEAR_MODE, mode);
	}

	private void cycleMode(ItemStack voidTear, boolean next) {
		if (isEmpty(voidTear)) {
			return;
		}

		if (next) {
			setMode(voidTear, getMode(voidTear).next());
		} else {
			setMode(voidTear, getMode(voidTear).previous());
		}
	}

	private int getKeepQuantity(ItemStack voidTear) {
		Mode mode = getMode(voidTear);

		if (mode == Mode.NO_REFILL) {
			return 0;
		}
		if (mode == Mode.ONE_STACK) {
			return getTearContents(voidTear).getMaxStackSize();
		}

		return Integer.MAX_VALUE;
	}

	private void onItemPickup(ItemEntityPickupEvent.Pre event) {
		ItemStack pickedUpStack = event.getItemEntity().getItem();
		Player player = event.getPlayer();
		ItemEntity itemEntity = event.getItemEntity();

		for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
			ItemStack tearStack = player.getInventory().getItem(slot);
			if (tearStack.getItem() == this && isEnabled(tearStack) && tryToPickupWithTear(event, pickedUpStack, player, itemEntity, tearStack)) {
				break;
			}
		}
	}

	private boolean tryToPickupWithTear(ItemEntityPickupEvent.Pre event, ItemStack pickedUpStack, Player player, ItemEntity itemEntity, ItemStack tearStack) {
		int tearItemQuantity = getItemQuantity(tearStack);
		if (canAbsorbStack(pickedUpStack, tearStack)) {
			IItemHandler playerInventory = InventoryHelper.getMainInventoryItemHandlerFrom(player);
			int playerItemQuantity = InventoryHelper.getItemQuantity(pickedUpStack, playerInventory);

			if (playerItemQuantity + pickedUpStack.getCount() >= getKeepQuantity(tearStack) || player.getInventory().getFreeSlot() == -1) {
				setItemQuantity(tearStack, tearItemQuantity + pickedUpStack.getCount());
				if (!itemEntity.isSilent()) {
					RandomSource rand = itemEntity.level().random;
					itemEntity.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, (RandHelper.getRandomMinusOneToOne(rand) * 0.7F + 1.0F) * 2.0F);
				}
				itemEntity.discard();
				event.setCanPickup(TriState.FALSE);
				return true;
			}
		}
		return false;
	}

	boolean canAbsorbStack(ItemStack pickedUpStack, ItemStack tearStack) {
		return ItemStack.isSameItemSameComponents(getTearContents(tearStack), pickedUpStack) && getItemQuantity(tearStack) + pickedUpStack.getCount() <= Config.COMMON.items.voidTear.itemLimit.get();
	}

	public boolean isEmpty(ItemStack voidTear) {
		return !voidTear.has(ModDataComponents.ENABLED) || getItemQuantity(voidTear) == 0;
	}

	private void setEmpty(ItemStack voidTear) {
		voidTear.remove(ModDataComponents.ENABLED);
		removeContainerContents(voidTear);
	}
}
