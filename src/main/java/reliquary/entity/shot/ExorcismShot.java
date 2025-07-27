package reliquary.entity.shot;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import reliquary.init.ModEntities;
import reliquary.reference.ClientReference;

public class ExorcismShot extends ShotBase {
	public ExorcismShot(EntityType<ExorcismShot> entityType, Level level) {
		super(entityType, level);
	}

	public ExorcismShot(Level level, Player player, InteractionHand hand) {
		super(ModEntities.EXORCISM_SHOT.get(), level, player, hand);
	}

	@Override
	void doFlightEffects() {
		if (ticksInAir % 3 == 0) {
			float gauss = gaussian(1.0F);
			level().addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, gauss, gauss, 0.0F), getX(), getY(), getZ(), 0, 0, 0);
		}
	}

	@Override
	void doFiringEffects() {
		level().addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.5F, 0.5F, 0.5F), getX() + smallGauss(0.1D), getY() + smallGauss(0.1D), getZ() + smallGauss(0.1D), 0, 0, 0);
		spawnMotionBasedParticle(ParticleTypes.FLAME);
	}

	@Override
	void doBurstEffect(Direction sideHit) {
		// none really.
	}

	@Override
	void spawnHitParticles(int i) {
		for (int particles = 0; particles < i; particles++) {
			level().addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, posGauss(1.0F), posGauss(1.0F), 0.0F), getX() + smallGauss(0.1D), getY() + smallGauss(0.1D), getZ() + smallGauss(0.1D), 0, 0, 0);
		}
	}

	private boolean isUndead(LivingEntity e) {
		return e.getType().is(EntityTypeTags.UNDEAD);
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
