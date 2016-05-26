package xreliquary.blocks.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.*;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import xreliquary.api.*;
import xreliquary.items.util.FilteredItemStackHandler;
import xreliquary.util.InventoryHelper;
import xreliquary.util.StackHelper;
import xreliquary.util.XRFakePlayerFactory;
import xreliquary.util.pedestal.PedestalRegistry;

import java.util.*;

public class TileEntityPedestal extends TileEntityBase implements IPedestal, IFluidHandler, IInventory {

	private boolean tickable = false;
	private int[] actionCooldowns = new int[0];
	private int currentItemIndex;
	private Map<Integer, IPedestalActionItem> actionItems = new HashMap<>();
	private Map<Integer, IPedestalActionItemWrapper> itemWrappers = new HashMap<>();
	private Map<Integer, IPedestalRedstoneItem> redstoneItems = new HashMap<>();
	private Map<Integer, IItemHandler> itemHandlers = new HashMap<>();
	private List<ItemStack> fluidContainers = new ArrayList<>();
	private FakePlayer fakePlayer = null;
	private boolean switchedOn = false;
	private List<Long> onSwitches = new ArrayList<>();
	private boolean initRedstone = false;

	private short slots = 0;
	private ItemStack[] inventory;

	public TileEntityPedestal() {
		this.slots = 1;
		inventory = new ItemStack[this.slots];
	}

