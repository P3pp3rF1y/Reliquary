package reliquary.client.init;

import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import reliquary.block.tile.ApothecaryCauldronBlockEntity;
import reliquary.init.ModBlocks;

import java.util.List;

public class ModBlockColors {
	private ModBlockColors() {
	}

	public static void registerBlockColors(RegisterColorHandlersEvent.BlockTintSources event) {
		event.register(List.of(BlockTintSources.constant(-1), new BlockTintSource() {
			@Override
			public int color(BlockState state) {
				return -1;
			}

			@Override
			public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
				if (level.getBlockEntity(pos) instanceof ApothecaryCauldronBlockEntity cauldron && cauldron.getLiquidLevel() > 0) {
					return cauldron.getColorMultiplier();
				}
				return -1;
			}
		}), ModBlocks.APOTHECARY_CAULDRON.get());
	}
}
