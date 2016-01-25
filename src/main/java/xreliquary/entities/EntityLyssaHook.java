package xreliquary.entities;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import xreliquary.Reliquary;
import xreliquary.reference.Names;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Xeno on 10/17/2014.
 */
public class EntityLyssaHook extends EntityFishHook {
    private static final List garbageList = Arrays.asList((new WeightedRandomFishable(new ItemStack(Items.leather_boots), 10)).setMaxDamagePercent(0.9F), new WeightedRandomFishable(new ItemStack(Items.leather), 10), new WeightedRandomFishable(new ItemStack(Items.bone), 10), new WeightedRandomFishable(new ItemStack(Items.potionitem), 10), new WeightedRandomFishable(new ItemStack(Items.string), 5), (new WeightedRandomFishable(new ItemStack(Items.fishing_rod), 2)).setMaxDamagePercent(0.9F), new WeightedRandomFishable(new ItemStack(Items.bowl), 10), new WeightedRandomFishable(new ItemStack(Items.stick), 5), new WeightedRandomFishable(new ItemStack(Items.dye, 10, 0), 1), new WeightedRandomFishable(new ItemStack(Blocks.tripwire_hook), 10), new WeightedRandomFishable(new ItemStack(Items.rotten_flesh), 10));
    private static final List plantList = Arrays.asList(new WeightedRandomFishable(new ItemStack(Blocks.waterlily), 1), new WeightedRandomFishable(new ItemStack(Items.name_tag), 1), new WeightedRandomFishable(new ItemStack(Items.saddle), 1), (new WeightedRandomFishable(new ItemStack(Items.bow), 1)).setMaxDamagePercent(0.25F).setEnchantable(), (new WeightedRandomFishable(new ItemStack(Items.fishing_rod), 1)).setMaxDamagePercent(0.25F).setEnchantable(), (new WeightedRandomFishable(new ItemStack(Items.book), 1)).setEnchantable());
    private static final List fishList = Arrays.asList(new WeightedRandomFishable(new ItemStack(Items.fish, 1, ItemFishFood.FishType.COD.getMetadata()), 60), new WeightedRandomFishable(new ItemStack(Items.fish, 1, ItemFishFood.FishType.SALMON.getMetadata()), 25), new WeightedRandomFishable(new ItemStack(Items.fish, 1, ItemFishFood.FishType.CLOWNFISH.getMetadata()), 2), new WeightedRandomFishable(new ItemStack(Items.fish, 1, ItemFishFood.FishType.PUFFERFISH.getMetadata()), 13));
    private int field_146037_g;
    private int field_146048_h;
    private int field_146050_i;
    private Block inBlock;
    private boolean field_146051_au;
    public int field_146044_a;
    private double field_146056_aC;
    private double field_146057_aD;
    private double field_146058_aE;
    private double field_146059_aF;
    private double field_146060_aG;
    @SideOnly(Side.CLIENT)
    private double field_146061_aH;
    @SideOnly(Side.CLIENT)
    private double field_146052_aI;
    @SideOnly(Side.CLIENT)
    private double field_146053_aJ;
    private int field_146049_av;
    private int field_146047_aw;
    private int ticksCatchable;
    private int field_146040_ay;
    private int field_146038_az;
    private float field_146054_aA;
    public Entity inEntity;
    private int field_146055_aB;

    //private List<ItemStack> pulledItems = new ArrayList<ItemStack>();

    public EntityLyssaHook(World world)
    {
        super(world);
    }

    @SideOnly(Side.CLIENT)
    public EntityLyssaHook(World world, double x, double y, double z, EntityPlayer player)
    {
        super(world, x, y, z, player);
    }

