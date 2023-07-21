package reliquary.util.potions;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import reliquary.init.ModItems;
import reliquary.reference.Reference;
import reliquary.reference.Settings;
import reliquary.util.LogHelper;
import reliquary.util.RegistryHelper;
import reliquary.util.StackHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Pattern;

public class PotionMap {
	private PotionMap() {}

	protected static final List<PotionIngredient> ingredients = new ArrayList<>();
	public static final List<PotionEssence> potionCombinations = new ArrayList<>();
	public static final List<PotionEssence> uniquePotionEssences = new ArrayList<>();
	public static final List<PotionEssence> uniquePotions = new ArrayList<>();

	public static void initPotionMap() {
		setDefaultInConfigIfEmpty();
		loadPotionMapFromSettings();
		LogHelper.debug("Starting calculation of potion combinations");
		loadPotionCombinations();
		loadUniquePotions();
		LogHelper.debug("Done with potion combinations");
	}

	private static void setDefaultInConfigIfEmpty() {
		if (Settings.COMMON.potions.potionMap.get().isEmpty()) {
			Settings.COMMON.potions.potionMap.set(getDefaultConfigPotionMap());
		}
	}

	private static void loadUniquePotions() {
		uniquePotionEssences.clear();
		uniquePotions.clear();

		for (PotionEssence essence : potionCombinations) {
			boolean found = false;
			for (PotionEssence uniqueEssence : uniquePotionEssences) {
				if (effectsEqual(essence.getEffects(), uniqueEssence.getEffects())) {
					found = true;
					break;
				}
			}
			if (!found) {
				uniquePotionEssences.add(essence);
				addUniquePotions(essence);
			}
		}

		uniquePotionEssences.sort(new PotionEssenceComparator());
		uniquePotions.sort(new PotionEssenceComparator());
	}

	private static void addUniquePotions(PotionEssence essence) {
		uniquePotions.add(essence);

		if (Boolean.TRUE.equals(Settings.COMMON.potions.redstoneAndGlowstone.get())) {
			PotionEssence redstone = essence.copy();
			redstone.setEffects(XRPotionHelper.augmentPotionEffects(redstone.getEffects(), 1, 0));
			redstone.setRedstoneCount(1);
			uniquePotions.add(redstone);

			PotionEssence glowstone = essence.copy();
			glowstone.setEffects(XRPotionHelper.augmentPotionEffects(redstone.getEffects(), 0, 1));
			glowstone.setGlowstoneCount(1);
			uniquePotions.add(glowstone);

			PotionEssence redstoneGlowstone = essence.copy();
			redstoneGlowstone.setEffects(XRPotionHelper.augmentPotionEffects(redstone.getEffects(), 1, 1));
			redstoneGlowstone.setRedstoneCount(1);
			redstoneGlowstone.setGlowstoneCount(1);
			uniquePotions.add(redstoneGlowstone);
		}

	}

