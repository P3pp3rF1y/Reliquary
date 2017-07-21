package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.lwjgl.input.Keyboard;
import xreliquary.entities.EntityXRFakePlayer;
import xreliquary.init.ModBlocks;
import xreliquary.init.ModItems;
import xreliquary.items.util.FilteredItemStackHandler;
import xreliquary.items.util.VoidTearItemStackHandler;
import xreliquary.network.PacketContainerItemHandlerSync;
import xreliquary.network.PacketHandler;
import xreliquary.network.PacketItemHandlerSync;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;
import xreliquary.util.StackHelper;
import xreliquary.util.XRFakePlayerFactory;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.*;

public class ItemVoidTear extends ItemToggleable {

	public ItemVoidTear() {
		super(Names.Items.VOID_TEAR);
		setMaxStackSize(1);
		setNoRepair();
		//noinspection ConstantConditions
		this.setCreativeTab(null);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		return new ICapabilitySerializable<NBTTagCompound>() {
			VoidTearItemStackHandler itemHandler = new VoidTearItemStackHandler();

			@Override
			public NBTTagCompound serializeNBT() {
				return itemHandler.serializeNBT();
			}

			@Override
			public void deserializeNBT(NBTTagCompound nbt) {
				itemHandler.deserializeNBT(nbt);
			}

			@Override
			public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
				return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
			}

			@Override
			public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
				if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
					//noinspection unchecked
					return (T) itemHandler;

				return null;
			}
		};
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return !(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) && super.hasEffect(stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean par4) {
		this.formatTooltip(null, stack, list);

		ItemStack contents = this.getContainerItem(stack);

		if(contents.isEmpty())
			return;

		if(this.isEnabled(stack)) {
			LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", TextFormatting.YELLOW + contents.getDisplayName()), list);
			list.add(LanguageHelper.getLocalization("tooltip.absorb_tear"));
		}
		LanguageHelper.formatTooltip("tooltip.tear_quantity", ImmutableMap.of("item", contents.getDisplayName(), "amount", Integer.toString(getItemQuantity(stack))), list);
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack voidTear = player.getHeldItem(hand);

		if(!world.isRemote) {

			if(getItemQuantity(voidTear) == 0)
				return new ActionResult<>(EnumActionResult.SUCCESS, new ItemStack(ModItems.emptyVoidTear, 1, 0));

			RayTraceResult rayTraceResult = this.rayTrace(world, player, false);

			//not letting logic go through if player was sneak clicking inventory or was trying to place a block
			//noinspection ConstantConditions
			if(rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK && (world.getTileEntity(rayTraceResult.getBlockPos()) instanceof IInventory && player.isSneaking() || getContainerItem(voidTear).getItem() instanceof ItemBlock))
				return new ActionResult<>(EnumActionResult.PASS, voidTear);

			if(player.isSneaking())
				return super.onItemRightClick(world, player, hand);

			if(this.attemptToEmptyIntoInventory(voidTear, player, player.inventory)) {
				player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, 0.5F * ((player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.7F + 1.2F));
				NBTHelper.resetTag(voidTear);
				return new ActionResult<>(EnumActionResult.SUCCESS, new ItemStack(ModItems.emptyVoidTear, 1, 0));
			}
		}

		player.inventoryContainer.detectAndSendChanges();
		return new ActionResult<>(EnumActionResult.PASS, voidTear);
	}

	private boolean canPlaceBlockOnSide(World worldIn, Block blockToPlace, BlockPos pos, EnumFacing side, EntityPlayer player) {
		Block block = worldIn.getBlockState(pos).getBlock();

		if(block == Blocks.SNOW_LAYER && block.isReplaceable(worldIn, pos)) {
			side = EnumFacing.UP;
		} else if(!block.isReplaceable(worldIn, pos)) {
			pos = pos.offset(side);
		}

		return worldIn.mayPlace(blockToPlace, pos, false, side, player);
	}

	@Override
	public void onUpdate(ItemStack voidTear, World world, Entity entity, int slotNumber, boolean isSelected) {
		if(!world.isRemote) {
			if(!(entity instanceof EntityPlayer))
				return;

			EntityPlayer player = (EntityPlayer) entity;

			boolean quantityUpdated = false;
			if(this.isEnabled(voidTear)) {
				ItemStack contents = this.getContainerItem(voidTear);

				if(!contents.isEmpty()) {
					int itemQuantity = InventoryHelper.getItemQuantity(contents, player.inventory);

					if(getItemQuantity(voidTear) <= Settings.VoidTear.itemLimit && itemQuantity > getKeepQuantity(voidTear) && InventoryHelper.consumeItem(contents, player, getKeepQuantity(voidTear), itemQuantity - getKeepQuantity(voidTear))) {
						//doesn't absorb in creative mode.. this is mostly for testing, it prevents the item from having unlimited *whatever* for eternity.
						if(!player.capabilities.isCreativeMode) {
							setItemQuantity(voidTear, getItemQuantity(voidTear) + itemQuantity - getKeepQuantity(voidTear));
							quantityUpdated = true;
						}
					}

					if(getMode(voidTear) != Mode.NO_REFILL && attemptToReplenish(player, voidTear))
						quantityUpdated = true;
				}
			}

			//noinspection ConstantConditions
			if(player.inventory.getStackInSlot(slotNumber).getItem() == ModItems.filledVoidTear && (isSelected || quantityUpdated)) {
				PacketHandler.networkWrapper.sendTo(new PacketItemHandlerSync(slotNumber, getItemHandlerNBT(voidTear)), (EntityPlayerMP) player);
			} else if(player.getHeldItemOffhand().getItem() == ModItems.filledVoidTear) {
				PacketHandler.networkWrapper.sendTo(new PacketItemHandlerSync(EnumHand.OFF_HAND, getItemHandlerNBT(voidTear)), (EntityPlayerMP) player);
			}

		}
	}

	private NBTTagCompound getItemHandlerNBT(ItemStack ist) {
		IItemHandler itemHandler = ist.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		if(!(itemHandler instanceof FilteredItemStackHandler))
			return null;

		FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;

		return filteredHandler.serializeNBT();
	}

	private boolean attemptToReplenish(EntityPlayer player, ItemStack voidTear) {
		IInventory inventory = player.inventory;
		for(int slot = 0; slot < inventory.getSizeInventory(); slot++) {
			ItemStack stackFound = inventory.getStackInSlot(slot);

			if(StackHelper.isItemAndNbtEqual(stackFound, getContainerItem(voidTear))) {
				int quantityToDecrease = Math.min(stackFound.getMaxStackSize() - stackFound.getCount(), getItemQuantity(voidTear) - 1);
				stackFound.grow(quantityToDecrease);
				setItemQuantity(voidTear, getItemQuantity(voidTear) - quantityToDecrease);
				if(getMode(voidTear) != Mode.FULL_INVENTORY)
					return true;
			}
		}

		int slot;
		while(getItemQuantity(voidTear) > 1 && (slot = player.inventory.getFirstEmptyStack()) != -1) {
			ItemStack newStack = getContainerItem(voidTear).copy();
			int quantityToDecrease = Math.min(newStack.getMaxStackSize(), getItemQuantity(voidTear) - 1);
			newStack.setCount(quantityToDecrease);
			player.inventory.setInventorySlotContents(slot, newStack);
			setItemQuantity(voidTear, getItemQuantity(voidTear) - quantityToDecrease);
			if(getMode(voidTear) != Mode.FULL_INVENTORY)
				return true;
		}

		return false;
	}

	@Nonnull
	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		ItemStack voidTear = player.getHeldItem(hand);
		if(world.getBlockState(pos).getBlock() == ModBlocks.pedestal)
			return EnumActionResult.PASS;

		if(world.getTileEntity(pos) instanceof IInventory) {
			if(!world.isRemote) {
				IInventory inventory = (IInventory) world.getTileEntity(pos);

				if(inventory instanceof TileEntityChest && world.getBlockState(pos).getBlock() instanceof BlockChest) {
					inventory = ((BlockChest) world.getBlockState(pos).getBlock()).getLockableContainer(world, pos);
				}

				//enabled == drinking mode, we're going to drain the inventory of items.
				if(this.isEnabled(voidTear)) {
					this.drainInventory(voidTear, player, inventory);
				} else {
					//disabled == placement mode, try and stuff the tear's contents into the inventory
					this.attemptToEmptyIntoInventory(voidTear, player, inventory);
					if(!player.isSneaking() && !(getItemQuantity(voidTear) > 0)) {
						player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(ModItems.emptyVoidTear, 1, 0));
					}
				}
				return EnumActionResult.SUCCESS;
			}
		} else if(getContainerItem(voidTear).getItem() instanceof ItemBlock) {
			ItemStack containerItem = getContainerItem(voidTear);
			ItemBlock itemBlock = (ItemBlock) containerItem.getItem();
			Block block = itemBlock.getBlock();

			if(canPlaceBlockOnSide(world, block, pos, side, player)) {
				setItemQuantity(voidTear, getItemQuantity(voidTear) - 1);
				if (!world.isRemote) {
					EntityXRFakePlayer fakePlayer = XRFakePlayerFactory.get((WorldServer) world);
					fakePlayer.setHeldItem(EnumHand.MAIN_HAND, containerItem);
					//noinspection SuspiciousNameCombination
					itemBlock.onItemUse(fakePlayer, world, pos, hand, side, hitX, hitY, hitZ);
				}
			}
		}
		return EnumActionResult.PASS;
	}

	private boolean attemptToEmptyIntoInventory(ItemStack ist, EntityPlayer player, IInventory inventory) {
		ItemStack contents = this.getContainerItem(ist);
		contents.setCount(1);

		int quantity = getItemQuantity(ist);
		int maxNumberToEmpty = player.isSneaking() ? quantity : Math.min(contents.getMaxStackSize(), quantity);

		quantity -= InventoryHelper.tryToAddToInventory(contents, inventory, maxNumberToEmpty);

		setItemQuantity(ist, quantity);
		if(quantity == 0) {
			player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, 0.5F * ((player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.7F + 1.8F));
			return true;
		} else {
			player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, 0.5F * ((player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.7F + 1.2F));
			return false;
		}
	}

	private void drainInventory(ItemStack ist, EntityPlayer player, IInventory inventory) {
		ItemStack contents = this.getContainerItem(ist);
		int quantity = getItemQuantity(ist);

		int quantityDrained = InventoryHelper.tryToRemoveFromInventory(contents, inventory, Settings.VoidTear.itemLimit - quantity);

		if(!(quantityDrained > 0))
			return;

		player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, 0.5F * ((player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.7F + 1.2F));

		setItemQuantity(ist, quantity + quantityDrained);
	}

	@Nonnull
	@Override
	public ItemStack getContainerItem(@Nonnull ItemStack voidTear) {
		IItemHandler itemHandler = voidTear.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		if(!(itemHandler instanceof FilteredItemStackHandler))
			return ItemStack.EMPTY;

		FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;
		ItemStack stackToReturn = ItemStack.EMPTY;
		if(!filteredHandler.getStackInParentSlot(0).isEmpty()) {
			stackToReturn = filteredHandler.getStackInParentSlot(0).copy();
			stackToReturn.setCount(filteredHandler.getTotalAmount(0));
		}

		return stackToReturn;
	}

	void setItemStack(ItemStack voidTear, ItemStack stack) {
		IItemHandler itemHandler = voidTear.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		if(!(itemHandler instanceof FilteredItemStackHandler))
			return;

		FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;
		filteredHandler.setParentSlotStack(0, stack);
	}

	void setItemQuantity(ItemStack voidTear, int quantity) {
		IItemHandler itemHandler = voidTear.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		if(!(itemHandler instanceof FilteredItemStackHandler))
			return;

		FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;
		filteredHandler.setTotalAmount(0, quantity);
	}

	private int getItemQuantity(ItemStack voidTear) {
		IItemHandler itemHandler = voidTear.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		if(!(itemHandler instanceof FilteredItemStackHandler))
			return 0;

		FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;
		return filteredHandler.getTotalAmount(0);
	}

	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack voidTear) {
		if(entityLiving.world.isRemote || !(entityLiving instanceof EntityPlayer))
			return false;

		EntityPlayer player = (EntityPlayer) entityLiving;
		if(player.isSneaking()) {
			cycleMode(voidTear);
			return true;
		}
		return false;
	}

	@SuppressWarnings("WeakerAccess")
	public enum Mode implements IStringSerializable {
		ONE_STACK, FULL_INVENTORY, NO_REFILL;

		@Nonnull
		@Override
		public String getName() {
			return name();
		}
	}

	public Mode getMode(ItemStack voidTear) {
		if(NBTHelper.getString("mode", voidTear).isEmpty()) {
			setMode(voidTear, Mode.ONE_STACK);
		}
		return Mode.valueOf(NBTHelper.getString("mode", voidTear));
	}

	private void setMode(ItemStack voidTear, Mode mode) {
		NBTHelper.setString("mode", voidTear, mode.toString());
	}

	private void cycleMode(ItemStack voidTear) {
		Mode mode = getMode(voidTear);
		switch(mode) {
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

		if(mode == Mode.NO_REFILL)
			return 0;
		if(mode == Mode.ONE_STACK)
			return getContainerItem(voidTear).getMaxStackSize();

		return Integer.MAX_VALUE;
	}

	@SubscribeEvent
	public void onItemPickup(EntityItemPickupEvent event) {
		ItemStack pickedUpStack = event.getItem().getItem();
		EntityPlayer player = event.getEntityPlayer();
		EntityItem itemEntity = event.getItem();

		for(int slot = 0; slot < player.inventory.getSizeInventory(); slot++) {
			ItemStack tearStack = player.inventory.getStackInSlot(slot);
			if(tearStack.getItem() == this && this.isEnabled(tearStack)) {
				int tearItemQuantity = this.getItemQuantity(tearStack);
				if(canAbsorbStack(pickedUpStack, tearStack)) {
					int playerItemQuantity = InventoryHelper.getItemQuantity(pickedUpStack, player.inventory);

					if(playerItemQuantity + pickedUpStack.getCount() >= getKeepQuantity(tearStack) || player.inventory.getFirstEmptyStack() == -1) {
						this.setItemQuantity(tearStack, tearItemQuantity + pickedUpStack.getCount());
						if(!itemEntity.isSilent()) {
							Random rand = new Random();
							itemEntity.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
						}
						itemEntity.setDead();
						event.setCanceled(true);
						break;
					}
				}
			}
		}
	}

	boolean canAbsorbStack(ItemStack pickedUpStack, ItemStack tearStack) {
		return StackHelper.isItemAndNbtEqual(this.getContainerItem(tearStack), pickedUpStack) && this.getItemQuantity(tearStack) + pickedUpStack.getCount() <= Settings.VoidTear.itemLimit;
	}

	private static Map<UUID, VoidTearListener> openListeners = new HashMap<>();

	@SubscribeEvent
	public void onContainerOpen(PlayerContainerEvent.Open event) {

		EntityPlayer player = event.getEntityPlayer();
		if(event.getContainer() != player.inventoryContainer) {
			VoidTearListener listener = new VoidTearListener((EntityPlayerMP) player);

			event.getContainer().addListener(listener);
			openListeners.put(player.getGameProfile().getId(), listener);
			listener.updateFullInventory(event.getContainer());
		}
	}

	private static final Field LISTENERS = ReflectionHelper.findField(Container.class, "field_75149_d", "listeners");

	private static List<IContainerListener> getListeners(Container container) {

		try {
			//noinspection unchecked
			return (List<IContainerListener>) LISTENERS.get(container);
		}
		catch(IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@SubscribeEvent
	public void onContainerClose(PlayerContainerEvent.Close event) {

		EntityPlayer player = event.getEntityPlayer();
		if(event.getContainer() != player.inventoryContainer) {

			UUID playerId = player.getGameProfile().getId();
			if(openListeners.keySet().contains(playerId)) {

				getListeners(event.getContainer()).remove(openListeners.get(playerId));
				openListeners.remove(playerId);
			}
		}
	}

	private class VoidTearListener implements IContainerListener {

		private EntityPlayerMP player;

		VoidTearListener(EntityPlayerMP player) {

			this.player = player;
		}

		@Override
		public void sendAllContents(@Nonnull Container containerToSend, @Nonnull NonNullList<ItemStack> itemsList) {

		}

		@Override
		public void sendSlotContents(@Nonnull Container container, int slot, @Nonnull ItemStack stack) {

			if(stack.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
				updateFullInventory(container);
			}
		}

		private void updateVoidTear(Container container, int slot, ItemStack stack) {

			if(stack == null || stack.getItem() != ModItems.filledVoidTear)
				return;

			PacketHandler.networkWrapper.sendTo(new PacketContainerItemHandlerSync(slot, getItemHandlerNBT(stack), container.windowId), player);
		}

		void updateFullInventory(Container container) {

			for (int slot = 0; slot < container.inventorySlots.size(); slot++) {
				updateVoidTear(container, slot, container.getSlot(slot).getStack());
			}
		}

		@Override
		public void sendWindowProperty(@Nonnull Container container, int varToUpdate, int newValue) {

		}

		@Override
		public void sendAllWindowProperties(@Nonnull Container container, @Nonnull IInventory inventory) {

		}
	}
}
