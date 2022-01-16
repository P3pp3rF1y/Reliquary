package xreliquary.client.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import xreliquary.blocks.tile.ApothecaryCauldronBlockEntity;
import xreliquary.init.ModBlocks;

public class ModBlockColors {
	private ModBlockColors() {}
	public static void init() {
		BlockColors blockColors = Minecraft.getInstance().getBlockColors();

		blockColors.register((state, world, pos, tintIndex) -> {
			if (world != null && pos != null) {
				ApothecaryCauldronBlockEntity cauldron = (ApothecaryCauldronBlockEntity) world.getBlockEntity(pos);
				if (cauldron != null && cauldron.getLiquidLevel() > 0) {
					return cauldron.getColorMultiplier();
				}
			}

			return -1;
		}, ModBlocks.APOTHECARY_CAULDRON.get());
	}
}
