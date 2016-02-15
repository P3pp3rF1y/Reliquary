package xreliquary.items;

import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import xreliquary.Reliquary;
import xreliquary.reference.Names;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by Xeno on 10/11/2014.
 */
public class ItemShearsOfWinter extends ItemBase {
    public ItemShearsOfWinter() {
        super(Names.shears_of_winter);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxDamage(0);
        this.setMaxStackSize(1);
        canRepair = false;
    }


    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, Block block, BlockPos pos , EntityLivingBase player)
    {
        if (block.getMaterial() != Material.leaves && block != Blocks.web && block != Blocks.tallgrass && block != Blocks.vine && block != Blocks.tripwire && !(block instanceof IShearable))
        {
            return super.onBlockDestroyed(stack, world, block, pos, player);
        }
        else
        {
            return true;
        }
    }

    @Override
    public boolean canHarvestBlock(Block block)
    {
        return block == Blocks.web || block == Blocks.redstone_wire || block == Blocks.tripwire;
    }

    @Override
    public float getStrVsBlock(ItemStack stack, Block block)
    {
        return block != Blocks.web && block.getMaterial() != Material.leaves ? (block == Blocks.wool ? 5.0F : super.getStrVsBlock(stack, block)) : 15.0F;
    }

    /**
     * Returns true if the item can be used on the given entity, e.g. shears on sheep.
     */
    @Override
    public boolean itemInteractionForEntity(ItemStack itemstack, EntityPlayer player, EntityLivingBase entity)
    {
        if (entity.worldObj.isRemote)
        {
            return false;
        }
        if (entity instanceof IShearable)
        {
            IShearable target = (IShearable)entity;
            BlockPos pos = new BlockPos(entity.posX, entity.posY, entity.posZ);
            if (target.isShearable(itemstack, entity.worldObj, pos))
            {
                List<ItemStack> drops = target.onSheared(itemstack, entity.worldObj, pos,
                        EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemstack));

                Random rand = new Random();
                for(ItemStack stack : drops)
                {
                    EntityItem ent = entity.entityDropItem(stack, 1.0F);
                    ent.motionY += rand.nextFloat() * 0.05F;
                    ent.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
                    ent.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
                }
                itemstack.damageItem(1, entity);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player)
    {
        if (player.worldObj.isRemote)
        {
            return false;
        }
        Block block = player.worldObj.getBlockState(pos).getBlock();
        if (block instanceof IShearable)
        {
            IShearable target = (IShearable)block;
            if (target.isShearable(itemstack, player.worldObj, pos))
            {
                List<ItemStack> drops = target.onSheared(itemstack, player.worldObj, pos,
                        EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemstack));
                Random rand = new Random();

                for(ItemStack stack : drops)
                {
                    float f = 0.7F;
                    double d  = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    double d1 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    double d2 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    EntityItem entityitem = new EntityItem(player.worldObj, (double)pos.getX() + d, (double)pos.getY() + d1, (double)pos.getZ() + d2, stack);
                    entityitem.setPickupDelay(10);
                    player.worldObj.spawnEntityInWorld(entityitem);
                }

                itemstack.damageItem(1, player);
                player.addStat(StatList.mineBlockStatArray[Block.getIdFromBlock(block)], 1);
            }
        }
        return false;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 2500;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack ist) {
        return EnumAction.BLOCK;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer player) {
        player.setItemInUse(ist, this.getMaxItemUseDuration(ist));
        return ist;
    }

    @Override
    public void onUsingTick(ItemStack ist, EntityPlayer player, int count) {
        //start the blizzard after a short delay, this prevents some abuse.
        if (getMaxItemUseDuration(ist) - count <= 5)
            return;
        Vec3 lookVector = player.getLookVec();
        spawnBlizzardParticles(lookVector, player);

        doEntityShearableCheck(ist, player, lookVector);
        if (lookVector.xCoord > 0)
            doPositiveXCheck(ist, player, lookVector);
        else
            doNegativeXCheck(ist, player, lookVector);

    }

    public void doPositiveXCheck(ItemStack ist, EntityPlayer player, Vec3 lookVector) {
        boolean firedOnce = false;

        for (int x = 0; x < (int)(lookVector.xCoord * 10D); x++) {
            firedOnce = true;
            if (lookVector.yCoord > 0)
                doPositiveYCheck(ist, player, lookVector, x);
            else
                doNegativeYCheck(ist, player, lookVector, x);
        }

        if (!firedOnce) {
            for (int x = -2; x <= 2; x++) {
                if (lookVector.yCoord > 0)
                    doPositiveYCheck(ist, player, lookVector, x);
                else
                    doNegativeYCheck(ist, player, lookVector, x);
            }
        }
    }

    public void doNegativeXCheck(ItemStack ist, EntityPlayer player, Vec3 lookVector) {
        boolean firedOnce = false;

        for (int x = 0; x > (int)(lookVector.xCoord * 10D); x--) {
            firedOnce = true;
            if (lookVector.yCoord > 0)
                doPositiveYCheck(ist, player, lookVector, x);
            else
                doNegativeYCheck(ist, player, lookVector, x);
        }

        if (!firedOnce) {
            for (int x = -2; x <= 2; x++) {
                if (lookVector.yCoord > 0)
                    doPositiveYCheck(ist, player, lookVector, x);
                else
                    doNegativeYCheck(ist, player, lookVector, x);
            }
        }
    }

    public void doPositiveYCheck(ItemStack ist, EntityPlayer player, Vec3 lookVector, int x) {
        boolean firedOnce = false;

        for (int y = 0; y < (int)(lookVector.yCoord * 10D); y++) {
            firedOnce = true;
            if (lookVector.zCoord > 0)
                doPositiveZCheck(ist, player, lookVector, x, y);
            else
                doNegativeZCheck(ist, player, lookVector, x, y);
        }

        if (!firedOnce) {
            for (int y = -2; y <= 2; y++) {
                if (lookVector.zCoord > 0)
                    doPositiveZCheck(ist, player, lookVector, x, y);
                else
                    doNegativeZCheck(ist, player, lookVector, x, y);
            }
        }
    }

    public void doNegativeYCheck(ItemStack ist, EntityPlayer player, Vec3 lookVector, int x) {
        boolean firedOnce = false;

        for (int y = 0; y > (int)(lookVector.yCoord * 10D); y--) {
            firedOnce = true;
            if (lookVector.zCoord > 0)
                doPositiveZCheck(ist, player, lookVector, x, y);
            else
                doNegativeZCheck(ist, player, lookVector, x, y);
        }

        if (!firedOnce) {
            for (int y = -2; y <= 2; y++) {
                if (lookVector.zCoord > 0)
                    doPositiveZCheck(ist, player, lookVector, x, y);
                else
                    doNegativeZCheck(ist, player, lookVector, x, y);
            }
        }
    }

    public void doPositiveZCheck(ItemStack ist, EntityPlayer player, Vec3 lookVector, int x, int y) {
        boolean firedOnce = false;

        for (int z = 0; z < (int)(lookVector.zCoord * 10D); z++) {
            firedOnce = true;
            checkAndBreakBlockAt(x, y, z, player, ist);
        }

        if (!firedOnce) {
            for (int z = -2; z <= 2; z++)
                checkAndBreakBlockAt(x, y, z, player, ist);
        }
    }

    public void doNegativeZCheck(ItemStack ist, EntityPlayer player, Vec3 lookVector, int x, int y) {
        boolean firedOnce = false;

        for (int z = 0; z > (int)(lookVector.zCoord * 10D); z--) {
            firedOnce = true;
            checkAndBreakBlockAt(x, y, z, player, ist);
        }

        if (!firedOnce) {
            for (int z = -2; z <= 2; z++)
                checkAndBreakBlockAt(x, y, z, player, ist);
        }
    }

    public void checkAndBreakBlockAt(int x, int y, int z, EntityPlayer player, ItemStack ist) {
        x += (int)player.posX;
        y += (int)(player.posY + player.getEyeHeight());
        z += (int)player.posZ;

        int distance = (int)player.getDistance((double)x, (double)y, (double)z);
        int probabilityFactor = 5 + distance;
        //chance of block break diminishes over distance
        if (player.worldObj.rand.nextInt(probabilityFactor) == 0) {
            Block block = player.worldObj.getBlockState(new BlockPos(x, y, z)).getBlock();
            if (block instanceof IShearable)
            {
                IShearable target = (IShearable)block;
                if (target.isShearable(new ItemStack(Items.shears, 1, 0), player.worldObj, new BlockPos(x, y, z)))
                {
                    //this commented portion causes the item to be sheared instead of just broken.
                    //ArrayList<ItemStack> drops = target.onSheared(new ItemStack(Items.shears, 1, 0), player.worldObj, x, y, z,
                    //        EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, ist));
                    List<ItemStack> drops = block.getDrops(player.worldObj, new BlockPos(x, y, z), player.worldObj.getBlockState(new BlockPos(x, y, z)),
                              EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, ist));
                    Random rand = new Random();

                    if (player.worldObj.isRemote) {
                        if (block.getMaterial() != Material.air)
                            player.worldObj.playAuxSFXAtEntity(player, 2001,new BlockPos(x, y, z), Block.getStateId(player.worldObj.getBlockState(new BlockPos(x, y, z))));

                    } else {
                        for(ItemStack stack : drops)
                        {
                            float f = 0.7F;
                            double d  = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                            double d1 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                            double d2 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                            EntityItem entityitem = new EntityItem(player.worldObj, (double)x + d, (double)y + d1, (double)z + d2, stack);
                            entityitem.setPickupDelay(10);
                            player.worldObj.spawnEntityInWorld(entityitem);
                        }

                        player.worldObj.setBlockState(new BlockPos(x, y, z), Blocks.air.getDefaultState());
                        player.addStat(StatList.mineBlockStatArray[Block.getIdFromBlock(block)], 1);
                        player.addExhaustion(0.01F);
                    }
                }
            }
            return;
        }
    }

    public void doEntityShearableCheck(ItemStack ist, EntityPlayer player, Vec3 lookVector) {
        if (player.worldObj.isRemote)
            return;
        double lowerX = Math.min(player.posX, player.posX + lookVector.xCoord * 10D);
        double lowerY = Math.min(player.posY + player.getEyeHeight(), player.posY + player.getEyeHeight() + lookVector.yCoord * 10D);
        double lowerZ = Math.min(player.posZ, player.posZ + lookVector.zCoord * 10D);
        double upperX = Math.max(player.posX, player.posX + lookVector.xCoord * 10D);
        double upperY = Math.max(player.posY + player.getEyeHeight(), player.posY + player.getEyeHeight() + lookVector.yCoord * 10D);
        double upperZ = Math.max(player.posZ, player.posZ + lookVector.zCoord * 10D);
        List eList = player.worldObj.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(lowerX, lowerY, lowerZ, upperX, upperY, upperZ));
        Iterator iterator = eList.iterator();
        while (iterator.hasNext()) {
            Entity e = (Entity)iterator.next();

            int distance = (int)player.getDistanceToEntity(e);
            int probabilityFactor = (distance - 3) / 2;
            if ( probabilityFactor > 0 && player.worldObj.rand.nextInt(probabilityFactor) != 0)
                continue;
            if (e instanceof EntityLivingBase && !e.isEntityEqual(player))
                ((EntityLivingBase) e).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id,120, 1));
            if (e instanceof IShearable)
            {
                IShearable target = (IShearable)e;
                if (target.isShearable(new ItemStack(Items.shears, 1, 0), e.worldObj, new BlockPos((int)e.posX, (int)e.posY, (int)e.posZ)))
                {
                    List<ItemStack> drops = target.onSheared(new ItemStack(Items.shears, 1, 0), e.worldObj, new BlockPos((int)e.posX, (int)e.posY, (int)e.posZ),
                            EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, ist));

                    Random rand = new Random();
                    for(ItemStack stack : drops)
                    {
                        EntityItem ent = e.entityDropItem(stack, 1.0F);
                        ent.motionY += rand.nextFloat() * 0.05F;
                        ent.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
                        ent.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
                    }

                    player.addExhaustion(0.01F);
                }
            }
        }
    }

    public void spawnBlizzardParticles(Vec3 lookVector, EntityPlayer player) {
        //spawn a whole mess of particles every tick.
        for (int i = 0; i < 16; ++i) {
            float randX = 10F * (player.worldObj.rand.nextFloat() - 0.5F);
            float randY = 10F * (player.worldObj.rand.nextFloat() - 0.5F);
            float randZ = 10F * (player.worldObj.rand.nextFloat() - 0.5F);

            player.worldObj.spawnParticle(EnumParticleTypes.BLOCK_DUST, player.posX + randX, player.posY + randY, player.posZ + randZ, lookVector.xCoord * 5, lookVector.yCoord * 5, lookVector.zCoord * 5, Block.getStateId(Blocks.snow_layer.getStateFromMeta(0)));

        }

    }


}
