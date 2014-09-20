package xreliquary.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityEnderShot extends EntityShotBase {
    public EntityEnderShot(World par1World) {
        super(par1World);
    }

    public EntityEnderShot(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
    }

    public EntityEnderShot(World par1World, EntityPlayer par2EntityPlayer) {
        super(par1World, par2EntityPlayer);
    }

    private void doPortalExplosion() {
        for (int particles = 0; particles < 3; particles++) {
            worldObj.spawnParticle("portal", posX, posY - 1, posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));
            worldObj.spawnParticle("smoke", posX, posY, posZ, 0, 0, 0);
        }
        this.setDead();
    }

    @Override
    void doBurstEffect(int sideHit) {
        // there aren't any burst effects because ender shots pass through
        // everything.
    }

    @Override
    void doFiringEffects() {
        worldObj.spawnParticle("mobSpellAmbient", posX + smallGauss(0.1D), posY + smallGauss(0.1D), posZ + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
        worldObj.spawnParticle("flame", posX, posY, posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));
    }

    @Override
    void onImpact(MovingObjectPosition mop) {
        if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && mop.entityHit != null) {
            if (mop.entityHit == shootingEntity)
                return;
            if (!(mop.entityHit instanceof EntityLivingBase))
                return;
            this.onImpact((EntityLivingBase)mop.entityHit);
        }
        // note that there is no tile impact.
    }

    @Override
    void doFlightEffects() {
        if (ticksInAir % 3 == 0)
            worldObj.spawnParticle("portal", posX, posY - 1, posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));

        // housed in the base class
        seekTarget();

        // these only last 5 seconds, to prevent them from killing too terribly
        // much.
        if (ticksInAir > 100)
            doPortalExplosion();
    }

    @Override
    void onImpact(EntityLivingBase mop) {
        if (mop != shootingEntity || ticksInAir > 3) {
            doDamage(mop);
        }
        spawnHitParticles("witchMagic", 8);
    }

    @Override
    void spawnHitParticles(String string, int i) {
        for (int particles = 0; particles < i; particles++) {
            worldObj.spawnParticle(string, posX, posY - (string == "portal" ? 1 : 0), posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));
        }
    }

    @Override
    int getRicochetMax() {
        return 0;
    }

    @Override
    int getDamageOfShot(EntityLivingBase mop) {
        return 16 + d12();
    }
}
