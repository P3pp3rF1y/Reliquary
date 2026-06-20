package reliquary.util.potions;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import org.apache.commons.lang3.stream.Streams;
import org.jspecify.annotations.Nullable;
import reliquary.item.PotionEssenceItem;
import reliquary.util.RegistryHelper;

import java.util.*;

public class PotionHelper {

	public static final String EFFECTS = "effects";

	private PotionHelper() {
	}

	private static final int MAX_DURATION = 36000;
	private static final int MAX_AMPLIFIER = 4;

	public static boolean isItemEssence(Item item) {
		return item instanceof PotionEssenceItem;
	}

	public static boolean isIngredient(Item item) {
		for (PotionIngredient ingredient : PotionMap.ingredients) {
			if (RegistryHelper.registryNamesEqual(ingredient.getIngredientItem(), item)) {
				return true;
			}
		}
		return false;
	}

	public static Optional<PotionIngredient> getIngredient(ItemStack stack) {
		if (isItemEssence(stack.getItem())) {
			return Optional.of(new PotionIngredient(stack, Streams.of(getPotionEffectsFromStack(stack)).toList()));
		}
		for (PotionIngredient ingredient : PotionMap.ingredients) {
			if (RegistryHelper.registryNamesEqual(ingredient.getIngredientItem(), stack.getItem())) {
				return Optional.of(ingredient);
			}
		}
		return Optional.empty();
	}

	private static final Set<Holder<MobEffect>> nonAugmentableEffects = Set.of(
			MobEffects.BLINDNESS,
			MobEffects.NAUSEA,
			MobEffects.INVISIBILITY,
			MobEffects.NIGHT_VISION,
			MobEffects.WATER_BREATHING
	);

	private static boolean isAugmentablePotionEffect(MobEffectInstance effect) {
		return !nonAugmentableEffects.contains(effect.getEffect());
	}

	public static void addPotionContentsToStack(ItemStack itemstack, PotionContents potionContents) {
		if (potionContents.hasEffects()) {
			itemstack.set(DataComponents.POTION_CONTENTS, potionContents);
		}
	}

	public static void cleanPotionEffects(ItemStack stack) {
		stack.remove(DataComponents.POTION_CONTENTS);
	}

	public static PotionContents changePotionEffectsDuration(PotionContents potionContents, float factor) {
		List<MobEffectInstance> ret = Lists.newArrayList();

		for (MobEffectInstance effect : potionContents.getAllEffects()) {
			int newDuration = (int) (effect.getEffect().value().isInstantaneous() ? 1 : effect.getDuration() * factor);
			ret.add(new MobEffectInstance(effect.getEffect(), newDuration, effect.getAmplifier(), effect.isAmbient(), effect.isVisible()));
		}

		return new PotionContents(potionContents.potion(), potionContents.customColor(), ret, potionContents.customName());
	}

	public static PotionContents augmentPotionContents(PotionContents potionContents, int redstoneCount, int glowstoneCount) {
		return addRedstone(addGlowstone(potionContents, glowstoneCount), redstoneCount);
	}

	private static PotionContents addRedstone(PotionContents potionContents, int redstoneCount) {
		if (redstoneCount <= 0) {
			return potionContents;
		}

		List<MobEffectInstance> newEffects = new ArrayList<>();

		int effectCnt = Iterables.size(potionContents.getAllEffects());
		double multiplier = 1.0;

		for (int redstoneLevel = 1; redstoneLevel <= redstoneCount; redstoneLevel++) {
			multiplier *= (((double) (8 + effectCnt)) / ((double) (3 + effectCnt)) - (1.0 / (3 + effectCnt) * (redstoneLevel - 1.0)));
		}

		for (MobEffectInstance effect : potionContents.getAllEffects()) {
			int newDuration = (int) (effect.getDuration() * multiplier);
			newDuration = Math.min(newDuration, MAX_DURATION * 2);

			MobEffectInstance newEffect = new MobEffectInstance(effect.getEffect(), newDuration, effect.getAmplifier(), effect.isAmbient(), effect.isVisible());
			newEffects.add(newEffect);
		}

		return new PotionContents(potionContents.potion(), potionContents.customColor(), newEffects, potionContents.customName());
	}

	private static PotionContents addGlowstone(PotionContents potionContents, int glowstoneCount) {
		if (glowstoneCount <= 0) {
			return potionContents;
		}

		List<MobEffectInstance> newEffects = new ArrayList<>();

		int effectCnt = Iterables.size(potionContents.getAllEffects());
		double multiplier = 1.0;

		for (int glowstoneLevel = 1; glowstoneLevel <= glowstoneCount; glowstoneLevel++) {
			multiplier *= (((double) (11 + effectCnt)) / ((double) (6 + effectCnt)) - (1.0 / (6 + effectCnt) * glowstoneLevel) - 1.0);
		}

		for (MobEffectInstance effect : potionContents.getAllEffects()) {
			int newAmplifier = effect.getAmplifier();

			if (PotionHelper.isAugmentablePotionEffect(effect)) {
				newAmplifier = Math.min(effect.getAmplifier() + glowstoneCount, MAX_AMPLIFIER + 1);
			}

			MobEffectInstance newEffect = new MobEffectInstance(effect.getEffect(), (int) (effect.getDuration() * multiplier), newAmplifier, effect.isAmbient(), effect.isVisible());
			newEffects.add(newEffect);
		}
		return new PotionContents(potionContents.potion(), potionContents.customColor(), newEffects, potionContents.customName());
	}

