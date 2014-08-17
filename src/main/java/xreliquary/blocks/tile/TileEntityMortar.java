package xreliquary.blocks.tile;

import lib.enderwizards.sandstone.blocks.tile.TileEntityBase;
import lib.enderwizards.sandstone.blocks.tile.TileEntityInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import xreliquary.lib.Reference;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class TileEntityMortar extends TileEntityInventory {

	// counts the number of times the player has right clicked the block
	// arbitrarily setting the number of times the player needs to grind the
	// materials to five.
	private int pestleUsedCounter;
	private String customInventoryName;

	public TileEntityMortar() {
		super(2);
		pestleUsedCounter = 0;
	}

	@Override
	public void updateEntity() {
		// do stuff on tick? I don't think we need this to tick.
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagList items = tag.getTagList("Items", 10);
		this.inventory = new ItemStack[this.getSizeInventory()];

		for (int i = 0; i < items.tagCount(); ++i) {
			NBTTagCompound item = items.getCompoundTagAt(i);
			byte b0 = item.getByte("Slot");

			if (b0 >= 0 && b0 < this.inventory.length) {
				this.inventory[b0] = ItemStack.loadItemStackFromNBT(item);
				if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
					System.out.println("Hi #1");
			}
		}

		this.pestleUsedCounter = tag.getShort("pestleUsed");

		if (tag.hasKey("CustomName", 8)) {
			this.customInventoryName = tag.getString("CustomName");
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setShort("pestleUsed", (short) this.pestleUsedCounter);
		NBTTagList items = new NBTTagList();

		for (int i = 0; i < this.inventory.length; ++i) {
			if (this.inventory[i] != null) {
				NBTTagCompound item = new NBTTagCompound();
				this.inventory[i].writeToNBT(item);
				item.setByte("Slot", (byte) i);
				System.out.println("Hi #2");
				items.appendTag(item);
			}
		}

		tag.setTag("Items", items);

		if (this.hasCustomInventoryName()) {
			tag.setString("CustomName", this.getInventoryName());
		}
	}

	// gets the contents of the tile entity as an array of inventory
	public ItemStack[] getItemStacks() {
		return inventory;
	}

	// increases the "pestleUsed" counter, checks to see if it is at its limit
	public void usePestle() {
		pestleUsedCounter++;

		if (pestleUsedCounter >= Reference.PESTLE_USAGE_MAX) {
			// do things
		}
	}

	@Override
	public String getInventoryName() {
		return customInventoryName;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return customInventoryName != null;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) {
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : var1.getDistanceSq((double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D, (double) this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(int var1, ItemStack var2) {
		// don't allow essence in slots after the second one.
		// eventually would be nice to replace this with something that was more
		// elaborate about
		// allowing potions to mix, and rejecting items that aren't even
		// ingredients.
		// isIngredient would be nice.
		return var1 >= 2 && isItemEssence(var2) ? false : true;
	}

	public boolean isItemEssence(ItemStack ist) {
		// essence not quite a thing just yet, force return true.
		return false;
	}
}
