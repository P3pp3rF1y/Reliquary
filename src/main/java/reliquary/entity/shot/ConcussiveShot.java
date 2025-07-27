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
import reliquary.entity.ConcussiveExplosion;
import reliquary.init.ModEntities;
import reliquary.reference.ClientReference;

public class ConcussiveShot extends ShotBase {
	public ConcussiveShot(EntityType<ConcussiveShot> entityType, Level level) {
		super(entityType, level);
	}

	public ConcussiveShot(Level level, Player player, InteractionHand hand) {
		super(ModEntities.CONCUSSIVE_SHOT.get(), level, player, hand);
	}

	@Override
	int getRicochetMax() {
		return 0;
	}

	@Override
	int getDamageOfShot(LivingEntity mop) {
		return 8 + d6();
	}

	@Override
	void doFlightEffects() {
		if (ticksInAir % 3 == 0) {
			spawnMotionBasedParticle(ParticleTypes.SMOKE);
		}
	}

	@Override
	void spawnHitParticles(int i) {
		// no need
	}

	@Override
	void doFiringEffects() {
		level().addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.5F, 0.5F, 0.5F), getX() + smallGauss(0.1D), getY() + smallGauss(0.1D), getZ() + smallGauss(0.1D), 0, 0, 0);
		spawnMotionBasedParticle(ParticleTypes.FLAME);
	}

	@Override
	protected void onImpact(LivingEntity livingEntity) {
		if (level().isClientSide) {
			return;
		}

		getShooterPlayer().ifPresent(player -> ConcussiveExplosion.customConcussiveExplosion(this, player, position(), 1.5F, true));
		super.onImpact(livingEntity);
	}

	@Override
	void doBurstEffect(Direction sideHit) {
		if (level().isClientSide) {
			return;
		}
		getShooterPlayer().ifPresent(player -> ConcussiveExplosion.customConcussiveExplosion(this, player, position(), 1.5F, true));
		discard();
	}

	@Override
	public ResourceLocation getShotTexture() {
		return ClientReference.CONCUSSIVE;
	}
}
