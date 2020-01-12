package xreliquary.client.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import xreliquary.blocks.tile.ApothecaryCauldronTileEntity;
import xreliquary.init.ModBlocks;

public class ModBlockColors {
	public static void init() {

		if (ModBlocks.APOTHECARY_CAULDRON != null && ModBlocks.APOTHECARY_CAULDRON.getRegistryName() != null) {
			BlockColors blockColors = Minecraft.getInstance().getBlockColors();

			blockColors.register((state, world, pos, tintIndex) -> {
				if (world != null && pos != null) {
					ApothecaryCauldronTileEntity cauldron = (ApothecaryCauldronTileEntity) world.getTileEntity(pos);
					if (cauldron != null) {
						return cauldron.getColorMultiplier();
					}
				}

				return -1;
			}, ModBlocks.APOTHECARY_CAULDRON);
		}
	}
}
