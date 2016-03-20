package xreliquary.client.init;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import xreliquary.blocks.tile.TileEntityCauldron;
import xreliquary.init.ModBlocks;

public class ModBlockColors {
	public static void init() {
		BlockColors blockColors = Minecraft.getMinecraft().getBlockColors();

		blockColors.registerBlockColorHandler(new IBlockColor() {
			@Override
			public int colorMultiplier(IBlockState state, IBlockAccess world, BlockPos pos, int tintIndex) {
				TileEntityCauldron cauldron = (TileEntityCauldron) world.getTileEntity(pos);
				if(cauldron != null) {
					return cauldron.getColorMultiplier();
				}

				return -1;
			}
		}, new Block[] {ModBlocks.apothecaryCauldron});
	}
}
