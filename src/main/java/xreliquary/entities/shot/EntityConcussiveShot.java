package xreliquary.entities.shot;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import xreliquary.entities.ConcussiveExplosion;

public class EntityConcussiveShot extends EntityShotBase {
	@SuppressWarnings("unused")
	public EntityConcussiveShot(World world) {
		super(world);
	}

	public EntityConcussiveShot(World world, EntityPlayer player, EnumHand hand) {
		super(world, player, hand);
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
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, posX, posY, posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));
		}
	}

	@Override
	void spawnHitParticles(int i) {
		// no need
	}

	@Override
	void doFiringEffects() {
		world.spawnParticle(EnumParticleTypes.SPELL_MOB_AMBIENT, posX + smallGauss(0.1D), posY + smallGauss(0.1D), posZ + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
		world.spawnParticle(EnumParticleTypes.FLAME, posX, posY, posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));
	}

	@Override
	protected void onImpact(EntityLivingBase entityLiving) {
		ConcussiveExplosion.customConcussiveExplosion(this, shootingEntity, posX, posY, posZ, 1.5F, true);
		super.onImpact(entityLiving);
	}

	@Override
	void doBurstEffect(EnumFacing sideHit) {
		ConcussiveExplosion.customConcussiveExplosion(this, shootingEntity, posX, posY, posZ, 1.5F, true);
		this.setDead();
	}
}
