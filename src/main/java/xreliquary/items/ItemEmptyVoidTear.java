package xreliquary.items;

import lib.enderwizards.sandstone.init.ContentHandler;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemBase;
import lib.enderwizards.sandstone.util.ContentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.lib.Names;

@ContentInit
public class ItemEmptyVoidTear extends ItemBase {

	public ItemEmptyVoidTear() {
		super(Names.void_tear_empty);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(0);
		this.setMaxStackSize(64);
		canRepair = false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer player) {
        if(player.capabilities.isCreativeMode)
            return ist;
		ItemStack tear = compressInventoryIntoTearForPlayer(player.inventory, player);
		if (tear == null)
			return ist;
		else {
			player.worldObj.playSoundAtEntity(player, "random.orb", 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.8F));
			--ist.stackSize;
            if (ist.stackSize == 0) {
                return tear;
            } else {
                addTearToInventory(player, tear);
            }
		}
		return ist;
	}

	@Override
	public boolean onItemUseFirst(ItemStack ist, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if(player.capabilities.isCreativeMode)
            return false;
		if (world.getTileEntity(x, y, z) instanceof IInventory) {
			IInventory inventory = (IInventory) world.getTileEntity(x, y, z);
			ItemStack tear = compressInventoryIntoTearForPlayer(inventory, player);
			if (tear != null) {
				player.worldObj.playSoundAtEntity(player, "random.orb", 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.8F));
				if (player.inventory.decrStackSize(player.inventory.currentItem, 1).stackSize == 0) {
					player.inventory.setInventorySlotContents(player.inventory.currentItem, tear);
				} else {
					addTearToInventory(player, tear);
				}
			}
			return true;
		}
		return false;
	}

	public ItemStack compressInventoryIntoTearForPlayer(IInventory inventory, EntityPlayer player) {
		ItemStack target = getTargetItem(inventory);
		if (target == null)
			return null;
		int itemMeta = target.getItemDamage();
		Item item = target.getItem();
		int itemQuantity = getQuantityInInventory(target, inventory);
		ItemStack tear = new ItemStack(ContentHandler.getItem(Names.void_tear), 1);
		tear.setTagCompound(createStackTagCompoundForTear(itemMeta, ContentHelper.getIdent(item), itemQuantity));
		findAndRemoveQuantity(inventory, new ItemStack(item, 1, itemMeta), itemQuantity);
		return tear;
	}

	public void addTearToInventory(EntityPlayer player, ItemStack tear) {
		if (!player.inventory.addItemStackToInventory(tear)) {
			EntityItem entityTear = new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, tear);
			player.worldObj.spawnEntityInWorld(entityTear);
		}
	}

	public void findAndRemoveQuantity(IInventory inventory, ItemStack ist, int quantity) {
		for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
			ItemStack stack = inventory.getStackInSlot(slot);
			if (stack == null) {
				continue;
			}
			if (inventory.getStackInSlot(slot).isItemEqual(ist)) {
				while (quantity > 0 && inventory.getStackInSlot(slot) != null) {
					inventory.decrStackSize(slot, 1);
					quantity--;
				}
			}
		}
	}

	public NBTTagCompound createStackTagCompoundForTear(int meta, String ID, int quantity) {
		NBTTagCompound tear = new NBTTagCompound();
		tear.setString("itemID", ID);
		tear.setShort("itemMeta", (short) meta);
		tear.setShort("itemQuantity", (short) quantity);
		return tear;
	}

	public ItemStack getTargetItem(IInventory inventory) {
		ItemStack targetItem = null;
		int itemQuantity = 0;
		for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
			ItemStack ist = inventory.getStackInSlot(slot);
			if (ist == null) {
				continue;
			}
			if (ContentHelper.getIdent(ist.getItem()).equals(ContentHelper.getIdent(this))) {
				continue;
			}
			if (ist.getMaxStackSize() == 1) {
				continue;
			}
			if (ist.getTagCompound() != null) {
				continue;
			}
			if (getQuantityInInventory(ist, inventory) > itemQuantity) {
				itemQuantity = getQuantityInInventory(ist, inventory);
				targetItem = ist;
			}
		}
		return targetItem;
	}

	public int getQuantityInInventory(ItemStack ist, IInventory inventory) {
		int itemQuantity = 0;
		for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
			ItemStack stack = inventory.getStackInSlot(slot);
			if (stack == null) {
				continue;
			}
			if (ist.isItemEqual(stack)) {
				itemQuantity += stack.stackSize;
			}
		}
		return itemQuantity;
	}
}
