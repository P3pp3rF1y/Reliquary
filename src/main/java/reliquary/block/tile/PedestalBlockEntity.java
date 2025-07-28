package reliquary.block.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.IItemHandler;
import reliquary.api.IPedestal;
import reliquary.api.IPedestalActionItem;
import reliquary.api.IPedestalRedstoneItem;
import reliquary.api.IPedestalRedstoneItemWrapper;
import reliquary.block.PedestalBlock;
import reliquary.init.ModBlocks;
import reliquary.pedestal.PedestalRegistry;
import reliquary.util.CombinedItemHandler;
import reliquary.util.FakePlayerFactory;
import reliquary.util.InventoryHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class PedestalBlockEntity extends PassivePedestalBlockEntity implements IPedestal {
	private boolean tickable = false;
	private int actionCooldown = 0;
	@Nullable
	private IPedestalActionItem actionItem = null;
	@Nullable
	private IPedestalRedstoneItem redstoneItem = null;
	@Nullable
	private IItemHandler itemHandler = null;
	@Nullable
	private IItemHandler combinedHandler = null;
	private ItemStack fluidContainer = ItemStack.EMPTY;
	private boolean switchedOn = false;
	private final List<Long> onSwitches = new ArrayList<>();
	private boolean enabledInitialized = false;
	private boolean powered = false;
	private PedestalFluidHandler pedestalFluidHandler = null;
	private Object itemData = null;

	public PedestalBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlocks.PEDESTAL_TILE_TYPE.get(), pos, state);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);

		switchedOn = tag.getBoolean("SwitchedOn");
		powered = tag.getBoolean("Powered");

		ListTag onLocations = tag.getList("OnSwitches", 4);

		onSwitches.clear();

		for (Tag onLocation : onLocations) {
			onSwitches.add(((LongTag) onLocation).getAsLong());
		}

		updateSpecialItems();
	}

	@Override
	public void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
		super.saveAdditional(compound, registries);

		compound.putBoolean("SwitchedOn", switchedOn);
		compound.putBoolean("Powered", powered);

		ListTag onLocations = new ListTag();

		for (Long onSwitch : onSwitches) {
			onLocations.add(LongTag.valueOf(onSwitch));
		}
		compound.put("OnSwitches", onLocations);
	}

	@Override
	public void onChunkUnloaded() {
		if (level != null && !level.isClientSide) {
			PedestalRegistry.unregisterPosition(level.dimension().registry(), worldPosition);
		}

		super.onChunkUnloaded();
	}

	@Override
	public void onLoad() {
		if (level != null && !level.isClientSide) {
			PedestalRegistry.registerPosition(level.dimension().registry(), worldPosition);
		}

		super.onLoad();
	}

	@Override
	public IItemHandler getItemHandler() {
		IItemHandler superInventory = super.getItemHandler();
		if (itemHandler == null) {
			return superInventory;
		}
		if (combinedHandler == null) {
			combinedHandler = new CombinedItemHandler(superInventory, itemHandler);
		}
		return combinedHandler;
	}

	public IFluidHandler getFluidHandler() {
		if (pedestalFluidHandler == null) {
			pedestalFluidHandler = new PedestalFluidHandler(this);
		}
		return pedestalFluidHandler;
	}

	public void executeOnActionItem(Consumer<IPedestalActionItem> execute) {
		if (actionItem == null) {
			return;
		}
		execute.accept(actionItem);
	}

	private void executeOnRedstoneItem(Consumer<IPedestalRedstoneItem> execute) {
		if (redstoneItem == null) {
			return;
		}
		execute.accept(redstoneItem);
	}

	private void updateSpecialItems() {
		resetSpecialItems();

		ItemStack item = getItem();
		if (item.isEmpty()) {
			return;
		}

		IItemHandler ih = item.getCapability(Capabilities.ItemHandler.ITEM);
		if (ih != null) {
			itemHandler = ih;
		}

		if (item.getItem() instanceof IPedestalActionItem pedestalActionItem) {
			tickable = true;
			actionItem = pedestalActionItem;
		} else if (item.getItem() instanceof IPedestalRedstoneItem pedestalRedstoneItem) {
			redstoneItem = pedestalRedstoneItem;
		} else {
			PedestalRegistry.getItemWrapper(item).ifPresent(wrapper -> {
				if (wrapper instanceof IPedestalActionItem pedestalActionItem) {
					tickable = true;
					actionItem = pedestalActionItem;
				}
				if (wrapper instanceof IPedestalRedstoneItemWrapper) {
					redstoneItem = (IPedestalRedstoneItem) wrapper;
				}
			});
		}


		IFluidHandlerItem itemFluidHandler = item.getCapability(Capabilities.FluidHandler.ITEM);
		if (itemFluidHandler != null) {
			fluidContainer = item;
		}

		actionCooldown = 0;
	}

	private void resetSpecialItems() {
		tickable = false;
		fluidContainer = ItemStack.EMPTY;
		actionItem = null;
		redstoneItem = null;
		itemHandler = null;
		if (combinedHandler != null) {
			level.invalidateCapabilities(getBlockPos());
		}
		combinedHandler = null;
	}

	public void serverTick(Level level) {
		if (level.isClientSide) {
			return;
		}

		if (!enabledInitialized) {
			enabledInitialized = true;
			neighborUpdate(level);
		}

		if (tickable && isEnabled()) {
			if (actionCooldown > 0) {
				actionCooldown--;
			} else {
				executeOnActionItem(ai -> ai.update(getItem(), level, this));
			}
		}
	}

	public void neighborUpdate(Level level) {
		if (powered != level.hasNeighborSignal(worldPosition)) {
			powered = level.hasNeighborSignal(worldPosition);

			if (powered) {
				switchOn(level, BlockPos.ZERO);
			} else {
				switchOff(level, BlockPos.ZERO);
			}
		}

		updateRedstone(level);
	}

	public void updateRedstone(Level level) {
		executeOnRedstoneItem(ri -> ri.updateRedstone(getItem(), level, this));
	}

	@Override
	public BlockPos getBlockPosition() {
		return getBlockPos();
	}

	@Override
	public int addToConnectedInventory(Level level, ItemStack stack) {
		int numberAdded = 0;
		for (Direction side : Direction.values()) {
			numberAdded += InventoryHelper.tryToAddToInventoryAtPos(stack, level, worldPosition.offset(side.getNormal()), side.getOpposite(), stack.getCount() - numberAdded);
			if (numberAdded >= stack.getCount()) {
				break;
			}
		}

		return numberAdded;
	}

	@Override
	public int fillConnectedTank(FluidStack fluidStack, IFluidHandler.FluidAction action) {
		List<IFluidHandler> adjacentTanks = getAdjacentTanks();

		int fluidFilled = 0;
		FluidStack copy = fluidStack.copy();

		for (IFluidHandler tank : adjacentTanks) {
			if (tank.fill(copy, IFluidHandler.FluidAction.SIMULATE) == copy.getAmount()) {
				fluidFilled += tank.fill(copy, action);

				if (fluidFilled >= fluidStack.getAmount()) {
					break;
				} else {
					copy.setAmount(fluidStack.getAmount() - fluidFilled);
				}
			}
		}

		return fluidFilled;
	}

	@Override
	public int fillConnectedTank(FluidStack fluidStack) {
		return fillConnectedTank(fluidStack, IFluidHandler.FluidAction.EXECUTE);
	}

	@Override
	public void setActionCoolDown(int coolDownTicks) {
		actionCooldown = coolDownTicks;
	}

	@Override
	public Optional<FakePlayer> getFakePlayer() {
		if (level == null || !(level instanceof ServerLevel serverLevel)) {
			return Optional.empty();
		}
		return Optional.of(FakePlayerFactory.get(serverLevel));
	}

	@Override
	public void destroyItem() {
		setItem(ItemStack.EMPTY);
	}

	@Override
	public List<BlockPos> getPedestalsInRange(Level level, int range) {
		return PedestalRegistry.getPositionsInRange(level.dimension().registry(), worldPosition, range);
	}

	@Override
	public void switchOn(Level level, BlockPos switchedOnFrom) {
		if (switchedOnFrom != BlockPos.ZERO && !onSwitches.contains(switchedOnFrom.asLong())) {
			onSwitches.add(switchedOnFrom.asLong());
		}

		setEnabled(level, true);

		BlockState blockState = level.getBlockState(worldPosition);
		level.sendBlockUpdated(worldPosition, blockState, blockState, 3);
	}

	@Override
	public void switchOff(Level level, BlockPos switchedOffFrom) {
		if (switchedOffFrom != BlockPos.ZERO) {
			onSwitches.remove(switchedOffFrom.asLong());
		}

		if (!switchedOn && !powered && onSwitches.isEmpty()) {
			setEnabled(level, false);
		}
		BlockState blockState = level.getBlockState(worldPosition);
		level.sendBlockUpdated(worldPosition, blockState, blockState, 3);
	}

	@Override
	public Optional<Object> getItemData() {
		return Optional.ofNullable(itemData);
	}

	@Override
	public void setItemData(@Nullable Object data) {
		itemData = data;
	}

	@Override
	public boolean switchedOn() {
		return switchedOn;
	}

	public boolean isPowered() {
		return powered;
	}

	public List<Long> getOnSwitches() {
		return onSwitches;
	}

	private void setEnabled(Level level, boolean switchedOn) {
		if (level.getBlockState(worldPosition).getBlock() instanceof PedestalBlock) {
			level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(PedestalBlock.ENABLED, switchedOn));
			if (!switchedOn) {
				executeOnActionItem(ai -> ai.stop(getItem(), level, this));
			}
		}
		setChanged();
	}

	private List<IFluidHandler> getAdjacentTanks() {
		List<IFluidHandler> adjacentTanks = new ArrayList<>();

		for (Direction side : Direction.values()) {
			BlockPos tankPos = getBlockPos().offset(side.getNormal());
			Direction tankDirection = side.getOpposite();
			addIfTank(adjacentTanks, tankPos, tankDirection);
		}

		return adjacentTanks;
	}

	private void addIfTank(List<IFluidHandler> adjacentTanks, BlockPos tankPos, Direction tankDirection) {
		IFluidHandler fh = level.getCapability(Capabilities.FluidHandler.BLOCK, tankPos, tankDirection);
		if (fh != null) {
			adjacentTanks.add(fh);
		}
	}

	public void removeSpecialItems(Level level) {
		removeSpecialItems(level, getItem());
	}

	public void removeSpecialItems(Level level, ItemStack itemBeingRemoved) {
		executeOnRedstoneItem(ri -> ri.onRemoved(itemBeingRemoved, level, this));
		executeOnActionItem(ai -> ai.onRemoved(itemBeingRemoved, level, this));
	}

	@Override
	public void removeAndSpawnItem(Level level) {
		removeSpecialItems(level);
		resetSpecialItems();
		super.removeAndSpawnItem(level);
	}

	@Override
	protected void onItemRemoved(ItemStack itemBeingRemoved) {
		super.onItemRemoved(itemBeingRemoved);
		if (level != null) {
			removeSpecialItems(level, itemBeingRemoved);
		}
		updateItemsAndBlock();
	}

	@Override
	protected void onItemAdded() {
		super.onItemAdded();
		updateItemsAndBlock();
	}

	private void updateItemsAndBlock() {
		updateSpecialItems();
		if (level == null) {
			return;
		}
		updateRedstone(level);
		BlockState blockState = level.getBlockState(getBlockPos());
		level.sendBlockUpdated(getBlockPos(), blockState, blockState, 3);
	}

	public void toggleSwitch(Level level) {
		switchedOn = !switchedOn;

		if (switchedOn) {
			switchOn(level, BlockPos.ZERO);
		} else {
			switchOff(level, BlockPos.ZERO);
		}

		updateRedstone(level);
	}

	ItemStack getFluidContainer() {
		return fluidContainer;
	}

	public boolean isEnabled() {
		return getBlockState().getValue(PedestalBlock.ENABLED);
	}
}