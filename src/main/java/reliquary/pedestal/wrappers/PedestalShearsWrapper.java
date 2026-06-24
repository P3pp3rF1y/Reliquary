package reliquary.pedestal.wrappers;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
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
import net.neoforged.neoforge.common.IShearable;
import net.neoforged.neoforge.common.util.FakePlayer;
import reliquary.api.IPedestal;
import reliquary.api.IPedestalActionItemWrapper;
import reliquary.reference.Config;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class PedestalShearsWrapper implements IPedestalActionItemWrapper {
	private boolean isShearingBlock = false;
	private BlockPos blockPosBeingSheared;
	private final Queue<BlockPos> blockQueue = new ArrayDeque<>();

	@Override
	public void update(ItemStack stack, Level level, IPedestal pedestal) {
		BlockPos pos = pedestal.getBlockPosition();
		int shearsRange = Config.COMMON.blocks.pedestal.shearsWrapperRange.get();

		if (shearAnimals(stack, level, pedestal, pos, shearsRange)) {
			return;
		}

		if (!isShearingBlock) {
			pedestal.setActionCoolDown(Config.COMMON.blocks.pedestal.shearsWrapperCooldown.get());
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
		// noop
	}

	@Override
	public void stop(ItemStack stack, Level level, IPedestal pedestal) {
		// noop
	}

	private boolean shearBlocks(ItemStack stack, Level level, IPedestal pedestal, FakePlayer fakePlayer, BlockPos pos, int shearsRange) {
		if (!isShearingBlock) {
			if (blockQueue.isEmpty()) {
				updateQueue(stack, level, pos, shearsRange);
				if (blockQueue.isEmpty()) {
					return false;
				}
			}

			blockPosBeingSheared = blockQueue.remove();
			BlockState blockState = level.getBlockState(blockPosBeingSheared);
			if (isShearableBlock(stack, level, blockState)) {
				float hardness = blockState.getDestroySpeed(level, blockPosBeingSheared);
				float digSpeed = stack.getItem().getDestroySpeed(stack, blockState);

				pedestal.setActionCoolDown((int) ((hardness * 1.5f * 20f) / digSpeed));
				isShearingBlock = true;
			}
		} else {
			BlockState blockState = level.getBlockState(blockPosBeingSheared);
			if (stack.getItem().canDestroyBlock(stack, blockState, level, blockPosBeingSheared, fakePlayer)) {
				if (blockState.getBlock() instanceof BeehiveBlock) {
					shearBeehive(level, blockPosBeingSheared, blockState, stack);
				} else {
					if (level.removeBlock(blockPosBeingSheared, false)) {
						Block.dropResources(blockState, level, blockPosBeingSheared, null, fakePlayer, new ItemStack(Items.SHEARS));
						level.levelEvent(2001, blockPosBeingSheared, Block.getId(blockState));
						if (level instanceof ServerLevel serverLevel) {
							stack.hurtAndBreak(1, serverLevel, null, item -> {
							});
						}
					}
				}
			}

			isShearingBlock = false;
		}
		return true;
	}

	private boolean isShearableBlock(ItemStack stack, Level level, BlockState blockState) {
		Block block = blockState.getBlock();
		return (block instanceof IShearable shearable && shearable.isShearable(null, stack, level, blockPosBeingSheared))
				|| (block instanceof BeehiveBlock && blockState.getValue(BeehiveBlock.HONEY_LEVEL) >= 5);
	}

	private void shearBeehive(Level level, BlockPos pos, BlockState blockState, ItemStack stack) {
		int honeyLevel = blockState.getValue(BeehiveBlock.HONEY_LEVEL);
		if (honeyLevel >= 5) {
			level.playSound(null, pos, SoundEvents.BEEHIVE_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
			if (level instanceof ServerLevel serverLevel) {
				BeehiveBlock.dropHoneycomb(serverLevel, stack, blockState, level.getBlockEntity(pos), null, pos);
				((BeehiveBlock) blockState.getBlock()).releaseBeesAndResetHoneyLevel(level, blockState, pos, null,
						BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED);
				stack.hurtAndBreak(1, serverLevel, null, item -> {
				});
			}
		}
	}

	private void updateQueue(ItemStack stack, Level level, BlockPos pos, int shearsRange) {
		for (int y = pos.getY() - shearsRange; y <= pos.getY() + shearsRange; y++) {
			for (int x = pos.getX() - shearsRange; x <= pos.getX() + shearsRange; x++) {
				for (int z = pos.getZ() - shearsRange; z <= pos.getZ() + shearsRange; z++) {
					BlockPos currentBlockPos = new BlockPos(x, y, z);
					BlockState blockState = level.getBlockState(currentBlockPos);
					if (isShearableBlock(stack, level, blockState)) {
						blockQueue.add(currentBlockPos);
					}
				}
			}
		}
	}

	private boolean shearAnimals(ItemStack stack, Level level, FakePlayer fakePlayer, BlockPos pos, int shearsRange) {
		List<Animal> entities = level.getEntitiesOfClass(Animal.class,
				AABB.encapsulatingFullBlocks(pos.offset(-shearsRange, -shearsRange, -shearsRange), pos.offset(shearsRange, shearsRange, shearsRange)));

		for (Animal animal : entities) {
			if (animal instanceof IShearable shearable && shearable.isShearable(null, stack, level, animal.blockPosition())) {
				fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, stack);
				fakePlayer.interactOn(animal, InteractionHand.MAIN_HAND, animal.position());
				return true;
			}
		}
		return false;
	}
}
