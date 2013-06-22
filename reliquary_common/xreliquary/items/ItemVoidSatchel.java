package xreliquary.items;

import java.util.List;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import xreliquary.lib.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemVoidSatchel extends ItemWithCapacity {
	public ItemVoidSatchel(int par1) {
		super(par1);
		canRepair = false;
		this.setUnlocalizedName(Names.VOID_SATCHEL_NAME);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.epic;
	}

	@Override
	public void addInformation(ItemStack ist, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		NBTTagCompound tag = ist.getTagCompound();
		if (tag == null) return;
		String details0 = "This Void Satchel ";
		String details1 = "holds ";
		ItemStack contents = new ItemStack(tag.getShort("itemID"), 1, tag.getShort("itemMeta"));
		String itemName = contents.getDisplayName();
		details1 += tag.getShort("itemQuantity") + " " + itemName;
		par3List.add(details0);
		par3List.add(details1);
		super.addInformation(ist, par2EntityPlayer, par3List, par4);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack ist) {
		return isActive(ist);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer player) {
		setIsActive(ist, !isActive(ist));
		return ist;
	}

	@Override
	public boolean onItemUse(ItemStack ist, EntityPlayer player, World world, int x, int y, int z, int side, float xOff, float yOff, float zOff) {
		// spew contents onto ground only if aiming at top of a block. Else,
		// toggle satchel.
		if (side == 1) {
			setIsActive(ist, false);
			int quantity = getQuantity(ist) >= 64 ? player.isSneaking() ? getQuantity(ist) : 64 : getQuantity(ist);
			int depletedBy = 0;
			while (quantity > 0) {
				ItemStack contents = getTargetItem(ist);
				if (quantity > contents.getMaxStackSize()) {
					contents.stackSize = contents.getMaxStackSize();
					quantity -= contents.getMaxStackSize();
					depletedBy += contents.getMaxStackSize();
				} else {
					contents.stackSize = quantity;
					depletedBy += quantity;
					quantity = 0;
				}
				if (world.isRemote) {
					continue;
				}
				EntityItem item = new EntityItem(world, x + 0.5F, y + 1.5F, z + 0.5F, contents);
				world.spawnEntityInWorld(item);
			}
			setQuantity(ist, getQuantity(ist) - depletedBy);
		}
		return true;
	}

	@Override
	public boolean onItemUseFirst(ItemStack ist, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (world.getBlockTileEntity(x, y, z) instanceof IInventory) {
			IInventory inventory = (IInventory)world.getBlockTileEntity(x, y, z);
			if (player.isSneaking()) {
				compressInventoryForBlock(ist, inventory);
			} else {
				unloadContentsIntoInventory(ist, inventory, player);
			}
			return false;
		}
		return false;
	}

	public void unloadContentsIntoInventory(ItemStack ist, IInventory inventory, EntityPlayer player) {
		ItemStack contents = getTargetItem(ist);
		if (contents == null) return;
		int quantity = getQuantity(ist);
		while (quantity > 0) {
			if (!tryToAddToInventory(contents, inventory)) {
				break;
			}
			quantity--;
		}
		player.worldObj.playSoundAtEntity(player, "random.orb", 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.8F));
		setQuantity(ist, quantity);
		return;
	}

	public boolean tryToAddToInventory(ItemStack contents, IInventory inventory) {
		for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
			if (inventory.getStackInSlot(slot) == null) {
				continue;
			}
			if (inventory.getStackInSlot(slot).isItemEqual(contents)) {
				if (inventory.getStackInSlot(slot).stackSize == inventory.getStackInSlot(slot).getMaxStackSize()) {
					continue;
				}
				inventory.getStackInSlot(slot).stackSize++;
				return true;
			}
		}
		for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
			if (inventory.getStackInSlot(slot) == null) {
				inventory.setInventorySlotContents(slot, new ItemStack(contents.itemID, contents.stackSize, contents.getItemDamage()));
				return true;
			}
		}
		return false;
	}

	public void compressInventoryForBlock(ItemStack ist, IInventory inventory) {
		ItemStack target = getTargetItem(ist);
		if (target == null) return;
		while (!isVoidItemFull(ist) && findAndRemove(inventory, target)) {
			increaseQuantity(ist);
		}
	}
}
