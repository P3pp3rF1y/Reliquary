package xreliquary.entities;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

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

    @Override
    protected float func_70182_d() {
        return 0.7F;
    }

    @Override
    protected float func_70183_g() {
        return -20.0F;
    }

    private boolean isUndead(EntityLivingBase e) {
        return e.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD;
    }

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    @Override
    protected void onImpact(MovingObjectPosition mop) {
        if (!worldObj.isRemote) {
            this.spawnParticles();
            AxisAlignedBB bb = boundingBox.expand(4.0D, 2.0D, 4.0D);
            List eList = worldObj.getEntitiesWithinAABB(EntityLiving.class, bb);
            Iterator i = eList.iterator();
            while (i.hasNext()) {
                EntityLiving e = (EntityLiving) i.next();
                if (isUndead(e) && this.getThrower() != null && this.getThrower() instanceof EntityPlayer) {
                    e.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) this.getThrower()), 18 + rand.nextInt(17));
                }
            }

            worldObj.playAuxSFX(2002, (int) Math.round(posX), (int) Math.round(posY), (int) Math.round(posZ), 0);
            this.setDead();
        }
    }

    private void spawnParticles() {
        double x = posX;
        double y = posY;
        double z = posZ;
        String itemBreakIcon = "iconcrack_" + Item.getIdFromItem(Items.potionitem);
        for (int particleNum = 0; particleNum < 8; ++particleNum) {
            worldObj.spawnParticle(itemBreakIcon, x, y, z, rand.nextGaussian() * 0.15D, rand.nextDouble() * 0.2D, rand.nextGaussian() * 0.15D);
        }

        float red = 1.0F;
        float green = 1.0F;
        float blue = 0.0F;
        String nameOfParticle = "spell";

        for (int particleNum = 0; particleNum < 100; ++particleNum) {
            double velocityCoefficient = rand.nextDouble() * 4.0D;
            double radian = rand.nextDouble() * Math.PI * 2.0D;
            double xVel = Math.cos(radian) * velocityCoefficient;
            double yVel = 0.01D + rand.nextDouble() * 0.5D;
            double zVel = Math.sin(radian) * velocityCoefficient;
            if (worldObj.isRemote) {
                EntityFX effect = Minecraft.getMinecraft().renderGlobal.doSpawnParticle(nameOfParticle, x + xVel * 0.1D, y + 0.3D, z + zVel * 0.1D, xVel, yVel, zVel);
                if (effect != null) {
                    float variance = 0.75F + rand.nextFloat() * 0.25F;
                    effect.setRBGColorF(red * variance, green * variance, blue * variance);
                    effect.multiplyVelocity((float) velocityCoefficient);
                }
            }
        }

        worldObj.playSoundEffect(posX + 0.5D, posY + 0.5D, posZ + 0.5D, "dig.glass", 1.0F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
    }
}
