package xreliquary.items.block;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xreliquary.Reliquary;
import xreliquary.util.LanguageHelper;

import javax.annotation.Nullable;
import java.util.List;

public class BlockItemBase extends BlockItem {

	public BlockItemBase(Block block) {
		this(block, new Properties());
	}

	public BlockItemBase(Block block, Properties builder) {
		super(block, builder.tab(Reliquary.ITEM_GROUP));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
		LanguageHelper.formatTooltip(getDescriptionId() + ".tooltip", null, tooltip);
	}
}
