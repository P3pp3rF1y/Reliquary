package reliquary.pedestal.wrappers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import reliquary.api.IPedestal;
import reliquary.api.IPedestalActionItemWrapper;
import reliquary.reference.Config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PedestalBucketWrapper implements IPedestalActionItemWrapper {

	private static final int UNSUCCESSFUL_TRIES_TO_CLEAN_QUEUE = 5;

	private final List<BlockPos> queueToDrain = new ArrayList<>();
	private int unsuccessfulTries = 0;

	@Override
	public void update(ItemStack stack, Level level, IPedestal pedestal) {
		BlockPos pos = pedestal.getBlockPosition();
		int bucketRange = Config.COMMON.blocks.pedestal.bucketWrapperRange.get();

		if (!milkCows(level, pedestal, pos, bucketRange, stack) && !drainLiquid(level, pedestal, pos, bucketRange)) {
			pedestal.setActionCoolDown(2 * Config.COMMON.blocks.pedestal.bucketWrapperCooldown.get());
			return;
		}

		pedestal.setActionCoolDown(Config.COMMON.blocks.pedestal.bucketWrapperCooldown.get());
	}

	@Override
	public void onRemoved(ItemStack stack, Level level, IPedestal pedestal) {
		//noop
	}

	@Override
	public void stop(ItemStack stack, Level level, IPedestal pedestal) {
		//noop
	}

	private boolean drainLiquid(Level level, IPedestal pedestal, BlockPos pos, int bucketRange) {
		if (queueToDrain.isEmpty()) {
			updateQueueToDrain(level, pos, bucketRange);
		}

		if (queueToDrain.isEmpty()) {
			return false;
		}

		Iterator<BlockPos> iterator = queueToDrain.iterator();

		//iterate through all the fluid blocks in queue - needed in case there are multiple fluids and next fluid in queue can't go in any tank
		while (iterator.hasNext()) {
			BlockPos blockToDrain = iterator.next();
			BlockState blockState = level.getBlockState(blockToDrain);
			Fluid fluid = blockState.getFluidState().getType();

			//make sure that the block is still fluid as we're working with cached queue
			if (fluid != Fluids.EMPTY && blockState.getBlock() instanceof BucketPickup bucketPickup) {
				try (Transaction tx = Transaction.openRoot()) {
					//check if we were able to fill the fluid in some tank, otherwise try the next fluid block in queue
					if ((pedestal.fillConnectedTank(FluidResource.of(fluid), FluidType.BUCKET_VOLUME, tx) != FluidType.BUCKET_VOLUME)) {
						continue;
					}

					bucketPickup.pickupBlock(null, level, blockToDrain, blockState);
					tx.commit();
					iterator.remove();
					return true;
				}
			} else {
				iterator.remove();
			}
		}

		unsuccessfulTries++;

		if (unsuccessfulTries >= UNSUCCESSFUL_TRIES_TO_CLEAN_QUEUE) {
			queueToDrain.clear();
			unsuccessfulTries = 0;
		}

		return false;
	}

	private void updateQueueToDrain(Level level, BlockPos pos, int bucketRange) {
		for (int y = pos.getY() + bucketRange; y >= pos.getY() - bucketRange; y--) {
			for (int x = pos.getX() - bucketRange; x <= pos.getX() + bucketRange; x++) {
				for (int z = pos.getZ() - bucketRange; z <= pos.getZ() + bucketRange; z++) {
					BlockPos currentBlockPos = new BlockPos(x, y, z);
					BlockState blockState = level.getBlockState(currentBlockPos);
					Fluid fluid = blockState.getFluidState().getType();

					if (fluid != Fluids.EMPTY && canDrainBlock(blockState, fluid)) {
						queueToDrain.add(currentBlockPos);
					}
				}
			}
		}
	}

	private boolean canDrainBlock(BlockState blockState, Fluid fluid) {
		return fluid != Fluids.EMPTY && blockState.getBlock() instanceof BucketPickup;
	}

	private boolean milkCows(Level level, IPedestal pedestal, BlockPos pos, int bucketRange, ItemStack stack) {
		//find all cow entities in range
		List<Cow> entities = level.getEntitiesOfClass(Cow.class,
				new AABB((double) pos.getX() - bucketRange, (double) pos.getY() - bucketRange, (double) pos.getZ() - bucketRange,
						(double) pos.getX() + bucketRange, (double) pos.getY() + bucketRange, (double) pos.getZ() + bucketRange));

		if (entities.isEmpty()) {
			return false;
		}

		Cow cow = entities.get(level.random.nextInt(entities.size()));

		pedestal.getFakePlayer().ifPresent(fakePlayer -> milkCow(level, pedestal, pos, stack, cow, fakePlayer));

		return true;
	}

	private void milkCow(Level level, IPedestal pedestal, BlockPos pos, ItemStack stack, Cow cow, FakePlayer fakePlayer) {
		//set position because of sound
		fakePlayer.setPos(pos.getX(), 0, pos.getZ());

		ItemStack bucketStack = new ItemStack(Items.BUCKET);
		fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, bucketStack);

		cow.mobInteract(fakePlayer, InteractionHand.MAIN_HAND);

		//put milk in the adjacent tanks
		if (fakePlayer.getItemInHand(InteractionHand.MAIN_HAND).getItem() == Items.MILK_BUCKET) {
			int fluidAdded = pedestal.fillConnectedTank(FluidResource.of(NeoForgeMod.MILK.get()), FluidType.BUCKET_VOLUME);
			//replace bucket in the pedestals with milk one if the tanks can't hold it
			if (fluidAdded == 0) {
				if (stack.getCount() == 1) {
					pedestal.setItem(new ItemStack(Items.MILK_BUCKET));
				} else if (stack.getCount() > 1) {
					stack.shrink(1);
					ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D, new ItemStack(Items.MILK_BUCKET));
					level.addFreshEntity(entity);
				}
			}
		}
	}
}
