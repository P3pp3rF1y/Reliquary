package xreliquary.items;

import lib.enderwizards.sandstone.init.ContentHandler;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemToggleable;
import lib.enderwizards.sandstone.util.InventoryHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Xeno on 10/11/2014.
 */
@ContentInit
public class ItemRendingGale extends ItemToggleable {
    public ItemRendingGale() {
        super(Names.rending_gale);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxDamage(321);
        this.setMaxStackSize(1);
        canRepair = false;
    }

    @Override
    public boolean isFull3D(){ return true; }

    public void attemptFlight(EntityLivingBase entityLiving, ItemStack ist, int tickUsed) {
        if (!(entityLiving instanceof EntityPlayer))
            return;

        EntityPlayer player = (EntityPlayer)entityLiving;

        Vec3 lookVec = player.getLook(0.66F);

        double x = lookVec.xCoord;
        double y = lookVec.yCoord;
        double z = lookVec.zCoord;

        //you're gonna clip into something, we're trying to prevent that.
        if (isAABBInAnythingButAir(player.worldObj, player.boundingBox.getOffsetBoundingBox(x, y, z))) {
            if (Math.abs(x) > Math.abs(y) && Math.abs(x) > Math.abs(z)) {
                if (!isAABBInAnythingButAir(player.worldObj, player.boundingBox.getOffsetBoundingBox(x, 0D, 0D))) {
                    // x is fine
                    if (Math.abs(z) > Math.abs(y)) {
                        if (!isAABBInAnythingButAir(player.worldObj, player.boundingBox.getOffsetBoundingBox(0D, 0D, z))) {
                            //z is fine
                            y = 0D;
                        } else if (!isAABBInAnythingButAir(player.worldObj, player.boundingBox.getOffsetBoundingBox(0D, y, 0D)))
                            z = 0D;
                    } else {
                        if (!isAABBInAnythingButAir(player.worldObj, player.boundingBox.getOffsetBoundingBox(0D, y, 0D))) {
                            //y is fine
                            z = 0D;
                        } else if (!isAABBInAnythingButAir(player.worldObj, player.boundingBox.getOffsetBoundingBox(0D, 0D, z)))
                            y = 0D;
                    }
                } else {
                    //x is not fine
                    x = 0D;
                    //and also do the standard y/z checks
                    if (Math.abs(z) > Math.abs(y)) {
                        if (!isAABBInAnythingButAir(player.worldObj, player.boundingBox.getOffsetBoundingBox(0D, 0D, z))) {
                            //z is fine
                            y = 0D;
                        } else if (!isAABBInAnythingButAir(player.worldObj, player.boundingBox.getOffsetBoundingBox(0D, y, 0D)))
                            z = 0D;
                    } else {
                        if (!isAABBInAnythingButAir(player.worldObj, player.boundingBox.getOffsetBoundingBox(0D, y, 0D))) {
                            //y is fine
                            z = 0D;
                        } else if (!isAABBInAnythingButAir(player.worldObj, player.boundingBox.getOffsetBoundingBox(0D, 0D, z)))
                            y = 0D;
                    }
                }
            } else if (Math.abs(z) > Math.abs(x) && Math.abs(z) > Math.abs(y)) {
                if (!isAABBInAnythingButAir(player.worldObj, player.boundingBox.getOffsetBoundingBox(0D, 0D, z))) {
                    //z is fine
                    if (Math.abs(x) > Math.abs(y)) {
                        if (!isAABBInAnythingButAir(player.worldObj, player.boundingBox.getOffsetBoundingBox(x, 0D, 0D))) {
                            //x is fine
                            y = 0D;
                        } else if (!isAABBInAnythingButAir(player.worldObj, player.boundingBox.getOffsetBoundingBox(0D, y, 0D)))
                            x = 0D;
                    } else {
                        if (!isAABBInAnythingButAir(player.worldObj, player.boundingBox.getOffsetBoundingBox(0D, y, 0D))) {
                            //y is fine
                            x = 0D;
                        } else if (!isAABBInAnythingButAir(player.worldObj, player.boundingBox.getOffsetBoundingBox(x, 0D, 0D)))
                            y = 0D;
                    }
                } else {
                    //z is not fine
                    z = 0D;
                    if (Math.abs(x) > Math.abs(y)) {
                        if (!isAABBInAnythingButAir(player.worldObj, player.boundingBox.getOffsetBoundingBox(x, 0D, 0D))) {
                            //x is fine
                            y = 0D;
                        } else if (!isAABBInAnythingButAir(player.worldObj, player.boundingBox.getOffsetBoundingBox(0D, y, 0D)))
                            x = 0D;
                    } else {
                        if (!isAABBInAnythingButAir(player.worldObj, player.boundingBox.getOffsetBoundingBox(0D, y, 0D))) {
                            //y is fine
                            x = 0D;
                        } else if (!isAABBInAnythingButAir(player.worldObj, player.boundingBox.getOffsetBoundingBox(x, 0D, 0D)))
                            y = 0D;
                    }
                }
            } else if (Math.abs(y) > Math.abs(x) && Math.abs(y) > Math.abs(z)) {
                if (!isAABBInAnythingButAir(player.worldObj, player.boundingBox.getOffsetBoundingBox(0D, y, 0D))) {
                    //y is fine
                    if (Math.abs(x) > Math.abs(z)) {
                        if (!isAABBInAnythingButAir(player.worldObj, player.boundingBox.getOffsetBoundingBox(x, 0D, 0D))) {
                            //x is fine
                            z = 0D;
                        } else  if (!isAABBInAnythingButAir(player.worldObj, player.boundingBox.getOffsetBoundingBox(0D, 0D, z)))
                            x = 0D;
                    } else {
                        if (!isAABBInAnythingButAir(player.worldObj, player.boundingBox.getOffsetBoundingBox(x, 0D, 0D))) {
                            //x is fine
                            z = 0D;
                        } else if (!isAABBInAnythingButAir(player.worldObj, player.boundingBox.getOffsetBoundingBox(0D, 0D, z)))
                            x = 0D;
                    }
                } else {
                    //y is not fine
                    y = 0D;
                    if (Math.abs(x) > Math.abs(z)) {
                        if (!isAABBInAnythingButAir(player.worldObj, player.boundingBox.getOffsetBoundingBox(x, 0D, 0D))) {
                            //x is fine
                            z = 0D;
                        } else  if (!isAABBInAnythingButAir(player.worldObj, player.boundingBox.getOffsetBoundingBox(0D, 0D, z)))
                            x = 0D;
                    } else {
                        if (!isAABBInAnythingButAir(player.worldObj, player.boundingBox.getOffsetBoundingBox(0D, 0D, z))) {
                            //x is fine
                            x = 0D;
                        } else if (!isAABBInAnythingButAir(player.worldObj, player.boundingBox.getOffsetBoundingBox(x, 0D, 0D)))
                            z = 0D;
                    }
                }
            }
            if (isAABBInAnythingButAir(player.worldObj, player.boundingBox.getOffsetBoundingBox(x, y, z))) {
                //we still failed, give up.
                return;
            }

        }

        if (tickUsed % 20 == 0)
            ist.setItemDamage(ist.getItemDamage() + 1);


        //player.setVelocity(x, y, z);
        player.motionX = x;
        player.motionY = y;
        player.motionZ = z;

        player.setPosition(player.posX + x, player.posY + y, player.posZ + z);

        player.fallDistance = 0.0F;


        spawnHurricaneParticles(player.getLookVec(), player);
        return;
    }

