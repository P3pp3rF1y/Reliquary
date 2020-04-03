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

public class BusterShotEntity extends ShotEntityBase {
	public BusterShotEntity(EntityType<BusterShotEntity> entityType, World world) {
		super(entityType, world);
	}

	public BusterShotEntity(World world, PlayerEntity player, Hand hand) {
		super(ModEntities.BUSTER_SHOT, world, player, hand);
	}

	@Override
	void doBurstEffect(Direction sideHit) {
		ConcussiveExplosion.customBusterExplosion(this, getPosX(), getPosY(), getPosZ(), 4.0F);
	}

	@Override
	void doFiringEffects() {
		world.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, getPosX() + smallGauss(0.1D), getPosY() + smallGauss(0.1D), getPosZ() + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
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
		ConcussiveExplosion.customBusterExplosion(this, getPosX(), getPosY(), getPosZ(), 4.0F);
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
