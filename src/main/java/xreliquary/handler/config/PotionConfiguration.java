package xreliquary.handler.config;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;
import xreliquary.handler.ConfigurationHandler;
import xreliquary.init.XRRecipes;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;
import xreliquary.util.LogHelper;
import xreliquary.util.StackHelper;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.PotionEssenceComparator;
import xreliquary.util.potions.PotionIngredient;
import xreliquary.util.potions.XRPotionHelper;

import java.util.List;
import java.util.Map;

public class PotionConfiguration {
	public static void loadPotionMap() {
		ConfigCategory category = ConfigurationHandler.configuration.getCategory(Names.potion_map);

		if(category.isEmpty()) {
			addDefaultPotionMap(category);
		}

		loadPotionMapIntoSettings(category);

		LogHelper.debug("Starting calculation of potion combinations");
		loadPotionCombinations();
		loadUniquePotions();
		LogHelper.debug("Done with potion combinations");

		ConfigurationHandler.setCategoryTranslations(Names.potion_map, true);
	}

	private static void loadUniquePotions() {
		Settings.Potions.uniquePotionEssences.clear();
		Settings.Potions.uniquePotions.clear();

		for(PotionEssence essence : Settings.Potions.potionCombinations) {
			boolean found = false;
			for(PotionEssence uniqueEssence : Settings.Potions.uniquePotionEssences) {
				if(effectsEqual(essence.effects, uniqueEssence.effects)) {
					found = true;
					break;
				}
			}
			if(!found) {
				Settings.Potions.uniquePotionEssences.add(essence);
				addUniquePotions(essence);
			}
		}

		Settings.Potions.uniquePotionEssences.sort(new PotionEssenceComparator());
		Settings.Potions.uniquePotions.sort(new PotionEssenceComparator());
	}

	private static void addUniquePotions(PotionEssence essence) {
		Settings.Potions.uniquePotions.add(essence);

		if(Settings.Potions.redstoneAndGlowstone) {
			PotionEssence redstone = new PotionEssence(essence.writeToNBT());
			redstone.addRedstone(1);
			Settings.Potions.uniquePotions.add(redstone);

			PotionEssence glowstone = new PotionEssence(essence.writeToNBT());
			glowstone.addGlowstone(1);
			Settings.Potions.uniquePotions.add(glowstone);

			PotionEssence redstoneGlowstone = new PotionEssence(essence.writeToNBT());
			redstoneGlowstone.addRedstone(1);
			redstoneGlowstone.addGlowstone(1);
			Settings.Potions.uniquePotions.add(redstoneGlowstone);
		}

	}

