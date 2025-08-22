package reliquary.item;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import reliquary.init.ModDataComponents;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("java:S110")
public class GlacialStaffItem extends IceMagusRodItem {
	public GlacialStaffItem() {
		super();
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, Player player, Entity e) {
		if (e instanceof LivingEntity livingBase && getSnowballs(stack) >= getSnowballCost()) {
			MobEffectInstance slow = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 0);

			//if the creature is slowed already, refresh the duration and increase the amplifier by 1.
			//5 hits is all it takes to max out the amplitude.
			MobEffectInstance slownessEffect = livingBase.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
			if (slownessEffect != null) {
				slow = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, Math.min(slownessEffect.getDuration() + 30, 300),
						Math.min(slownessEffect.getAmplifier() + 1, 4));
			}

			((LivingEntity) e).addEffect(slow);
			e.hurt(player.damageSources().playerAttack(player), slow.getAmplifier());
			useCharge(stack, getSnowballCost());
		}
		return super.onLeftClickEntity(stack, player, e);
	}

	@Override
	public void inventoryTick(ItemStack staff, Level level, Entity entity, int itemSlot, boolean isSelected) {
		super.inventoryTick(staff, level, entity, itemSlot, isSelected);

		if (level.isClientSide() || !(entity instanceof Player player) || player.isSpectator() || level.getGameTime() % 2 != 0) {
			return;
		}

		if (isEnabled(staff)) {
			freezeBlocks(staff, level, player);
		}
		meltBlocks(staff, level, player);
	}

	private void freezeBlocks(ItemStack staff, Level level, Player player) {
		BlockPos playerPos = player.blockPosition();
		BlockPos.betweenClosed(playerPos.offset(-2, -1, -2), playerPos.offset(2, -1, 2))
				.forEach(pos -> {
					if (Math.abs(playerPos.getX() - pos.getX()) == 2 && Math.abs(playerPos.getZ() - pos.getZ()) == 2) {
						return;
					}
					doFreezeCheck(staff, pos.immutable(), level);
				});
	}

	private void meltBlocks(ItemStack staff, Level level, Player player) {
		if (!level.isClientSide) {
			BlockPos playerPos = player.blockPosition();
			for (BlockPos pos : getBlockLocations(staff)) {
				if (!level.isLoaded(pos)) {
					continue;
				}

				int xOff = Math.abs(playerPos.getX() - pos.getX());
				int yOff = Math.abs(playerPos.getY() - pos.getY());
				int zOff = Math.abs(playerPos.getZ() - pos.getZ());

				if (xOff < 3 && yOff < 3 && zOff < 3 && !(xOff == 2 && zOff == 2)) {
					continue;
				}

				doThawCheck(staff, pos, level);
			}
		}
	}

	private Set<BlockPos> getBlockLocations(ItemStack stack) {
		return stack.getOrDefault(ModDataComponents.FROZEN_POSITIONS, new HashSet<>());
	}

	private void doFreezeCheck(ItemStack stack, BlockPos pos, Level level) {
		BlockState blockState = level.getBlockState(pos);
		if (blockState.getBlock() == Blocks.WATER && blockState.getValue(LiquidBlock.LEVEL) == 0 && level.isEmptyBlock(pos.above())) {
			addFrozenBlockToList(stack, pos);
			level.setBlockAndUpdate(pos, Blocks.PACKED_ICE.defaultBlockState());
			for (int particleNum = level.random.nextInt(3); particleNum < 2; ++particleNum) {
				float xVel = level.random.nextFloat();
				float yVel = level.random.nextFloat() + 0.5F;
				float zVel = level.random.nextFloat();
				level.addParticle(ICE_PARTICLE, pos.getX() + xVel, pos.getY() + yVel, pos.getZ() + zVel, 0.75F, 0.75F, 1.0F);
			}
		} else if (blockState.getBlock() == Blocks.LAVA && blockState.getValue(LiquidBlock.LEVEL) == 0) {
			addFrozenBlockToList(stack, pos);
			level.setBlockAndUpdate(pos, Blocks.OBSIDIAN.defaultBlockState());
			for (int particleNum = level.random.nextInt(3); particleNum < 2; ++particleNum) {
				float xVel = level.random.nextFloat();
				float yVel = level.random.nextFloat() + 0.5F;
				float zVel = level.random.nextFloat();
				level.addParticle(level.random.nextInt(3) == 0 ? ParticleTypes.LARGE_SMOKE : ParticleTypes.SMOKE, pos.getX() + xVel, pos.getY() + yVel, pos.getZ() + zVel, 0.0D, 0.2D, 0.0D);
			}
		}
	}

	private void doThawCheck(ItemStack stack, BlockPos pos, Level level) {
		BlockState blockState = level.getBlockState(pos);
		if (blockState == Blocks.PACKED_ICE.defaultBlockState()) {
			if (removeFrozenBlockFromList(stack, pos)) {
				level.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
				for (int particleNum = level.random.nextInt(3); particleNum < 2; ++particleNum) {
					float xVel = level.random.nextFloat();
					float yVel = level.random.nextFloat() + 0.5F;
					float zVel = level.random.nextFloat();
					level.addParticle(level.random.nextInt(3) == 0 ? ParticleTypes.LARGE_SMOKE : ParticleTypes.SMOKE, pos.getX() + xVel, pos.getY() + yVel, pos.getZ() + zVel, 0.0D, 0.2D, 0.0D);
				}
			}
		} else if (blockState == Blocks.OBSIDIAN.defaultBlockState() && removeFrozenBlockFromList(stack, pos)) {
			level.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
			for (int particleNum = level.random.nextInt(3); particleNum < 2; ++particleNum) {
				float xVel = level.random.nextFloat();
				float yVel = level.random.nextFloat() + 0.5F;
				float zVel = level.random.nextFloat();
				level.addParticle(DustParticleOptions.REDSTONE, pos.getX() + xVel, pos.getY() + yVel, pos.getZ() + zVel, 0F, 0.2F, 0F);
			}
		}
	}

	private void addFrozenBlockToList(ItemStack stack, BlockPos pos) {
		HashSet<BlockPos> frozenPositions = new HashSet<>(getBlockLocations(stack));
		frozenPositions.add(pos);

		stack.set(ModDataComponents.FROZEN_POSITIONS, ImmutableSet.copyOf(frozenPositions));
	}

	private boolean removeFrozenBlockFromList(ItemStack stack, BlockPos pos) {
		HashSet<BlockPos> frozenPositions = new HashSet<>(getBlockLocations(stack));
		if (frozenPositions.remove(pos)) {
			stack.set(ModDataComponents.FROZEN_POSITIONS, ImmutableSet.copyOf(frozenPositions));
			return true;
		}
		return false;
	}
}
