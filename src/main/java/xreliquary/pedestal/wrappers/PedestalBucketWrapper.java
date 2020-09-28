package xreliquary.pedestal.wrappers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.IFluidHandler;
import xreliquary.api.IPedestal;
import xreliquary.api.IPedestalActionItemWrapper;
import xreliquary.init.ModFluids;
import xreliquary.reference.Settings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class PedestalBucketWrapper implements IPedestalActionItemWrapper {

	private static final int UNSUCCESSFUL_TRIES_TO_CLEAN_QUEUE = 5;

	private final List<BlockPos> queueToDrain = new ArrayList<>();
	private int unsuccessfulTries = 0;

	@Override
	public void update(ItemStack stack, IPedestal pedestal) {
		BlockPos pos = pedestal.getBlockPos();
		int bucketRange = Settings.COMMON.blocks.pedestal.bucketWrapperRange.get();

		if (!milkCows(pedestal, pos, bucketRange, stack) && !drainLiquid(pedestal, pos, bucketRange)) {
			pedestal.setActionCoolDown(2 * Settings.COMMON.blocks.pedestal.bucketWrapperCooldown.get());
			return;
		}

		pedestal.setActionCoolDown(Settings.COMMON.blocks.pedestal.bucketWrapperCooldown.get());
	}

	@Override
	public void onRemoved(ItemStack stack, IPedestal pedestal) {
		//noop
	}

	@Override
	public void stop(ItemStack stack, IPedestal pedestal) {
		//noop
	}

	private boolean drainLiquid(IPedestal pedestal, BlockPos pos, int bucketRange) {
		World world = pedestal.getTheWorld();
		if(queueToDrain.isEmpty()) {
			updateQueueToDrain(world, pos, bucketRange);
		}

		if(queueToDrain.isEmpty()) {
			return false;
		}

		Iterator<BlockPos> iterator = queueToDrain.iterator();

		//iterate through all the fluid blocks in queue - needed in case there are multiple fluids and next fluid in queue can't go in any tank
		while(iterator.hasNext()) {
			BlockPos blockToDrain = iterator.next();
			BlockState blockState = world.getBlockState(blockToDrain);
			Fluid fluid = blockState.getFluidState().getFluid();

			//make sure that the block is still fluid as we're working with cached queue
			if(fluid != Fluids.EMPTY) {
				Optional<FluidStack> fs = drainBlock(world, blockToDrain, blockState.getBlock(), blockState, fluid, IFluidHandler.FluidAction.SIMULATE);
				if(fs.isPresent()) {
					FluidStack fluidStack = fs.get();
					//check if we were able to fill the fluid in some tank, otherwise try the next fluid block in queue
					if((pedestal.fillConnectedTank(fluidStack, IFluidHandler.FluidAction.SIMULATE) != fluidStack.getAmount())) {
						continue;
					}

					drainBlock(world, blockToDrain, blockState.getBlock(), blockState, fluid, IFluidHandler.FluidAction.EXECUTE);
					pedestal.fillConnectedTank(fluidStack);
					iterator.remove();
					return true;
				} else {
					iterator.remove();
				}
			} else {
				iterator.remove();
			}
		}

		unsuccessfulTries++;

		if(unsuccessfulTries >= UNSUCCESSFUL_TRIES_TO_CLEAN_QUEUE) {
			queueToDrain.clear();
			unsuccessfulTries = 0;
		}

		return false;
	}

	private void updateQueueToDrain(World world, BlockPos pos, int bucketRange) {
		for(int y = pos.getY() + bucketRange; y >= pos.getY() - bucketRange; y--) {
			for(int x = pos.getX() - bucketRange; x <= pos.getX() + bucketRange; x++) {
				for(int z = pos.getZ() - bucketRange; z <= pos.getZ() + bucketRange; z++) {
					BlockPos currentBlockPos = new BlockPos(x, y, z);
					BlockState blockState = world.getBlockState(currentBlockPos);
					Fluid fluid = blockState.getFluidState().getFluid();

					if(fluid != Fluids.EMPTY && canDrainBlock(world, currentBlockPos, blockState.getBlock(), blockState, fluid)) {
						queueToDrain.add(currentBlockPos);
					}
				}
			}
		}
	}

	private boolean canDrainBlock(World world, BlockPos pos, Block block, BlockState blockState, Fluid fluid) {
		return drainBlock(world, pos, block, blockState, fluid, IFluidHandler.FluidAction.SIMULATE).isPresent();
	}

	private Optional<FluidStack> drainBlock(World world, BlockPos pos, Block block, BlockState blockState, Fluid fluid, IFluidHandler.FluidAction action) {
		if(block instanceof IFluidBlock) {
			IFluidBlock fluidBlock = (IFluidBlock) block;
			if(!fluidBlock.canDrain(world, pos)) {
				return Optional.empty();
			}
			return Optional.of(fluidBlock.drain(world, pos, action));
		} else if(block instanceof FlowingFluidBlock) {
			int level = blockState.get(FlowingFluidBlock.LEVEL);
			if(level != 0) {
				return Optional.empty();
			}

			if(action == IFluidHandler.FluidAction.EXECUTE) {
				world.setBlockState(pos, Blocks.AIR.getDefaultState());
			}

			return Optional.of(new FluidStack(fluid, FluidAttributes.BUCKET_VOLUME));
		}
		return Optional.empty();
	}

	private boolean milkCows(IPedestal pedestal, BlockPos pos, int bucketRange, ItemStack stack) {
		//find all cow entities in range
		World world = pedestal.getTheWorld();
		List<CowEntity> entities = world.getEntitiesWithinAABB(CowEntity.class,
				new AxisAlignedBB((double) pos.getX() - bucketRange, (double) pos.getY() - bucketRange, (double) pos.getZ() - bucketRange,
						(double) pos.getX() + bucketRange, (double) pos.getY() + bucketRange, (double) pos.getZ() + bucketRange));

		if(entities.isEmpty()) {
			return false;
		}

		CowEntity cow = entities.get(world.rand.nextInt(entities.size()));

		//init fake player
		FakePlayer fakePlayer = pedestal.getFakePlayer();

		//set position because of sound
		fakePlayer.setPosition(pos.getX(), 0, pos.getZ());

		ItemStack bucketStack = new ItemStack(Items.BUCKET);
		fakePlayer.setHeldItem(Hand.MAIN_HAND, bucketStack);

		//right click cow with bucket
		cow.func_230254_b_(fakePlayer, Hand.MAIN_HAND);

		//put milk in the adjacent tanks
		if(fakePlayer.getHeldItem(Hand.MAIN_HAND).getItem() == Items.MILK_BUCKET) {
			int fluidAdded = pedestal.fillConnectedTank(new FluidStack(ModFluids.MILK_STILL.get(), FluidAttributes.BUCKET_VOLUME));
			//replace bucket in the pedestals with milk one if the tanks can't hold it
			if(fluidAdded == 0) {
				if(stack.getCount() == 1) {
					pedestal.setItem(new ItemStack(Items.MILK_BUCKET));
					return true;
				} else if(stack.getCount() > 1) {
					stack.shrink(1);
					ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D, new ItemStack(Items.MILK_BUCKET));
					world.addEntity(entity);
				}
			}
		}
		return true;
	}
}
