package reliquary.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import reliquary.entities.shot.ShotEntityBase;
import reliquary.reference.Settings;

import java.util.List;

public class InterdictionTorchBlock extends TorchBlock {
	protected static final int TICK_RATE = 1;

	public InterdictionTorchBlock() {
		super(Properties.of(Material.DECORATION).strength(0).lightLevel(value -> 15).randomTicks().sound(SoundType.WOOD).noCollission(), ParticleTypes.FLAME);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
		world.scheduleTick(pos, this, TICK_RATE);
		super.onPlace(state, world, pos, oldState, isMoving);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		super.tick(state, world, pos, random);
		world.scheduleTick(pos, this, TICK_RATE);
		if (world.isClientSide) {
			return;
		}
		int radius = Settings.COMMON.blocks.interdictionTorch.pushRadius.get();

		List<Entity> entities = world.getEntitiesOfClass(Entity.class, new AABB(pos).inflate(radius), e -> (e instanceof Mob || e instanceof Projectile));
		for (Entity entity : entities) {
			if (entity instanceof Player || entity instanceof ShotEntityBase || entity instanceof FishingHook || isBlacklistedEntity(entity)) {
				continue;
			}
			double distance = Math.sqrt(entity.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()));
			if (distance < radius && distance != 0) {
				moveEntity(pos, entity, distance);
			}
		}
	}

	private void moveEntity(BlockPos pos, Entity entity, double distance) {
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
		Vec3 angleOfAttack = entity.position().add(-(pos.getX() + 0.5D), -pos.getY(), -(pos.getZ() + 0.5D));

		// we use the resultant vector to determine the force to apply.
		double xForce = angleOfAttack.x * knockbackMultiplier * reductionCoefficient;
		double yForce = angleOfAttack.y * knockbackMultiplier * reductionCoefficient;
		double zForce = angleOfAttack.z * knockbackMultiplier * reductionCoefficient;
		entity.setDeltaMovement(entity.getDeltaMovement().add(xForce, yForce, zForce));
	}

	private boolean isBlacklistedEntity(Entity entity) {
		if (ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()) == null) {
			return false;
		}

		//noinspection ConstantConditions
		String entityName = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString();
		return isBlacklistedLivingEntity(entity, entityName) || Settings.COMMON.blocks.interdictionTorch.canPushProjectiles.get() && isBlacklistedProjectile(entity, entityName);
	}

	private boolean isBlacklistedProjectile(Entity entity, String entityName) {
		return entity instanceof Projectile && Settings.COMMON.blocks.interdictionTorch.pushableProjectilesBlacklist.get().contains(entityName);
	}

	private boolean isBlacklistedLivingEntity(Entity entity, String entityName) {
		return entity instanceof Mob && Settings.COMMON.blocks.interdictionTorch.pushableEntitiesBlacklist.get().contains(entityName);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
		double xOffset = pos.getX() + 0.5F;
		double yOffset = pos.getY() + 0.7F;
		double zOffset = pos.getZ() + 0.5F;
		world.addParticle(ParticleTypes.ENTITY_EFFECT, xOffset, yOffset, zOffset, 0.0D, 0.0D, 0.0D);
		world.addParticle(ParticleTypes.FLAME, xOffset, yOffset, zOffset, 0.0D, 0.0D, 0.0D);
	}
}
