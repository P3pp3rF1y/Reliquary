package reliquary.item.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import reliquary.init.ModBlocks;
import reliquary.item.ICreativeTabItemGenerator;

import java.util.function.Consumer;

public class InterdictionTorchItem extends StandingAndWallBlockItem implements ICreativeTabItemGenerator {
	public InterdictionTorchItem(Properties properties) {
		super(ModBlocks.INTERDICTION_TORCH.get(), ModBlocks.WALL_INTERDICTION_TORCH.get(), Direction.DOWN, properties.overrideDescription(ModBlocks.INTERDICTION_TORCH.get().getDescriptionId()));
	}

	@Override
	public void addCreativeTabItems(Consumer<ItemStack> itemConsumer) {
		if (getBlock() instanceof ICreativeTabItemGenerator creativeTabItemGenerator) {
			creativeTabItemGenerator.addCreativeTabItems(itemConsumer);
		}
	}
}
