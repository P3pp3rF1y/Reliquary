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
	public EntityStormShot(World par1World) {
		super(par1World);
	}

	public EntityStormShot(World par1World, double par2, double par4, double par6) {
		super(par1World, par2, par4, par6);
	}

	public EntityStormShot(World par1World, EntityPlayer par2EntityPlayer, EnumHand hand) {
		super(par1World, par2EntityPlayer, hand);
	}

	@Override
	void doFiringEffects() {
		worldObj.spawnParticle(EnumParticleTypes.SPELL_MOB_AMBIENT, posX + smallGauss(0.1D), posY + smallGauss(0.1D), posZ + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
		worldObj.spawnParticle(EnumParticleTypes.FLAME, posX, posY, posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));
	}

	@Override
	void doFlightEffects() {
		// does nothing
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if(result.typeOfHit == RayTraceResult.Type.BLOCK) {
			if(worldObj.isRainingAt(result.getBlockPos()) && worldObj.getWorldInfo().isRaining() && worldObj.getWorldInfo().isThundering())
				worldObj.addWeatherEffect(new EntityLightningBolt(worldObj, result.getBlockPos().getX(), result.getBlockPos().getY(), result.getBlockPos().getZ(), false));
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
			worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, posX, posY, posZ, gaussian(motionX), rand.nextFloat() + motionY, gaussian(motionZ));
	}

	@Override
	int getRicochetMax() {
		return 1;
	}

	@Override
	int getDamageOfShot(EntityLivingBase entity) {
		if(entity instanceof EntityCreeper)
			entity.onStruckByLightning(new EntityLightningBolt(worldObj, entity.posX, entity.posY, entity.posZ, false));
		if(worldObj.isRainingAt(new BlockPos((int) (entity.posX + 0.5F), (int) (entity.posY + 0.5F), (int) (entity.posZ + 0.5F))) && worldObj.getWorldInfo().isRaining() && worldObj.getWorldInfo().isThundering())
			worldObj.addWeatherEffect(new EntityLightningBolt(worldObj, (int) (entity.posX + 0.5F), (int) (entity.posY + 0.5F), (int) (entity.posZ + 0.5F), false));
		float f = 1F + (worldObj.isRaining() ? 0.5F : 0F) + (worldObj.isThundering() ? 0.5F : 0F);
		return Math.round(9F * f) + d6();
	}
}
