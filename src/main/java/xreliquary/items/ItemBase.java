package xreliquary.items;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
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
import java.util.function.Supplier;

public class ItemBase extends Item {
	private final Supplier<Boolean> isDisabled;

	public ItemBase() {
		this(new Properties(), () -> false);
	}

	public ItemBase(Supplier<Boolean> isDisabled) {
		this(new Properties(), isDisabled);
	}
	public ItemBase(Properties properties) {
		this(properties, () -> false);
	}
	public ItemBase(Properties properties, Supplier<Boolean> isDisabled) {
		super(properties.group(Reliquary.ITEM_GROUP));
		this.isDisabled = isDisabled;
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if (Boolean.TRUE.equals(isDisabled.get())) {
			return;
		}
		super.fillItemGroup(group, items);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		if (LanguageHelper.localizationExists(getTranslationKey() + ".tooltip")) {
			LanguageHelper.formatTooltip(getTranslationKey() + ".tooltip", tooltip);
		}

		if (hasMoreInformation(stack)) {
			if (Screen.hasShiftDown()) {
				List<ITextComponent> detailTooltip = Lists.newArrayList();
				addMoreInformation(stack, world, detailTooltip);
				if (!detailTooltip.isEmpty()) {
					tooltip.add(new StringTextComponent(""));
					tooltip.addAll(detailTooltip);
				}
			} else {
				tooltip.add(new TranslationTextComponent("tooltip." + Reference.MOD_ID + ".shift_for_more_info").mergeStyle(TextFormatting.WHITE).mergeStyle(TextFormatting.ITALIC));
			}
		}
	}

	@SuppressWarnings("squid:S1172") //parameter used in overrides
	protected boolean hasMoreInformation(ItemStack stack) {
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip) {
		//overriden in child classes
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		return new StringTextComponent(LanguageHelper.getLocalization(getTranslationKey(stack)));
	}
}

