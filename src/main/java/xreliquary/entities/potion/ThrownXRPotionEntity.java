package xreliquary.entities.potion;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;
import xreliquary.init.ModEntities;
import xreliquary.init.ModItems;
import xreliquary.items.IPotionItem;
import xreliquary.network.PacketFXThrownPotionImpact;
import xreliquary.network.PacketHandler;
import xreliquary.util.potions.XRPotionHelper;

import java.util.List;
import java.util.Random;

@OnlyIn(
		value = Dist.CLIENT,
		_interface = IRendersAsItem.class
)
public class ThrownXRPotionEntity extends ThrowableEntity implements IEntityAdditionalSpawnData, IRendersAsItem {
	private static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(ThrownPotionEntity.class, DataSerializers.ITEMSTACK);

	public ThrownXRPotionEntity(EntityType<ThrownXRPotionEntity> entityType, World world) {
		super(entityType, world);
	}

	public ThrownXRPotionEntity(World world, double x, double y, double z, ItemStack potion) {
		super(ModEntities.THROWN_POTION, x, y, z, world);
		setItem(potion);
	}

	public ThrownXRPotionEntity(World world, LivingEntity entity, ItemStack potion) {
		super(ModEntities.THROWN_POTION, entity, world);
		setItem(potion);
	}

	/**
	 * Gets the amount of gravity to apply to the thrown entity with each tick.
	 */
	@Override
	protected float getGravityVelocity() {
		return 0.04F;
	}

	//no clue what these do

	/**
	 * Called when this EntityThrowable hits a block or entity.
	 */
	@Override
	protected void onImpact(RayTraceResult result) {
		if (!world.isRemote) {
			ItemStack potion = getItem();
			if (!(potion.getItem() instanceof IPotionItem)) {
				return;
			}
			List<EffectInstance> effects = ((IPotionItem) potion.getItem()).getEffects(potion);

			int color = PotionUtils.getPotionColorFromEffectList(effects);
			if (potion.getItem() == ModItems.LINGERING_POTION) {
				spawnAreaEffectCloud(effects, color);
			} else {
				splashPotion(result, effects);
			}

			spawnParticles(color);
			remove();
		}
	}

	private void splashPotion(RayTraceResult result, List<EffectInstance> effects) {
		AxisAlignedBB axisalignedbb = getBoundingBox().grow(4.0D, 2.0D, 4.0D);
		List<LivingEntity> livingEntities = world.getEntitiesWithinAABB(LivingEntity.class, axisalignedbb);

		if (!livingEntities.isEmpty()) {
			for (LivingEntity entity : livingEntities) {
				double d0 = getDistance(entity);

				if (d0 < 16.0D) {
					double d1 = 1.0D - Math.sqrt(d0) / 4.0D;

					if (result.getType() == RayTraceResult.Type.ENTITY && entity == ((EntityRayTraceResult) result).getEntity()) {
						d1 = 1.0D;
					}

					XRPotionHelper.applyEffectsToEntity(effects, this, getThrower(), entity, d1);
				}
			}
		}
	}

	private void spawnAreaEffectCloud(List<EffectInstance> effects, int color) {
		AreaEffectCloudEntity areaEffectCloud = new AreaEffectCloudEntity(world, posX, posY, posZ);
		areaEffectCloud.setOwner(getThrower());
		areaEffectCloud.setRadius(3.0F);
		areaEffectCloud.setRadiusOnUse(-0.5F);
		areaEffectCloud.setWaitTime(10);
		areaEffectCloud.setRadiusPerTick(-areaEffectCloud.getRadius() / (float) areaEffectCloud.getDuration());
		areaEffectCloud.setColor(color);

		for (EffectInstance potionEffect : effects) {
			areaEffectCloud.addEffect(new EffectInstance(potionEffect.getPotion(), potionEffect.getDuration(), potionEffect.getAmplifier()));
		}

		world.addEntity(areaEffectCloud);
	}

	private void spawnParticles(int color) {
		if (world.isRemote) {
			return;
		}

		Random var7 = rand;
		for (int var15 = 0; var15 < 8; ++var15) {
			world.addParticle(new ItemParticleData(ParticleTypes.ITEM, getItem()), posX, posY, posZ, var7.nextGaussian() * 0.15D, var7.nextDouble() * 0.2D, var7.nextGaussian() * 0.15D);
		}

		world.playSound(null, getPosition(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
		PacketHandler.sendToAllAround(new PacketFXThrownPotionImpact(color, posX, posY, posZ), new PacketDistributor.TargetPoint(posX, posY, posZ, 32.0D, world.getDimension().getType()));
	}

	@Override
	public void writeAdditional(CompoundNBT nbt) {
		super.writeAdditional(nbt);
		nbt.put("item", getItem().write(new CompoundNBT()));
	}

	@Override
	public void readAdditional(CompoundNBT nbt) {
		super.readAdditional(nbt);
		setItem(ItemStack.read(nbt.getCompound("item")));
	}

	@Override
	protected void registerData() {
		dataManager.register(ITEM, ItemStack.EMPTY);
	}

	@Override
	public ItemStack getItem() {
		return dataManager.get(ITEM);
	}

	private void setItem(ItemStack stack) {
		dataManager.set(ITEM, stack);
	}

	@Override
	public void writeSpawnData(PacketBuffer buffer) {
		buffer.writeItemStack(getItem());
	}

	@Override
	public void readSpawnData(PacketBuffer additionalData) {
		setItem(additionalData.readItemStack());
	}

	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}

