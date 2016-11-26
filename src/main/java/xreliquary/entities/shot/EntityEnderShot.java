package xreliquary.entities.shot;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityEnderShot extends EntityShotBase {
	@SuppressWarnings("unused")
	public EntityEnderShot(World world) {
		super(world);
	}

	public EntityEnderShot(World world, EntityPlayer player, EnumHand hand) {
		super(world, player, hand);
	}

	private void doPortalExplosion() {
		for(int particles = 0; particles < 3; particles++) {
			world.spawnParticle(EnumParticleTypes.PORTAL, posX, posY - 1, posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, posX, posY, posZ, 0, 0, 0);
		}
		this.setDead();
	}

	@Override
	void doBurstEffect(EnumFacing sideHit) {
		// there aren't any burst effects because ender shots pass through
		// everything.
	}

	@Override
	void doFiringEffects() {
		world.spawnParticle(EnumParticleTypes.SPELL_MOB_AMBIENT, posX + smallGauss(0.1D), posY + smallGauss(0.1D), posZ + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
		world.spawnParticle(EnumParticleTypes.FLAME, posX, posY, posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if(result.typeOfHit == RayTraceResult.Type.ENTITY && result.entityHit != null) {
			if(result.entityHit == shootingEntity)
				return;
			if(!(result.entityHit instanceof EntityLivingBase))
				return;
			this.onImpact((EntityLivingBase) result.entityHit);
		}
		// note that there is no tile impact.
	}

	@Override
	protected void onImpact(EntityLivingBase mop) {
		if(mop != shootingEntity || ticksInAir > 3) {
			doDamage(mop);
		}
		spawnHitParticles(8);

		//continues after hit and thus no setting dead here
	}

	@Override
	protected void groundImpact(EnumFacing SideHit) {
		//do absolutely nothing. this avoids a death sentence.
	}

	@Override
	void doFlightEffects() {
		if(ticksInAir % 3 == 0)
			world.spawnParticle(EnumParticleTypes.PORTAL, posX, posY - 1, posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));

		// housed in the base class
		seekTarget();

		// these only last 5 seconds, to prevent them from killing too terribly
		// much.
		if(ticksInAir > 100)
			doPortalExplosion();
	}

	@Override
	void spawnHitParticles(int i) {
		for(int particles = 0; particles < i; particles++) {
			world.spawnParticle(EnumParticleTypes.SPELL_WITCH, posX, posY, posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));
		}
	}

	@Override
	int getRicochetMax() {
		return 0;
	}

	@Override
	int getDamageOfShot(EntityLivingBase mop) {
		return 16 + d12();
	}
}
