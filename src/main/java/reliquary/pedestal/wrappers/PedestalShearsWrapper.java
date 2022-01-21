package reliquary.pedestal.wrappers;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.util.FakePlayer;
import reliquary.api.IPedestal;
import reliquary.api.IPedestalActionItemWrapper;
import reliquary.reference.Settings;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class PedestalShearsWrapper implements IPedestalActionItemWrapper {
	private boolean isShearingBlock = false;
	private BlockPos blockPosBeingSheared;
	private final Queue<BlockPos> blockQueue = new ArrayDeque<>();

	@Override
	public void update(ItemStack stack, Level level, IPedestal pedestal) {
		BlockPos pos = pedestal.getBlockPos();
		int shearsRange = Settings.COMMON.blocks.pedestal.shearsWrapperRange.get();

		if (shearAnimals(stack, level, pedestal, pos, shearsRange)) {
			return;
		}

		if (!isShearingBlock) {
			pedestal.setActionCoolDown(Settings.COMMON.blocks.pedestal.shearsWrapperCooldown.get());
		}

		if (stack.getCount() == 0) {
			pedestal.destroyItem();
		}
	}

	private boolean shearAnimals(ItemStack stack, Level level, IPedestal pedestal, BlockPos pos, int shearsRange) {
		return pedestal.getFakePlayer().map(fakePlayer -> {
			if (!shearAnimals(stack, level, fakePlayer, pos, shearsRange) && !shearBlocks(stack, level, pedestal, fakePlayer, pos, shearsRange)) {
				pedestal.setActionCoolDown(100);
				return true;
			}
			return false;
		}).orElse(false);
	}

	@Override
	public void onRemoved(ItemStack stack, Level level, IPedestal pedestal) {
		//noop
	}

	@Override
	public void stop(ItemStack stack, Level level, IPedestal pedestal) {
		//noop
	}

	private boolean shearBlocks(ItemStack stack, Level world, IPedestal pedestal, FakePlayer fakePlayer, BlockPos pos, int shearsRange) {
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
				float hardness = blockState.getDestroySpeed(world, blockPosBeingSheared);
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
					Block.dropResources(blockState, world, pos, null, fakePlayer, new ItemStack(Items.SHEARS));
					world.levelEvent(2001, blockPosBeingSheared, Block.getId(blockState));
					stack.hurt(1, world.getRandom(), null);
				}
			}

			isShearingBlock = false;
		}
		return true;
	}

	private boolean isShearableBlock(ItemStack stack, Level world, BlockState blockState) {
		Block block = blockState.getBlock();
		return (block instanceof IForgeShearable shearable && shearable.isShearable(stack, world, blockPosBeingSheared))
				|| (block instanceof BeehiveBlock && blockState.getValue(BeehiveBlock.HONEY_LEVEL) >= 5);
	}

	private void shearBeehive(Level world, BlockPos pos, BlockState blockState, ItemStack stack) {
		int honeyLevel = blockState.getValue(BeehiveBlock.HONEY_LEVEL);
		if (honeyLevel >= 5) {
			world.playSound(null, pos, SoundEvents.BEEHIVE_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
			BeehiveBlock.dropHoneycomb(world, pos);
			((BeehiveBlock) blockState.getBlock()).releaseBeesAndResetHoneyLevel(world, blockState, pos, null, BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED);
			stack.hurt(1, world.getRandom(), null);
		}
	}

	private void updateQueue(ItemStack stack, Level world, BlockPos pos, int shearsRange) {
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

	private boolean shearAnimals(ItemStack stack, Level world, FakePlayer fakePlayer, BlockPos pos, int shearsRange) {
		List<Animal> entities = world.getEntitiesOfClass(Animal.class,
				new AABB(pos.offset(-shearsRange, -shearsRange, -shearsRange), pos.offset(shearsRange, shearsRange, shearsRange)));

		for (Animal animal : entities) {
			if (animal instanceof IForgeShearable shearable && shearable.isShearable(stack, world, animal.blockPosition())) {
				fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, stack);
				fakePlayer.interactOn(animal, InteractionHand.MAIN_HAND);
				return true;
			}
		}
		return false;
	}
}
