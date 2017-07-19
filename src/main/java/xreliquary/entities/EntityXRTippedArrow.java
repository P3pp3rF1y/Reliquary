package xreliquary.entities;

import com.google.common.collect.Lists;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.init.ModItems;
import xreliquary.util.potions.XRPotionHelper;

import javax.annotation.Nonnull;
import java.util.List;

public class EntityXRTippedArrow extends EntityArrow {
	private static final DataParameter<Integer> COLOR = EntityDataManager.createKey(EntityTippedArrow.class, DataSerializers.VARINT);
	private List<PotionEffect> effects = Lists.newArrayList();

	public EntityXRTippedArrow(World worldIn) {
		super(worldIn);
	}

	public EntityXRTippedArrow(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
	}

	public EntityXRTippedArrow(World worldIn, EntityLivingBase shooter) {
		super(worldIn, shooter);
	}

	public void setPotionEffect(ItemStack stack) {
		effects = XRPotionHelper.getPotionEffectsFromStack(stack);

		this.dataManager.set(COLOR, PotionUtils.getPotionColorFromEffectList(effects));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(COLOR, 0);
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate() {
		super.onUpdate();

		if(this.world.isRemote) {
			if(this.inGround) {
				if(this.timeInGround % 5 == 0) {
					this.spawnPotionParticles(1);
				}
			} else {
				this.spawnPotionParticles(2);
			}
		} else if(this.inGround && this.timeInGround != 0 && !this.effects.isEmpty() && this.timeInGround >= 600) {
			this.world.setEntityState(this, (byte) 0);
			this.effects.clear();
			this.dataManager.set(COLOR, 0);
		}
	}

	private void spawnPotionParticles(int particleCount) {
		int i = this.getColor();

		if(i != 0 && particleCount > 0) {
			double d0 = (double) (i >> 16 & 255) / 255.0D;
			double d1 = (double) (i >> 8 & 255) / 255.0D;
			double d2 = (double) (i & 255) / 255.0D;

			for(int j = 0; j < particleCount; ++j) {
				this.world.spawnParticle(EnumParticleTypes.SPELL_MOB, this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width, this.posY + this.rand.nextDouble() * (double) this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width, d0, d1, d2);
			}
		}
	}

	public int getColor() {
		return this.dataManager.get(COLOR);
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);

		XRPotionHelper.addPotionEffectsToCompoundTag(compound, effects);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);

		effects = XRPotionHelper.getPotionEffectsFromCompoundTag(compound);

		if(!effects.isEmpty()) {
			this.dataManager.set(COLOR, PotionUtils.getPotionColorFromEffectList(effects));
		}
	}

	@Override
	protected void arrowHit(EntityLivingBase living) {
		super.arrowHit(living);

		XRPotionHelper.applyEffectsToEntity(effects, this, this.shootingEntity, living);
	}

	@Nonnull
	@Override
	protected ItemStack getArrowStack() {
		if(this.effects.isEmpty()) {
			return new ItemStack(Items.ARROW);
		} else {
			ItemStack itemstack = new ItemStack(ModItems.tippedArrow);
			XRPotionHelper.addPotionEffectsToStack(itemstack, effects);
			return itemstack;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte id) {
		if(id == 0) {
			int i = this.getColor();

			if(i > 0) {
				double d0 = (double) (i >> 16 & 255) / 255.0D;
				double d1 = (double) (i >> 8 & 255) / 255.0D;
				double d2 = (double) (i & 255) / 255.0D;

				for(int j = 0; j < 20; ++j) {
					this.world.spawnParticle(EnumParticleTypes.SPELL_MOB, this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width, this.posY + this.rand.nextDouble() * (double) this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width, d0, d1, d2);
				}
			}
		} else {
			super.handleStatusUpdate(id);
		}
	}
}
