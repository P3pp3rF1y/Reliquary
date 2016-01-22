package xreliquary.entities.shot;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.lib.ClientReference;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

import java.util.Iterator;
import java.util.List;

public abstract class EntityShotBase extends Entity implements IProjectile {
    protected int xTile = -1;
    protected int yTile = -1;
    protected int zTile = -1;
    protected boolean inGround = false;

    /**
     * The owner of this arrow.
     */
    public EntityPlayer shootingEntity;
    protected int ticksInAir = 0;

    protected int ricochetCounter = 0;
    protected boolean scheduledForDeath = false;

    public EntityShotBase(World par1World) {
        super(par1World);
        this.setSize(0.01F, 0.01F);
    }

    public EntityShotBase(World par1World, double par2, double par4, double par6) {
        this(par1World);
        this.setPosition(par2, par4, par6);
    }

    public EntityShotBase(World par1World, EntityPlayer par2EntityPlayer) {
        this(par1World);
        shootingEntity = par2EntityPlayer;
        float par3 = 0.8F;
        this.setLocationAndAngles(par2EntityPlayer.posX, par2EntityPlayer.posY + par2EntityPlayer.getEyeHeight(), par2EntityPlayer.posZ, par2EntityPlayer.rotationYaw, par2EntityPlayer.rotationPitch);
        posX -= MathHelper.cos(rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
        posY -= 0.2D;
        posZ -= MathHelper.sin(rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
        this.setPosition(posX, posY, posZ);
        motionX = -MathHelper.sin(rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float) Math.PI);
        motionZ = MathHelper.cos(rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float) Math.PI);
        motionY = -MathHelper.sin(rotationPitch / 180.0F * (float) Math.PI);
        this.setThrowableHeading(motionX, motionY, motionZ, par3 * 1.5F, 1.0F);
    }

    @Override
    protected void entityInit() {
        dataWatcher.addObject(16, (byte) 0);
    }

    /**
     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z
     * direction.
     */
    @Override
    public void setThrowableHeading(double var1, double var3, double var5, float var7, float var8) {
        float var9 = MathHelper.sqrt_double(var1 * var1 + var3 * var3 + var5 * var5);
        var1 /= var9;
        var3 /= var9;
        var5 /= var9;
        var1 += rand.nextGaussian() * 0.007499999832361937D * var8;
        var3 += rand.nextGaussian() * 0.007499999832361937D * var8;
        var5 += rand.nextGaussian() * 0.007499999832361937D * var8;
        var1 *= var7;
        var3 *= var7;
        var5 *= var7;
        motionX = var1;
        motionY = var3;
        motionZ = var5;
        float var10 = MathHelper.sqrt_double(var1 * var1 + var5 * var5);
        prevRotationYaw = rotationYaw = (float) (Math.atan2(var1, var5) * 180.0D / Math.PI);
        prevRotationPitch = rotationPitch = (float) (Math.atan2(var3, var10) * 180.0D / Math.PI);
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * Sets the position and rotation. Only difference from the other one is no bounding on the rotation. Args: posX,
     * posY, posZ, yaw, pitch
     */
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean p_180426_10_) {
        this.setPosition(x, y, z);
        this.setRotation(yaw, pitch);
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * Sets the velocity to the args. Args: x, y, z
     */
    public void setVelocity(double par1, double par3, double par5) {
        motionX = par1;
        motionY = par3;
        motionZ = par5;

        if (prevRotationPitch == 0.0F && prevRotationYaw == 0.0F) {
            float var7 = MathHelper.sqrt_double(par1 * par1 + par5 * par5);
            prevRotationYaw = rotationYaw = (float) (Math.atan2(par1, par5) * 180.0D / Math.PI);
            prevRotationPitch = rotationPitch = (float) (Math.atan2(par3, var7) * 180.0D / Math.PI);
            prevRotationPitch = rotationPitch;
            prevRotationYaw = rotationYaw;
            this.setLocationAndAngles(posX, posY, posZ, rotationYaw, rotationPitch);
        }
    }

    // this makes sure no matter where the bullet originated, it "belongs" to a
    // player.
    // this prevents a lot of weird things from happening with bullet effects
    // that
    // require an originating player. Consider deprecating this.
    protected void ensurePlayerShooterEntity() {
        if (shootingEntity == null) {
            List players = worldObj.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(posX - 1, posY - 1, posZ - 1, posX + 1, posY + 1, posZ + 1));
            Iterator i = players.iterator();
            double closestDistance = Double.MAX_VALUE;
            EntityPlayer closestPlayer = null;
            while (i.hasNext()) {
                EntityPlayer e = (EntityPlayer) i.next();
                double distance = e.getDistanceToEntity(this);
                if (distance < closestDistance)
                    closestPlayer = e;
            }
            if (closestPlayer != null)
                shootingEntity = closestPlayer;
        }
    }

