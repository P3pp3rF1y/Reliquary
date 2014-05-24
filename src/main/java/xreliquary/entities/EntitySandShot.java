package xreliquary.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntitySandShot extends EntityShotBase {
	public EntitySandShot(World par1World) {
		super(par1World);
	}

	public EntitySandShot(World par1World, double par2, double par4, double par6) {
		super(par1World, par2, par4, par6);
	}

	public EntitySandShot(World par1World, EntityPlayer par2EntityPlayer) {
		super(par1World, par2EntityPlayer);
	}

    @Override
    void doFiringEffects() {
		worldObj.spawnParticle("mobSpellAmbient", posX + smallGauss(0.1D), posY + smallGauss(0.1D), posZ + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
		worldObj.spawnParticle("flame", posX, posY, posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));
	}

    @Override
    void doFlightEffects() {
        if (ticksInAir % 3 == 0)
            spawnHitParticles("reddust", 1);
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
        spawnHitParticles("reddust", 8);
    }

    @Override
    void onImpact(Entity mop) {
		if (mop != shootingEntity || ticksInAir > 3) {
			doDamage(mop);
		}
		spawnHitParticles("reddust", 8);
		this.setDead();
	}

    @Override
    void spawnHitParticles(String string, int i) {
		for (int particles = 0; particles < i; particles++) {
			worldObj.spawnParticle(string, posX, posY - (string == "portal" ? 1 : 0), posZ, 0.7F, 0.7F, 0.3F);
		}
	}

    @Override
    int getDamageOfShot(Entity mop) {
        //creepers turn sand shots into straight explosions.
        if (mop instanceof EntityCreeper) {
            ConcussiveExplosion.customBusterExplosion(this, shootingEntity, posX, posY, posZ, 2.0F, false, true);
            return 0;
        }
        //it also causes blinding
        if (mop instanceof EntityLiving)
            ((EntityLiving) mop).addPotionEffect(new PotionEffect(Potion.blindness.id, 200, 1));
        return  (worldObj.getWorldInfo().isRaining() ? 4 : 8) + d6();
    }

    @Override
    int getRicochetMax() {
		return 0;
	}
}
