package xreliquary.util.potions;

import com.google.common.collect.Lists;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;
import xreliquary.util.LogHelper;
import xreliquary.util.StackHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Pattern;

public class PotionMap {
	public static List<PotionIngredient> ingredients = new ArrayList<>();
	public static List<PotionEssence> potionCombinations = new ArrayList<>();
	public static List<PotionEssence> uniquePotionEssences = new ArrayList<>();
	public static List<PotionEssence> uniquePotions = new ArrayList<>();

	public static void initPotionMap() {
		loadPotionMapFromSettings();

		LogHelper.debug("Starting calculation of potion combinations");
		loadPotionCombinations();
		loadUniquePotions();
		LogHelper.debug("Done with potion combinations");
	}

	private static void loadUniquePotions() {
		uniquePotionEssences.clear();
		uniquePotions.clear();

		for(PotionEssence essence : potionCombinations) {
			boolean found = false;
			for(PotionEssence uniqueEssence : uniquePotionEssences) {
				if(effectsEqual(essence.getEffects(), uniqueEssence.getEffects())) {
					found = true;
					break;
				}
			}
			if(!found) {
				uniquePotionEssences.add(essence);
				addUniquePotions(essence);
			}
		}

		uniquePotionEssences.sort(new PotionEssenceComparator());
		uniquePotions.sort(new PotionEssenceComparator());
	}

