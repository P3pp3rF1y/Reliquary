package reliquary.block.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.transfer.CombinedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;
import reliquary.api.IPedestal;
import reliquary.api.IPedestalActionItem;
import reliquary.api.IPedestalRedstoneItem;
import reliquary.api.IPedestalRedstoneItemWrapper;
import reliquary.block.PedestalBlock;
import reliquary.init.ModBlocks;
import reliquary.pedestal.PedestalRegistry;
import reliquary.util.FakePlayerFactory;
import reliquary.util.InventoryHelper;

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
	private ResourceHandler<ItemResource> itemHandler = null;
	@Nullable
	private ResourceHandler<ItemResource> combinedHandler = null;
	private boolean switchedOn = false;
	private final List<BlockPos> onSwitches = new ArrayList<>();
	private boolean enabledInitialized = false;
	private boolean powered = false;
	private PedestalFluidHandler pedestalFluidHandler = null;
	private Object itemData = null;

	public PedestalBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlocks.PEDESTAL_TILE_TYPE.get(), pos, state);
	}

	@Override
	protected void loadAdditional(ValueInput in) {
		super.loadAdditional(in);

		switchedOn = in.getBooleanOr("SwitchedOn", false);
		powered = in.getBooleanOr("Powered", false);

		onSwitches.clear();
		in.listOrEmpty("OnSwitches", BlockPos.CODEC).forEach(onSwitches::add);

		updateSpecialItems();
	}

	@Override
	public void saveAdditional(ValueOutput out) {
		super.saveAdditional(out);

		out.putBoolean("SwitchedOn", switchedOn);
		out.putBoolean("Powered", powered);

		ValueOutput.TypedOutputList<BlockPos> switchesOutList = out.list("OnSwitches", BlockPos.CODEC);

		onSwitches.forEach(switchesOutList::add);
	}

	@Override
	public void onChunkUnloaded() {
		if (level != null && !level.isClientSide()) {
			PedestalRegistry.unregisterPosition(level.dimension().registry(), worldPosition);
		}

		super.onChunkUnloaded();
	}

	@Override
	public void onLoad() {
		if (level != null && !level.isClientSide()) {
			PedestalRegistry.registerPosition(level.dimension().registry(), worldPosition);
		}

		super.onLoad();
	}

	@Override
	public ResourceHandler<ItemResource> getItemHandler() {
		ResourceHandler<ItemResource> superInventory = super.getItemHandler();
		if (itemHandler == null) {
			return superInventory;
		}
		if (combinedHandler == null) {
			combinedHandler = new CombinedResourceHandler<>(superInventory, itemHandler);
		}
		return combinedHandler;
	}

	public ResourceHandler<FluidResource> getFluidHandler() {
		if (pedestalFluidHandler == null) {
			pedestalFluidHandler = new PedestalFluidHandler(this);
		}
		return pedestalFluidHandler;
	}

	public Optional<IPedestalActionItem> getActionItem() {
		return Optional.ofNullable(actionItem);
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

		ResourceHandler<ItemResource> ih = item.getCapability(Capabilities.Item.ITEM, ItemAccess.forStack(item));
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

		actionCooldown = 0;
	}

	private void resetSpecialItems() {
		tickable = false;
		actionItem = null;
		redstoneItem = null;
		itemHandler = null;
		if (combinedHandler != null) {
			level.invalidateCapabilities(getBlockPos());
		}
		combinedHandler = null;
	}

	public void serverTick(Level level) {
		if (level.isClientSide()) {
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
				getActionItem().ifPresent(ai -> ai.update(getItem(), level, this));
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
			numberAdded += InventoryHelper.tryToAddToInventoryAtPos(stack, level, worldPosition.offset(side.getUnitVec3i()), side.getOpposite(),
					stack.getCount() - numberAdded);
			if (numberAdded >= stack.getCount()) {
				break;
			}
		}

		return numberAdded;
	}

	@Override
	public int fillConnectedTank(FluidResource fluidResource, int amount, Transaction tx) {
		int fluidFilled = 0;
		List<ResourceHandler<FluidResource>> adjacentTanks = getAdjacentTanks();
		fluidFilled = getFluidFilled(fluidResource, amount, adjacentTanks, fluidFilled, tx);
		return fluidFilled;
	}

	private static int getFluidFilled(FluidResource fluidResource, int amount, List<ResourceHandler<FluidResource>> adjacentTanks, int fluidFilled,
			Transaction tx) {
		for (ResourceHandler<FluidResource> tank : adjacentTanks) {
			fluidFilled += tank.insert(fluidResource, amount - fluidFilled, tx);
			if (fluidFilled >= amount) {
				break;
			}
		}
		return fluidFilled;
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
		if (switchedOnFrom != BlockPos.ZERO && !onSwitches.contains(switchedOnFrom)) {
			onSwitches.add(switchedOnFrom);
		}

		setEnabled(level, true);

		BlockState blockState = level.getBlockState(worldPosition);
		level.sendBlockUpdated(worldPosition, blockState, blockState, 3);
	}

	@Override
	public void switchOff(Level level, BlockPos switchedOffFrom) {
		if (switchedOffFrom != BlockPos.ZERO) {
			onSwitches.remove(switchedOffFrom);
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

	public List<BlockPos> getOnSwitches() {
		return onSwitches;
	}

	private void setEnabled(Level level, boolean switchedOn) {
		if (level.getBlockState(worldPosition).getBlock() instanceof PedestalBlock) {
			level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(PedestalBlock.ENABLED, switchedOn));
			if (!switchedOn) {
				getActionItem().ifPresent(ai -> ai.stop(getItem(), level, this));
			}
		}
		setChanged();
	}

	private List<ResourceHandler<FluidResource>> getAdjacentTanks() {
		List<ResourceHandler<FluidResource>> adjacentTanks = new ArrayList<>();

		for (Direction side : Direction.values()) {
			BlockPos tankPos = getBlockPos().offset(side.getUnitVec3i());
			Direction tankDirection = side.getOpposite();
			addIfTank(adjacentTanks, tankPos, tankDirection);
		}

		return adjacentTanks;
	}

	private void addIfTank(List<ResourceHandler<FluidResource>> adjacentTanks, BlockPos tankPos, Direction tankDirection) {
		ResourceHandler<FluidResource> fh = level.getCapability(Capabilities.Fluid.BLOCK, tankPos, tankDirection);
		if (fh != null) {
			adjacentTanks.add(fh);
		}
	}

	public void removeSpecialItems(Level level) {
		removeSpecialItems(level, getItem());
	}

	public void removeSpecialItems(Level level, ItemStack itemBeingRemoved) {
		executeOnRedstoneItem(ri -> ri.onRemoved(itemBeingRemoved, level, this));
		getActionItem().ifPresent(ai -> ai.onRemoved(itemBeingRemoved, level, this));
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

	public boolean isEnabled() {
		return getBlockState().getValue(PedestalBlock.ENABLED);
	}
}
