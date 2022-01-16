package xreliquary.entities.potion;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import xreliquary.network.PacketFXThrownPotionImpact;
import xreliquary.network.PacketHandler;

import java.util.List;

@OnlyIn(
		value = Dist.CLIENT,
		_interface = ItemSupplier.class
)
public abstract class ThrownPotionEntity extends ThrowableProjectile implements ItemSupplier {
	private static final EntityDataAccessor<ItemStack> ITEM = SynchedEntityData.defineId(ThrownPotionEntity.class, EntityDataSerializers.ITEM_STACK);

	<T extends ThrownPotionEntity> ThrownPotionEntity(EntityType<T> entityType, Level world, ItemStack thrownStack) {
		super(entityType, world);
		setItem(thrownStack);
	}

	<T extends ThrownPotionEntity> ThrownPotionEntity(EntityType<T> entityType, Level world, Player player, ItemStack thrownStack) {
		super(entityType, player, world);
		setItem(thrownStack);
	}

	<T extends ThrownPotionEntity> ThrownPotionEntity(EntityType<T> entityType, Level world, double x, double y, double z, ItemStack thrownStack) {
		super(entityType, x, y, z, world);
		setItem(thrownStack);
	}

	private void setItem(ItemStack thrownStack) {
		entityData.set(ITEM, thrownStack);
	}

	@Override
	protected float getGravity() {
		return 0.05F;
	}

	@Override
	protected void onHit(HitResult result) {
		if (!level.isClientSide) {
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
		List<LivingEntity> eList = level.getEntitiesOfClass(LivingEntity.class, bb);
		eList.forEach(this::doLivingSplashEffect);
	}

	abstract void doGroundSplashEffect();

	private void spawnParticles() {
		if (level.isClientSide) {
			return;
		}

		for (int i = 0; i < 8; ++i) {
			level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, getItem()), getX(), getY(), getZ(),
					random.nextGaussian() * 0.15D, random.nextDouble() * 0.2D, random.nextGaussian() * 0.15D);
		}

		level.playSound(null, blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.1F + 0.9F);

		PacketHandler.sendToAllAround(new PacketFXThrownPotionImpact(getColor(), getX(), getY(), getZ()), new PacketDistributor.TargetPoint(getX(), getY(), getZ(), 32D, level.dimension()));
	}

	// this gets called inside the on-impact method on EVERY living entity
	// within the AOE
	abstract void doLivingSplashEffect(LivingEntity e);

	// these are just the getters for the particle coloration. They're all the
	// same particle style, so it's really just a matter of coloration.
	abstract int getColor();

	@Override
	protected void defineSynchedData() {
		entityData.define(ITEM, ItemStack.EMPTY);
	}

	@Override
	public ItemStack getItem() {
		ItemStack stack = getEntityData().get(ITEM);
		if (stack.getItem() != Items.SPLASH_POTION && stack.getItem() != Items.LINGERING_POTION) {
			LOGGER.error("EntityThrownPotion entity {} has no item?!", getId());

			return new ItemStack(Items.SPLASH_POTION);
		} else {
			return stack;
		}
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
