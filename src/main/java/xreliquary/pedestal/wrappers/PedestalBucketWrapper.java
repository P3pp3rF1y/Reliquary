package xreliquary.pedestal.wrappers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import xreliquary.api.IPedestal;
import xreliquary.api.IPedestalActionItemWrapper;
import xreliquary.init.ModFluids;
import xreliquary.reference.Settings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PedestalBucketWrapper implements IPedestalActionItemWrapper {

	private static final int UNSUCCESSFUL_TRIES_TO_CLEAN_QUEUE = 5;

	private List<BlockPos> queueToDrain = new ArrayList<>();
	private int unsuccessfulTries = 0;

	@Override
	public void update(ItemStack stack, IPedestal pedestal) {
		BlockPos pos = pedestal.getBlockPos();
		int bucketRange = Settings.Pedestal.bucketWrapperRange;

		if(!milkCows(pedestal, pos, bucketRange, stack)) {
			if(!drainLiquid(pedestal, pos, bucketRange)) {
				pedestal.setActionCoolDown(2 * Settings.Pedestal.bucketWrapperCooldown);
				return;
			}
		}

		pedestal.setActionCoolDown(Settings.Pedestal.bucketWrapperCooldown);
	}

	@Override
	public void onRemoved(ItemStack stack, IPedestal pedestal) {
	}

	@Override
	public void stop(ItemStack stack, IPedestal pedestal) {
	}

	private boolean drainLiquid(IPedestal pedestal, BlockPos pos, int bucketRange) {
		World world = pedestal.getTheWorld();
		if(queueToDrain.isEmpty()) {
			updateQueueToDrain(world, pos, bucketRange);
		}

		if(queueToDrain.isEmpty())
			return false;

		Iterator<BlockPos> iterator = queueToDrain.iterator();

		//iterate through all the fluid blocks in queue - needed in case there are multiple fluids and next fluid in queue can't go in any tank
		while(iterator.hasNext()) {
			BlockPos blockToDrain = iterator.next();
			IBlockState blockState = world.getBlockState(blockToDrain);
			Fluid fluid = FluidRegistry.lookupFluidForBlock(blockState.getBlock());

			//make sure that the block is still fluid as we're working with cached queue
			if(fluid != null) {
				FluidStack fluidStack = drainBlock(world, blockToDrain, blockState.getBlock(), blockState, fluid, false);
				if(fluidStack != null) {

					//check if we were able to fill the fluid in some tank, otherwise try the next fluid block in queue
					if((pedestal.fillConnectedTank(fluidStack, false) != fluidStack.amount))
						continue;

					drainBlock(world, blockToDrain, blockState.getBlock(), blockState, fluid, true);
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
					IBlockState blockState = world.getBlockState(currentBlockPos);
					Fluid fluid = FluidRegistry.lookupFluidForBlock(blockState.getBlock());

					if(fluid != null && canDrainBlock(world, currentBlockPos, blockState.getBlock(), blockState, fluid)) {
						queueToDrain.add(currentBlockPos);
					}
				}
			}
		}
	}

	private boolean canDrainBlock(World world, BlockPos pos, Block block, IBlockState blockState, Fluid fluid) {
		return drainBlock(world, pos, block, blockState, fluid, false) != null;
	}

	private FluidStack drainBlock(World world, BlockPos pos, Block block, IBlockState blockState, Fluid fluid, boolean doDrain) {
		if(block instanceof IFluidBlock) {
			IFluidBlock fluidBlock = (IFluidBlock) block;
			if(!fluidBlock.canDrain(world, pos)) {
				return null;
			}
			return fluidBlock.drain(world, pos, doDrain);
		} else if(block instanceof BlockLiquid) {
			int level = blockState.getValue(BlockLiquid.LEVEL);
			if(level != 0) {
				return null;
			}

			if(doDrain)
				world.setBlockToAir(pos);

			return new FluidStack(fluid, Fluid.BUCKET_VOLUME);
		}
		return null;
	}

	private boolean milkCows(IPedestal pedestal, BlockPos pos, int bucketRange, ItemStack stack) {
		//find all cow entities in range
		World world = pedestal.getTheWorld();
		List<EntityCow> entities = world.getEntitiesWithinAABB(EntityCow.class, new AxisAlignedBB(pos.getX() - bucketRange, pos.getY() - bucketRange, pos.getZ() - bucketRange, pos.getX() + bucketRange, pos.getY() + bucketRange, pos.getZ() + bucketRange));

		if(entities.size() == 0) {
			return false;
		}

		EntityCow cow = entities.get(world.rand.nextInt(entities.size()));

		//init fake player
		FakePlayer fakePlayer = pedestal.getFakePlayer();
		ItemStack bucketStack = new ItemStack(Items.BUCKET);
		fakePlayer.setHeldItem(EnumHand.MAIN_HAND, bucketStack);

		//right click cow with bucket
		cow.processInteract(fakePlayer, EnumHand.MAIN_HAND, bucketStack);

		//put milk in the adjacent tanks
		if(fakePlayer.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.MILK_BUCKET) {
			int fluidAdded = pedestal.fillConnectedTank(new FluidStack(ModFluids.milk, Fluid.BUCKET_VOLUME));
			//replace bucket in the pedestal with milk one if the tanks can't hold it
			if(fluidAdded == 0) {
				if(stack.stackSize == 1) {
					pedestal.replaceCurrentItem(new ItemStack(Items.MILK_BUCKET));
					return true;
				} else if(stack.stackSize > 1) {
					stack.stackSize--;
					EntityItem entity = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D, new ItemStack(Items.MILK_BUCKET));
					world.spawnEntityInWorld(entity);
				}
			}
		}
		return true;
	}
}
