package xreliquary.entities.shot;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntitySeekerShot extends EntityShotBase {
    public EntitySeekerShot(World par1World) {
        super(par1World);
    }

    public EntitySeekerShot(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
    }

    public EntitySeekerShot(World par1World, EntityPlayer par2EntityPlayer) {
        super(par1World, par2EntityPlayer);
    }

    @Override
    void doFlightEffects() {
        if (ticksInAir % 3 == 0)
            worldObj.spawnParticle(EnumParticleTypes.SPELL_MOB_AMBIENT, posX, posY, posZ, 0.0F, gaussian(1.0F), gaussian(1.0F));

        // housed in the base class
        seekTarget();
    }

    @Override
    void doFiringEffects() {
        worldObj.spawnParticle(EnumParticleTypes.SPELL_MOB_AMBIENT, posX + smallGauss(0.1D), posY + smallGauss(0.1D), posZ + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
        worldObj.spawnParticle(EnumParticleTypes.FLAME, posX, posY, posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));
    }

    @Override
    protected void onImpact(MovingObjectPosition mop) {
        if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && mop.entityHit != null) {
            if (mop.entityHit == shootingEntity)
                return;
            if (!(mop.entityHit instanceof EntityLivingBase))
                return;
            this.onImpact((EntityLivingBase)mop.entityHit);
        } else if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            this.groundImpact(mop.sideHit);
        }
    }

    @Override
    void doBurstEffect(EnumFacing sideHit) {
        // does nothing
    }

    @Override
    void onImpact(EntityLivingBase mop) {
        if (mop != shootingEntity || ticksInAir > 3) {
            doDamage(mop);
        }
        spawnHitParticles(8);
        this.setDead();
    }

    @Override
    void spawnHitParticles(int i) {
        for (int particles = 0; particles < i; particles++)
            worldObj.spawnParticle(EnumParticleTypes.SPELL_MOB_AMBIENT, posX + smallGauss(0.1D), posY + smallGauss(0.1D), posZ + smallGauss(0.1D), 0.1F, 1.0F, 1.0F);
    }

    @Override
    int getRicochetMax() {
        return 3;
    }

    @Override
    int getDamageOfShot(EntityLivingBase mop) {
        return 10 + d12();
    }
}