	static PotionContents combineIngredients(PotionIngredient... ingredients) {
		return combineIngredients(Arrays.asList(ingredients));
	}

	//this handles the actual combining of two or more ingredients, including other essences.
	public static PotionContents combineIngredients(Collection<PotionIngredient> ingredients) {

		//helper list to store what we have, altogether
		Map<ResourceKey<MobEffect>, List<MobEffectInstance>> potionEffectCounterList = new HashMap<>();

		//actual list to store what we have two or more of, these are the actual final effects
		Set<ResourceKey<MobEffect>> potionEffectList = new HashSet<>();

		//add each effect to the counter list. if it appears twice, add it to the potionEffectList too.
		for (PotionIngredient ingredient : ingredients) {
			for (MobEffectInstance effect : ingredient.getEffects()) {
				if (potionEffectCounterList.containsKey(effect.getEffect().getKey())) {
					potionEffectList.add(effect.getEffect().getKey());
					potionEffectCounterList.get(effect.getEffect().getKey()).add(effect);
				} else {
					ArrayList<MobEffectInstance> effects = new ArrayList<>();
					effects.add(effect);
					potionEffectCounterList.put(effect.getEffect().getKey(), effects);
				}
			}
		}

		List<MobEffectInstance> combinedEffects = Lists.newArrayList();

		//iterate through common effects
		for (ResourceKey<MobEffect> potionKey : potionEffectList) {
			List<MobEffectInstance> effects = potionEffectCounterList.get(potionKey);

			int duration = getCombinedDuration(effects);
			int amplifier = getCombinedAmplifier(potionKey, effects);

			if (duration == 0) {
				continue;
			}

			BuiltInRegistries.MOB_EFFECT.get(potionKey).ifPresent(mobEffect -> combinedEffects.add(new MobEffectInstance(mobEffect, duration, amplifier)));
		}
		combinedEffects.sort(new EffectComparator());

		return new PotionContents(Optional.empty(), Optional.empty(), combinedEffects, Optional.empty());
	}

	private static int getCombinedAmplifier(ResourceKey<MobEffect> potionKey, List<MobEffectInstance> effects) {
		int amplifier = 0;
		for (MobEffectInstance effect : effects) {
			amplifier += effect.getAmplifier();
		}

		if (!MobEffects.SATURATION.is(potionKey)) {
			amplifier = Math.min(amplifier, PotionHelper.MAX_AMPLIFIER);
		}

		return amplifier;
	}

	private static int getCombinedDuration(List<MobEffectInstance> effects) {
		int count = 0;
		int duration = 0;
		for (MobEffectInstance effect : effects) {
			if (effect.getEffect().value().isInstantaneous()) {
				return 1;
			}

			count++;

			duration += effect.getDuration();
		}

		duration = (int) (duration / 1.2);
		if (count == 3) {
			duration = (int) (duration / 1.1);
		}

		return Math.min(duration, PotionHelper.MAX_DURATION);
	}

	public static void applyEffectsToEntity(PotionContents potionContents, Entity source,
											@Nullable Entity indirectSource, LivingEntity livingEntity) {
		applyEffectsToEntity(potionContents, source, indirectSource, livingEntity, 1.0);
	}

	public static void applyEffectsToEntity(PotionContents potionContents, Entity source,
											@Nullable Entity indirectSource, LivingEntity livingEntity, double amplifier) {

		potionContents.forEachEffect(effectInstance -> {
			if (effectInstance.getEffect().value().isInstantaneous()) {
				if (livingEntity.level() instanceof ServerLevel serverLevel) {
					effectInstance.getEffect().value().applyInstantaneousEffect(serverLevel, source, indirectSource, livingEntity, effectInstance.getAmplifier(), amplifier);
				}
			} else {
				int j = (int) (amplifier * effectInstance.getDuration() + 0.5D);

				if (j > 20) {
					livingEntity.addEffect(new MobEffectInstance(effectInstance.getEffect(), j, effectInstance.getAmplifier(), false, false));
				}
			}
		}, 1);
	}

	public static boolean hasPotionContents(ItemStack stack) {
		return stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).hasEffects();
	}

	public static Iterable<MobEffectInstance> getPotionEffectsFromStack(ItemStack stack) {
		return stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getAllEffects();
	}
}
