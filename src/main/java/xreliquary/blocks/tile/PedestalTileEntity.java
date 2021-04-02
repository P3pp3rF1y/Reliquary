package xreliquary.blocks.tile;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import xreliquary.api.IPedestal;
import xreliquary.api.IPedestalActionItem;
import xreliquary.api.IPedestalRedstoneItem;
import xreliquary.api.IPedestalRedstoneItemWrapper;
import xreliquary.blocks.PedestalBlock;
import xreliquary.init.ModBlocks;
import xreliquary.items.util.FilteredItemStackHandler;
import xreliquary.pedestal.PedestalRegistry;
import xreliquary.util.InventoryHelper;
import xreliquary.util.StackHelper;
import xreliquary.util.WorldHelper;
import xreliquary.util.XRFakePlayerFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class PedestalTileEntity extends PassivePedestalTileEntity implements IPedestal, ITickableTileEntity {
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

	public PedestalTileEntity() {
		super(ModBlocks.PEDESTAL_TILE_TYPE.get());
	}

	@Override
	public void read(BlockState state, CompoundNBT tag) {
		super.read(state, tag);

		switchedOn = tag.getBoolean("SwitchedOn");
		powered = tag.getBoolean("Powered");

		ListNBT onLocations = tag.getList("OnSwitches", 4);

		onSwitches.clear();

		for (INBT onLocation : onLocations) {
			onSwitches.add(((LongNBT) onLocation).getLong());
		}

		updateSpecialItems();
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);

		compound.putBoolean("SwitchedOn", switchedOn);
		compound.putBoolean("Powered", powered);

		ListNBT onLocations = new ListNBT();

		for (Long onSwitch : onSwitches) {
			onLocations.add(LongNBT.valueOf(onSwitch));
		}
		compound.put("OnSwitches", onLocations);

		return compound;
	}

	@Override
	public void markDirty() {
		if (itemHandler instanceof FilteredItemStackHandler) {
			FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;

			filteredHandler.markDirty();
		}

		super.markDirty();
	}

	@Override
	public void onChunkUnloaded() {
		if (!world.isRemote) {
			PedestalRegistry.unregisterPosition(world.getDimensionKey().getRegistryName(), pos);
		}

		super.onChunkUnloaded();
	}

	@Override
	public void onLoad() {
		if (!world.isRemote) {
			PedestalRegistry.registerPosition(world.getDimensionKey().getRegistryName(), pos);
		}

		super.onLoad();
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			if (pedestalFluidHandler == null) {
				pedestalFluidHandler = new PedestalFluidHandler(this);
			}
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> pedestalFluidHandler));
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

		item.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(ih -> itemHandler = ih);

		if (item.getItem() instanceof IPedestalActionItem) {
			tickable = true;
			actionItem = (IPedestalActionItem) item.getItem();
		} else if (item.getItem() instanceof IPedestalRedstoneItem) {
			redstoneItem = (IPedestalRedstoneItem) item.getItem();
		} else {
			PedestalRegistry.getItemWrapper(item).ifPresent(wrapper -> {
				if (wrapper instanceof IPedestalActionItem) {
					tickable = true;
					actionItem = (IPedestalActionItem) wrapper;
				}
				if (wrapper instanceof IPedestalRedstoneItemWrapper) {
					redstoneItem = (IPedestalRedstoneItem) wrapper;
				}
			});
		}

		item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).ifPresent(fh -> fluidContainer = item);
		item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).ifPresent(fh -> fluidContainer = item);

		actionCooldown = 0;
	}

	private void resetSpecialItems() {
		tickable = false;
		fluidContainer = ItemStack.EMPTY;
		actionItem = null;
		redstoneItem = null;
		itemHandler = null;
	}

	@Override
	public void tick() {
		if (world.isRemote) {
			return;
		}

		if (!enabledInitialized) {
			enabledInitialized = true;
			neighborUpdate();
		}

		if (tickable && isEnabled()) {
			if (actionCooldown > 0) {
				actionCooldown--;
			} else {
				executeOnActionItem(ai -> ai.update(item, this));
			}
		}
	}

	public void neighborUpdate() {
		if (powered != world.isBlockPowered(pos)) {
			powered = world.isBlockPowered(pos);

			if (powered) {
				switchOn(BlockPos.ZERO);
			} else {
				switchOff(BlockPos.ZERO);
			}
		}

		updateRedstone();
	}

	public void updateRedstone() {
		executeOnRedstoneItem(ri -> ri.updateRedstone(item, this));
	}

	@Override
	public World getTheWorld() {
		return getWorld();
	}

	@Override
	public BlockPos getBlockPos() {
		return getPos();
	}

	@Override
	public int addToConnectedInventory(ItemStack stack) {
		int numberAdded = 0;
		for (Direction side : Direction.values()) {
			LazyOptional<IItemHandler> inventory = InventoryHelper.getInventoryAtPos(world, pos.add(side.getDirectionVec()), side.getOpposite());

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
	@Nullable
	public FakePlayer getFakePlayer() {
		if (world.isRemote) {
			return null;
		}

		ServerWorld world = (ServerWorld) this.world;

		return XRFakePlayerFactory.get(world);
	}

	@Override
	public void destroyItem() {
		setItem(ItemStack.EMPTY);
	}

	@Override
	public void setItem(ItemStack stack) {
		removeSpecialItems();
		item = stack;
		updateItemsAndBlock();
	}

	@Override
	public List<BlockPos> getPedestalsInRange(int range) {
		return PedestalRegistry.getPositionsInRange(world.getDimensionKey().getRegistryName(), pos, range);
	}

	@Override
	public void switchOn(BlockPos switchedOnFrom) {
		if (switchedOnFrom != BlockPos.ZERO && !onSwitches.contains(switchedOnFrom.toLong())) {
			onSwitches.add(switchedOnFrom.toLong());
		}

		setEnabled(true);

		BlockState blockState = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, blockState, blockState, 3);
	}

	@Override
	public void switchOff(BlockPos switchedOffFrom) {
		if (switchedOffFrom != BlockPos.ZERO) {
			onSwitches.remove(switchedOffFrom.toLong());
		}

		if (!switchedOn && !powered && onSwitches.isEmpty()) {
			setEnabled(false);
		}
		BlockState blockState = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, blockState, blockState, 3);
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

	private void setEnabled(boolean switchedOn) {
		if (world.getBlockState(pos).getBlock() instanceof PedestalBlock) {
			world.setBlockState(pos, world.getBlockState(pos).with(PedestalBlock.ENABLED, switchedOn));
			if (!switchedOn) {
				executeOnActionItem(ai -> ai.stop(item, this));
			}
		}
		markDirty();
	}

	private List<IFluidHandler> getAdjacentTanks() {
		List<IFluidHandler> adjacentTanks = new ArrayList<>();

		for (Direction side : Direction.values()) {
			BlockPos tankPos = getPos().add(side.getDirectionVec());
			Direction tankDirection = side.getOpposite();
			addIfTank(adjacentTanks, tankPos, tankDirection);
		}

		return adjacentTanks;
	}

	private void addIfTank(List<IFluidHandler> adjacentTanks, BlockPos tankPos, Direction tankDirection) {
		WorldHelper.getTile(world, tankPos).ifPresent(te -> te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, tankDirection).ifPresent(adjacentTanks::add));
	}

	public void removeSpecialItems() {
		executeOnRedstoneItem(ri -> ri.onRemoved(item, this));
		executeOnActionItem(ai -> ai.onRemoved(item, this));
	}

	@Override
	public void removeAndSpawnItem() {
		removeSpecialItems();
		resetSpecialItems();
		super.removeAndSpawnItem();
	}

	@Override
	public int getSizeInventory() {
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
	public ItemStack getStackInSlot(int slot) {
		if (slot == 0) {
			return item;
		}

		return applyToItemHandler(ih -> ih.getStackInSlot(getInternalItemHandlerSlot(slot))).orElse(ItemStack.EMPTY);
	}

	private int getInternalItemHandlerSlot(int slot) {
		return slot - 1;
	}

	@Override
	public ItemStack decrStackSize(int slot, int count) {
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
				removeSpecialItems();
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
		updateRedstone();
		BlockState blockState = world.getBlockState(getPos());
		world.notifyBlockUpdate(getPos(), blockState, blockState, 3);
	}

	@Override
	public ItemStack removeStackFromSlot(int slot) {
		if (slot == 0) {
			ItemStack stack;
			stack = item;
			removeSpecialItems();
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
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if (slot == 0) {
			setItem(stack);
			return;
		}

		runOnItemHandler(ih -> setItemHandlerSlotContents(slot, stack, ih));
	}

	private void setItemHandlerSlotContents(int slot, ItemStack stack, IItemHandler ih) {
		int adjustedSlot = getInternalItemHandlerSlot(slot);
		ItemStack stackInSlot = ih.getStackInSlot(adjustedSlot);

		if (!stackInSlot.isEmpty() && !stack.isEmpty() && !StackHelper.isItemAndNbtEqual(stack, stackInSlot)) {
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
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		if (super.isItemValidForSlot(index, stack)) {
			return true;
		}

		return applyToItemHandler(ih -> {
			ItemStack returnedStack = ih.insertItem(getInternalItemHandlerSlot(index), stack, true);
			return returnedStack.isEmpty() || returnedStack.getCount() != stack.getCount();
		}).orElse(false);
	}

	public void toggleSwitch() {
		switchedOn = !switchedOn;

		if (switchedOn) {
			switchOn(BlockPos.ZERO);
		} else {
			switchOff(BlockPos.ZERO);
		}

		updateRedstone();
	}

	ItemStack getFluidContainer() {
		return fluidContainer;
	}

	public boolean isEnabled() {
		return getBlockState().get(PedestalBlock.ENABLED);
	}
}