package xreliquary.entities.shot;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import xreliquary.entities.ConcussiveExplosion;
import xreliquary.init.ModEntities;
import xreliquary.reference.ClientReference;

public class SandShotEntity extends ShotEntityBase {
	public SandShotEntity(EntityType<SandShotEntity> entityType, World world) {
		super(entityType, world);
	}

	public SandShotEntity(World world, PlayerEntity player, Hand hand) {
		super(ModEntities.SAND_SHOT, world, player, hand);
	}

	@Override
	void doFiringEffects() {
		world.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, getPosX() + smallGauss(0.1D), getPosY() + smallGauss(0.1D), getPosZ() + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
		spawnMotionBasedParticle(ParticleTypes.FLAME);
	}

	@Override
	void doFlightEffects() {
		if (ticksInAir % 3 == 0) {
			spawnHitParticles(1);
		}
	}

	@Override
	void doBurstEffect(Direction sideHit) {
		spawnHitParticles(8);
	}

	@Override
	void spawnHitParticles(int i) {
		for (int particles = 0; particles < i; particles++) {
			world.addParticle(RedstoneParticleData.REDSTONE_DUST, getPosX(), getPosY(), getPosZ(), 0.7F, 0.7F, 0.3F);
		}
	}

	@Override
	int getDamageOfShot(LivingEntity e) {
		// creepers turn sand shots into straight explosions.
		if (e instanceof CreeperEntity) {
			ConcussiveExplosion.customBusterExplosion(this, getPosX(), getPosY(), getPosZ(), 2.0F);
			e.attackEntityFrom(DamageSource.causePlayerDamage(shootingEntity), 20);
			return 0;
		}
		// it also causes blinding
		if (e instanceof MobEntity) {
			e.addPotionEffect(new EffectInstance(Effects.BLINDNESS, 200, 1));
		}
		return (world.getWorldInfo().isRaining() ? 4 : 8) + d6();
	}

	@Override
	int getRicochetMax() {
		return 0;
	}

	@Override
	public ResourceLocation getShotTexture() {
		return ClientReference.SAND;
	}
}
