package xreliquary.entities.shot;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import xreliquary.entities.ConcussiveExplosion;

public class EntitySandShot extends EntityShotBase {
	public EntitySandShot(World par1World) {
		super(par1World);
	}

	public EntitySandShot(World par1World, double par2, double par4, double par6) {
		super(par1World, par2, par4, par6);
	}

	public EntitySandShot(World par1World, EntityPlayer par2EntityPlayer, EnumHand hand) {
		super(par1World, par2EntityPlayer, hand);
	}

	@Override
	void doFiringEffects() {
		worldObj.spawnParticle(EnumParticleTypes.SPELL_MOB_AMBIENT, posX + smallGauss(0.1D), posY + smallGauss(0.1D), posZ + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
		worldObj.spawnParticle(EnumParticleTypes.FLAME, posX, posY, posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));
	}

	@Override
	void doFlightEffects() {
		if(ticksInAir % 3 == 0)
			spawnHitParticles(1);
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
		spawnHitParticles(8);
	}

	@Override
	void onImpact(EntityLivingBase mop) {
		if(mop != shootingEntity || ticksInAir > 3) {
			doDamage(mop);
		}
		spawnHitParticles(8);
		this.setDead();
	}

	@Override
	void spawnHitParticles(int i) {
		for(int particles = 0; particles < i; particles++) {
			worldObj.spawnParticle(EnumParticleTypes.REDSTONE, posX, posY, posZ, 0.7F, 0.7F, 0.3F);
		}
	}

	@Override
	int getDamageOfShot(EntityLivingBase e) {
		// creepers turn sand shots into straight explosions.
		if(e instanceof EntityCreeper) {
			ConcussiveExplosion.customBusterExplosion(this, shootingEntity, posX, posY, posZ, 2.0F, false, true);
			e.attackEntityFrom(DamageSource.causePlayerDamage(shootingEntity), 20);
			return 0;
		}
		// it also causes blinding
		if(e instanceof EntityLiving)
			e.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 200, 1));
		return (worldObj.getWorldInfo().isRaining() ? 4 : 8) + d6();
	}

	@Override
	int getRicochetMax() {
		return 0;
	}
}
