package xreliquary.entities.shot;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import xreliquary.entities.ConcussiveExplosion;
import xreliquary.init.ModEntities;
import xreliquary.reference.ClientReference;

public class BusterShotEntity extends ShotEntityBase {
	public BusterShotEntity(EntityType<BusterShotEntity> entityType, Level world) {
		super(entityType, world);
	}

	public BusterShotEntity(Level world, Player player, InteractionHand hand) {
		super(ModEntities.BUSTER_SHOT.get(), world, player, hand);
	}

	@Override
	void doBurstEffect(Direction sideHit) {
		ConcussiveExplosion.customBusterExplosion(this, getX(), getY(), getZ(), 4.0F);
	}

	@Override
	void doFiringEffects() {
		level.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, getX() + smallGauss(0.1D), getY() + smallGauss(0.1D), getZ() + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
		spawnMotionBasedParticle(ParticleTypes.FLAME);
	}

	@Override
	void doFlightEffects() {
		if (ticksInAir % 3 == 0) {
			spawnMotionBasedParticle(ParticleTypes.SMOKE);
		}
	}

	@Override
	void spawnHitParticles(int i) {
		// no need, really.
	}

	@Override
	protected void onImpact(LivingEntity entityLiving) {
		ConcussiveExplosion.customBusterExplosion(this, getX(), getY(), getZ(), 4.0F);
		super.onImpact(entityLiving);
	}

	@Override
	int getRicochetMax() {
		// uh.. yeah, buster shots don't ricochet.
		return 0;
	}

	@Override
	int getDamageOfShot(LivingEntity mop) {
		// if the damage doesn't kill them, the explosion will.
		// Buster shots are almost guaranteed fatal.
		return 8 + d6();
	}

	@Override
	public ResourceLocation getShotTexture() {
		return ClientReference.BUSTER;
	}
}
