package xreliquary.entities.shot;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import xreliquary.entities.ConcussiveExplosion;
import xreliquary.init.ModEntities;
import xreliquary.reference.ClientReference;

public class ConcussiveShotEntity extends ShotEntityBase {
	public ConcussiveShotEntity(EntityType<ConcussiveShotEntity> entityType, World world) {
		super(entityType, world);
	}

	public ConcussiveShotEntity(World world, PlayerEntity player, Hand hand) {
		super(ModEntities.CONCUSSIVE_SHOT, world, player, hand);
	}

	@Override
	int getRicochetMax() {
		return 0;
	}

	@Override
	int getDamageOfShot(LivingEntity mop) {
		return 8 + d6();
	}

	@Override
	void doFlightEffects() {
		if(ticksInAir % 3 == 0) {
			spawnMotionBasedParticle(ParticleTypes.SMOKE);
		}
	}

	@Override
	void spawnHitParticles(int i) {
		// no need
	}

	@Override
	void doFiringEffects() {
		world.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, posX + smallGauss(0.1D), posY + smallGauss(0.1D), posZ + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
		spawnMotionBasedParticle(ParticleTypes.FLAME);
	}

	@Override
	protected void onImpact(LivingEntity entityLiving) {
		ConcussiveExplosion.customConcussiveExplosion(this, shootingEntity, getPositionVec(), 1.5F, true);
		super.onImpact(entityLiving);
	}

	@Override
	void doBurstEffect(Direction sideHit) {
		ConcussiveExplosion.customConcussiveExplosion(this, shootingEntity, getPositionVec(), 1.5F, true);
		remove();
	}

	@Override
	public ResourceLocation getShotTexture() {
		return ClientReference.CONCUSSIVE;
	}
}
