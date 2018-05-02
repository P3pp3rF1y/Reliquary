package xreliquary.blocks.tile;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import xreliquary.api.IPedestal;
import xreliquary.api.IPedestalActionItem;
import xreliquary.api.IPedestalActionItemWrapper;
import xreliquary.api.IPedestalItemWrapper;
import xreliquary.api.IPedestalRedstoneItem;
import xreliquary.api.IPedestalRedstoneItemWrapper;
import xreliquary.blocks.BlockPedestal;
import xreliquary.init.ModBlocks;
import xreliquary.items.util.FilteredItemStackHandler;
import xreliquary.pedestal.PedestalRegistry;
import xreliquary.util.InventoryHelper;
import xreliquary.util.StackHelper;
import xreliquary.util.XRFakePlayerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TileEntityPedestal extends TileEntityPedestalPassive implements IPedestal, ITickable {

	private boolean tickable = false;
	private int[] actionCooldowns = new int[0];
	private int currentItemIndex;
	private Map<Integer, IPedestalActionItem> actionItems = new HashMap<>();
	private Map<Integer, IPedestalActionItemWrapper> itemWrappers = new HashMap<>();
	private Map<Integer, IPedestalRedstoneItem> redstoneItems = new HashMap<>();
	private Map<Integer, IItemHandler> itemHandlers = new HashMap<>();
	private List<ItemStack> fluidContainers = new ArrayList<>();
	private boolean switchedOn = false;
	private List<Long> onSwitches = new ArrayList<>();
	private boolean enabledInitialized = false;
	private boolean powered = false;
	private PedestalFluidHandler pedestalFluidHandler = null;
	private List<Object> itemData = new ArrayList<>();
	private IItemHandler inventoryWrapper = new InvWrapper(this);

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		switchedOn = tag.getBoolean("SwitchedOn");
		powered = tag.getBoolean("Powered");

		NBTTagList onLocations = tag.getTagList("OnSwitches", 4);

		onSwitches.clear();

		for(int i = 0; i < onLocations.tagCount(); i++) {
			onSwitches.add(((NBTTagLong) onLocations.get(i)).getLong());
		}

		updateSpecialItems();
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);

		compound.setBoolean("SwitchedOn", switchedOn);
		compound.setBoolean("Powered", powered);

		NBTTagList onLocations = new NBTTagList();

		for(Long onSwitch : onSwitches) {
			onLocations.appendTag(new NBTTagLong(onSwitch));
		}
		compound.setTag("OnSwitches", onLocations);

		return compound;
	}

	@Override
	public void markDirty() {
		itemHandlers.values().stream().filter(itemHandler -> itemHandler instanceof FilteredItemStackHandler).forEach(itemHandler -> {
			FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;

			filteredHandler.markDirty();
		});

		super.markDirty();
	}

	@Override
	public void onChunkUnload() {
		if(!this.world.isRemote)
			PedestalRegistry.unregisterPosition(this.world.provider.getDimension(), this.pos);

		super.onChunkUnload();
	}

	@Override
	public void onLoad() {
		if(!this.world.isRemote)
			PedestalRegistry.registerPosition(this.world.provider.getDimension(), this.pos);

		super.onLoad();
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			if(pedestalFluidHandler == null) {
				pedestalFluidHandler = new PedestalFluidHandler(this);
			}
			//noinspection unchecked
			return (T) pedestalFluidHandler;
		} else if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) inventoryWrapper;
		}

		return super.getCapability(capability, facing);
	}

	private void updateSpecialItems() {
		tickable = false;
		fluidContainers.clear();
		itemWrappers.clear();
		actionItems.clear();
		redstoneItems.clear();
		itemHandlers.clear();

		for(int slot = 0; slot < inventory.size(); slot++) {
			ItemStack item = inventory.get(slot);
			if(item.isEmpty())
				continue;

			if(item.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
				itemHandlers.put(slot, item.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null));
			}

			if(item.getItem() instanceof IPedestalActionItem) {
				tickable = true;
				actionItems.put(slot, (IPedestalActionItem) item.getItem());
			} else if(item.getItem() instanceof IPedestalRedstoneItem) {
				redstoneItems.put(slot, (IPedestalRedstoneItem) item.getItem());
			} else {
				IPedestalItemWrapper wrapper = PedestalRegistry.getItemWrapper(item);
				if(wrapper != null) {
					if(wrapper instanceof IPedestalActionItemWrapper) {
						tickable = true;
						itemWrappers.put(slot, (IPedestalActionItemWrapper) wrapper);
					}
					if(wrapper instanceof IPedestalRedstoneItemWrapper) {
						redstoneItems.put(slot, (IPedestalRedstoneItem) wrapper);
					}
				}
			}

			if(item.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) || item.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
				fluidContainers.add(item);
			}
		}

		actionCooldowns = new int[inventory.size()];
		Arrays.fill(actionCooldowns, 0);
	}

	@Override
	public void update() {
		if(world.isRemote)
			return;

		IBlockState blockState = world.getBlockState(this.pos);
		if(blockState.getBlock() != ModBlocks.pedestal)
			return;

		if(!enabledInitialized) {
			enabledInitialized = true;
			neighborUpdate();
		}

		if(tickable && blockState.getValue(BlockPedestal.ENABLED)) {
			for(currentItemIndex = 0; currentItemIndex < inventory.size(); currentItemIndex++) {
				if(actionCooldowns[currentItemIndex] > 0) {
					actionCooldowns[currentItemIndex]--;
				} else {
					ItemStack item = inventory.get(currentItemIndex);
					//TODO refactor to just one actionItems list
					if(actionItems.containsKey(currentItemIndex)) {
						actionItems.get(currentItemIndex).update(item, this);
					} else if(itemWrappers.containsKey(currentItemIndex)) {
						itemWrappers.get(currentItemIndex).update(inventory.get(currentItemIndex), this);
					}
				}
			}
		}
	}

	public void neighborUpdate() {
		if(powered != world.isBlockPowered(this.pos)) {
			powered = world.isBlockPowered(this.pos);

			if(powered)
				switchOn(null);
			else
				switchOff(null);
		}

		updateRedstone();
	}

	public void updateRedstone() {
		for(int i = 0; i < inventory.size(); i++) {
			if(redstoneItems.containsKey(i)) {
				redstoneItems.get(i).updateRedstone(inventory.get(i), this);
			}
		}
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
	public int addToConnectedInventory(@Nonnull ItemStack stack) {
		int numberAdded = 0;
		for(EnumFacing facing : EnumFacing.VALUES) {
			IInventory inventory = getInventoryAtPos(this.getPos().add(facing.getDirectionVec()));

			if(inventory != null) {
				numberAdded += InventoryHelper.tryToAddToInventory(stack, inventory, stack.getCount() - numberAdded, facing.getOpposite());

				if(numberAdded >= stack.getCount())
					break;
			}
		}

		return numberAdded;
	}

	@Override
	public int fillConnectedTank(FluidStack fluidStack, boolean doFill) {
		List<IFluidHandler> adjacentTanks = getAdjacentTanks();

		int fluidFilled = 0;
		FluidStack copy = fluidStack.copy();

		for(IFluidHandler tank : adjacentTanks) {
			if(tank.fill(copy, false) == copy.amount) {
				fluidFilled += tank.fill(copy, doFill);

				if(fluidFilled >= fluidStack.amount) {
					break;
				} else {
					copy.amount = fluidStack.amount - fluidFilled;
				}
			}
		}

		return fluidFilled;
	}

	@Override
	public int fillConnectedTank(FluidStack fluidStack) {
		return fillConnectedTank(fluidStack, true);
	}

	@Override
	public void setActionCoolDown(int coolDownTicks) {
		//prevent derpy items from updating cooldown after the item gets destroyed by its use
		if(currentItemIndex < actionCooldowns.length)
			actionCooldowns[currentItemIndex] = coolDownTicks;
	}

	@Override
	public FakePlayer getFakePlayer() {
		if(this.world.isRemote)
			return null;

		WorldServer world = (WorldServer) this.world;

		return XRFakePlayerFactory.get(world);
	}

	@Override
	public void destroyCurrentItem() {
		this.setInventorySlotContents(currentItemIndex, ItemStack.EMPTY);
	}

	@Override
	public void replaceCurrentItem(@Nonnull ItemStack stack) {
		this.setInventorySlotContents(currentItemIndex, stack);
	}

	@Override
	public List<BlockPos> getPedestalsInRange(int range) {
		return PedestalRegistry.getPositionsInRange(this.world.provider.getDimension(), this.pos, range);
	}

	@Override
	public void switchOn(BlockPos switchedOnFrom) {
		if(switchedOnFrom != null && !onSwitches.contains(switchedOnFrom.toLong()))
			onSwitches.add(switchedOnFrom.toLong());

		setEnabled(true);

		IBlockState blockState = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, blockState, blockState, 3);
	}

	@Override
	public void switchOff(BlockPos switchedOffFrom) {
		if(switchedOffFrom != null)
			onSwitches.remove(switchedOffFrom.toLong());

		if(!switchedOn && !powered && onSwitches.size() == 0) {
			setEnabled(false);
		}
		IBlockState blockState = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, blockState, blockState, 3);
	}

	@Override
	public int getCurrentItemIndex() {
		return currentItemIndex;
	}

	@Override
	public Object getItemData(int index) {
		if(itemData.size() <= index)
			return null;

		return itemData.get(index);
	}

	@Override
	public void setItemData(int index, Object data) {
		while(itemData.size() < index)
			itemData.add(null);

		if(itemData.size() == index)
			itemData.add(data);
		else
			itemData.set(index, data);
	}

	@Override
	public boolean switchedOn() {
		return switchedOn;
	}

	private void setEnabled(boolean switchedOn) {
		ModBlocks.pedestal.setEnabled(world, pos, switchedOn);
		if(!switchedOn)
			for(currentItemIndex = 0; currentItemIndex < inventory.size(); currentItemIndex++) {
				if(actionItems.containsKey(currentItemIndex)) {
					actionItems.get(currentItemIndex).stop(inventory.get(currentItemIndex), this);
				} else if(itemWrappers.containsKey(currentItemIndex)) {
					itemWrappers.get(currentItemIndex).stop(inventory.get(currentItemIndex), this);
				}
			}
	}

	private IInventory getInventoryAtPos(BlockPos pos) {
		if(world.getTileEntity(pos) instanceof IInventory) {
			IInventory inventory = (IInventory) world.getTileEntity(pos);
			Block block = world.getBlockState(pos).getBlock();

			if(inventory instanceof TileEntityChest && block instanceof BlockChest) {
				inventory = ((BlockChest) block).getContainer(world, pos, true);
			}

			return inventory;
		}
		return null;
	}

	private List<IFluidHandler> getAdjacentTanks() {
		List<IFluidHandler> adjacentTanks = new ArrayList<>();

		for(EnumFacing facing : EnumFacing.values()) {
			BlockPos tankPos = this.getPos().add(facing.getDirectionVec());

			IFluidHandler tank = getTankAtPos(tankPos, facing.getOpposite());
			if(tank != null)
				adjacentTanks.add(tank);
		}

		return adjacentTanks;
	}

	private IFluidHandler getTankAtPos(BlockPos pos, EnumFacing facing) {
		TileEntity te = world.getTileEntity(pos);
		if(te == null)
			return null;

		if(te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing))
			return te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing);
		return null;
	}

	public void removeItemsFromLists() {
		for(Map.Entry<Integer, IPedestalRedstoneItem> item : redstoneItems.entrySet()) {
			item.getValue().onRemoved(inventory.get(item.getKey()), this);
		}

		for(Map.Entry<Integer, IPedestalActionItem> item : actionItems.entrySet()) {
			item.getValue().onRemoved(inventory.get(item.getKey()), this);
		}

		for(Map.Entry<Integer, IPedestalActionItemWrapper> item : itemWrappers.entrySet()) {
			item.getValue().onRemoved(inventory.get(item.getKey()), this);
		}
	}

	// IInventory
	@Override
	public int getSizeInventory() {
		int itemHandlerSlots = 0;
		for(IItemHandler itemHandler : itemHandlers.values()) {
			itemHandlerSlots += itemHandler.getSlots();
		}

		return slots + itemHandlerSlots;
	}

	@Nonnull
	@Override
	public ItemStack getStackInSlot(int slot) {
		if(slot < slots)
			return inventory.get(slot);

		int adjustedSlot = slot - slots;

		Tuple<IItemHandler, Integer> handlerSlot = getHandlerSlot(adjustedSlot);

		return handlerSlot == null ? ItemStack.EMPTY : handlerSlot.getFirst().getStackInSlot(handlerSlot.getSecond());
	}

	private Tuple<IItemHandler, Integer> getHandlerSlot(int origSlot) {
		Tuple<IItemHandler, Integer> handlerSlot = null;
		int adjustedSlot = origSlot;
		for(IItemHandler itemHandler : itemHandlers.values()) {
			if(adjustedSlot < itemHandler.getSlots()) {
				handlerSlot = new Tuple<>(itemHandler, adjustedSlot);
				break;
			}
			adjustedSlot -= itemHandler.getSlots();
		}
		return handlerSlot;
	}

	@Nonnull
	@Override
	public ItemStack decrStackSize(int slot, int count) {
		if(slot < slots) {
			return decrStackInInventory(slot, count);
		}

		int adjustedSlot = slot - slots;

		Tuple<IItemHandler, Integer> handlerSlot = getHandlerSlot(adjustedSlot);

		return handlerSlot.getFirst().extractItem(handlerSlot.getSecond(), count, false);
	}

	private ItemStack decrStackInInventory(int slot, int count) {
		if(!this.inventory.get(slot).isEmpty()) {
			ItemStack stack;

			if(inventory.get(slot).getCount() > count) {
				stack = this.inventory.get(slot).splitStack(count);
			} else {
				stack = this.inventory.get(slot);

				if(redstoneItems.containsKey(slot)) {
					redstoneItems.get(slot).onRemoved(inventory.get(slot), this);
				} else if(actionItems.containsKey(slot)) {
					actionItems.get(slot).onRemoved(inventory.get(slot), this);
				} else if(itemWrappers.containsKey(slot)) {
					itemWrappers.get(slot).onRemoved(inventory.get(slot), this);
				}

				this.inventory.set(slot, ItemStack.EMPTY);

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
		IBlockState blockState = world.getBlockState(getPos());
		world.notifyBlockUpdate(getPos(), blockState, blockState, 3);
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, @Nonnull IBlockState oldState, @Nonnull IBlockState newState) {
		return !(oldState.getBlock() == ModBlocks.pedestal && newState.getBlock() == ModBlocks.pedestal);
	}

	@Nonnull
	@Override
	public ItemStack removeStackFromSlot(int slot) {
		ItemStack stack;
		if(slot < slots) {
			stack = this.inventory.get(slot);

			if(redstoneItems.containsKey(slot)) {
				redstoneItems.get(slot).onRemoved(inventory.get(slot), this);
			} else if(actionItems.containsKey(slot)) {
				actionItems.get(slot).onRemoved(inventory.get(slot), this);
			} else if(itemWrappers.containsKey(slot)) {
				itemWrappers.get(slot).onRemoved(inventory.get(slot), this);
			}

			this.inventory.set(slot, ItemStack.EMPTY);

			updateItemsAndBlock();

			return stack;
		}

		int adjustedSlot = slot - slots;

		Tuple<IItemHandler, Integer> handlerSlot = getHandlerSlot(adjustedSlot);

		stack = handlerSlot.getFirst().getStackInSlot(handlerSlot.getSecond());

		return handlerSlot.getFirst().extractItem(handlerSlot.getSecond(), stack.getCount(), false);
	}

	@Override
	public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
		if(slot < slots) {
			IPedestalRedstoneItem removedRedstoneItem = null;
			IPedestalActionItem removedActionItem = null;
			if(stack.isEmpty()) {
				if(redstoneItems.containsKey(slot)) {
					removedRedstoneItem = redstoneItems.get(slot);
				} else if(actionItems.containsKey(slot)) {
					removedActionItem = actionItems.get(slot);
				} else if(itemWrappers.containsKey(slot)) {
					removedActionItem = itemWrappers.get(slot);
				}
			}

			this.inventory.set(slot, stack);

			updateItemsAndBlock();

			if(removedRedstoneItem != null)
				removedRedstoneItem.onRemoved(inventory.get(slot), this);

			if(removedActionItem != null)
				removedActionItem.onRemoved(inventory.get(slot), this);

			return;
		}

		int adjustedSlot = slot - slots;

		Tuple<IItemHandler, Integer> handlerSlot = getHandlerSlot(adjustedSlot);

		IItemHandler itemHandler = handlerSlot.getFirst();
		adjustedSlot = handlerSlot.getSecond();

		ItemStack stackInSlot = itemHandler.getStackInSlot(adjustedSlot);

		if(!stackInSlot.isEmpty() && !stack.isEmpty() && !StackHelper.isItemAndNbtEqual(stack, stackInSlot))
			return;

		if(!stackInSlot.isEmpty() && (stack.isEmpty() || stack.getCount() < stackInSlot.getCount())) {
			int amount = stackInSlot.getCount() - (stack.isEmpty() ? 0 : stack.getCount());
			itemHandler.extractItem(adjustedSlot, amount, false);
		} else if(!stack.isEmpty() && (stackInSlot.isEmpty() || stack.getCount() > stackInSlot.getCount())) {
			stack.shrink(stackInSlot.isEmpty() ? 0 : stackInSlot.getCount());
			itemHandler.insertItem(adjustedSlot, stack, false);
		}
	}

	@Override
	public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
		if(super.isItemValidForSlot(index, stack))
			return true;

		int adjustedSlot = index - slots;

		Tuple<IItemHandler, Integer> handlerSlot = getHandlerSlot(adjustedSlot);

		IItemHandler itemHandler = handlerSlot.getFirst();
		adjustedSlot = handlerSlot.getSecond();

		ItemStack returnedStack = itemHandler.insertItem(adjustedSlot, stack, true);

		return returnedStack.isEmpty() || returnedStack.getCount() != stack.getCount();
	}

	public void toggleSwitch() {
		switchedOn = !switchedOn;

		if(switchedOn)
			switchOn(null);
		else
			switchOff(null);

		updateRedstone();
	}

	public boolean isPowered() {
		return powered;
	}

	public List<Long> getOnSwitches() {
		return onSwitches;
	}

	public boolean isSwitchedOn() {
		return switchedOn;
	}

	List<ItemStack> getFluidContainers() {
		return fluidContainers;
	}
}
