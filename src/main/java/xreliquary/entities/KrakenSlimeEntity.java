package xreliquary.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import xreliquary.init.ModEntities;
import xreliquary.init.ModPotions;

public class KrakenSlimeEntity extends ProjectileItemEntity {
	public KrakenSlimeEntity(EntityType<KrakenSlimeEntity> entityType, World world) {
		super(entityType, world);
	}

	public KrakenSlimeEntity(World world, PlayerEntity player) {
		super(ModEntities.KRAKEN_SLIME, player, world);
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		Entity thrower = getShooter();
		if (world.isRemote || result.getType() == RayTraceResult.Type.ENTITY && ((EntityRayTraceResult) result).getEntity() == thrower) {
			return;
		}

		if (result.getType() == RayTraceResult.Type.ENTITY && ((EntityRayTraceResult) result).getEntity() instanceof MobEntity) {
			MobEntity living = (MobEntity) ((EntityRayTraceResult) result).getEntity();
			living.attackEntityFrom(DamageSource.causeThrownDamage(this, thrower), 5.0f);
			living.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 20 * 20, 2));
			living.addPotionEffect(new EffectInstance(ModPotions.potionPacification, 15 * 20));
		}

		double motionX = getMotion().getX();
		double motionY = getMotion().getY();
		double motionZ = getMotion().getZ();
		for (int count = 0; count < 6; ++count) {
			float amplifier = 0.25F;
			world.addParticle(ParticleTypes.ITEM_SLIME, getPosX() - motionX * amplifier + world.rand.nextDouble(), getPosY() - motionY * amplifier + world.rand.nextDouble(), getPosZ() - motionZ * amplifier + world.rand.nextDouble(), motionX, motionY, motionZ);
		}
		world.playSound(null, getPosition(), SoundEvents.ENTITY_SLIME_JUMP, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.rand.nextFloat() * 0.4F + 0.8F));
		remove();
	}

	@Override
	protected float getGravityVelocity() {
		return 0.001F;
	}

	@Override
	protected Item getDefaultItem() {
		return Items.SLIME_BALL;
	}

	@Override
	public void tick() {
		super.tick();

		double motionX = getMotion().getX();
		double motionY = getMotion().getY();
		double motionZ = getMotion().getZ();
		for (int count = 0; count < 2; ++count) {
			float amplifier = 0.25F;
			world.addParticle(ParticleTypes.ITEM_SLIME, getPosX() - motionX * amplifier, getPosY() - motionY * amplifier, getPosZ() - motionZ * amplifier, motionX, motionY, motionZ);
		}

		if (world.isRemote) {
			return;
		}

		if (motionX < 0.5 && motionY == 0 && motionZ == 0) {
			remove();
			return;
		}

		if (getPosY() > world.getHeight() || getPosY() <= 0) {
			remove();
		}
	}

	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

}