	private static void addUniquePotions(PotionEssence essence) {
		uniquePotions.add(essence);

		if(Settings.Potions.redstoneAndGlowstone) {
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
		for(PotionIngredient ingredient1 : ingredients) {
			for(PotionIngredient ingredient2 : ingredients) {
				if(ingredient1.item.getItem() != ingredient2.item.getItem() || ingredient1.item.getMetadata() != ingredient2.item.getMetadata()) {
					PotionEssence twoEssence = new PotionEssence.Builder().setIngredients(ingredient1, ingredient2).setEffects(XRPotionHelper.combineIngredients(ingredient1, ingredient2)).build();
					if(twoEssence.getEffects().size() > 0 && twoEssence.getEffects().size() <= Settings.Potions.maxEffectCount) {
						addPotionCombination(twoEssence);

						if(Settings.Potions.threeIngredients) {
							for(PotionIngredient ingredient3 : ingredients) {
								if((ingredient3.item.getItem() != ingredient1.item.getItem() || ingredient3.item.getMetadata() != ingredient1.item.getMetadata()) && (ingredient3.item.getItem() != ingredient2.item.getItem() || ingredient3.item.getMetadata() != ingredient2.item.getMetadata())) {
									PotionEssence threeEssence = new PotionEssence.Builder().setIngredients(ingredient1, ingredient2, ingredient3).setEffects(XRPotionHelper.combineIngredients(ingredient1, ingredient2, ingredient3)).build();

									if(!effectsEqual(twoEssence.getEffects(), threeEssence.getEffects())) {
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
		for(PotionEssence essence : potionCombinations) {
			//exactly same ingredients in a different order are not to be added here
			if(ingredientsEqual(essence.getIngredients(), newEssence.getIngredients())) {
				return;
			}
			//the same effect potion id with different duration is turned on by config option
			if(effectsEqual(essence.getEffects(), newEssence.getEffects(), Settings.Potions.differentDurations) && !effectsEqual(essence.getEffects(), newEssence.getEffects())) {
				return;
			}
		}

		potionCombinations.add(newEssence);
	}

	private static boolean ingredientsEqual(List<PotionIngredient> a, List<PotionIngredient> b) {
		if(a.size() != b.size())
			return false;
		for(PotionIngredient ingredientA : a) {
			boolean found = false;
			for(PotionIngredient ingredientB : b) {
				if(ingredientA.item.getItem() == ingredientB.item.getItem() && ingredientA.item.getMetadata() == ingredientB.item.getMetadata()) {
					found = true;
					break;
				}
			}
			if(!found)
				return false;
		}
		return true;
	}

	private static boolean effectsEqual(List<PotionEffect> a, List<PotionEffect> b) {
		return effectsEqual(a, b, true);
	}

	private static boolean effectsEqual(List<PotionEffect> a, List<PotionEffect> b, boolean compareDuration) {
		if(a.size() != b.size())
			return false;

		for(PotionEffect effectA : a) {
			boolean found = false;
			for(PotionEffect effectB : b) {
				if(effectA.getEffectName().equals(effectB.getEffectName()) && (!compareDuration || effectA.getDuration() == effectB.getDuration()) && (effectA.getAmplifier() == effectB.getAmplifier())) {
					found = true;
					break;
				}
			}
			if(!found)
				return false;
		}
		return true;
	}

	private static void loadPotionMapFromSettings() {
		ingredients.clear();

		Pattern validEntry = Pattern.compile("[a-z_:0-9]+\\|[0-9]+=[a-z_0-9:\\.\\|;]+");
		for(String entry : Settings.Potions.potionMap) {
			if (validEntry.matcher(entry).matches()) {
				String[] entryParts = entry.split("=");

				String[] nameParts = entryParts[0].split("\\|");
				String[] effects = entryParts[1].split(";");

				String modId = nameParts[0].split(":")[0];
				String name = nameParts[0].split(":")[1];
				int meta = Integer.parseInt(nameParts[1]);

				ItemStack stack = StackHelper.getItemStackFromNameMeta(modId, name, meta);

				if(stack != null) {
					PotionIngredient ingredient = new PotionIngredient(stack);
					for(String effect : effects) {
						String[] effectValues = effect.split("\\|");
						String potionName = effectValues[0];
						if(!potionName.isEmpty()) {
							short durationWeight = Short.parseShort(effectValues[1]);
							short ampWeight = Short.parseShort(effectValues[2]);
							ingredient.addEffect(potionName, durationWeight, ampWeight);
						}
					}
					if(!ingredient.effects.isEmpty()) {
						ingredients.add(ingredient);
					}
				}
			} else {
				LogHelper.error("Potion map entry \"" + entry + "\" is not valid.\n"
						+ "Needs to be mod:item_or_block_registry_name|meta=potion_effect_name|duration_multiplier|amplifier\n"
						+ "Potion effect part (\"potion_effect_name|...|amplifier\") can be optionally repeated if there are multiple effects on item, the individual potion sections are delimited by semicolon \";\"\n"
						+ "Duration multiplier is multiples of 15 seconds the potion will last");
			}
		}
	}

	public static String[] getDefaultConfigPotionMap() {
		List<String> potionMap = Lists.newArrayList();

		String mobIngredient = Reference.MOD_ID + ":" + Names.Items.MOB_INGREDIENT;

		//TIER ONE INGREDIENTS, these are always 0 potency and have minimal durations (3 for positive, 1 for negative or super-positive)
		addPotionIngredient(potionMap, Items.SUGAR, speed(3, 0), haste(3, 0));
		addPotionIngredient(potionMap, Items.APPLE, heal(0), hboost(3, 0), cure(0));
		addPotionIngredient(potionMap, Items.COAL, blind(1), absorb(3, 0));
		addPotionIngredient(potionMap, Items.COAL, 1, invis(1), wither(0, 0));
		addPotionIngredient(potionMap, Items.FEATHER, jump(3, 0), weak(1, 0));
		addPotionIngredient(potionMap, Items.WHEAT_SEEDS, harm(0), hboost(3, 0));
		addPotionIngredient(potionMap, Items.WHEAT, heal(0), hboost(3, 0));
		addPotionIngredient(potionMap, Items.FLINT, harm(0), dboost(3, 0));
		addPotionIngredient(potionMap, Items.PORKCHOP, slow(1, 0), fatigue(1, 0));
		addPotionIngredient(potionMap, Items.LEATHER, resist(3, 0), absorb(3, 0));
		addPotionIngredient(potionMap, Items.CLAY_BALL, slow(1, 0), hboost(3, 0));
		addPotionIngredient(potionMap, Items.EGG, absorb(3, 0), regen(0, 0));
		addPotionIngredient(potionMap, Items.DYE, Reference.RED_DYE_META, heal(0), hboost(3, 0)); //rose red
		addPotionIngredient(potionMap, Items.DYE, Reference.YELLOW_DYE_META, jump(3, 0), weak(1, 0)); //dandellion yellow
		addPotionIngredient(potionMap, Items.DYE, Reference.GREEN_DYE_META, resist(3, 0), absorb(3, 0)); //cactus green
		addPotionIngredient(potionMap, Items.DYE, Reference.WHITE_DYE_META, weak(1, 0), fatigue(1, 0)); //bone meal
		addPotionIngredient(potionMap, Items.PUMPKIN_SEEDS, invis(1), fireres(1));
		addPotionIngredient(potionMap, Items.BEEF, slow(1, 0), satur(0));
		addPotionIngredient(potionMap, Items.CHICKEN, nausea(1), poison(1));
		addPotionIngredient(potionMap, Items.ROTTEN_FLESH, nausea(1), hunger(1), wither(0, 0));
		addPotionIngredient(potionMap, Items.GOLD_NUGGET, dboost(0, 0), haste(0, 0));
		addPotionIngredient(potionMap, Items.CARROT, vision(3), hboost(3, 0));
		addPotionIngredient(potionMap, Items.POTATO, hboost(3, 0), satur(0));
		addPotionIngredient(potionMap, Items.FISH, satur(0), breath(1));

		//TIER TWO INGREDIENTS, one of the effects of each will always be a one, slightly increased duration vs. TIER ONE
		addPotionIngredient(potionMap, Items.SPIDER_EYE, vision(4), poison(2));
		addPotionIngredient(potionMap, Items.BLAZE_POWDER, dboost(4, 0), harm(0));
		addPotionIngredient(potionMap, Items.IRON_INGOT, resist(4, 0), slow(2, 0));
		addPotionIngredient(potionMap, Items.STRING, slow(2, 0), fatigue(2, 0));
		addPotionIngredient(potionMap, Items.BREAD, hboost(4, 0), satur(0));
		addPotionIngredient(potionMap, Items.COOKED_PORKCHOP, fatigue(2, 0), satur(0));
		addPotionIngredient(potionMap, Items.SLIME_BALL, resist(4, 0), fireres(2));
		addPotionIngredient(potionMap, Items.COOKED_FISH, satur(0), breath(2));
		addPotionIngredient(potionMap, Items.DYE, Reference.BLUE_DYE_META, haste(4, 0), dboost(4, 0));  //lapis lazuli
		addPotionIngredient(potionMap, Items.DYE, Reference.BLACK_DYE_META, blind(2), invis(2)); //ink
		addPotionIngredient(potionMap, Items.BONE, weak(2, 0), fatigue(2, 0));
		addPotionIngredient(potionMap, Items.COOKIE, heal(0), satur(0));
		addPotionIngredient(potionMap, Items.MELON, heal(0), speed(4, 0));
		addPotionIngredient(potionMap, Items.COOKED_BEEF, resist(4, 0), satur(0));
		addPotionIngredient(potionMap, Items.COOKED_CHICKEN, jump(4, 0), satur(0));
		addPotionIngredient(potionMap, Items.BAKED_POTATO, satur(0), regen(1, 0));
		addPotionIngredient(potionMap, Items.POISONOUS_POTATO, poison(2), wither(1, 0));
		addPotionIngredient(potionMap, Items.QUARTZ, harm(0), dboost(4, 0));
		addPotionIngredient(potionMap, mobIngredient, Reference.ZOMBIE_INGREDIENT_META, nausea(2), hunger(2), wither(1, 0));
		addPotionIngredient(potionMap, mobIngredient, Reference.SQUID_INGREDIENT_META, hunger(2), breath(2));

		//TIER THREE INGREDIENTS, these are closer to vanilla durations, carry many effects or a slightly increased duration. Some/most are combos.
		addPotionIngredient(potionMap, Items.PUMPKIN_PIE, invis(1), fireres(1), speed(3, 0), haste(3, 0), absorb(3, 0), regen(0, 0)); //combination of ingredients, strong.
		addPotionIngredient(potionMap, Items.MAGMA_CREAM, dboost(4, 0), harm(0), resist(4, 0), fireres(2)); //also a combo, strong.
		addPotionIngredient(potionMap, Items.SPECKLED_MELON, dboost(3, 0), haste(3, 0), heal(0), speed(4, 0)); //combo
		addPotionIngredient(potionMap, Items.GHAST_TEAR, regen(3, 0), absorb(5, 0));
		addPotionIngredient(potionMap, Items.FERMENTED_SPIDER_EYE, vision(4), poison(2), speed(3, 0), haste(3, 0)); //combo
		addPotionIngredient(potionMap, Items.GOLDEN_CARROT, dboost(3, 0), haste(3, 0), hboost(3, 0), vision(3)); //combo
		addPotionIngredient(potionMap, Items.GOLD_INGOT, dboost(4, 0), haste(4, 0), cure(0)); //combo
		addPotionIngredient(potionMap, mobIngredient, Reference.SKELETON_INGREDIENT_META, weak(3, 0), fatigue(3, 0), cure(0));
		addPotionIngredient(potionMap, Items.ENDER_PEARL, invis(5), speed(5, 0));
		addPotionIngredient(potionMap, Items.BLAZE_ROD, dboost(8, 0), harm(0));
		addPotionIngredient(potionMap, Items.FIRE_CHARGE, dboost(4, 0), harm(0), blind(1), absorb(3, 0)); //combo
		addPotionIngredient(potionMap, mobIngredient, Reference.CREEPER_INGREDIENT_META, regen(3, 0), hboost(5, 0));
		addPotionIngredient(potionMap, mobIngredient, Reference.SPIDER_INGREDIENT_META, poison(3), weak(3, 0));
		addPotionIngredient(potionMap, mobIngredient, Reference.SLIME_INGREDIENT_META, resist(5, 0), absorb(5, 0));
		addPotionIngredient(potionMap, mobIngredient, Reference.SHELL_INGREDIENT_META, absorb(5, 0), breath(5));
		addPotionIngredient(potionMap, mobIngredient, Reference.BAT_INGREDIENT_META, jump(5, 0), weak(3, 0));
		addPotionIngredient(potionMap, Items.GOLDEN_APPLE, cure(1));
		addPotionIngredient(potionMap, Items.GOLDEN_APPLE, 1, cure(2));

		//TIER FOUR INGREDIENTS, these carry multiple one-potency effects and have the most duration for any given effect.
		addPotionIngredient(potionMap, Items.DIAMOND, resist(6, 1), absorb(6, 1), fireres(6), cure(0));
		addPotionIngredient(potionMap, mobIngredient, Reference.WITHER_INGREDIENT_META, wither(2, 1), weak(3, 1), slow(3, 1), fatigue(3, 1), cure(0));
		addPotionIngredient(potionMap, Items.ENDER_EYE, dboost(6, 1), invis(6), speed(6, 1), harm(1));
		addPotionIngredient(potionMap, Items.EMERALD, haste(6, 1), speed(6, 1), hboost(6, 1), cure(1));
		addPotionIngredient(potionMap, Items.NETHER_STAR, hboost(24, 1), regen(24, 1), absorb(24, 1), cure(2)); //nether star is holy stonk
		addPotionIngredient(potionMap, mobIngredient, Reference.MOLTEN_INGREDIENT_META, dboost(6, 1), fireres(6), harm(1));
		addPotionIngredient(potionMap, mobIngredient, Reference.STORM_INGREDIENT_META, haste(24, 1), speed(24, 1), jump(24, 1), harm(1), cure(1));
		addPotionIngredient(potionMap, mobIngredient, Reference.FERTILE_INGREDIENT_META, hboost(8, 1), regen(3, 1), heal(1), satur(1), weak(9, 1), fatigue(9, 1), cure(0));
		addPotionIngredient(potionMap, mobIngredient, Reference.FROZEN_INGREDIENT_META, absorb(6, 1), slow(3, 1), fatigue(3, 1), harm(1), fireres(6));
		addPotionIngredient(potionMap, mobIngredient, Reference.ENDER_INGREDIENT_META, vision(6), invis(6), harm(1), hboost(6, 1), dboost(6, 1), speed(6, 1), haste(6, 1));
		addPotionIngredient(potionMap, mobIngredient, Reference.CLAW_INGREDIENT_META, harm(1), resist(6, 1), fireres(6), dboost(6, 1), satur(1), heal(1));

		return potionMap.toArray(new String[potionMap.size()]);
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
		addPotionIngredient(potionMap, ingredient, 0, effects);
	}

	private static void addPotionIngredient(List<String> potionMap, Item ingredient, int meta, String... effects) {
		//noinspection ConstantConditions
		addPotionIngredient(potionMap, ingredient.getRegistryName().toString(), meta, effects);
	}
	private static void addPotionIngredient(List<String> potionMap, String itemRegistryName, int meta, String... effects) {
		StringJoiner effectsString = new StringJoiner(";");
		Arrays.stream(effects).forEach(effectsString::add);

		potionMap.add(String.format("%s=%s", String.format("%s|%d", itemRegistryName, meta), effectsString.toString()));
	}
}
