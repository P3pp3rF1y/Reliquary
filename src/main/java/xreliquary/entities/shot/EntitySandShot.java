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
import net.minecraft.world.World;
import xreliquary.entities.ConcussiveExplosion;

public class EntitySandShot extends EntityShotBase {
	@SuppressWarnings("unused")
	public EntitySandShot(World world) {
		super(world);
	}

	public EntitySandShot(World world, EntityPlayer player, EnumHand hand) {
		super(world, player, hand);
	}

	@Override
	void doFiringEffects() {
		world.spawnParticle(EnumParticleTypes.SPELL_MOB_AMBIENT, posX + smallGauss(0.1D), posY + smallGauss(0.1D), posZ + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
		world.spawnParticle(EnumParticleTypes.FLAME, posX, posY, posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));
	}

	@Override
	void doFlightEffects() {
		if(ticksInAir % 3 == 0)
			spawnHitParticles(1);
	}

	@Override
	void doBurstEffect(EnumFacing sideHit) {
		spawnHitParticles(8);
	}

	@Override
	void spawnHitParticles(int i) {
		for(int particles = 0; particles < i; particles++) {
			world.spawnParticle(EnumParticleTypes.REDSTONE, posX, posY, posZ, 0.7F, 0.7F, 0.3F);
		}
	}

	@Override
	int getDamageOfShot(EntityLivingBase e) {
		// creepers turn sand shots into straight explosions.
		if(e instanceof EntityCreeper) {
			ConcussiveExplosion.customBusterExplosion(this, posX, posY, posZ, 2.0F);
			e.attackEntityFrom(DamageSource.causePlayerDamage(shootingEntity), 20);
			return 0;
		}
		// it also causes blinding
		if(e instanceof EntityLiving)
			e.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 200, 1));
		return (world.getWorldInfo().isRaining() ? 4 : 8) + d6();
	}

	@Override
	int getRicochetMax() {
		return 0;
	}
}
