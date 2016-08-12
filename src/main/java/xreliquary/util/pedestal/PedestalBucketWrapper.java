package xreliquary.util.pedestal;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
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
import java.util.List;

public class PedestalBucketWrapper implements IPedestalActionItemWrapper {

	private List<BlockPos> queueToDrain = new ArrayList<>();
	private Fluid currentFluid;

	@Override
	public void update(ItemStack stack, IPedestal pedestal) {
		BlockPos pos = pedestal.getBlockPos();
		int bucketRange = Settings.Pedestal.bucketWrapperRange;

		if(!milkCows(pedestal, pos, bucketRange)) {
			if(!drainLiquid(pedestal, pos, bucketRange)) {
				pedestal.setActionCoolDown(2 * Settings.Pedestal.bucketWrapperCooldown);
				return;
			}
		}

		pedestal.setActionCoolDown(Settings.Pedestal.bucketWrapperCooldown);
	}

	private boolean drainLiquid(IPedestal pedestal, BlockPos pos, int bucketRange) {
		World world = pedestal.getTheWorld();
		if(queueToDrain.isEmpty()) {
			updateQueueToDrain(world, pos, bucketRange);
		}

		if(queueToDrain.isEmpty())
			return false;

		BlockPos blockToDrain = queueToDrain.get(0);
		IBlockState blockState = world.getBlockState(blockToDrain);
		Fluid fluid = FluidRegistry.lookupFluidForBlock(blockState.getBlock());
		if(fluid != null) {
			FluidStack fluidStack = drainBlock(world, blockToDrain, blockState.getBlock(), blockState, fluid, false);
			if(fluidStack != null) {
				if((pedestal.fillConnectedTank(fluidStack, false) != fluidStack.amount))
					return false;

				drainBlock(world, blockToDrain, blockState.getBlock(), blockState, fluid, true);
				pedestal.fillConnectedTank(fluidStack);
			}
		}

		queueToDrain.remove(0);

		return true;
	}

	private void updateQueueToDrain(World world, BlockPos pos, int bucketRange) {
		for(int y = pos.getY() + bucketRange; y >= pos.getY() - bucketRange; y--) {
			for(int x = pos.getX() - bucketRange; x <= pos.getX() + bucketRange; x++) {
				for(int z = pos.getZ() - bucketRange; z <= pos.getZ() + bucketRange; z++) {
					BlockPos currentBlockPos = new BlockPos(x, y, z);
					IBlockState blockState = world.getBlockState(currentBlockPos);
					Fluid fluid = FluidRegistry.lookupFluidForBlock(blockState.getBlock());

					if(fluid != null && canDrainBlock(world, currentBlockPos, blockState.getBlock(), blockState, fluid) && (fluid == currentFluid || queueToDrain.isEmpty())) {
						if(queueToDrain.isEmpty())
							currentFluid = fluid;

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

	private boolean milkCows(IPedestal pedestal, BlockPos pos, int bucketRange) {
		World world = pedestal.getTheWorld();
		List<EntityCow> entities = world.getEntitiesWithinAABB(EntityCow.class, new AxisAlignedBB(pos.getX() - bucketRange, pos.getY() - bucketRange, pos.getZ() - bucketRange, pos.getX() + bucketRange, pos.getY() + bucketRange, pos.getZ() + bucketRange));

		if(entities.size() == 0) {
			return false;
		}

		EntityCow cow = entities.get(world.rand.nextInt(entities.size()));

		FakePlayer fakePlayer = pedestal.getFakePlayer();
		ItemStack bucketStack = new ItemStack(Items.BUCKET);
		fakePlayer.setHeldItem(EnumHand.MAIN_HAND, bucketStack);

		cow.processInteract(fakePlayer, EnumHand.MAIN_HAND, bucketStack);

		if(fakePlayer.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.MILK_BUCKET) {
			int fluidAdded = pedestal.fillConnectedTank(new FluidStack(ModFluids.milk, Fluid.BUCKET_VOLUME));
			if(fluidAdded == 0) {
				pedestal.replaceCurrentItem(new ItemStack(Items.MILK_BUCKET));
				return true;
			}
		}
		return true;
	}
}
