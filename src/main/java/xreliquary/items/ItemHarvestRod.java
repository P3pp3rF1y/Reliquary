package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemToggleable;
import lib.enderwizards.sandstone.util.InventoryHelper;
import lib.enderwizards.sandstone.util.LanguageHelper;
import lib.enderwizards.sandstone.util.NBTHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.IGrowable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import org.lwjgl.input.Keyboard;
import xreliquary.Reliquary;
import xreliquary.blocks.BlockFertileLilypad;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Xeno on 10/11/2014.
 */
@ContentInit
public class ItemHarvestRod extends ItemToggleable {
    public ItemHarvestRod() {
        super(Names.harvest_rod);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxStackSize(1);
        canRepair = false;
    }

    @Override
    public void addInformation(ItemStack ist, EntityPlayer player, List list, boolean par4) {
        if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
            return;
        this.formatTooltip(ImmutableMap.of("charge", Integer.toString(NBTHelper.getInteger("bonemeal", ist))), ist, list);
        if(this.isEnabled(ist))
            LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", EnumChatFormatting.WHITE + Items.dye.getItemStackDisplayName(new ItemStack(Items.dye, 1, Reference.WHITE_DYE_META))), ist, list);
        LanguageHelper.formatTooltip("tooltip.absorb", null, ist, list);
    }

    @Override
    public boolean isFull3D(){ return true; }

    public int getBonemealLimit() { return Reliquary.CONFIG.getInt(Names.harvest_rod, "bonemeal_limit"); }
    public int getBonemealWorth() { return Reliquary.CONFIG.getInt(Names.harvest_rod, "bonemeal_worth"); }
    public int getBonemealCost() { return Reliquary.CONFIG.getInt(Names.harvest_rod, "bonemeal_cost"); }
    public int getLuckRolls() { return Reliquary.CONFIG.getInt(Names.harvest_rod, "bonemeal_luck_rolls"); }
    public int getLuckPercent() { return Reliquary.CONFIG.getInt(Names.harvest_rod, "bonemeal_luck_percent_chance"); }
    public int getBreakRadius() { return Reliquary.CONFIG.getInt(Names.harvest_rod, "harvest_break_radius"); }

    @Override
    public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean b) {
        if (world.isRemote)
            return;
        EntityPlayer player = null;
        if (e instanceof EntityPlayer) {
            player = (EntityPlayer) e;
        }
        if (player == null)
            return;

        if (this.isEnabled(ist)) {
            if (NBTHelper.getInteger("bonemeal", ist) + getBonemealWorth() <= getBonemealLimit()) {
                if (InventoryHelper.consumeItem(new ItemStack(Items.dye, 1, Reference.WHITE_DYE_META), player)) {
                    NBTHelper.setInteger("bonemeal", ist, NBTHelper.getInteger("bonemeal", ist) + getBonemealWorth());
                }
            }
        }
    }

    @Override
    public boolean onBlockStartBreak(ItemStack ist, int x, int y, int z, EntityPlayer player) {
        if (player.worldObj.isRemote)
            return false;

        Block block = player.worldObj.getBlock(x, y, z);
        if (block instanceof IPlantable || block instanceof IGrowable) {
            for (int xOff = -getBreakRadius(); xOff <= getBreakRadius(); xOff++) {
                for (int yOff = -getBreakRadius(); yOff <= getBreakRadius(); yOff++) {
                    for (int zOff = -getBreakRadius(); zOff <= getBreakRadius(); zOff++) {
                        doHarvestBlockBreak(ist, x, y, z, player, xOff, yOff, zOff);
                    }
                }
            }
        }

        return true;
    }

    public void doHarvestBlockBreak(ItemStack ist, int x, int y, int z, EntityPlayer player, int xOff, int yOff, int zOff) {
        x += xOff;
        y += yOff;
        z += zOff;

        Block block = player.worldObj.getBlock(x, y, z);

        if (!(block instanceof IPlantable) && !(block instanceof BlockCrops))
            return;
        if (block instanceof BlockFertileLilypad)
            return;

        ArrayList<ItemStack> drops = block.getDrops(player.worldObj, x, y, z, player.worldObj.getBlockMetadata(x, y, z),
                EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, ist));
        Random rand = new Random();

        if (player.worldObj.isRemote) {
            for (int particles = 0; particles <= 8; particles++)player.worldObj.playAuxSFXAtEntity(player, 2001, x, y, z, Block.getIdFromBlock(block) + (player.worldObj.getBlockMetadata(x, y, z) << 12));
        } else {
            for(ItemStack stack : drops)
            {
                float f = 0.7F;
                double d  = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                double d1 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                double d2 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                EntityItem entityitem = new EntityItem(player.worldObj, (double)x + d, (double)y + d1, (double)z + d2, stack);
                entityitem.delayBeforeCanPickup = 10;
                player.worldObj.spawnEntityInWorld(entityitem);
            }

            player.worldObj.setBlock(x, y, z, Blocks.air);
            player.addStat(StatList.mineBlockStatArray[Block.getIdFromBlock(block)], 1);
            player.addExhaustion(0.01F);
        }
    }

    @Override
    public boolean onItemUse(ItemStack ist, EntityPlayer player, World world, int x, int y, int z, int side, float xOff, float yOff, float zOff) {
        if (NBTHelper.getInteger("bonemeal", ist) >= getBonemealCost()) {
            ItemStack fakeItemStack = new ItemStack(Items.dye, 1, Reference.WHITE_DYE_META);
            ItemDye fakeItemDye = (ItemDye)fakeItemStack.getItem();

            boolean usedRod = false;
            for (int repeatedUses = 0; repeatedUses <= getLuckRolls(); repeatedUses++) {
                if (repeatedUses == 0 || world.rand.nextInt(100) <= getLuckPercent()) {
                    if (fakeItemDye.onItemUse(fakeItemStack, player, world, x, y, z, side, xOff, yOff, zOff)) {
                        if (!usedRod) usedRod = true;
                        player.worldObj.playSoundAtEntity(player, "random.orb", 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.2F));
                    }
                }
            }

            if (usedRod)
                NBTHelper.setInteger("bonemeal", ist, NBTHelper.getInteger("bonemeal", ist) - getBonemealCost());
        }

        return true;
    }
}