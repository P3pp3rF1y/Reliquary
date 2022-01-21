package reliquary.entities.shot;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import reliquary.init.ModEntities;
import reliquary.reference.ClientReference;

public class NeutralShotEntity extends ShotEntityBase {
	public NeutralShotEntity(EntityType<NeutralShotEntity> entityType, Level world) {
		super(entityType, world);
	}

	public NeutralShotEntity(Level world, Player player, InteractionHand hand) {
		super(ModEntities.NEUTRAL_SHOT.get(), world, player, hand);
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
		level.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, getX() + smallGauss(0.1D), getY() + smallGauss(0.1D), getZ() + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
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
