package xreliquary.entities;

import com.google.common.collect.Sets;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class EntityXRTippedArrow extends EntityArrow {
	private static final DataParameter<Integer> COLOR = EntityDataManager.createKey(EntityTippedArrow.class, DataSerializers.VARINT);
	private final Set<PotionEffect> customPotionEffects = Sets.newHashSet();

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
		Collection<PotionEffect> collection = PotionUtils.getFullEffectsFromItem(stack);

		this.customPotionEffects.addAll(collection.stream().map(PotionEffect::new).collect(Collectors.toList()));

		this.dataManager.set(COLOR, PotionUtils.getPotionColorFromEffectList(collection));
	}

	private void addEffect(PotionEffect effect) {
		this.customPotionEffects.add(effect);
		this.getDataManager().set(COLOR, PotionUtils.getPotionColorFromEffectList(this.customPotionEffects));
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
		} else if(this.inGround && this.timeInGround != 0 && !this.customPotionEffects.isEmpty() && this.timeInGround >= 600) {
			this.world.setEntityState(this, (byte) 0);
			this.customPotionEffects.clear();
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

		if(!this.customPotionEffects.isEmpty()) {
			NBTTagList nbttaglist = new NBTTagList();

			for(PotionEffect potioneffect : this.customPotionEffects) {
				nbttaglist.appendTag(potioneffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
			}

			compound.setTag("CustomPotionEffects", nbttaglist);
		}
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);

		PotionUtils.getFullEffectsFromTag(compound).forEach(this::addEffect);

		if(!this.customPotionEffects.isEmpty()) {
			this.dataManager.set(COLOR, PotionUtils.getPotionColorFromEffectList(this.customPotionEffects));
		}
	}

	@Override
	protected void arrowHit(EntityLivingBase living) {
		super.arrowHit(living);

		if(!this.customPotionEffects.isEmpty()) {
			this.customPotionEffects.forEach(living::addPotionEffect);
		}
	}

	@Nonnull
	@Override
	protected ItemStack getArrowStack() {
		if(this.customPotionEffects.isEmpty()) {
			return new ItemStack(Items.ARROW);
		} else {
			ItemStack itemstack = new ItemStack(ModItems.tippedArrow);
			PotionUtils.appendEffects(itemstack, this.customPotionEffects);
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
