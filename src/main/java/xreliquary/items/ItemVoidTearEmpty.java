package xreliquary.items;

import net.minecraft.block.BlockChest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.init.ModItems;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.NBTHelper;

public class ItemVoidTearEmpty extends ItemBase {
	public ItemVoidTearEmpty() {
		super(Names.void_tear_empty);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxStackSize(16);
		canRepair = false;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack ist, World world, EntityPlayer player, EnumHand hand) {
		if(!world.isRemote) {
			ItemStack createdTear = buildTear(ist, player, player.inventory, true);
			if(createdTear != null) {
				--ist.stackSize;
				player.worldObj.playSound(null, player.getPosition(), SoundEvents.entity_experience_orb_touch, SoundCategory.PLAYERS, 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.2F));
				if(ist.stackSize == 0)
					return new ActionResult<>(EnumActionResult.SUCCESS, createdTear);
				else {
					addItemToInventory(player, createdTear);
					return new ActionResult<>(EnumActionResult.SUCCESS, ist);
				}
			}
		}
		return new ActionResult<>(EnumActionResult.PASS, ist);
	}

	@Override
	public EnumActionResult onItemUseFirst(ItemStack ist, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		if(!world.isRemote) {

			if(world.getTileEntity(pos) instanceof IInventory) {
				IInventory inventory = (IInventory) world.getTileEntity(pos);

				if(inventory instanceof TileEntityChest && world.getBlockState(pos).getBlock() instanceof BlockChest) {
					inventory = ((BlockChest) world.getBlockState(pos).getBlock()).getLockableContainer(world, pos);
				}

				ItemStack createdTear = buildTear(ist, player, inventory, false);
				if(createdTear != null) {
					--ist.stackSize;
					player.worldObj.playSound(null, player.getPosition(), SoundEvents.entity_experience_orb_touch, SoundCategory.PLAYERS, 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.2F));
					if(ist.stackSize == 0)
						player.inventory.setInventorySlotContents(player.inventory.currentItem, createdTear);
					else
						addItemToInventory(player, createdTear);
					return EnumActionResult.SUCCESS;
				}
			}
		}
		return EnumActionResult.PASS;
	}

	protected void addItemToInventory(EntityPlayer player, ItemStack ist) {
		for(int i = 0; i < player.inventory.mainInventory.length; ++i) {
			if(player.inventory.getStackInSlot(i) == null) {
				player.inventory.setInventorySlotContents(i, ist);
				return;
			}
		}
		player.worldObj.spawnEntityInWorld(new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, ist));
	}

	protected ItemStack buildTear(ItemStack ist, EntityPlayer player, IInventory inventory, boolean isPlayerInventory) {
		ItemStack target = InventoryHelper.getTargetItem(ist, inventory, false);
		if(target == null)
			return null;
		ItemStack filledTear = new ItemStack(ModItems.filledVoidTear, 1, 0);

		ModItems.filledVoidTear.setItemStack(filledTear, target);

		int quantity = InventoryHelper.getItemQuantity(target, inventory);
		if(isPlayerInventory) {
			if((quantity - target.getMaxStackSize()) > 0) {
				InventoryHelper.consumeItem(target, player, target.getMaxStackSize(), quantity - target.getMaxStackSize());
				quantity = quantity - target.getMaxStackSize();
			} else {
				quantity = 0;
			}
		} else {
			quantity = InventoryHelper.tryToRemoveFromInventory(target, inventory, Settings.VoidTear.itemLimit);
		}
		ModItems.filledVoidTear.setItemQuantity(filledTear, quantity);

		//configurable auto-drain when created.
		NBTHelper.setBoolean("enabled", filledTear, Settings.VoidTear.absorbWhenCreated);

		return filledTear;
	}
}
