package reliquary.client.init;

import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import reliquary.blocks.tile.ApothecaryCauldronBlockEntity;
import reliquary.init.ModBlocks;

public class ModBlockColors {
	private ModBlockColors() {
	}

	public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
		event.register((state, world, pos, tintIndex) -> {
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
