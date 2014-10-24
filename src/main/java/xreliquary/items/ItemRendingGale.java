package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemToggleable;
import lib.enderwizards.sandstone.util.InventoryHelper;
import lib.enderwizards.sandstone.util.NBTHelper;
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
    private static int getColumnSize() { return Reliquary.CONFIG.getInt(Names.rending_gale, "push_column_size"); }
    private int getColumnLength() { return ((getColumnSize() * 2) + 1) * 2; }
    private static int getVerticalColumnTargetingRange() { return Reliquary.CONFIG.getInt(Names.rending_gale, "horizontal_column_radius"); }
    private static int getRadialPushRadius() { return Reliquary.CONFIG.getInt(Names.rending_gale, "radial_push_radius"); }
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
        return NBTHelper.getString("flight", ist);
    }

    public void setMode(ItemStack ist, String s) {
        NBTHelper.setString("mode", ist, s);
    }

    public void cycleMode(ItemStack ist) {
        if (getMode(ist).equals("flight"))
            setMode(ist, "lift");
        else if (getMode(ist).equals("lift"))
            setMode(ist, "push");
        else if (getMode(ist).equals("push"))
            setMode(ist, "radial");
        else if (getMode(ist).equals("radial"))
            setMode(ist, "bolt");
        else
            setMode(ist, "flight");
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack ist) {
        if (entityLiving.worldObj.isRemote)
            return true;
        if (!(entityLiving instanceof EntityPlayer))
            return true;
        EntityPlayer player = (EntityPlayer)entityLiving;
        if (player.isSneaking()) {
            cycleMode(ist);
        }
        return false;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 11;
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
        double d3 = 12.0D;
        Vec3 vec31 = vec3.addVector((double)f7 * d3, (double)f6 * d3, (double)f8 * d3);
        return world.func_147447_a(vec3, vec31, true, false, false);
    }

    @Override
    public void onUsingTick(ItemStack ist, EntityPlayer player, int count) {
        if ((getMaxItemUseDuration(ist) - count) * getChargeCost() >= NBTHelper.getInteger("feathers", ist)) {
            player.stopUsingItem();
        }
        if (getMode(ist).equals("flight")) {
            attemptFlight(player);
            for (int frames = 0; frames < 7; ++frames)
                spawnHurricaneParticles(player.worldObj, player.posX, player.posY + player.getEyeHeight(), player.posZ, player.getLookVec());
        } else if (getMode(ist).equals("radial")) {
            doRadialPush(player);
        } else if (getMode(ist).equals("push")) {
            for (int frames = 0; frames < 7; frames++) {
                doPushEffect(player, player.worldObj, player.posX, player.posY + player.getEyeHeight(), player.posZ, player.getLookVec());
                spawnHurricaneParticles(player.worldObj, player.posX, player.posY + player.getEyeHeight(), player.posZ, player.getLookVec());
            }
        } else if (getMode(ist).equals("lift") || getMode(ist).equals("bolt")) {
            MovingObjectPosition mop = this.getCycloneBlockTarget(player.worldObj, player);

            if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                if (getMode(ist).equals("lift")) {
                    double x = (double) mop.blockX + 0.5D;
                    double y = (double) mop.blockY + 1D;
                    double z = (double) mop.blockZ + 0.5D;
                    Vec3 straightUpVector = Vec3.createVectorHelper(0D, 1D, 0D);
                    for (int frames = 0; frames < 7; frames++) {
                        doPushEffect(player, player.worldObj, x, y, z, straightUpVector);
                        spawnHurricaneParticles(player.worldObj, x, y, z, straightUpVector);
                    }
                } else {
                    //todo
                    //lightning bolt mechanics, these should have a really heavy drain on charge
                }
            }
        }
    }

    //experimenting with a more sophisticated charge/drain mechanism
    @Override
    public void onPlayerStoppedUsing(ItemStack ist, World world, EntityPlayer player, int count) {
        int chargeUsed = (getMaxItemUseDuration(ist) - count) * getChargeCost();
        NBTHelper.setInteger("feathers", ist, NBTHelper.getInteger("feathers", ist) - chargeUsed);
    }

    public void doRadialPush(EntityPlayer player) {
        //push effect free at the moment, if you restore cost, remember to change this to NBTHelper.getInteger("feathers", ist)
        spawnRadialHurricaneParticles(player);
        if (player.worldObj.isRemote)
            return;

        double lowerX = player.posX - getRadialPushRadius();
        double lowerY = player.posY - (double)getRadialPushRadius() / 5D;
        double lowerZ = player.posZ - getRadialPushRadius();
        double upperX = player.posX + getRadialPushRadius();
        double upperY = player.posY + (double)getRadialPushRadius() / 2D;
        double upperZ = player.posZ + getRadialPushRadius();

        List eList = player.worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(lowerX, lowerY, lowerZ, upperX, upperY, upperZ));

        Iterator iterator = eList.iterator();
        while (iterator.hasNext()) {
            Entity e = (Entity)iterator.next();

            double distance = player.getDistanceToEntity(e);
            if (distance >= getRadialPushRadius())
                continue;

            if (e.equals(player))
                continue;
            Vec3 pushVector = Vec3.createVectorHelper(e.posX - player.posX, e.posY - player.posY, e.posZ - player.posZ);
            pushVector= pushVector.normalize();
            e.moveEntity(0.0D, 0.2D, 0.0D);
            e.moveEntity(pushVector.xCoord, Math.min(pushVector.yCoord,0.1D) * 1.5D, pushVector.zCoord);
        }
    }

    public void doPushEffect(EntityPlayer player, World world, double x, double y, double z, Vec3 lookVector) {
        double xDiff;
        if (lookVector.xCoord < 0)
            xDiff = Math.min(lookVector.xCoord * (double)getColumnLength(), -(double)getColumnSize());
        else
            xDiff = Math.max(lookVector.xCoord * (double)getColumnLength(), (double)getColumnSize());

        double yDiff;
        if (lookVector.yCoord < 0)
            yDiff = Math.min(lookVector.yCoord * (double)getColumnLength(), -(double)getColumnSize());
        else
            yDiff = Math.max(lookVector.yCoord * (double)getColumnLength(), (double)getColumnSize());

        double zDiff;
        if (lookVector.zCoord < 0)
            zDiff = Math.min(lookVector.zCoord * (double)getColumnLength(), -(double)getColumnSize());
        else
            zDiff = Math.max(lookVector.zCoord * (double)getColumnLength(), (double)getColumnSize());

        double lowerX = Math.min(x, x + xDiff);
        double lowerY = Math.min(y, y + yDiff);
        double lowerZ = Math.min(z, z + zDiff);
        double upperX = Math.max(x, x + xDiff);
        double upperY = Math.max(y, y + yDiff);
        double upperZ = Math.max(z, z + zDiff);

        List eList = world.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(lowerX, lowerY, lowerZ, upperX, upperY, upperZ));

        Iterator iterator = eList.iterator();
        while (iterator.hasNext()) {
            Entity e = (Entity)iterator.next();

            if (e.equals(player))
                continue;

            //always has some upward force
            e.moveEntity(0.0D, 0.2D, 0.0D);
            e.moveEntity(lookVector.xCoord, Math.min(lookVector.yCoord,0.1D) * 1.5D, lookVector.zCoord);
        }
    }

    public void spawnHurricaneParticles(World world, double x, double y, double z, Vec3 lookVector) {
        //spawn a whole mess of particles every tick.
        for (int i = 0; i < 3; ++i) {
            float randX = (float)getColumnLength() * (itemRand.nextFloat() - 0.5F);
            float randY = (float)getColumnLength() * (itemRand.nextFloat() - 0.5F);
            float randZ = (float)getColumnLength() * (itemRand.nextFloat() - 0.5F);

            world.spawnParticle("blockdust_" + Block.getIdFromBlock(Blocks.snow_layer) + "_" + 0, x + randX, y + randY, z + randZ, lookVector.xCoord * 5, lookVector.yCoord * 5, lookVector.zCoord * 5);
        }
    }

    public void spawnRadialHurricaneParticles(EntityPlayer player) {
        //spawn a whole mess of particles every tick.
        for (int i = 0; i < 24; ++i) {
            float randX = 2F * (player.worldObj.rand.nextFloat() - 0.5F);
            float randY = 2F * (player.worldObj.rand.nextFloat() - 0.5F);
            float randZ = 2F * (player.worldObj.rand.nextFloat() - 0.5F);

            player.worldObj.spawnParticle("blockdust_" + Block.getIdFromBlock(Blocks.snow_layer) + "_" + 0, player.posX + randX, player.posY + randY, player.posZ + randZ, player.worldObj.rand.nextGaussian(), player.worldObj.rand.nextGaussian(), player.worldObj.rand.nextGaussian());
        }

    }
}
