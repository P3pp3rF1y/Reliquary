package xreliquary.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityExorcismShot extends EntityShotBase {
    public EntityExorcismShot(World par1World) {
        super(par1World);
    }

    public EntityExorcismShot(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
    }

    public EntityExorcismShot(World par1World, EntityPlayer par2EntityPlayer) {
        super(par1World, par2EntityPlayer);
    }

    @Override
    void doFlightEffects() {
        if (ticksInAir % 3 == 0) {
            double gauss = gaussian(1.0F);
            worldObj.spawnParticle("mobSpell", posX, posY, posZ, gauss, gauss, 0.0F);
        }
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
            this.onImpact(mop.entityHit);
        } else if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            this.groundImpact(mop.sideHit);
        }
    }

    @Override
    void doBurstEffect(int sideHit) {
        // none really.
    }

    @Override
    void onImpact(Entity mop) {
        if (mop != shootingEntity || ticksInAir > 3) {
            doDamage(mop);
        }
        // unfortunately this isn't a traditional call of spawnHitParticles or
        // we could factor out the whole method. "mobSpellAmbient" is a weird
        // particle.
        spawnHitParticles("mobSpellAmbient", 8);
        this.setDead();
    }

    @Override
    void spawnHitParticles(String string, int i) {
        for (int particles = 0; particles < i; particles++)
            worldObj.spawnParticle(string, posX + smallGauss(0.1D), posY + smallGauss(0.1D), posZ + smallGauss(0.1D), posGauss(1.0F), posGauss(1.0F), 0.0F);
    }

    boolean isUndead(Entity mop) {
        return mop instanceof EntitySkeleton || mop instanceof EntityGhast || mop instanceof EntityWither || mop instanceof EntityZombie || mop instanceof EntityPigZombie;
    }

    @Override
    int getRicochetMax() {
        return 1;
    }

    @Override
    int getDamageOfShot(Entity mop) {
        // there is a relatively small chance this will fail to kill an undead
        // creature.
        // undead take 10-12 damage, in addition to the 9-14 damage they would
        // normally do.
        return (isUndead(mop) ? (9 + d3()) : 0) + 8 + d6();
    }

}
