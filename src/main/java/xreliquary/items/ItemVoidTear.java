package xreliquary.items;

import java.util.List;

import com.google.common.collect.ImmutableMap;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.init.XRItems;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemVoidTear extends ItemBase {

	public ItemVoidTear() {
		super(Reference.MOD_ID, Names.VOID_TEAR_NAME);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		canRepair = false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.epic;
	}

	@Override
	public void addInformation(ItemStack ist, EntityPlayer par2EntityPlayer, List list, boolean par4) {
		NBTTagCompound tag = ist.getTagCompound();
		String holds = null;
		if (tag == null || new ItemStack((Item) Item.itemRegistry.getObject(tag.getString("itemID")), 1, tag.getShort("itemMeta")).getItem() == null) {
			holds = "nothing";
		} else {
			ItemStack contents = new ItemStack((Item) Item.itemRegistry.getObject(tag.getString("itemID")), 1, tag.getShort("itemMeta"));
			String itemName = contents.getDisplayName();
			holds = tag.getShort("itemQuantity") + "x " + itemName;
		}
		this.formatTooltip(ImmutableMap.of("holds", holds), ist, list);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer player) {
		unloadContentsIntoPlayerInventory(ist, player.inventory, player);
		return ist;
	}

	@Override
	public boolean onItemUseFirst(ItemStack ist, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (world.getTileEntity(x, y, z) instanceof IInventory) {
			IInventory inventory = (IInventory) world.getTileEntity(x, y, z);
			unloadContentsIntoInventory(ist, inventory, player);
			return false;
		}
		return false;
	}

	public void unloadContentsIntoInventory(ItemStack ist, IInventory inventory, EntityPlayer player) {
		NBTTagCompound tearTag = ist.getTagCompound();
		if (tearTag == null)
			return;
		ItemStack contents = new ItemStack((Item) Item.itemRegistry.getObject(tearTag.getString("itemID")), 1, tearTag.getShort("itemMeta"));
		int quantity = tearTag.getShort("itemQuantity");
		while (quantity > 0) {
			if (!tryToAddToInventory(contents, inventory)) {
				break;
			}
			quantity--;
		}
		if (quantity == 0) {
			addEmptyTearToPlayerInventory(player);
			player.worldObj.playSoundAtEntity(player, "random.orb", 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.8F));

			player.inventory.decrStackSize(player.inventory.currentItem, 1);
			return;
		} else {
			player.worldObj.playSoundAtEntity(player, "random.orb", 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.8F));
			tearTag.setShort("itemQuantity", (short) quantity);
			ist.setTagCompound(tearTag);
		}
		return;
	}

	public void unloadContentsIntoPlayerInventory(ItemStack ist, IInventory inventory, EntityPlayer player) {
		NBTTagCompound tearTag = ist.getTagCompound();
		if (tearTag == null)
			return;
		ItemStack contents = new ItemStack((Item) Item.itemRegistry.getObject(tearTag.getString("itemID")), 1, tearTag.getShort("itemMeta"));
		int quantity = tearTag.getShort("itemQuantity");
		while (quantity > 0) {
			if (!tryToAddToPlayerInventory(contents, inventory, player)) {
				break;
			}
			quantity--;
		}
		if (quantity == 0) {
			addEmptyTearToPlayerInventory(player);
			player.worldObj.playSoundAtEntity(player, "random.orb", 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.8F));
			player.inventory.decrStackSize(player.inventory.currentItem, 1);
			return;
		} else {
			player.worldObj.playSoundAtEntity(player, "random.orb", 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.8F));
			tearTag.setShort("itemQuantity", (short) quantity);
			ist.setTagCompound(tearTag);
		}
		return;
	}

	private void addEmptyTearToPlayerInventory(EntityPlayer player) {
		if (!player.inventory.addItemStackToInventory(new ItemStack(XRItems.emptyVoidTear, 1))) {
			player.worldObj.spawnEntityInWorld(new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, new ItemStack(XRItems.emptyVoidTear, 1)));
		}
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
				inventory.setInventorySlotContents(slot, new ItemStack(contents.getItem(), contents.stackSize, contents.getItemDamage()));
				return true;
			}
		}
		return false;
	}

	public boolean tryToAddToPlayerInventory(ItemStack contents, IInventory inventory, EntityPlayer player) {
		for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
			if (inventory.getStackInSlot(slot) == null) {
				continue;
			}
			if (slot >= player.inventory.mainInventory.length) {
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
				inventory.setInventorySlotContents(slot, new ItemStack(contents.getItem(), contents.stackSize, contents.getItemDamage()));
				return true;
			}
		}
		return false;
	}
}
