package reliquary.item.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.block.Block;
import reliquary.item.ICreativeTabItemGenerator;

import java.util.List;
import java.util.function.Consumer;

public class BlockItemBase extends BlockItem implements ICreativeTabItemGenerator {
	public BlockItemBase(Block block, Properties properties) {
		super(block, properties.overrideDescription(block.getDescriptionId())
				.component(DataComponents.LORE, new ItemLore(List.of(
						Component.translatable(block.getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY)))));
	}

	@Override
	public void addCreativeTabItems(Consumer<ItemStack> itemConsumer) {
		if (getBlock() instanceof ICreativeTabItemGenerator creativeTabItemGenerator) {
			creativeTabItemGenerator.addCreativeTabItems(itemConsumer);
		}
	}
}
