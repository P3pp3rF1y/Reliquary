package xreliquary.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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
			worldObj.spawnParticle("mobSpellAmbient", posX, posY, posZ, 0.0F, gaussian(1.0F), gaussian(1.0F));

		// housed in the base class
		seekTarget();
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
		// does nothing
	}

	@Override
	void onImpact(Entity mop) {
		if (mop != shootingEntity || ticksInAir > 3) {
			doDamage(mop);
		}
		spawnHitParticles("mobSpellAmbient", 8);
		this.setDead();
	}

	@Override
	void spawnHitParticles(String string, int i) {
		for (int particles = 0; particles < i; particles++)
			worldObj.spawnParticle(string, posX + smallGauss(0.1D), posY + smallGauss(0.1D), posZ + smallGauss(0.1D), 0.1F, 1.0F, 1.0F);
	}

	@Override
	int getRicochetMax() {
		return 3;
	}

	@Override
	int getDamageOfShot(Entity mop) {
		return 10 + d12();
	}
}
