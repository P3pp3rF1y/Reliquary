package xreliquary.entities.shot;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityExorcismShot extends EntityShotBase {
	@SuppressWarnings("unused")
	public EntityExorcismShot(World world) {
		super(world);
	}

	public EntityExorcismShot(World world, EntityPlayer player, EnumHand hand) {
		super(world, player, hand);
	}

	@Override
	void doFlightEffects() {
		if(ticksInAir % 3 == 0) {
			double gauss = gaussian(1.0F);
			world.spawnParticle(EnumParticleTypes.SPELL_MOB, posX, posY, posZ, gauss, gauss, 0.0F);
		}
	}

	@Override
	void doFiringEffects() {
		world.spawnParticle(EnumParticleTypes.SPELL_MOB_AMBIENT, posX + smallGauss(0.1D), posY + smallGauss(0.1D), posZ + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
		world.spawnParticle(EnumParticleTypes.FLAME, posX, posY, posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));
	}

	@Override
	void doBurstEffect(EnumFacing sideHit) {
		// none really.
	}

	@Override
	void spawnHitParticles(int i) {
		for(int particles = 0; particles < i; particles++)
			world.spawnParticle(EnumParticleTypes.SPELL_MOB_AMBIENT, posX + smallGauss(0.1D), posY + smallGauss(0.1D), posZ + smallGauss(0.1D), posGauss(1.0F), posGauss(1.0F), 0.0F);
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
