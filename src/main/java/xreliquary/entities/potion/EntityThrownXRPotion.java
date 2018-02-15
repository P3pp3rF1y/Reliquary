package xreliquary.entities.potion;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import xreliquary.init.ModItems;
import xreliquary.network.PacketFXThrownPotionImpact;
import xreliquary.network.PacketHandler;
import xreliquary.util.potions.XRPotionHelper;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class EntityThrownXRPotion extends EntityThrowable implements IEntityAdditionalSpawnData {

	@SuppressWarnings("unused")
	public EntityThrownXRPotion(World world) {
		super(world);
	}

	private int renderColor;
	private boolean lingering = false;
	private List<PotionEffect> effects;

	public EntityThrownXRPotion(World world, double x, double y, double z, ItemStack potion) {
		super(world, x, y, z);
		setEffects(potion);
		this.lingering = ModItems.potion.isLingering(potion);
	}

	private void setEffects(ItemStack ist) {
		this.effects = XRPotionHelper.getPotionEffectsFromStack(ist);
		setRenderColor(getColor());
	}

	public EntityThrownXRPotion(World world, EntityLivingBase elb, ItemStack potion) {
		super(world, elb);
		setEffects(potion);
		this.lingering = ModItems.potion.isLingering(potion);
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
	protected void onImpact(@Nonnull RayTraceResult result) {
		if(!this.world.isRemote) {
			if (this.lingering) {
				EntityAreaEffectCloud entityareaeffectcloud = new EntityAreaEffectCloud(this.world, this.posX, this.posY, this.posZ);
				entityareaeffectcloud.setOwner(this.getThrower());
				entityareaeffectcloud.setRadius(3.0F);
				entityareaeffectcloud.setRadiusOnUse(-0.5F);
				entityareaeffectcloud.setWaitTime(10);
				entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
				entityareaeffectcloud.setColor(this.renderColor);

				for (PotionEffect potioneffect : this.effects)
				{
					entityareaeffectcloud.addEffect(new PotionEffect(potioneffect.getPotion(), potioneffect.getDuration(), potioneffect.getAmplifier()));
				}

				this.world.spawnEntity(entityareaeffectcloud);
			} else {
				if(effects != null && !effects.isEmpty()) {
					AxisAlignedBB axisalignedbb = this.getEntityBoundingBox().grow(4.0D, 2.0D, 4.0D);
					List<EntityLivingBase> livingEntities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);

					if(!livingEntities.isEmpty()) {

						for(EntityLivingBase entitylivingbase : livingEntities) {
							double d0 = this.getDistance(entitylivingbase);

							if(d0 < 16.0D) {
								double d1 = 1.0D - Math.sqrt(d0) / 4.0D;

								if(entitylivingbase == result.entityHit) {
									d1 = 1.0D;
								}

								XRPotionHelper.applyEffectsToEntity(effects, this, this.getThrower(), entitylivingbase, d1);
							}
						}
					}
				}
			}

			spawnParticles();
			this.setDead();
		}
	}

	public int getColor() {
		//basically we're just using vanillas right now. This is hilarious in comparison to the old method, which is a mile long.
		return effects == null ? getRenderColor() : PotionUtils.getPotionColorFromEffectList(effects);
	}

	// most of these are the same in every potion, the only thing that isn't is
	// the coloration of the particles.
	private void spawnParticles() {
		if(world.isRemote)
			return;

		Random var7 = rand;
		for(int var15 = 0; var15 < 8; ++var15) {
			world.spawnParticle(EnumParticleTypes.ITEM_CRACK, this.posX, this.posY, this.posZ, var7.nextGaussian() * 0.15D, var7.nextDouble() * 0.2D, var7.nextGaussian() * 0.15D, Item.getIdFromItem(Items.POTIONITEM));
		}

		world.playSound(null, this.getPosition(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
		PacketHandler.networkWrapper.sendToAllAround(new PacketFXThrownPotionImpact(getColor(), this.posX, this.posY, this.posZ), new NetworkRegistry.TargetPoint(this.dimension, this.posX, this.posY, this.posZ, 32.0D));
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		this.effects = XRPotionHelper.getPotionEffectsFromCompoundTag(tag);
		setRenderColor(tag.getInteger("color"));
		this.lingering = tag.getBoolean("lingering");
		if(this.effects.isEmpty())
			this.setDead();
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		XRPotionHelper.addPotionEffectsToCompoundTag(tag, effects);
		tag.setInteger("color", getRenderColor());
		tag.setBoolean("lingering", this.lingering);
	}

	public int getRenderColor() {
		return renderColor;
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeInt(renderColor);
		buffer.writeBoolean(lingering);
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		setRenderColor(additionalData.readInt());
		this.lingering = additionalData.readBoolean();
	}

	private void setRenderColor(int renderColor) {
		this.renderColor = renderColor;
	}

	public boolean getLingering() {
		return lingering;
	}
}

