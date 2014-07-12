package xreliquary.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityBlazeShot extends EntityShotBase {
	public EntityBlazeShot(World par1World) {
		super(par1World);
	}

	public EntityBlazeShot(World par1World, double par2, double par4, double par6) {
		super(par1World);
	}

	public EntityBlazeShot(World par1World, EntityPlayer par2EntityPlayer) {
		super(par1World, par2EntityPlayer);
	}

	@Override
	void doFlightEffects() {
		if (ticksInAir % 3 == 0 && ticksInAir < 9) {
			worldObj.spawnParticle("flame", posX, posY, posZ, lowGauss(motionX), lowGauss(motionY), lowGauss(motionZ));
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
	void onImpact(Entity mop) {
		if (mop != shootingEntity || ticksInAir > 3) {
			doDamage(mop);
			mop.setFire(40);
		}
		spawnHitParticles("flame", 8);
		this.setDead();
	}

	@Override
	void spawnHitParticles(String string, int i) {
		for (int particles = 0; particles < i; particles++) {
			worldObj.spawnParticle(string, posX, posY - (string == "portal" ? 1 : 0), posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));
		}
	}

	@Override
	protected void groundImpact(int sideHit) {
	}

	@Override
	int getRicochetMax() {
		return 0;
	}

	@Override
	int getDamageOfShot(Entity mop) {
		// they're not COMPLETELY useless against fireImmune mobs, just mostly
		// useless.
		return mop.isImmuneToFire() ? 2 : (10 + d12());
	}

	@Override
	void doBurstEffect(int sideHit) {
		for (int particles = 0; particles < 40; particles++) {
			switch (sideHit) {
			case 0:
				worldObj.spawnParticle("flame", posX, posY, posZ, smallGauss(0.1D) + motionX / 4, -posGauss(0.2D), smallGauss(0.2D) + motionZ / 4);
				break;
			case 1:
				worldObj.spawnParticle("flame", posX, posY, posZ, smallGauss(0.1D) + motionX / 4, posGauss(0.2D), smallGauss(0.2D) + motionZ / 4);
				break;
			case 2:
				worldObj.spawnParticle("flame", posX, posY, posZ, smallGauss(0.1D) + motionX / 4, smallGauss(0.1D) + motionY / 4, -posGauss(0.2D));
				break;
			case 3:
				worldObj.spawnParticle("flame", posX, posY, posZ, smallGauss(0.1D) + motionX / 4, smallGauss(0.1D) + motionY / 4, posGauss(0.2D));
				break;
			case 4:
				worldObj.spawnParticle("flame", posX, posY, posZ, -posGauss(0.2D), smallGauss(0.1D) + motionY / 4, smallGauss(0.1D) + motionZ / 4);
				break;
			case 5:
				worldObj.spawnParticle("flame", posX, posY, posZ, posGauss(0.2D), smallGauss(0.1D) + motionY / 4, smallGauss(0.1D) + motionZ / 4);
				break;
			}
		}
	}
}
