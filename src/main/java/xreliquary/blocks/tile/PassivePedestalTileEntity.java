package xreliquary.blocks.tile;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.registries.ObjectHolder;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.util.InjectionHelper;
import xreliquary.util.InventoryHelper;

import javax.annotation.Nullable;

public class PassivePedestalTileEntity extends TileEntityBase implements IInventory {
	@SuppressWarnings("WeakerAccess")
	@ObjectHolder(Reference.MOD_ID + ":" + Names.Blocks.PASSIVE_PEDESTAL)
	public static final TileEntityType<PassivePedestalTileEntity> TYPE = InjectionHelper.nullValue();
	protected ItemStack item;
	private IItemHandler inventoryWrapper = new InvWrapper(this);

	public ItemStack getItem() {
		return item;
	}

	public PassivePedestalTileEntity() {
		this(TYPE);
	}

	PassivePedestalTileEntity(TileEntityType<?> tileEntityType) {
		super(tileEntityType);
		item = ItemStack.EMPTY;
	}

	public void dropPedestalInventory() {
		if (!item.isEmpty()) {
			InventoryHelper.spawnItemStack(world, pos, item);
		}
	}

	public void removeAndSpawnItem() {
		if (!item.isEmpty()) {
			if (!world.isRemote) {
				markDirty();
				ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D, item);
				world.addEntity(itemEntity);
			}
			item = ItemStack.EMPTY;
		}
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return index == 0 ? item : ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		if (index == 0) {
			return decrStackInInventory(count);
		}
		return ItemStack.EMPTY;
	}

	private ItemStack decrStackInInventory(int count) {
		if (!item.isEmpty()) {
			ItemStack stack = item.split(count);

			if (item.isEmpty()) {
				notifyBlock();
			}

			return stack;
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		if (index == 0) {
			ItemStack stack = item;
			item = ItemStack.EMPTY;
			notifyBlock();
			return stack;
		}

		return ItemStack.EMPTY;
	}

	private void notifyBlock() {
		BlockState blockState = world.getBlockState(getPos());
		world.notifyBlockUpdate(getPos(), blockState, blockState, 3);
	}

	@Override
	public void read(CompoundNBT compound) {
		super.read(compound);
		item = compound.contains("item") ? ItemStack.read(compound.getCompound("item")) : ItemStack.EMPTY;
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);

		if (!item.isEmpty()) {
			compound.put("item", item.write(new CompoundNBT()));
		}
		return compound;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		if (index == 0) {
			item = stack;
			notifyBlock();
		}
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		return false;
	}

	@Override
	public void openInventory(PlayerEntity player) {
		//noop
	}

	@Override
	public void closeInventory(PlayerEntity player) {
		//noop
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return index == 0;
	}

	@Override
	public void clear() {
		for (int i = 0; i < getSizeInventory(); i++) {
			setInventorySlotContents(i, ItemStack.EMPTY);
		}
	}

	@Override
	public boolean isEmpty() {
		return item.isEmpty();
	}


	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> inventoryWrapper));
		}
		return super.getCapability(cap, side);
	}
}
