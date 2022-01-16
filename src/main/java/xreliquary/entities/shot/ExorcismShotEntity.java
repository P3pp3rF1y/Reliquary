package xreliquary.entities.shot;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import xreliquary.init.ModEntities;
import xreliquary.reference.ClientReference;

public class ExorcismShotEntity extends ShotEntityBase {
	public ExorcismShotEntity(EntityType<ExorcismShotEntity> entityType, Level world) {
		super(entityType, world);
	}

	public ExorcismShotEntity(Level world, Player player, InteractionHand hand) {
		super(ModEntities.EXORCISM_SHOT.get(), world, player, hand);
	}

	@Override
	void doFlightEffects() {
		if(ticksInAir % 3 == 0) {
			double gauss = gaussian(1.0F);
			level.addParticle(ParticleTypes.ENTITY_EFFECT, getX(), getY(), getZ(), gauss, gauss, 0.0F);
		}
	}

	@Override
	void doFiringEffects() {
		level.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, getX() + smallGauss(0.1D), getY() + smallGauss(0.1D), getZ() + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
		spawnMotionBasedParticle(ParticleTypes.FLAME);
	}

	@Override
	void doBurstEffect(Direction sideHit) {
		// none really.
	}

	@Override
	void spawnHitParticles(int i) {
		for(int particles = 0; particles < i; particles++) {
			level.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, getX() + smallGauss(0.1D), getY() + smallGauss(0.1D), getZ() + smallGauss(0.1D), posGauss(1.0F), posGauss(1.0F), 0.0F);
		}
	}

	private boolean isUndead(LivingEntity e) {
		return e.getMobType() == MobType.UNDEAD;
	}

	@Override
	int getRicochetMax() {
		return 1;
	}

	@Override
	int getDamageOfShot(LivingEntity e) {
		// there is a relatively small chance this will fail to kill an undead
		// creature.
		// undead take 10-12 damage, in addition to the 9-14 damage they would
		// normally do.
		return (isUndead(e) ? (9 + d3()) : 0) + 8 + d6();
	}

	@Override
	public ResourceLocation getShotTexture() {
		return ClientReference.EXORCISM;
	}
}
