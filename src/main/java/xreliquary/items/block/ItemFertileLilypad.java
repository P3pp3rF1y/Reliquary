package xreliquary.items.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.lib.Names;

public class ItemFertileLilypad extends ItemBlock {

    public ItemFertileLilypad(Block block) {
        super(block);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        //this is the "ray-trace" portion of the method
        MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, true);

        boolean blockPlaced = false;
        if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {

                if (world.isBlockModifiable(player, mop.getBlockPos()) && player.canPlayerEdit(mop.getBlockPos(), mop.sideHit, stack)
                        && world.getBlockState(mop.getBlockPos()).getBlock().getMaterial() == Material.water
                        && world.getBlockState(mop.getBlockPos()).getValue(BlockLiquid.LEVEL) == 0 && world.isAirBlock(mop.getBlockPos().add(0,1,0))) {
                    world.setBlockState(mop.getBlockPos().add(0,1,0), Reliquary.CONTENT.getBlock(Names.lilypad).getDefaultState());
                    world.scheduleBlockUpdate(mop.getBlockPos().add(0,1,0), Reliquary.CONTENT.getBlock(Names.lilypad), (int)(1360F * ((float) getDelayInSeconds() / 100F)),1);

                    if (!player.capabilities.isCreativeMode) --stack.stackSize;
                    blockPlaced = true;
                }
        }

        if (!blockPlaced) {
            //TODO: make sure that calling this here doesn't break logic when lilypad is not placed due to condition not met above
            return super.onItemUse(stack, player, world, pos, side, hitX, hitY, hitZ);
        }
        return false;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is
     * pressed. Args: itemStack, world, entityPlayer
     */
    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World world, EntityPlayer par3EntityPlayer) {
        MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, par3EntityPlayer, true);

        if (mop == null)
            return par1ItemStack;
        else {
            if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                if (!world.isBlockModifiable(par3EntityPlayer, mop.getBlockPos()))
                    return par1ItemStack;

                if (!par3EntityPlayer.canPlayerEdit(mop.getBlockPos(), mop.sideHit, par1ItemStack))
                    return par1ItemStack;

                if (world.getBlockState(mop.getBlockPos()).getBlock().getMaterial() == Material.water && world.getBlockState(mop.getBlockPos()).getValue(BlockLiquid.LEVEL) == 0 && world.isAirBlock(mop.getBlockPos().add(0,1,0))) {
                    world.setBlockState(mop.getBlockPos().add(0,1,0), Reliquary.CONTENT.getBlock(Names.lilypad).getDefaultState());
                    world.scheduleBlockUpdate(mop.getBlockPos().add(0,1,0), Reliquary.CONTENT.getBlock(Names.lilypad), (int)(1200 * (float) getDelayInSeconds()),1);

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
