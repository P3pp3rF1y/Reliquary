package xreliquary.util.potions;

import com.google.common.collect.Lists;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import xreliquary.items.ItemPotionEssence;
import xreliquary.reference.Settings;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

	private static Potion[] nonAugmentableEffects = new Potion[] {MobEffects.BLINDNESS,
			MobEffects.NAUSEA,
			MobEffects.INVISIBILITY,
			MobEffects.NIGHT_VISION,
			MobEffects.WATER_BREATHING};

	public static boolean isAugmentablePotionEffect(PotionEffect effect) {
		for(Potion nonAugmentableEffect : nonAugmentableEffects) {
			if(nonAugmentableEffect == effect.getPotion())
				return false;
		}

		return true;
	}

	public static void addPotionInfo(PotionEssence essence, List<String> list) {
		addPotionInfo(essence, list, true);
	}

	public static void addPotionInfo(PotionEssence essence, List<String> list, boolean addEffectDescription) {
		if(essence.getEffects().size() > 0) {
			List<Tuple<String, AttributeModifier>> list1 = Lists.newArrayList();
			for(PotionEffect potioneffect : essence.getEffects()) {
				String s1 = I18n.translateToLocal(potioneffect.getEffectName()).trim();
				Potion potion = potioneffect.getPotion();
				Map<IAttribute, AttributeModifier> map = potion.getAttributeModifierMap();

				if(!map.isEmpty()) {
					for(Map.Entry<IAttribute, AttributeModifier> entry : map.entrySet()) {
						AttributeModifier attributemodifier = entry.getValue();
						AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), potion.getAttributeModifierAmount(potioneffect.getAmplifier(), attributemodifier), attributemodifier.getOperation());
						list1.add(new Tuple<>(entry.getKey().getAttributeUnlocalizedName(), attributemodifier1));
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
					AttributeModifier attributemodifier2 = tuple.getSecond();
					double d0 = attributemodifier2.getAmount();
					double d1;

					if(attributemodifier2.getOperation() != 1 && attributemodifier2.getOperation() != 2) {
						d1 = attributemodifier2.getAmount();
					} else {
						d1 = attributemodifier2.getAmount() * 100.0D;
					}

					if(d0 > 0.0D) {
						list.add(TextFormatting.BLUE + I18n.translateToLocalFormatted("attribute.modifier.plus." + attributemodifier2.getOperation(), ItemStack.DECIMALFORMAT.format(d1), I18n.translateToLocal("attribute.name." + tuple.getFirst())));
					} else if(d0 < 0.0D) {
						d1 = d1 * -1.0D;
						list.add(TextFormatting.RED + I18n.translateToLocalFormatted("attribute.modifier.take." + attributemodifier2.getOperation(), ItemStack.DECIMALFORMAT.format(d1), I18n.translateToLocal("attribute.name." + tuple.getFirst())));
					}
				}
			}
		}
	}

	public static List<PotionEffect> changeDuration(List<PotionEffect> effects, float factor) {
		return effects.stream().map(effect -> new PotionEffect(effect.getPotion(), (int) (effect.getPotion().isInstant() ? 1 : effect.getDuration() * factor), effect.getAmplifier(), effect.getIsAmbient(), effect.doesShowParticles())).collect(Collectors.toList());
	}

	public static void appendEffectsToNBT(NBTTagCompound compound, List<PotionEffect> potionEffects) {
		if (potionEffects == null || potionEffects.size() == 0)
			return;

		NBTTagList nbttaglist = compound.getTagList("CustomPotionEffects", 9);

		for(PotionEffect potioneffect : potionEffects) {
			nbttaglist.appendTag(potioneffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
		}

		compound.setTag("CustomPotionEffects", nbttaglist);
	}
}