    /**
     * Sets the position and rotation. Only difference from the other one is no bounding on the rotation. Args: posX,
     * posY, posZ, yaw, pitch
     */
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(double p_70056_1_, double p_70056_3_, double p_70056_5_, float p_70056_7_, float p_70056_8_, int p_70056_9_)
    {
        this.field_146056_aC = p_70056_1_;
        this.field_146057_aD = p_70056_3_;
        this.field_146058_aE = p_70056_5_;
        this.field_146059_aF = (double)p_70056_7_;
        this.field_146060_aG = (double)p_70056_8_;
        this.field_146055_aB = p_70056_9_;
        this.motionX = this.field_146061_aH;
        this.motionY = this.field_146052_aI;
        this.motionZ = this.field_146053_aJ;
    }

    @SideOnly(Side.CLIENT)
    public void setVelocity(double p_70016_1_, double p_70016_3_, double p_70016_5_)
    {
        this.field_146061_aH = this.motionX = p_70016_1_;
        this.field_146052_aI = this.motionY = p_70016_3_;
        this.field_146053_aJ = this.motionZ = p_70016_5_;
    }

    public EntityLyssaHook(World world, EntityPlayer player)
    {
        super(world);
        this.field_146037_g = -1;
        this.field_146048_h = -1;
        this.field_146050_i = -1;
        this.ignoreFrustumCheck = true;
        this.angler = player;
        this.angler.fishEntity = this;
        this.setSize(0.25F, 0.25F);
        this.setLocationAndAngles(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ, player.rotationYaw, player.rotationPitch);
        this.posX -= (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        this.posY -= 0.10000000149011612D;
        this.posZ -= (double)(MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        this.setPosition(this.posX, this.posY, this.posZ);
        float f = 0.4F;
        this.motionX = (double)(-MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI) * f);
        this.motionZ = (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI) * f);
        this.motionY = (double)(-MathHelper.sin(this.rotationPitch / 180.0F * (float)Math.PI) * f);
        this.func_146035_c(this.motionX, this.motionY, this.motionZ, 3F, 1.0F);
    }

