package xreliquary.entities.potion;

import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by Xeno on 3/11/14.
 */
public abstract class EntityThrownPotion extends EntityThrowable {
    public EntityThrownPotion(World par1World) {
        super(par1World);
    }

    public EntityThrownPotion(World par1World, EntityPlayer par2EntityPlayer) {
        super(par1World, par2EntityPlayer);
    }

    @SideOnly(Side.CLIENT)
    public EntityThrownPotion(World par1World, double par2, double par4, double par6, int par8) {
        this(par1World, par2, par4, par6);
    }

    public EntityThrownPotion(World par1World, double par2, double par4, double par6) {
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
    protected float getVelocity() {
        return 0.7F;
    }

    @Override
    protected float getInaccuracy() {
        return -20.0F;
    }

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    @Override
    protected void onImpact(MovingObjectPosition mop) {
        this.spawnParticles();
        this.doSplashEffect();
        this.setDead();
    }

    // called by the splashEffect so that it can skip the bounding box and
    // entity iteration, just keeps things moving.
    abstract boolean hasLivingEntityEffect();

    protected void doSplashEffect() {
        this.doGroundSplashEffect();
        if (!this.hasLivingEntityEffect())
            return;
        AxisAlignedBB bb = this.getEntityBoundingBox().expand( 4.0D, 2.0D, 4.0D );
        List eList = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, bb);
        Iterator i = eList.iterator();
        while (i.hasNext()) {
            EntityLivingBase e = (EntityLivingBase) i.next();
            this.doLivingSplashEffect(e);
        }
    }

    // called by doSplash effect, allows user to override the ground effect,
    // since most of the potion don't have one.
    abstract void doGroundSplashEffect();

    // most of these are the same in every potion, the only thing that isn't is
    // the coloration of the particles.
    protected void spawnParticles() {
        //TODO: fix the variable names
        Random var7 = rand;
        for (int var15 = 0; var15 < 8; ++var15) {
            worldObj.spawnParticle( EnumParticleTypes.ITEM_CRACK, this.posX, this.posY, this.posZ, var7.nextGaussian() * 0.15D, var7.nextDouble() * 0.2D, var7.nextGaussian() * 0.15D, Item.getIdFromItem(Items.potionitem));
        }

        String var19 = "spell";

        for (int var20 = 0; var20 < 100; ++var20) {
            double var39 = var7.nextDouble() * 4.0D;
            double var23 = var7.nextDouble() * Math.PI * 2.0D;
            double var25 = Math.cos(var23) * var39;
            double var27 = 0.01D + var7.nextDouble() * 0.5D;
            double var29 = Math.sin(var23) * var39;
            if (worldObj.isRemote) {
                EntityFX var31 = Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.SPELL.getParticleID(), this.posX + var25 * 0.1D, this.posY + 0.3D, this.posZ + var29 * 0.1D, var25, var27, var29, new int[0]);
                if (var31 != null) {
                    float var32 = 0.75F + var7.nextFloat() * 0.25F;
                    var31.setRBGColorF(this.getRed() * var32, this.getGreen() * var32, this.getBlue() * var32);
                    var31.multiplyVelocity((float) var39);
                }
            }
        }

        worldObj.playSoundEffect(posX + 0.5D, posY + 0.5D, posZ + 0.5D, "dig.glass", 1.0F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
    }

    // this gets called inside the on-impact method on EVERY living entity
    // within the AOE
    abstract void doLivingSplashEffect(EntityLivingBase e);

    // these are just the getters for the particle coloration. They're all the
    // same particle style, so it's really just a matter of coloration.
    abstract float getRed();

    abstract float getGreen();

    abstract float getBlue();
}
