package xreliquary.util.potions;

import com.google.common.collect.Lists;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import xreliquary.items.ItemPotionEssence;
import xreliquary.reference.Settings;

import java.util.List;
import java.util.Map;

/**
 * Created by Xeno on 11/8/2014.
 */
public class XRPotionHelper {

	public static boolean isItemEssence(ItemStack ist) {
		// essence not quite a thing just yet.
		return ist.getItem() instanceof ItemPotionEssence;
	}

	public static boolean isItemIngredient(ItemStack ist) {
		for(PotionIngredient ingredient : Settings.Potions.potionMap) {
			if(ingredient.item.getItem().getRegistryName().equals(ist.getItem().getRegistryName()) && ingredient.item.getMetadata() == ist.getMetadata()) {
				return true;
			}
		}
		return false;
	}

	public static int getPotionIdByName(String name) {
		return Potion.getIdFromPotion(Potion.getPotionFromResourceLocation(name)); //TODO verify this works
	}

	public static PotionIngredient getIngredient(ItemStack ist) {
		if(ist.getItem() instanceof ItemPotionEssence) {
			return new PotionEssence(ist.getTagCompound());
		}
		for(PotionIngredient ingredient : Settings.Potions.potionMap) {
			if(ingredient.item.getItem().getRegistryName().equals(ist.getItem().getRegistryName()) && ingredient.item.getMetadata() == ist.getMetadata())
				return ingredient;
		}
		return null;
	}

	private static Potion[] nonAugmentableEffects = new Potion[] {MobEffects.blindness, MobEffects.confusion, MobEffects.invisibility, MobEffects.nightVision, MobEffects.waterBreathing};

	public static boolean isAugmentablePotionEffect(PotionEffect effect) {
		for(int i = 0; i < nonAugmentableEffects.length; i++) {
			if(nonAugmentableEffects[i] == effect.getPotion())
				return false;
		}

		return true;
	}

	public static void addPotionInfo(PotionEssence essence, List list) {
		addPotionInfo(essence, list, true);
	}

	public static void addPotionInfo(PotionEssence essence, List list, boolean addEffectDescription) {
		if(essence.getEffects().size() > 0) {
			List<Tuple<String, AttributeModifier>> list1 = Lists.<Tuple<String, AttributeModifier>>newArrayList();
			for(PotionEffect potioneffect : essence.getEffects()) {
				String s1 = I18n.translateToLocal(potioneffect.getEffectName()).trim();
				Potion potion = potioneffect.getPotion();
				Map<IAttribute, AttributeModifier> map = potion.getAttributeModifierMap();

				if(!map.isEmpty()) {
					for(Map.Entry<IAttribute, AttributeModifier> entry : map.entrySet()) {
						AttributeModifier attributemodifier = (AttributeModifier) entry.getValue();
						AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), potion.getAttributeModifierAmount(potioneffect.getAmplifier(), attributemodifier), attributemodifier.getOperation());
						list1.add(new Tuple(((IAttribute) entry.getKey()).getAttributeUnlocalizedName(), attributemodifier1));
					}
				}

				if(potioneffect.getAmplifier() > 0) {
					s1 = s1 + " " + I18n.translateToLocal("potion.potency." + potioneffect.getAmplifier()).trim();
				}

				if(potioneffect.getDuration() > 20) {
					s1 = s1 + " (" + Potion.getPotionDurationString(potioneffect, 1.0F) + ")";
				}

				if(potion.isBadEffect()) {
					list.add(TextFormatting.RED + s1);
				} else {
					list.add(TextFormatting.BLUE + s1);
				}
			}

			if(!list1.isEmpty()) {
				list.add("");
				list.add(TextFormatting.DARK_PURPLE + I18n.translateToLocal("potion.whenDrank"));

				for(Tuple<String, AttributeModifier> tuple : list1) {
					AttributeModifier attributemodifier2 = (AttributeModifier) tuple.getSecond();
					double d0 = attributemodifier2.getAmount();
					double d1;

					if(attributemodifier2.getOperation() != 1 && attributemodifier2.getOperation() != 2) {
						d1 = attributemodifier2.getAmount();
					} else {
						d1 = attributemodifier2.getAmount() * 100.0D;
					}

					if(d0 > 0.0D) {
						list.add(TextFormatting.BLUE + I18n.translateToLocalFormatted("attribute.modifier.plus." + attributemodifier2.getOperation(), new Object[] {ItemStack.DECIMALFORMAT.format(d1), I18n.translateToLocal("attribute.name." + (String) tuple.getFirst())}));
					} else if(d0 < 0.0D) {
						d1 = d1 * -1.0D;
						list.add(TextFormatting.RED + I18n.translateToLocalFormatted("attribute.modifier.take." + attributemodifier2.getOperation(), new Object[] {ItemStack.DECIMALFORMAT.format(d1), I18n.translateToLocal("attribute.name." + (String) tuple.getFirst())}));
					}
				}
			}
		}
	}
}
