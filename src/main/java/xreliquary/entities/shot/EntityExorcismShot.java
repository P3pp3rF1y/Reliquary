package xreliquary.entities.shot;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityExorcismShot extends EntityShotBase {
	public EntityExorcismShot(World par1World) {
		super(par1World);
	}

	public EntityExorcismShot(World par1World, double par2, double par4, double par6) {
		super(par1World, par2, par4, par6);
	}

	public EntityExorcismShot(World par1World, EntityPlayer par2EntityPlayer, EnumHand hand) {
		super(par1World, par2EntityPlayer, hand);
	}

	@Override
	void doFlightEffects() {
		if(ticksInAir % 3 == 0) {
			double gauss = gaussian(1.0F);
			worldObj.spawnParticle(EnumParticleTypes.SPELL_MOB, posX, posY, posZ, gauss, gauss, 0.0F);
		}
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
	void doBurstEffect(EnumFacing sideHit) {
		// none really.
	}

	@Override
	void onImpact(EntityLivingBase e) {
		if(e != shootingEntity || ticksInAir > 3) {
			doDamage(e);
		}
		// unfortunately this isn't a traditional call of spawnHitParticles or
		// we could factor out the whole method. "mobSpellAmbient" is a weird
		// particle.
		spawnHitParticles(8);
		this.setDead();
	}

	@Override
	void spawnHitParticles(int i) {
		for(int particles = 0; particles < i; particles++)
			worldObj.spawnParticle(EnumParticleTypes.SPELL_MOB_AMBIENT, posX + smallGauss(0.1D), posY + smallGauss(0.1D), posZ + smallGauss(0.1D), posGauss(1.0F), posGauss(1.0F), 0.0F);
	}

	private boolean isUndead(EntityLivingBase e) {
		return e.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD;
	}

	@Override
	int getRicochetMax() {
		return 1;
	}

	@Override
	int getDamageOfShot(EntityLivingBase e) {
		// there is a relatively small chance this will fail to kill an undead
		// creature.
		// undead take 10-12 damage, in addition to the 9-14 damage they would
		// normally do.
		return (isUndead(e) ? (9 + d3()) : 0) + 8 + d6();
	}

}
