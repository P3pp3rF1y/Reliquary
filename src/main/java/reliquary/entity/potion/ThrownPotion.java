package reliquary.entity.potion;

import net.minecraft.core.Position;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import reliquary.init.ModEntities;
import reliquary.init.ModItems;
import reliquary.item.util.IPotionItem;
import reliquary.network.SpawnThrownPotionImpactParticlesPayload;
import reliquary.util.potions.PotionHelper;

import java.util.List;

public class ThrownPotion extends ThrowableItemProjectile {
	public ThrownPotion(EntityType<ThrownPotion> entityType, Level level) {
		super(entityType, level);
	}

	public ThrownPotion(Level level, Position position, ItemStack potion) {
		super(ModEntities.THROWN_POTION.get(), position.x(), position.y(), position.z(), level, potion);
	}

	public ThrownPotion(Level level, LivingEntity entity, ItemStack potion) {
		super(ModEntities.THROWN_POTION.get(), entity, level, potion);
	}

	/**
	 * Gets the amount of gravity to apply to the thrown entity with each tick.
	 */
	@Override
	protected double getDefaultGravity() {
		return 0.04D;
	}

	// no clue what these do

	/**
	 * Called when this EntityThrowable hits a block or entity.
	 */
	@Override
	protected void onHit(HitResult result) {
		if (!level().isClientSide()) {
			ItemStack potion = getItem();
			if (!(potion.getItem() instanceof IPotionItem)) {
				return;
			}
			PotionContents potionContents = ((IPotionItem) potion.getItem()).getPotionContents(potion);

			int color = potionContents.getColor();
			if (potion.getItem() == ModItems.LINGERING_POTION.get()) {
				spawnAreaEffectCloud(potionContents);
			} else {
				splashPotion(result, potionContents);
			}

			spawnParticles(color);
			discard();
		}
	}

	private void splashPotion(HitResult result, PotionContents potionContents) {
		AABB axisalignedbb = getBoundingBox().inflate(4.0D, 2.0D, 4.0D);
		List<LivingEntity> livingEntities = level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);

		if (!livingEntities.isEmpty()) {
			for (LivingEntity entity : livingEntities) {
				double distance = distanceTo(entity);

				if (distance < 16.0D) {
					double amplifier = 1.0D - Math.sqrt(distance) / 4.0D;

					if (result.getType() == HitResult.Type.ENTITY && entity == ((EntityHitResult) result).getEntity()) {
						amplifier = 1.0D;
					}

					PotionHelper.applyEffectsToEntity(potionContents, this, getOwner(), entity, amplifier);
				}
			}
		}
	}

	private void spawnAreaEffectCloud(PotionContents potionContents) {
		AreaEffectCloud areaEffectCloud = new AreaEffectCloud(level(), getX(), getY(), getZ());
		if (getOwner() instanceof LivingEntity livingEntity) {
			areaEffectCloud.setOwner(livingEntity);
		}
		areaEffectCloud.setRadius(3.0F);
		areaEffectCloud.setRadiusOnUse(-0.5F);
		areaEffectCloud.setDuration(600);
		areaEffectCloud.setWaitTime(10);
		areaEffectCloud.setRadiusPerTick(-areaEffectCloud.getRadius() / areaEffectCloud.getDuration());
		areaEffectCloud.setPotionContents(potionContents);
		level().addFreshEntity(areaEffectCloud);
	}

	private void spawnParticles(int color) {
		if (level().isClientSide()) {
			return;
		}

		RandomSource var7 = random;
		for (int var15 = 0; var15 < 8; ++var15) {
			level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, getItem()), getX(), getY(), getZ(), var7.nextGaussian() * 0.15D,
					var7.nextDouble() * 0.2D, var7.nextGaussian() * 0.15D);
		}

		level().playSound(null, blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0F, level().random.nextFloat() * 0.1F + 0.9F);
		PacketDistributor.sendToPlayersTrackingEntity(this, new SpawnThrownPotionImpactParticlesPayload(color, getX(), getY(), getZ()));
	}

	@Override
	protected Item getDefaultItem() {
		return ModItems.SPLASH_POTION.get();
	}

	@Override
	public void addAdditionalSaveData(ValueOutput out) {
		super.addAdditionalSaveData(out);
		out.storeNullable("item", ItemStack.CODEC, getItem());
	}

	@Override
	public void readAdditionalSaveData(ValueInput in) {
		super.readAdditionalSaveData(in);
		setItem(in.read("item", ItemStack.CODEC).orElseGet(() -> new ItemStack(ModItems.POTION.get())));
	}
}
