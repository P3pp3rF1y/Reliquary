package xreliquary.client.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import xreliquary.blocks.tile.TileEntityCauldron;
import xreliquary.init.ModBlocks;

public class ModBlockColors {
	public static void init() {

		if(ModBlocks.apothecaryCauldron != null && ModBlocks.apothecaryCauldron.getRegistryName() != null) {
	    BlockColors blockColors = Minecraft.getMinecraft().getBlockColors();

			blockColors.registerBlockColorHandler((state, world, pos, tintIndex) -> {
				if(world != null && pos != null) {
					TileEntityCauldron cauldron = (TileEntityCauldron) world.getTileEntity(pos);
					if(cauldron != null) {
						return cauldron.getColorMultiplier();
					}
				}

				return -1;
			}, ModBlocks.apothecaryCauldron);
		}
	}
}
