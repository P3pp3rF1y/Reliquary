package reliquary.entities.shot;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import reliquary.entities.ConcussiveExplosion;
import reliquary.init.ModEntities;
import reliquary.reference.ClientReference;

public class ConcussiveShotEntity extends ShotEntityBase {
	public ConcussiveShotEntity(EntityType<ConcussiveShotEntity> entityType, Level world) {
		super(entityType, world);
	}

	public ConcussiveShotEntity(Level world, Player player, InteractionHand hand) {
		super(ModEntities.CONCUSSIVE_SHOT.get(), world, player, hand);
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
		if(ticksInAir % 3 == 0) {
			spawnMotionBasedParticle(ParticleTypes.SMOKE);
		}
	}

	@Override
	void spawnHitParticles(int i) {
		// no need
	}

	@Override
	void doFiringEffects() {
		level().addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, getX() + smallGauss(0.1D), getY() + smallGauss(0.1D), getZ() + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
		spawnMotionBasedParticle(ParticleTypes.FLAME);
	}

	@Override
	protected void onImpact(LivingEntity entityLiving) {
		if (level().isClientSide) {
			return;
		}

		getShooterPlayer().ifPresent(player -> ConcussiveExplosion.customConcussiveExplosion(this, player, position(), 1.5F, true));
		super.onImpact(entityLiving);
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
