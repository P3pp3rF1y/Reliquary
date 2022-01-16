package xreliquary.entities.shot;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import xreliquary.init.ModEntities;
import xreliquary.reference.ClientReference;

public class SeekerShotEntity extends ShotEntityBase {
	public SeekerShotEntity(EntityType<SeekerShotEntity> entityType, Level world) {
		super(entityType, world);
	}

	public SeekerShotEntity(Level world, Player player, InteractionHand hand) {
		super(ModEntities.SEEKER_SHOT.get(), world, player, hand);
	}

	@Override
	void doFlightEffects() {
		if(ticksInAir % 3 == 0) {
			level.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, getX(), getY(), getZ(), 0.0F, gaussian(1.0F), gaussian(1.0F));
		}

		// housed in the base class
		seekTarget();
	}

	@Override
	void doFiringEffects() {
		level.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, getX() + smallGauss(0.1D), getY() + smallGauss(0.1D), getZ() + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
		spawnMotionBasedParticle(ParticleTypes.FLAME);
	}

	@Override
	void doBurstEffect(Direction sideHit) {
		// does nothing
	}

	@Override
	void spawnHitParticles(int i) {
		for(int particles = 0; particles < i; particles++) {
			level.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, getX() + smallGauss(0.1D), getY() + smallGauss(0.1D), getZ() + smallGauss(0.1D), 0.1F, 1.0F, 1.0F);
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
