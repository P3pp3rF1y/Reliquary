package xreliquary.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import xreliquary.reference.Settings;

import java.util.List;
import java.util.Random;

public class InterdictionTorchBlock extends TorchBlock {
	protected static final int TICK_RATE = 1;

	public InterdictionTorchBlock() {
		super(Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(0).setLightLevel(value -> 15).tickRandomly().sound(SoundType.WOOD).doesNotBlockMovement(), ParticleTypes.FLAME);
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
		world.getPendingBlockTicks().scheduleTick(pos, this, TICK_RATE);
		super.onBlockAdded(state, world, pos, oldState, isMoving);
	}

	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		super.tick(state, world, pos, random);
		world.getPendingBlockTicks().scheduleTick(pos, this, TICK_RATE);
		if (world.isRemote) {
			return;
		}
		int radius = Settings.COMMON.blocks.interdictionTorch.pushRadius.get();

		List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos).grow(radius), e -> (e instanceof MobEntity || e instanceof ProjectileEntity));
		for (Entity entity : entities) {
			if (entity instanceof PlayerEntity) {
				continue;
			}
			double distance = Math.sqrt(entity.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()));
			if (distance < radius && distance != 0) {
				if (isBlacklistedEntity(entity)) {
					continue;
				}

				// the multiplier is based on a set rate added to an inverse
				// proportion to the distance.
				// we raise the distance to 1 if it's less than one, or it becomes a
				// crazy multiplier we don't want/need.
				if (distance < 1D) {
					distance = 1D;
				}
				double knockbackMultiplier = 1D + (1D / distance);

				// we also need a reduction coefficient because the above force is
				// WAY TOO MUCH to apply every tick.
				double reductionCoefficient = 0.04D;

				// the resultant vector between the two 3d coordinates is the
				// difference of each coordinate pair
				// note that we do not add 0.5 to the y coord, if we wanted to be
				// SUPER accurate, we would be using
				// the entity height offset to find its "center of mass"
				Vector3d angleOfAttack = entity.getPositionVec().add(-(pos.getX() + 0.5D), -pos.getY(), -(pos.getZ() + 0.5D));

				// we use the resultant vector to determine the force to apply.
				double xForce = angleOfAttack.x * knockbackMultiplier * reductionCoefficient;
				double yForce = angleOfAttack.y * knockbackMultiplier * reductionCoefficient;
				double zForce = angleOfAttack.z * knockbackMultiplier * reductionCoefficient;
				entity.setMotion(entity.getMotion().add(xForce, yForce, zForce));
			}
		}
	}

	private boolean isBlacklistedEntity(Entity entity) {
		if (ForgeRegistries.ENTITIES.getKey(entity.getType()) == null) {
			return false;
		}

		//noinspection ConstantConditions
		String entityName = ForgeRegistries.ENTITIES.getKey(entity.getType()).toString();
		return isBlacklistedLivingEntity(entity, entityName) || Settings.COMMON.blocks.interdictionTorch.canPushProjectiles.get() && isBlacklistedProjectile(entity, entityName);
	}

	private boolean isBlacklistedProjectile(Entity entity, String entityName) {
		return entity instanceof ProjectileEntity && Settings.COMMON.blocks.interdictionTorch.pushableProjectilesBlacklist.get().contains(entityName);
	}

	private boolean isBlacklistedLivingEntity(Entity entity, String entityName) {
		return entity instanceof MobEntity && Settings.COMMON.blocks.interdictionTorch.pushableEntitiesBlacklist.get().contains(entityName);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
		double xOffset = (float) pos.getX() + 0.5F;
		double yOffset = (float) pos.getY() + 0.7F;
		double zOffset = (float) pos.getZ() + 0.5F;
		world.addParticle(ParticleTypes.ENTITY_EFFECT, xOffset, yOffset, zOffset, 0.0D, 0.0D, 0.0D);
		world.addParticle(ParticleTypes.FLAME, xOffset, yOffset, zOffset, 0.0D, 0.0D, 0.0D);
	}
}
