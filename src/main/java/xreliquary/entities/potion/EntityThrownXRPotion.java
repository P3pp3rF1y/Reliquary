package xreliquary.entities.potion;


import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import xreliquary.util.potions.PotionEssence;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by Xeno on 11/9/2014.
 */
public class EntityThrownXRPotion extends EntityThrowable implements IEntityAdditionalSpawnData
{
    public EntityThrownXRPotion(World world)
    {
        super(world);
    }

    private int renderColor;
    public PotionEssence essence = null;

    public EntityThrownXRPotion(World world, EntityLivingBase elb, ItemStack ist) {
        super(world, elb);
        this.essence = new PotionEssence(ist.getTagCompound());
        setRenderColor(getColor());
    }

    public EntityThrownXRPotion(World p_i1792_1_, double p_i1792_2_, double p_i1792_4_, double p_i1792_6_, ItemStack ist)
    {
        super(p_i1792_1_, p_i1792_2_, p_i1792_4_, p_i1792_6_);
        this.essence = new PotionEssence(ist.getTagCompound());
        setRenderColor(getColor());
    }

    /**
     * Gets the amount of gravity to apply to the thrown entity with each tick.
     */
    @Override
    protected float getGravityVelocity()
    {
        return 0.04F;
    }

    //no clue what these do

    @Override
    protected float getVelocity()
    {
        return 0.5F;
    }

    @Override
    protected float getInaccuracy()
    {
        return -20.0F;
    }

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    protected void onImpact(MovingObjectPosition mop)
    {
        if (!this.worldObj.isRemote)
        {
            List list = essence.getEffects();

            if (list != null && !list.isEmpty())
            {
                AxisAlignedBB axisalignedbb = this.getEntityBoundingBox().expand( 4.0D, 2.0D, 4.0D );
                List list1 = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);

                if (list1 != null && !list1.isEmpty())
                {
                    Iterator iterator = list1.iterator();

                    while (iterator.hasNext())
                    {
                        EntityLivingBase entitylivingbase = (EntityLivingBase)iterator.next();
                        double d0 = this.getDistanceSqToEntity(entitylivingbase);

                        if (d0 < 16.0D)
                        {
                            double d1 = 1.0D - Math.sqrt(d0) / 4.0D;

                            if (entitylivingbase == mop.entityHit)
                            {
                                d1 = 1.0D;
                            }

                            Iterator iterator1 = list.iterator();

                            while (iterator1.hasNext())
                            {
                                PotionEffect potioneffect = (PotionEffect)iterator1.next();
                                int i = potioneffect.getPotionID();

                                if (Potion.potionTypes[i].isInstant())
                                {
                                    Potion.potionTypes[i].affectEntity(this, this.getThrower(), entitylivingbase, potioneffect.getAmplifier(), d1);
                                }
                                else
                                {
                                    int j = (int)(d1 * (double)potioneffect.getDuration() + 0.5D);

                                    if (j > 20)
                                    {
                                        entitylivingbase.addPotionEffect(new PotionEffect(i, j, potioneffect.getAmplifier()));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            this.setDead();
        }
        spawnParticles();
    }

    public int getColor() {
        //basically we're just using vanillas right now. This is hilarious in comparison to the old method, which is a mile long.
        return essence == null ? getRenderColor() : PotionHelper.calcPotionLiquidColor(essence.getEffects());
    }

    // most of these are the same in every potion, the only thing that isn't is
    // the coloration of the particles.
    protected void spawnParticles() {
        Random var7 = rand;
        if (!worldObj.isRemote){
            for (int var15 = 0; var15 < 8; ++var15) {
                worldObj.spawnParticle( EnumParticleTypes.ITEM_CRACK, this.posX, this.posY, this.posZ, var7.nextGaussian() * 0.15D, var7.nextDouble() * 0.2D, var7.nextGaussian() * 0.15D, Item.getIdFromItem(Items.potionitem));
            }
            worldObj.playSoundAtEntity(this, "dig.glass", 1.0F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
        } else {
            int color = getColor();

            float red = (((color >> 16) & 255) / 256F);
            float green = (((color >> 8) & 255) / 256F);
            float blue = (((color >> 0) & 255) / 256F);

            for (int var20 = 0; var20 < 100; ++var20) {
                double var39 = var7.nextDouble() * 4.0D;
                double var23 = var7.nextDouble() * Math.PI * 2.0D;
                double var25 = Math.cos(var23) * var39;
                double var27 = 0.01D + var7.nextDouble() * 0.5D;
                double var29 = Math.sin(var23) * var39;
                EntityFX var31 = spawnEntityFX(Minecraft.getMinecraft(), EnumParticleTypes.SPELL.getParticleID(), EnumParticleTypes.SPELL.getShouldIgnoreRange(), this.posX + var25 * 0.1D, this.posY + 0.3D, this.posZ + var29 * 0.1D, var25, var27, var29, new int[0]);
                if (var31 != null) {
                    float var32 = 0.75F + var7.nextFloat() * 0.25F;
                    var31.setRBGColorF(red * var32, green * var32, blue * var32);
                    var31.multiplyVelocity((float) var39);
                }
            }
        }
    }

    private EntityFX spawnEntityFX(Minecraft mc, int p_174974_1_, boolean ignoreRange, double p_174974_3_, double p_174974_5_, double p_174974_7_, double p_174974_9_, double p_174974_11_, double p_174974_13_, int... p_174974_15_)
    {
        if (mc != null && mc.getRenderViewEntity() != null && mc.effectRenderer != null)
        {
            int i = mc.gameSettings.particleSetting;

            if (i == 1 && worldObj.rand.nextInt(3) == 0)
            {
                i = 2;
            }

            double d0 = mc.getRenderViewEntity().posX - p_174974_3_;
            double d1 = mc.getRenderViewEntity().posY - p_174974_5_;
            double d2 = mc.getRenderViewEntity().posZ - p_174974_7_;

            if (ignoreRange)
            {
                return mc.effectRenderer.spawnEffectParticle(p_174974_1_, p_174974_3_, p_174974_5_, p_174974_7_, p_174974_9_, p_174974_11_, p_174974_13_, p_174974_15_);
            }
            else
            {
                double d3 = 16.0D;
                return d0 * d0 + d1 * d1 + d2 * d2 > 256.0D ? null : (i > 1 ? null : mc.effectRenderer.spawnEffectParticle(p_174974_1_, p_174974_3_, p_174974_5_, p_174974_7_, p_174974_9_, p_174974_11_, p_174974_13_, p_174974_15_));
            }
        }
        else
        {
            return null;
        }
    }
    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound tag)
    {
        super.readEntityFromNBT(tag);
        this.essence = new PotionEssence(tag);
        setRenderColor(tag.getInteger("color"));
        if (this.essence.getEffects().size() == 0)
            this.setDead();
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound tag)
    {
        super.writeEntityToNBT(tag);
        tag.setTag("potion", essence == null ? new NBTTagCompound() : essence.writeToNBT());
        tag.setInteger("color", getRenderColor());
    }

    public int getRenderColor() {
        return renderColor;
    }

    private static final int COLOR = 1;

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeInt(renderColor);
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        setRenderColor(additionalData.readInt());
    }

    public void setRenderColor(int renderColor) {
        this.renderColor = renderColor;
    }
}

