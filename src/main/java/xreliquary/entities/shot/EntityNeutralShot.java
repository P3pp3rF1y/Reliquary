package xreliquary.entities.shot;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityNeutralShot extends EntityShotBase {
	@SuppressWarnings("unused")
	public EntityNeutralShot(World par1World) {
		super(par1World);
	}

	/* TODO remove
		public EntityNeutralShot(World par1World, double par2, double par4, double par6) {
			super(par1World, par2, par4, par6);
		}

	*/
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
	void doFiringEffects() {
		world.spawnParticle(EnumParticleTypes.SPELL_MOB_AMBIENT, posX + smallGauss(0.1D), posY + smallGauss(0.1D), posZ + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
		world.spawnParticle(EnumParticleTypes.FLAME, posX, posY, posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));
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
			world.spawnParticle(EnumParticleTypes.CRIT_MAGIC, posX, posY, posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));
		}
	}
}
