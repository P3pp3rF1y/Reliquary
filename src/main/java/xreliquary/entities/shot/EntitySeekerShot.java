package xreliquary.entities.shot;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntitySeekerShot extends EntityShotBase {
	@SuppressWarnings("unused")
	public EntitySeekerShot(World world) {
		super(world);
	}

	public EntitySeekerShot(World world, EntityPlayer player, EnumHand hand) {
		super(world, player, hand);
	}

	@Override
	void doFlightEffects() {
		if(ticksInAir % 3 == 0)
			world.spawnParticle(EnumParticleTypes.SPELL_MOB_AMBIENT, posX, posY, posZ, 0.0F, gaussian(1.0F), gaussian(1.0F));

		// housed in the base class
		seekTarget();
	}

	@Override
	void doFiringEffects() {
		world.spawnParticle(EnumParticleTypes.SPELL_MOB_AMBIENT, posX + smallGauss(0.1D), posY + smallGauss(0.1D), posZ + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
		world.spawnParticle(EnumParticleTypes.FLAME, posX, posY, posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));
	}

	@Override
	void doBurstEffect(EnumFacing sideHit) {
		// does nothing
	}

	@Override
	void spawnHitParticles(int i) {
		for(int particles = 0; particles < i; particles++)
			world.spawnParticle(EnumParticleTypes.SPELL_MOB_AMBIENT, posX + smallGauss(0.1D), posY + smallGauss(0.1D), posZ + smallGauss(0.1D), 0.1F, 1.0F, 1.0F);
	}

	@Override
	int getRicochetMax() {
		return 3;
	}

	@Override
	int getDamageOfShot(EntityLivingBase mop) {
		return 10 + d12();
	}
}
