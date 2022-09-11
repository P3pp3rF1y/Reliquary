package reliquary.items;

import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import reliquary.Reliquary;
import reliquary.reference.Reference;
import reliquary.util.LanguageHelper;

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
		super(properties.tab(Reliquary.ITEM_GROUP));
		this.isDisabled = isDisabled;
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		if (Boolean.TRUE.equals(isDisabled.get())) {
			return;
		}
		super.fillItemCategory(group, items);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
		if (LanguageHelper.localizationExists(getDescriptionId() + ".tooltip")) {
			tooltip.add(Component.translatable(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY));
		}

		if (hasMoreInformation(stack)) {
			if (Screen.hasShiftDown()) {
				List<Component> detailTooltip = Lists.newArrayList();
				addMoreInformation(stack, world, detailTooltip);
				if (!detailTooltip.isEmpty()) {
					tooltip.add(Component.literal(""));
					tooltip.addAll(detailTooltip);
				}
			} else {
				tooltip.add(Component.literal(""));
				tooltip.add(Component.translatable("tooltip." + Reference.MOD_ID + ".hold_for_more_info",
						Component.translatable("tooltip." + Reference.MOD_ID + ".shift").withStyle(ChatFormatting.AQUA)
				).withStyle(ChatFormatting.GRAY));
			}
		}
	}

	@SuppressWarnings("squid:S1172") //parameter used in overrides
	protected boolean hasMoreInformation(ItemStack stack) {
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack stack, @Nullable Level world, List<Component> tooltip) {
		//overriden in child classes
	}

	@Override
	public Component getName(ItemStack stack) {
		return Component.literal(LanguageHelper.getLocalization(getDescriptionId(stack)));
	}
}

