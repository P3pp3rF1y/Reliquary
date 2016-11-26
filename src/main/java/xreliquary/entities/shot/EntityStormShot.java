package xreliquary.entities.shot;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityStormShot extends EntityShotBase {
	@SuppressWarnings("unused")
	public EntityStormShot(World world) {
		super(world);
	}

	public EntityStormShot(World world, EntityPlayer player, EnumHand hand) {
		super(world, player, hand);
	}

	@Override
	void doFiringEffects() {
		world.spawnParticle(EnumParticleTypes.SPELL_MOB_AMBIENT, posX + smallGauss(0.1D), posY + smallGauss(0.1D), posZ + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
		world.spawnParticle(EnumParticleTypes.FLAME, posX, posY, posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));
	}

	@Override
	void doFlightEffects() {
		// does nothing
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if(result.typeOfHit == RayTraceResult.Type.BLOCK) {
			if(world.isRainingAt(result.getBlockPos()) && world.getWorldInfo().isRaining() && world.getWorldInfo().isThundering())
				world.addWeatherEffect(new EntityLightningBolt(world, result.getBlockPos().getX(), result.getBlockPos().getY(), result.getBlockPos().getZ(), false));
		}
		super.onImpact(result);
	}

	@Override
	void doBurstEffect(EnumFacing sideHit) {
		// does nothing
	}

	@Override
	void spawnHitParticles(int i) {
		for(int particles = 0; particles < i; particles++)
			world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, posX, posY, posZ, gaussian(motionX), rand.nextFloat() + motionY, gaussian(motionZ));
	}

	@Override
	int getRicochetMax() {
		return 1;
	}

	@Override
	int getDamageOfShot(EntityLivingBase entity) {
		if(entity instanceof EntityCreeper)
			entity.onStruckByLightning(new EntityLightningBolt(world, entity.posX, entity.posY, entity.posZ, false));
		if(world.isRainingAt(new BlockPos((int) (entity.posX + 0.5F), (int) (entity.posY + 0.5F), (int) (entity.posZ + 0.5F))) && world.getWorldInfo().isRaining() && world.getWorldInfo().isThundering())
			world.addWeatherEffect(new EntityLightningBolt(world, (int) (entity.posX + 0.5F), (int) (entity.posY + 0.5F), (int) (entity.posZ + 0.5F), false));
		float f = 1F + (world.isRaining() ? 0.5F : 0F) + (world.isThundering() ? 0.5F : 0F);
		return Math.round(9F * f) + d6();
	}
}
