package xreliquary.entities;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import xreliquary.init.ModEntities;
import xreliquary.reference.Settings;
import xreliquary.util.RandHelper;

@SuppressWarnings({"squid:S2160", "squid:MaximumInheritanceDepth"})
public class SpecialSnowballEntity extends ProjectileItemEntity {
	private boolean fromGlacialStaff;

	public SpecialSnowballEntity(EntityType<SpecialSnowballEntity> entityType, World world) {
		super(entityType, world);
	}

	public SpecialSnowballEntity(World world, LivingEntity entity, boolean b) {
		super(ModEntities.SPECIAL_SNOWBALL, entity, world);
		fromGlacialStaff = b;
	}

	private int getSnowballDamage() {
		return fromGlacialStaff ? Settings.COMMON.items.glacialStaff.snowballDamage.get() : Settings.COMMON.items.iceMagusRod.snowballDamage.get();
	}

	private int getSnowballDamageFireImmuneBonus() {
		return fromGlacialStaff ? Settings.COMMON.items.glacialStaff.snowballDamageBonusFireImmune.get() : Settings.COMMON.items.iceMagusRod.snowballDamageBonusFireImmune.get();
	}

	private int getSnowballDamageBlazeBonus() {
		return fromGlacialStaff ? Settings.COMMON.items.glacialStaff.snowballDamageBonusBlaze.get() : Settings.COMMON.items.iceMagusRod.snowballDamageBonusBlaze.get();
	}

	/**
	 * Called when this EntityThrowable hits a block or entity.
	 */
	@Override
	protected void onImpact(RayTraceResult result) {
		for (int var3 = 0; var3 < 8; ++var3) {
			world.addParticle(ParticleTypes.ITEM_SNOWBALL, getPosX(), getPosY(), getPosZ(), 0.0D, 0.0D, 0.0D);
		}

		if (!world.isRemote) {
			if (result.getType() == RayTraceResult.Type.ENTITY) {
				Entity entityHit = ((EntityRayTraceResult) result).getEntity();
				int damage = getSnowballDamage();
				if (entityHit.isImmuneToFire()) {
					damage += getSnowballDamageFireImmuneBonus();
				}
				if (entityHit instanceof BlazeEntity) {
					damage += getSnowballDamageBlazeBonus();
				}
				entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, func_234616_v_()), damage);
			} else if (result.getType() == RayTraceResult.Type.BLOCK) {
				BlockPos pos = ((BlockRayTraceResult) result).getPos();
				if (world.getBlockState(pos.up()).getBlock() == Blocks.FIRE) {
					world.playSound(null, pos.up(), SoundEvents.ENTITY_GENERIC_BURN, SoundCategory.NEUTRAL, 0.5F, RandHelper.getRandomMinusOneToOne(world.rand) * 0.8F);
					world.setBlockState(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ()), Blocks.AIR.getDefaultState());
				}
			}
			remove();
		}
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void tick() {
		super.tick();
		if (ticksExisted % 4 == world.rand.nextInt(5)) {
			world.addParticle(RedstoneParticleData.REDSTONE_DUST, getPosX(), getPosY(), getPosZ(), 5.0D, 5.0D, 1.0D);
		}
	}

	/**
	 * Gets the amount of gravity to apply to the thrown entity with each tick.
	 */
	@Override
	protected float getGravityVelocity() {
		// flies slightly farther than a normal projectile
		return 0.01F;
	}

	@Override
	protected Item getDefaultItem() {
		return Items.SNOWBALL;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleStatusUpdate(byte id) {
		if (id == 3) {
			ItemStack stack = func_213882_k();
			IParticleData particleData = stack.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemParticleData(ParticleTypes.ITEM, stack);

			for(int i = 0; i < 8; ++i) {
				world.addParticle(particleData, getPosX(), getPosY(), getPosZ(), 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean isInRangeToRenderDist(double distance) {
		double d0 = 4.0D;

		d0 = d0 * 64.0D;
		return distance < d0 * d0;
	}
}