	public void dropPedestalInventory() {
		for(int i = 0; i < inventory.length; ++i) {
			ItemStack itemstack = inventory[i];

			if(itemstack != null) {
				InventoryHelper.spawnItemStack(this.worldObj, this.pos.getX(), this.pos.getY(), this.pos.getZ(), itemstack);
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		NBTTagList items = tag.getTagList("Items", 10);

		this.inventory = new ItemStack[this.getSizeInventory()];

		for(int i = 0; i < items.tagCount(); ++i) {
			NBTTagCompound item = items.getCompoundTagAt(i);
			byte b0 = item.getByte("Slot");

			if(b0 >= 0 && b0 < this.inventory.length) {
				this.inventory[b0] = ItemStack.loadItemStackFromNBT(item);
			}
		}

		updateSpecialItems();
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		NBTTagList items = new NBTTagList();

		for(int i = 0; i < this.inventory.length; ++i) {
			if(this.inventory[i] != null) {
				NBTTagCompound item = new NBTTagCompound();
				this.inventory[i].writeToNBT(item);
				item.setByte("Slot", (byte) i);
				items.appendTag(item);
			}
		}
		tag.setTag("Items", items);
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

			if(item.getItem() instanceof IFluidContainerItem) {
				fluidContainers.add(item);
				//TODO code to check that all have the same fluid (Due to fluidhandler interface drain method that doesn't specify type of fluid to drain
				// maybe it's not an issue as the method description in interface says it's not fluid sensitive
			}
		}

		actionCooldowns = new int[inventory.length];
		Arrays.fill(actionCooldowns, 0);
	}

	@Override
	public void update() {
		if(worldObj.isRemote)
			return;

		if(!initRedstone) {
			initRedstone = true;
			updateRedstone();
		}

		if((isPowered() || switchedOn) && tickable) {
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

	public void updateRedstone() {
		for(currentItemIndex = 0; currentItemIndex < inventory.length; currentItemIndex++) {
			if(redstoneItems.containsKey(currentItemIndex)) {
				redstoneItems.get(currentItemIndex).updateRedstone(inventory[currentItemIndex], this);
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

		int sizeAdded = 0;
		for(IInventory inventory : adjacentInventories) {
			sizeAdded += InventoryHelper.tryToAddToInventory(stack, inventory, 0, stack.stackSize - sizeAdded);

			if(sizeAdded >= stack.stackSize)
				break;
		}

		return sizeAdded;
	}

	@Override
	public int fillConnectedTank(FluidStack fluidStack, boolean doFill) {
		List<IFluidHandler> adjacentTanks = getAdjacentTanks();

		int fluidFilled = 0;
		FluidStack copy = fluidStack.copy();

		for(IFluidHandler tank : adjacentTanks) {
			if(tank.fill(EnumFacing.UP, copy, false) == copy.amount) {
				fluidFilled += tank.fill(EnumFacing.UP, copy, doFill);

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
		if(!onSwitches.contains(switchedOnFrom.toLong()))
			onSwitches.add(switchedOnFrom.toLong());

		this.switchedOn = true;
	}

	@Override
	public void switchOff(BlockPos switchedOffFrom) {
		onSwitches.remove(switchedOffFrom.toLong());

		if(onSwitches.size() == 0)
			this.switchedOn = false;
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
		if(worldObj.getTileEntity(pos) instanceof IInventory)
			return (IInventory) worldObj.getTileEntity(pos);
		return null;
	}

	public List<IFluidHandler> getAdjacentTanks() {
		BlockPos south = this.getPos().add(EnumFacing.SOUTH.getDirectionVec());
		BlockPos north = this.getPos().add(EnumFacing.NORTH.getDirectionVec());
		BlockPos east = this.getPos().add(EnumFacing.EAST.getDirectionVec());
		BlockPos west = this.getPos().add(EnumFacing.WEST.getDirectionVec());

		List<IFluidHandler> adjacentTanks = new ArrayList<>();

		IFluidHandler tank = getTankAtPos(south);
		if(tank != null)
			adjacentTanks.add(tank);

		tank = getTankAtPos(north);
		if(tank != null)
			adjacentTanks.add(tank);

		tank = getTankAtPos(east);
		if(tank != null)
			adjacentTanks.add(tank);

		tank = getTankAtPos(west);
		if(tank != null)
			adjacentTanks.add(tank);

		return adjacentTanks;
	}

	private IFluidHandler getTankAtPos(BlockPos pos) {
		if(worldObj.getTileEntity(pos) instanceof IFluidHandler)
			return (IFluidHandler) worldObj.getTileEntity(pos);
		return null;
	}

	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		int totalFilled = 0;
		FluidStack resourceCopy = resource.copy();
		for(ItemStack container : fluidContainers) {
			IFluidContainerItem fluidContainer = (IFluidContainerItem) container.getItem();

			totalFilled += fluidContainer.fill(container, resourceCopy, doFill);
			resourceCopy.amount = resource.amount - totalFilled;

			if(totalFilled >= resource.amount)
				break;
		}

		return totalFilled;
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		int totalDrained = 0;
		for(ItemStack container : fluidContainers) {
			IFluidContainerItem fluidContainer = (IFluidContainerItem) container.getItem();

			FluidStack drainedStack = fluidContainer.drain(container, resource.amount - totalDrained, doDrain);
			totalDrained += drainedStack.amount;

			if(totalDrained >= resource.amount)
				break;
		}

		return new FluidStack(resource.getFluid(), totalDrained);
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		if(fluidContainers.size() == 0)
			return null;

		ItemStack container = fluidContainers.get(0);
		Fluid fluid = getContainerFluid(container);

		return drain(from, new FluidStack(fluid, maxDrain), doDrain);
	}

	private Fluid getContainerFluid(ItemStack container) {
		return ((IFluidContainerItem) container.getItem()).getFluid(container).getFluid();
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		if(fluidContainers.size() == 0)
			return false;

		for(ItemStack container : fluidContainers) {
			if(((IFluidContainerItem) container.getItem()).fill(container, new FluidStack(fluid, 1), false) == 1)
				return true;
		}

		return false;
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		if(fluidContainers.size() == 0)
			return false;

		for(ItemStack container : fluidContainers) {
			if(((IFluidContainerItem) container.getItem()).drain(container, 1, false) == new FluidStack(fluid, 1))
				return true;
		}

		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		FluidTankInfo[] tankInfo = new FluidTankInfo[fluidContainers.size()];

		for(int i = 0; i < fluidContainers.size(); i++) {
			ItemStack container = fluidContainers.get(i);
			IFluidContainerItem fluidContainer = (IFluidContainerItem) container.getItem();

			tankInfo[i] = new FluidTankInfo(fluidContainer.getFluid(container), fluidContainer.getCapacity(container));
		}

		return tankInfo;
	}

	private boolean isPowered() {
		return worldObj.isBlockPowered(pos);
	}

	public void removeRedstoneItems() {
		for(Map.Entry<Integer, IPedestalRedstoneItem> item : redstoneItems.entrySet()) {
			item.getValue().onRemoved(inventory[item.getKey()], this);
		}
	}

	// IInventory

	@Override
	public String getName() {
		return null;
	}

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
	public ItemStack removeStackFromSlot(int slot) {
		ItemStack stack;
		if(slot < slots) {
			stack = this.inventory[slot];

			if(redstoneItems.containsKey(slot)) {
				redstoneItems.get(slot).onRemoved(inventory[slot], this);
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
			if(stack == null && redstoneItems.containsKey(slot)) {
				redstoneItems.get(slot).onRemoved(inventory[slot], this);
			}

			this.inventory[slot] = stack;
			if(stack != null && stack.stackSize > 1) {
				stack.stackSize = 1;
			}

			updateItemsAndBlock();
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
			int amount = stack.stackSize - (stackInSlot == null ? 0 : stackInSlot.stackSize);
			stack.stackSize = amount;
			itemHandler.insertItem(adjustedSlot, stack, false);
		}
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return null;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return false;
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		if(index < slots)
			return true;

		int adjustedSlot = index - slots;

		Tuple<IItemHandler, Integer> handlerSlot = getHandlerSlot(adjustedSlot);

		IItemHandler itemHandler = handlerSlot.getFirst();
		adjustedSlot = handlerSlot.getSecond();

		ItemStack returnedStack = itemHandler.insertItem(adjustedSlot, stack, true);

		return returnedStack == null || returnedStack.stackSize != stack.stackSize;
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
			this.setInventorySlotContents(i, null);
	}

	public void removeLastPedestalStack() {
		for(int i = slots - 1; i >= 0; i--) {
			if(inventory[i] != null) {
				ItemStack stack = inventory[i].copy();
				setInventorySlotContents(i, null);
				if(worldObj.isRemote)
					return;
				markDirty();
				EntityItem itemEntity = new EntityItem(worldObj, pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D, stack);
				worldObj.spawnEntityInWorld(itemEntity);
				break;
			}
		}
	}

}