    /**
     * Called to update the entity's position/logic. Special snippets of the
     * usual projectile code have been removed so they can be handled manually
     * in the onImpact methods of the base shot.
     */
    @Override
    public void onUpdate() {
        super.onUpdate();
        // bullets fly reasonably fast. if it stays "alive" for more than 30
        // seconds
        // this forces it to de-spawn.. just to be on the safe side.
        if (ticksInAir > 600)
            this.setDead();
        ensurePlayerShooterEntity();

        if (prevRotationPitch == 0.0F && prevRotationYaw == 0.0F) {
            float pythingy = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
            prevRotationYaw = rotationYaw = (float) (Math.atan2(motionX, motionZ) * 180.0D / Math.PI);
            prevRotationPitch = rotationPitch = (float) (Math.atan2(motionY, pythingy) * 180.0D / Math.PI);
        }

        IBlockState blockState = this.worldObj.getBlockState(new BlockPos(this.xTile, this.yTile, this.zTile));
        Block block = blockState.getBlock();

        if (block.getMaterial() != Material.air) {
            block.setBlockBoundsBasedOnState(this.worldObj, new BlockPos(this.xTile, this.yTile, this.zTile));
            AxisAlignedBB axisalignedbb = block.getCollisionBoundingBox(this.worldObj, new BlockPos(this.xTile, this.yTile, this.zTile), blockState);

            if (axisalignedbb != null && axisalignedbb.isVecInside(new Vec3(this.posX, this.posY, this.posZ)))
                this.inGround = true;

        }

        if (!inGround) {
            ++ticksInAir;
            if (ticksInAir > 1 && ticksInAir < 3) {
                worldObj.spawnParticle(EnumParticleTypes.FLAME, posX + smallGauss(0.1D), posY + smallGauss(0.1D), posZ + smallGauss(0.1D), 0D, 0D, 0D);
                for (int particles = 0; particles < 3; particles++)
                    this.doFiringEffects();

            } else
                this.doFlightEffects();

            Vec3 posVector = new Vec3(posX, posY, posZ);
            Vec3 approachVector = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);
            MovingObjectPosition objectStruckByVector = worldObj.rayTraceBlocks(posVector, approachVector, false, true, false);
            posVector = new Vec3(posX, posY, posZ);
            approachVector = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);

            if (objectStruckByVector != null)
                approachVector = new Vec3(objectStruckByVector.hitVec.xCoord, objectStruckByVector.hitVec.yCoord, objectStruckByVector.hitVec.zCoord);

