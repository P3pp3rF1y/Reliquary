package xreliquary.entities.shot;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class EntityBlazeShot extends EntityShotBase {
    public EntityBlazeShot(World par1World) {
        super(par1World);
    }

    public EntityBlazeShot(World par1World, double par2, double par4, double par6) {
        super(par1World);
    }

    public EntityBlazeShot(World par1World, EntityPlayer par2EntityPlayer) {
        super(par1World, par2EntityPlayer);
    }

    @Override
    void doFlightEffects() {
        if (ticksInAir % 3 == 0 && ticksInAir < 9) {
            worldObj.spawnParticle( EnumParticleTypes.FLAME, posX, posY, posZ, lowGauss(motionX), lowGauss(motionY), lowGauss(motionZ));
        }
    }

    @Override
    void doFiringEffects() {
        worldObj.spawnParticle(EnumParticleTypes.SPELL_MOB_AMBIENT, posX + smallGauss(0.1D), posY + smallGauss(0.1D), posZ + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
        worldObj.spawnParticle(EnumParticleTypes.FLAME, posX, posY, posZ, gaussian(motionX), gaussian(motionY), gaussian( motionZ ));
    }

    @Override
    protected DamageSource getDamageSource() {
        return super.getDamageSource().setFireDamage();
    }

    @Override
    protected void onImpact(MovingObjectPosition mop) {
        if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && mop.entityHit != null) {
            if (mop.entityHit == shootingEntity)
                return;
            if (!(mop.entityHit instanceof EntityLivingBase))
                return;
            this.onImpact((EntityLivingBase)mop.entityHit);
        } else if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            if (shootingEntity == null) return;

            this.groundImpact(mop.sideHit);

            BlockPos fireSpawnPos = mop.getBlockPos().offset( mop.sideHit );

            if (shootingEntity.canPlayerEdit(fireSpawnPos, mop.sideHit, new ItemStack(Items.flint_and_steel, 1, 0)))
            {
                if (this.worldObj.isAirBlock(fireSpawnPos))
                {
                    worldObj.setBlockState( fireSpawnPos, Blocks.fire.getDefaultState() );
                }
            }
        }
    }

    @Override
    void onImpact(EntityLivingBase mop) {
        if (mop != shootingEntity || ticksInAir > 3) {
            mop.setFire(40);
            doDamage(mop);
        }
        spawnHitParticles(8);
        this.setDead();
    }

    @Override
    void spawnHitParticles(int i) {
        for (int particles = 0; particles < i; particles++) {
            worldObj.spawnParticle(EnumParticleTypes.FLAME, posX, posY, posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));
        }
    }

    @Override
    protected void groundImpact(EnumFacing sideHit) {
        this.setDead();
    }

    @Override
    int getRicochetMax() {
        return 0;
    }

    @Override
    int getDamageOfShot(EntityLivingBase mop) {
        // they're not COMPLETELY useless against fireImmune mobs, just mostly
        // useless.
        //this probably isn't gonna work now the bullets do purely fire damage.
        return mop.isImmuneToFire() ? 2 : (10 + d12());
    }

    @Override
    void doBurstEffect(EnumFacing sideHit) {
        for (int particles = 0; particles < 40; particles++) {
            switch (sideHit) {
                case DOWN:
                    worldObj.spawnParticle(EnumParticleTypes.FLAME, posX, posY, posZ, smallGauss(0.1D) + motionX / 4, -posGauss(0.2D), smallGauss(0.2D) + motionZ / 4);
                    break;
                case UP:
                    worldObj.spawnParticle(EnumParticleTypes.FLAME, posX, posY, posZ, smallGauss(0.1D) + motionX / 4, posGauss(0.2D), smallGauss(0.2D) + motionZ / 4);
                    break;
                case NORTH:
                    worldObj.spawnParticle(EnumParticleTypes.FLAME, posX, posY, posZ, smallGauss(0.1D) + motionX / 4, smallGauss(0.1D) + motionY / 4, -posGauss(0.2D));
                    break;
                case SOUTH:
                    worldObj.spawnParticle(EnumParticleTypes.FLAME, posX, posY, posZ, smallGauss(0.1D) + motionX / 4, smallGauss(0.1D) + motionY / 4, posGauss(0.2D));
                    break;
                case WEST:
                    worldObj.spawnParticle(EnumParticleTypes.FLAME, posX, posY, posZ, -posGauss(0.2D), smallGauss(0.1D) + motionY / 4, smallGauss(0.1D) + motionZ / 4);
                    break;
                case EAST:
                    worldObj.spawnParticle(EnumParticleTypes.FLAME, posX, posY, posZ, posGauss(0.2D), smallGauss(0.1D) + motionY / 4, smallGauss(0.1D) + motionZ / 4);
                    break;
            }
        }


    }
}
