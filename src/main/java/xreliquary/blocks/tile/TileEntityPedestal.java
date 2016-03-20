package xreliquary.blocks.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.*;
import xreliquary.api.IPedestal;
import xreliquary.api.IPedestalActionItem;
import xreliquary.api.IPedestalActionItemWrapper;
import xreliquary.util.InventoryHelper;
import xreliquary.util.XRFakePlayerFactory;
import xreliquary.util.pedestal.PedestalRegistry;

import java.util.*;

public class TileEntityPedestal extends TileEntityInventory implements IPedestal, IFluidHandler {

	private boolean tickable = false;
	private int[] actionCooldowns;
	private int currentItemIndex;
	private Map<Integer, IPedestalActionItem> actionItems = new HashMap<>();
	private Map<Integer, IPedestalActionItemWrapper> itemWrappers = new HashMap<>();
	private List<ItemStack> fluidContainers = new ArrayList<>();
	private FakePlayer fakePlayer = null;

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

	public TileEntityPedestal() {
		super(1);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		super.setInventorySlotContents(slot, stack);
		updateSpecialItems();
		IBlockState blockState = worldObj.getBlockState(getPos());
		worldObj.notifyBlockUpdate(getPos(), blockState, blockState, 3);
	}

	private void updateSpecialItems() {
		tickable = false;
		fluidContainers.clear();
		itemWrappers.clear();
		actionItems.clear();

		for(int i = 0; i < inventory.length; i++) {
			ItemStack item = inventory[i];
			if(item == null)
				continue;

			if(item.getItem() instanceof IPedestalActionItem) {
				tickable = true;
				actionItems.put(i, (IPedestalActionItem) item.getItem());
			} else {
				IPedestalActionItemWrapper wrapper = PedestalRegistry.getItemWrapper(item);
				if(wrapper != null) {
					tickable = true;
					itemWrappers.put(i, wrapper);
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
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

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
	public void update() {
		if(isPowered() && tickable && !worldObj.isRemote) {
			for(currentItemIndex = 0; currentItemIndex < inventory.length; currentItemIndex++) {
				if(actionCooldowns[currentItemIndex] > 0) {
					actionCooldowns[currentItemIndex]--;
				} else {
					ItemStack item = inventory[currentItemIndex];

					if(actionItems.containsKey(currentItemIndex)) {
						actionItems.get(currentItemIndex).update(item, this);
					} else if(itemWrappers.containsKey(currentItemIndex)) {
						itemWrappers.get(currentItemIndex).update(inventory[currentItemIndex], this);
					}
				}
			}
		}
	}

	@Override
	public String getName() {
		return null;
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
}