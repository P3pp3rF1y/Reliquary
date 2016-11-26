package xreliquary.items;

import net.minecraft.block.BlockChest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
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

import javax.annotation.Nonnull;

public class ItemVoidTearEmpty extends ItemBase {
	public ItemVoidTearEmpty() {
		super(Names.Items.VOID_TEAR_EMPTY);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxStackSize(16);
		canRepair = false;
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if(!world.isRemote) {
			ItemStack createdTear = buildTear(stack, player, player.inventory, true);
			if(!createdTear.isEmpty()) {
				stack.shrink(1);
				player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, 0.5F * ((player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.7F + 1.2F));
				if(stack.getCount() == 0)
					return new ActionResult<>(EnumActionResult.SUCCESS, createdTear);
				else {
					addItemToInventory(player, createdTear);
					return new ActionResult<>(EnumActionResult.SUCCESS, stack);
				}
			}
		}
		return new ActionResult<>(EnumActionResult.PASS, stack);
	}

	@Nonnull
	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		ItemStack ist = player.getHeldItem(hand);
		if(!world.isRemote) {

			if(world.getTileEntity(pos) instanceof IInventory) {
				IInventory inventory = (IInventory) world.getTileEntity(pos);

				if(inventory instanceof TileEntityChest && world.getBlockState(pos).getBlock() instanceof BlockChest) {
					inventory = ((BlockChest) world.getBlockState(pos).getBlock()).getLockableContainer(world, pos);
				}

				ItemStack createdTear = buildTear(ist, player, inventory, false);
				if(!createdTear.isEmpty()) {
					ist.shrink(1);
					player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, 0.5F * ((player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.7F + 1.2F));
					if(ist.getCount() == 0)
						player.inventory.setInventorySlotContents(player.inventory.currentItem, createdTear);
					else
						addItemToInventory(player, createdTear);
					return EnumActionResult.SUCCESS;
				}
			}
		}
		return EnumActionResult.PASS;
	}

	private void addItemToInventory(EntityPlayer player, ItemStack ist) {
		for(int i = 0; i < player.inventory.mainInventory.size(); ++i) {
			if(player.inventory.getStackInSlot(i).isEmpty()) {
				player.inventory.setInventorySlotContents(i, ist);
				return;
			}
		}
		player.world.spawnEntity(new EntityItem(player.world, player.posX, player.posY, player.posZ, ist));
	}

	@Nonnull
	private ItemStack buildTear(ItemStack ist, EntityPlayer player, IInventory inventory, boolean isPlayerInventory) {
		ItemStack target = InventoryHelper.getTargetItem(ist, inventory, false);
		if(target.isEmpty())
			return ItemStack.EMPTY;
		ItemStack filledTear = new ItemStack(ModItems.filledVoidTear, 1, 0);

		ModItems.filledVoidTear.setItemStack(filledTear, target);

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
			quantity = InventoryHelper.tryToRemoveFromInventory(target, inventory, Settings.VoidTear.itemLimit);
		}
		ModItems.filledVoidTear.setItemQuantity(filledTear, quantity);

		//configurable auto-drain when created.
		NBTHelper.setBoolean("enabled", filledTear, Settings.VoidTear.absorbWhenCreated);

		return filledTear;
	}
}
