package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundCategory;
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
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.lwjgl.input.Keyboard;
import xreliquary.Reliquary;
import xreliquary.entities.EntityXRFakePlayer;
import xreliquary.init.ModBlocks;
import xreliquary.items.util.FilteredItemStackHandler;
import xreliquary.items.util.VoidTearItemStackHandler;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;
import xreliquary.util.StackHelper;
import xreliquary.util.XRFakePlayerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

@MethodsReturnNonnullByDefault
public class ItemVoidTear extends ItemToggleable {
	public ItemVoidTear() {
		super(Names.Items.VOID_TEAR);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return isEmpty(stack, FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) ? "item.void_tear_empty" : "item.void_tear";
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return isEmpty(stack) ? 16 : 1;
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
	public void addInformation(ItemStack voidTear, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
			return;

		ItemStack contents = this.getContainerItem(voidTear, true);

		if(isEmpty(voidTear, true))
			return;

		if(this.isEnabled(voidTear)) {
			LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", TextFormatting.YELLOW + contents.getDisplayName()), tooltip);
			tooltip.add(LanguageHelper.getLocalization("tooltip.absorb_tear"));
		}
		LanguageHelper.formatTooltip("tooltip.tear_quantity", ImmutableMap.of("item", contents.getDisplayName(), "amount", Integer.toString(contents.getCount())), tooltip);
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack voidTear = player.getHeldItem(hand);

		if(!world.isRemote) {
			RayTraceResult rayTraceResult = this.rayTrace(world, player, false);

			//not letting logic go through if player was sneak clicking inventory or was trying to place a block
			//noinspection ConstantConditions
			if(rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK && (world.getTileEntity(rayTraceResult.getBlockPos()) instanceof IInventory && player.isSneaking() || getContainerItem(voidTear).getItem() instanceof ItemBlock))
				return new ActionResult<>(EnumActionResult.PASS, voidTear);

			if (isEmpty(voidTear)) {
				return rightClickEmpty(voidTear, player);
			}

			if(getItemQuantity(voidTear) == 0) {
				setEmpty(voidTear);
				return new ActionResult<>(EnumActionResult.SUCCESS, voidTear);
			}

			if(player.isSneaking())
				return super.onItemRightClick(world, player, hand);

			if(this.attemptToEmptyIntoInventory(voidTear, player, player.inventory)) {
				player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, 0.5F * ((player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.7F + 1.2F));
				setEmpty(voidTear);
				return new ActionResult<>(EnumActionResult.SUCCESS, voidTear);
			}
		}
		return new ActionResult<>(EnumActionResult.PASS, voidTear);
	}

	private ActionResult<ItemStack> rightClickEmpty(ItemStack emptyVoidTear, EntityPlayer player) {
		ItemStack target = InventoryHelper.getTargetItem(emptyVoidTear, player.inventory);
		if(!target.isEmpty()) {
			ItemStack filledTear;
			if (emptyVoidTear.getCount() > 1) {
				emptyVoidTear.shrink(1);
				filledTear = new ItemStack(this);
			} else {
				filledTear = emptyVoidTear;
			}
			buildTear(filledTear, target, player, player.inventory, true);
			player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, 0.5F * ((player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.7F + 1.2F));
			if(emptyVoidTear.getCount() == 1)
				return new ActionResult<>(EnumActionResult.SUCCESS, filledTear);
			else {
				InventoryHelper.addItemToPlayerInventory(player, filledTear);
				return new ActionResult<>(EnumActionResult.SUCCESS, emptyVoidTear);
			}
		}

		return new ActionResult<>(EnumActionResult.PASS, emptyVoidTear);
	}

	private void buildTear(ItemStack voidTear, ItemStack target, EntityPlayer player, IInventory inventory, boolean isPlayerInventory) {
		setItemStack(voidTear, target);

		int quantity = InventoryHelper.getItemQuantity(target, inventory);
		if(isPlayerInventory) {
			if((quantity - target.getMaxStackSize()) > 0) {
				InventoryHelper.consumeItem(target, player, target.getMaxStackSize(), quantity - target.getMaxStackSize());
				quantity = quantity - target.getMaxStackSize();
			} else {
				InventoryHelper.consumeItem(target, player, 0, 1);
				quantity = 1;
			}
		} else {
			quantity = InventoryHelper.tryToRemoveFromInventory(target, inventory, Settings.Items.VoidTear.itemLimit);
		}
		setItemQuantity(voidTear, quantity);

		//configurable auto-drain when created.
		NBTHelper.setBoolean("enabled", voidTear, Settings.Items.VoidTear.absorbWhenCreated);
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

			if(this.isEnabled(voidTear)) {
				ItemStack contents = this.getContainerItem(voidTear);

				if(!contents.isEmpty()) {
					int itemQuantity = InventoryHelper.getItemQuantity(contents, player.inventory);

					if(getItemQuantity(voidTear) <= Settings.Items.VoidTear.itemLimit && itemQuantity > getKeepQuantity(voidTear) && InventoryHelper.consumeItem(contents, player, getKeepQuantity(voidTear), itemQuantity - getKeepQuantity(voidTear))) {
						//doesn't absorb in creative mode.. this is mostly for testing, it prevents the item from having unlimited *whatever* for eternity.
						if(!player.capabilities.isCreativeMode) {
							setItemQuantity(voidTear, getItemQuantity(voidTear) + itemQuantity - getKeepQuantity(voidTear));
						}
					}
				}
				if(getMode(voidTear) != Mode.NO_REFILL) {
					attemptToReplenish(player, voidTear);
				}
			}
		}
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

				if (isEmpty(voidTear)) {
					return onItemUseFirstEmpty(voidTear, inventory, player, hand);
				}

				//enabled == drinking mode, we're going to drain the inventory of items.
				if(this.isEnabled(voidTear)) {
					this.drainInventory(voidTear, player, inventory);
				} else {
					//disabled == placement mode, try and stuff the tear's contents into the inventory
					this.attemptToEmptyIntoInventory(voidTear, player, inventory);
					if(getItemQuantity(voidTear) <= 0) {
						setEmpty(voidTear);
						player.setHeldItem(hand, voidTear);
					}
				}
			}
			return EnumActionResult.SUCCESS;
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

