package xreliquary.pedestal.wrappers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.util.FakePlayer;
import xreliquary.api.IPedestal;
import xreliquary.api.IPedestalActionItemWrapper;
import xreliquary.reference.Settings;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class PedestalShearsWrapper implements IPedestalActionItemWrapper {
	private boolean isShearingBlock = false;
	private BlockPos blockPosBeingSheared;
	private Queue<BlockPos> blockQueue = new ArrayDeque<>();

	@Override
	public void update(ItemStack stack, IPedestal pedestal) {
		World world = pedestal.getTheWorld();
		BlockPos pos = pedestal.getBlockPos();
		FakePlayer fakePlayer = pedestal.getFakePlayer();
		int shearsRange = Settings.COMMON.blocks.pedestal.shearsWrapperRange.get();

		if (!shearAnimals(stack, world, fakePlayer, pos, shearsRange) && !shearBlocks(stack, world, pedestal, fakePlayer, pos, shearsRange)) {
			pedestal.setActionCoolDown(100);
			return;
		}

		if (!isShearingBlock) {
			pedestal.setActionCoolDown(Settings.COMMON.blocks.pedestal.shearsWrapperCooldown.get());
		}

		if (stack.getCount() == 0) {
			pedestal.destroyItem();
		}
	}

	@Override
	public void onRemoved(ItemStack stack, IPedestal pedestal) {
		//noop
	}

	@Override
	public void stop(ItemStack stack, IPedestal pedestal) {
		//noop
	}

	private boolean shearBlocks(ItemStack stack, World world, IPedestal pedestal, FakePlayer fakePlayer, BlockPos pos, int shearsRange) {
		if (!isShearingBlock) {
			if (blockQueue.isEmpty()) {
				updateQueue(stack, world, pos, shearsRange);
				if (blockQueue.isEmpty()) {
					return false;
				}
			}

			blockPosBeingSheared = blockQueue.remove();
			BlockState blockState = world.getBlockState(blockPosBeingSheared);
			if (blockState.getBlock() instanceof IShearable && ((IShearable) blockState.getBlock()).isShearable(stack, world, blockPosBeingSheared)) {
				float hardness = blockState.getBlockHardness(world, blockPosBeingSheared);
				float digSpeed = stack.getItem().getDestroySpeed(stack, blockState);

				pedestal.setActionCoolDown((int) ((hardness * 1.5f * 20f) / digSpeed));
				isShearingBlock = true;
			}
		} else {
			stack.getItem().onBlockStartBreak(stack, blockPosBeingSheared, fakePlayer);
			BlockState blockState = world.getBlockState(blockPosBeingSheared);
			world.removeBlock(blockPosBeingSheared, false);
			world.playEvent(2001, blockPosBeingSheared, Block.getStateId(blockState));

			isShearingBlock = false;
		}
		return true;
	}

	private void updateQueue(ItemStack stack, World world, BlockPos pos, int shearsRange) {
		for (int y = pos.getY() - shearsRange; y <= pos.getY() + shearsRange; y++) {
			for (int x = pos.getX() - shearsRange; x <= pos.getX() + shearsRange; x++) {
				for (int z = pos.getZ() - shearsRange; z <= pos.getZ() + shearsRange; z++) {
					BlockPos currentBlockPos = new BlockPos(x, y, z);
					BlockState blockState = world.getBlockState(currentBlockPos);
					if (blockState.getBlock() instanceof IShearable && ((IShearable) blockState.getBlock()).isShearable(stack, world, currentBlockPos)) {
						blockQueue.add(currentBlockPos);
					}
				}
			}
		}
	}

	private boolean shearAnimals(ItemStack stack, World world, FakePlayer fakePlayer, BlockPos pos, int shearsRange) {
		List<AnimalEntity> entities = world.getEntitiesWithinAABB(AnimalEntity.class, new AxisAlignedBB(pos.getX() - shearsRange, pos.getY() - shearsRange, pos.getZ() - shearsRange, pos.getX() + shearsRange, pos.getY() + shearsRange, pos.getZ() + shearsRange));

		for (AnimalEntity animal : entities) {
			if (animal instanceof IShearable && ((IShearable) animal).isShearable(stack, world, animal.getPosition())) {
				fakePlayer.setHeldItem(Hand.MAIN_HAND, stack);
				fakePlayer.interactOn(animal, Hand.MAIN_HAND);
				return true;
			}
		}
		return false;
	}
}
