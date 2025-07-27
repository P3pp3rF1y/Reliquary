package reliquary.entity.shot;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import reliquary.init.ModEntities;
import reliquary.reference.ClientReference;

public class NeutralShot extends ShotBase {
	public NeutralShot(EntityType<NeutralShot> entityType, Level level) {
		super(entityType, level);
	}

	public NeutralShot(Level level, Player player, InteractionHand hand) {
		super(ModEntities.NEUTRAL_SHOT.get(), level, player, hand);
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
		level().addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.5F, 0.5F, 0.5F), getX() + smallGauss(0.1D), getY() + smallGauss(0.1D), getZ() + smallGauss(0.1D), 0, 0, 0);
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
