package xreliquary.entities.shot;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityStormShot extends EntityShotBase {
    public EntityStormShot(World par1World) {
        super(par1World);
    }

    public EntityStormShot(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
    }

    public EntityStormShot(World par1World, EntityPlayer par2EntityPlayer) {
        super(par1World, par2EntityPlayer);
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
    void onImpact(MovingObjectPosition mop) {
        if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && mop.entityHit != null) {
            if (mop.entityHit == shootingEntity)
                return;
            if (!(mop.entityHit instanceof EntityLivingBase))
                return;
            this.onImpact((EntityLivingBase)mop.entityHit);
        } else if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            if (worldObj.canLightningStrike(mop.getBlockPos()) && worldObj.getWorldInfo().isRaining() && worldObj.getWorldInfo().isThundering())
                worldObj.addWeatherEffect(new EntityLightningBolt(worldObj, mop.getBlockPos().getX(), mop.getBlockPos().getY(), mop.getBlockPos().getZ()));
            this.groundImpact(mop.sideHit);
        }
    }

    @Override
    void doBurstEffect(EnumFacing sideHit) {
        // does nothing
    }

    @Override
    void onImpact(EntityLivingBase mop) {
        if (mop != shootingEntity || ticksInAir > 3)
            doDamage(mop);
        spawnHitParticles(18);
        this.setDead();
    }

    @Override
    void spawnHitParticles(int i) {
        for (int particles = 0; particles < i; particles++)
            worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, posX, posY, posZ, gaussian(motionX), rand.nextFloat() + motionY, gaussian(motionZ));
    }

    @Override
    int getRicochetMax() {
        return 1;
    }

    @Override
    int getDamageOfShot(EntityLivingBase mop) {
        if (mop instanceof EntityCreeper)
            (mop).onStruckByLightning(new EntityLightningBolt(worldObj, mop.posX, mop.posY, mop.posZ));
        if (worldObj.canLightningStrike(new BlockPos((int) (mop.posX + 0.5F), (int) (mop.posY + 0.5F), (int) (mop.posZ + 0.5F))) && worldObj.getWorldInfo().isRaining() && worldObj.getWorldInfo().isThundering())
            worldObj.addWeatherEffect(new EntityLightningBolt(worldObj, (int) (mop.posX + 0.5F), (int) (mop.posY + 0.5F), (int) (mop.posZ + 0.5F)));
        float f = 1F + (worldObj.isRaining() ? 0.5F : 0F) + (worldObj.isThundering() ? 0.5F : 0F);
        return Math.round(9F * f) + d6();
    }
}
