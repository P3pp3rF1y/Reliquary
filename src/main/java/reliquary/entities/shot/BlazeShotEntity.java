package reliquary.entities.shot;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import reliquary.init.ModEntities;
import reliquary.reference.ClientReference;

public class BlazeShotEntity extends ShotEntityBase {
	public BlazeShotEntity(EntityType<BlazeShotEntity> entityType, Level world) {
		super(entityType, world);
	}

	public BlazeShotEntity(Level world, Player player, InteractionHand hand) {
		super(ModEntities.BLAZE_SHOT.get(), world, player, hand);
	}

	@Override
	void doFlightEffects() {
		if (ticksInAir % 3 == 0 && ticksInAir < 9) {
			Vec3 motion = getDeltaMovement();
			level().addParticle(ParticleTypes.FLAME, getX(), getY(), getZ(), lowGauss(motion.x()), lowGauss(motion.y()), lowGauss(motion.z()));
		}
	}

	@Override
	void doFiringEffects() {
		level().addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, getX() + smallGauss(0.1D), getY() + smallGauss(0.1D), getZ() + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
		spawnMotionBasedParticle(ParticleTypes.FLAME);
	}

	@Override
	protected DamageSource getDamageSource(LivingEntity livingEntity) {
		return (livingEntity.damageSources().source(DamageTypes.FIREBALL, getOwner(), this));
	}

	@Override
	protected void onHit(HitResult result) {
		if (result.getType() == HitResult.Type.ENTITY) {
			Entity entityHit = ((EntityHitResult) result).getEntity();
			if (entityHit == getOwner() || !(entityHit instanceof LivingEntity)) {
				return;
			}
			onImpact((LivingEntity) entityHit);
		} else if (result.getType() == HitResult.Type.BLOCK) {
			if (!(getOwner() instanceof Player)) {
				return;
			}

			BlockHitResult blockResult = (BlockHitResult) result;
			groundImpact(blockResult.getDirection());

			BlockPos fireSpawnPos = blockResult.getBlockPos().relative(blockResult.getDirection());

			if (level().isEmptyBlock(fireSpawnPos)) {
				level().setBlockAndUpdate(fireSpawnPos, BaseFireBlock.getState(level(), fireSpawnPos));
			}
		}
	}

	@Override
	protected void onImpact(LivingEntity entityLiving) {
		entityLiving.setSecondsOnFire(40);
		super.onImpact(entityLiving);
	}

	@Override
	void spawnHitParticles(int i) {
		for (int particles = 0; particles < i; particles++) {
			spawnMotionBasedParticle(ParticleTypes.FLAME);
		}
	}

	@Override
	protected void groundImpact(Direction sideHit) {
		discard();
	}

	@Override
	int getRicochetMax() {
		return 0;
	}

	@Override
	int getDamageOfShot(LivingEntity mop) {
		// they're not COMPLETELY useless against fireImmune mobs, just mostly
		// useless.
		//this probably isn't gonna work now the bullets do purely fire damage.
		return mop.fireImmune() ? 2 : (10 + d12());
	}

	@Override
	void doBurstEffect(Direction sideHit) {
		Vec3 motion = getDeltaMovement();
		double motionX = motion.x();
		double motionY = motion.y();
		double motionZ = motion.z();
		for (int particles = 0; particles < 40; particles++) {
			switch (sideHit) {
				case DOWN ->
						level().addParticle(ParticleTypes.FLAME, getX(), getY(), getZ(), smallGauss(0.1D) + motionX / 4, -posGauss(0.2D), smallGauss(0.2D) + motionZ / 4);
				case UP ->
						level().addParticle(ParticleTypes.FLAME, getX(), getY(), getZ(), smallGauss(0.1D) + motionX / 4, posGauss(0.2D), smallGauss(0.2D) + motionZ / 4);
				case NORTH ->
						level().addParticle(ParticleTypes.FLAME, getX(), getY(), getZ(), smallGauss(0.1D) + motionX / 4, smallGauss(0.1D) + motionY / 4, -posGauss(0.2D));
				case SOUTH ->
						level().addParticle(ParticleTypes.FLAME, getX(), getY(), getZ(), smallGauss(0.1D) + motionX / 4, smallGauss(0.1D) + motionY / 4, posGauss(0.2D));
				case WEST ->
						level().addParticle(ParticleTypes.FLAME, getX(), getY(), getZ(), -posGauss(0.2D), smallGauss(0.1D) + motionY / 4, smallGauss(0.1D) + motionZ / 4);
				case EAST ->
						level().addParticle(ParticleTypes.FLAME, getX(), getY(), getZ(), posGauss(0.2D), smallGauss(0.1D) + motionY / 4, smallGauss(0.1D) + motionZ / 4);
				default -> {/*noop*/}
			}
		}
	}

	@Override
	public ResourceLocation getShotTexture() {
		return ClientReference.BLAZE;
	}
}
