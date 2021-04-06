package xreliquary.pedestal.wrappers;

import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IForgeShearable;
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
	private final Queue<BlockPos> blockQueue = new ArrayDeque<>();

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
			if (isShearableBlock(stack, world, blockState)) {
				float hardness = blockState.getBlockHardness(world, blockPosBeingSheared);
				float digSpeed = stack.getItem().getDestroySpeed(stack, blockState);

				pedestal.setActionCoolDown((int) ((hardness * 1.5f * 20f) / digSpeed));
				isShearingBlock = true;
			}
		} else {
			stack.getItem().onBlockStartBreak(stack, blockPosBeingSheared, fakePlayer);
			BlockState blockState = world.getBlockState(blockPosBeingSheared);
			if (blockState.getBlock() instanceof BeehiveBlock) {
				shearBeehive(world, blockPosBeingSheared, blockState, stack);
			} else {
				if (world.removeBlock(blockPosBeingSheared, false)) {
					Block.spawnDrops(blockState, world, pos, null, fakePlayer, new ItemStack(Items.SHEARS));
					world.playEvent(2001, blockPosBeingSheared, Block.getStateId(blockState));
					stack.attemptDamageItem(1, world.getRandom(), null);
				}
			}

			isShearingBlock = false;
		}
		return true;
	}

	private boolean isShearableBlock(ItemStack stack, World world, BlockState blockState) {
		Block block = blockState.getBlock();
		return (block instanceof IForgeShearable && ((IForgeShearable) block).isShearable(stack, world, blockPosBeingSheared))
				|| (block instanceof BeehiveBlock && blockState.get(BeehiveBlock.HONEY_LEVEL) >= 5);
	}

	private void shearBeehive(World world, BlockPos pos, BlockState blockState, ItemStack stack) {
		int honeyLevel = blockState.get(BeehiveBlock.HONEY_LEVEL);
		if (honeyLevel >= 5) {
			world.playSound(null, pos, SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.BLOCKS, 1.0F, 1.0F);
			BeehiveBlock.dropHoneyComb(world, pos);
			((BeehiveBlock) blockState.getBlock()).takeHoney(world, blockState, pos, null, BeehiveTileEntity.State.BEE_RELEASED);
			stack.attemptDamageItem(1, world.getRandom(), null);
		}
	}

	private void updateQueue(ItemStack stack, World world, BlockPos pos, int shearsRange) {
		for (int y = pos.getY() - shearsRange; y <= pos.getY() + shearsRange; y++) {
			for (int x = pos.getX() - shearsRange; x <= pos.getX() + shearsRange; x++) {
				for (int z = pos.getZ() - shearsRange; z <= pos.getZ() + shearsRange; z++) {
					BlockPos currentBlockPos = new BlockPos(x, y, z);
					BlockState blockState = world.getBlockState(currentBlockPos);
					if (isShearableBlock(stack, world, blockState)) {
						blockQueue.add(currentBlockPos);
					}
				}
			}
		}
	}

	private boolean shearAnimals(ItemStack stack, World world, FakePlayer fakePlayer, BlockPos pos, int shearsRange) {
		List<AnimalEntity> entities = world.getEntitiesWithinAABB(AnimalEntity.class,
				new AxisAlignedBB(pos.add(-shearsRange, -shearsRange, -shearsRange), pos.add(shearsRange, shearsRange, shearsRange)));

		for (AnimalEntity animal : entities) {
			if (animal instanceof IForgeShearable && ((IForgeShearable) animal).isShearable(stack, world, animal.getPosition())) {
				fakePlayer.setHeldItem(Hand.MAIN_HAND, stack);
				fakePlayer.interactOn(animal, Hand.MAIN_HAND);
				return true;
			}
		}
		return false;
	}
}
