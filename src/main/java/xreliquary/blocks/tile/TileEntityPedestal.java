package xreliquary.blocks.tile;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
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
import xreliquary.api.*;
import xreliquary.blocks.BlockPedestal;
import xreliquary.init.ModBlocks;
import xreliquary.items.util.FilteredItemStackHandler;
import xreliquary.util.InventoryHelper;
import xreliquary.util.StackHelper;
import xreliquary.util.XRFakePlayerFactory;
import xreliquary.util.pedestal.PedestalRegistry;

import java.util.*;

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

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);

		compound.setBoolean("SwitchedOn", switchedOn);
		compound.setBoolean("Powered", powered);

		NBTTagList onLocations = new NBTTagList();

		for(int i = 0; i < onSwitches.size(); i++) {
			onLocations.appendTag(new NBTTagLong(onSwitches.get(i)));
		}
		compound.setTag("OnSwitches", onLocations);

		return compound;
	}

	@Override
	public void markDirty() {
		for(IItemHandler itemHandler : itemHandlers.values()) {
			if(itemHandler instanceof FilteredItemStackHandler) {
				FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;

				filteredHandler.markDirty();
			}
		}

		super.markDirty();
	}

	@Override
	public void onChunkUnload() {
		if(!this.worldObj.isRemote)
			PedestalRegistry.unregisterPosition(this.worldObj.provider.getDimension(), this.pos);

		super.onChunkUnload();
	}

	@Override
	public void onLoad() {
		if(!this.worldObj.isRemote)
			PedestalRegistry.registerPosition(this.worldObj.provider.getDimension(), this.pos);

		super.onLoad();
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			if(pedestalFluidHandler == null) {
				pedestalFluidHandler = new PedestalFluidHandler(this);
			}
			return (T) pedestalFluidHandler;
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

		for(int i = 0; i < inventory.length; i++) {
			ItemStack item = inventory[i];
			if(item == null)
				continue;

			if(item.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
				itemHandlers.put(i, item.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null));
			}

			if(item.getItem() instanceof IPedestalActionItem) {
				tickable = true;
				actionItems.put(i, (IPedestalActionItem) item.getItem());
			} else if(item.getItem() instanceof IPedestalRedstoneItem) {
				redstoneItems.put(i, (IPedestalRedstoneItem) item.getItem());
			} else {
				IPedestalItemWrapper wrapper = PedestalRegistry.getItemWrapper(item);
				if(wrapper != null) {
					if(wrapper instanceof IPedestalActionItemWrapper) {
						tickable = true;
						itemWrappers.put(i, (IPedestalActionItemWrapper) wrapper);
					}
					if(wrapper instanceof IPedestalRedstoneItemWrapper) {
						redstoneItems.put(i, (IPedestalRedstoneItem) wrapper);
					}
				}
			}

			if(item.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
				fluidContainers.add(item);
			}
		}

		actionCooldowns = new int[inventory.length];
		Arrays.fill(actionCooldowns, 0);
	}

	@Override
	public void update() {
		if(worldObj.isRemote)
			return;

		IBlockState blockState = worldObj.getBlockState(this.pos);
		if (blockState.getBlock() != ModBlocks.pedestal)
			return;

		if(!enabledInitialized) {
			enabledInitialized = true;
			neighborUpdate();
		}

		if(tickable && blockState.getValue(BlockPedestal.ENABLED)) {
			for(currentItemIndex = 0; currentItemIndex < inventory.length; currentItemIndex++) {
				if(actionCooldowns[currentItemIndex] > 0) {
					actionCooldowns[currentItemIndex]--;
				} else {
					ItemStack item = inventory[currentItemIndex];
					//TODO refactor to just one actionItems list
					if(actionItems.containsKey(currentItemIndex)) {
						actionItems.get(currentItemIndex).update(item, this);
					} else if(itemWrappers.containsKey(currentItemIndex)) {
						itemWrappers.get(currentItemIndex).update(inventory[currentItemIndex], this);
					}
				}
			}
		}
	}

	public void neighborUpdate() {
		if(powered != worldObj.isBlockPowered(this.pos)) {
			powered = worldObj.isBlockPowered(this.pos);

			if(powered)
				switchOn(null);
			else
				switchOff(null);
		}

		updateRedstone();
	}

	public void updateRedstone() {
		for(int i = 0; i < inventory.length; i++) {
			if(redstoneItems.containsKey(i)) {
				redstoneItems.get(i).updateRedstone(inventory[i], this);
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
	public int addToConnectedInventory(ItemStack stack) {
		List<IInventory> adjacentInventories = getAdjacentInventories();

		int numberAdded = 0;
		for(EnumFacing facing : EnumFacing.VALUES) {
			IInventory inventory = getInventoryAtPos(this.getPos().add(facing.getDirectionVec()));

			if(inventory != null) {
				numberAdded += InventoryHelper.tryToAddToInventory(stack, inventory, stack.stackSize - numberAdded, facing.getOpposite());

				if(numberAdded >= stack.stackSize)
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
		if(this.worldObj.isRemote)
			return null;

		WorldServer world = (WorldServer) worldObj;

		return XRFakePlayerFactory.get(world);
	}

	@Override
	public void destroyCurrentItem() {
		this.setInventorySlotContents(currentItemIndex, null);
	}

	@Override
	public void replaceCurrentItem(ItemStack stack) {
		this.setInventorySlotContents(currentItemIndex, stack);
	}

	@Override
	public List<BlockPos> getPedestalsInRange(int range) {
		return PedestalRegistry.getPositionsInRange(this.worldObj.provider.getDimension(), this.pos, range);
	}

	@Override
	public void switchOn(BlockPos switchedOnFrom) {
		if(switchedOnFrom != null && !onSwitches.contains(switchedOnFrom.toLong()))
			onSwitches.add(switchedOnFrom.toLong());

		setEnabled(true);

		IBlockState blockState = worldObj.getBlockState(pos);
		worldObj.notifyBlockUpdate(pos, blockState, blockState, 3);
	}

	@Override
	public void switchOff(BlockPos switchedOffFrom) {
		if(switchedOffFrom != null)
			onSwitches.remove(switchedOffFrom.toLong());

		if(!switchedOn && !powered && onSwitches.size() == 0) {
			setEnabled(false);
		}
		IBlockState blockState = worldObj.getBlockState(pos);
		worldObj.notifyBlockUpdate(pos, blockState, blockState, 3);
	}

	@Override
	public int getCurrentItemIndex() {
		return currentItemIndex;
	}

	@Override
	public Object getItemData(int index) {
		if (itemData.size() <= index)
			return null;

		return itemData.get(index);
	}

	@Override
	public void setItemData(int index, Object data) {
		while(itemData.size() < index)
			itemData.add(null);

		if (itemData.size() == index)
			itemData.add(data);
		else
			itemData.set(index, data);
	}

	private void setEnabled(boolean switchedOn) {
		ModBlocks.pedestal.setEnabled(worldObj, pos, switchedOn);
		if (!switchedOn)
		for(currentItemIndex = 0; currentItemIndex<inventory.length; currentItemIndex++) {
			if(actionItems.containsKey(currentItemIndex)) {
				actionItems.get(currentItemIndex).stop(inventory[currentItemIndex], this);
			} else if (itemWrappers.containsKey(currentItemIndex)) {
				itemWrappers.get(currentItemIndex).stop(inventory[currentItemIndex], this);
			}
		}
	}

	public List<IInventory> getAdjacentInventories() {
		BlockPos south = this.getPos().add(EnumFacing.SOUTH.getDirectionVec());
		BlockPos north = this.getPos().add(EnumFacing.NORTH.getDirectionVec());
		BlockPos east = this.getPos().add(EnumFacing.EAST.getDirectionVec());
		BlockPos west = this.getPos().add(EnumFacing.WEST.getDirectionVec());

		List<IInventory> adjacentInventories = new ArrayList<>();

		IInventory inventory = getInventoryAtPos(south);
		if(inventory != null)
			adjacentInventories.add(inventory);

		inventory = getInventoryAtPos(north);
		if(inventory != null)
			adjacentInventories.add(inventory);

		inventory = getInventoryAtPos(east);
		if(inventory != null)
			adjacentInventories.add(inventory);

		inventory = getInventoryAtPos(west);
		if(inventory != null)
			adjacentInventories.add(inventory);

		return adjacentInventories;
	}

	private IInventory getInventoryAtPos(BlockPos pos) {
		if(worldObj.getTileEntity(pos) instanceof IInventory) {
			IInventory inventory = (IInventory) worldObj.getTileEntity(pos);
			Block block = worldObj.getBlockState(pos).getBlock();

			if (inventory instanceof TileEntityChest && block instanceof BlockChest)
			{
				inventory = ((BlockChest)block).getContainer(worldObj, pos, true);
			}

			return inventory;
		}
		return null;
	}

	public List<IFluidHandler> getAdjacentTanks() {
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
		if(worldObj.getTileEntity(pos) == null)
			return null;

		if(worldObj.getTileEntity(pos).hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing))
			return worldObj.getTileEntity(pos).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing);
		return null;
	}

	public void removeItemsFromLists() {
		for(Map.Entry<Integer, IPedestalRedstoneItem> item : redstoneItems.entrySet()) {
			item.getValue().onRemoved(inventory[item.getKey()], this);
		}

		for(Map.Entry<Integer, IPedestalActionItem> item : actionItems.entrySet()) {
			item.getValue().onRemoved(inventory[item.getKey()], this);
		}

		for(Map.Entry<Integer, IPedestalActionItemWrapper> item : itemWrappers.entrySet()) {
			item.getValue().onRemoved(inventory[item.getKey()], this);
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

	@Override
	public ItemStack getStackInSlot(int slot) {
		if(slot < slots)
			return inventory[slot];

		int adjustedSlot = slot - slots;

		Tuple<IItemHandler, Integer> handlerSlot = getHandlerSlot(adjustedSlot);

		return handlerSlot == null ? null : handlerSlot.getFirst().getStackInSlot(handlerSlot.getSecond());
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
		if(this.inventory[slot] != null) {
			ItemStack stack;

			if(this.inventory[slot].stackSize > count) {
				stack = this.inventory[slot].splitStack(count);
			} else {
				stack = this.inventory[slot];

				if(redstoneItems.containsKey(slot)) {
					redstoneItems.get(slot).onRemoved(inventory[slot], this);
				} else if (actionItems.containsKey(slot)) {
					actionItems.get(slot).onRemoved(inventory[slot], this);
				} else if (itemWrappers.containsKey(slot)) {
					itemWrappers.get(slot).onRemoved(inventory[slot], this);
				}

				this.inventory[slot] = null;

				updateItemsAndBlock();
			}

			return stack;
		} else {
			return null;
		}
	}

	private void updateItemsAndBlock() {
		updateSpecialItems();
		updateRedstone();
		IBlockState blockState = worldObj.getBlockState(getPos());
		worldObj.notifyBlockUpdate(getPos(), blockState, blockState, 3);
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return !(oldState.getBlock() == ModBlocks.pedestal && newState.getBlock() == ModBlocks.pedestal);
	}

	@Override
	public ItemStack removeStackFromSlot(int slot) {
		ItemStack stack;
		if(slot < slots) {
			stack = this.inventory[slot];

			if(redstoneItems.containsKey(slot)) {
				redstoneItems.get(slot).onRemoved(inventory[slot], this);
			} else if (actionItems.containsKey(slot)) {
				actionItems.get(slot).onRemoved(inventory[slot], this);
			} else if (itemWrappers.containsKey(slot)) {
				itemWrappers.get(slot).onRemoved(inventory[slot], this);
			}

			this.inventory[slot] = null;

			updateItemsAndBlock();

			return stack;
		}

		int adjustedSlot = slot - slots;

		Tuple<IItemHandler, Integer> handlerSlot = getHandlerSlot(adjustedSlot);

		stack = handlerSlot.getFirst().getStackInSlot(handlerSlot.getSecond());

		return handlerSlot.getFirst().extractItem(handlerSlot.getSecond(), stack.stackSize, false);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if(slot < slots) {
			IPedestalRedstoneItem removedRedstoneItem = null;
			IPedestalActionItem removedActionItem = null;
			if(stack == null) {
				if (redstoneItems.containsKey(slot)) {
					removedRedstoneItem = redstoneItems.get(slot);
				} else if (actionItems.containsKey(slot)) {
					removedActionItem = actionItems.get(slot);
				} else if (itemWrappers.containsKey(slot)) {
					removedActionItem = itemWrappers.get(slot);
				}
			}

			this.inventory[slot] = stack;
			if(stack != null && stack.stackSize > 1) {
				stack.stackSize = 1;
			}

			updateItemsAndBlock();

			if(removedRedstoneItem != null)
				removedRedstoneItem.onRemoved(inventory[slot], this);

			if(removedActionItem != null)
				removedActionItem.onRemoved(inventory[slot], this);

			return;
		}

		int adjustedSlot = slot - slots;

		Tuple<IItemHandler, Integer> handlerSlot = getHandlerSlot(adjustedSlot);

		IItemHandler itemHandler = handlerSlot.getFirst();
		adjustedSlot = handlerSlot.getSecond();

		ItemStack stackInSlot = itemHandler.getStackInSlot(adjustedSlot);

		if(stackInSlot != null && stack != null && !StackHelper.isItemAndNbtEqual(stack, stackInSlot))
			return;

		if(stackInSlot != null && (stack == null || stack.stackSize < stackInSlot.stackSize)) {
			int amount = stackInSlot.stackSize - (stack == null ? 0 : stack.stackSize);
			itemHandler.extractItem(adjustedSlot, amount, false);
		} else if(stack != null && (stackInSlot == null || stack.stackSize > stackInSlot.stackSize)) {
			stack.stackSize = stack.stackSize - (stackInSlot == null ? 0 : stackInSlot.stackSize);
			itemHandler.insertItem(adjustedSlot, stack, false);
		}
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		if(super.isItemValidForSlot(index, stack))
			return true;

		int adjustedSlot = index - slots;

		Tuple<IItemHandler, Integer> handlerSlot = getHandlerSlot(adjustedSlot);

		IItemHandler itemHandler = handlerSlot.getFirst();
		adjustedSlot = handlerSlot.getSecond();

		ItemStack returnedStack = itemHandler.insertItem(adjustedSlot, stack, true);

		return returnedStack == null || returnedStack.stackSize != stack.stackSize;
	}

	public void toggleSwitch() {
		switchedOn = !switchedOn;

		if(switchedOn)
			switchOn(null);
		else
			switchOff(null);

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

	public List<ItemStack> getFluidContainers() {
		return fluidContainers;
	}
}