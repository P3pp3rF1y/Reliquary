package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemToggleable;
import lib.enderwizards.sandstone.util.InventoryHelper;
import lib.enderwizards.sandstone.util.NBTHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.lib.Names;

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
        this.setMaxStackSize(1);
        canRepair = false;
    }

    @Override
    public void addInformation(ItemStack ist, EntityPlayer player, List list, boolean par4) {
        String charge = Integer.toString(NBTHelper.getInteger("feathers", ist));
        this.formatTooltip(ImmutableMap.of("charge", charge), ist, list);
    }

    private static int getChargeLimit() { return Reliquary.CONFIG.getInt(Names.rending_gale, "charge_limit"); }
    public static int getChargeCost() { return Reliquary.CONFIG.getInt(Names.rending_gale, "cast_charge_cost"); }
    private static int getFeathersWorth() { return Reliquary.CONFIG.getInt(Names.rending_gale, "charge_feather_worth"); }
    private static int getBoltChargeCost() { return Reliquary.CONFIG.getInt(Names.rending_gale, "bolt_charge_cost"); }
    private static int getBoltTargetRange() { return Reliquary.CONFIG.getInt(Names.rending_gale, "block_target_range"); }
    private static int getRadialPushRadius() { return Reliquary.CONFIG.getInt(Names.rending_gale, "push_pull_radius"); }
    private static boolean canPushProjectiles() { return Reliquary.CONFIG.getBool(Names.rending_gale, "can_push_projectiles"); }

    @Override
    public boolean isFull3D(){ return true; }

    public void attemptFlight(EntityLivingBase entityLiving) {
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

        //player.setVelocity(x, y, z);
        player.motionX = x;
        player.motionY = y;
        player.motionZ = z;

        player.setPosition(player.posX + x, player.posY + y, player.posZ + z);

        player.fallDistance = 0.0F;

        return;
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
                            block.getMaterial() != Material.fire && block.getMaterial() != Material.vine && block.getMaterial() != Material.plants && block.getMaterial() != Material.circuits)
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
        if (world.isRemote)
            return;
        if (this.isEnabled(ist)) {
            if (NBTHelper.getInteger("feathers", ist) + getFeathersWorth() <= getChargeLimit()) {
                if (InventoryHelper.consumeItem(new ItemStack(Items.feather), player)) {
                    NBTHelper.setInteger("feathers", ist, NBTHelper.getInteger("feathers", ist) + getFeathersWorth());
                }
            }
        }
    }

    public String getMode(ItemStack ist) {
        if (NBTHelper.getString("mode", ist).equals("")) {
            setMode(ist, "flight");
        }
        return NBTHelper.getString("mode", ist);
    }

    public void setMode(ItemStack ist, String s) {
        NBTHelper.setString("mode", ist, s);
    }

    public void cycleMode(ItemStack ist, boolean isRaining) {
        if (getMode(ist).equals("flight"))
            setMode(ist, "push");
        else if (getMode(ist).equals("push"))
            setMode(ist, "pull");
        else if (getMode(ist).equals("pull") && isRaining)
            setMode(ist, "bolt");
        else
            setMode(ist, "flight");
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack ist) {
        if (entityLiving.worldObj.isRemote)
            return false;
        if (!(entityLiving instanceof EntityPlayer))
            return false;
        EntityPlayer player = (EntityPlayer)entityLiving;
        if (player.isSneaking()) {
            cycleMode(ist, player.worldObj.isRaining());
            return true;
        }
        return false;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 64;
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

    //a longer ranged version of "getMovingObjectPositionFromPlayer" basically
    public MovingObjectPosition getCycloneBlockTarget(World world, EntityPlayer player) {
        float f = 1.0F;
        float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
        float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
        double d0 = player.prevPosX + (player.posX - player.prevPosX) * (double)f;
        double d1 = player.prevPosY + (player.posY - player.prevPosY) * (double)f + (double)(world.isRemote ? player.getEyeHeight() - player.getDefaultEyeHeight() : player.getEyeHeight()); // isRemote check to revert changes to ray trace position due to adding the eye height clientside and player yOffset differences
        double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double)f;
        Vec3 vec3 = Vec3.createVectorHelper(d0, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - (float)Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d3 = (double) getBoltTargetRange();
        Vec3 vec31 = vec3.addVector((double)f7 * d3, (double)f6 * d3, (double)f8 * d3);
        return world.func_147447_a(vec3, vec31, true, false, false);
    }

    @Override
    public void onUsingTick(ItemStack ist, EntityPlayer player, int count) {
        if (NBTHelper.getInteger("feathers", ist) < getChargeCost())
            return;
        count -= 1;
        count = getMaxItemUseDuration(ist) - count;
        if (count == getMaxItemUseDuration(ist) || (getMaxItemUseDuration(ist) - count) * getChargeCost() >= NBTHelper.getInteger("feathers", ist)) {
            int chargeUsed = count * getChargeCost();
            NBTHelper.setInteger("feathers", ist, NBTHelper.getInteger("feathers", ist) - chargeUsed);
            player.stopUsingItem();
        }

        if (getMode(ist).equals("flight")) {
            attemptFlight(player);
            spawnFlightParticles(player.worldObj, player.posX, player.posY + player.getEyeHeight(), player.posZ, player.getLookVec());
        } else if (getMode(ist).equals("push")) {
            doRadialPush(player, false);
        } else if (getMode(ist).equals("pull")) {
            doRadialPush(player, true);
            //doPushEffect(player, player.worldObj, player.posX, player.posY + player.getEyeHeight(), player.posZ, player.getLookVec());
            //spawnFlightParticles(player.worldObj, player.posX, player.posY + player.getEyeHeight(), player.posZ, player.getLookVec());

        } else if (getMode(ist).equals("bolt")) {
            MovingObjectPosition mop = this.getCycloneBlockTarget(player.worldObj, player);

            if (mop != null) {
                if (count % 8 == 0) {
                    int attemptedY = mop.blockY;
                    if (!player.worldObj.canLightningStrikeAt(mop.blockX, mop.blockY, mop.blockZ)) {
                        attemptedY++;
                    }
                    if (player.worldObj.canLightningStrikeAt(mop.blockX, attemptedY, mop.blockZ)) {
                        if (NBTHelper.getInteger("feathers", ist) >= getBoltChargeCost()) {
                            NBTHelper.setInteger("feathers", ist, NBTHelper.getInteger("feathers", ist) - getBoltChargeCost());
                            player.worldObj.addWeatherEffect(new EntityLightningBolt(player.worldObj, (double) mop.blockX, (double) mop.blockY, (double) mop.blockZ));
                        }
                    }
                }
            }
        }
    }

    //experimenting with a more sophisticated charge/drain mechanism
    @Override
    public void onPlayerStoppedUsing(ItemStack ist, World world, EntityPlayer player, int count) {
        if (world.isRemote)
            return;
        //count starts at 64 instead of 63, so it needs to account for its first used tick.
        count -= 1;
        int chargeUsed = (getMaxItemUseDuration(ist) - count) * getChargeCost();
        NBTHelper.setInteger("feathers", ist, NBTHelper.getInteger("feathers", ist) - Math.min(chargeUsed, NBTHelper.getInteger("feathers", ist)));
    }

    public void doRadialPush(EntityPlayer player, boolean pull) {
        //push effect free at the moment, if you restore cost, remember to change this to NBTHelper.getInteger("feathers", ist)
        spawnRadialHurricaneParticles(player, pull);
        if (player.worldObj.isRemote)
            return;

        double lowerX = player.posX - getRadialPushRadius();
        double lowerY = player.posY - (double)getRadialPushRadius() / 5D;
        double lowerZ = player.posZ - getRadialPushRadius();
        double upperX = player.posX + getRadialPushRadius();
        double upperY = player.posY + (double)getRadialPushRadius() / 2D;
        double upperZ = player.posZ + getRadialPushRadius();


        List<String> entitiesThatCanBePushed = (List<String>) Reliquary.CONFIG.get(Names.rending_gale, "entities_that_can_be_pushed");
        List<String> projectilesThatCanBePushed = (List<String>) Reliquary.CONFIG.get(Names.rending_gale, "projectiles_that_can_be_pushed");

        List eList = player.worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(lowerX, lowerY, lowerZ, upperX, upperY, upperZ));

        Iterator iterator = eList.iterator();
        while (iterator.hasNext()) {
            Entity e = (Entity)iterator.next();
            Class entityClass = e.getClass();
            String entityName = (String) EntityList.classToStringMapping.get(entityClass);
            if (entitiesThatCanBePushed.contains(entityName) || (!pull && canPushProjectiles() && projectilesThatCanBePushed.contains(entityName))) {
                double distance = player.getDistanceToEntity(e);
                if (distance >= getRadialPushRadius())
                    continue;

                if (e.equals(player))
                    continue;
                Vec3 pushVector;
                if (pull) {
                    pushVector = Vec3.createVectorHelper(player.posX - e.posX, player.posY - e.posY, player.posZ - e.posZ);
                } else {
                    pushVector = Vec3.createVectorHelper(e.posX - player.posX, e.posY - player.posY, e.posZ - player.posZ);
                }
                pushVector = pushVector.normalize();
                e.moveEntity(0.0D, 0.2D, 0.0D);
                e.moveEntity(pushVector.xCoord, Math.min(pushVector.yCoord, 0.1D) * 1.5D, pushVector.zCoord);
            }
        }
    }

    public void spawnFlightParticles(World world, double x, double y, double z, Vec3 lookVector) {
        //spawn a whole mess of particles every tick.
        for (int i = 0; i < 8; ++i) {
            float randX = 10F * (itemRand.nextFloat() - 0.5F);
            float randY = 10F * (itemRand.nextFloat() - 0.5F);
            float randZ = 10F * (itemRand.nextFloat() - 0.5F);

            world.spawnParticle("blockdust_" + Block.getIdFromBlock(Blocks.snow_layer) + "_" + 0, x + randX, y + randY, z + randZ, lookVector.xCoord * 5, lookVector.yCoord * 5, lookVector.zCoord * 5);
        }
    }

    public void spawnRadialHurricaneParticles(EntityPlayer player, boolean pull) {
        //spawn a whole mess of particles every tick.
        for (int i = 0; i < 3; ++i) {
            float randX = player.worldObj.rand.nextFloat() - 0.5F;
            float randZ = player.worldObj.rand.nextFloat() - 0.5F;
            float motX = randX * 10F;
            float motZ = randZ * 10F;
            if (pull) {
                randX *= 10F;
                randZ *= 10F;
                motX *= -1F;
                motZ *= -1F;
            }

            player.worldObj.spawnParticle("blockdust_" + Block.getIdFromBlock(Blocks.snow_layer) + "_" + 0, player.posX + randX, (player.posY + player.getEyeHeight()) - (player.height / 2), player.posZ + randZ, motX, 0.0D, motZ);
        }

    }
}
