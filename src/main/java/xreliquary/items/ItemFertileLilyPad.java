package xreliquary.items;

import lib.enderwizards.sandstone.items.block.ItemBlockBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemColored;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.init.ModBlocks;
import xreliquary.lib.Names;

public class ItemFertileLilyPad extends ItemBlockBase
{
    //TODO: fix json model link

    public ItemFertileLilyPad(Block block)
    {
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

        if (mop != null) {
            blockPlaced = TryPlacingLilyPad( stack, world, player, mop );
        }

        if (!blockPlaced) {
            return super.onItemUse(stack, player, world, pos, side, hitX, hitY, hitZ);
        }
        return false;
    }


    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
        MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(worldIn, playerIn, true);

        if (movingobjectposition == null)
        {
            return itemStackIn;
        }
        else
        {
            if(!TryPlacingLilyPad( itemStackIn, worldIn, playerIn, movingobjectposition ) )
                return itemStackIn;

            return itemStackIn;
        }
    }

    private boolean TryPlacingLilyPad( ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, MovingObjectPosition movingobjectposition )
    {
        if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
		{
			BlockPos blockpos = movingobjectposition.getBlockPos();

			if (!worldIn.isBlockModifiable(playerIn, blockpos))
			{
                return true;
			}

			if (!playerIn.canPlayerEdit(blockpos.offset(movingobjectposition.sideHit), movingobjectposition.sideHit, itemStackIn))
			{
                return true;
			}

			BlockPos blockpos1 = blockpos.up();
			IBlockState iblockstate = worldIn.getBlockState(blockpos);

			if (iblockstate.getBlock().getMaterial() == Material.water && ((Integer)iblockstate.getValue( BlockLiquid.LEVEL)).intValue() == 0 && worldIn.isAirBlock(blockpos1))
			{
				// special case for handling block placement with water lilies
				net.minecraftforge.common.util.BlockSnapshot blocksnapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot(worldIn, blockpos1);
				worldIn.setBlockState(blockpos1, ModBlocks.fertileLilypad.getDefaultState());
				if (net.minecraftforge.event.ForgeEventFactory.onPlayerBlockPlace(playerIn, blocksnapshot, EnumFacing.UP).isCanceled())
				{
					blocksnapshot.restore(true, false);
                    return false;
				}

				int secondsBetweenGrowthTicks = Reliquary.CONFIG.getInt( Names.lilypad, "seconds_between_growth_ticks");
				worldIn.scheduleBlockUpdate(blockpos1, ModBlocks.fertileLilypad, secondsBetweenGrowthTicks * 20, 1);


				if (!playerIn.capabilities.isCreativeMode)
				{
					--itemStackIn.stackSize;
				}

			}
		}
        return true;
    }
}
