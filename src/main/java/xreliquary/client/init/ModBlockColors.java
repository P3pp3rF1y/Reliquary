package xreliquary.client.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import xreliquary.blocks.tile.TileEntityCauldron;
import xreliquary.init.ModBlocks;

public class ModBlockColors {
	public static void init() {
		BlockColors blockColors = Minecraft.getMinecraft().getBlockColors();

		if(ModBlocks.apothecaryCauldron.getRegistryName() != null) {
			blockColors.registerBlockColorHandler((state, world, pos, tintIndex) -> {
				if(pos != null) {
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
