package xreliquary.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityNeutralShot extends EntityShotBase {
	public EntityNeutralShot(World par1World) {
		super(par1World);
	}

	public EntityNeutralShot(World par1World, double par2, double par4, double par6) {
		super(par1World,  par2,  par4,  par6);
	}

	public EntityNeutralShot(World par1World, EntityPlayer par2EntityPlayer) {
		super(par1World, par2EntityPlayer);
	}

    @Override
    int getRicochetMax() {
        return 1;
    }

    @Override
    int getDamageOfShot(Entity mop) {
        return 8 + d6();
    }

    @Override
    void onImpact(Entity mop) {
        if (mop != shootingEntity || ticksInAir > 3)
            doDamage(mop);
        spawnHitParticles("magicCrit", 8);
        this.setDead();
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
    void doFiringEffects() {
        worldObj.spawnParticle("mobSpellAmbient", posX + smallGauss(0.1D), posY + smallGauss(0.1D), posZ + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
        worldObj.spawnParticle("flame", posX, posY, posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));
    }

    @Override
    void doFlightEffects() {
        //none for neutral
    }

    @Override
    void doBurstEffect(int sideHit) {
        //none for neutral
    }

    @Override
    void spawnHitParticles(String string, int i) {
        for (int particles = 0; particles < i; particles++) {
            worldObj.spawnParticle(string, posX, posY - (string == "portal" ? 1 : 0), posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));
        }
    }
}
