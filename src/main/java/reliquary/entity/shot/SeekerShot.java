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

public class SeekerShot extends ShotBase {
	public SeekerShot(EntityType<SeekerShot> entityType, Level level) {
		super(entityType, level);
	}

	public SeekerShot(Level level, Player player, InteractionHand hand) {
		super(ModEntities.SEEKER_SHOT.get(), level, player, hand);
	}

	@Override
	void doFlightEffects() {
		if (ticksInAir % 3 == 0) {
			level().addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.0F, gaussian(1.0F), gaussian(1.0F)), getX(), getY(), getZ(), 0, 0, 0);
		}

		// housed in the base class
		seekTarget();
	}

	@Override
	void doFiringEffects() {
		level().addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.5F, 0.5F, 0.5F), getX() + smallGauss(0.1D), getY() + smallGauss(0.1D), getZ() + smallGauss(0.1D), 0, 0, 0);
		spawnMotionBasedParticle(ParticleTypes.FLAME);
	}

	@Override
	void doBurstEffect(Direction sideHit) {
		// does nothing
	}

	@Override
	void spawnHitParticles(int i) {
		for (int particles = 0; particles < i; particles++) {
			level().addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.1F, 1.0F, 1.0F), getX() + smallGauss(0.1D), getY() + smallGauss(0.1D), getZ() + smallGauss(0.1D), 0, 0, 0);
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
