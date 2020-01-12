package xreliquary.entities;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import xreliquary.init.ModEntities;
import xreliquary.init.ModItems;
import xreliquary.network.PacketFXThrownPotionImpact;
import xreliquary.network.PacketHandler;
import xreliquary.reference.Colors;

import java.util.List;

public class GlowingWaterEntity extends ThrowableEntity implements IRendersAsItem {
	public GlowingWaterEntity(EntityType<GlowingWaterEntity> entityType, World world) {
		super(entityType, world);
	}

	public GlowingWaterEntity(World world, PlayerEntity player) {
		super(ModEntities.GLOWING_WATER, player, world);
	}

	public GlowingWaterEntity(World world, double x, double y, double z) {
		super(ModEntities.GLOWING_WATER, x, y, z, world);
	}

	/**
	 * Gets the amount of gravity to apply to the thrown entity with each tick.
	 */
	@Override
	protected float getGravityVelocity() {
		return 0.05F;
	}

	private boolean isUndead(LivingEntity e) {
		return e.getCreatureAttribute() == CreatureAttribute.UNDEAD;
	}

	/**
	 * Called when this EntityThrowable hits a block or entity.
	 */
	@Override
	protected void onImpact(RayTraceResult result) {
		if (!world.isRemote) {
			spawnParticles();
			AxisAlignedBB bb = getBoundingBox().grow(4.0D, 2.0D, 4.0D);
			List<MobEntity> eList = world.getEntitiesWithinAABB(MobEntity.class, bb);
			eList.stream().filter(this::isUndead).forEach(e -> {
				float amount = 18f + rand.nextInt(17);
				if (getThrower() instanceof PlayerEntity) {
					e.attackEntityFrom(DamageSource.causePlayerDamage((PlayerEntity) getThrower()), amount);
				} else {
					e.attackEntityFrom(DamageSource.MAGIC, amount);
				}
			});

			world.playEvent(2002, new BlockPos(this), 0);
			remove();
		}
	}

	private void spawnParticles() {
		double x = posX;
		double y = posY;
		double z = posZ;

		ItemParticleData itemParticleData = new ItemParticleData(ParticleTypes.ITEM, new ItemStack(ModItems.GLOWING_WATER));
		for (int particleNum = 0; particleNum < 8; ++particleNum) {
			world.addParticle(itemParticleData, x, y, z, rand.nextGaussian() * 0.15D, rand.nextDouble() * 0.2D, rand.nextGaussian() * 0.15D);
		}

		world.playSound(null, getPosition(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.NEUTRAL, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
		PacketHandler.sendToAllAround(new PacketFXThrownPotionImpact(Colors.get(Colors.BLUE), posX, posY, posZ), new PacketDistributor.TargetPoint(posX, posY, posZ, 32.0D, world.getDimension().getType()));

	}

	@Override
	protected void registerData() {
		//noop
	}

	@Override
	public ItemStack getItem() {
		return new ItemStack(ModItems.GLOWING_WATER);
	}
}
