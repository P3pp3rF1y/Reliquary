package xreliquary.entities.shot;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import xreliquary.init.ModEntities;
import xreliquary.reference.ClientReference;

public class EnderShotEntity extends ShotEntityBase {
	public EnderShotEntity(EntityType<EnderShotEntity> entityType, World world) {
		super(entityType, world);
	}

	public EnderShotEntity(World world, PlayerEntity player, Hand hand) {
		super(ModEntities.ENDER_SHOT, world, player, hand);
	}

	private void doPortalExplosion() {
		for (int particles = 0; particles < 3; particles++) {
			spawnMotionBasedParticle(ParticleTypes.PORTAL, getPosY() - 1);
			world.addParticle(ParticleTypes.SMOKE, getPosX(), getPosY(), getPosZ(), 0, 0, 0);
		}
		remove();
	}

	@Override
	void doBurstEffect(Direction sideHit) {
		// there aren't any burst effects because ender shots pass through
		// everything.
	}

	@Override
	void doFiringEffects() {
		world.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, getPosX() + smallGauss(0.1D), getPosY() + smallGauss(0.1D), getPosZ() + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
		spawnMotionBasedParticle(ParticleTypes.FLAME);
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if (result.getType() == RayTraceResult.Type.ENTITY) {
			Entity entityHit = ((EntityRayTraceResult) result).getEntity();
			if (entityHit == shootingEntity || !(entityHit instanceof LivingEntity)) {
				return;
			}
			onImpact((LivingEntity) entityHit);
		}
		// note that there is no tile impact.
	}

	@Override
	protected void onImpact(LivingEntity mop) {
		if (mop != shootingEntity || ticksInAir > 3) {
			doDamage(mop);
		}
		spawnHitParticles(8);

		//continues after hit and thus no setting dead here
	}

	@Override
	protected void groundImpact(Direction sideHit) {
		//do absolutely nothing. this avoids a death sentence.
	}

	@Override
	void doFlightEffects() {
		if (ticksInAir % 3 == 0) {
			spawnMotionBasedParticle(ParticleTypes.PORTAL, getPosY() - 1);
		}

		// housed in the base class
		seekTarget();

		// these only last 5 seconds, to prevent them from killing too terribly
		// much.
		if (ticksInAir > 100) {
			doPortalExplosion();
		}
	}

	@Override
	void spawnHitParticles(int i) {
		for (int particles = 0; particles < i; particles++) {
			spawnMotionBasedParticle(ParticleTypes.WITCH);
		}
	}

	@Override
	int getRicochetMax() {
		return 0;
	}

	@Override
	int getDamageOfShot(LivingEntity mop) {
		return 16 + d12();
	}

	@Override
	public ResourceLocation getShotTexture() {
		return ClientReference.ENDER;
	}
}
