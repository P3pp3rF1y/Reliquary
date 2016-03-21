package xreliquary.entities;


import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Iterator;
import java.util.List;

public class EntityGlowingWater extends EntityThrowable {
    public EntityGlowingWater(World par1World) {
        super(par1World);
    }

    public EntityGlowingWater(World par1World, EntityPlayer par2EntityPlayer) {
        super(par1World, par2EntityPlayer);
    }

    @SideOnly(Side.CLIENT)
    public EntityGlowingWater(World par1World, double par2, double par4, double par6, int par8) {
        this(par1World, par2, par4, par6);
    }

    public EntityGlowingWater(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
    }

    /**
     * Gets the amount of gravity to apply to the thrown entity with each tick.
     */
    @Override
    protected float getGravityVelocity() {
        return 0.03F;
    }

    private boolean isUndead(EntityLivingBase e) {
        return e.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD;
    }

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    @Override
    protected void onImpact(RayTraceResult result) {
        if (!worldObj.isRemote) {
            this.spawnParticles();
            AxisAlignedBB bb = this.getEntityBoundingBox().expand( 4.0D, 2.0D, 4.0D );
            List eList = worldObj.getEntitiesWithinAABB(EntityLiving.class, bb);
            Iterator i = eList.iterator();
            while (i.hasNext()) {
                EntityLiving e = (EntityLiving) i.next();
                if (isUndead(e) && this.getThrower() != null && this.getThrower() instanceof EntityPlayer) {
                    e.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) this.getThrower()), 18 + rand.nextInt(17));
                }
            }

            worldObj.playAuxSFX(2002, new BlockPos(this), 0);
            this.setDead();
        }
    }

    private void spawnParticles() {
        double x = posX;
        double y = posY;
        double z = posZ;

        for (int particleNum = 0; particleNum < 8; ++particleNum) {
            worldObj.spawnParticle( EnumParticleTypes.ITEM_CRACK, x, y, z, rand.nextGaussian() * 0.15D, rand.nextDouble() * 0.2D, rand.nextGaussian() * 0.15D, new int[] {Item.getIdFromItem(Items.potionitem)});
        }

        float red = 1.0F;
        float green = 1.0F;
        float blue = 0.0F;

        for (int particleNum = 0; particleNum < 100; ++particleNum) {
            double velocityCoefficient = rand.nextDouble() * 4.0D;
            double radian = rand.nextDouble() * Math.PI * 2.0D;
            double xVel = Math.cos(radian) * velocityCoefficient;
            double yVel = 0.01D + rand.nextDouble() * 0.5D;
            double zVel = Math.sin(radian) * velocityCoefficient;
            if (worldObj.isRemote) {
                EntityFX effect = Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.SPELL.getParticleID(), x + xVel * 0.1D, y + 0.3D, z + zVel * 0.1D, xVel, yVel, zVel );
                if (effect != null) {
                    float variance = 0.75F + rand.nextFloat() * 0.25F;
                    effect.setRBGColorF(red * variance, green * variance, blue * variance);
                    effect.multiplyVelocity((float) velocityCoefficient);
                }
            }
        }

        worldObj.playSound(null, getPosition(), SoundEvents.block_glass_break, SoundCategory.NEUTRAL, 1.0F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
    }
}
