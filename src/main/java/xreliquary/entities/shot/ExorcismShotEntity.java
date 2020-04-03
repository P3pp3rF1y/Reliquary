package xreliquary.entities.shot;

import net.minecraft.entity.CreatureAttribute;
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

public class ExorcismShotEntity extends ShotEntityBase {
	public ExorcismShotEntity(EntityType<ExorcismShotEntity> entityType, World world) {
		super(entityType, world);
	}

	public ExorcismShotEntity(World world, PlayerEntity player, Hand hand) {
		super(ModEntities.EXORCISM_SHOT, world, player, hand);
	}

	@Override
	void doFlightEffects() {
		if(ticksInAir % 3 == 0) {
			double gauss = gaussian(1.0F);
			world.addParticle(ParticleTypes.ENTITY_EFFECT, getPosX(), getPosY(), getPosZ(), gauss, gauss, 0.0F);
		}
	}

	@Override
	void doFiringEffects() {
		world.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, getPosX() + smallGauss(0.1D), getPosY() + smallGauss(0.1D), getPosZ() + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
		spawnMotionBasedParticle(ParticleTypes.FLAME);
	}

	@Override
	void doBurstEffect(Direction sideHit) {
		// none really.
	}

	@Override
	void spawnHitParticles(int i) {
		for(int particles = 0; particles < i; particles++) {
			world.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, getPosX() + smallGauss(0.1D), getPosY() + smallGauss(0.1D), getPosZ() + smallGauss(0.1D), posGauss(1.0F), posGauss(1.0F), 0.0F);
		}
	}

	private boolean isUndead(LivingEntity e) {
		return e.getCreatureAttribute() == CreatureAttribute.UNDEAD;
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
