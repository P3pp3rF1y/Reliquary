package reliquary.entities;

import com.google.common.collect.Lists;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import reliquary.init.ModEntities;
import reliquary.init.ModItems;
import reliquary.util.potions.XRPotionHelper;

import java.util.List;

@SuppressWarnings("squid:S2160")
public class XRTippedArrowEntity extends AbstractArrow {
	private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(XRTippedArrowEntity.class, EntityDataSerializers.INT);
	private List<MobEffectInstance> effects = Lists.newArrayList();

	public XRTippedArrowEntity(EntityType<XRTippedArrowEntity> entityType, Level world) {
		super(entityType, world);
	}

	public XRTippedArrowEntity(Level world, double x, double y, double z) {
		super(ModEntities.TIPPED_ARROW.get(), x, y, z, world);
	}

	public XRTippedArrowEntity(Level world, LivingEntity shooter) {
		super(ModEntities.TIPPED_ARROW.get(), shooter, world);
	}

	public void setPotionEffect(ItemStack stack) {
		effects = XRPotionHelper.getPotionEffectsFromStack(stack);

		entityData.set(COLOR, PotionUtils.getColor(effects));
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(COLOR, 0);
	}

	@Override
	public void tick() {
		super.tick();

		if (level.isClientSide) {
			if (inGround) {
				if (inGroundTime % 5 == 0) {
					spawnPotionParticles(1);
				}
			} else {
				spawnPotionParticles(2);
			}
		} else if (inGround && inGroundTime != 0 && !effects.isEmpty() && inGroundTime >= 600) {
			level.broadcastEntityEvent(this, (byte) 0);
			effects.clear();
			entityData.set(COLOR, 0);
		}
	}

	private void spawnPotionParticles(int particleCount) {
		int i = getColor();

		if (i != 0 && particleCount > 0) {
			double d0 = (i >> 16 & 255) / 255.0D;
			double d1 = (i >> 8 & 255) / 255.0D;
			double d2 = (i & 255) / 255.0D;

			for (int j = 0; j < particleCount; ++j) {
				level.addParticle(ParticleTypes.ENTITY_EFFECT, getX() + (random.nextDouble() - 0.5D) * getBbWidth(), getY() + random.nextDouble() * getBbHeight(), getZ() + (random.nextDouble() - 0.5D) * getBbWidth(), d0, d1, d2);
			}
		}
	}

	public int getColor() {
		return entityData.get(COLOR);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		XRPotionHelper.addPotionEffectsToCompoundTag(compound, effects);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		effects = XRPotionHelper.getPotionEffectsFromCompoundTag(compound);

		if (!effects.isEmpty()) {
			entityData.set(COLOR, PotionUtils.getColor(effects));
		}
	}

	@Override
	protected void doPostHurtEffects(LivingEntity living) {
		super.doPostHurtEffects(living);
		Entity shooter = getOwner();
		if (shooter != null) {
			XRPotionHelper.applyEffectsToEntity(effects, this, shooter, living);
		}
	}

	@Override
	protected ItemStack getPickupItem() {
		if (effects.isEmpty()) {
			return new ItemStack(Items.ARROW);
		} else {
			ItemStack itemstack = new ItemStack(ModItems.TIPPED_ARROW.get());
			XRPotionHelper.addPotionEffectsToStack(itemstack, effects);
			return itemstack;
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleEntityEvent(byte id) {
		if (id == 0) {
			int i = getColor();

			if (i > 0) {
				double d0 = (i >> 16 & 255) / 255.0D;
				double d1 = (i >> 8 & 255) / 255.0D;
				double d2 = (i & 255) / 255.0D;

				for (int j = 0; j < 20; ++j) {
					level.addParticle(ParticleTypes.ENTITY_EFFECT, getX() + (random.nextDouble() - 0.5D) * getBbWidth(), getY() + random.nextDouble() * getBbHeight(), getZ() + (random.nextDouble() - 0.5D) * getBbWidth(), d0, d1, d2);
				}
			}
		} else {
			super.handleEntityEvent(id);
		}
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
