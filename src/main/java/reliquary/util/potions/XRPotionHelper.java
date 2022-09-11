package reliquary.util.potions;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import reliquary.items.PotionEssenceItem;
import reliquary.util.RegistryHelper;

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
			if (RegistryHelper.registryNamesEqual(ingredient.getItem().getItem(), stack.getItem())) {
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
			if (RegistryHelper.registryNamesEqual(ingredient.getItem().getItem(), stack.getItem())) {
				return Optional.of(ingredient);
			}
		}
		return Optional.empty();
	}

	private static final MobEffect[] nonAugmentableEffects = new MobEffect[] {MobEffects.BLINDNESS,
			MobEffects.CONFUSION,
			MobEffects.INVISIBILITY,
			MobEffects.NIGHT_VISION,
			MobEffects.WATER_BREATHING};

	private static boolean isAugmentablePotionEffect(MobEffectInstance effect) {
		for (MobEffect nonAugmentableEffect : nonAugmentableEffects) {
			if (nonAugmentableEffect == effect.getEffect()) {
				return false;
			}
		}

		return true;
	}

	@OnlyIn(Dist.CLIENT)
	public static void addPotionTooltip(List<MobEffectInstance> effects, List<Component> tooltip) {
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

	@OnlyIn(Dist.CLIENT)
	public static void addPotionTooltip(ItemStack stack, List<Component> list) {
		addPotionTooltip(getPotionEffectsFromStack(stack), list);
	}

	public static void addPotionEffectsToCompoundTag(CompoundTag tag, Collection<MobEffectInstance> effects) {
		if (effects.isEmpty()) {
			return;
		}

		ListTag effectList = tag.getList(EFFECTS_TAG, 10);
		for (MobEffectInstance object : effects) {
			CompoundTag effect = new CompoundTag();
			effect.putString("name", RegistryHelper.getRegistryName(object.getEffect()).toString());
			effect.putInt("duration", object.getEffect().isInstantenous() ? 1 : object.getDuration());
			effect.putInt("potency", object.getAmplifier());
			effectList.add(effect);
		}
		tag.put(EFFECTS_TAG, effectList);
	}

	public static List<MobEffectInstance> getPotionEffectsFromCompoundTag(CompoundTag tag) {
		if (!tag.contains(EFFECTS_NBT_TAG)) {
			return Lists.newArrayList();
		}

		ListTag effectList = tag.getList(EFFECTS_NBT_TAG, 10);

		List<MobEffectInstance> ret = Lists.newArrayList();
		for (Tag effectTag : effectList) {
			CompoundTag effect = (CompoundTag) effectTag;

			String registryName = effect.getString("name");
			int duration = effect.getInt("duration");
			int potency = effect.getInt("potency");
			//noinspection ConstantConditions
			ret.add(new MobEffectInstance(ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(registryName)), duration, potency));
		}

		return ret;
	}

	public static List<MobEffectInstance> getPotionEffectsFromStack(ItemStack stack) {
		if (!stack.hasTag()) {
			return Collections.emptyList();
		}

		//noinspection ConstantConditions
		return getPotionEffectsFromCompoundTag(stack.getTag());
	}

	public static void addPotionEffectsToStack(ItemStack itemstack, List<MobEffectInstance> effects) {
		CompoundTag tag = MoreObjects.firstNonNull(itemstack.getTag(), new CompoundTag());
		addPotionEffectsToCompoundTag(tag, effects);
		itemstack.setTag(tag);
	}

	public static void cleanPotionEffects(ItemStack stack) {
		CompoundTag tag = stack.getTag();

		if (tag == null) {
			return;
		}

		if (tag.contains(EFFECTS_TAG)) {
			tag.remove(EFFECTS_TAG);
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static List<MobEffectInstance> changePotionEffectsDuration(Collection<MobEffectInstance> effects, float factor) {
		List<MobEffectInstance> ret = Lists.newArrayList();

		for (MobEffectInstance effect : effects) {
			int newDuration = (int) (effect.getEffect().isInstantenous() ? 1 : effect.getDuration() * factor);
			ret.add(new MobEffectInstance(effect.getEffect(), newDuration, effect.getAmplifier(), effect.isAmbient(), effect.isVisible()));
		}

		return ret;
	}

	public static List<MobEffectInstance> augmentPotionEffects(List<MobEffectInstance> effects, int redstoneCount, int glowstoneCount) {
		return addRedstone(addGlowstone(effects, glowstoneCount), redstoneCount);
	}

	private static List<MobEffectInstance> addRedstone(List<MobEffectInstance> effects, int redstoneCount) {
		if (redstoneCount <= 0) {
			return effects;
		}

		List<MobEffectInstance> newEffects = new ArrayList<>();

		int effectCnt = effects.size();
		double multiplier = 1.0;

		for (int redstoneLevel = 1; redstoneLevel <= redstoneCount; redstoneLevel++) {
			multiplier *= (((double) (8 + effectCnt)) / ((double) (3 + effectCnt)) - (1.0 / (3 + effectCnt) * (redstoneLevel - 1.0)));
		}

		for (MobEffectInstance effect : effects) {
			int newDuration = (int) (effect.getDuration() * multiplier);
			newDuration = Math.min(newDuration, MAX_DURATION * 2);

			MobEffectInstance newEffect = new MobEffectInstance(effect.getEffect(), newDuration, effect.getAmplifier(), effect.isAmbient(), effect.isVisible());
			newEffects.add(newEffect);
		}

		return newEffects;
	}

	private static List<MobEffectInstance> addGlowstone(List<MobEffectInstance> effects, int glowstoneCount) {
		if (glowstoneCount <= 0) {
			return effects;
		}

		List<MobEffectInstance> newEffects = new ArrayList<>();

		int effectCnt = effects.size();
		double multiplier = 1.0;

		for (int glowstoneLevel = 1; glowstoneLevel <= glowstoneCount; glowstoneLevel++) {
			multiplier *= (((double) (11 + effectCnt)) / ((double) (6 + effectCnt)) - (1.0 / (6 + effectCnt) * glowstoneLevel) - 1.0);
		}

		for (MobEffectInstance effect : effects) {
			int newAmplifier = effect.getAmplifier();

			if (XRPotionHelper.isAugmentablePotionEffect(effect)) {
				newAmplifier = Math.min(effect.getAmplifier() + glowstoneCount, MAX_AMPLIFIER + 1);
			}

			MobEffectInstance newEffect = new MobEffectInstance(effect.getEffect(), (int) (effect.getDuration() * multiplier), newAmplifier, effect.isAmbient(), effect.isVisible());
			newEffects.add(newEffect);
		}
		return newEffects;
	}

	static List<MobEffectInstance> combineIngredients(PotionIngredient... ingredients) {
		return combineIngredients(Arrays.asList(ingredients));
	}

	//this handles the actual combining of two or more ingredients, including other essences.
	public static List<MobEffectInstance> combineIngredients(Collection<PotionIngredient> ingredients) {

		//helper list to store what we have, altogether
		Map<ResourceLocation, List<MobEffectInstance>> potionEffectCounterList = new HashMap<>();

		//actual list to store what we have two or more of, these are the actual final effects
		List<ResourceLocation> potionEffectList = new ArrayList<>();

		//add each effect to the counter list. if it appears twice, add it to the potionEffectList too.
		for (PotionIngredient ingredient : ingredients) {
			for (MobEffectInstance effect : ingredient.getEffects()) {
				if (potionEffectCounterList.containsKey(RegistryHelper.getRegistryName(effect.getEffect()))) {
					if (!potionEffectList.contains(RegistryHelper.getRegistryName(effect.getEffect()))) {
						potionEffectList.add(RegistryHelper.getRegistryName(effect.getEffect()));
					}
					potionEffectCounterList.get(RegistryHelper.getRegistryName(effect.getEffect())).add(effect);
				} else {
					ArrayList<MobEffectInstance> effects = new ArrayList<>();
					effects.add(effect);
					potionEffectCounterList.put(RegistryHelper.getRegistryName(effect.getEffect()), effects);
				}
			}
		}

		List<MobEffectInstance> combinedEffects = Lists.newArrayList();

		//iterate through common effects
		for (ResourceLocation potionName : potionEffectList) {
			List<MobEffectInstance> effects = potionEffectCounterList.get(potionName);

			int duration = getCombinedDuration(effects);
			int amplifier = getCombinedAmplifier(potionName, effects);

			if (duration == 0) {
				continue;
			}

			MobEffect potion = ForgeRegistries.MOB_EFFECTS.getValue(potionName);
			if (potion != null) {
				combinedEffects.add(new MobEffectInstance(potion, duration, amplifier));
			}
		}
		combinedEffects.sort(new EffectComparator());

		return combinedEffects;
	}

	private static int getCombinedAmplifier(ResourceLocation potionName, List<MobEffectInstance> effects) {
		int amplifier = 0;
		for (MobEffectInstance effect : effects) {
			amplifier += effect.getAmplifier();
		}

		if (!potionName.equals(RegistryHelper.getRegistryName(MobEffects.SATURATION))) {
			amplifier = Math.min(amplifier, XRPotionHelper.MAX_AMPLIFIER);
		}

		return amplifier;
	}

	private static int getCombinedDuration(List<MobEffectInstance> effects) {
		int count = 0;
		int duration = 0;
		for (MobEffectInstance effect : effects) {
			if (effect.getEffect().isInstantenous()) {
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

	public static void applyEffectsToEntity(Collection<MobEffectInstance> effects, Entity source,
			@Nullable Entity indirectSource, LivingEntity entitylivingbase) {
		applyEffectsToEntity(effects, source, indirectSource, entitylivingbase, 1.0);
	}

	public static void applyEffectsToEntity(Collection<MobEffectInstance> effects, Entity source,
			@Nullable Entity indirectSource, LivingEntity entitylivingbase, double amplifier) {
		for (MobEffectInstance potioneffect : effects) {
			if (potioneffect.getEffect().isInstantenous()) {
				potioneffect.getEffect().applyInstantenousEffect(source, indirectSource, entitylivingbase, potioneffect.getAmplifier(), amplifier);
			} else {
				int j = (int) (amplifier * potioneffect.getDuration() + 0.5D);

				if (j > 20) {
					entitylivingbase.addEffect(new MobEffectInstance(potioneffect.getEffect(), j, potioneffect.getAmplifier(), false, false));
				}
			}
		}
	}
}
