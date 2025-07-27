package reliquary.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import reliquary.init.ModEntities;
import reliquary.reference.Config;
import reliquary.util.RandHelper;

import static reliquary.item.IceMagusRodItem.ICE_PARTICLE;

@SuppressWarnings({"squid:S2160", "squid:MaximumInheritanceDepth"})
public class SpecialSnowball extends ThrowableItemProjectile {
	private boolean fromGlacialStaff;

	public SpecialSnowball(EntityType<SpecialSnowball> entityType, Level level) {
		super(entityType, level);
	}

	public SpecialSnowball(Level level, LivingEntity entity, boolean b) {
		super(ModEntities.SPECIAL_SNOWBALL.get(), entity, level);
		fromGlacialStaff = b;
	}

	private int getSnowballDamage() {
		return fromGlacialStaff ? Config.COMMON.items.glacialStaff.snowballDamage.get() : Config.COMMON.items.iceMagusRod.snowballDamage.get();
	}

	private int getSnowballDamageFireImmuneBonus() {
		return fromGlacialStaff ? Config.COMMON.items.glacialStaff.snowballDamageBonusFireImmune.get() : Config.COMMON.items.iceMagusRod.snowballDamageBonusFireImmune.get();
	}

	private int getSnowballDamageBlazeBonus() {
		return fromGlacialStaff ? Config.COMMON.items.glacialStaff.snowballDamageBonusBlaze.get() : Config.COMMON.items.iceMagusRod.snowballDamageBonusBlaze.get();
	}

	/**
	 * Called when this EntityThrowable hits a block or entity.
	 */
	@Override
	protected void onHit(HitResult result) {
		for (int var3 = 0; var3 < 8; ++var3) {
			level().addParticle(ParticleTypes.ITEM_SNOWBALL, getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
		}

		if (!level().isClientSide) {
			if (result.getType() == HitResult.Type.ENTITY) {
				Entity entityHit = ((EntityHitResult) result).getEntity();
				int damage = getSnowballDamage();
				if (entityHit.fireImmune()) {
					damage += getSnowballDamageFireImmuneBonus();
				}
				if (entityHit instanceof Blaze) {
					damage += getSnowballDamageBlazeBonus();
				}
				entityHit.hurt(damageSources().thrown(this, getOwner()), damage);
			} else if (result.getType() == HitResult.Type.BLOCK) {
				BlockPos posUp = ((BlockHitResult) result).getBlockPos().above();
				if (level().getBlockState(posUp).getBlock() instanceof BaseFireBlock) {
					level().playSound(null, posUp, SoundEvents.GENERIC_BURN, SoundSource.NEUTRAL, 0.5F, RandHelper.getRandomMinusOneToOne(level().random) * 0.8F);
					level().setBlockAndUpdate(posUp, Blocks.AIR.defaultBlockState());
				}
			}
			discard();
		}
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void tick() {
		super.tick();
		if (tickCount % 4 == level().random.nextInt(5)) {
			level().addParticle(ICE_PARTICLE, getX(), getY(), getZ(), 5.0D, 5.0D, 1.0D);
		}
	}

	/**
	 * Gets the amount of gravity to apply to the thrown entity with each tick.
	 */
	@Override
	protected double getDefaultGravity() {
		// flies slightly farther than a normal projectile
		return 0.01F;
	}

	@Override
	protected Item getDefaultItem() {
		return Items.SNOWBALL;
	}

	@Override
	public void handleEntityEvent(byte id) {
		if (id == 3) {
			ItemStack stack = getItem();
			ParticleOptions particleData = stack.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemParticleOption(ParticleTypes.ITEM, stack);

			for (int i = 0; i < 8; ++i) {
				level().addParticle(particleData, getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	public boolean shouldRenderAtSqrDistance(double distance) {
		double d0 = 4.0D;

		d0 = d0 * 64.0D;
		return distance < d0 * d0;
	}
}