	private static void loadPotionCombinations() {
		potionCombinations.clear();

		//multiple effect potions and potions made of 3 ingredients are turned on by config option
		for (PotionIngredient ingredient1 : ingredients) {
			for (PotionIngredient ingredient2 : ingredients) {
				if (ingredient1.getItem().getItem() != ingredient2.getItem().getItem()) {
					PotionEssence twoEssence = new PotionEssence.Builder().setIngredients(ingredient1, ingredient2).setEffects(XRPotionHelper.combineIngredients(ingredient1, ingredient2)).build();
					if (!twoEssence.getEffects().isEmpty() && twoEssence.getEffects().size() <= Settings.COMMON.potions.maxEffectCount.get()) {
						addPotionCombination(twoEssence);

						if (Boolean.TRUE.equals(Settings.COMMON.potions.threeIngredients.get())) {
							for (PotionIngredient ingredient3 : ingredients) {
								if ((ingredient3.getItem().getItem() != ingredient1.getItem().getItem()) && ingredient3.getItem().getItem() != ingredient2.getItem().getItem()) {
									PotionEssence threeEssence = new PotionEssence.Builder().setIngredients(ingredient1, ingredient2, ingredient3).setEffects(XRPotionHelper.combineIngredients(ingredient1, ingredient2, ingredient3)).build();

									if (!effectsEqual(twoEssence.getEffects(), threeEssence.getEffects())) {
										addPotionCombination(threeEssence);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private static void addPotionCombination(PotionEssence newEssence) {
		for (PotionEssence essence : potionCombinations) {
			//exactly same ingredients in a different order are not to be added here
			if (ingredientsEqual(essence.getIngredients(), newEssence.getIngredients())) {
				return;
			}
			//the same effect potion id with different duration is turned on by config option
			if (effectsEqual(essence.getEffects(), newEssence.getEffects(), Settings.COMMON.potions.differentDurations.get()) && !effectsEqual(essence.getEffects(), newEssence.getEffects())) {
				return;
			}
		}

		potionCombinations.add(newEssence);
	}

	private static boolean ingredientsEqual(List<PotionIngredient> a, List<PotionIngredient> b) {
		if (a.size() != b.size()) {
			return false;
		}
		for (PotionIngredient ingredientA : a) {
			boolean found = false;
			for (PotionIngredient ingredientB : b) {
				if (ingredientA.getItem().getItem() == ingredientB.getItem().getItem()) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}

	private static boolean effectsEqual(List<MobEffectInstance> a, List<MobEffectInstance> b) {
		return effectsEqual(a, b, true);
	}

	private static boolean effectsEqual(List<MobEffectInstance> a, List<MobEffectInstance> b, boolean compareDuration) {
		if (a.size() != b.size()) {
			return false;
		}

		for (MobEffectInstance effectA : a) {
			boolean found = false;
			for (MobEffectInstance effectB : b) {
				if (effectA.getDescriptionId().equals(effectB.getDescriptionId()) && (!compareDuration || effectA.getDuration() == effectB.getDuration()) && (effectA.getAmplifier() == effectB.getAmplifier())) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("squid:S4784")
	private static void loadPotionMapFromSettings() {
		ingredients.clear();

		Pattern validEntry = Pattern.compile("[a-z_:0-9]+=[a-z_0-9:.|;]+");
		for (String entry : Settings.COMMON.potions.potionMap.get()) {
			if (validEntry.matcher(entry).matches()) {
				String[] entryParts = entry.split("=");

				String name = entryParts[0];
				String[] effects = entryParts[1].split(";");

				addItemEffectsToPotionMap(name, effects);
			} else {
				LogHelper.error("Potion map entry \"" + entry + "\" is not valid.\n"
						+ "Needs to be mod:item_or_block_registry_name=potion_effect_name|duration_multiplier|amplifier\n"
						+ "Potion effect part (\"potion_effect_name|...|amplifier\") can be optionally repeated if there are multiple effects on item, the individual potion sections are delimited by semicolon \";\"\n"
						+ "Duration multiplier is multiples of 15 seconds the potion will last");
			}
		}
	}

	private static void addItemEffectsToPotionMap(String name, String[] effects) {
		StackHelper.getItemStackFromName(name).ifPresent(stack -> {
			PotionIngredient ingredient = new PotionIngredient(stack);
			for (String effect : effects) {
				String[] effectValues = effect.split("\\|");
				String potionName = effectValues[0];
				if (!potionName.isEmpty()) {
					short durationWeight = Short.parseShort(effectValues[1]);
					short ampWeight = Short.parseShort(effectValues[2]);
					ingredient.addEffect(potionName, durationWeight, ampWeight);
				}
			}
			if (!ingredient.getEffects().isEmpty()) {
				ingredients.add(ingredient);
			}
		});
	}

	public static List<String> getDefaultConfigPotionMap() {
		List<String> potionMap = new ArrayList<>();

		//TIER ONE INGREDIENTS, these are always 0 potency and have minimal durations (3 for positive, 1 for negative or super-positive)
		addPotionIngredient(potionMap, Items.SUGAR, speed(3, 0), haste(3, 0));
		addPotionIngredient(potionMap, Items.APPLE, heal(0), hboost(3, 0), cure(0));
		addPotionIngredient(potionMap, Items.COAL, blind(1), absorb(3, 0), invis(1), wither(0, 0));
		addPotionIngredient(potionMap, Items.FEATHER, jump(3, 0), weak(1, 0));
		addPotionIngredient(potionMap, Items.WHEAT_SEEDS, harm(0), hboost(3, 0));
		addPotionIngredient(potionMap, Items.WHEAT, heal(0), hboost(3, 0));
		addPotionIngredient(potionMap, Items.FLINT, harm(0), dboost(3, 0));
		addPotionIngredient(potionMap, Items.PORKCHOP, slow(1, 0), fatigue(1, 0));
		addPotionIngredient(potionMap, Items.LEATHER, resist(3, 0), absorb(3, 0));
		addPotionIngredient(potionMap, Items.CLAY_BALL, slow(1, 0), hboost(3, 0));
		addPotionIngredient(potionMap, Items.EGG, absorb(3, 0), regen(0, 0));
		addPotionIngredient(potionMap, Items.RED_DYE, heal(0), hboost(3, 0)); //rose red
		addPotionIngredient(potionMap, Items.YELLOW_DYE, jump(3, 0), weak(1, 0)); //dandellion yellow
		addPotionIngredient(potionMap, Items.GREEN_DYE, resist(3, 0), absorb(3, 0)); //cactus green
		addPotionIngredient(potionMap, Items.BONE_MEAL, weak(1, 0), fatigue(1, 0)); //bone meal
		addPotionIngredient(potionMap, Items.PUMPKIN_SEEDS, invis(1), fireres(1));
		addPotionIngredient(potionMap, Items.BEEF, slow(1, 0), satur(5));
		addPotionIngredient(potionMap, Items.CHICKEN, nausea(1), poison(1));
		addPotionIngredient(potionMap, Items.ROTTEN_FLESH, nausea(1), hunger(1), wither(0, 0));
		addPotionIngredient(potionMap, Items.GOLD_NUGGET, dboost(0, 0), haste(0, 0));
		addPotionIngredient(potionMap, Items.CARROT, vision(3), hboost(3, 0));
		addPotionIngredient(potionMap, Items.POTATO, hboost(3, 0), satur(2));
		addPotionIngredient(potionMap, Items.COD, satur(3), breath(1));

		//TIER TWO INGREDIENTS, one of the effects of each will always be a one, slightly increased duration vs. TIER ONE
		addPotionIngredient(potionMap, Items.SPIDER_EYE, vision(4), poison(2));
		addPotionIngredient(potionMap, Items.BLAZE_POWDER, dboost(4, 0), harm(0));
		addPotionIngredient(potionMap, Items.IRON_INGOT, resist(4, 0), slow(2, 0));
		addPotionIngredient(potionMap, Items.STRING, slow(2, 0), fatigue(2, 0));
		addPotionIngredient(potionMap, Items.BREAD, hboost(4, 0), satur(5));
		addPotionIngredient(potionMap, Items.COOKED_PORKCHOP, fatigue(2, 0), satur(5));
		addPotionIngredient(potionMap, Items.SLIME_BALL, resist(4, 0), fireres(2));
		addPotionIngredient(potionMap, Items.COOKED_COD, satur(4), breath(2));
		addPotionIngredient(potionMap, Items.LAPIS_LAZULI, haste(4, 0), dboost(4, 0));  //lapis lazuli
		addPotionIngredient(potionMap, Items.INK_SAC, blind(2), invis(2)); //ink
		addPotionIngredient(potionMap, Items.BONE, weak(2, 0), fatigue(2, 0));
		addPotionIngredient(potionMap, Items.COOKIE, heal(0), satur(3));
		addPotionIngredient(potionMap, Items.MELON, heal(0), speed(4, 0));
		addPotionIngredient(potionMap, Items.COOKED_BEEF, resist(4, 0), satur(5));
		addPotionIngredient(potionMap, Items.COOKED_CHICKEN, jump(4, 0), satur(5));
		addPotionIngredient(potionMap, Items.BAKED_POTATO, satur(4), regen(1, 0));
		addPotionIngredient(potionMap, Items.POISONOUS_POTATO, poison(2), wither(1, 0));
		addPotionIngredient(potionMap, Items.QUARTZ, harm(0), dboost(4, 0));
		addPotionIngredient(potionMap, ModItems.ZOMBIE_HEART.get(), nausea(2), hunger(2), wither(1, 0));
		addPotionIngredient(potionMap, ModItems.SQUID_BEAK.get(), hunger(2), breath(2));

		//TIER THREE INGREDIENTS, these are closer to vanilla durations, carry many effects or a slightly increased duration. Some/most are combos.
		addPotionIngredient(potionMap, Items.PUMPKIN_PIE, invis(1), fireres(1), speed(3, 0), haste(3, 0), absorb(3, 0), regen(0, 0)); //combination of ingredients, strong.
		addPotionIngredient(potionMap, Items.MAGMA_CREAM, dboost(4, 0), harm(0), resist(4, 0), fireres(2)); //also a combo, strong.
		addPotionIngredient(potionMap, Items.GLISTERING_MELON_SLICE, dboost(3, 0), haste(3, 0), heal(0), speed(4, 0)); //combo
		addPotionIngredient(potionMap, Items.GHAST_TEAR, regen(3, 0), absorb(5, 0));
		addPotionIngredient(potionMap, Items.FERMENTED_SPIDER_EYE, vision(4), poison(2), speed(3, 0), haste(3, 0)); //combo
		addPotionIngredient(potionMap, Items.GOLDEN_CARROT, dboost(3, 0), haste(3, 0), hboost(3, 0), vision(3)); //combo
		addPotionIngredient(potionMap, Items.GOLD_INGOT, dboost(4, 0), haste(4, 0), cure(0)); //combo
		addPotionIngredient(potionMap, ModItems.RIB_BONE.get(), weak(3, 0), fatigue(3, 0), cure(0));
		addPotionIngredient(potionMap, Items.ENDER_PEARL, invis(5), speed(5, 0));
		addPotionIngredient(potionMap, Items.BLAZE_ROD, dboost(8, 0), harm(0));
		addPotionIngredient(potionMap, Items.FIRE_CHARGE, dboost(4, 0), harm(0), blind(1), absorb(3, 0)); //combo
		addPotionIngredient(potionMap, ModItems.CATALYZING_GLAND.get(), regen(3, 0), hboost(5, 0));
		addPotionIngredient(potionMap, ModItems.CHELICERAE.get(), poison(3), weak(3, 0));
		addPotionIngredient(potionMap, ModItems.SLIME_PEARL.get(), resist(5, 0), absorb(5, 0));
		addPotionIngredient(potionMap, ModItems.KRAKEN_SHELL_FRAGMENT.get(), absorb(5, 0), breath(5));
		addPotionIngredient(potionMap, ModItems.BAT_WING.get(), jump(5, 0), weak(3, 0));
		addPotionIngredient(potionMap, Items.GOLDEN_APPLE, cure(1));
		addPotionIngredient(potionMap, Items.GOLDEN_APPLE, cure(2));

		//TIER FOUR INGREDIENTS, these carry multiple one-potency effects and have the most duration for any given effect.
		addPotionIngredient(potionMap, Items.DIAMOND, resist(6, 1), absorb(6, 1), fireres(6), cure(0));
		addPotionIngredient(potionMap, ModItems.WITHERED_RIB.get(), wither(2, 1), weak(3, 1), slow(3, 1), fatigue(3, 1), cure(0));
		addPotionIngredient(potionMap, Items.ENDER_EYE, dboost(6, 1), invis(6), speed(6, 1), harm(1));
		addPotionIngredient(potionMap, Items.EMERALD, haste(6, 1), speed(6, 1), hboost(6, 1), cure(1));
		addPotionIngredient(potionMap, Items.NETHER_STAR, hboost(24, 1), regen(24, 1), absorb(24, 1), cure(2)); //nether star is holy stonk
		addPotionIngredient(potionMap, ModItems.MOLTEN_CORE.get(), dboost(6, 1), fireres(6), harm(1));
		addPotionIngredient(potionMap, ModItems.EYE_OF_THE_STORM.get(), haste(24, 1), speed(24, 1), jump(24, 1), harm(1), cure(1));
		addPotionIngredient(potionMap, ModItems.FERTILE_ESSENCE.get(), hboost(8, 1), regen(3, 1), heal(1), satur(4), weak(9, 1), fatigue(9, 1), cure(0));
		addPotionIngredient(potionMap, ModItems.FROZEN_CORE.get(), absorb(6, 1), slow(3, 1), fatigue(3, 1), harm(1), fireres(6));
		addPotionIngredient(potionMap, ModItems.NEBULOUS_HEART.get(), vision(6), invis(6), harm(1), hboost(6, 1), dboost(6, 1), speed(6, 1), haste(6, 1));
		addPotionIngredient(potionMap, ModItems.INFERNAL_CLAW.get(), harm(1), resist(6, 1), fireres(6), dboost(6, 1), satur(5), heal(1));

		return potionMap;
	}

	private static String harm(int potency) {
		return effectString(Reference.HARM, Integer.toString(0), Integer.toString(potency));
	}

	private static String heal(int potency) {
		return effectString(Reference.HEAL, Integer.toString(0), Integer.toString(potency));
	}

	private static String satur(int potency) {
		return effectString(Reference.SATURATION, Integer.toString(0), Integer.toString(potency));
	}

	private static String invis(int duration) {
		return effectString(Reference.INVIS, Integer.toString(duration), Integer.toString(0));
	}

	private static String absorb(int duration, int potency) {
		return effectString(Reference.ABSORB, Integer.toString(duration), Integer.toString(potency));
	}

	private static String hboost(int duration, int potency) {
		return effectString(Reference.HBOOST, Integer.toString(duration), Integer.toString(potency));
	}

	private static String dboost(int duration, int potency) {
		return effectString(Reference.DBOOST, Integer.toString(duration), Integer.toString(potency));
	}

	private static String speed(int duration, int potency) {
		return effectString(Reference.SPEED, Integer.toString(duration), Integer.toString(potency));
	}

	private static String haste(int duration, int potency) {
		return effectString(Reference.HASTE, Integer.toString(duration), Integer.toString(potency));
	}

	private static String slow(int duration, int potency) {
		return effectString(Reference.SLOW, Integer.toString(duration), Integer.toString(potency));
	}

	private static String fatigue(int duration, int potency) {
		return effectString(Reference.FATIGUE, Integer.toString(duration), Integer.toString(potency));
	}

	private static String breath(int duration) {
		return effectString(Reference.BREATH, Integer.toString(duration), Integer.toString(0));
	}

	private static String vision(int duration) {
		return effectString(Reference.VISION, Integer.toString(duration), Integer.toString(0));
	}

	private static String resist(int duration, int potency) {
		return effectString(Reference.RESIST, Integer.toString(duration), Integer.toString(potency));
	}

	private static String fireres(int duration) {
		return effectString(Reference.FRESIST, Integer.toString(duration), Integer.toString(0));
	}

	private static String weak(int duration, int potency) {
		return effectString(Reference.WEAK, Integer.toString(duration), Integer.toString(potency));
	}

	private static String jump(int duration, int potency) {
		return effectString(Reference.JUMP, Integer.toString(duration), Integer.toString(potency));
	}

	private static String nausea(int duration) {
		return effectString(Reference.NAUSEA, Integer.toString(duration), Integer.toString(0));
	}

	private static String hunger(int duration) {
		return effectString(Reference.HUNGER, Integer.toString(duration), Integer.toString(0));
	}

	private static String regen(int duration, int potency) {
		return effectString(Reference.REGEN, Integer.toString(duration), Integer.toString(potency));
	}

	private static String poison(int duration) {
		return effectString(Reference.POISON, Integer.toString(duration), Integer.toString(0));
	}

	private static String wither(int duration, int potency) {
		return effectString(Reference.WITHER, Integer.toString(duration), Integer.toString(potency));
	}

	private static String blind(int duration) {
		return effectString(Reference.BLIND, Integer.toString(duration), Integer.toString(0));
	}

	private static String cure(int potency) {
		return effectString(Reference.CURE, Integer.toString(1), Integer.toString(potency));
	}

	private static String effectString(String name, String duration, String potency) {
		return name + "|" + duration + "|" + potency;
	}

	private static void addPotionIngredient(List<String> potionMap, Item ingredient, String... effects) {
		addPotionIngredient(potionMap, RegistryHelper.getRegistryName(ingredient).toString(), effects);
	}

	private static void addPotionIngredient(List<String> potionMap, String itemRegistryName, String... effects) {
		StringJoiner effectsString = new StringJoiner(";");
		Arrays.stream(effects).forEach(effectsString::add);

		potionMap.add(String.format("%s=%s", itemRegistryName, effectsString));
	}
}
