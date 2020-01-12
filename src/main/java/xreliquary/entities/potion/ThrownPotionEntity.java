package xreliquary.entities.potion;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import xreliquary.network.PacketFXThrownPotionImpact;
import xreliquary.network.PacketHandler;

import java.util.List;
import java.util.Random;

public abstract class ThrownPotionEntity extends ThrowableEntity implements IRendersAsItem {
	private static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(ThrownPotionEntity.class, DataSerializers.ITEMSTACK);

	<T extends ThrownPotionEntity> ThrownPotionEntity(EntityType<T> entityType, World world, ItemStack thrownStack) {
		super(entityType, world);
		setItem(thrownStack);
	}

	<T extends ThrownPotionEntity> ThrownPotionEntity(EntityType<T> entityType, World world, PlayerEntity player, ItemStack thrownStack) {
		super(entityType, player, world);
		setItem(thrownStack);
	}

	<T extends ThrownPotionEntity> ThrownPotionEntity(EntityType<T> entityType, World world, double x, double y, double z, ItemStack thrownStack) {
		super(entityType, x, y, z, world);
		setItem(thrownStack);
	}

	private void setItem(ItemStack thrownStack) {
		dataManager.set(ITEM, thrownStack);
	}

	@Override
	protected float getGravityVelocity() {
		return 0.05F;
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if (!world.isRemote) {
			spawnParticles();
			doSplashEffect();
			remove();
		}
	}

	abstract boolean hasLivingEntityEffect();

	private void doSplashEffect() {
		doGroundSplashEffect();
		if (!hasLivingEntityEffect()) {
			return;
		}
		AxisAlignedBB bb = getBoundingBox().grow(4.0D, 2.0D, 4.0D);
		List<LivingEntity> eList = world.getEntitiesWithinAABB(LivingEntity.class, bb);
		eList.forEach(this::doLivingSplashEffect);
	}

	abstract void doGroundSplashEffect();

	private void spawnParticles() {
		if (world.isRemote) {
			return;
		}

		Random rand = this.rand;
		for (int i = 0; i < 8; ++i) {
			world.addParticle(new ItemParticleData(ParticleTypes.ITEM, getItem()), posX, posY, posZ,
					rand.nextGaussian() * 0.15D, rand.nextDouble() * 0.2D, rand.nextGaussian() * 0.15D);
		}

		world.playSound(null, getPosition(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);

		PacketHandler.sendToAllAround(new PacketFXThrownPotionImpact(getColor(), posX, posY, posZ), new PacketDistributor.TargetPoint(posX, posY, posZ, 32D, world.getDimension().getType()));
	}

	// this gets called inside the on-impact method on EVERY living entity
	// within the AOE
	abstract void doLivingSplashEffect(LivingEntity e);

	// these are just the getters for the particle coloration. They're all the
	// same particle style, so it's really just a matter of coloration.
	abstract int getColor();

	@Override
	protected void registerData() {
		dataManager.register(ITEM, ItemStack.EMPTY);
	}

	@Override
	public ItemStack getItem() {
		ItemStack stack = getDataManager().get(ITEM);
		if (stack.getItem() != Items.SPLASH_POTION && stack.getItem() != Items.LINGERING_POTION) {
			if (world != null) {
				LOGGER.error("EntityThrownPotion entity {} has no item?!", getEntityId());
			}

			return new ItemStack(Items.SPLASH_POTION);
		} else {
			return stack;
		}
	}
}
