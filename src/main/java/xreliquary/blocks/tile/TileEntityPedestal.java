package xreliquary.blocks.tile;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fluids.*;
import xreliquary.api.IPedestal;
import xreliquary.api.IPedestalActionItem;
import xreliquary.api.IPedestalActionItemWrapper;
import xreliquary.entities.EntityXRFakePlayer;
import xreliquary.init.ModFluids;
import xreliquary.util.InventoryHelper;
import xreliquary.util.pedestal.PedestalRegistry;

import java.util.*;

public class TileEntityPedestal extends TileEntityInventory implements IPedestal, IFluidHandler {

	private boolean tickable = false;
	private int[] actionCooldowns;
	private int currentItemIndex;
	private List<ItemStack> actionItems = new ArrayList<>();
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
		worldObj.markBlockForUpdate(getPos());
	}

	private void updateSpecialItems() {
		tickable = false;
		actionItems.clear();
		fluidContainers.clear();
		itemWrappers.clear();

		for(int i = 0; i < inventory.length; i++) {
			ItemStack item = inventory[i];
			if(item == null)
				continue;

			if(item.getItem() instanceof IPedestalActionItem) {
				tickable = true;
				actionItems.add(item);
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
		actionCooldowns = new int[actionItems.size() + itemWrappers.size()];
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
		if(tickable && !worldObj.isRemote) {
			for(currentItemIndex = 0; currentItemIndex < actionItems.size(); currentItemIndex++) {
				if(actionCooldowns[currentItemIndex] > 0) {
					actionCooldowns[currentItemIndex]--;
				} else {
					ItemStack item = actionItems.get(currentItemIndex);
					IPedestalActionItem actionItem = (IPedestalActionItem) item.getItem();
					actionItem.update(item, this);
				}
			}

			for(int itemIndex : itemWrappers.keySet()) {
				if(actionCooldowns[currentItemIndex] > 0) {
					actionCooldowns[currentItemIndex]--;
				} else
					itemWrappers.get(itemIndex).update(inventory[itemIndex], this);

				currentItemIndex++;
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
	public IChatComponent getDisplayName() {
		return null;
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
	public int addToConnectedTank(FluidStack fluidStack) {
		List<IFluidHandler> adjacentTanks = getAdjacentTanks();

		int fluidFilled = 0;
		FluidStack copy = fluidStack.copy();

		for(IFluidHandler tank : adjacentTanks) {
			if(tank.canFill(EnumFacing.UP, ModFluids.fluidXpJuice)) {
				fluidFilled += tank.fill(EnumFacing.UP, copy, true);

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
	public void setActionCoolDown(int coolDownTicks) {
		actionCooldowns[currentItemIndex] = coolDownTicks;
	}

	@Override
	public FakePlayer getFakePlayer() {
		if(this.worldObj.isRemote)
			return null;

		if(fakePlayer == null) {
			WorldServer world = (WorldServer) worldObj;
			fakePlayer = new EntityXRFakePlayer(world);
		}

		return fakePlayer;
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
		if (worldObj.getTileEntity(pos) instanceof IInventory)
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
		if (worldObj.getTileEntity(pos) instanceof IFluidHandler)
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

		if(getContainerFluid(fluidContainers.get(0)) == fluid) {
			return true; //TODO check for free capacity of handlers?
		}

		return false;
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		if(fluidContainers.size() == 0)
			return false;

		if(getContainerFluid(fluidContainers.get(0)) == fluid) {
			return true; //TODO check for free capacity of handlers?
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
}