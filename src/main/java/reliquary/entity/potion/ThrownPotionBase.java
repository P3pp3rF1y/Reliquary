package reliquary.entity.potion;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import reliquary.network.SpawnThrownPotionImpactParticlesPayload;
import reliquary.util.LogHelper;

import java.util.List;

public abstract class ThrownPotionBase extends ThrowableProjectile implements ItemSupplier {
	private static final EntityDataAccessor<ItemStack> ITEM = SynchedEntityData.defineId(ThrownPotionBase.class, EntityDataSerializers.ITEM_STACK);

	<T extends ThrownPotionBase> ThrownPotionBase(EntityType<T> entityType, Level level, ItemStack thrownStack) {
		super(entityType, level);
		setItem(thrownStack);
	}

	<T extends ThrownPotionBase> ThrownPotionBase(EntityType<T> entityType, Level level, Player player, ItemStack thrownStack) {
		super(entityType, player, level);
		setItem(thrownStack);
	}

	<T extends ThrownPotionBase> ThrownPotionBase(EntityType<T> entityType, Level level, double x, double y, double z, ItemStack thrownStack) {
		super(entityType, x, y, z, level);
		setItem(thrownStack);
	}

	private void setItem(ItemStack thrownStack) {
		entityData.set(ITEM, thrownStack);
	}

	@Override
	protected double getDefaultGravity() {
		return 0.05;
	}

	@Override
	protected void onHit(HitResult result) {
		if (!level().isClientSide) {
			spawnParticles();
			doSplashEffect();
			discard();
		}
	}

	abstract boolean hasLivingEntityEffect();

	private void doSplashEffect() {
		doGroundSplashEffect();
		if (!hasLivingEntityEffect()) {
			return;
		}
		AABB bb = getBoundingBox().inflate(4.0D, 2.0D, 4.0D);
		List<LivingEntity> eList = level().getEntitiesOfClass(LivingEntity.class, bb);
		eList.forEach(this::doLivingSplashEffect);
	}

	abstract void doGroundSplashEffect();

	private void spawnParticles() {
		if (level().isClientSide) {
			return;
		}

		for (int i = 0; i < 8; ++i) {
			level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, getItem()), getX(), getY(), getZ(),
					random.nextGaussian() * 0.15D, random.nextDouble() * 0.2D, random.nextGaussian() * 0.15D);
		}

		level().playSound(null, blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0F, level().random.nextFloat() * 0.1F + 0.9F);

		PacketDistributor.sendToPlayersTrackingEntity(this, new SpawnThrownPotionImpactParticlesPayload(getColor(), getX(), getY(), getZ()));
	}

	// this gets called inside the on-impact method on EVERY living entity
	// within the AOE
	abstract void doLivingSplashEffect(LivingEntity e);

	// these are just the getters for the particle coloration. They're all the
	// same particle style, so it's really just a matter of coloration.
	abstract int getColor();

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(ITEM, ItemStack.EMPTY);
	}

	@Override
	public ItemStack getItem() {
		ItemStack stack = getEntityData().get(ITEM);
		if (stack.getItem() != Items.SPLASH_POTION && stack.getItem() != Items.LINGERING_POTION) {
			LogHelper.error("EntityThrownPotion entity {} has no item?!", getId());

			return new ItemStack(Items.SPLASH_POTION);
		} else {
			return stack;
		}
	}
}
