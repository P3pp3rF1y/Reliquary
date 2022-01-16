package xreliquary.entities.potion;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import xreliquary.init.ModEntities;
import xreliquary.init.ModItems;
import xreliquary.items.util.IPotionItem;
import xreliquary.network.PacketFXThrownPotionImpact;
import xreliquary.network.PacketHandler;
import xreliquary.util.potions.XRPotionHelper;

import java.util.List;
import java.util.Random;

@OnlyIn(
		value = Dist.CLIENT,
		_interface = ItemSupplier.class
)
public class ThrownXRPotionEntity extends ThrowableProjectile implements IEntityAdditionalSpawnData, ItemSupplier {
	private static final EntityDataAccessor<ItemStack> ITEM = SynchedEntityData.defineId(ThrownPotionEntity.class, EntityDataSerializers.ITEM_STACK);

	public ThrownXRPotionEntity(EntityType<ThrownXRPotionEntity> entityType, Level world) {
		super(entityType, world);
	}

	public ThrownXRPotionEntity(Level world, double x, double y, double z, ItemStack potion) {
		super(ModEntities.THROWN_POTION.get(), x, y, z, world);
		setItem(potion);
	}

	public ThrownXRPotionEntity(Level world, LivingEntity entity, ItemStack potion) {
		super(ModEntities.THROWN_POTION.get(), entity, world);
		setItem(potion);
	}

	/**
	 * Gets the amount of gravity to apply to the thrown entity with each tick.
	 */
	@Override
	protected float getGravity() {
		return 0.04F;
	}

	//no clue what these do

	/**
	 * Called when this EntityThrowable hits a block or entity.
	 */
	@Override
	protected void onHit(HitResult result) {
		if (!level.isClientSide) {
			ItemStack potion = getItem();
			if (!(potion.getItem() instanceof IPotionItem)) {
				return;
			}
			List<MobEffectInstance> effects = ((IPotionItem) potion.getItem()).getEffects(potion);

			int color = PotionUtils.getColor(effects);
			if (potion.getItem() == ModItems.LINGERING_POTION.get()) {
				spawnAreaEffectCloud(effects, color);
			} else {
				splashPotion(result, effects);
			}

			spawnParticles(color);
			discard();
		}
	}

	private void splashPotion(HitResult result, List<MobEffectInstance> effects) {
		AABB axisalignedbb = getBoundingBox().inflate(4.0D, 2.0D, 4.0D);
		List<LivingEntity> livingEntities = level.getEntitiesOfClass(LivingEntity.class, axisalignedbb);

		if (!livingEntities.isEmpty()) {
			for (LivingEntity entity : livingEntities) {
				double d0 = distanceTo(entity);

				if (d0 < 16.0D) {
					double d1 = 1.0D - Math.sqrt(d0) / 4.0D;

					if (result.getType() == HitResult.Type.ENTITY && entity == ((EntityHitResult) result).getEntity()) {
						d1 = 1.0D;
					}

					XRPotionHelper.applyEffectsToEntity(effects, this, getOwner(), entity, d1);
				}
			}
		}
	}

	private void spawnAreaEffectCloud(List<MobEffectInstance> effects, int color) {
		Entity thrower = getOwner();
		if (!(thrower instanceof LivingEntity)) {
			return;
		}
		AreaEffectCloud areaEffectCloud = new AreaEffectCloud(level, getX(), getY(), getZ());
		areaEffectCloud.setOwner((LivingEntity) thrower);
		areaEffectCloud.setRadius(3.0F);
		areaEffectCloud.setRadiusOnUse(-0.5F);
		areaEffectCloud.setWaitTime(10);
		areaEffectCloud.setRadiusPerTick(-areaEffectCloud.getRadius() / areaEffectCloud.getDuration());
		areaEffectCloud.setFixedColor(color);

		for (MobEffectInstance potionEffect : effects) {
			areaEffectCloud.addEffect(new MobEffectInstance(potionEffect.getEffect(), potionEffect.getDuration(), potionEffect.getAmplifier()));
		}

		level.addFreshEntity(areaEffectCloud);
	}

	private void spawnParticles(int color) {
		if (level.isClientSide) {
			return;
		}

		Random var7 = random;
		for (int var15 = 0; var15 < 8; ++var15) {
			level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, getItem()), getX(), getY(), getZ(), var7.nextGaussian() * 0.15D, var7.nextDouble() * 0.2D, var7.nextGaussian() * 0.15D);
		}

		level.playSound(null, blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.1F + 0.9F);
		PacketHandler.sendToAllAround(new PacketFXThrownPotionImpact(color, getX(), getY(), getZ()), new PacketDistributor.TargetPoint(getX(), getY(), getZ(), 32.0D, level.dimension()));
	}

	@Override
	public void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);
		nbt.put("item", getItem().save(new CompoundTag()));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
		super.readAdditionalSaveData(nbt);
		setItem(ItemStack.of(nbt.getCompound("item")));
	}

	@Override
	protected void defineSynchedData() {
		entityData.define(ITEM, ItemStack.EMPTY);
	}

	@Override
	public ItemStack getItem() {
		return entityData.get(ITEM);
	}

	private void setItem(ItemStack stack) {
		entityData.set(ITEM, stack);
	}

	@Override
	public void writeSpawnData(FriendlyByteBuf buffer) {
		buffer.writeItem(getItem());
	}

	@Override
	public void readSpawnData(FriendlyByteBuf additionalData) {
		setItem(additionalData.readItem());
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}