	private static void loadPotionCombinations() {
		Settings.Potions.potionCombinations.clear();

		//multiple effect potions and potions made of 3 ingredients are turned on by config option
		for(PotionIngredient ingredient1 : Settings.Potions.potionMap) {
			for(PotionIngredient ingredient2 : Settings.Potions.potionMap) {
				if(ingredient1.item.getItem() != ingredient2.item.getItem() || ingredient1.item.getMetadata() != ingredient2.item.getMetadata()) {
					PotionEssence twoEssence = new PotionEssence(new PotionIngredient[] {ingredient1, ingredient2});
					if(twoEssence.effects.size() > 0 && twoEssence.effects.size() <= Settings.Potions.maxEffectCount) {
						addPotionCombination(twoEssence);

						if(Settings.Potions.threeIngredients) {
							for(PotionIngredient ingredient3 : Settings.Potions.potionMap) {
								if((ingredient3.item.getItem() != ingredient1.item.getItem() || ingredient3.item.getMetadata() != ingredient1.item.getMetadata()) && (ingredient3.item.getItem() != ingredient2.item.getItem() || ingredient3.item.getMetadata() != ingredient2.item.getMetadata())) {
									PotionEssence threeEssence = new PotionEssence(new PotionIngredient[] {ingredient1, ingredient2, ingredient3});

									if(!effectsEqual(twoEssence.effects, threeEssence.effects)) {
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
		for(PotionEssence essence : Settings.Potions.potionCombinations) {
			//exactly same ingredients in a different order are not to be added here
			if(ingredientsEqual(essence.ingredients, newEssence.ingredients)) {
				return;
			}
			//the same effect potion id with different duration is turned on by config option
			if(effectsEqual(essence.effects, newEssence.effects, Settings.Potions.differentDurations, true) && !effectsEqual(essence.effects, newEssence.effects)) {
				return;
			}
		}

		Settings.Potions.potionCombinations.add(newEssence);
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
		return effectsEqual(a, b, true, true);
	}

	private static boolean effectsEqual(List<PotionEffect> a, List<PotionEffect> b, boolean compareDuration, boolean comparePotency) {
		if(a.size() != b.size())
			return false;

		for(PotionEffect effectA : a) {
			boolean found = false;
			for(PotionEffect effectB : b) {//TODO verify that getEffectName comparison is the same as getPotionId previously
				if(effectA.getEffectName().equals(effectB.getEffectName()) && (!compareDuration || effectA.getDuration() == effectB.getDuration()) && (!comparePotency || effectA.getAmplifier() == effectB.getAmplifier())) {
					found = true;
					break;
				}
			}
			if(!found)
				return false;
		}
		return true;
	}

	private static void loadPotionMapIntoSettings(ConfigCategory category) {
		Settings.Potions.potionMap.clear();

		for(Map.Entry<String, Property> entry : category.getValues().entrySet()) {
			String[] nameParts = entry.getKey().split("\\|");
			String[] effects = entry.getValue().getStringList();

			String modId = nameParts[0].split(":")[0];
			String name = nameParts[0].split(":")[1];
			int meta = Integer.parseInt(nameParts[1]);

			ItemStack stack = StackHelper.getItemStackFromNameMeta(modId, name, meta);

			if(stack != null) {
				PotionIngredient ingredient = new PotionIngredient(stack);
				for(int i = 0; i < effects.length; i++) {
					String[] effectValues = effects[i].split("\\|");
					int potionId = XRPotionHelper.getPotionIdByName(effectValues[0]);
					if(potionId > 0) {
						short durationWeight = Short.parseShort(effectValues[1]);
						short ampWeight = Short.parseShort(effectValues[2]);
						ingredient.addEffect(potionId, durationWeight, ampWeight);
					}
				}
				if(ingredient.effects.size() > 0) {
					Settings.Potions.potionMap.add(ingredient);
				}
			}
		}
	}

	private static void addDefaultPotionMap(ConfigCategory category) {
		//TIER ONE INGREDIENTS, these are always 0 potency and have minimal durations (3 for positive, 1 for negative or super-positive)
		addPotionConfig(category, Items.SUGAR, speed(3, 0), haste(3, 0));
		addPotionConfig(category, Items.APPLE, heal(0), hboost(3, 0));
		addPotionConfig(category, Items.COAL, blind(1, 0), absorb(3, 0));
		addPotionConfig(category, Items.COAL, 1, invis(1, 0), wither(0, 0));
		addPotionConfig(category, Items.FEATHER, jump(3, 0), weak(1, 0));
		addPotionConfig(category, Items.WHEAT_SEEDS, harm(0), hboost(3, 0));
		addPotionConfig(category, Items.WHEAT, heal(0), hboost(3, 0));
		addPotionConfig(category, Items.FLINT, harm(0), dboost(3, 0));
		addPotionConfig(category, Items.PORKCHOP, slow(1, 0), fatigue(1, 0));
		addPotionConfig(category, Items.LEATHER, resist(3, 0), absorb(3, 0));
		addPotionConfig(category, Items.CLAY_BALL, slow(1, 0), hboost(3, 0));
		addPotionConfig(category, Items.EGG, absorb(3, 0), regen(0, 0));
		addPotionConfig(category, Items.DYE, Reference.RED_DYE_META, heal(0), hboost(3, 0)); //rose red
		addPotionConfig(category, Items.DYE, Reference.YELLOW_DYE_META, jump(3, 0), weak(1, 0)); //dandellion yellow
		addPotionConfig(category, Items.DYE, Reference.GREEN_DYE_META, resist(3, 0), absorb(3, 0)); //cactus green
		addPotionConfig(category, Items.DYE, Reference.WHITE_DYE_META, weak(1, 0), fatigue(1, 0)); //bone meal
		addPotionConfig(category, Items.PUMPKIN_SEEDS, invis(1, 0), fireres(1, 0));
		addPotionConfig(category, Items.BEEF, slow(1, 0), satur(0));
		addPotionConfig(category, Items.CHICKEN, nausea(1, 0), poison(1, 0));
		addPotionConfig(category, Items.ROTTEN_FLESH, nausea(1, 0), hunger(1, 0), wither(0, 0));
		addPotionConfig(category, Items.GOLD_NUGGET, dboost(0, 0), haste(0, 0));
		addPotionConfig(category, Items.CARROT, vision(3, 0), hboost(3, 0));
		addPotionConfig(category, Items.POTATO, hboost(3, 0), satur(0));
		addPotionConfig(category, Items.FISH, satur(0), breath(1, 0));

		//TIER TWO INGREDIENTS, one of the effects of each will always be a one, slightly increased duration vs. TIER ONE
		addPotionConfig(category, Items.SPIDER_EYE, vision(4, 0), poison(2, 0));
		addPotionConfig(category, Items.BLAZE_POWDER, dboost(4, 0), harm(0));
		addPotionConfig(category, Items.IRON_INGOT, resist(4, 0), slow(2, 0));
		addPotionConfig(category, Items.STRING, slow(2, 0), fatigue(2, 0));
		addPotionConfig(category, Items.BREAD, hboost(4, 0), satur(0));
		addPotionConfig(category, Items.COOKED_PORKCHOP, fatigue(2, 0), satur(0));
		addPotionConfig(category, Items.SLIME_BALL, resist(4, 0), fireres(2, 0));
		addPotionConfig(category, Items.COOKED_FISH, satur(0), breath(2, 0));
		addPotionConfig(category, Items.DYE, Reference.BLUE_DYE_META, haste(4, 0), dboost(4, 0));  //lapis lazuli
		addPotionConfig(category, Items.DYE, Reference.BLACK_DYE_META, blind(2, 0), invis(2, 0)); //ink
		addPotionConfig(category, Items.BONE, weak(2, 0), fatigue(2, 0));
		addPotionConfig(category, Items.COOKIE, heal(0), satur(0));
		addPotionConfig(category, Items.MELON, heal(0), speed(4, 0));
		addPotionConfig(category, Items.COOKED_BEEF, resist(4, 0), satur(0));
		addPotionConfig(category, Items.COOKED_CHICKEN, jump(4, 0), satur(0));
		addPotionConfig(category, Items.BAKED_POTATO, satur(0), regen(1, 0));
		addPotionConfig(category, Items.POISONOUS_POTATO, poison(2, 0), wither(1, 0));
		addPotionConfig(category, Items.QUARTZ, harm(0), dboost(4, 0));
		addPotionConfig(category, XRRecipes.ZOMBIE_HEART, nausea(2, 0), hunger(2, 0), wither(1, 0));
		addPotionConfig(category, XRRecipes.SQUID_BEAK, hunger(2, 0), breath(2, 0));

		//TIER THREE INGREDIENTS, these are closer to vanilla durations, carry many effects or a slightly increased duration. Some/most are combos.
		addPotionConfig(category, Items.PUMPKIN_PIE, invis(1, 0), fireres(1, 0), speed(3, 0), haste(3, 0), absorb(3, 0), regen(0, 0)); //combination of ingredients, strong.
		addPotionConfig(category, Items.MAGMA_CREAM, dboost(4, 0), harm(0), resist(4, 0), fireres(2, 0)); //also a combo, strong.
		addPotionConfig(category, Items.SPECKLED_MELON, dboost(3, 0), haste(3, 0), heal(0), speed(4, 0)); //combo
		addPotionConfig(category, Items.GHAST_TEAR, regen(3, 0), absorb(5, 0));
		addPotionConfig(category, Items.FERMENTED_SPIDER_EYE, vision(4, 0), poison(2, 0), speed(3, 0), haste(3, 0)); //combo
		addPotionConfig(category, Items.GOLDEN_CARROT, dboost(3, 0), haste(3, 0), hboost(3, 0), vision(3, 0)); //combo
		addPotionConfig(category, Items.GOLD_INGOT, dboost(4, 0), haste(4, 0)); //combo
		addPotionConfig(category, XRRecipes.RIB_BONE, weak(3, 0), fatigue(3, 0));
		addPotionConfig(category, Items.ENDER_PEARL, invis(5, 0), speed(5, 0));
		addPotionConfig(category, Items.BLAZE_ROD, dboost(8, 0), harm(0));
		addPotionConfig(category, Items.FIRE_CHARGE, dboost(4, 0), harm(0), blind(1, 0), absorb(3, 0)); //combo
		addPotionConfig(category, XRRecipes.CREEPER_GLAND, regen(3, 0), hboost(5, 0));
		addPotionConfig(category, XRRecipes.CHELICERAE, poison(3, 0), weak(3, 0));
		addPotionConfig(category, XRRecipes.SLIME_PEARL, resist(5, 0), absorb(5, 0));
		addPotionConfig(category, XRRecipes.SHELL_FRAGMENT, absorb(5, 0), breath(5, 0));
		addPotionConfig(category, XRRecipes.BAT_WING, jump(5, 0), weak(3, 0));

		//TIER FOUR INGREDIENTS, these carry multiple one-potency effects and have the most duration for any given effect.
		addPotionConfig(category, Items.DIAMOND, resist(6, 1), absorb(6, 1), fireres(6, 0));
		addPotionConfig(category, XRRecipes.WITHER_RIB, wither(2, 1), weak(3, 1), slow(3, 1), fatigue(3, 1));
		addPotionConfig(category, Items.ENDER_EYE, dboost(6, 1), invis(6, 0), speed(6, 1), harm(1));
		addPotionConfig(category, Items.EMERALD, haste(6, 1), speed(6, 1), hboost(6, 1));
		addPotionConfig(category, Items.NETHER_STAR, hboost(24, 1), regen(24, 1), absorb(24, 1)); //nether star is holy stonk
		addPotionConfig(category, XRRecipes.MOLTEN_CORE, dboost(6, 1), fireres(6, 0), harm(1));
		addPotionConfig(category, XRRecipes.STORM_EYE, haste(24, 1), speed(24, 1), jump(24, 1), harm(1));
		addPotionConfig(category, XRRecipes.FERTILE_ESSENCE, hboost(8, 1), regen(3, 1), heal(1), satur(1), weak(9, 1), fatigue(9, 1));
		addPotionConfig(category, XRRecipes.FROZEN_CORE, absorb(6, 1), slow(3, 1), fatigue(3, 1), harm(1), fireres(6, 0));
		addPotionConfig(category, XRRecipes.NEBULOUS_HEART, vision(6, 0), invis(6, 0), harm(1), hboost(6, 1), dboost(6, 1), speed(6, 1), haste(6, 1));
		addPotionConfig(category, XRRecipes.INFERNAL_CLAW, harm(1), resist(6, 1), fireres(6, 0), dboost(6, 1), satur(1), heal(1));
	}

	public static String harm(int potency) {
		return effectString(Reference.HARM, Integer.toString(0), Integer.toString(potency));
	}

	public static String heal(int potency) {
		return effectString(Reference.HEAL, Integer.toString(0), Integer.toString(potency));
	}

	public static String satur(int potency) {
		return effectString(Reference.SATURATION, Integer.toString(0), Integer.toString(potency));
	}

	public static String invis(int duration, int potency) {
		return effectString(Reference.INVIS, Integer.toString(duration), Integer.toString(potency));
	}

	public static String absorb(int duration, int potency) {
		return effectString(Reference.ABSORB, Integer.toString(duration), Integer.toString(potency));
	}

	public static String hboost(int duration, int potency) {
		return effectString(Reference.HBOOST, Integer.toString(duration), Integer.toString(potency));
	}

	public static String dboost(int duration, int potency) {
		return effectString(Reference.DBOOST, Integer.toString(duration), Integer.toString(potency));
	}

	public static String speed(int duration, int potency) {
		return effectString(Reference.SPEED, Integer.toString(duration), Integer.toString(potency));
	}

	public static String haste(int duration, int potency) {
		return effectString(Reference.HASTE, Integer.toString(duration), Integer.toString(potency));
	}

	public static String slow(int duration, int potency) {
		return effectString(Reference.SLOW, Integer.toString(duration), Integer.toString(potency));
	}

	public static String fatigue(int duration, int potency) {
		return effectString(Reference.FATIGUE, Integer.toString(duration), Integer.toString(potency));
	}

	public static String breath(int duration, int potency) {
		return effectString(Reference.BREATH, Integer.toString(duration), Integer.toString(potency));
	}

	public static String vision(int duration, int potency) {
		return effectString(Reference.VISION, Integer.toString(duration), Integer.toString(potency));
	}

	public static String resist(int duration, int potency) {
		return effectString(Reference.RESIST, Integer.toString(duration), Integer.toString(potency));
	}

	public static String fireres(int duration, int potency) {
		return effectString(Reference.FRESIST, Integer.toString(duration), Integer.toString(potency));
	}

	public static String weak(int duration, int potency) {
		return effectString(Reference.WEAK, Integer.toString(duration), Integer.toString(potency));
	}

	public static String jump(int duration, int potency) {
		return effectString(Reference.JUMP, Integer.toString(duration), Integer.toString(potency));
	}

	public static String nausea(int duration, int potency) {
		return effectString(Reference.NAUSEA, Integer.toString(duration), Integer.toString(potency));
	}

	public static String hunger(int duration, int potency) {
		return effectString(Reference.HUNGER, Integer.toString(duration), Integer.toString(potency));
	}

	public static String regen(int duration, int potency) {
		return effectString(Reference.REGEN, Integer.toString(duration), Integer.toString(potency));
	}

	public static String poison(int duration, int potency) {
		return effectString(Reference.POISON, Integer.toString(duration), Integer.toString(potency));
	}

	public static String wither(int duration, int potency) {
		return effectString(Reference.WITHER, Integer.toString(duration), Integer.toString(potency));
	}

	public static String blind(int duration, int potency) {
		return effectString(Reference.BLIND, Integer.toString(duration), Integer.toString(potency));
	}

	public static String effectString(String name, String duration, String potency) {
		return name + "|" + duration + "|" + potency;
	}

	private static void addPotionConfig(ConfigCategory category, ItemStack ingredient, String... effects) {
		addPotionConfig(category, ingredient.getItem(), ingredient.getMetadata(), effects);
	}

	private static void addPotionConfig(ConfigCategory category, Item ingredient, String... effects) {
		addPotionConfig(category, ingredient, 0, effects);
	}

	private static void addPotionConfig(ConfigCategory category, Item ingredient, int meta, String... effects) {
		Property prop = new Property(String.format("%s|%d", ingredient.getRegistryName(), meta), effects, Property.Type.STRING);

		category.put(prop.getName(), prop);
	}
}
