package reliquary.blocks.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import reliquary.init.ModBlocks;
import reliquary.util.InventoryHelper;

import javax.annotation.Nullable;

public class PassivePedestalBlockEntity extends BlockEntityBase implements Container {
	protected ItemStack item;
	private final IItemHandler inventoryWrapper = new InvWrapper(this);

	public ItemStack getItem() {
		return item;
	}

	public PassivePedestalBlockEntity(BlockPos pos, BlockState state) {
		this(ModBlocks.PASSIVE_PEDESTAL_TILE_TYPE.get(), pos, state);
	}

	PassivePedestalBlockEntity(BlockEntityType<?> tileEntityType, BlockPos pos, BlockState state) {
		super(tileEntityType, pos, state);
		item = ItemStack.EMPTY;
	}

	public void dropPedestalInventory(Level level) {
		if (!item.isEmpty()) {
			InventoryHelper.spawnItemStack(level, worldPosition, item);
		}
	}

	public void removeAndSpawnItem(Level level) {
		if (!item.isEmpty()) {
			if (!level.isClientSide) {
				setChanged();
				ItemEntity itemEntity = new ItemEntity(level, worldPosition.getX() + 0.5D, worldPosition.getY() + 1D, worldPosition.getZ() + 0.5D, item);
				level.addFreshEntity(itemEntity);
			}
			item = ItemStack.EMPTY;
		}
	}

	@Override
	public int getContainerSize() {
		return 1;
	}

	@Override
	public ItemStack getItem(int index) {
		return index == 0 ? item : ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItem(int index, int count) {
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
	public ItemStack removeItemNoUpdate(int index) {
		if (index == 0) {
			ItemStack stack = item;
			item = ItemStack.EMPTY;
			notifyBlock();
			return stack;
		}

		return ItemStack.EMPTY;
	}

	private void notifyBlock() {
		if (level == null) {
			return;
		}
		BlockState blockState = level.getBlockState(getBlockPos());
		level.sendBlockUpdated(getBlockPos(), blockState, blockState, 3);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		item = nbt.contains("item") ? ItemStack.of(nbt.getCompound("item")) : ItemStack.EMPTY;
	}

	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);

		if (!item.isEmpty()) {
			compound.put("item", item.save(new CompoundTag()));
		}
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		if (index == 0) {
			item = stack;
			notifyBlock();
		}
	}

	@Override
	public boolean stillValid(Player player) {
		return false;
	}

	@Override
	public void startOpen(Player player) {
		//noop
	}

	@Override
	public void stopOpen(Player player) {
		//noop
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return index == 0;
	}

	@Override
	public void clearContent() {
		for (int i = 0; i < getContainerSize(); i++) {
			setItem(i, ItemStack.EMPTY);
		}
	}

	@Override
	public boolean isEmpty() {
		return item.isEmpty();
	}


	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
		if (cap == ForgeCapabilities.ITEM_HANDLER) {
			return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, LazyOptional.of(() -> inventoryWrapper));
		}
		return super.getCapability(cap, side);
	}
}
