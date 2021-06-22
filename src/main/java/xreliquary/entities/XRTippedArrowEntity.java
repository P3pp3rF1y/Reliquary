package xreliquary.entities;

import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import xreliquary.init.ModEntities;
import xreliquary.init.ModItems;
import xreliquary.util.potions.XRPotionHelper;

import java.util.List;

@SuppressWarnings("squid:S2160")
public class XRTippedArrowEntity extends AbstractArrowEntity {
	private static final DataParameter<Integer> COLOR = EntityDataManager.createKey(XRTippedArrowEntity.class, DataSerializers.VARINT);
	private List<EffectInstance> effects = Lists.newArrayList();

	public XRTippedArrowEntity(EntityType<XRTippedArrowEntity> entityType, World world) {
		super(entityType, world);
	}

	public XRTippedArrowEntity(World world, double x, double y, double z) {
		super(ModEntities.TIPPED_ARROW, x, y, z, world);
	}

	public XRTippedArrowEntity(World world, LivingEntity shooter) {
		super(ModEntities.TIPPED_ARROW, shooter, world);
	}

	public void setPotionEffect(ItemStack stack) {
		effects = XRPotionHelper.getPotionEffectsFromStack(stack);

		dataManager.set(COLOR, PotionUtils.getPotionColorFromEffectList(effects));
	}

	@Override
	protected void registerData() {
		super.registerData();
		dataManager.register(COLOR, 0);
	}

	@Override
	public void tick() {
		super.tick();

		if (world.isRemote) {
			if (inGround) {
				if (timeInGround % 5 == 0) {
					spawnPotionParticles(1);
				}
			} else {
				spawnPotionParticles(2);
			}
		} else if (inGround && timeInGround != 0 && !effects.isEmpty() && timeInGround >= 600) {
			world.setEntityState(this, (byte) 0);
			effects.clear();
			dataManager.set(COLOR, 0);
		}
	}

	private void spawnPotionParticles(int particleCount) {
		int i = getColor();

		if (i != 0 && particleCount > 0) {
			double d0 = (double) (i >> 16 & 255) / 255.0D;
			double d1 = (double) (i >> 8 & 255) / 255.0D;
			double d2 = (double) (i & 255) / 255.0D;

			for (int j = 0; j < particleCount; ++j) {
				world.addParticle(ParticleTypes.ENTITY_EFFECT, getPosX() + (rand.nextDouble() - 0.5D) * (double) getWidth(), getPosY() + rand.nextDouble() * (double) getHeight(), getPosZ() + (rand.nextDouble() - 0.5D) * (double) getWidth(), d0, d1, d2);
			}
		}
	}

	public int getColor() {
		return dataManager.get(COLOR);
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		XRPotionHelper.addPotionEffectsToCompoundTag(compound, effects);
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		effects = XRPotionHelper.getPotionEffectsFromCompoundTag(compound);

		if (!effects.isEmpty()) {
			dataManager.set(COLOR, PotionUtils.getPotionColorFromEffectList(effects));
		}
	}

	@Override
	protected void arrowHit(LivingEntity living) {
		super.arrowHit(living);
		Entity shooter = func_234616_v_();
		if (shooter != null) {
			XRPotionHelper.applyEffectsToEntity(effects, this, shooter, living);
		}
	}

	@Override
	protected ItemStack getArrowStack() {
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
	public void handleStatusUpdate(byte id) {
		if (id == 0) {
			int i = getColor();

			if (i > 0) {
				double d0 = (double) (i >> 16 & 255) / 255.0D;
				double d1 = (double) (i >> 8 & 255) / 255.0D;
				double d2 = (double) (i & 255) / 255.0D;

				for (int j = 0; j < 20; ++j) {
					world.addParticle(ParticleTypes.ENTITY_EFFECT, getPosX() + (rand.nextDouble() - 0.5D) * (double) getWidth(), getPosY() + rand.nextDouble() * (double) getHeight(), getPosZ() + (rand.nextDouble() - 0.5D) * (double) getWidth(), d0, d1, d2);
				}
			}
		} else {
			super.handleStatusUpdate(id);
		}
	}

	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
