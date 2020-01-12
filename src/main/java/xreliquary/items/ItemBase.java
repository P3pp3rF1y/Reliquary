package xreliquary.items;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xreliquary.Reliquary;
import xreliquary.reference.Reference;
import xreliquary.util.LanguageHelper;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBase extends Item {
	public ItemBase(String registryName, Properties properties) {
		super(properties.group(Reliquary.ITEM_GROUP));
		setRegistryName(Reference.MOD_ID, registryName);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		if (LanguageHelper.localizationExists(getRegistryName() + ".tooltip")) {
			LanguageHelper.formatTooltip(getRegistryName() + ".tooltip", tooltip);
		}

		if (Screen.hasShiftDown()) {
			List<ITextComponent> detailTooltip = Lists.newArrayList();
			addMoreInformation(stack, world, detailTooltip);
			if (!detailTooltip.isEmpty()) {
				tooltip.add(new StringTextComponent(""));
				tooltip.addAll(detailTooltip);
			}
		} else {
			tooltip.add(new TranslationTextComponent(TextFormatting.WHITE + TextFormatting.ITALIC.toString() + LanguageHelper.getLocalization("tooltip.shift_for_more_info") + TextFormatting.RESET));
		}
	}

	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip) {
		//overriden in child classes
	}
}

