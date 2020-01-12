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

public class NeutralShotEntity extends ShotEntityBase {
	public NeutralShotEntity(EntityType<NeutralShotEntity> entityType, World world) {
		super(entityType, world);
	}

	public NeutralShotEntity(World world, PlayerEntity player, Hand hand) {
		super(ModEntities.NEUTRAL_SHOT, world, player, hand);
	}

	@Override
	int getRicochetMax() {
		return 1;
	}

	@Override
	int getDamageOfShot(LivingEntity mop) {
		return 8 + d6();
	}

	@Override
	void doFiringEffects() {
		world.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, posX + smallGauss(0.1D), posY + smallGauss(0.1D), posZ + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
		spawnMotionBasedParticle(ParticleTypes.FLAME);
	}

	@Override
	void doFlightEffects() {
		// none for neutral
	}

	@Override
	void doBurstEffect(Direction sideHit) {
		// none for neutral
	}

	@Override
	void spawnHitParticles(int i) {
		for(int particles = 0; particles < i; particles++) {
			spawnMotionBasedParticle(ParticleTypes.ENCHANTED_HIT);
		}
	}

	@Override
	public ResourceLocation getShotTexture() {
		return ClientReference.NEUTRAL;
	}
}
