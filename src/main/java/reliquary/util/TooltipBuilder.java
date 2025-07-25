package reliquary.util;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import reliquary.Reliquary;
import reliquary.item.EnderStaffItem;

import java.util.List;
import java.util.function.UnaryOperator;

public class TooltipBuilder {
	private static Item.TooltipContext context;
	private final List<Component> tooltip;

	public static TooltipBuilder of(List<Component> tooltip, Item.TooltipContext context) {
		TooltipBuilder.context = context;
		return new TooltipBuilder(tooltip);
	}

	private TooltipBuilder(List<Component> tooltip) {
		this.tooltip = tooltip;
	}

	public void potionEffects(PotionContents potionContents) {
		potionContents.addPotionTooltip(tooltip::add, 1, context.tickRate());
	}

	public void potionEffects(ItemStack stack) {
		potionEffects(stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY));
	}

	public TooltipBuilder itemTooltip(Item item) {
		String langName = item.getDescriptionId() + ".tooltip";
		if (Language.getInstance().has(langName)) {
			addTooltipLines(c -> c.withStyle(ChatFormatting.GRAY), item.getDescriptionId() + ".tooltip");
		}
		return this;
	}

	public TooltipBuilder charge(Item item, String langSuffix, int charge, int chargeLimit) {
		tooltip.add(Component.translatable(
						item.getDescriptionId() + langSuffix,
						Component.literal(String.valueOf(charge)).withStyle(ChatFormatting.WHITE),
						Component.literal(String.valueOf(chargeLimit)).withStyle(ChatFormatting.BLUE))
				.withStyle(ChatFormatting.GREEN));
		return this;
	}

	public TooltipBuilder data(Item item, String langSuffix, Object... args) {
		return data(item.getDescriptionId() + langSuffix, args);
	}

	public TooltipBuilder data(String langKey, Object... args) {
		Component[] components;
		if (args.length > 0) {
			components = new Component[args.length];
			for (int i = 0, argsLength = args.length; i < argsLength; i++) {
				Object arg = args[i];
				if (arg instanceof Component argComponent) {
					components[i] = argComponent;
				} else {
					components[i] = Component.literal(String.valueOf(arg)).withStyle(ChatFormatting.WHITE);
				}
			}
		} else {
			components = new Component[0];
		}

		tooltip.add(Component.translatable(langKey, components).withStyle(ChatFormatting.GREEN));
		return this;
	}

	public TooltipBuilder charge(Item item, String langSuffix, String chargeName, int charge) {
		tooltip.add(Component.translatable(
						item.getDescriptionId() + langSuffix,
						Component.literal(chargeName).withStyle(ChatFormatting.WHITE),
						Component.literal(String.valueOf(charge)).withStyle(ChatFormatting.WHITE))
				.withStyle(ChatFormatting.GREEN));
		return this;
	}

	public TooltipBuilder charge(Item item, String langSuffix, int charge) {
		tooltip.add(Component.translatable(
						item.getDescriptionId() + langSuffix,
						Component.literal(String.valueOf(charge)).withStyle(ChatFormatting.WHITE))
				.withStyle(ChatFormatting.GREEN));
		return this;
	}

	public TooltipBuilder showMoreInfo() {
		if (!Screen.hasShiftDown()) {
			tooltip.add(Component.translatable("tooltip." + Reliquary.MOD_ID + ".hold_for_more_info",
					Component.translatable("tooltip." + Reliquary.MOD_ID + ".shift").withStyle(ChatFormatting.AQUA)
			).withStyle(ChatFormatting.DARK_GRAY));
		}
		return this;
	}

	public TooltipBuilder absorb() {
		tooltip.add(Component.translatable("tooltip." + Reliquary.MOD_ID + ".absorb").withStyle(ChatFormatting.DARK_GRAY));
		return this;
	}

	public TooltipBuilder absorbActive(String itemName) {
		return absorbActive(Component.literal(itemName).withStyle(ChatFormatting.DARK_AQUA));
	}

	public TooltipBuilder absorbActive(Component thingName) {
		tooltip.add(Component.translatable("tooltip." + Reliquary.MOD_ID + ".absorb_active", thingName).withStyle(ChatFormatting.DARK_GRAY));
		return this;
	}

	public TooltipBuilder description(String langKey, Object... args) {
		addTooltipLines(c -> c.withStyle(ChatFormatting.DARK_GRAY), langKey, args);
		return this;
	}

	public TooltipBuilder description(Item item, String langSuffix, Object... args) {
		return description(item.getDescriptionId() + langSuffix, args);
	}

	public TooltipBuilder warning(EnderStaffItem enderStaffItem, String langSuffix) {
		tooltip.add(Component.translatable(enderStaffItem.getDescriptionId() + langSuffix).withStyle(ChatFormatting.RED));
		return this;
	}

	private void addTooltipLines(UnaryOperator<MutableComponent> applyStyle, String langKey, Object... args) {
		String text = Language.getInstance().getOrDefault(langKey);
		String[] lines = text.split("\n");
		for (String line : lines) {
			tooltip.add(applyStyle.apply(Component.translatableWithFallback("", line, args)));
		}
	}
}
