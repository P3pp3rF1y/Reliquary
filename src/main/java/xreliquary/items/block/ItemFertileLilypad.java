package xreliquary.items.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.init.ContentHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

public class ItemFertileLilypad extends ItemBlock {

    public ItemFertileLilypad(Block block) {
        super(block);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase() + ":" + Names.lilypad);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack i, int renderpass) {
        return itemIcon;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float posX, float posY, float posZ) {
        //this is the "ray-trace" portion of the method
        MovingObjectPosition var4 = this.getMovingObjectPositionFromPlayer(world, player, true);

        boolean blockPlaced = false;
        if (var4 != null && var4.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                int var5 = var4.blockX;
                int var6 = var4.blockY;
                int var7 = var4.blockZ;

                if (world.canMineBlock(player, var5, var6, var7) && player.canPlayerEdit(var5, var6, var7, var4.sideHit, stack)
                        && world.getBlock(var5, var6, var7).getMaterial() == Material.water
                        && world.getBlockMetadata(var5, var6, var7) == 0 && world.isAirBlock(var5, var6 + 1, var7)) {
                    world.setBlock(var5, var6 + 1, var7, ContentHandler.getBlock(Names.lilypad));
                    world.scheduleBlockUpdate(var5, var6 + 1, var7, ContentHandler.getBlock(Names.lilypad), (int)(1360F * ((float) getDelayInSeconds() / 100F)));

                    if (!player.capabilities.isCreativeMode) --stack.stackSize;
                    blockPlaced = true;
                }
        }

        if (!blockPlaced) {
            //on-block-click portion of the method, we're going to make this the "alternative" to trying to raytrace first, since raytrace is optimal.
            Block block = world.getBlock(x, y, z);

            if (block == Blocks.snow_layer && (world.getBlockMetadata(x, y, z) & 7) < 1)
                side = 1;
            else if (block != Blocks.vine && !block.isReplaceable(world, x, y, z)) {
                if (side == 0) --y;
                if (side == 1) ++y;
                if (side == 2) --z;
                if (side == 3) ++z;
                if (side == 4) --x;
                if (side == 5) ++x;
            }

            if (stack.stackSize == 0)
                return false;
            else if (!player.canPlayerEdit(x, y, z, side, stack))
                return false;
            else if (y == 255 && this.field_150939_a.getMaterial().isSolid())
                return false;
            else if (world.canPlaceEntityOnSide(this.field_150939_a, x, y, z, false, side, player, stack))
            {
                int i1 = this.getMetadata(stack.getItemDamage());
                int j1 = this.field_150939_a.onBlockPlaced(world, x, y, z, side, posX, posY, posZ, i1);

                //added a check to make sure that the block can exist at the location before actually placing the thing.
                if (y >= 0 && y < 256 && world.getBlock(x, y - 1, z).getMaterial() == Material.water && world.getBlockMetadata(x, y - 1, z) == 0 && placeBlockAt(stack, player, world, x, y, z, side, posX, posY, posZ, j1))
                {
                    world.playSoundEffect((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), this.field_150939_a.stepSound.func_150496_b(), (this.field_150939_a.stepSound.getVolume() + 1.0F) / 2.0F, this.field_150939_a.stepSound.getPitch() * 0.8F);
                    --stack.stackSize;
                }

                return true;
            }
        }
        return false;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is
     * pressed. Args: itemStack, world, entityPlayer
     */
    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World world, EntityPlayer par3EntityPlayer) {
        MovingObjectPosition var4 = this.getMovingObjectPositionFromPlayer(world, par3EntityPlayer, true);

        if (var4 == null)
            return par1ItemStack;
        else {
            if (var4.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                int var5 = var4.blockX;
                int var6 = var4.blockY;
                int var7 = var4.blockZ;

                if (!world.canMineBlock(par3EntityPlayer, var5, var6, var7))
                    return par1ItemStack;

                if (!par3EntityPlayer.canPlayerEdit(var5, var6, var7, var4.sideHit, par1ItemStack))
                    return par1ItemStack;

                if (world.getBlock(var5, var6, var7).getMaterial() == Material.water && world.getBlockMetadata(var5, var6, var7) == 0 && world.isAirBlock(var5, var6 + 1, var7)) {
                    world.setBlock(var5, var6 + 1, var7, ContentHandler.getBlock(Names.lilypad));
                    world.scheduleBlockUpdate(var5, var6 + 1, var7, ContentHandler.getBlock(Names.lilypad), (int)(1200 * (float) getDelayInSeconds()));

                    if (!par3EntityPlayer.capabilities.isCreativeMode) {
                        --par1ItemStack.stackSize;
                    }
                }
            }

            return par1ItemStack;
        }
    }

    private int getDelayInSeconds() {
        return Reliquary.CONFIG.getInt(Names.lilypad, "seconds_between_growth_ticks");
    }

}
