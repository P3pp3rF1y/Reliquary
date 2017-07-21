package xreliquary.util.potions;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.items.ItemPotionEssence;
import xreliquary.reference.Settings;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XRPotionHelper {

	private static final String EFFECTS_NBT_TAG = "effects";
	private static int MAX_DURATION = 36000;
	private static int MAX_AMPLIFIER = 4;

	public static boolean isItemEssence(ItemStack ist) {
		// essence not quite a thing just yet.
		return ist.getItem() instanceof ItemPotionEssence;
	}

	public static boolean isItemIngredient(ItemStack ist) {
		for(PotionIngredient ingredient : Settings.Potions.potionMap) {
			//noinspection ConstantConditions
			if(ingredient.item.getItem().getRegistryName().equals(ist.getItem().getRegistryName()) && ingredient.item.getMetadata() == ist.getMetadata()) {
				return true;
			}
		}
		return false;
	}

	public static PotionIngredient getIngredient(ItemStack ist) {
		if(ist.getItem() instanceof ItemPotionEssence) {
			return new PotionIngredient(ist, XRPotionHelper.getPotionEffectsFromStack(ist));
		}
		for(PotionIngredient ingredient : Settings.Potions.potionMap) {
			//noinspection ConstantConditions
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

	private static boolean isAugmentablePotionEffect(PotionEffect effect) {
		for(Potion nonAugmentableEffect : nonAugmentableEffects) {
			if(nonAugmentableEffect == effect.getPotion())
				return false;
		}

		return true;
	}

	@SideOnly(Side.CLIENT)
	private static void addPotionTooltip(List<PotionEffect> effects, List<String> list) {
		addPotionTooltip(effects, list, true, true);
	}

	@SideOnly(Side.CLIENT)
	public static void addPotionTooltip(List<PotionEffect> effects, List<String> list, boolean displayWhenDrankInfo, boolean addFormatting) {
		if(!effects.isEmpty()) {
			List<Tuple<String, AttributeModifier>> list1 = Lists.newArrayList();
			for(PotionEffect potioneffect : effects) {
				//noinspection deprecation
				String s1 = I18n.translateToLocal(potioneffect.getEffectName()).trim();
				Potion potion = potioneffect.getPotion();
				Map<IAttribute, AttributeModifier> map = potion.getAttributeModifierMap();

				if(!map.isEmpty()) {
					for(Map.Entry<IAttribute, AttributeModifier> entry : map.entrySet()) {
						AttributeModifier attributemodifier = entry.getValue();
						AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), potion.getAttributeModifierAmount(potioneffect.getAmplifier(), attributemodifier), attributemodifier.getOperation());
						list1.add(new Tuple<>(entry.getKey().getName(), attributemodifier1));
					}
				}

				if(potioneffect.getAmplifier() > 0) {
					//noinspection deprecation
					s1 = s1 + " " + I18n.translateToLocal("potion.potency." + potioneffect.getAmplifier()).trim();
				}

				if(potioneffect.getDuration() > 20) {
					s1 = s1 + " (" + Potion.getPotionDurationString(potioneffect, 1.0F) + ")";
				}

				if(potion.isBadEffect()) {
					list.add((addFormatting ? TextFormatting.RED.toString() : "") + s1);
				} else {
					list.add((addFormatting ? TextFormatting.BLUE.toString() : "") + s1);
				}
			}

			if(displayWhenDrankInfo && !list1.isEmpty()) {
				list.add("");
				//noinspection deprecation
				list.add((addFormatting ? TextFormatting.DARK_PURPLE.toString() : "") + I18n.translateToLocal("potion.whenDrank"));

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
						//noinspection deprecation
						list.add((addFormatting ? TextFormatting.BLUE.toString() : "") + I18n.translateToLocalFormatted("attribute.modifier.plus." + attributemodifier2.getOperation(), ItemStack.DECIMALFORMAT.format(d1), I18n.translateToLocal("attribute.name." + tuple.getFirst())));
					} else if(d0 < 0.0D) {
						d1 = d1 * -1.0D;
						//noinspection deprecation
						list.add((addFormatting ? TextFormatting.RED.toString() : "") + I18n.translateToLocalFormatted("attribute.modifier.take." + attributemodifier2.getOperation(), ItemStack.DECIMALFORMAT.format(d1), I18n.translateToLocal("attribute.name." + tuple.getFirst())));
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static void addPotionTooltip(ItemStack stack, List<String> list) {
		addPotionTooltip(getPotionEffectsFromStack(stack), list);
	}

	public static void addPotionEffectsToCompoundTag(@Nonnull NBTTagCompound tag, Collection<PotionEffect> effects) {
		if (effects == null || effects.isEmpty())
			return;

		NBTTagList effectList = tag.getTagList("effects", 10);
		for(PotionEffect object : effects) {
			NBTTagCompound effect = new NBTTagCompound();
			//noinspection ConstantConditions
			effect.setString("name", object.getPotion().getRegistryName().toString());
			effect.setInteger("duration", object.getPotion().isInstant() ? 1 : object.getDuration());
			effect.setInteger("potency", object.getAmplifier());
			effectList.appendTag(effect);
		}
		tag.setTag("effects", effectList);
	}

	public static List<PotionEffect> getPotionEffectsFromCompoundTag(NBTTagCompound tag) {
		if(tag == null || !tag.hasKey(EFFECTS_NBT_TAG))
			return Lists.newArrayList();

		NBTTagList effectList = tag.getTagList(EFFECTS_NBT_TAG, 10);

		List<PotionEffect> ret = Lists.newArrayList();
		for (NBTBase effectTag : effectList) {
			NBTTagCompound effect = (NBTTagCompound) effectTag;

			String registryName = effect.getString("name");
			int duration = effect.getInteger("duration");
			int potency = effect.getInteger("potency");
			//noinspection ConstantConditions
			ret.add(new PotionEffect(Potion.getPotionFromResourceLocation(registryName), duration, potency));
		}

		return ret;
	}

	public static List<PotionEffect> getPotionEffectsFromStack(ItemStack stack) {
		return getPotionEffectsFromCompoundTag(stack.getTagCompound());
	}

	public static void addPotionEffectsToStack(ItemStack itemstack, List<PotionEffect> effects) {
		NBTTagCompound tag = Objects.firstNonNull(itemstack.getTagCompound(), new NBTTagCompound());
		addPotionEffectsToCompoundTag(tag, effects);
		itemstack.setTagCompound(tag);
	}

	@SuppressWarnings("SameParameterValue")
	public static List<PotionEffect> changePotionEffectsDuration(Collection<PotionEffect> effects, float factor) {
		List<PotionEffect> ret = Lists.newArrayList();

		for (PotionEffect effect : effects) {
			int newDuration = (int) (effect.getPotion().isInstant() ? 1 : effect.getDuration() * factor);
			ret.add(new PotionEffect(effect.getPotion(), newDuration, effect.getAmplifier(), effect.getIsAmbient(), effect.doesShowParticles()));
		}

		return ret;
	}

	public static List<PotionEffect> augmentPotionEffects(List<PotionEffect> effects, int redstoneCount, int glowstoneCount) {
		return addRedstone(addGlowstone(effects, glowstoneCount), redstoneCount);
	}

	private static List<PotionEffect> addRedstone(List<PotionEffect> effects, int redstoneCount) {
		if (redstoneCount <= 0)
			return effects;

		List<PotionEffect> newEffects = new ArrayList<>();

		int effectCnt = effects.size();
		double multiplier = 1.0;

		for(int redstoneLevel = 1; redstoneLevel <= redstoneCount; redstoneLevel++) {
			multiplier *= (((double) (8 + effectCnt)) / ((double) (3 + effectCnt)) - (1.0 / ((double) (3 + effectCnt)) * (((double) redstoneLevel) - 1.0)));
		}

		for(PotionEffect effect : effects) {
			int newDuration = new Double((double) effect.getDuration() * multiplier).intValue();
			newDuration = Math.min(newDuration, MAX_DURATION * 2);

			PotionEffect newEffect = new PotionEffect(effect.getPotion(), newDuration, effect.getAmplifier(), effect.getIsAmbient(), effect.doesShowParticles());
			newEffects.add(newEffect);
		}

		return newEffects;
	}

	private static List<PotionEffect> addGlowstone(List<PotionEffect> effects, int glowstoneCount) {
		if (glowstoneCount <= 0)
			return effects;

		List<PotionEffect> newEffects = new ArrayList<>();

		int effectCnt = effects.size();
		double multiplier = 1.0;

		for(int glowstoneLevel = 1; glowstoneLevel <= glowstoneCount; glowstoneLevel++) {
			multiplier *= ((((double) (11 + effectCnt)) / ((double) (6 + effectCnt)) - (1.0 / ((double) (6 + effectCnt)) * ((double) glowstoneLevel)) - 1.0));
		}

		for(PotionEffect effect : effects) {
			int newAmplifier = effect.getAmplifier();

			if(XRPotionHelper.isAugmentablePotionEffect(effect))
				newAmplifier = Math.min(effect.getAmplifier() + glowstoneCount, MAX_AMPLIFIER + 1);

			PotionEffect newEffect = new PotionEffect(effect.getPotion(), new Double(effect.getDuration() * multiplier).intValue(), newAmplifier, effect.getIsAmbient(), effect.doesShowParticles());
			newEffects.add(newEffect);
		}
		return newEffects;
	}

	public static List<PotionEffect> combineIngredients(PotionIngredient... ingredients) {
		return combineIngredients(Arrays.asList(ingredients));
	}

	//this handles the actual combining of two or more ingredients, including other essences.
	public static List<PotionEffect> combineIngredients(Collection<PotionIngredient> ingredients) {

		//helper list to store what we have, altogether
		Map<ResourceLocation, List<PotionEffect>> potionEffectCounterList = new HashMap<>();

		//actual list to store what we have two or more of, these are the actual final effects
		List<ResourceLocation> potionEffectList = new ArrayList<>();

		//add each effect to the counter list. if it appears twice, add it to the potionEffectList too.
		for(PotionIngredient ingredient : ingredients) {
			for(PotionEffect effect : ingredient.getEffects()) {
				if(potionEffectCounterList.keySet().contains(effect.getPotion().getRegistryName())) {
					if(!potionEffectList.contains(effect.getPotion().getRegistryName()))
						potionEffectList.add(effect.getPotion().getRegistryName());
					potionEffectCounterList.get(effect.getPotion().getRegistryName()).add(effect);
				} else {
					ArrayList<PotionEffect> effects = new ArrayList<>();
					effects.add(effect);
					potionEffectCounterList.put(effect.getPotion().getRegistryName(), effects);
				}
			}
		}

		List<PotionEffect> combinedEffects = Lists.newArrayList();

		//iterate through common effects
		for(ResourceLocation potionName : potionEffectList) {
			List<PotionEffect> effects = potionEffectCounterList.get(potionName);

			int duration = getCombinedDuration(effects);
			int amplifier = getCombinedAmplifier(effects);

			if(duration == 0)
				continue;

			Potion potion = Potion.REGISTRY.getObject(potionName);
			if(potion != null) {
				combinedEffects.add(new PotionEffect(potion, duration, amplifier));
			}
		}
		combinedEffects.sort(new EffectComparator());

		return combinedEffects;
	}

	private static int getCombinedAmplifier(List<PotionEffect> effects) {
		int amplifier = 0;
		for(PotionEffect effect : effects) {
			amplifier += effect.getAmplifier();
		}

		return Math.min(amplifier, XRPotionHelper.MAX_AMPLIFIER);
	}

	private static int getCombinedDuration(List<PotionEffect> effects) {
		int count = 0;
		int duration = 0;
		for(PotionEffect effect : effects) {
			if(effect.getPotion().isInstant())
				return 1;

			count++;

			duration += effect.getDuration();
		}

		duration = (int) (duration / 1.2);
		if(count == 3)
			duration = (int) (duration / 1.1);

		return Math.min(duration, XRPotionHelper.MAX_DURATION);
	}

	public static void applyEffectsToEntity(Collection<PotionEffect> effects, Entity source, Entity indirectSource, EntityLivingBase entitylivingbase) {
		applyEffectsToEntity(effects, source, indirectSource, entitylivingbase, 1.0);
	}

	public static void applyEffectsToEntity(Collection<PotionEffect> effects, Entity source, Entity indirectSource, EntityLivingBase entitylivingbase, double amplifier) {
		for(PotionEffect potioneffect : effects) {
			if(potioneffect.getPotion().isInstant()) {
				potioneffect.getPotion().affectEntity(source, indirectSource, entitylivingbase, potioneffect.getAmplifier(), amplifier);
			} else {
				int j = (int) (amplifier * (double) potioneffect.getDuration() + 0.5D);

				if(j > 20) {
					entitylivingbase.addPotionEffect(new PotionEffect(potioneffect.getPotion(), j, potioneffect.getAmplifier(), false, false));
				}
			}
		}
	}
}
