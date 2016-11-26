package xreliquary.blocks.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import xreliquary.util.InventoryHelper;

import javax.annotation.Nonnull;

public class TileEntityPedestalPassive extends TileEntityBase implements IInventory {
	protected NonNullList<ItemStack> inventory;
	protected short slots = 0;
	private EnumDyeColor color = EnumDyeColor.RED;

	public TileEntityPedestalPassive() {
		this.slots = 1;
		inventory = NonNullList.withSize(this.slots, ItemStack.EMPTY);

	}

	public void dropPedestalInventory() {
		inventory.stream().filter(itemstack -> !itemstack.isEmpty())
				.forEach(itemstack -> InventoryHelper.spawnItemStack(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), itemstack));
	}

	public void removeLastPedestalStack() {
		for(int i = slots - 1; i >= 0; i--) {
			if(!inventory.get(i).isEmpty()) {
				ItemStack stack = inventory.get(i).copy();
				setInventorySlotContents(i, ItemStack.EMPTY);
				if(world.isRemote)
					return;
				markDirty();
				EntityItem itemEntity = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D, stack);
				world.spawnEntity(itemEntity);
				break;
			}
		}

	}

	@Override
	public int getSizeInventory() {
		return slots;
	}

	@Nonnull
	@Override
	public ItemStack getStackInSlot(int index) {
		return index < inventory.size() ? inventory.get(index) : ItemStack.EMPTY;
	}

	@Nonnull
	@Override
	public ItemStack decrStackSize(int index, int count) {
		if(index < slots) {
			return decrStackInInventory(index, count);
		}
		return ItemStack.EMPTY;
	}

	private ItemStack decrStackInInventory(int slot, int count) {
		if(!this.inventory.get(slot).isEmpty()) {
			ItemStack stack;

			stack = this.inventory.get(slot).splitStack(count);

			if(this.inventory.get(slot).isEmpty()) {
				notifyBlock();
			}

			return stack;
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Nonnull
	@Override
	public ItemStack removeStackFromSlot(int index) {
		if(index < slots) {
			ItemStack stack = ItemStackHelper.getAndRemove(inventory, index);

			notifyBlock();

			return stack;
		}

		return ItemStack.EMPTY;
	}

	private void notifyBlock() {
		IBlockState blockState = world.getBlockState(getPos());
		world.notifyBlockUpdate(getPos(), blockState, blockState, 3);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);

		NBTTagList items = compound.getTagList("Items", 10);

		this.inventory = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);

		for(int i = 0; i < items.tagCount(); ++i) {
			NBTTagCompound item = items.getCompoundTagAt(i);
			byte slot = item.getByte("Slot");

			if(slot >= 0 && slot < this.inventory.size()) {
				this.inventory.set(slot, new ItemStack(item));
			}
		}
		color = EnumDyeColor.byMetadata(compound.getInteger("color"));
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);

		NBTTagList items = new NBTTagList();

		for(int slot = 0; slot < this.inventory.size(); ++slot) {
			if(!this.inventory.get(slot).isEmpty()) {
				NBTTagCompound item = new NBTTagCompound();
				this.inventory.get(slot).writeToNBT(item);
				item.setByte("Slot", (byte) slot);
				items.appendTag(item);
			}
		}
		compound.setTag("Items", items);
		compound.setInteger("color", color.getMetadata());

		return compound;
	}

	@Override
	public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
		if(index < slots) {
			this.inventory.set(index, stack);
			if(!stack.isEmpty() && stack.getCount() > 1) {
				stack.setCount(1);
			}

			notifyBlock();
		}
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(@Nonnull EntityPlayer player) {
		return false;
	}

	@Override
	public void openInventory(@Nonnull EntityPlayer player) {

	}

	@Override
	public void closeInventory(@Nonnull EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
		return index < slots;
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
			this.setInventorySlotContents(i, ItemStack.EMPTY);
	}

	@Nonnull
	@Override
	public String getName() {
		return "container.tile_entity_pedestal";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Nonnull
	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString("Pedestal");
	}

	public EnumDyeColor getClothColor() {
		return color;
	}

	public void setColor(EnumDyeColor color) {
		this.color = color;
	}

	@Override
	public boolean isEmpty() {
		for(ItemStack stack : inventory) {
			if(!stack.isEmpty())
				return false;
		}
		return true;
	}
}
