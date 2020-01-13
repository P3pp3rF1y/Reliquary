package xreliquary.items.block;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xreliquary.Reliquary;
import xreliquary.util.LanguageHelper;

import javax.annotation.Nullable;
import java.util.List;

/**
 * ItemBlockBase, a helper class for item blocks. Handles language names and language
 * parsing (see LanguageHelper).
 * <p>
 * In most ways, completely similar to ItemBase.
 *
 * @author TheMike
 */
public class BlockItemBase extends BlockItem {

	public BlockItemBase(Block block, Properties builder) {
		super(block, builder.group(Reliquary.ITEM_GROUP));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		LanguageHelper.formatTooltip(getTranslationKey() + ".tooltip", null, tooltip);
	}
}
