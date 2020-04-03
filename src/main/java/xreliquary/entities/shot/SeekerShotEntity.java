package xreliquary.entities.shot;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import xreliquary.init.ModEntities;
import xreliquary.reference.ClientReference;

public class SeekerShotEntity extends ShotEntityBase {
	public SeekerShotEntity(EntityType<SeekerShotEntity> entityType, World world) {
		super(entityType, world);
	}

	public SeekerShotEntity(World world, PlayerEntity player, Hand hand) {
		super(ModEntities.SEEKER_SHOT, world, player, hand);
	}

	@Override
	void doFlightEffects() {
		if(ticksInAir % 3 == 0) {
			world.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, getPosX(), getPosY(), getPosZ(), 0.0F, gaussian(1.0F), gaussian(1.0F));
		}

		// housed in the base class
		seekTarget();
	}

	@Override
	void doFiringEffects() {
		world.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, getPosX() + smallGauss(0.1D), getPosY() + smallGauss(0.1D), getPosZ() + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
		spawnMotionBasedParticle(ParticleTypes.FLAME);
	}

	@Override
	void doBurstEffect(Direction sideHit) {
		// does nothing
	}

	@Override
	void spawnHitParticles(int i) {
		for(int particles = 0; particles < i; particles++) {
			world.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, getPosX() + smallGauss(0.1D), getPosY() + smallGauss(0.1D), getPosZ() + smallGauss(0.1D), 0.1F, 1.0F, 1.0F);
		}
	}

	@Override
	int getRicochetMax() {
		return 3;
	}

	@Override
	int getDamageOfShot(LivingEntity mop) {
		return 10 + d12();
	}

	@Override
	public ResourceLocation getShotTexture() {
		return ClientReference.SEEKER;
	}
}
