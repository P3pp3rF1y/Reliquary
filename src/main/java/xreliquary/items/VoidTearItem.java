package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import xreliquary.blocks.PedestalBlock;
import xreliquary.items.util.VoidTearItemStackHandler;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;
import xreliquary.util.NoPlayerBlockItemUseContext;
import xreliquary.util.RandHelper;
import xreliquary.util.StackHelper;
import xreliquary.util.WorldHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

public class VoidTearItem extends ToggleableItem {

	private static final String CONTENTS_TAG = "contents";
	private static final String TOOLTIP_PREFIX = "tooltip.";

	public VoidTearItem() {
		super(Names.Items.VOID_TEAR, new Properties());
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return isEmpty(stack) ? 16 : 1;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return new ICapabilitySerializable<CompoundNBT>() {
			VoidTearItemStackHandler itemHandler = new VoidTearItemStackHandler();

			@Override
			public CompoundNBT serializeNBT() {
				return itemHandler.serializeNBT();
			}

			@Override
			public void deserializeNBT(CompoundNBT nbt) {
				itemHandler.deserializeNBT(nbt);
			}

			@Override
			public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(capability, LazyOptional.of(() -> itemHandler));
			}
		};
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return !(Minecraft.getInstance().gameSettings.keyBindSneak.isKeyDown()) && super.hasEffect(stack);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack voidTear, @Nullable World world, List<ITextComponent> tooltip) {
		ItemStack contents = getContainerItem(voidTear, true);

		if (isEmpty(voidTear, true)) {
			return;
		}

		if (isEnabled(voidTear)) {
			LanguageHelper.formatTooltip(TOOLTIP_PREFIX + Reference.MOD_ID + ".absorb_active", ImmutableMap.of("item", TextFormatting.YELLOW + contents.getDisplayName().getString()), tooltip);
			tooltip.add(new TranslationTextComponent(TOOLTIP_PREFIX + Reference.MOD_ID + ".absorb_tear"));
		}
		LanguageHelper.formatTooltip(TOOLTIP_PREFIX + Reference.MOD_ID + ".tear_quantity", ImmutableMap.of("item", contents.getDisplayName().getString(), "amount", Integer.toString(contents.getCount())), tooltip);
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return true;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack voidTear = player.getHeldItem(hand);

		if (!world.isRemote) {
			RayTraceResult rayTraceResult = rayTrace(world, player, RayTraceContext.FluidMode.NONE);

			//not letting logic go through if player was sneak clicking inventory or was trying to place a block
			//noinspection ConstantConditions
			if (rayTraceResult != null && rayTraceResult.getType() == RayTraceResult.Type.BLOCK &&
					(InventoryHelper.hasItemHandler(world, ((BlockRayTraceResult) rayTraceResult).getPos()) && player.isSneaking() || getContainerItem(voidTear).getItem() instanceof BlockItem)) {
				return new ActionResult<>(ActionResultType.PASS, voidTear);
			}

			if (isEmpty(voidTear)) {
				return rightClickEmpty(voidTear, player);
			}

			if (getItemQuantity(voidTear) == 0) {
				setEmpty(voidTear);
				return new ActionResult<>(ActionResultType.SUCCESS, voidTear);
			}

			if (player.isSneaking()) {
				return super.onItemRightClick(world, player, hand);
			}

			if (Boolean.TRUE.equals(InventoryHelper.getItemHandlerFrom(player).map(h -> attemptToEmptyIntoInventory(voidTear, player, h)).orElse(false))) {
				player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, 0.5F * (RandHelper.getRandomMinusOneToOne(player.world.rand) * 0.7F + 1.2F));
				setEmpty(voidTear);
				return new ActionResult<>(ActionResultType.SUCCESS, voidTear);
			}
		}
		return new ActionResult<>(ActionResultType.PASS, voidTear);
	}

	private ActionResult<ItemStack> rightClickEmpty(ItemStack emptyVoidTear, PlayerEntity player) {
		return InventoryHelper.getItemHandlerFrom(player).map(playerInventory -> {
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
				player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, 0.5F * (RandHelper.getRandomMinusOneToOne(player.world.rand) * 0.7F + 1.2F));
				if (emptyVoidTear.getCount() == 1) {
					return new ActionResult<>(ActionResultType.SUCCESS, filledTear);
				} else {
					InventoryHelper.addItemToPlayerInventory(player, filledTear);
					return new ActionResult<>(ActionResultType.SUCCESS, emptyVoidTear);
				}
			}
			return new ActionResult<>(ActionResultType.PASS, emptyVoidTear);
		}).orElse(new ActionResult<>(ActionResultType.PASS, emptyVoidTear));
	}

	private void buildTear(ItemStack voidTear, ItemStack target, PlayerEntity player, IItemHandler inventory, boolean isPlayerInventory) {

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
			quantity = InventoryHelper.tryToRemoveFromInventory(target, inventory, Settings.COMMON.items.voidTear.itemLimit.get());
		}
		setItemStack(voidTear, target);
		setItemQuantity(voidTear, quantity);

		//configurable auto-drain when created.
		NBTHelper.putBoolean("enabled", voidTear, Settings.COMMON.items.voidTear.absorbWhenCreated.get());
	}

	@Override
	public void inventoryTick(ItemStack voidTear, World world, Entity entity, int slotNumber, boolean isSelected) {
		if (!world.isRemote) {
			if (!(entity instanceof PlayerEntity)) {
				return;
			}

			PlayerEntity player = (PlayerEntity) entity;

			if (isEnabled(voidTear)) {
				ItemStack contents = getContainerItem(voidTear);

				if (!contents.isEmpty()) {
					fillTear(voidTear, player, contents);
				} else {
					setEmpty(voidTear);
				}
			}
		}
	}

	private void fillTear(ItemStack voidTear, PlayerEntity player, ItemStack contents) {
		int itemQuantity = InventoryHelper.getItemHandlerFrom(player).map(h -> InventoryHelper.getItemQuantity(contents, h)).orElse(9);

		//doesn't absorb in creative mode.. this is mostly for testing, it prevents the item from having unlimited *whatever* for eternity.
		if (getItemQuantity(voidTear) <= Settings.COMMON.items.voidTear.itemLimit.get() && itemQuantity > getKeepQuantity(voidTear) && InventoryHelper.consumeItem(contents, player, getKeepQuantity(voidTear), itemQuantity - getKeepQuantity(voidTear)) && !player.isCreative()) {
			setItemQuantity(voidTear, getItemQuantity(voidTear) + itemQuantity - getKeepQuantity(voidTear));
		}
		if (getMode(voidTear) != Mode.NO_REFILL) {
			attemptToReplenish(player, voidTear);
		}
	}

	private void attemptToReplenish(PlayerEntity player, ItemStack voidTear) {
		if (Boolean.TRUE.equals(InventoryHelper.getItemHandlerFrom(player).map(h -> fillFirstFirstStackFound(voidTear, h)).orElse(false))) {
			return;
		}

		int slot;
		while (getItemQuantity(voidTear) > 1 && (slot = player.inventory.getFirstEmptyStack()) != -1) {
			ItemStack newStack = getContainerItem(voidTear).copy();
			int quantityToDecrease = Math.min(newStack.getMaxStackSize(), getItemQuantity(voidTear) - 1);
			newStack.setCount(quantityToDecrease);
			player.inventory.setInventorySlotContents(slot, newStack);
			setItemQuantity(voidTear, getItemQuantity(voidTear) - quantityToDecrease);
			if (getMode(voidTear) != Mode.FULL_INVENTORY) {
				return;
			}
		}
	}

	private boolean fillFirstFirstStackFound(ItemStack voidTear, IItemHandler h) {
		for (int slot = 0; slot < h.getSlots(); slot++) {
			ItemStack stackFound = h.getStackInSlot(slot);

			if (StackHelper.isItemAndNbtEqual(stackFound, getContainerItem(voidTear))) {
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
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
		PlayerEntity player = context.getPlayer();
		if (player == null) {
			return ActionResultType.PASS;
		}
		Hand hand = context.getHand();
		World world = context.getWorld();
		BlockPos pos = context.getPos();
		ItemStack voidTear = player.getHeldItem(hand);
		if (world.getBlockState(pos).getBlock() instanceof PedestalBlock) {
			return ActionResultType.PASS;
		}

		LazyOptional<IItemHandler> handler = WorldHelper.getTile(world, pos).map(InventoryHelper::getItemHandlerFrom).orElse(LazyOptional.empty());
		if (handler.isPresent()) {
			return handler.map(h -> processItemHandlerInteraction(player, hand, world, voidTear, h)).orElse(ActionResultType.FAIL);
		} else if (getContainerItem(voidTear).getItem() instanceof BlockItem && getItemQuantity(voidTear) > 0) {
			ItemStack containerItem = getContainerItem(voidTear);
			BlockItem itemBlock = (BlockItem) containerItem.getItem();

			Direction face = context.getFace();
			NoPlayerBlockItemUseContext noPlayerBlockItemUseContext = new NoPlayerBlockItemUseContext(world, pos, new ItemStack(itemBlock), face);
			if (noPlayerBlockItemUseContext.canPlace()) {
				setItemQuantity(voidTear, getItemQuantity(voidTear) - 1);
				if (!world.isRemote) {
					itemBlock.tryPlace(noPlayerBlockItemUseContext);
				}
			}
		}
		return ActionResultType.PASS;
	}

	private ActionResultType processItemHandlerInteraction(PlayerEntity player, Hand hand, World world, ItemStack voidTear, IItemHandler itemHandler) {
		if (!world.isRemote) {
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
		return ActionResultType.SUCCESS;
	}

	private void emptyIntoInventory(PlayerEntity player, Hand hand, ItemStack voidTear, IItemHandler itemHandler) {
		attemptToEmptyIntoInventory(voidTear, player, itemHandler);
		if (getItemQuantity(voidTear) <= 0) {
			setEmpty(voidTear);
			player.setHeldItem(hand, voidTear);
		}
	}

	private ActionResultType onItemUseFirstEmpty(ItemStack emptyVoidTear, IItemHandler inventory, PlayerEntity player, Hand hand) {
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

			player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, 0.5F * (RandHelper.getRandomMinusOneToOne(player.world.rand) * 0.7F + 1.2F));
			if (emptyVoidTear.getCount() == 1) {
				player.setHeldItem(hand, filledTear);
			} else {
				InventoryHelper.addItemToPlayerInventory(player, filledTear);
			}
			return ActionResultType.SUCCESS;
		}

		return ActionResultType.PASS;
	}

	private boolean attemptToEmptyIntoInventory(ItemStack stack, PlayerEntity player, IItemHandler inventory) {
		ItemStack contents = getContainerItem(stack).copy();
		contents.setCount(1);

		int quantity = getItemQuantity(stack);
		int maxNumberToEmpty = player.isSneaking() ? quantity : Math.min(contents.getMaxStackSize(), quantity);

		quantity -= InventoryHelper.tryToAddToInventory(contents, inventory, maxNumberToEmpty);

		setItemQuantity(stack, quantity);
		if (quantity == 0) {
			player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, 0.5F * (RandHelper.getRandomMinusOneToOne(player.world.rand) * 0.7F + 1.8F));
			return true;
		} else {
			player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, 0.5F * (RandHelper.getRandomMinusOneToOne(player.world.rand) * 0.7F + 1.2F));
			return false;
		}
	}

	private void drainInventory(ItemStack stack, PlayerEntity player, IItemHandler inventory) {
		ItemStack contents = getContainerItem(stack);
		int quantity = getItemQuantity(stack);

		int quantityDrained = InventoryHelper.tryToRemoveFromInventory(contents, inventory, Settings.COMMON.items.voidTear.itemLimit.get() - quantity);

		if (quantityDrained <= 0) {
			return;
		}

		player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, 0.5F * (RandHelper.getRandomMinusOneToOne(player.world.rand) * 0.7F + 1.2F));

		setItemQuantity(stack, quantity + quantityDrained);
	}

	@Nullable
	@Override
	public CompoundNBT getShareTag(ItemStack voidTear) {
		CompoundNBT nbt = super.getShareTag(voidTear);

		if (isEmpty(voidTear)) {
			return nbt;
		}

		if (nbt == null) {
			nbt = new CompoundNBT();
		}
		nbt.putInt("count", getItemQuantity(voidTear));
		nbt.put(CONTENTS_TAG, getContainerItem(voidTear).write(new CompoundNBT()));

		return nbt;
	}

	@Override
	public ItemStack getContainerItem(ItemStack voidTear) {
		return getContainerItem(voidTear, false);
	}

	public ItemStack getContainerItem(ItemStack voidTear, boolean isClient) {
		if (isClient) {
			CompoundNBT nbt = voidTear.getTag();
			if (nbt == null || !nbt.contains(CONTENTS_TAG)) {
				return ItemStack.EMPTY;
			}
			ItemStack contents = ItemStack.read(nbt.getCompound(CONTENTS_TAG));
			contents.setCount(nbt.getInt("count"));

			return contents;
		}

		return getFromHandler(voidTear, VoidTearItemStackHandler::getTotalAmountStack).orElse(ItemStack.EMPTY);
	}

	private <T> Optional<T> getFromHandler(ItemStack voidTear, Function<VoidTearItemStackHandler, T> get) {
		return InventoryHelper.getFromHandler(voidTear, get, VoidTearItemStackHandler.class);
	}

	private void runOnHandler(ItemStack voidTear, Consumer<VoidTearItemStackHandler> run) {
		InventoryHelper.runOnItemHandler(voidTear, run, VoidTearItemStackHandler.class);
	}

	private void setItemStack(ItemStack voidTear, ItemStack stack) {
		runOnHandler(voidTear, h -> h.setContainedStack(stack));
	}

	private void setItemQuantity(ItemStack voidTear, int quantity) {
		runOnHandler(voidTear, h -> h.setContainedStackAmount(quantity));
	}

	private int getItemQuantity(ItemStack voidTear) {
		return getFromHandler(voidTear, VoidTearItemStackHandler::getContainedAmount).orElse(0);
	}

	@Override
	public boolean onEntitySwing(ItemStack voidTear, LivingEntity entityLiving) {
		if (entityLiving.world.isRemote || !(entityLiving instanceof PlayerEntity)) {
			return false;
		}

		PlayerEntity player = (PlayerEntity) entityLiving;
		if (player.isSneaking()) {
			cycleMode(voidTear);
			return true;
		}
		return false;
	}

	public enum Mode implements IStringSerializable {
		ONE_STACK, FULL_INVENTORY, NO_REFILL;

		@Override
		public String getName() {
			return name();
		}
	}

	public Mode getMode(ItemStack voidTear) {
		if (NBTHelper.getString("mode", voidTear).isEmpty()) {
			setMode(voidTear, Mode.ONE_STACK);
		}
		return Mode.valueOf(NBTHelper.getString("mode", voidTear));
	}

	private void setMode(ItemStack voidTear, Mode mode) {
		NBTHelper.putString("mode", voidTear, mode.toString());
	}

	private void cycleMode(ItemStack voidTear) {
		if (isEmpty(voidTear)) {
			return;
		}

		Mode mode = getMode(voidTear);
		switch (mode) {
			case ONE_STACK:
				setMode(voidTear, Mode.FULL_INVENTORY);
				break;
			case FULL_INVENTORY:
				setMode(voidTear, Mode.NO_REFILL);
				break;
			case NO_REFILL:
				setMode(voidTear, Mode.ONE_STACK);
				break;
		}
	}

	private int getKeepQuantity(ItemStack voidTear) {
		Mode mode = getMode(voidTear);

		if (mode == Mode.NO_REFILL) {
			return 0;
		}
		if (mode == Mode.ONE_STACK) {
			return getContainerItem(voidTear).getMaxStackSize();
		}

		return Integer.MAX_VALUE;
	}

	@SubscribeEvent
	public void onItemPickup(EntityItemPickupEvent event) {
		ItemStack pickedUpStack = event.getItem().getItem();
		PlayerEntity player = event.getPlayer();
		ItemEntity itemEntity = event.getItem();

		for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++) {
			ItemStack tearStack = player.inventory.getStackInSlot(slot);
			if (tearStack.getItem() == this && isEnabled(tearStack) && tryToPickupWithTear(event, pickedUpStack, player, itemEntity, tearStack)) {
				break;
			}
		}
	}

	private boolean tryToPickupWithTear(EntityItemPickupEvent event, ItemStack pickedUpStack, PlayerEntity player, ItemEntity itemEntity, ItemStack tearStack) {
		int tearItemQuantity = getItemQuantity(tearStack);
		if (canAbsorbStack(pickedUpStack, tearStack)) {
			int playerItemQuantity = InventoryHelper.getItemHandlerFrom(player).map(h -> InventoryHelper.getItemQuantity(pickedUpStack, h)).orElse(0);

			if (playerItemQuantity + pickedUpStack.getCount() >= getKeepQuantity(tearStack) || player.inventory.getFirstEmptyStack() == -1) {
				setItemQuantity(tearStack, tearItemQuantity + pickedUpStack.getCount());
				if (!itemEntity.isSilent()) {
					Random rand = itemEntity.world.rand;
					itemEntity.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, (RandHelper.getRandomMinusOneToOne(rand) * 0.7F + 1.0F) * 2.0F);
				}
				itemEntity.remove();
				event.setCanceled(true);
				return true;
			}
		}
		return false;
	}

	boolean canAbsorbStack(ItemStack pickedUpStack, ItemStack tearStack) {
		return StackHelper.isItemAndNbtEqual(getContainerItem(tearStack), pickedUpStack) && getItemQuantity(tearStack) + pickedUpStack.getCount() <= Settings.COMMON.items.voidTear.itemLimit.get();
	}

	public boolean isEmpty(ItemStack voidTear) {
		return isEmpty(voidTear, false);
	}

	public boolean isEmpty(ItemStack voidTear, boolean isClient) {
		if (isClient) {
			return getContainerItem(voidTear, true).isEmpty();
		}

		return voidTear.getTag() == null || voidTear.getTag().keySet().isEmpty();
	}

	private void setEmpty(ItemStack voidTear) {
		voidTear.setTag(null);
		setItemStack(voidTear, ItemStack.EMPTY);
		setItemQuantity(voidTear, 0);
	}
}
