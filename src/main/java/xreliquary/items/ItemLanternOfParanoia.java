package xreliquary.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.init.ContentHandler;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemBase;
import lib.enderwizards.sandstone.items.ItemToggleable;
import lib.enderwizards.sandstone.util.ContentHelper;
import lib.enderwizards.sandstone.util.InventoryHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.lib.Names;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xeno on 5/15/14.
 */
@ContentInit
public class ItemLanternOfParanoia extends ItemToggleable {

    public ItemLanternOfParanoia() {
        super(Names.lantern_of_paranoia);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxStackSize(1);
        canRepair = false;
    }

    // so it can be extended by phoenix down
    protected ItemLanternOfParanoia(String name) {
        super(name);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }

    public int getRadius() { return Reliquary.CONFIG.getInt(Names.lantern_of_paranoia, "placement_scan_radius"); }
    // event driven item, does nothing here.

    // minor jump buff
    @Override
    public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean f) {
        if (!this.isEnabled(ist))
            return;
        if (e instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) e;
            if (e instanceof EntityPlayer) {
                player = (EntityPlayer) e;
            }
            if (player == null)
                return;


            //TODO this is where we'll be placing our algorithm for darkness detection and placing torches!

            //TODO ACTUALLY make this configurable
            // always on for now, takes effect only at a configurable light level

            int playerX = MathHelper.floor_double(player.posX);
            int playerY = MathHelper.floor_double(player.boundingBox.minY);
            int playerZ = MathHelper.floor_double(player.posZ);

            for (int xDiff = -getRadius(); xDiff <= getRadius(); xDiff++) {
                for (int zDiff = -getRadius(); zDiff <= getRadius(); zDiff++) {
                    for (int yDiff = getRadius() / 2; yDiff >= -getRadius() / 2; yDiff--) {
                        int x = playerX + xDiff;
                        int y = playerY + yDiff;
                        int z = playerZ + zDiff;
                        if (!player.worldObj.isAirBlock(x, y, z))
                            continue;
                        int lightLevel = player.worldObj.getBlockLightValue(x, y, z);
                        if (lightLevel > Reliquary.CONFIG.getInt(Names.lantern_of_paranoia, "min_light_level"))
                            continue;
                        tryToPlaceTorchAround(ist, x, y, z, player, world);
                    }
                }
            }
        }
    }

//    private boolean findAndRemoveTorch(EntityPlayer player) {
//        List<String> torches = (List<String>) Reliquary.CONFIG.get(Names.sojourner_staff, "torches");
//        List<Item> items = new ArrayList<Item>();
//
//        for (String torch : torches) {
//            items.add(ContentHandler.getItem(torch));
//        }
//        return InventoryHelper.consumeItem(items.toArray(), player, 0, 1);
//    }

    private boolean findAndDrainSojournersStaff(EntityPlayer player) {
        Item staffItem = ContentHandler.getItem(Names.sojourner_staff);
        if (player.capabilities.isCreativeMode)
            return true;
        for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++) {
            if (player.inventory.getStackInSlot(slot) == null)
                continue;
            if (!(staffItem == player.inventory.getStackInSlot(slot).getItem()))
                continue;
            Item torch = ItemBlock.getItemFromBlock(Blocks.torch);
            if (((ItemSojournerStaff)staffItem).removeItemFromInternalStorage(player.inventory.getStackInSlot(slot), torch, 1))
                return true;
        }
        return false;
    }

    public void tryToPlaceTorchAround(ItemStack ist, int xO, int yO, int zO, EntityPlayer player, World world) {
        Block var12 = Blocks.torch;

        int x = xO;
        int y = yO;
        int z = zO;
        float xOff = (float)player.posX;
        float zOff = (float)player.posZ;
        float yOff = (float)player.posY;

//        for (int yD = 2; yD >= -2; yD--) {
//            int y = yO + yD;
            if (Blocks.torch.canPlaceBlockAt(world, x, y, z)) {

                int rotation = ((MathHelper.floor_double((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) + 2) % 4;
                int trySide = 0;
                switch (rotation) {
                    case (0):
                        trySide = 5;
                        break;
                    case (1):
                        trySide = 3;
                        break;
                    case (2):
                        trySide = 4;
                        break;
                    case (3):
                        trySide = 2;
                        break;
                }

                List<Integer> trySides = new ArrayList<Integer>();
                trySides.add(trySide);
                trySides.add(0);
                int[] tryOtherSides = {2, 3, 4, 5};
                for (int tryOtherSide : tryOtherSides) {
                    if (trySides.contains(tryOtherSide)) continue;
                    trySides.add(tryOtherSide);
                }
                for (int side : trySides) {
                    if (!world.canPlaceEntityOnSide(Blocks.torch, x, y, z, false, side, player, ist))
                        continue;
                    if (!(InventoryHelper.consumeItem(Blocks.torch, player, 0, 1) || findAndDrainSojournersStaff(player)))
                        continue;
                    if (placeBlockAt(ist, player, world, x, y, z, side, xOff, yOff, zOff, attemptSide(world, x, y, z, side))) {
                        Blocks.torch.onBlockAdded(world, x, y, z);
                        double gauss = 0.5D + world.rand.nextFloat() / 2;
                        world.spawnParticle("mobSpell", x + 0.5D, y + 0.5D, z + 0.5D, gauss, gauss, 0.0F);
                        world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, var12.stepSound.getStepResourcePath(), (var12.stepSound.getVolume() + 1.0F) / 2.0F, var12.stepSound.getPitch() * 0.8F);
                        return;
                    }
                }
            }
        //}
    }

    private int attemptSide(World world, int x, int y, int z, int side) {
        return Blocks.torch.onBlockPlaced(world, x, y, z, side, x, y, z, 0);
    }

    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
        if (!world.setBlock(x, y, z, Blocks.torch, metadata, 3))
            return false;

        if (ContentHelper.areBlocksEqual(world.getBlock(x, y, z), Blocks.torch)) {
            Blocks.torch.onNeighborBlockChange(world, x, y, z, world.getBlock(x, y, z));
            Blocks.torch.onBlockPlacedBy(world, x, y, z, player, stack);
        }

        return true;
    }
}
