package reliquary.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import reliquary.init.ModEffects;
import reliquary.init.ModEntities;

public class KrakenSlime extends ThrowableItemProjectile {
	public KrakenSlime(EntityType<KrakenSlime> entityType, Level level) {
		super(entityType, level);
	}

	public KrakenSlime(Level level, Player player) {
		super(ModEntities.KRAKEN_SLIME.get(), player, level);
	}

	@Override
	protected void onHit(HitResult result) {
		Entity thrower = getOwner();
		if (level().isClientSide || result.getType() == HitResult.Type.ENTITY && ((EntityHitResult) result).getEntity() == thrower) {
			return;
		}

		if (result.getType() == HitResult.Type.ENTITY && ((EntityHitResult) result).getEntity() instanceof Mob living) {
			living.hurt(damageSources().thrown(this, thrower), 5.0f);
			living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 20, 2));
			living.addEffect(new MobEffectInstance(ModEffects.PACIFICATION, 15 * 20));
		}

		double motionX = getDeltaMovement().x();
		double motionY = getDeltaMovement().y();
		double motionZ = getDeltaMovement().z();
		for (int count = 0; count < 6; ++count) {
			float amplifier = 0.25F;
			level().addParticle(ParticleTypes.ITEM_SLIME, getX() - motionX * amplifier + level().random.nextDouble(), getY() - motionY * amplifier + level().random.nextDouble(), getZ() - motionZ * amplifier + level().random.nextDouble(), motionX, motionY, motionZ);
		}
		level().playSound(null, blockPosition(), SoundEvents.SLIME_JUMP, SoundSource.NEUTRAL, 0.5F, 0.4F / (level().random.nextFloat() * 0.4F + 0.8F));
		discard();
	}

	@Override
	protected double getDefaultGravity() {
		return 0.001F;
	}

	@Override
	protected Item getDefaultItem() {
		return Items.SLIME_BALL;
	}

	@Override
	public void tick() {
		super.tick();

		double motionX = getDeltaMovement().x();
		double motionY = getDeltaMovement().y();
		double motionZ = getDeltaMovement().z();
		for (int count = 0; count < 2; ++count) {
			float amplifier = 0.25F;
			level().addParticle(ParticleTypes.ITEM_SLIME, getX() - motionX * amplifier, getY() - motionY * amplifier, getZ() - motionZ * amplifier, motionX, motionY, motionZ);
		}

		if (level().isClientSide) {
			return;
		}

		if (motionX < 0.5 && motionY == 0 && motionZ == 0) {
			discard();
			return;
		}

		if (getY() > level().getMaxBuildHeight() || getY() <= 0) {
			discard();
		}
	}
}