            Entity hitEntity = null;
            List struckEntitiesInAABB = worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().addCoord(motionX, motionY, motionZ).expand(1.0D, 1.0D, 1.0D));
            double var7 = 0.0D;
            Iterator struckEntityIterator = struckEntitiesInAABB.iterator();
            float var11;

            while (struckEntityIterator.hasNext()) {
                Entity struckEntity = (Entity) struckEntityIterator.next();
                if (struckEntity.canBeCollidedWith() && (struckEntity != shootingEntity || ticksInAir >= 5)) {
                    var11 = 0.5F;
                    AxisAlignedBB var12 = struckEntity.getEntityBoundingBox().expand(var11, var11, var11);
                    MovingObjectPosition var13 = var12.calculateIntercept(posVector, approachVector);

                    if (var13 != null) {
                        double var14 = posVector.distanceTo(var13.hitVec);

                        if (var14 < var7 || var7 == 0.0D) {
                            hitEntity = struckEntity;
                            var7 = var14;
                        }
                    }
                }
            }

            if (hitEntity != null)
                objectStruckByVector = new MovingObjectPosition(hitEntity);

            if (objectStruckByVector != null) {
                this.onImpact(objectStruckByVector);
                if (scheduledForDeath)
                    this.setDead();
            }

            this.moveEntity(motionX, motionY, motionZ);
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
        par1NBTTagCompound.setShort("xTile", (short) xTile);
        par1NBTTagCompound.setShort("yTile", (short) yTile);
        par1NBTTagCompound.setShort("zTile", (short) zTile);
        par1NBTTagCompound.setByte("inGround", (byte) (inGround ? 1 : 0));
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
        xTile = par1NBTTagCompound.getShort("xTile");
        yTile = par1NBTTagCompound.getShort("yTile");
        zTile = par1NBTTagCompound.getShort("zTile");
        inGround = par1NBTTagCompound.getByte("inGround") == 1;
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they
     * walk on. used for spiders and wolves to prevent them from trampling crops
     */
    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean isInRangeToRenderDist(double distance) {
        double d0 = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0D;
        if(Double.isNaN(d0)) {
            d0 = 4.0D;
        }

        d0 *= 64.0D;
        return distance < d0 * d0;
    }

    /**
     * If returns false, the item will not inflict any damage against entities.
     */
    @Override
    public boolean canAttackWithItem() {
        return false;
    }

    // these are just simulated dice rolls, they make it slightly easier to
    // adjust damage
    protected int d3() {
        return rand.nextInt(3) + 1;
    }

    protected int d6() {
        return rand.nextInt(6) + 1;
    }

    protected int d12() {
        return rand.nextInt(12) + 1;
    }

    protected void doDamage(EntityLivingBase e) {
        // minor modification here, the shots are quite strong
        // so I've made it so they only do half damage against player entities.
        e.attackEntityFrom(this.getDamageSource(), (e instanceof EntityPlayer ? 0.5F : 1F) * this.getDamageOfShot(e));
    }

    protected DamageSource getDamageSource() {
        return DamageSource.causePlayerDamage(shootingEntity);
    }

    protected void groundImpact(EnumFacing sideHit) {
        this.ricochet(sideHit);
    }

    /**
     * This small collection of gaussian options is used to simplify spawning
     * particles with easily repeated, low velocity vectors.
     */

    /**
     * @param d is the factor of the double
     * @return a negative or positive value with limits of 50% of d
     */
    protected double smallGauss(double d) {
        return (worldObj.rand.nextFloat() - 0.5D) * d;
    }

    /**
     * @param d is the factor of the double
     * @return a positive value between 0% and 50% of d
     */
    protected double posGauss(double d) {
        return rand.nextFloat() * 0.5D * d;
    }

    /**
     * @param d haphazardly determines the upper bounds of the [always
     *          positive] gaussian
     * @return a [comparatively] normal gaussian ranging from 75% to 125% of the
     * parameter d
     */
    protected double gaussian(double d) {
        return d + d * ((rand.nextFloat() - 0.5D) / 4);
    }

    /**
     * @param d haphazardly determines the upper bounds of the [always
     *          positive] gaussian
     * @return a [comparatively] low gaussian ranging from 25% to 75% of the
     * parameter d
     */
    protected double lowGauss(double d) {
        return d - d * (rand.nextFloat() / 4 + 0.5);
    }

    /**
     * Handles the ricochet "event", more or less determines when the entity
     * does or doesn't ricochet. If the ricochet limit is set to 0, it will
     * still "ricochet" once, but will immediately self destruct by calling its
     * burstEffect and setting itself to dead.
     *
     * @param sideHit
     */
    protected void ricochet(EnumFacing sideHit) {
        switch (sideHit) {

            case DOWN:
            case UP:
                // topHit, bottomHit, reflect Y
                motionY = motionY * -1;
                break;
            case WEST:
            case EAST:
                // westHit, eastHit, reflect Z
                motionZ = motionZ * -1;
                break;
            case SOUTH:
            case NORTH:
                // southHit, northHit, reflect X
                motionX = motionX * -1;
                break;
        }
        ricochetCounter++;
        if (ricochetCounter > this.getRicochetMax()) {
            this.doBurstEffect(sideHit);
            scheduledForDeath = true;
            for (int particles = 0; particles < 4; particles++) {
                //TODO: verify particles spawning in correct places
                switch (sideHit) {
                    case DOWN:
                        worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, posX, posY, posZ, gaussian(0.1D), -gaussian(0.1D), gaussian(0.1D));
                        break;
                    case UP:
                        worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, posX, posY, posZ, gaussian(0.1D), gaussian(0.1D), gaussian(0.1D));
                        break;
                    case NORTH:
                        worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, posX, posY, posZ, gaussian(0.1D), gaussian(0.1D), -gaussian(0.1D));
                        break;
                    case SOUTH:
                        worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, posX, posY, posZ, gaussian(0.1D), gaussian(0.1D), gaussian(0.1D));
                        break;
                    case WEST:
                        worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, posX, posY, posZ, -gaussian(0.1D), gaussian(0.1D), gaussian(0.1D));
                        break;
                    case EAST:
                        worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, posX, posY, posZ, gaussian(0.1D), gaussian(0.1D), gaussian(0.1D));
                        break;
                }
            }
        }
    }

    /*
     * custom seek method, currently only being used in seeker and ender shots.
     * it could be better, but it works. As of writing this, both shots share
     * the same formulas.
     */
    protected void seekTarget() {
        Entity closestTarget = null;
        List<String> entitiesThatCanBeHunted = (List<String>) Reliquary.CONFIG.get(Names.seeker_shot, "entities_that_can_be_hunted");
        List targetsList = worldObj.getEntitiesWithinAABBExcludingEntity(this, new AxisAlignedBB(posX - 5, posY - 5, posZ - 5, posX + 5, posY + 5, posZ + 5));
        Iterator iTarget = targetsList.iterator();
        double closestDistance = Double.MAX_VALUE;
        while (iTarget.hasNext()) {
            Entity currentTarget = (Entity) iTarget.next();

            Class entityClass = currentTarget.getClass();
            String entityName = (String) EntityList.classToStringMapping.get(entityClass);
            if (!entitiesThatCanBeHunted.contains(entityName) || (currentTarget == shootingEntity) || (currentTarget.isDead))
                continue;
            // goes for the closest thing it can
            if (this.getDistanceToEntity(currentTarget) < closestDistance) {
                closestDistance = this.getDistanceToEntity(currentTarget);
                closestTarget = currentTarget;
            }
        }
        //TODO: verify that using entity height instead of ySize in 1.7 doesn't mess this up
        // these are extremely touchy, tune them lightly.
        if (closestTarget != null && shootingEntity != null) {
            double x = closestTarget.getEntityBoundingBox().minX + closestTarget.getEntityBoundingBox().maxX;
            x /= 2D;
            double y = closestTarget.getEntityBoundingBox().minY + Math.max(closestTarget.getYOffset(), closestTarget.height);
            y -= closestTarget.height / 2D;
            double z = closestTarget.getEntityBoundingBox().minZ + closestTarget.getEntityBoundingBox().maxZ;
            z /= 2D;
            double trueX = this.getEntityBoundingBox().minX + this.getEntityBoundingBox().maxX;
            trueX /= 2D;
            double trueY = this.getEntityBoundingBox().minY + this.getYOffset();
            trueY -= this.height  / 2D;
            double trueZ = this.getEntityBoundingBox().minZ + this.getEntityBoundingBox().maxZ;
            trueZ /= 2D;
            Vec3 seekVector = new Vec3(x - trueX, y - trueY, z - trueZ);
            seekVector = seekVector.normalize();

            this.motionX = seekVector.xCoord * 0.4D;
            this.motionY = seekVector.yCoord * 0.4D;
            this.motionZ = seekVector.zCoord * 0.4D;
            if(worldObj.isRemote){
                this.setVelocity(motionX, motionY, motionZ);
            }
        }
    }

    /**
     * Determines the maximum number of bounces a bullet gets before burstEffect
     * + dying. 0 means it is destroyed the moment it makes impact. This only
     * matters if you actually call "groundImpact" from your onImpact(MOP)
     * method.
     */
    abstract int getRicochetMax();

    /**
     * @param e an optional parameter, some shots need to know what entity
     *            they're hitting for damage counts
     * @return the int of damage the shot should deal. Most of these use my "dX"
     * methods of this class to randomize damage, to a degree.
     */
    abstract int getDamageOfShot(EntityLivingBase e);

    /**
     * Additional entity impact effects should go here
     *
     * @param mop the entity being struck
     */
    abstract void onImpact(EntityLivingBase mop);

    /**
     * Additional effects of TILE/MISC [non-entity] impacts should go here
     *
     * @param mop the MOP data of the tile/misc object being struck
     */
    protected abstract void onImpact(MovingObjectPosition mop);

    /**
     * This is the effect called when the shot reaches the ground and has no
     * ricochets remaining. It can also be called at any time by hooking into
     * flight/firing effects.
     *
     * @param sideHit is sometimes used when you need particles to fly in a certain
     *                direction.
     */
    abstract void doBurstEffect(EnumFacing sideHit);

    /**
     * The particle/effect window when the gun is initially fired, between
     * "ticksAlive" 1 and 3.
     */
    abstract void doFiringEffects();

    /**
     * The particle/effect window any time after the initial firing window of
     * doFiringEffects. (ticksAlive >= 3)
     */
    abstract void doFlightEffects();

    /**
     * The particles which spawn when the bullet impacts an entity.
     *
     * @param i      the number of times you want the inner for loop to produce a
     *               single particle at random velocity
     */
    abstract void spawnHitParticles(int i);

    protected int getShotType() {
        if (this instanceof EntityNeutralShot)
            return Reference.NEUTRAL_SHOT_INDEX;
        else if (this instanceof EntityExorcismShot)
            return Reference.EXORCISM_SHOT_INDEX;
        else if (this instanceof EntityBlazeShot)
            return Reference.BLAZE_SHOT_INDEX;
        else if (this instanceof EntityEnderShot)
            return Reference.ENDER_SHOT_INDEX;
        else if (this instanceof EntityConcussiveShot)
            return Reference.CONCUSSIVE_SHOT_INDEX;
        else if (this instanceof EntityBusterShot)
            return Reference.BUSTER_SHOT_INDEX;
        else if (this instanceof EntitySeekerShot)
            return Reference.SEEKER_SHOT_INDEX;
        else if (this instanceof EntitySandShot)
            return Reference.SAND_SHOT_INDEX;
        else if (this instanceof EntityStormShot)
            return Reference.STORM_SHOT_INDEX;
        return 0;
    }

    // used by the renderer to pull the shot texture directly from the entity.
    // This might not work.
    public ResourceLocation getShotTexture() {
        switch (this.getShotType()) {
            case 0:
            case Reference.NEUTRAL_SHOT_INDEX:
                return ClientReference.NEUTRAL;
            case  Reference.EXORCISM_SHOT_INDEX:
                return ClientReference.EXORCISM;
            case Reference.BLAZE_SHOT_INDEX:
                return ClientReference.BLAZE;
            case Reference.ENDER_SHOT_INDEX:
                return ClientReference.ENDER;
            case Reference.CONCUSSIVE_SHOT_INDEX:
                return ClientReference.CONCUSSIVE;
            case Reference.BUSTER_SHOT_INDEX:
                return ClientReference.BUSTER;
            case Reference.SEEKER_SHOT_INDEX:
                return ClientReference.SEEKER;
            case Reference.SAND_SHOT_INDEX:
                return ClientReference.SAND;
            case Reference.STORM_SHOT_INDEX:
                return ClientReference.STORM;
        }
        return ClientReference.NEUTRAL;
    }

    /**
     * simple overloaded method for the standard doBurstEffect, basically calls
     * the abstracted version wherever it is needed. It just passes sideHit as a
     * EnumFacing.UP, since it doesn't matter.
     */
    protected void doBurstEffect() {
        this.doBurstEffect(EnumFacing.UP);
    }

}
