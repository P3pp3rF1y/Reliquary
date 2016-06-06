package xreliquary.entities.shot;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityNeutralShot extends EntityShotBase {
	public EntityNeutralShot(World par1World) {
		super(par1World);
	}

	public EntityNeutralShot(World par1World, double par2, double par4, double par6) {
		super(par1World, par2, par4, par6);
	}

	public EntityNeutralShot(World par1World, EntityPlayer par2EntityPlayer, EnumHand hand) {
		super(par1World, par2EntityPlayer, hand);
	}

	@Override
	int getRicochetMax() {
		return 1;
	}

	@Override
	int getDamageOfShot(EntityLivingBase mop) {
		return 8 + d6();
	}

	@Override
	void onImpact(EntityLivingBase mop) {
		if(mop != shootingEntity || ticksInAir > 3)
			doDamage(mop);
		spawnHitParticles(8);
		this.setDead();
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
	void doFiringEffects() {
		worldObj.spawnParticle(EnumParticleTypes.SPELL_MOB_AMBIENT, posX + smallGauss(0.1D), posY + smallGauss(0.1D), posZ + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
		worldObj.spawnParticle(EnumParticleTypes.FLAME, posX, posY, posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));
	}

	@Override
	void doFlightEffects() {
		// none for neutral
	}

	@Override
	void doBurstEffect(EnumFacing sideHit) {
		// none for neutral
	}

	@Override
	void spawnHitParticles(int i) {
		for(int particles = 0; particles < i; particles++) {
			worldObj.spawnParticle(EnumParticleTypes.CRIT_MAGIC, posX, posY, posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));
		}
	}
}
