package xreliquary.items.block;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.init.ModBlocks;
import xreliquary.reference.Settings;

import javax.annotation.Nonnull;

public class ItemFertileLilyPad extends ItemBlockBase {
	public ItemFertileLilyPad() {
		super(ModBlocks.fertileLilypad);
	}

	@Override
	@Nonnull
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
	}

	@Override
	@Nonnull
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		RayTraceResult result = this.rayTrace(world, player, true);

		ItemStack stack = player.getHeldItem(hand);
		if(TryPlacingLilyPad(stack, world, player, result, hand)) {
			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		}
		return new ActionResult<>(EnumActionResult.FAIL, stack);
	}

	private boolean TryPlacingLilyPad(ItemStack itemStack, World worldIn, EntityPlayer playerIn, RayTraceResult result, EnumHand hand) {
		if(result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
			BlockPos blockpos = result.getBlockPos();

			if(!worldIn.isBlockModifiable(playerIn, blockpos)) {
				return true;
			}

			if(!playerIn.canPlayerEdit(blockpos.offset(result.sideHit), result.sideHit, itemStack)) {
				return true;
			}

			BlockPos blockpos1 = blockpos.up();
			IBlockState iblockstate = worldIn.getBlockState(blockpos);

			if(iblockstate.getMaterial() == Material.WATER && iblockstate.getValue(BlockLiquid.LEVEL) == 0 && worldIn.isAirBlock(blockpos1)) {
				// special case for handling block placement with water lilies
				net.minecraftforge.common.util.BlockSnapshot blocksnapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot(worldIn, blockpos1);
				worldIn.setBlockState(blockpos1, ModBlocks.fertileLilypad.getDefaultState());
				if(net.minecraftforge.event.ForgeEventFactory.onPlayerBlockPlace(playerIn, blocksnapshot, EnumFacing.UP, hand).isCanceled()) {
					blocksnapshot.restore(true, false);
					return false;
				}

				int secondsBetweenGrowthTicks = Settings.Blocks.FertileLilypad.secondsBetweenGrowthTicks;
				worldIn.scheduleBlockUpdate(blockpos1, ModBlocks.fertileLilypad, secondsBetweenGrowthTicks * 20, 1);

				if(!playerIn.capabilities.isCreativeMode) {
					itemStack.shrink(1);
				}

			}
		}
		return true;
	}
}
