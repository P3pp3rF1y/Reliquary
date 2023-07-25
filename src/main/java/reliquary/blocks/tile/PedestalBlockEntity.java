package reliquary.blocks.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import reliquary.api.IPedestal;
import reliquary.api.IPedestalActionItem;
import reliquary.api.IPedestalRedstoneItem;
import reliquary.api.IPedestalRedstoneItemWrapper;
import reliquary.blocks.PedestalBlock;
import reliquary.init.ModBlocks;
import reliquary.items.util.FilteredItemStackHandler;
import reliquary.pedestal.PedestalRegistry;
import reliquary.util.InventoryHelper;
import reliquary.util.WorldHelper;
import reliquary.util.XRFakePlayerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class PedestalBlockEntity extends PassivePedestalBlockEntity implements IPedestal {
	private boolean tickable = false;
	private int actionCooldown = 0;
	@Nullable
	private IPedestalActionItem actionItem = null;
	@Nullable
	private IPedestalRedstoneItem redstoneItem = null;
	@Nullable
	private IItemHandler itemHandler = null;
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
	public void load(CompoundTag tag) {
		super.load(tag);

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
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);

		compound.putBoolean("SwitchedOn", switchedOn);
		compound.putBoolean("Powered", powered);

		ListTag onLocations = new ListTag();

		for (Long onSwitch : onSwitches) {
			onLocations.add(LongTag.valueOf(onSwitch));
		}
		compound.put("OnSwitches", onLocations);
	}

	@Override
	public void setChanged() {
		if (itemHandler instanceof FilteredItemStackHandler filteredHandler) {
			filteredHandler.markDirty();
		}

		super.setChanged();
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

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
		if (cap == ForgeCapabilities.FLUID_HANDLER) {
			if (pedestalFluidHandler == null) {
				pedestalFluidHandler = new PedestalFluidHandler(this);
			}
			return ForgeCapabilities.FLUID_HANDLER.orEmpty(cap, LazyOptional.of(() -> pedestalFluidHandler));
		}

		return super.getCapability(cap, side);
	}

	private void executeOnActionItem(Consumer<IPedestalActionItem> execute) {
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

		if (item.isEmpty()) {
			return;
		}

		item.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(ih -> itemHandler = ih);

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

		item.getCapability(ForgeCapabilities.FLUID_HANDLER, null).ifPresent(fh -> fluidContainer = item);
		item.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null).ifPresent(fh -> fluidContainer = item);

		actionCooldown = 0;
	}

	private void resetSpecialItems() {
		tickable = false;
		fluidContainer = ItemStack.EMPTY;
		actionItem = null;
		redstoneItem = null;
		itemHandler = null;
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
				executeOnActionItem(ai -> ai.update(item, level, this));
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
		executeOnRedstoneItem(ri -> ri.updateRedstone(item, level, this));
	}

	@Override
	public BlockPos getBlockPosition() {
		return getBlockPos();
	}

	@Override
	public int addToConnectedInventory(Level level, ItemStack stack) {
		int numberAdded = 0;
		for (Direction side : Direction.values()) {
			LazyOptional<IItemHandler> inventory = InventoryHelper.getInventoryAtPos(level, worldPosition.offset(side.getNormal()), side.getOpposite());

			int finalNumberAdded = numberAdded;
			numberAdded += inventory.map(handler -> InventoryHelper.tryToAddToInventory(stack, handler, stack.getCount() - finalNumberAdded)).orElse(0);
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
		if (level == null || level.isClientSide) {
			return Optional.empty();
		}

		ServerLevel world = (ServerLevel) level;

		return Optional.of(XRFakePlayerFactory.get(world));
	}

	@Override
	public void destroyItem() {
		setItem(ItemStack.EMPTY);
	}

	@Override
	public void setItem(ItemStack stack) {
		if (level == null) {
			return;
		}

		removeSpecialItems(level);
		item = stack;
		updateItemsAndBlock();
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
				executeOnActionItem(ai -> ai.stop(item, level, this));
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
		WorldHelper.getBlockEntity(level, tankPos).ifPresent(te -> te.getCapability(ForgeCapabilities.FLUID_HANDLER, tankDirection).ifPresent(adjacentTanks::add));
	}

	public void removeSpecialItems(Level level) {
		executeOnRedstoneItem(ri -> ri.onRemoved(item, level, this));
		executeOnActionItem(ai -> ai.onRemoved(item, level, this));
	}

	@Override
	public void removeAndSpawnItem(Level level) {
		removeSpecialItems(level);
		resetSpecialItems();
		super.removeAndSpawnItem(level);
	}

	@Override
	public int getContainerSize() {
		return applyToItemHandler(IItemHandler::getSlots).orElse(0) + 1;
	}

	private <T> Optional<T> applyToItemHandler(Function<IItemHandler, T> function) {
		return itemHandler != null ? Optional.of(function.apply(itemHandler)) : Optional.empty();
	}

	private void runOnItemHandler(Consumer<IItemHandler> consumer) {
		if (itemHandler != null) {
			consumer.accept(itemHandler);
		}
	}

	@Override
	public ItemStack getItem(int slot) {
		if (slot == 0) {
			return item;
		}

		return applyToItemHandler(ih -> ih.getStackInSlot(getInternalItemHandlerSlot(slot))).orElse(ItemStack.EMPTY);
	}

	private int getInternalItemHandlerSlot(int slot) {
		return slot - 1;
	}

	@Override
	public ItemStack removeItem(int slot, int count) {
		if (slot == 0) {
			return decrStack(count);
		}

		return applyToItemHandler(ih -> ih.extractItem(getInternalItemHandlerSlot(slot), count, false)).orElse(ItemStack.EMPTY);
	}

	private ItemStack decrStack(int count) {
		if (!item.isEmpty()) {
			ItemStack stack;

			if (item.getCount() > count) {
				stack = item.split(count);
			} else {
				stack = item;
				if (level != null) {
					removeSpecialItems(level);
				}
				item = ItemStack.EMPTY;
				updateItemsAndBlock();
			}

			return stack;
		} else {
			return ItemStack.EMPTY;
		}
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

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		if (slot == 0) {
			ItemStack stack;
			stack = item;
			if (level != null) {
				removeSpecialItems(level);
			}
			item = ItemStack.EMPTY;

			updateItemsAndBlock();

			return stack;
		}

		return applyToItemHandler(ih -> {
			int s = getInternalItemHandlerSlot(slot);
			ItemStack stack = ih.getStackInSlot(s);
			return ih.extractItem(s, stack.getCount(), false);
		}).orElse(ItemStack.EMPTY);
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		if (slot == 0) {
			setItem(stack);
			return;
		}

		runOnItemHandler(ih -> setItemHandlerSlotContents(slot, stack, ih));
	}

	private void setItemHandlerSlotContents(int slot, ItemStack stack, IItemHandler ih) {
		int adjustedSlot = getInternalItemHandlerSlot(slot);
		ItemStack stackInSlot = ih.getStackInSlot(adjustedSlot);

		if (!stackInSlot.isEmpty() && !stack.isEmpty() && !ItemHandlerHelper.canItemStacksStack(stack, stackInSlot)) {
			return;
		}

		if (!stackInSlot.isEmpty() && (stack.isEmpty() || stack.getCount() < stackInSlot.getCount())) {
			int amount = stackInSlot.getCount() - (stack.isEmpty() ? 0 : stack.getCount());
			ih.extractItem(adjustedSlot, amount, false);
		} else if (!stack.isEmpty() && (stackInSlot.isEmpty() || stack.getCount() > stackInSlot.getCount())) {
			stack.shrink(stackInSlot.isEmpty() ? 0 : stackInSlot.getCount());
			ih.insertItem(adjustedSlot, stack, false);
		}
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		if (super.canPlaceItem(index, stack)) {
			return true;
		}

		return applyToItemHandler(ih -> {
			ItemStack returnedStack = ih.insertItem(getInternalItemHandlerSlot(index), stack, true);
			return returnedStack.isEmpty() || returnedStack.getCount() != stack.getCount();
		}).orElse(false);
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

	@Override
	public AABB getRenderBoundingBox() {
		BlockPos pos = getBlockPos();
		AABB aabb = new AABB(pos.offset(-1, 0, -1), pos.offset(1, 1, 1));
		executeOnActionItem(ai -> ai.getRenderBoundingBoxOuterPosition().ifPresent(aabb::expandTowards));
		return aabb;
	}
}