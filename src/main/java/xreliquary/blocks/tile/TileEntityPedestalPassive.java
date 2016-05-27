package xreliquary.blocks.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import xreliquary.util.InventoryHelper;

public class TileEntityPedestalPassive extends TileEntityBase implements IInventory {
	protected ItemStack[] inventory;
	protected short slots = 0;
	private EnumDyeColor color = EnumDyeColor.RED;

	public TileEntityPedestalPassive() {
		this.slots = 1;
		inventory = new ItemStack[this.slots];

	}

	public void dropPedestalInventory() {
		for(int i = 0; i < inventory.length; ++i) {
			ItemStack itemstack = inventory[i];

			if(itemstack != null) {
				InventoryHelper.spawnItemStack(this.worldObj, this.pos.getX(), this.pos.getY(), this.pos.getZ(), itemstack);
			}
		}
	}

	public void removeLastPedestalStack() {
		for(int i = slots - 1; i >= 0; i--) {
			if(inventory[i] != null) {
				ItemStack stack = inventory[i].copy();
				setInventorySlotContents(i, null);
				if(worldObj.isRemote)
					return;
				markDirty();
				EntityItem itemEntity = new EntityItem(worldObj, pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D, stack);
				worldObj.spawnEntityInWorld(itemEntity);
				break;
			}
		}

	}

	@Override
	public int getSizeInventory() {
		return slots;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return index < inventory.length ? inventory[index] : null;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		if(index < slots) {
			return decrStackInInventory(index, count);
		}
		return null;
	}

	private ItemStack decrStackInInventory(int slot, int count) {
		if(this.inventory[slot] != null) {
			ItemStack stack;

			if(this.inventory[slot].stackSize > count) {
				stack = this.inventory[slot].splitStack(count);
			} else {
				stack = this.inventory[slot];

				this.inventory[slot] = null;

				notifyBlock();
			}

			return stack;
		} else {
			return null;
		}
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		if(index < slots) {
			ItemStack stack = this.inventory[index];

			this.inventory[index] = null;

			notifyBlock();

			return stack;
		}

		return null;
	}

	private void notifyBlock() {
		IBlockState blockState = worldObj.getBlockState(getPos());
		worldObj.notifyBlockUpdate(getPos(), blockState, blockState, 3);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);

		NBTTagList items = compound.getTagList("Items", 10);

		this.inventory = new ItemStack[this.getSizeInventory()];

		for(int i = 0; i < items.tagCount(); ++i) {
			NBTTagCompound item = items.getCompoundTagAt(i);
			byte b0 = item.getByte("Slot");

			if(b0 >= 0 && b0 < this.inventory.length) {
				this.inventory[b0] = ItemStack.loadItemStackFromNBT(item);
			}
		}
		color = EnumDyeColor.byMetadata(compound.getInteger("color"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);

		NBTTagList items = new NBTTagList();

		for(int i = 0; i < this.inventory.length; ++i) {
			if(this.inventory[i] != null) {
				NBTTagCompound item = new NBTTagCompound();
				this.inventory[i].writeToNBT(item);
				item.setByte("Slot", (byte) i);
				items.appendTag(item);
			}
		}
		compound.setTag("Items", items);
		compound.setInteger("color", color.getMetadata());

		return compound;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		if(index < slots) {
			this.inventory[index] = stack;
			if(stack != null && stack.stackSize > 1) {
				stack.stackSize = 1;
			}

			notifyBlock();
		}
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return false;
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		if(index < slots)
			return true;
		return false;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		for(int i = 0; i < this.getSizeInventory(); i++)
			this.setInventorySlotContents(i, null);
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return null;
	}

	public EnumDyeColor getClothColor() {
		return color;
	}

	public void setColor(EnumDyeColor color) {
		this.color = color;
	}
}
