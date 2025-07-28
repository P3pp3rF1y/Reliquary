package reliquary.block.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import reliquary.init.ModBlocks;
import reliquary.util.InventoryHelper;
import reliquary.util.WorldHelper;

public class PassivePedestalBlockEntity extends BlockEntityBase {
	private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
		@Override
		protected void onContentsChanged(int slot) {
			if (level != null && !level.isClientSide) {
				setChangedAndNotifyBlock();
			}
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			ItemStack item = getStackInSlot(0).copy();

			ItemStack result = super.extractItem(slot, amount, simulate);

			if (getStackInSlot(0).isEmpty()) {
				onItemRemoved(item);
			}

			return result;
		}

		@Override
		public void setStackInSlot(int slot, ItemStack stack) {
			ItemStack item = getStackInSlot(0).copy();

			super.setStackInSlot(slot, stack);

			if (getStackInSlot(0).isEmpty()) {
				onItemRemoved(item);
			} else {
				onItemAdded();
			}
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			boolean wasEmpty = getStackInSlot(0).isEmpty();
			ItemStack result = super.insertItem(slot, stack, simulate);
			if (wasEmpty && !getStackInSlot(0).isEmpty()) {
				onItemAdded();
			}
			return result;
		}
	};

	public ItemStack getItem() {
		return itemHandler.getStackInSlot(0);
	}

	public void setItem(ItemStack item) {
		itemHandler.setStackInSlot(0, item);
	}

	protected void onItemRemoved(ItemStack itemBeingRemoved) {
		// Override this method in subclasses to handle item removal logic
	}

	protected void onItemAdded() {
		// Override this method in subclasses to handle item addition logic
	}

	public PassivePedestalBlockEntity(BlockPos pos, BlockState state) {
		this(ModBlocks.PASSIVE_PEDESTAL_TILE_TYPE.get(), pos, state);
	}

	PassivePedestalBlockEntity(BlockEntityType<?> tileEntityType, BlockPos pos, BlockState state) {
		super(tileEntityType, pos, state);
	}

	public IItemHandler getItemHandler() {
		return itemHandler;
	}

	public void dropPedestalInventory(Level level) {
		InventoryHelper.dropInventoryItems(level, worldPosition, itemHandler);
	}

	public void removeAndSpawnItem(Level level) {
		ItemStack item = itemHandler.getStackInSlot(0);
		if (!item.isEmpty()) {
			if (!level.isClientSide) {
				setChanged();
				ItemEntity itemEntity = new ItemEntity(level, worldPosition.getX() + 0.5D, worldPosition.getY() + 1D, worldPosition.getZ() + 0.5D, item);
				level.addFreshEntity(itemEntity);
				WorldHelper.notifyBlockUpdate(this);
			}
			itemHandler.setStackInSlot(0, ItemStack.EMPTY);
		}
	}

	private void setChangedAndNotifyBlock() {
		if (level == null) {
			return;
		}
		setChanged();
		BlockState blockState = level.getBlockState(getBlockPos());
		level.sendBlockUpdated(getBlockPos(), blockState, blockState, 3);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		itemHandler.setStackInSlot(0, tag.contains("item") ? ItemStack.parse(registries, tag.getCompound("item")).orElse(ItemStack.EMPTY) : ItemStack.EMPTY);
	}

	@Override
	public void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
		super.saveAdditional(compound, registries);

		ItemStack item = getItem();
		if (!item.isEmpty()) {
			compound.put("item", item.save(registries));
		}
	}
}
