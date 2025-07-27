package reliquary.item.block;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import reliquary.item.ICreativeTabItemGenerator;

import java.util.List;
import java.util.function.Consumer;

public class BlockItemBase extends BlockItem implements ICreativeTabItemGenerator {

	public BlockItemBase(Block block) {
		this(block, new Properties());
	}

	@Override
	public void addCreativeTabItems(Consumer<ItemStack> itemConsumer) {
		if (getBlock() instanceof ICreativeTabItemGenerator creativeTabItemGenerator) {
			creativeTabItemGenerator.addCreativeTabItems(itemConsumer);
		}
	}

	public BlockItemBase(Block block, Properties builder) {
		super(block, builder);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY));
	}
}
