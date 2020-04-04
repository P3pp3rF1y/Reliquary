package xreliquary.util.potions;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import xreliquary.items.PotionEssenceItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class XRPotionHelper {
	private static final String EFFECTS_TAG = "effects";

	private XRPotionHelper() {}

	private static final String EFFECTS_NBT_TAG = EFFECTS_TAG;
	private static final int MAX_DURATION = 36000;
	private static final int MAX_AMPLIFIER = 4;

	public static boolean isItemEssence(ItemStack stack) {
		// essence not quite a thing just yet.
		return stack.getItem() instanceof PotionEssenceItem;
	}

	public static boolean isIngredient(ItemStack stack) {
		for (PotionIngredient ingredient : PotionMap.ingredients) {
			//noinspection ConstantConditions
			if (ingredient.getItem().getItem().getRegistryName().equals(stack.getItem().getRegistryName())) {
				return true;
			}
		}
		return false;
	}

	public static Optional<PotionIngredient> getIngredient(ItemStack stack) {
		if (stack.getItem() instanceof PotionEssenceItem) {
			return Optional.of(new PotionIngredient(stack, XRPotionHelper.getPotionEffectsFromStack(stack)));
		}
		for (PotionIngredient ingredient : PotionMap.ingredients) {
			//noinspection ConstantConditions
			if (ingredient.getItem().getItem().getRegistryName().equals(stack.getItem().getRegistryName())) {
				return Optional.of(ingredient);
			}
		}
		return Optional.empty();
	}

	private static Effect[] nonAugmentableEffects = new Effect[] {Effects.BLINDNESS,
			Effects.NAUSEA,
			Effects.INVISIBILITY,
			Effects.NIGHT_VISION,
			Effects.WATER_BREATHING};

	private static boolean isAugmentablePotionEffect(EffectInstance effect) {
		for (Effect nonAugmentableEffect : nonAugmentableEffects) {
			if (nonAugmentableEffect == effect.getPotion()) {
				return false;
			}
		}

		return true;
	}

	@OnlyIn(Dist.CLIENT)
	private static void addPotionTooltip(List<EffectInstance> effects, List<ITextComponent> list) {
		if (!effects.isEmpty()) {
			List<Tuple<String, AttributeModifier>> list1 = Lists.newArrayList();
			for (EffectInstance potioneffect : effects) {
				String s1 = I18n.format(potioneffect.getEffectName()).trim();
				Effect potion = potioneffect.getPotion();
				Map<IAttribute, AttributeModifier> map = potion.getAttributeModifierMap();

				if (!map.isEmpty()) {
					for (Map.Entry<IAttribute, AttributeModifier> entry : map.entrySet()) {
						AttributeModifier attributemodifier = entry.getValue();
						AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), potion.getAttributeModifierAmount(potioneffect.getAmplifier(), attributemodifier), attributemodifier.getOperation());
						list1.add(new Tuple<>(entry.getKey().getName(), attributemodifier1));
					}
				}

				if (potioneffect.getAmplifier() > 0) {
					s1 = s1 + " " + I18n.format("potion.potency." + potioneffect.getAmplifier()).trim();
				}

				if (potioneffect.getDuration() > 20) {
					s1 = s1 + " (" + EffectUtils.getPotionDurationString(potioneffect, 1.0F) + ")";
				}

				if (potion.isBeneficial()) {
					list.add(new StringTextComponent((TextFormatting.BLUE.toString()) + s1));
				} else {
					list.add(new StringTextComponent((TextFormatting.RED.toString()) + s1));
				}
			}

			if (!list1.isEmpty()) {
				list.add(new StringTextComponent(""));
				list.add(new StringTextComponent(TextFormatting.DARK_PURPLE.toString() + I18n.format("potion.whenDrank")));

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
						list.add((new TranslationTextComponent("attribute.modifier.plus." + attributemodifier2.getOperation().getId(), ItemStack.DECIMALFORMAT.format(d1), new TranslationTextComponent("attribute.name." + tuple.getA()))).applyTextStyle(TextFormatting.BLUE));
					} else if (d0 < 0.0D) {
						d1 = d1 * -1.0D;
						list.add((new TranslationTextComponent("attribute.modifier.take." + attributemodifier2.getOperation().getId(), ItemStack.DECIMALFORMAT.format(d1), new TranslationTextComponent("attribute.name." + tuple.getA()))).applyTextStyle(TextFormatting.RED));
					}
				}
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static void addPotionTooltip(ItemStack stack, List<ITextComponent> list) {
		addPotionTooltip(getPotionEffectsFromStack(stack), list);
	}

	public static void addPotionEffectsToCompoundTag(CompoundNBT tag, Collection<EffectInstance> effects) {
		if (effects.isEmpty()) {
			return;
		}

		ListNBT effectList = tag.getList(EFFECTS_TAG, 10);
		for (EffectInstance object : effects) {
			CompoundNBT effect = new CompoundNBT();
			//noinspection ConstantConditions
			effect.putString("name", object.getPotion().getRegistryName().toString());
			effect.putInt("duration", object.getPotion().isInstant() ? 1 : object.getDuration());
			effect.putInt("potency", object.getAmplifier());
			effectList.add(effect);
		}
		tag.put(EFFECTS_TAG, effectList);
	}

	public static List<EffectInstance> getPotionEffectsFromCompoundTag(CompoundNBT tag) {
		if (!tag.contains(EFFECTS_NBT_TAG)) {
			return Lists.newArrayList();
		}

		ListNBT effectList = tag.getList(EFFECTS_NBT_TAG, 10);

		List<EffectInstance> ret = Lists.newArrayList();
		for (INBT effectTag : effectList) {
			CompoundNBT effect = (CompoundNBT) effectTag;

			String registryName = effect.getString("name");
			int duration = effect.getInt("duration");
			int potency = effect.getInt("potency");
			//noinspection ConstantConditions
			ret.add(new EffectInstance(ForgeRegistries.POTIONS.getValue(new ResourceLocation(registryName)), duration, potency));
		}

		return ret;
	}

	public static List<EffectInstance> getPotionEffectsFromStack(ItemStack stack) {
		if (!stack.hasTag()) {
			return Collections.emptyList();
		}

		//noinspection ConstantConditions
		return getPotionEffectsFromCompoundTag(stack.getTag());
	}

	public static void addPotionEffectsToStack(ItemStack itemstack, List<EffectInstance> effects) {
		CompoundNBT tag = MoreObjects.firstNonNull(itemstack.getTag(), new CompoundNBT());
		addPotionEffectsToCompoundTag(tag, effects);
		itemstack.setTag(tag);
	}

	public static void cleanPotionEffects(ItemStack stack) {
		CompoundNBT tag = stack.getTag();

		if (tag == null) {
			return;
		}

		if (tag.contains(EFFECTS_TAG)) {
			tag.remove(EFFECTS_TAG);
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static List<EffectInstance> changePotionEffectsDuration(Collection<EffectInstance> effects, float factor) {
		List<EffectInstance> ret = Lists.newArrayList();

		for (EffectInstance effect : effects) {
			int newDuration = (int) (effect.getPotion().isInstant() ? 1 : effect.getDuration() * factor);
			ret.add(new EffectInstance(effect.getPotion(), newDuration, effect.getAmplifier(), effect.isAmbient(), effect.doesShowParticles()));
		}

		return ret;
	}

	public static List<EffectInstance> augmentPotionEffects(List<EffectInstance> effects, int redstoneCount, int glowstoneCount) {
		return addRedstone(addGlowstone(effects, glowstoneCount), redstoneCount);
	}

	private static List<EffectInstance> addRedstone(List<EffectInstance> effects, int redstoneCount) {
		if (redstoneCount <= 0) {
			return effects;
		}

		List<EffectInstance> newEffects = new ArrayList<>();

		int effectCnt = effects.size();
		double multiplier = 1.0;

		for (int redstoneLevel = 1; redstoneLevel <= redstoneCount; redstoneLevel++) {
			multiplier *= (((double) (8 + effectCnt)) / ((double) (3 + effectCnt)) - (1.0 / ((double) (3 + effectCnt)) * (((double) redstoneLevel) - 1.0)));
		}

		for (EffectInstance effect : effects) {
			int newDuration = (int) (effect.getDuration() * multiplier);
			newDuration = Math.min(newDuration, MAX_DURATION * 2);

			EffectInstance newEffect = new EffectInstance(effect.getPotion(), newDuration, effect.getAmplifier(), effect.isAmbient(), effect.doesShowParticles());
			newEffects.add(newEffect);
		}

		return newEffects;
	}

	private static List<EffectInstance> addGlowstone(List<EffectInstance> effects, int glowstoneCount) {
		if (glowstoneCount <= 0) {
			return effects;
		}

		List<EffectInstance> newEffects = new ArrayList<>();

		int effectCnt = effects.size();
		double multiplier = 1.0;

		for (int glowstoneLevel = 1; glowstoneLevel <= glowstoneCount; glowstoneLevel++) {
			multiplier *= (((double) (11 + effectCnt)) / ((double) (6 + effectCnt)) - (1.0 / ((double) (6 + effectCnt)) * ((double) glowstoneLevel)) - 1.0);
		}

		for (EffectInstance effect : effects) {
			int newAmplifier = effect.getAmplifier();

			if (XRPotionHelper.isAugmentablePotionEffect(effect)) {
				newAmplifier = Math.min(effect.getAmplifier() + glowstoneCount, MAX_AMPLIFIER + 1);
			}

			EffectInstance newEffect = new EffectInstance(effect.getPotion(), (int) (effect.getDuration() * multiplier), newAmplifier, effect.isAmbient(), effect.doesShowParticles());
			newEffects.add(newEffect);
		}
		return newEffects;
	}

	static List<EffectInstance> combineIngredients(PotionIngredient... ingredients) {
		return combineIngredients(Arrays.asList(ingredients));
	}

	//this handles the actual combining of two or more ingredients, including other essences.
	public static List<EffectInstance> combineIngredients(Collection<PotionIngredient> ingredients) {

		//helper list to store what we have, altogether
		Map<ResourceLocation, List<EffectInstance>> potionEffectCounterList = new HashMap<>();

		//actual list to store what we have two or more of, these are the actual final effects
		List<ResourceLocation> potionEffectList = new ArrayList<>();

		//add each effect to the counter list. if it appears twice, add it to the potionEffectList too.
		for (PotionIngredient ingredient : ingredients) {
			for (EffectInstance effect : ingredient.getEffects()) {
				if (potionEffectCounterList.containsKey(effect.getPotion().getRegistryName())) {
					if (!potionEffectList.contains(effect.getPotion().getRegistryName())) {
						potionEffectList.add(effect.getPotion().getRegistryName());
					}
					potionEffectCounterList.get(effect.getPotion().getRegistryName()).add(effect);
				} else {
					ArrayList<EffectInstance> effects = new ArrayList<>();
					effects.add(effect);
					potionEffectCounterList.put(effect.getPotion().getRegistryName(), effects);
				}
			}
		}

		List<EffectInstance> combinedEffects = Lists.newArrayList();

		//iterate through common effects
		for (ResourceLocation potionName : potionEffectList) {
			List<EffectInstance> effects = potionEffectCounterList.get(potionName);

			int duration = getCombinedDuration(effects);
			int amplifier = getCombinedAmplifier(effects);

			if (duration == 0) {
				continue;
			}

			Effect potion = ForgeRegistries.POTIONS.getValue(potionName);
			if (potion != null) {
				combinedEffects.add(new EffectInstance(potion, duration, amplifier));
			}
		}
		combinedEffects.sort(new EffectComparator());

		return combinedEffects;
	}

	private static int getCombinedAmplifier(List<EffectInstance> effects) {
		int amplifier = 0;
		for (EffectInstance effect : effects) {
			amplifier += effect.getAmplifier();
		}

		return Math.min(amplifier, XRPotionHelper.MAX_AMPLIFIER);
	}

	private static int getCombinedDuration(List<EffectInstance> effects) {
		int count = 0;
		int duration = 0;
		for (EffectInstance effect : effects) {
			if (effect.getPotion().isInstant()) {
				return 1;
			}

			count++;

			duration += effect.getDuration();
		}

		duration = (int) (duration / 1.2);
		if (count == 3) {
			duration = (int) (duration / 1.1);
		}

		return Math.min(duration, XRPotionHelper.MAX_DURATION);
	}

	public static void applyEffectsToEntity(Collection<EffectInstance> effects, Entity source, Entity indirectSource, LivingEntity entitylivingbase) {
		applyEffectsToEntity(effects, source, indirectSource, entitylivingbase, 1.0);
	}

	public static void applyEffectsToEntity(Collection<EffectInstance> effects, Entity source,
			@Nullable Entity indirectSource, LivingEntity entitylivingbase, double amplifier) {
		for (EffectInstance potioneffect : effects) {
			if (potioneffect.getPotion().isInstant()) {
				potioneffect.getPotion().affectEntity(source, indirectSource, entitylivingbase, potioneffect.getAmplifier(), amplifier);
			} else {
				int j = (int) (amplifier * (double) potioneffect.getDuration() + 0.5D);

				if (j > 20) {
					entitylivingbase.addPotionEffect(new EffectInstance(potioneffect.getPotion(), j, potioneffect.getAmplifier(), false, false));
				}
			}
		}
	}
}
