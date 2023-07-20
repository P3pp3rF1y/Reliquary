package reliquary.util;

import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import reliquary.items.EnderStaffItem;
import reliquary.reference.Reference;
import reliquary.util.potions.XRPotionHelper;

import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

public class TooltipBuilder {
	private final List<Component> tooltip;

	public static TooltipBuilder of(List<Component> tooltip) {
		return new TooltipBuilder(tooltip);
	}

	private TooltipBuilder(List<Component> tooltip) {
		this.tooltip = tooltip;
	}

	public void potionEffects(List<MobEffectInstance> effects) {
		if (!effects.isEmpty()) {
			List<Tuple<String, AttributeModifier>> attributeModifiers = Lists.newArrayList();
			for (MobEffectInstance potioneffect : effects) {
				String s1 = I18n.get(potioneffect.getDescriptionId()).trim();
				MobEffect potion = potioneffect.getEffect();
				Map<Attribute, AttributeModifier> map = potion.getAttributeModifiers();

				if (!map.isEmpty()) {
					for (Map.Entry<Attribute, AttributeModifier> entry : map.entrySet()) {
						AttributeModifier attributemodifier = entry.getValue();
						AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), potion.getAttributeModifierValue(potioneffect.getAmplifier(), attributemodifier), attributemodifier.getOperation());
						attributeModifiers.add(new Tuple<>(entry.getKey().getDescriptionId(), attributemodifier1));
					}
				}

				if (potioneffect.getAmplifier() > 0) {
					s1 = s1 + " " + I18n.get("potion.potency." + potioneffect.getAmplifier()).trim();
				}

				if (potioneffect.getDuration() > 20) {
					s1 = s1 + " (" + MobEffectUtil.formatDuration(potioneffect, 1.0F) + ")";
				}

				if (potion.isBeneficial()) {
					tooltip.add(Component.literal(ChatFormatting.BLUE + s1));
				} else {
					tooltip.add(Component.literal(ChatFormatting.RED + s1));
				}
			}

			addAttributeModifierTooltip(tooltip, attributeModifiers);
		}
	}

	private static void addAttributeModifierTooltip(List<Component> list, List<Tuple<String, AttributeModifier>> list1) {
		if (!list1.isEmpty()) {
			list.add(Component.literal(""));
			list.add(Component.literal(ChatFormatting.DARK_PURPLE + I18n.get("potion.whenDrank")));

			for (Tuple<String, AttributeModifier> tuple : list1) {
				AttributeModifier attributemodifier2 = tuple.getB();
				double d0 = attributemodifier2.getAmount();
				double d1;

				if (attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
					d1 = attributemodifier2.getAmount();
				} else {
					d1 = attributemodifier2.getAmount() * 100.0D;
				}

				if (d0 > 0.0D) {
					list.add((Component.translatable("attribute.modifier.plus." + attributemodifier2.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(tuple.getA()))).withStyle(ChatFormatting.BLUE));
				} else if (d0 < 0.0D) {
					d1 = d1 * -1.0D;
					list.add((Component.translatable("attribute.modifier.take." + attributemodifier2.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(tuple.getA()))).withStyle(ChatFormatting.RED));
				}
			}
		}
	}

	public void potionEffects(ItemStack stack) {
		potionEffects(XRPotionHelper.getPotionEffectsFromStack(stack));
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
			tooltip.add(Component.translatable("tooltip." + Reference.MOD_ID + ".hold_for_more_info",
					Component.translatable("tooltip." + Reference.MOD_ID + ".shift").withStyle(ChatFormatting.AQUA)
			).withStyle(ChatFormatting.DARK_GRAY));
		}
		return this;
	}

	public TooltipBuilder absorb() {
		tooltip.add(Component.translatable("tooltip." + Reference.MOD_ID + ".absorb").withStyle(ChatFormatting.DARK_GRAY));
		return this;
	}

	public TooltipBuilder absorbActive(String itemName) {
		return absorbActive(Component.literal(itemName).withStyle(ChatFormatting.DARK_AQUA));
	}

	public TooltipBuilder absorbActive(Component thingName) {
		tooltip.add(Component.translatable("tooltip." + Reference.MOD_ID + ".absorb_active", thingName).withStyle(ChatFormatting.DARK_GRAY));
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
		boolean hasComponentArg = false;
		for (Object arg : args) {
			if (arg instanceof Component) {
				hasComponentArg = true;
				break;
			}
		}
		if (hasComponentArg) {
			Object[] newArgs = new Object[args.length];
			for (int i = 0; i < args.length; i++) {
				Object arg = args[i];
				if (arg instanceof Component component) {
					newArgs[i] = component.getString();
				} else {
					newArgs[i] = arg;
				}
			}
			args = newArgs;
		}

		String text = I18n.get(langKey, args);

		String[] lines = text.split("\n");
		for (String line : lines) {
			tooltip.add(applyStyle.apply(Component.literal(line)));
		}
	}
}