    public void func_146035_c(double setX, double setY, double setZ, float velCoefficient, float velCoefficient2)
    {
        float f2 = MathHelper.sqrt_double(setX * setX + setY * setY + setZ * setZ);
        setX /= (double)f2;
        setY /= (double)f2;
        setZ /= (double)f2;
        setX += this.rand.nextGaussian() * 0.007499999832361937D * (double)velCoefficient2;
        setY += this.rand.nextGaussian() * 0.007499999832361937D * (double)velCoefficient2;
        setZ += this.rand.nextGaussian() * 0.007499999832361937D * (double)velCoefficient2;
        setX *= (double)velCoefficient;
        setY *= (double)velCoefficient;
        setZ *= (double)velCoefficient;
        this.motionX = setX;
        this.motionY = setY;
        this.motionZ = setZ;
        float f3 = MathHelper.sqrt_double(setX * setX + setZ * setZ);
        this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(setX, setZ) * 180.0D / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(setY, (double)f3) * 180.0D / Math.PI);
        this.field_146049_av = 0;
    }

    /**
     * Checks if the entity is in range to render by using the past in distance and comparing it to its average edge
     * length * 64 * renderDistanceWeight Args: distance
     */
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double p_70112_1_)
    {
        double d1 = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0D;
        d1 *= 128.0D;
        return p_70112_1_ < d1 * d1;
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate() {

        //pulling items toward it routine
        List pullingItemsList = this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expand(3.0D, 3.0D, 3.0D));

        Iterator itemIterator = pullingItemsList.iterator();
        while (itemIterator.hasNext()) {
            Entity e = (Entity) itemIterator.next();
            if (!(e instanceof EntityItem))
                continue;
            //if (e.getDistance(this.posX, this.posY, this.posZ) < 0.5D) {
            //EntityItem itemEntity = (EntityItem) e;
            //pulledItems.add(itemEntity.getEntityItem());
            //itemEntity.setDead();
            //} else {
                Vec3 pullVector = new Vec3(this.posX - e.posX, this.posY - e.posY, this.posZ - e.posZ).normalize();
                e.motionX = pullVector.xCoord * 0.4D;
                e.motionY = pullVector.yCoord * 0.4D;
                e.motionZ = pullVector.zCoord * 0.4D;


        }

        if (this.field_146055_aB > 0)
        {
            double d7 = this.posX + (this.field_146056_aC - this.posX) / (double)this.field_146055_aB;
            double d8 = this.posY + (this.field_146057_aD - this.posY) / (double)this.field_146055_aB;
            double d9 = this.posZ + (this.field_146058_aE - this.posZ) / (double)this.field_146055_aB;
            double d1 = MathHelper.wrapAngleTo180_double(this.field_146059_aF - (double)this.rotationYaw);
            this.rotationYaw = (float)((double)this.rotationYaw + d1 / (double)this.field_146055_aB);
            this.rotationPitch = (float)((double)this.rotationPitch + (this.field_146060_aG - (double)this.rotationPitch) / (double)this.field_146055_aB);
            --this.field_146055_aB;
            this.setPosition(d7, d8, d9);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }
        else
        {
            if (!this.worldObj.isRemote)
            {
                ItemStack itemstack = this.angler.getCurrentEquippedItem();

                if (this.angler.isDead || !this.angler.isEntityAlive() || itemstack == null || itemstack.getItem() != Reliquary.CONTENT.getItem(Names.rod_of_lyssa) || this.getDistanceSqToEntity(this.angler) > 4096.0D)
                {
                    this.setDead();
                    this.angler.fishEntity = null;
                    return;
                }

                if (this.inEntity != null)
                {
                    if (!this.inEntity.isDead)
                    {
                        this.posX = this.inEntity.posX;
                        this.posY = this.inEntity.getEntityBoundingBox().minY + (double)this.inEntity.height * 0.8D;
                        this.posZ = this.inEntity.posZ;
                        return;
                    }

                    this.inEntity = null;
                }
            }

            if (this.field_146044_a > 0)
            {
                --this.field_146044_a;
            }

            Vec3 vec31 = new Vec3(this.posX, this.posY, this.posZ);
            Vec3 vec3 = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            MovingObjectPosition movingobjectposition = this.worldObj.rayTraceBlocks(vec31, vec3);
            vec31 = new Vec3(this.posX, this.posY, this.posZ);
            vec3 = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

            if (movingobjectposition != null)
            {
                vec3 = new Vec3(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
            }

            Entity entity = null;
            List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
            double d0 = 0.0D;
            double d2;

            for (int i = 0; i < list.size(); ++i)
            {
                Entity entity1 = (Entity)list.get(i);

                if (entity1.canBeCollidedWith() && (entity1 != this.angler || this.field_146047_aw >= 5))
                {
                    float f = 0.3F;
                    AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand((double) f, (double) f, (double) f);
                    MovingObjectPosition movingobjectposition1 = axisalignedbb.calculateIntercept(vec31, vec3);

                    if (movingobjectposition1 != null)
                    {
                        d2 = vec31.distanceTo(movingobjectposition1.hitVec);

                        if (d2 < d0 || d0 == 0.0D)
                        {
                            entity = entity1;
                            d0 = d2;
                        }
                    }
                }
            }

            if (entity != null)
            {
                movingobjectposition = new MovingObjectPosition(entity);
            }

            if (movingobjectposition != null)
            {
                if (movingobjectposition.entityHit != null)
                {

                    double lowerX = this.posX - 7D;
                    double lowerY = this.posY - 5D;
                    double lowerZ = this.posZ - 7D;
                    double upperX = this.posX + 7D;
                    double upperY = this.posY + 5D;
                    double upperZ = this.posZ + 7D;

                    List eList = worldObj.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(lowerX, lowerY, lowerZ, upperX, upperY, upperZ));
                    Iterator iterator = eList.iterator();
                    while (iterator.hasNext()) {
                        Entity e = (Entity)iterator.next();
                        if (e instanceof EntityLivingBase && !e.isEntityEqual(angler)) {
                            if (angler.isSneaking() || movingobjectposition.entityHit.attackEntityFrom(DamageSource.causeMobDamage((EntityLivingBase)e), 0.0F))
                            {
                                this.inEntity = movingobjectposition.entityHit;
                                break;
                            }
                        }
                    }


                }
                else
                {
                    this.field_146051_au = true;
                }
            }

            if (!this.field_146051_au)
            {
                this.moveEntity(this.motionX, this.motionY, this.motionZ);
                float f5 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
                this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

                while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
                {
                    this.prevRotationPitch += 360.0F;
                }

                while (this.rotationYaw - this.prevRotationYaw < -180.0F)
                {
                    this.prevRotationYaw -= 360.0F;
                }

                while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
                {
                    this.prevRotationYaw += 360.0F;
                }

                this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
                this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
                float f6 = 0.92F;

                if (this.onGround || this.isCollidedHorizontally)
                {
                    f6 = 0.5F;
                }

                byte b0 = 5;
                double d10 = 0.0D;

                for (int j = 0; j < b0; ++j)
                {
                    double d3 = this.getEntityBoundingBox().minY + (this.getEntityBoundingBox().maxY - this.getEntityBoundingBox().minY) * (double)(j + 0) / (double)b0 - 0.125D + 0.125D;
                    double d4 = this.getEntityBoundingBox().minY + (this.getEntityBoundingBox().maxY - this.getEntityBoundingBox().minY) * (double)(j + 1) / (double)b0 - 0.125D + 0.125D;
                    AxisAlignedBB axisalignedbb1 = new AxisAlignedBB(this.getEntityBoundingBox().minX, d3, this.getEntityBoundingBox().minZ, this.getEntityBoundingBox().maxX, d4, this.getEntityBoundingBox().maxZ);

                    if (this.worldObj.isAABBInMaterial(axisalignedbb1, Material.water))
                    {
                        d10 += 1.0D / (double)b0;
                    }
                }

                if (!this.worldObj.isRemote && d10 > 0.0D)
                {
                    WorldServer worldserver = (WorldServer)this.worldObj;
                    int k = 1;

                    if (this.rand.nextFloat() < 0.25F && this.worldObj.canLightningStrike(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY) + 1, MathHelper.floor_double(this.posZ))))
                    {
                        k = 2;
                    }

                    if (this.rand.nextFloat() < 0.5F && !this.worldObj.canBlockSeeSky(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY) + 1, MathHelper.floor_double(this.posZ))))
                    {
                        --k;
                    }

                    if (this.ticksCatchable > 0)
                    {
                        --this.ticksCatchable;

                        if (this.ticksCatchable <= 0)
                        {
                            this.field_146040_ay = 0;
                            this.field_146038_az = 0;
                        }
                    }
                    else
                    {
                        float f1;
                        float f2;
                        double d5;
                        double d6;
                        float f7;
                        double d11;

                        if (this.field_146038_az > 0)
                        {
                            this.field_146038_az -= k;

                            if (this.field_146038_az <= 0)
                            {
                                this.motionY -= 0.20000000298023224D;
                                this.playSound("random.splash", 0.25F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                                f1 = (float)MathHelper.floor_double(this.getEntityBoundingBox().minY);
                                worldserver.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX, (double) (f1 + 1.0F), this.posZ, (int) (1.0F + this.width * 20.0F), (double) this.width, 0.0D, (double) this.width, 0.20000000298023224D);
                                worldserver.spawnParticle(EnumParticleTypes.WATER_WAKE, this.posX, (double) (f1 + 1.0F), this.posZ, (int) (1.0F + this.width * 20.0F), (double) this.width, 0.0D, (double) this.width, 0.20000000298023224D);
                                this.ticksCatchable = MathHelper.getRandomIntegerInRange(this.rand, 10, 30);
                            }
                            else
                            {
                                this.field_146054_aA = (float)((double)this.field_146054_aA + this.rand.nextGaussian() * 4.0D);
                                f1 = this.field_146054_aA * 0.017453292F;
                                f7 = MathHelper.sin(f1);
                                f2 = MathHelper.cos(f1);
                                d11 = this.posX + (double)(f7 * (float)this.field_146038_az * 0.1F);
                                d5 = (double)((float)MathHelper.floor_double(this.getEntityBoundingBox().minY) + 1.0F);
                                d6 = this.posZ + (double)(f2 * (float)this.field_146038_az * 0.1F);

                                if (this.rand.nextFloat() < 0.15F)
                                {
                                    worldserver.spawnParticle(EnumParticleTypes.WATER_BUBBLE, d11, d5 - 0.10000000149011612D, d6, 1, (double) f7, 0.1D, (double) f2, 0.0D);
                                }

                                float f3 = f7 * 0.04F;
                                float f4 = f2 * 0.04F;
                                worldserver.spawnParticle(EnumParticleTypes.WATER_WAKE, d11, d5, d6, 0, (double) f4, 0.01D, (double) (-f3), 1.0D);
                                worldserver.spawnParticle(EnumParticleTypes.WATER_WAKE, d11, d5, d6, 0, (double) (-f4), 0.01D, (double) f3, 1.0D);
                            }
                        }
                        else if (this.field_146040_ay > 0)
                        {
                            this.field_146040_ay -= k;
                            f1 = 0.15F;

                            if (this.field_146040_ay < 20)
                            {
                                f1 = (float)((double)f1 + (double)(20 - this.field_146040_ay) * 0.05D);
                            }
                            else if (this.field_146040_ay < 40)
                            {
                                f1 = (float)((double)f1 + (double)(40 - this.field_146040_ay) * 0.02D);
                            }
                            else if (this.field_146040_ay < 60)
                            {
                                f1 = (float)((double)f1 + (double)(60 - this.field_146040_ay) * 0.01D);
                            }

                            if (this.rand.nextFloat() < f1)
                            {
                                f7 = MathHelper.randomFloatClamp(this.rand, 0.0F, 360.0F) * 0.017453292F;
                                f2 = MathHelper.randomFloatClamp(this.rand, 25.0F, 60.0F);
                                d11 = this.posX + (double)(MathHelper.sin(f7) * f2 * 0.1F);
                                d5 = (double)((float)MathHelper.floor_double(this.getEntityBoundingBox().minY) + 1.0F);
                                d6 = this.posZ + (double)(MathHelper.cos(f7) * f2 * 0.1F);
                                worldserver.spawnParticle(EnumParticleTypes.WATER_SPLASH, d11, d5, d6, 2 + this.rand.nextInt(2), 0.10000000149011612D, 0.0D, 0.10000000149011612D, 0.0D);
                            }

                            if (this.field_146040_ay <= 0)
                            {
                                this.field_146054_aA = MathHelper.randomFloatClamp(this.rand, 0.0F, 360.0F);
                                this.field_146038_az = MathHelper.getRandomIntegerInRange(this.rand, 20, 80);
                            }
                        }
                        else
                        {
                            this.field_146040_ay = MathHelper.getRandomIntegerInRange(this.rand, 100, 900);
                            this.field_146040_ay -= EnchantmentHelper.getLureModifier(this.angler) * 20 * 5;
                        }
                    }

                    if (this.ticksCatchable > 0)
                    {
                        this.motionY -= (double)(this.rand.nextFloat() * this.rand.nextFloat() * this.rand.nextFloat()) * 0.2D;
                    }
                }

                d2 = d10 * 2.0D - 1.0D;
                this.motionY += 0.03999999910593033D * d2;

                if (d10 > 0.0D)
                {
                    f6 = (float)((double)f6 * 0.9D);
                    this.motionY *= 0.8D;
                }

                this.motionX *= (double)f6;
                this.motionY *= (double)f6;
                this.motionZ *= (double)f6;
                this.setPosition(this.posX, this.posY, this.posZ);
            }
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound p_70014_1_)
    {
        p_70014_1_.setShort("xTile", (short)this.field_146037_g);
        p_70014_1_.setShort("yTile", (short)this.field_146048_h);
        p_70014_1_.setShort("zTile", (short)this.field_146050_i);
        p_70014_1_.setByte("inTile", (byte)Block.getIdFromBlock(this.inBlock));
        p_70014_1_.setByte("shake", (byte)this.field_146044_a);
        p_70014_1_.setByte("inGround", (byte)(this.field_146051_au ? 1 : 0));
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound p_70037_1_)
    {
        this.field_146037_g = p_70037_1_.getShort("xTile");
        this.field_146048_h = p_70037_1_.getShort("yTile");
        this.field_146050_i = p_70037_1_.getShort("zTile");
        this.inBlock = Block.getBlockById(p_70037_1_.getByte("inTile") & 255);
        this.field_146044_a = p_70037_1_.getByte("shake") & 255;
        this.field_146051_au = p_70037_1_.getByte("inGround") == 1;
    }

    @SideOnly(Side.CLIENT)
    public float getShadowSize()
    {
        return 0.0F;
    }

    //function that actually pulls the fishing rod/entity/player against block
    @Override
    public int handleHookRetraction()
    {
        if (this.worldObj.isRemote)
        {
            return 0;
        }
        else
        {
            byte b0 = 0;

            if (this.inEntity != null)
            {
                double d0 = this.angler.posX - this.posX;
                double d2 = this.angler.posY - this.posY;
                double d4 = this.angler.posZ - this.posZ;
                double d6 = (double)MathHelper.sqrt_double(d0 * d0 + d2 * d2 + d4 * d4);
                double d8 = angler.isSneaking() ? 0.0D : 0.1D;
                this.inEntity.motionX += d0 * d8;
                this.inEntity.motionY += d2 * d8 + (double)MathHelper.sqrt_double(d6) * 0.12D;
                this.inEntity.motionZ += d4 * d8;

                if (angler.isSneaking() && this.inEntity instanceof EntityLivingBase) {
                    EntityLivingBase livingBase = (EntityLivingBase) this.inEntity;
                    if (!(livingBase instanceof EntityPlayer)) {
                        int slotBeingStolenFrom = worldObj.rand.nextInt(5);
                        ItemStack stolenStack = livingBase.getEquipmentInSlot(slotBeingStolenFrom);
                        if (stolenStack == null && !Reliquary.CONFIG.getBool(Names.rod_of_lyssa, "steal_from_vacant_slots")) {
                            for (int i = 0; i < 5; ++i) {
                                stolenStack = livingBase.getEquipmentInSlot(i);
                                if (stolenStack != null)
                                    break;
                            }
                        }

                        float failProbabilityFactor;

                        if (Reliquary.CONFIG.getBool(Names.rod_of_lyssa, "use_leveled_failure_rate"))
                            failProbabilityFactor = 1F / ((float)Math.sqrt((double)Math.max(1, Math.min(angler.experienceLevel, Reliquary.CONFIG.getInt(Names.rod_of_lyssa, "level_cap_for_leveled_formula")))) * 2);
                        else
                            failProbabilityFactor = (float)Reliquary.CONFIG.getInt(Names.rod_of_lyssa, "flat_steal_failure_percent_rate") / 100F;

                        if (rand.nextFloat() <= failProbabilityFactor || (stolenStack == null && Reliquary.CONFIG.getBool(Names.rod_of_lyssa, "fail_steal_from_vacant_slots"))) {
                            if (Reliquary.CONFIG.getBool(Names.rod_of_lyssa, "anger_on_steal_failure"))
                                livingBase.attackEntityFrom(DamageSource.causePlayerDamage(this.angler),0.0F);
                        }
                        if (stolenStack != null) {
                            int randomItemDamage = worldObj.rand.nextInt(3);
                            stolenStack.damageItem(randomItemDamage, livingBase);
                            EntityItem entityitem = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, stolenStack);
                            double d1 = this.angler.posX - this.posX;
                            double d3 = this.angler.posY - this.posY;
                            double d5 = this.angler.posZ - this.posZ;
                            double d7 = (double) MathHelper.sqrt_double(d1 * d1 + d3 * d3 + d5 * d5);
                            double d9 = 0.1D;
                            entityitem.motionX = d1 * d9;
                            entityitem.motionY = d3 * d9 + (double) MathHelper.sqrt_double(d7) * 0.08D;
                            entityitem.motionZ = d5 * d9;
                            this.worldObj.spawnEntityInWorld(entityitem);

                            livingBase.setCurrentItemOrArmor(slotBeingStolenFrom, null);
                        }
                    }
                }
                b0 = 3;
            }
            else if (this.ticksCatchable > 0)
            {
                EntityItem entityitem = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, this.func_146033_f());
                double d1 = this.angler.posX - this.posX;
                double d3 = this.angler.posY - this.posY;
                double d5 = this.angler.posZ - this.posZ;
                double d7 = (double)MathHelper.sqrt_double(d1 * d1 + d3 * d3 + d5 * d5);
                double d9 = 0.1D;
                entityitem.motionX = d1 * d9;
                entityitem.motionY = d3 * d9 + (double)MathHelper.sqrt_double(d7) * 0.08D;
                entityitem.motionZ = d5 * d9;
                this.worldObj.spawnEntityInWorld(entityitem);
                this.angler.worldObj.spawnEntityInWorld(new EntityXPOrb(this.angler.worldObj, this.angler.posX, this.angler.posY + 0.5D, this.angler.posZ + 0.5D, this.rand.nextInt(6) + 1));
                b0 = 1;
            }

            List pullingItemsList = this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));

            Iterator itemIterator = pullingItemsList.iterator();
            while (itemIterator.hasNext()) {
                Entity e = (Entity) itemIterator.next();
                if (!(e instanceof EntityItem))
                    continue;

                double d1 = this.angler.posX - this.posX;
                double d3 = this.angler.posY - this.posY;
                double d5 = this.angler.posZ - this.posZ;
                double d7 = (double)MathHelper.sqrt_double(d1 * d1 + d3 * d3 + d5 * d5);
                double d9 = 0.1D;
                ((EntityItem)e).motionX = d1 * d9;
                ((EntityItem)e).motionY = d3 * d9 + (double)MathHelper.sqrt_double(d7) * 0.08D;
                ((EntityItem)e).motionZ = d5 * d9;
            }

            this.setDead();
            this.angler.fishEntity = null;

            return b0;
        }
    }

    private ItemStack func_146033_f()
    {
        float f = this.worldObj.rand.nextFloat();
        int i = EnchantmentHelper.getLuckOfSeaModifier(this.angler);
        int j = EnchantmentHelper.getLureModifier(this.angler);
        float f1 = 0.1F - (float)i * 0.025F - (float)j * 0.01F;
        float f2 = 0.05F + (float)i * 0.01F - (float)j * 0.01F;
        f1 = MathHelper.clamp_float(f1, 0.0F, 1.0F);
        f2 = MathHelper.clamp_float(f2, 0.0F, 1.0F);

        if (f < f1)
        {
            this.angler.addStat(StatList.junkFishedStat, 1);
            return ((WeightedRandomFishable)WeightedRandom.getRandomItem(this.rand, garbageList)).getItemStack(this.rand);
        }
        else
        {
            f -= f1;

            if (f < f2)
            {
                this.angler.addStat(StatList.treasureFishedStat, 1);
                return ((WeightedRandomFishable)WeightedRandom.getRandomItem(this.rand, plantList)).getItemStack(this.rand);
            }
            else
            {
                float f3 = f - f2;
                this.angler.addStat(StatList.fishCaughtStat, 1);
                return ((WeightedRandomFishable)WeightedRandom.getRandomItem(this.rand, fishList)).getItemStack(this.rand);
            }
        }
    }

    /**
     * Will get destroyed next tick.
     */
    public void setDead()
    {
        super.setDead();

        if (this.angler != null)
        {
            this.angler.fishEntity = null;
        }
    }
}
