package reliquary.block.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import reliquary.init.ModBlocks;
import reliquary.util.WorldHelper;

import java.util.Objects;

public class PassivePedestalBlockEntity extends BlockEntityBase {
	private final PedestalInventoryResourceHandler itemHandler = new PedestalInventoryResourceHandler();

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

	public ResourceHandler<ItemResource> getItemHandler() {
		return itemHandler;
	}

	public void removeAndSpawnItem(Level level) {
		ItemStack item = itemHandler.getStackInSlot(0);
		if (!item.isEmpty()) {
			if (!level.isClientSide()) {
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
	protected void loadAdditional(ValueInput in) {
		super.loadAdditional(in);
		itemHandler.setStackInSlot(0, in.read("item", ItemStack.CODEC).orElse(ItemStack.EMPTY));
	}

	@Override
	public void saveAdditional(ValueOutput out) {
		super.saveAdditional(out);

		ItemStack item = getItem();
		if (!item.isEmpty()) {
			out.store("item", ItemStack.CODEC, item);
		}
	}

	private class PedestalInventoryResourceHandler extends ItemStacksResourceHandler {
		public PedestalInventoryResourceHandler() {
			super(1);
		}

		@Override
		protected void onContentsChanged(int index, ItemStack previousContents) {
			if (level != null && !level.isClientSide()) {
				setChangedAndNotifyBlock();
			}
		}

		@Override
		public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
			ItemStack item = getResource(0).toStack();

			int extracted = super.extract(index, resource, amount, transaction);

			if (getResource(0).isEmpty()) {
				onItemRemoved(item);
			}

			return extracted;
		}

		@Override
		public void set(int index, ItemResource resource, int amount) {
			ItemStack item = getResource(0).toStack();

			super.set(index, resource, amount);

			if (getResource(0).isEmpty()) {
				onItemRemoved(item);
			} else {
				onItemAdded();
			}
		}

		@Override
		public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
			boolean wasEmpty = getResource(0).isEmpty();
			int inserted = super.insert(index, resource, amount, transaction);
			if (wasEmpty && !getResource(0).isEmpty()) {
				onItemAdded();
			}
			return inserted;
		}

		public ItemStack getStackInSlot(int slot) {
			Objects.checkIndex(slot, size());
			return stacks.get(slot);
		}

		public void setStackInSlot(int index, ItemStack stack) {
			Objects.checkIndex(index, size());
			ItemStack oldContents = this.stacks.set(index, stack);
			this.onContentsChanged(index, oldContents);
		}
	}
}
