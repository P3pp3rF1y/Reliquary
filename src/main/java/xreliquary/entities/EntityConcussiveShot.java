package xreliquary.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityConcussiveShot extends EntityShotBase {
	public EntityConcussiveShot(World par1World) {
		super(par1World);
	}

	public EntityConcussiveShot(World par1World, double par2, double par4, double par6) {
		super(par1World, par2, par4, par6);
	}

	public EntityConcussiveShot(World par1World, EntityPlayer par2EntityPlayer) {
		super(par1World, par2EntityPlayer);
	}

    @Override
    int getRicochetMax() {
        return 0;
    }

    @Override
    int getDamageOfShot(Entity mop) {
        return 8 + d6();
    }

    @Override
    void doFlightEffects() {
		if (ticksInAir % 3 == 0) {
			worldObj.spawnParticle("smoke", posX, posY, posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));
		}
	}

    @Override
    void spawnHitParticles(String string, int i) {
        //no need
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
    void onImpact(Entity mop) {
		if (mop != shootingEntity || ticksInAir > 3)
			doDamage( mop);
		ConcussiveExplosion.customConcussiveExplosion(this, shootingEntity, posX, posY, posZ, 1.5F, true, true);
		this.setDead();
	}

    @Override
    void doBurstEffect(int sideHit) {
		ConcussiveExplosion.customConcussiveExplosion(this, shootingEntity, posX, posY, posZ, 1.5F, true, true);
		this.setDead();
	}
}
