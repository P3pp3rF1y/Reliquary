package xreliquary.entities.shot;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import xreliquary.entities.ConcussiveExplosion;

public class EntityConcussiveShot extends EntityShotBase {
	public EntityConcussiveShot(World par1World) {
		super(par1World);
	}

	public EntityConcussiveShot(World par1World, double par2, double par4, double par6) {
		super(par1World, par2, par4, par6);
	}

	public EntityConcussiveShot(World par1World, EntityPlayer par2EntityPlayer, EnumHand hand) {
		super(par1World, par2EntityPlayer, hand);
	}

	@Override
	int getRicochetMax() {
		return 0;
	}

	@Override
	int getDamageOfShot(EntityLivingBase mop) {
		return 8 + d6();
	}

	@Override
	void doFlightEffects() {
		if(ticksInAir % 3 == 0) {
			worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, posX, posY, posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));
		}
	}

	@Override
	void spawnHitParticles(int i) {
		// no need
	}

	@Override
	void doFiringEffects() {
		worldObj.spawnParticle(EnumParticleTypes.SPELL_MOB_AMBIENT, posX + smallGauss(0.1D), posY + smallGauss(0.1D), posZ + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
		worldObj.spawnParticle(EnumParticleTypes.FLAME, posX, posY, posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if(result.typeOfHit == RayTraceResult.Type.ENTITY && result.entityHit != null) {
			if(result.entityHit == shootingEntity)
				return;
			if(!(result.entityHit instanceof EntityLivingBase))
				return;
			this.onImpact((EntityLivingBase) result.entityHit);
		} else if(result.typeOfHit == RayTraceResult.Type.BLOCK) {
			this.groundImpact(result.sideHit);
		}
	}

	@Override
	void onImpact(EntityLivingBase mop) {
		if(mop != shootingEntity || ticksInAir > 3)
			doDamage(mop);
		ConcussiveExplosion.customConcussiveExplosion(this, shootingEntity, posX, posY, posZ, 1.5F, true, true);
		this.setDead();
	}

	@Override
	void doBurstEffect(EnumFacing sideHit) {
		ConcussiveExplosion.customConcussiveExplosion(this, shootingEntity, posX, posY, posZ, 1.5F, true, true);
		this.setDead();
	}
}
