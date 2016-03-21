package xreliquary.entities.shot;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import xreliquary.entities.ConcussiveExplosion;

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
	protected void onImpact(RayTraceResult result) {
		if(result.typeOfHit == RayTraceResult.Type.ENTITY && result.entityHit != null) {
			if(result.entityHit == shootingEntity)
				return;
			if(!(result.entityHit instanceof EntityLivingBase))
				return;
			this.onImpact((EntityLivingBase) result.entityHit);
		} else if(result.typeOfHit == RayTraceResult.Type.BLOCK)
			this.groundImpact(result.sideHit);
	}

	@Override
	void doBurstEffect(EnumFacing sideHit) {
		ConcussiveExplosion.customBusterExplosion(this, shootingEntity, posX, posY, posZ, 4.0F, false, true);
		this.setDead();
	}

	@Override
	void doFiringEffects() {
		worldObj.spawnParticle(EnumParticleTypes.SPELL_MOB_AMBIENT, posX + smallGauss(0.1D), posY + smallGauss(0.1D), posZ + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
		worldObj.spawnParticle(EnumParticleTypes.FLAME, posX, posY, posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));
	}

	@Override
	void doFlightEffects() {
		if(ticksInAir % 3 == 0)
			worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, posX, posY, posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));
	}

	@Override
	void spawnHitParticles(int i) {
		// no need, really.
	}

	@Override
	void onImpact(EntityLivingBase mop) {
		if(mop != shootingEntity || ticksInAir > 3)
			doDamage(mop);
		this.doBurstEffect();
	}

	@Override
	int getRicochetMax() {
		// uh.. yeah, buster shots don't ricochet.
		return 0;
	}

	@Override
	int getDamageOfShot(EntityLivingBase mop) {
		// if the damage doesn't kill them, the explosion will.
		// Buster shots are almost guaranteed fatal.
		return 8 + d6();
	}
}