	private EnumActionResult onItemUseFirstEmpty(ItemStack emptyVoidTear, IInventory inventory, EntityPlayer player, EnumHand hand) {
		ItemStack target = InventoryHelper.getTargetItem(emptyVoidTear, inventory);
		if(!target.isEmpty()) {
			ItemStack filledTear;
			if (emptyVoidTear.getCount() > 1) {
				emptyVoidTear.shrink(1);
				filledTear = new ItemStack(this);
			} else {
				filledTear = emptyVoidTear;
			}
			buildTear(filledTear, target, player, inventory, false);

			player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, 0.5F * ((player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.7F + 1.2F));
			if(emptyVoidTear.getCount() == 1)
				player.setHeldItem(hand, filledTear);
			else
				InventoryHelper.addItemToPlayerInventory(player, filledTear);
			return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.PASS;
	}

	private boolean attemptToEmptyIntoInventory(ItemStack ist, EntityPlayer player, IInventory inventory) {
		ItemStack contents = this.getContainerItem(ist).copy();
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

		int quantityDrained = InventoryHelper.tryToRemoveFromInventory(contents, inventory, Settings.Items.VoidTear.itemLimit - quantity);

		if(!(quantityDrained > 0))
			return;

		player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, 0.5F * ((player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.7F + 1.2F));

		setItemQuantity(ist, quantity + quantityDrained);
	}

	@Nullable
	@Override
	public NBTTagCompound getNBTShareTag(ItemStack voidTear) {
		NBTTagCompound nbt = super.getNBTShareTag(voidTear);

		if (isEmpty(voidTear)) {
			return nbt;
		}

		if (nbt == null) {
			nbt = new NBTTagCompound();
		}
		nbt.setInteger("count", getItemQuantity(voidTear));
		nbt.setTag("contents", getContainerItem(voidTear).writeToNBT(new NBTTagCompound()));

		return nbt;
	}

	@Nonnull
	@Override
	public ItemStack getContainerItem(@Nonnull ItemStack voidTear) {
		return getContainerItem(voidTear, false);
	}
	public ItemStack getContainerItem(@Nonnull ItemStack voidTear, boolean isClient) {
		if (isClient) {
			NBTTagCompound nbt = voidTear.getTagCompound();
			if (nbt == null || !nbt.hasKey("contents")) {
				return ItemStack.EMPTY;
			}
			ItemStack contents = new ItemStack(nbt.getCompoundTag("contents"));
			contents.setCount(nbt.getInteger("count"));

			return contents;
		}

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

	private void setItemStack(ItemStack voidTear, ItemStack stack) {
		IItemHandler itemHandler = voidTear.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		if(!(itemHandler instanceof FilteredItemStackHandler))
			return;

		FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;
		filteredHandler.setParentSlotStack(0, stack);
	}

	private void setItemQuantity(ItemStack voidTear, int quantity) {
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
		if (isEmpty(voidTear))
			return;

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
		return StackHelper.isItemAndNbtEqual(this.getContainerItem(tearStack), pickedUpStack) && this.getItemQuantity(tearStack) + pickedUpStack.getCount() <= Settings.Items.VoidTear.itemLimit;
	}

	public boolean isEmpty(ItemStack voidTear) {
		return isEmpty(voidTear, false);
	}

	public boolean isEmpty(ItemStack voidTear, boolean isClient) {
		if (isClient) {
			return getContainerItem(voidTear, true).isEmpty();
		}

		return voidTear.getTagCompound() == null || voidTear.getTagCompound().getKeySet().isEmpty();
	}

	private void setEmpty(ItemStack voidTear) {
		voidTear.setTagCompound(null);
		setItemStack(voidTear, ItemStack.EMPTY);
		setItemQuantity(voidTear, 0);
	}
}
