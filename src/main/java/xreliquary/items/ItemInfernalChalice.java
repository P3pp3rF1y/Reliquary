package xreliquary.items;

import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemToggleable;
import lib.enderwizards.sandstone.util.ContentHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.lib.Names;

/**
 * Created by Xeno on 10/11/2014.
 */
@ContentInit
public class ItemInfernalChalice extends ItemToggleable {
    public ItemInfernalChalice() {
        super(Names.infernal_chalice);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxDamage(513);
        this.setMaxStackSize(1);
        canRepair = false;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer player) {
        if (player.isSneaking()) {
            if (!this.isEnabled(ist))
                doHasGlacialStaffEnabledCheck(player);
            return super.onItemRightClick(ist, world, player);
        }

        float var4 = 1.0F;
        double var5 = player.prevPosX + (player.posX - player.prevPosX) * var4;
        double var7 = player.prevPosY + (player.posY - player.prevPosY) * var4 + 1.62D - player.yOffset;
        double var9 = player.prevPosZ + (player.posZ - player.prevPosZ) * var4;
        MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, true);

        if (mop == null) {
            return ist;
        } else {
            if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                int var13 = mop.blockX;
                int var14 = mop.blockY;
                int var15 = mop.blockZ;

                if (!world.canMineBlock(player, var13, var14, var15))
                    return ist;

                if (!player.canPlayerEdit(var13, var14, var15, mop.sideHit, ist))
                    return ist;

                String ident = ContentHelper.getIdent(world.getBlock(var13, var14, var15));
                if ((ident.equals(ContentHelper.getIdent(Blocks.flowing_lava)) || ident.equals(ContentHelper.getIdent(Blocks.lava))) && world.getBlockMetadata(var13, var14, var15) == 0) {
                    world.setBlock(var13, var14, var15, Blocks.air);
                    ist.setItemDamage(ist.getItemDamage() == 0 ? ist.getMaxDamage() - 1 : ist.getItemDamage() - 1);
                    return ist;
                }

                if (ist.getItemDamage() == 1) {
                    if (mop.sideHit == 0) {
                        --var14;
                    }

                    if (mop.sideHit == 1) {
                        ++var14;
                    }

                    if (mop.sideHit == 2) {
                        --var15;
                    }

                    if (mop.sideHit == 3) {
                        ++var15;
                    }

                    if (mop.sideHit == 4) {
                        --var13;
                    }

                    if (mop.sideHit == 5) {
                        ++var13;
                    }

                    if (!player.canPlayerEdit(var13, var14, var15, mop.sideHit, ist))
                        return ist;

                    if (ist.getItemDamage() != 0 && ist.getItemDamage() <= ist.getMaxDamage() - 1 && this.tryPlaceContainedLiquid(world, ist, var5, var7, var9, var13, var14, var15) && !player.capabilities.isCreativeMode) {
                        ist.setItemDamage(ist.getItemDamage() >= ist.getMaxDamage() - 1 ? 0 : ist.getItemDamage() + 1);
                        return ist;
                    }

                }
            }

            return ist;
        }
    }

    public void doHasGlacialStaffEnabledCheck(EntityPlayer player) {
        for (int i = 0; i < player.inventory.mainInventory.length; i++) {
            if (player.inventory.mainInventory[i] != null && player.inventory.mainInventory[i].getItem() instanceof ItemGlacialStaff) {
                if (((ItemToggleable)player.inventory.mainInventory[i].getItem()).isEnabled(player.inventory.mainInventory[i])) {
                    ((ItemToggleable)player.inventory.mainInventory[i].getItem()).toggleEnabled(player.inventory.mainInventory[i]);
                }
            }
        }
    }

    public boolean tryPlaceContainedLiquid(World par1World, ItemStack ist, double par2, double par4, double par6, int par8, int par9, int par10) {
        if (!par1World.isAirBlock(par8, par9, par10) && par1World.getBlock(par8, par9, par10).getMaterial().isSolid())
            return false;
        else {
            par1World.setBlock(par8, par9, par10, Blocks.flowing_lava, 0, 3);
            return true;
        }
    }

    @Override
    public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean b) {
        EntityPlayer player = null;
        if (e instanceof EntityPlayer) {
            player = (EntityPlayer) e;
        }
        if (player == null)
            return;

        if (this.isEnabled(ist)) {
            doHasGlacialStaffEnabledCheck(player);


            int x = MathHelper.floor_double(player.posX);
            int y = MathHelper.floor_double(player.boundingBox.minY) - 1;
            int z = MathHelper.floor_double(player.posZ);

            for (int xOff = -3; xOff <= 3; xOff++) {
                for (int yOff = -3; yOff <= 3; yOff++) {
                    for (int zOff = -3; zOff <= 3; zOff++) {
                        if (ist.getItemDamage() == 0 || ist.getItemDamage() > 1)
                            doDrainCheck(ist, x, y, z, world, xOff, yOff, zOff);
                    }
                }
            }

        }
    }

    public void doDrainCheck(ItemStack ist, int x, int y, int z, World world, int xOff, int yOff, int zOff) {
        x += xOff;
        y += yOff;
        z += zOff;
        Block block = world.getBlock(x, y, z);
        if (block.getMaterial() == Material.lava && world.getBlockMetadata(x, y, z) == 0) {
            ist.setItemDamage(ist.getItemDamage() == 0 ? ist.getMaxDamage() - 1 : ist.getItemDamage() - 1);
            world.setBlock(x, y, z, Blocks.cobblestone);
        }
    }
}
