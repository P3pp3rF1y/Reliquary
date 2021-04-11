package xreliquary.client.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import xreliquary.blocks.tile.ApothecaryCauldronTileEntity;
import xreliquary.init.ModBlocks;

public class ModBlockColors {
	private ModBlockColors() {}
	public static void init() {
		BlockColors blockColors = Minecraft.getInstance().getBlockColors();

		blockColors.register((state, world, pos, tintIndex) -> {
			if (world != null && pos != null) {
				ApothecaryCauldronTileEntity cauldron = (ApothecaryCauldronTileEntity) world.getTileEntity(pos);
				if (cauldron != null && cauldron.getLiquidLevel() > 0) {
					return cauldron.getColorMultiplier();
				}
			}

			return -1;
		}, ModBlocks.APOTHECARY_CAULDRON.get());
	}
}