    public AxisAlignedBB directionalExpand(double x, double y, double z, AxisAlignedBB aabb)
    {
        double minX = aabb.minX + (x < 0D ? x : 0D);
        double minY = aabb.minY + (y < 0D ? y : 0D);
        double minZ = aabb.minZ + (z < 0D ? z : 0D);
        double maxX = aabb.maxX + (x > 0D ? x : 0D);
        double maxY = aabb.maxY + (y > 0D ? y : 0D);
        double maxZ = aabb.maxZ + (z > 0D ? z : 0D);
        /**
         * Returns a bounding box with the specified bounds. Args: minX, minY, minZ, maxX, maxY, maxZ
         */
        return aabb.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public boolean isAABBInAnythingButAir(World worldObj, AxisAlignedBB aabb)
    {
        int minX = MathHelper.floor_double(aabb.minX);
        int maxX = MathHelper.floor_double(aabb.maxX + 1.0D);
        int minY = MathHelper.floor_double(aabb.minY);
        int maxY = MathHelper.floor_double(aabb.maxY + 1.0D);
        int minZ = MathHelper.floor_double(aabb.minZ);
        int maxZ = MathHelper.floor_double(aabb.maxZ + 1.0D);

        for (int xOff = minX; xOff < maxX; ++xOff)
        {
            for (int yOff = minY; yOff < maxY; ++yOff)
            {
                for (int zOff = minZ; zOff < maxZ; ++zOff)
                {
                    Block block = worldObj.getBlock(xOff, yOff, zOff);

                    if (block.getMaterial() != Material.air && block.getMaterial() != Material.water && block.getMaterial() != Material.lava &&
                            block.getMaterial() != Material.fire && block.getMaterial() != Material.vine && block.getMaterial() != Material.plants)
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean b) {
        EntityPlayer player = null;
        if (e instanceof EntityPlayer) {
            player = (EntityPlayer) e;
        }
        if (player == null)
            return;

        if (player.isSwingInProgress && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().isItemEqual(ist)) {
            for (int frames = 0; frames < 5; frames++) {
                doPushEffect(ist, player, player.getLookVec());
                spawnHurricaneParticles(player.getLookVec(), player);
            }
        }

        if (world.isRemote)
            return;

        if (this.isEnabled(ist)) {
            if (ist.getItemDamage() == 0 || ist.getItemDamage() > 2) {
                if (InventoryHelper.consumeItem(new ItemStack(Items.feather), player)) {
                    ist.setItemDamage(ist.getItemDamage() == 0 ? ist.getMaxDamage() - 2: ist.getItemDamage() - 2);
                }
            }
        }
    }


    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 32000;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack ist) {
        return EnumAction.block;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer player) {
        if (player.isSneaking())
            super.onItemRightClick(ist, world, player);
        else
            player.setItemInUse(ist, this.getMaxItemUseDuration(ist));
        return ist;
    }

    @Override
    public void onUsingTick(ItemStack ist, EntityPlayer player, int count) {
        if (ist.getItemDamage() == 0 || ist.getItemDamage() >= ist.getMaxDamage() - 1)
            return;
        Vec3 lookVector = player.getLookVec();
        spawnHurricaneParticles(lookVector, player);

        attemptFlight(player, ist, count);
    }

    public void doPushEffect(ItemStack ist, EntityPlayer player, Vec3 lookVector) {
        if (ist.getItemDamage() == 0 || ist.getItemDamage() >= ist.getMaxDamage() - 1)
            return;
        spawnHurricaneParticles(lookVector, player);
        if (player.worldObj.isRemote)
            return;

        double lowerX = Math.min(player.posX, player.posX + lookVector.xCoord * 10D);
        double lowerY = Math.min(player.posY + player.getEyeHeight(), player.posY + player.getEyeHeight() + lookVector.yCoord * 10D);
        double lowerZ = Math.min(player.posZ, player.posZ + lookVector.zCoord * 10D);
        double upperX = Math.max(player.posX, player.posX + lookVector.xCoord * 10D);
        double upperY = Math.max(player.posY + player.getEyeHeight(), player.posY + player.getEyeHeight() + lookVector.yCoord * 10D);
        double upperZ = Math.max(player.posZ, player.posZ + lookVector.zCoord * 10D);

        List eList = player.worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(lowerX - 1D, lowerY - 1D, lowerZ - 1D, upperX + 1D, upperY + 1D, upperZ + 1D));

        Iterator iterator = eList.iterator();
        while (iterator.hasNext()) {
            Entity e = (Entity)iterator.next();

            int distance = (int)player.getDistanceToEntity(e);

            int probabilityFactor = (distance - 3) / 2;

            if ( probabilityFactor > 0 && player.worldObj.rand.nextInt(probabilityFactor) != 0) {
                //added to reduce the number of push frames that fail by 50%, no matter what.
                if (player.worldObj.rand.nextInt(2) == 0)
                    continue;
            }

            if (e.equals(player))
                continue;

            e.moveEntity(lookVector.xCoord, lookVector.yCoord, lookVector.zCoord);
        }
    }

    public void spawnHurricaneParticles(Vec3 lookVector, EntityPlayer player) {
        //spawn a whole mess of particles every tick.
        for (int i = 0; i < 3; ++i) {
            float randX = 10F * (player.worldObj.rand.nextFloat() - 0.5F);
            float randY = 10F * (player.worldObj.rand.nextFloat() - 0.5F);
            float randZ = 10F * (player.worldObj.rand.nextFloat() - 0.5F);

            player.worldObj.spawnParticle("blockdust_" + Block.getIdFromBlock(Blocks.snow_layer) + "_" + 0, player.posX + randX, player.posY + randY, player.posZ + randZ, lookVector.xCoord * 5, lookVector.yCoord * 5, lookVector.zCoord * 5);
        }

    }
}
