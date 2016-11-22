package xreliquary.entities.shot;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import xreliquary.entities.ConcussiveExplosion;

public class EntityBusterShot extends EntityShotBase {
	@SuppressWarnings("unused")
	public EntityBusterShot(World par1World) {
		super(par1World);
	}

	/* TODO remove
		public EntityBusterShot(World par1World, double par2, double par4, double par6) {
			super(par1World, par2, par4, par6);
		}

	*/
	public EntityBusterShot(World par1World, EntityPlayer par2EntityPlayer, EnumHand hand) {
		super(par1World, par2EntityPlayer, hand);
	}

	@Override
	void doBurstEffect(EnumFacing sideHit) {
		ConcussiveExplosion.customBusterExplosion(this, posX, posY, posZ, 4.0F);
	}

	@Override
	void doFiringEffects() {
		world.spawnParticle(EnumParticleTypes.SPELL_MOB_AMBIENT, posX + smallGauss(0.1D), posY + smallGauss(0.1D), posZ + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
		world.spawnParticle(EnumParticleTypes.FLAME, posX, posY, posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));
	}

	@Override
	void doFlightEffects() {
		if(ticksInAir % 3 == 0)
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, posX, posY, posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));
	}

	@Override
	void spawnHitParticles(int i) {
		// no need, really.
	}

	@Override
	protected void onImpact(EntityLivingBase entityLiving) {
		ConcussiveExplosion.customBusterExplosion(this, posX, posY, posZ, 4.0F);
		super.onImpact(entityLiving);
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
