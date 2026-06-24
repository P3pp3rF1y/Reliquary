package reliquary.entity;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import reliquary.init.ModEntities;
import reliquary.init.ModItems;
import reliquary.util.potions.PotionHelper;

import javax.annotation.Nullable;

@SuppressWarnings("squid:S2160")
public class TippedArrow extends AbstractArrow {
	private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(TippedArrow.class, EntityDataSerializers.INT);
	private PotionContents potionContents = PotionContents.EMPTY;

	public TippedArrow(EntityType<TippedArrow> entityType, Level level) {
		super(entityType, level);
	}

	public TippedArrow(Level level, LivingEntity shooter, ItemStack pickupItemStack, @Nullable ItemStack projectileWeaponStack) {
		super(ModEntities.TIPPED_ARROW.get(), shooter, level, pickupItemStack, projectileWeaponStack);
	}

	@Override
	protected ItemStack getDefaultPickupItem() {
		return new ItemStack(ModItems.TIPPED_ARROW.get());
	}

	public void setPotionEffect(ItemStack stack) {
		potionContents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
		entityData.set(COLOR, potionContents.getColor());
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(COLOR, 0);
	}

	@Override
	public void tick() {
		super.tick();

		if (level().isClientSide()) {
			if (isInGround()) {
				if (inGroundTime % 5 == 0) {
					spawnPotionParticles(1);
				}
			} else {
				spawnPotionParticles(2);
			}
		} else if (isInGround() && inGroundTime != 0 && potionContents.hasEffects() && inGroundTime >= 600) {
			level().broadcastEntityEvent(this, (byte) 0);
			potionContents = PotionContents.EMPTY;
			entityData.set(COLOR, 0);
		}
	}

	private void spawnPotionParticles(int particleCount) {
		int color = getColor();

		if (color != 0 && particleCount > 0) {
			for (int j = 0; j < particleCount; ++j) {
				level().addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, color), getX() + (random.nextDouble() - 0.5D) * getBbWidth(),
						getY() + random.nextDouble() * getBbHeight(), getZ() + (random.nextDouble() - 0.5D) * getBbWidth(), 0, 0, 0);
			}
		}
	}

	public int getColor() {
		return entityData.get(COLOR);
	}

	@Override
	public void addAdditionalSaveData(ValueOutput out) {
		super.addAdditionalSaveData(out);
		out.store(PotionHelper.EFFECTS, PotionContents.CODEC, potionContents);
	}

	@Override
	public void readAdditionalSaveData(ValueInput in) {
		super.readAdditionalSaveData(in);
		potionContents = in.read(PotionHelper.EFFECTS, PotionContents.CODEC).orElse(PotionContents.EMPTY);

		if (potionContents.hasEffects()) {
			entityData.set(COLOR, potionContents.getColor());
		}
	}

	@Override
	protected void doPostHurtEffects(LivingEntity living) {
		super.doPostHurtEffects(living);
		Entity shooter = getOwner();
		if (shooter != null) {
			PotionHelper.applyEffectsToEntity(potionContents, this, shooter, living);
		}
	}

	@Override
	protected ItemStack getPickupItem() {
		if (!potionContents.hasEffects()) {
			return new ItemStack(Items.ARROW);
		} else {
			ItemStack stack = new ItemStack(ModItems.TIPPED_ARROW.get());
			stack.set(DataComponents.POTION_CONTENTS, potionContents);
			return stack;
		}
	}

	@Override
	public void handleEntityEvent(byte id) {
		if (id == 0) {
			int color = getColor();

			if (color > 0) {
				for (int j = 0; j < 20; ++j) {
					level().addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, color), getX() + (random.nextDouble() - 0.5D) * getBbWidth(),
							getY() + random.nextDouble() * getBbHeight(), getZ() + (random.nextDouble() - 0.5D) * getBbWidth(), 0, 0, 0);
				}
			}
		} else {
			super.handleEntityEvent(id);
		}
	}
}
