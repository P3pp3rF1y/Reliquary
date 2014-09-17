package xreliquary.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityBusterShot extends EntityShotBase {
    public EntityBusterShot(World par1World) {
        super(par1World);
    }

    public EntityBusterShot(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
    }

    public EntityBusterShot(World par1World, EntityPlayer par2EntityPlayer) {
        super(par1World, par2EntityPlayer);
    }

    @Override
    void onImpact(MovingObjectPosition mop) {
        if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && mop.entityHit != null) {
            if (mop.entityHit == shootingEntity)
                return;
            this.onImpact(mop.entityHit);
        } else if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            this.groundImpact(mop.sideHit);
    }

    @Override
    void doBurstEffect(int sideHit) {
        ConcussiveExplosion.customBusterExplosion(this, shootingEntity, posX, posY, posZ, 4.0F, false, true);
        this.setDead();
    }

    @Override
    void doFiringEffects() {
        worldObj.spawnParticle("mobSpellAmbient", posX + smallGauss(0.1D), posY + smallGauss(0.1D), posZ + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
        worldObj.spawnParticle("flame", posX, posY, posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));
    }

    @Override
    void doFlightEffects() {
        if (ticksInAir % 3 == 0)
            worldObj.spawnParticle("smoke", posX, posY, posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));
    }

    @Override
    void spawnHitParticles(String string, int i) {
        // no need, really.
    }

    @Override
    void onImpact(Entity mop) {
        if (mop != shootingEntity || ticksInAir > 3)
            doDamage(mop);
        this.doBurstEffect();
    }

    @Override
    int getRicochetMax() {
        // uh.. yeah, buster shots don't ricochet.
        return 0;
    }

    @Override
    int getDamageOfShot(Entity mop) {
        // if the damage doesn't kill them, the explosion will.
        // Buster shots are almost guaranteed fatal.
        return 8 + d6();
    }
}
