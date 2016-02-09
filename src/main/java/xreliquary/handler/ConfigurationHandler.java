package xreliquary.handler;


import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IProjectile;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;
import xreliquary.init.XRRecipes;
import xreliquary.items.ItemDestructionCatalyst;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;
import xreliquary.util.LogHelper;
import xreliquary.util.alkahestry.AlkahestChargeRecipe;
import xreliquary.util.alkahestry.AlkahestCraftRecipe;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.PotionIngredient;
import xreliquary.util.potions.XRPotionHelper;

import java.io.File;
import java.util.*;


public class ConfigurationHandler
{
	//TODO: extract individual config parts into separate classes eg. /config/PotionMapConfig
	private static final int TOME_COST_LOW_TIER = 4;
	private static final int TOME_COST_MIDDLE_TIER = 8;
	private static final int TOME_COST_HIGH_TIER = 32;
	private static final int TOME_COST_UBER_TIER = 64;


	public static Configuration configuration;

	public static void init(File configFile)
	{
		if (configuration == null)
		{
			configuration = new Configuration(configFile, true);
			loadConfiguration();
		}
	}

	private static void loadConfiguration()
	{
		loadHudPositions();
		loadEasyModeSettings();
		loadMobDropProbabilities();
		loadBlockAndItemSettings();
	}

	public static void loadPotionMap() {
		ConfigCategory category = configuration.getCategory(Names.potion_map);

		if (category.isEmpty()) {
			addDefaultPotionMap(category);
		}

		loadPotionMapIntoSettings(category);

		LogHelper.debug("Starting calculation of potion combinations");
		loadPotionCombinations();
		List<PotionEssence> unique = getUniquePotionIdCombinations();
		LogHelper.debug("Done with potion combinations");

		setCategoryTranslations(Names.potion_map, true);
	}

	private static void loadPotionCombinations() {
		Settings.potionCombinations.clear();

		for(PotionIngredient ingredient1 : Settings.potionMap) {
			for(PotionIngredient ingredient2 : Settings.potionMap) {
				if (ingredient1.item.getItem() != ingredient2.item.getItem()) {
					PotionEssence twoEssence = new PotionEssence(new PotionIngredient[] {ingredient1, ingredient2});
					if (twoEssence.effects.size()>0) {
						addPotionCombination(twoEssence);
/*
						for(PotionIngredient ingredient3 : Settings.potionMap) {
							if(ingredient3.item.getItem() != ingredient1.item.getItem() && ingredient3.item.getItem() != ingredient2.item.getItem()) {
								PotionEssence threeEssence = new PotionEssence(new PotionIngredient[]{ingredient1, ingredient2, ingredient3});
								if (!effectsEqual(threeEssence.effects, twoEssence.effects)) {
									addPotionCombination(threeEssence);
								}
							}
						}
*/
					}
				}
			}
		}
	}

	private static List<PotionEssence> getUniquePotionIdCombinations() {
		ArrayList<PotionEssence> unique = new ArrayList<>();

		for (PotionEssence essence : Settings.potionCombinations) {
			boolean found = false;
			for(PotionEssence included : unique) {
				if (potionIdsEqual(essence.effects, included.effects)) {
					found = true;
					break;
				}
			}
			if (!found)
				unique.add(essence);
		}
		return unique;
	}

	private static boolean potionIdsEqual(List<PotionEffect> a, List<PotionEffect> b) {

		if (a.size() != b.size())
			return false;

		for(PotionEffect effectA : a) {
			boolean found = false;
			for(PotionEffect effectB:b) {
				if(effectA.getPotionID() == effectB.getPotionID()) {
					found = true;
					break;
				}
			}
			if (!found)
				return false;
		}
		return true;
	}

	private static void addPotionCombination(PotionEssence newEssence) {
/*
		for (PotionEssence essence: Settings.potionCombinations) {
			if(effectsEqual(essence.effects, newEssence.effects)) {
				return;
			}
		}
*/
		for (PotionEssence essence: Settings.potionCombinations) {
			if(ingredientsEqual(essence.ingredients, newEssence.ingredients)) {
				return;
			}
		}
		Settings.potionCombinations.add(newEssence);
	}

	private static boolean ingredientsEqual(List<PotionIngredient> a, List<PotionIngredient> b) {
		if (a.size() != b.size())
			return false;
		for (PotionIngredient ingredientA:a) {
			boolean found = false;
			for(PotionIngredient ingredientB:b) {
				if(ingredientA.item.getItem() == ingredientB.item.getItem()
						&& ingredientA.item.getMetadata() == ingredientB.item.getMetadata()) {
					found = true;
					break;
				}
			}
			if (!found)
				return false;
		}
		return true;
	}

	private static boolean effectsEqual(List<PotionEffect> a, List<PotionEffect> b) {
		if(a.size() != b.size())
			return false;

		for (PotionEffect effectA:a) {
			boolean found = false;
			for(PotionEffect effectB:b) {
				if(effectA.getPotionID() == effectB.getPotionID()
						&& effectA.getDuration() == effectB.getDuration()
						&& effectA.getAmplifier() == effectB.getAmplifier()) {
					found = true;
					break;
				}
			}
			if (!found)
				return false;
		}
		return true;
	}

	private static void loadPotionMapIntoSettings(ConfigCategory category) {
		Settings.potionMap.clear();

		for(Map.Entry<String, Property> entry: category.getValues().entrySet()) {
			String[] nameParts = entry.getKey().split("\\|");
			String[] effects = entry.getValue().getStringList();

			String modId = nameParts[0].split(":")[0];
			String name = nameParts[0].split(":")[1];
			int meta = Integer.parseInt(nameParts[1]);

			ItemStack stack = getItemStackFromNameMeta(modId, name, meta);

			if (stack != null) {
				PotionIngredient ingredient = new PotionIngredient(stack);
				for (int i=0; i<effects.length; i++) {
					String[] effectValues = effects[i].split("\\|");
					int potionId = XRPotionHelper.getPotionIdByName(effectValues[0]);
					if (potionId > 0) {
						short durationWeight = Short.parseShort(effectValues[1]);
						short ampWeight = Short.parseShort(effectValues[2]);
						ingredient.addEffect(potionId, durationWeight, ampWeight);
					}
				}
				if (ingredient.effects.size() > 0) {
					Settings.potionMap.add(ingredient);
				}
			}
		}
	}

	private static void addDefaultPotionMap(ConfigCategory category) {
		//TIER ONE INGREDIENTS, these are always 0 potency and have minimal durations (3 for positive, 1 for negative or super-positive)
		addPotionConfig(category, Items.sugar, speed(3, 0), haste(3, 0));
		addPotionConfig(category, Items.apple,heal(0), hboost(3, 0));
		addPotionConfig(category, Items.coal, blind(1, 0), absorb(3, 0));
		addPotionConfig(category, Items.coal, 1, invis(1, 0), wither(0, 0));
		addPotionConfig(category, Items.feather, jump(3, 0), weak(1, 0));
		addPotionConfig(category, Items.wheat_seeds, harm(0), hboost(3, 0));
		addPotionConfig(category, Items.wheat,heal(0), hboost(3, 0));
		addPotionConfig(category, Items.flint, harm(0), dboost(3, 0));
		addPotionConfig(category, Items.porkchop,slow(1, 0), fatigue(1, 0));
		addPotionConfig(category, Items.leather,resist(3, 0), absorb(3, 0));
		addPotionConfig(category, Items.clay_ball,slow(1, 0), hboost(3, 0));
		addPotionConfig(category, Items.egg,absorb(3, 0), regen(0, 0));
		addPotionConfig(category, Items.dye, Reference.RED_DYE_META, heal(0), hboost(3, 0)); //rose red
		addPotionConfig(category, Items.dye, Reference.YELLOW_DYE_META,jump(3, 0), weak(1, 0)); //dandellion yellow
		addPotionConfig(category, Items.dye, Reference.GREEN_DYE_META,resist(3, 0), absorb(3, 0)); //cactus green
		addPotionConfig(category, Items.dye, Reference.WHITE_DYE_META, weak(1, 0), fatigue(1, 0)); //bone meal
		addPotionConfig(category, Items.pumpkin_seeds,invis(1, 0), fireres(1,0));
		addPotionConfig(category, Items.beef,slow(1,0), satur(0));
		addPotionConfig(category, Items.chicken,nausea(1, 0), poison(1, 0));
		addPotionConfig(category, Items.rotten_flesh,nausea(1, 0), hunger(1, 0), wither(0, 0));
		addPotionConfig(category, Items.gold_nugget, dboost(0, 0), haste(0, 0));
		addPotionConfig(category, Items.carrot,vision(3, 0), hboost(3, 0));
		addPotionConfig(category, Items.potato,hboost(3, 0), satur(0));
		addPotionConfig(category, Items.fish, satur(0), breath(1, 0));

		//TIER TWO INGREDIENTS, one of the effects of each will always be a one, slightly increased duration vs. TIER ONE
		addPotionConfig(category, Items.spider_eye, vision(4, 0), poison(2, 0));
		addPotionConfig(category, Items.blaze_powder, dboost(4, 0), harm(0));
		addPotionConfig(category, Items.iron_ingot, resist(4, 0), slow(2, 0));
		addPotionConfig(category, Items.string, slow(2, 0), fatigue(2, 0));
		addPotionConfig(category, Items.bread, hboost(4, 0), satur(0));
		addPotionConfig(category, Items.cooked_porkchop, fatigue(2, 0), satur(0));
		addPotionConfig(category, Items.slime_ball, resist(4, 0), fireres(2, 0));
		addPotionConfig(category, Items.cooked_fish, satur(0), breath(2, 0));
		addPotionConfig(category, Items.dye, Reference.BLUE_DYE_META, haste(4, 0), dboost(4, 0));  //lapis lazuli
		addPotionConfig(category, Items.dye, Reference.BLACK_DYE_META, blind(2, 0), invis(2, 0)); //ink
		addPotionConfig(category, Items.bone, weak(2, 0), fatigue(2, 0));
		addPotionConfig(category, Items.cookie, heal(0), satur(0));
		addPotionConfig(category, Items.melon, heal(0), speed(4, 0));
		addPotionConfig(category, Items.cooked_beef, resist(4, 0), satur(0));
		addPotionConfig(category, Items.cooked_chicken, jump(4, 0), satur(0));
		addPotionConfig(category, Items.baked_potato, satur(0), regen(1, 0));
		addPotionConfig(category, Items.poisonous_potato, poison(2, 0), wither(1, 0));
		addPotionConfig(category, Items.quartz, harm(0), dboost(4, 0));
		addPotionConfig(category, XRRecipes.zombieHeart(), nausea(2, 0), hunger(2, 0), wither(1, 0));
		addPotionConfig(category, XRRecipes.squidBeak(), hunger(2, 0), breath(2, 0));

		//TIER THREE INGREDIENTS, these are closer to vanilla durations, carry many effects or a slightly increased duration. Some/most are combos.
		addPotionConfig(category, Items.pumpkin_pie, invis(1, 0), fireres(1, 0), speed(3, 0), haste(3, 0), absorb(3, 0), regen(0, 0)); //combination of ingredients, strong.
		addPotionConfig(category, Items.magma_cream, dboost(4, 0), harm(0), resist(4, 0), fireres(2, 0)); //also a combo, strong.
		addPotionConfig(category, Items.speckled_melon, dboost(3, 0), haste(3, 0), heal(0), speed(4, 0)); //combo
		addPotionConfig(category, Items.ghast_tear, regen(3, 0), absorb(5, 0));
		addPotionConfig(category, Items.fermented_spider_eye, vision(4, 0), poison(2, 0), speed(3, 0), haste(3, 0)); //combo
		addPotionConfig(category, Items.golden_carrot, dboost(3, 0), haste(3, 0), hboost(3, 0), vision(3, 0)); //combo
		addPotionConfig(category, Items.gold_ingot, dboost(4, 0), haste(4, 0)); //combo
		addPotionConfig(category, XRRecipes.ribBone(), weak(3, 0), fatigue(3, 0));
		addPotionConfig(category, Items.ender_pearl, invis(5, 0), speed(5, 0));
		addPotionConfig(category, Items.blaze_rod, dboost(8, 0), harm(0));
		addPotionConfig(category, Items.fire_charge, dboost(4, 0), harm(0), blind(1, 0), absorb(3, 0)); //combo
		addPotionConfig(category, XRRecipes.creeperGland(), regen(3, 0), hboost(5, 0));
		addPotionConfig(category, XRRecipes.spiderFangs(), poison(3, 0), weak(3, 0));
		addPotionConfig(category, XRRecipes.slimePearl(), resist(5, 0), absorb(5, 0));
		addPotionConfig(category, XRRecipes.shellFragment(), absorb(5, 0), breath(5, 0));
		addPotionConfig(category, XRRecipes.batWing(), jump(5, 0), weak(3, 0));

		//TIER FOUR INGREDIENTS, these carry multiple one-potency effects and have the most duration for any given effect.
		addPotionConfig(category, Items.diamond, resist(6, 1), absorb(6, 1), fireres(6, 0));
		addPotionConfig(category, XRRecipes.witherRib(), wither(2, 1), weak(3, 1), slow(3, 1), fatigue(3, 1));
		addPotionConfig(category, Items.ender_eye, dboost(6, 1), invis(6, 0), speed(6, 1), harm(1));
		addPotionConfig(category, Items.emerald, haste(6, 1), speed(6, 1), hboost(6, 1));
		addPotionConfig(category, Items.nether_star, hboost(24, 1), regen(24, 1), absorb(24, 1)); //nether star is holy stonk
		addPotionConfig(category, XRRecipes.moltenCore(), dboost(6, 1), fireres(6, 0), harm(1));
		addPotionConfig(category, XRRecipes.stormEye(), haste(24, 1), speed(24, 1), jump(24, 1), harm(1));
		addPotionConfig(category, XRRecipes.fertileEssence(), hboost(8, 1), regen(3, 1), heal(1), satur(1), weak(9, 1), fatigue(9, 1));
		addPotionConfig(category, XRRecipes.frozenCore(), absorb(6, 1), slow(3, 1), fatigue(3, 1), harm(1), fireres(6, 0));
		addPotionConfig(category, XRRecipes.enderHeart(), vision(6, 0), invis(6, 0), harm(1), hboost(6, 1), dboost(6, 1), speed(6, 1), haste(6, 1));
		addPotionConfig(category, XRRecipes.infernalClaw(), harm(1), resist(6, 1), fireres(6, 0), dboost(6, 1), satur(1), heal(1));
	}

	public static String harm(int potency) { return effectString(Reference.HARM, Integer.toString(0),Integer.toString(potency)); }
	public static String heal(int potency) { return effectString(Reference.HEAL, Integer.toString(0),Integer.toString(potency)); }
	public static String satur(int potency) { return effectString(Reference.SATURATION, Integer.toString(0),Integer.toString(potency)); }
	public static String invis(int duration, int potency) { return effectString(Reference.INVIS, Integer.toString(duration), Integer.toString(potency)); }
	public static String absorb(int duration, int potency) { return effectString(Reference.ABSORB, Integer.toString(duration),Integer.toString(potency)); }
	public static String hboost(int duration, int potency) { return effectString(Reference.HBOOST, Integer.toString(duration),Integer.toString(potency)); }
	public static String dboost(int duration, int potency) { return effectString(Reference.DBOOST, Integer.toString(duration),Integer.toString(potency)); }
	public static String speed(int duration, int potency) { return effectString(Reference.SPEED, Integer.toString(duration),Integer.toString(potency)); }
	public static String haste(int duration, int potency) { return effectString(Reference.HASTE, Integer.toString(duration),Integer.toString(potency)); }
	public static String slow(int duration, int potency) { return effectString(Reference.SLOW, Integer.toString(duration),Integer.toString(potency)); }
	public static String fatigue(int duration, int potency) { return effectString(Reference.FATIGUE, Integer.toString(duration),Integer.toString(potency)); }
	public static String breath(int duration, int potency) { return effectString(Reference.BREATH, Integer.toString(duration),Integer.toString(potency)); }
	public static String vision(int duration, int potency) { return effectString(Reference.VISION, Integer.toString(duration),Integer.toString(potency)); }
	public static String resist(int duration, int potency) { return effectString(Reference.RESIST, Integer.toString(duration),Integer.toString(potency)); }
	public static String fireres(int duration, int potency) { return effectString(Reference.FRESIST, Integer.toString(duration),Integer.toString(potency)); }
	public static String weak(int duration, int potency) { return effectString(Reference.WEAK, Integer.toString(duration),Integer.toString(potency)); }
	public static String jump(int duration, int potency) { return effectString(Reference.JUMP, Integer.toString(duration),Integer.toString(potency)); }
	public static String nausea(int duration, int potency) { return effectString(Reference.NAUSEA, Integer.toString(duration),Integer.toString(potency)); }
	public static String hunger(int duration, int potency) { return effectString(Reference.HUNGER, Integer.toString(duration),Integer.toString(potency)); }
	public static String regen(int duration, int potency) { return effectString(Reference.REGEN, Integer.toString(duration),Integer.toString(potency)); }
	public static String poison(int duration, int potency) { return effectString(Reference.POISON, Integer.toString(duration),Integer.toString(potency)); }
	public static String wither(int duration, int potency) { return effectString(Reference.WITHER, Integer.toString(duration),Integer.toString(potency)); }
	public static String blind(int duration, int potency) { return effectString(Reference.BLIND, Integer.toString(duration), Integer.toString(potency)); }

	public static String effectString(String name, String duration, String potency) {
		return name + "|" + duration + "|" + potency;
	}

	private static void addPotionConfig(ConfigCategory category, ItemStack ingredient, String... effects ) {
		addPotionConfig(category, ingredient.getItem(), ingredient.getMetadata(), effects);
	}

	private static void addPotionConfig(ConfigCategory category, Item ingredient, String... effects ) {
		addPotionConfig(category, ingredient, 0, effects);
	}

	private static void addPotionConfig(ConfigCategory category, Item ingredient, int meta, String... effects ) {
		Property prop = new Property(String.format("%s|%d", ingredient.getRegistryName(), meta),effects, Property.Type.STRING);

		category.put(prop.getName(), prop);
	}

	public static void postInit() {
		List<String> entityNames = new ArrayList<String>();
		for (Object o : EntityList.stringToClassMapping.values()) {
			Class c = (Class)o;
			if (EntityLiving.class.isAssignableFrom(c)) {
				entityNames.add( EntityList.classToStringMapping.get(o) );
			}
		}
		List<String> projectileNames = new ArrayList<String>();
		for (Object o : EntityList.stringToClassMapping.values()) {
			Class c = (Class)o;
			if (IProjectile.class.isAssignableFrom(c)) {
				projectileNames.add( EntityList.classToStringMapping.get(o) );
			}
		}

		Settings.InterdictionTorch.entitiesThatCanBePushed = getStringList("entities_that_can_be_pushed", Names.item_and_block_settings + "." + Names.interdiction_torch, entityNames );
		Settings.InterdictionTorch.projectilesThatCanBePushed = getStringList("projectiles_that_can_be_pushed", Names.item_and_block_settings + "." + Names.interdiction_torch, projectileNames);

		Settings.RendingGale.entitiesThatCanBePushed = getStringList("entities_that_can_be_pushed", Names.item_and_block_settings + "." + Names.rending_gale, entityNames );
		Settings.RendingGale.projectilesThatCanBePushed = getStringList( "projectiles_that_can_be_pushed", Names.item_and_block_settings + "." + Names.rending_gale, projectileNames );

		Settings.SeekerShot.entitiesThatCanBeHunted = getStringList( "entities_that_can_be_hunted", Names.item_and_block_settings + "." + Names.seeker_shot, entityNames);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.seeker_shot, true);

		//loading here to allow for recipes with items from other mods
		// probably could be done earlier, but what do I know about when mods are loading stuff
		loadAlkahestCraftingRecipes();
		loadAlkahestChargingRecipes();
		loadAlkahestBaseItem();

		if (configuration.hasChanged())
		{
			configuration.save();
		}
	}

	private static void loadAlkahestBaseItem() {
		String registryName = getString("base_item", Names.item_and_block_settings + "." + Names.alkahestry_tome, Items.redstone.getRegistryName());
		int meta = getInt("base_item_meta", Names.item_and_block_settings + "." + Names.alkahestry_tome, 0, 0, 16);
		String[] splitName = registryName.split(":");
		ItemStack stack = getItemStackFromNameMeta(splitName[0], splitName[1], meta);
		Settings.AlkahestryTome.baseItem = stack;

		Settings.AlkahestryTome.baseItemWorth = getInt("base_item_worth", Names.item_and_block_settings + "." + Names.alkahestry_tome, 1, 1, 1000);
	}

	private static void loadAlkahestChargingRecipes() {
		ConfigCategory category = configuration.getCategory(Names.item_and_block_settings + "." + Names.alkahestry_tome + "." + Names.charging_recipes);

		if (category.isEmpty()) {
			addDefaultAlkahestChargingRecipes(category);
		}

		loadAlkahestChargingRecipesIntoSettings(category);

		setCategoryTranslations(Names.item_and_block_settings + "." + Names.alkahestry_tome + "." + Names.charging_recipes, true);
	}

	private static void loadAlkahestChargingRecipesIntoSettings(ConfigCategory category) {
		Settings.AlkahestryTome.chargingRecipes.clear();

		for(Map.Entry<String, Property> entry: category.getValues().entrySet()) {
			String[] nameParts =entry.getKey().split(":");
			int[] values = entry.getValue().getIntList();

			String modId = nameParts[0];
			String name = nameParts[1];
			int meta = values[0];
			int charge = values[1];

			ItemStack stack = getItemStackFromNameMeta(modId, name, meta);

			if (stack != null) {
				Settings.AlkahestryTome.chargingRecipes.put(entry.getKey(), new AlkahestChargeRecipe(stack, charge));
			}
		}
	}

	private static void addDefaultAlkahestChargingRecipes(ConfigCategory category) {
		addConfigAlkahestChargingRecipe(category, Blocks.redstone_block.getRegistryName(), 9);
		addConfigAlkahestChargingRecipe(category, Items.redstone.getRegistryName(), 1);
		addConfigAlkahestChargingRecipe(category, Blocks.glowstone.getRegistryName(), 4);
		addConfigAlkahestChargingRecipe(category, Items.glowstone_dust.getRegistryName(), 1);
	}

	private static void addConfigAlkahestChargingRecipe(ConfigCategory category, String item, Integer charge) {
		addConfigAlkahestChargingRecipe(category, item, 0, charge);
	}
	private static void addConfigAlkahestChargingRecipe(ConfigCategory category, String item, Integer meta, Integer charge) {
		Property prop = new Property(item,new String[]{meta.toString(), charge.toString()}, Property.Type.INTEGER);

		category.put(item, prop);
	}

	private static void loadAlkahestCraftingRecipes() {
		ConfigCategory category = configuration.getCategory(Names.item_and_block_settings + "." + Names.alkahestry_tome + "." + Names.crafting_recipes);

		if (category.isEmpty()) {
			addDefaultAlkahestCraftingRecipes(category);
		}

		loadAlkahestCraftingRecipesIntoSettings(category);

		setCategoryTranslations(Names.item_and_block_settings + "." + Names.alkahestry_tome + "." + Names.crafting_recipes, true);
	}

	private static void loadAlkahestCraftingRecipesIntoSettings(ConfigCategory category) {
		Settings.AlkahestryTome.craftingRecipes.clear();

		for(Map.Entry<String, Property> entry: category.getValues().entrySet()) {
			String[] nameParts =entry.getKey().split(":");
			int[] values = entry.getValue().getIntList();

			String modId = nameParts[0];
			String name = nameParts[1];
			int meta = values[0];
			int yield = values[1];
			int cost = values[2];


			if (modId.toLowerCase().equals("oredictionary")) {
				Settings.AlkahestryTome.craftingRecipes.put(entry.getKey(), new AlkahestCraftRecipe(name, yield, cost));
			} else {
				ItemStack stack = getItemStackFromNameMeta(modId, name, meta);

				if (stack != null) {
					Settings.AlkahestryTome.craftingRecipes.put(entry.getKey(), new AlkahestCraftRecipe(stack, yield, cost));
				}
			}
		}
	}

	//TODO: refactor out into stack helper or such
	private static ItemStack getItemStackFromNameMeta(String modId, String name, int meta) {
		ItemStack stack = null;
		Item item = GameRegistry.findItem(modId, name);

		if (item != null && item != GameData.getItemRegistry().getDefaultValue()) {
            stack = new ItemStack(item, 1, meta);
        } else {
            Block block = GameRegistry.findBlock(modId, name);
            if (block != null && block != GameData.getBlockRegistry().getDefaultValue()) {
                stack = new ItemStack(item, 1, meta);
            }
        }
		return stack;
	}

	private static void addDefaultAlkahestCraftingRecipes(ConfigCategory category) {

		addConfigAlkahestCraftingRecipe(category, Blocks.dirt.getRegistryName(), 32, TOME_COST_LOW_TIER);

		addConfigAlkahestCraftingRecipe(category, Blocks.cobblestone.getRegistryName(), 32, TOME_COST_LOW_TIER);
		addConfigAlkahestCraftingRecipe(category, Blocks.sand.getRegistryName(), 32, TOME_COST_LOW_TIER);
		addConfigAlkahestCraftingRecipe(category, Blocks.gravel.getRegistryName(), 16, TOME_COST_LOW_TIER);
		addConfigAlkahestCraftingRecipe(category, Blocks.sandstone.getRegistryName(), 8, TOME_COST_LOW_TIER);
		addConfigAlkahestCraftingRecipe(category, Blocks.clay.getRegistryName(), 2, TOME_COST_LOW_TIER);
		addConfigAlkahestCraftingRecipe(category, Blocks.netherrack.getRegistryName(), 8, TOME_COST_LOW_TIER);
		addConfigAlkahestCraftingRecipe(category, Items.coal.getRegistryName(), 1, 4, TOME_COST_LOW_TIER);
		addConfigAlkahestCraftingRecipe(category, Items.dye.getRegistryName(), 4, 1, TOME_COST_LOW_TIER);

		addConfigAlkahestCraftingRecipe(category, Blocks.obsidian.getRegistryName(), 4, TOME_COST_MIDDLE_TIER);
		addConfigAlkahestCraftingRecipe(category, Blocks.soul_sand.getRegistryName(), 8, TOME_COST_MIDDLE_TIER);
		addConfigAlkahestCraftingRecipe(category, Blocks.nether_brick.getRegistryName(), 4, TOME_COST_MIDDLE_TIER);
		addConfigAlkahestCraftingRecipe(category, Blocks.end_stone.getRegistryName(), 16, TOME_COST_MIDDLE_TIER);
		addConfigAlkahestCraftingRecipe(category, Items.coal.getRegistryName(), 4, TOME_COST_MIDDLE_TIER);
		addConfigAlkahestCraftingRecipe(category, Items.gunpowder.getRegistryName(), 2, TOME_COST_MIDDLE_TIER);
		addConfigAlkahestCraftingRecipe(category, Items.flint.getRegistryName(), 8, TOME_COST_MIDDLE_TIER);

		//high tier
		addConfigAlkahestCraftingRecipe(category, Items.gold_ingot.getRegistryName(), 1, TOME_COST_HIGH_TIER);
		addConfigAlkahestCraftingRecipe(category, Items.iron_ingot.getRegistryName(), 1, TOME_COST_HIGH_TIER);
		addConfigAlkahestCraftingRecipe(category, Items.emerald.getRegistryName(), 1, TOME_COST_HIGH_TIER);

		// I guess mods should start following the new naming convention.
		// *shrugs*
		addConfigAlkahestCraftingRecipe(category, oreDictionary("tin_ingot"), 1, TOME_COST_HIGH_TIER);
		addConfigAlkahestCraftingRecipe(category, oreDictionary("silver_ingot"), 1, TOME_COST_HIGH_TIER);
		addConfigAlkahestCraftingRecipe(category, oreDictionary("copper_ingot"), 1, TOME_COST_HIGH_TIER);
		addConfigAlkahestCraftingRecipe(category, oreDictionary("steel_ingot"), 1, TOME_COST_HIGH_TIER);

		addConfigAlkahestCraftingRecipe(category, oreDictionary("ingotTin"), 1, TOME_COST_HIGH_TIER);
		addConfigAlkahestCraftingRecipe(category, oreDictionary("ingotSilver"), 1, TOME_COST_HIGH_TIER);
		addConfigAlkahestCraftingRecipe(category, oreDictionary("ingotCopper"), 1, TOME_COST_HIGH_TIER);
		addConfigAlkahestCraftingRecipe(category, oreDictionary("ingotSteel"), 1, TOME_COST_HIGH_TIER);

		addConfigAlkahestCraftingRecipe(category, Items.diamond.getRegistryName(), 1, TOME_COST_UBER_TIER);

		//above uber
		addConfigAlkahestCraftingRecipe(category, Items.nether_star.getRegistryName(), 1, TOME_COST_UBER_TIER * 2);
	}

	private static String oreDictionary(String name) {
		return "OreDictionary:" + name;
	}

	private static void addConfigAlkahestCraftingRecipe(ConfigCategory category, String item, Integer yield, Integer cost) {
		addConfigAlkahestCraftingRecipe(category, item, 0, yield,cost);
	}
	private static void addConfigAlkahestCraftingRecipe(ConfigCategory category, String item, Integer meta, Integer yield, Integer cost) {

		Property prop = new Property(item,new String[]{meta.toString(), yield.toString(), cost.toString()}, Property.Type.INTEGER);

		category.put(item, prop);
	}

	private static void loadBlockAndItemSettings() {
		int itemCap = 9999;
		int cleanShortMax = 30000;
		int cleanIntMax = 2000000000;

		//alkahestry tome configs
		Settings.AlkahestryTome.chargeLimit = getInt("charge_limit", Names.item_and_block_settings + "." + Names.alkahestry_tome, 250, 0, itemCap);
		configuration.getCategory(Names.item_and_block_settings + "." + Names.alkahestry_tome).get("charge_limit").setRequiresMcRestart(true);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.alkahestry_tome, true);

		//altar configs
		Settings.Altar.redstoneCost = getInt("redstone_cost", Names.item_and_block_settings + "." + Names.altar, 3, 0, 10);
		Settings.Altar.timeInMinutes = getInt("time_in_minutes", Names.item_and_block_settings + "." + Names.altar, 20, 0, 60);
		Settings.Altar.maximumTimeVarianceInMinutes = getInt("maximum_time_variance_in_minutes", Names.item_and_block_settings + "." + Names.altar, 5, 0, 15);
		Settings.Altar.outputLightLevelWhileActive = getInt("output_light_level_while_active", Names.item_and_block_settings + "." + Names.altar, 16, 16, 0);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.altar, true);

		//angelic feather configs
		Settings.AngelicFeather.hungerCostPercent = getInt("hunger_cost_percent", Names.item_and_block_settings + "." + Names.angelic_feather, 50, 0, 100);
		Settings.AngelicFeather.leapingPotency = getInt("leaping_potency", Names.item_and_block_settings + "." + Names.angelic_feather, 1, 0, 5);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.angelic_feather, true);

		//angelheart vial configs
		Settings.AngelHeartVial.healPercentageOfMaxLife = getInt("heal_percentage_of_max_life", Names.item_and_block_settings + "." + Names.angelheart_vial, 25, 0, 100);
		Settings.AngelHeartVial.removeNegativeStatus = getBoolean("remove_negative_status", Names.item_and_block_settings + "." + Names.angelheart_vial, true);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.angelheart_vial, true);

		//apothecary cauldron configs
		List<String> heatSources = ImmutableList.of();
		Settings.ApothecaryCauldron.redstoneLimit = getInt("redstone_limit", Names.item_and_block_settings + "." + Names.apothecary_cauldron, 5, 0, 100);
		Settings.ApothecaryCauldron.cookTime = getInt("cook_time", Names.item_and_block_settings + "." + Names.apothecary_cauldron, 160, 20, 32000);
		Settings.ApothecaryCauldron.heatSources = getStringList("heat_sources", Names.item_and_block_settings + "." + Names.apothecary_cauldron, heatSources);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.apothecary_cauldron, true);

		//destruction catalyst configs
		Settings.DestructionCatalyst.mundaneBlocks = getStringList("mundane_blocks", Names.item_and_block_settings + "." + Names.destruction_catalyst, new ArrayList<String>(ItemDestructionCatalyst.ids));
		Settings.DestructionCatalyst.gunpowderCost = getInt("gunpowder_cost", Names.item_and_block_settings + "." + Names.destruction_catalyst, 3, 0, 10);
		Settings.DestructionCatalyst.gunpowderWorth = getInt("gunpowder_worth", Names.item_and_block_settings + "." + Names.destruction_catalyst, 1, 0, 3);
		Settings.DestructionCatalyst.gunpowderLimit = getInt("gunpowder_limit", Names.item_and_block_settings + "." + Names.destruction_catalyst, 250, 0, itemCap);
		Settings.DestructionCatalyst.explosionRadius = getInt("explosion_radius", Names.item_and_block_settings + "." + Names.destruction_catalyst, 1, 1, 5);
		Settings.DestructionCatalyst.centeredExplosion = getBoolean("centered_explosion", Names.item_and_block_settings + "." + Names.destruction_catalyst, false);
		Settings.DestructionCatalyst.perfectCube = getBoolean("perfect_cube", Names.item_and_block_settings + "." + Names.destruction_catalyst, true);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.destruction_catalyst, true);

		//emperor's chalice configs
		Settings.EmperorChalice.hungerSatiationMultiplier = getInt("hunger_satiation_multiplier", Names.item_and_block_settings + "." + Names.emperor_chalice, 4, 0, 10);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.emperor_chalice, true);

		//ender staff configs
		Settings.EnderStaff.enderPearlCastCost = getInt("ender_pearl_cast_cost", Names.item_and_block_settings + "." + Names.ender_staff, 1, 0, 3);
		Settings.EnderStaff.enderPearlNodeWarpCost = getInt("ender_pearl_node_warp_cost",Names.item_and_block_settings + "." + Names.ender_staff,  1, 0, 3);
		Settings.EnderStaff.enderPearlWorth = getInt("ender_pearl_worth", Names.item_and_block_settings + "." + Names.ender_staff, 1, 0, 10);
		Settings.EnderStaff.enderPearlLimit = getInt("ender_pearl_limit", Names.item_and_block_settings + "." + Names.ender_staff, 250, 0, itemCap);
		Settings.EnderStaff.nodeWarpCastTime = getInt("node_warp_cast_time", Names.item_and_block_settings + "." + Names.ender_staff, 60, 10, 120);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.ender_staff, true);

		//fortune coin configs
		Settings.FortuneCoin.disableAudio = getBoolean("disable_audio", Names.item_and_block_settings + "." + Names.fortune_coin, false);
		Settings.FortuneCoin.standardPullDistance = getInt("standard_pull_distance", Names.item_and_block_settings + "." + Names.fortune_coin, 5, 3, 10);
		Settings.FortuneCoin.longRangePullDistance = getInt("long_range_pull_distance", Names.item_and_block_settings + "." + Names.fortune_coin, 15, 9, 30);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.fortune_coin, true);

		//glacial staff configs
		Settings.GlacialStaff.snowballLimit = getInt("snowball_limit", Names.item_and_block_settings + "." + Names.glacial_staff, 250, 0, itemCap);
		Settings.GlacialStaff.snowballCost = getInt("snowball_cost", Names.item_and_block_settings + "." + Names.glacial_staff, 1, 0, 3);
		Settings.GlacialStaff.snowballWorth = getInt("snowball_worth", Names.item_and_block_settings + "." + Names.glacial_staff, 1, 0, 3);
		Settings.GlacialStaff.snowballDamage = getInt("snowball_damage", Names.item_and_block_settings + "." + Names.glacial_staff, 3, 0, 6);
		Settings.GlacialStaff.snowballDamageBonusFireImmune = getInt("snowball_damage_bonus_fire_immune", Names.item_and_block_settings + "." + Names.glacial_staff, 3, 0, 6);
		Settings.GlacialStaff.snowballDamageBonusBlaze = getInt("snowball_damage_bonus_blaze", Names.item_and_block_settings + "." + Names.glacial_staff, 6, 0, 12);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.glacial_staff, true);

		//harvest rod configs
		Settings.HarvestRod.bonemealLimit = getInt("bonemeal_limit", Names.item_and_block_settings + "." + Names.harvest_rod, 250, 0, itemCap);
		Settings.HarvestRod.bonemealCost = getInt("bonemeal_cost", Names.item_and_block_settings + "." + Names.harvest_rod, 1, 0, 3);
		Settings.HarvestRod.bonemealWorth = getInt("bonemeal_worth", Names.item_and_block_settings + "." + Names.harvest_rod, 1, 0, 3);
		Settings.HarvestRod.bonemealLuckPercentChance = getInt("bonemeal_luck_percent_chance", Names.item_and_block_settings + "." + Names.harvest_rod, 33, 1, 100);
		Settings.HarvestRod.bonemealLuckRolls = getInt("bonemeal_luck_rolls", Names.item_and_block_settings + "." + Names.harvest_rod, 2, 0, 7);
		Settings.HarvestRod.harvestBreakRadius = getInt("harvest_break_radius", Names.item_and_block_settings + "." + Names.harvest_rod, 2, 0, 5);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.harvest_rod, true);

		//hero's medallion config
		Settings.HeroMedallion.experienceLevelMaximum = getInt("experience_level_maximum", Names.item_and_block_settings + "." + Names.hero_medallion, 30, 0, 60);
		Settings.HeroMedallion.experienceLevelMinimum = getInt("experience_level_minimum", Names.item_and_block_settings + "." + Names.hero_medallion, 0, 0, 30);
		Settings.HeroMedallion.experienceLimit =getInt("experience_limit", Names.item_and_block_settings + "." + Names.hero_medallion, cleanIntMax, 0, cleanIntMax);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.hero_medallion, true);

		//ice rod configs
		Settings.IceMagusRod.snowballLimit = getInt("snowball_limit", Names.item_and_block_settings + "." + Names.ice_magus_rod, 250, 0, itemCap);
		Settings.IceMagusRod.snowballCost = getInt("snowball_cost", Names.item_and_block_settings + "." + Names.ice_magus_rod, 1, 0, 3);
		Settings.IceMagusRod.snowballWorth = getInt("snowball_worth", Names.item_and_block_settings + "." + Names.ice_magus_rod, 1, 0, 3);
		Settings.IceMagusRod.snowballDamage = getInt("snowball_damage", Names.item_and_block_settings + "." + Names.ice_magus_rod, 2, 0, 4);
		Settings.IceMagusRod.snowballDamageBonusFireImmune = getInt("snowball_damage_bonus_fire_immune", Names.item_and_block_settings + "." + Names.ice_magus_rod, 2, 0, 4);
		Settings.IceMagusRod.snowballDamageBonusBlaze = getInt("snowball_damage_bonus_blaze", Names.item_and_block_settings + "." + Names.ice_magus_rod, 4, 0, 8);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.ice_magus_rod, true);

		//infernal claws configs
		Settings.InfernalClaws.hungerCostPercent = getInt("hunger_cost_percent", Names.item_and_block_settings + "." + Names.infernal_claws, 10, 0, 30);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.infernal_claws, true);

		//infernal chalice configs
		Settings.InfernalChalice.hungerCostPercent = getInt("hunger_cost_percent", Names.item_and_block_settings + "." + Names.infernal_chalice, 5, 0, 10);
		Settings.InfernalChalice.fluidLimit = getInt("fluid_limit", Names.item_and_block_settings + "." + Names.infernal_chalice, 500000, 0, cleanIntMax);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.infernal_chalice, true);

		//infernal tear
		Settings.InfernalTear.absorbWhenCreated = getBoolean("absorb_when_created", Names.item_and_block_settings + "." + Names.infernal_tear, false);

		//interdiction torch configs
		//see post init for entity configs
		Settings.InterdictionTorch.pushRadius = getInt("push_radius", Names.item_and_block_settings + "." + Names.interdiction_torch, 5, 1, 15);
		Settings.InterdictionTorch.canPushProjectiles = getBoolean("can_push_projectiles", Names.item_and_block_settings + "." + Names.interdiction_torch, false);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.interdiction_torch, true);

		//kraken shell configs
		Settings.KrakenShell.hungerCostPercent = getInt("hunger_cost_percent", Names.item_and_block_settings + "." + Names.kraken_shell, 25, 0, 50);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.kraken_shell, true);

		//lantern of paranoia configs
		Settings.LanternOfParanoia.minLightLevel = getInt("min_light_level", Names.item_and_block_settings + "." + Names.lantern_of_paranoia, 8, 0, 15);
		Settings.LanternOfParanoia.placementScanRadius = getInt("placement_scan_radius", Names.item_and_block_settings + "." + Names.lantern_of_paranoia, 6, 1, 15);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.lantern_of_paranoia, true);

		//fertile_lilypad of fertility configs
		Settings.FertileLilypad.secondsBetweenGrowthTicks = getInt("seconds_between_growth_ticks", Names.item_and_block_settings + "." + Names.fertile_lilypad, 47, 1, 150);
		Settings.FertileLilypad.tileRange = getInt("tile_range", Names.item_and_block_settings + "." + Names.fertile_lilypad, 4, 1, 15);
		Settings.FertileLilypad.fullPotencyRange = getInt("full_potency_range", Names.item_and_block_settings + "." + Names.fertile_lilypad, 1, 1, 15);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.fertile_lilypad, true);

		//midas touchstone configs
		List<String> goldItems = ImmutableList.of();
		Settings.MidasTouchstone.goldItems = getStringList("gold_items", Names.item_and_block_settings + "." + Names.midas_touchstone, goldItems);
		Settings.MidasTouchstone.ticksBetweenRepairTicks = getInt("ticks_between_repair_ticks", Names.item_and_block_settings + "." + Names.midas_touchstone, 4, 1, cleanShortMax);
		Settings.MidasTouchstone.glowstoneCost = getInt("glowstone_cost", Names.item_and_block_settings + "." + Names.midas_touchstone, 1, 0, 3);
		Settings.MidasTouchstone.glowstoneWorth = getInt("glowstone_worth", Names.item_and_block_settings + "." + Names.midas_touchstone, 4, 0, 12);
		Settings.MidasTouchstone.glowstoneLimit = getInt("glowstone_limit", Names.item_and_block_settings + "." + Names.midas_touchstone, 250, 0, itemCap);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.midas_touchstone, true);

		//phoenix down configs
		Settings.PhoenixDown.hungerCostPercent = getInt("hunger_cost_percent", Names.item_and_block_settings + "." + Names.phoenix_down, 25, 0, 50);
		Settings.PhoenixDown.leapingPotency = getInt("leaping_potency", Names.item_and_block_settings + "." + Names.phoenix_down, 1, 0, 5);
		Settings.PhoenixDown.healPercentageOfMaxLife = getInt("heal_percentage_of_max_life", Names.item_and_block_settings + "." + Names.phoenix_down, 100, 0, 100);
		Settings.PhoenixDown.removeNegativeStatus = getBoolean("remove_negative_status", Names.item_and_block_settings + "." + Names.phoenix_down, true);
		Settings.PhoenixDown.giveTemporaryDamageResistance = getBoolean("give_temporary_damage_resistance", Names.item_and_block_settings + "." + Names.phoenix_down, true);
		Settings.PhoenixDown.giveTemporaryRegeneration = getBoolean("give_temporary_regeneration", Names.item_and_block_settings + "." + Names.phoenix_down, true);
		Settings.PhoenixDown.giveTemporaryFireResistanceIfFireDamageKilledYou = getBoolean("give_temporary_fire_resistance_if_fire_damage_killed_you", Names.item_and_block_settings + "." + Names.phoenix_down, true);
		Settings.PhoenixDown.giveTemporaryWaterBreathingIfDrowningKilledYou = getBoolean("give_temporary_water_breathing_if_drowning_killed_you", Names.item_and_block_settings + "." + Names.phoenix_down, true);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.phoenix_down, true);

		//pyromancer staff configs
		Settings.PyromancerStaff.hungerCostPercent = getInt("hunger_cost_percent", Names.item_and_block_settings + "." + Names.pyromancer_staff, 5, 0, 10);
		Settings.PyromancerStaff.fireChargeLimit = getInt("fire_charge_limit", Names.item_and_block_settings + "." + Names.pyromancer_staff, 250, 0, itemCap);
		Settings.PyromancerStaff.fireChargeCost = getInt("fire_charge_cost", Names.item_and_block_settings + "." + Names.pyromancer_staff, 1, 0, 3);
		Settings.PyromancerStaff.fireChargeWorth = getInt("fire_charge_worth", Names.item_and_block_settings + "." + Names.pyromancer_staff, 1, 0, 3);
		Settings.PyromancerStaff.ghastAbsorbWorth = getInt("ghast_absorb_worth", Names.item_and_block_settings + "." + Names.pyromancer_staff, 1, 0, 3);
		Settings.PyromancerStaff.blazePowderLimit = getInt("blaze_powder_limit", Names.item_and_block_settings + "." + Names.pyromancer_staff, 250, 0, itemCap);
		Settings.PyromancerStaff.blazePowderCost = getInt("blaze_powder_cost", Names.item_and_block_settings + "." + Names.pyromancer_staff, 1, 0, 3);
		Settings.PyromancerStaff.blazePowderWorth = getInt("blaze_powder_worth", Names.item_and_block_settings + "." + Names.pyromancer_staff, 1, 0, 3);
		Settings.PyromancerStaff.blazeAbsorbWorth = getInt("blaze_absorb_worth", Names.item_and_block_settings + "." + Names.pyromancer_staff, 1, 0, 3);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.pyromancer_staff, true);

		//rending gale configs
		Settings.RendingGale.chargeLimit = getInt("charge_limit", Names.item_and_block_settings + "." + Names.rending_gale, cleanShortMax, 0, cleanIntMax);
		Settings.RendingGale.castChargeCost = getInt("cast_charge_cost", Names.item_and_block_settings + "." + Names.rending_gale, 1, 0, 3);
		Settings.RendingGale.boltChargeCost = getInt("bolt_charge_cost", Names.item_and_block_settings + "." + Names.rending_gale, 100, 0, 250);
		Settings.RendingGale.chargeFeatherWorth = getInt("charge_feather_worth", Names.item_and_block_settings + "." + Names.rending_gale, 100, 1, 250);
		Settings.RendingGale.blockTargetRange = getInt("block_target_range", Names.item_and_block_settings + "." + Names.rending_gale, 12, 5, 15);
		Settings.RendingGale.pushPullRadius = getInt("push_pull_radius", Names.item_and_block_settings + "." + Names.rending_gale, 10, 1, 20);
		Settings.RendingGale.canPushProjectiles = getBoolean("can_push_projectiles", Names.item_and_block_settings + "." + Names.rending_gale, false);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.rending_gale, true);

		//rod of lyssa configs
		Settings.RodOfLyssa.useLeveledFailureRate = getBoolean("use_leveled_failure_rate", Names.item_and_block_settings + "." + Names.rod_of_lyssa, true);
		Settings.RodOfLyssa.levelCapForLeveledFormula = getInt("level_cap_for_leveled_formula", Names.item_and_block_settings + "." + Names.rod_of_lyssa, 100, 1, 900);
		Settings.RodOfLyssa.flatStealFailurePercentRate = getInt("flat_steal_failure_percent_rate", Names.item_and_block_settings + "." + Names.rod_of_lyssa, 10, 0, 100);
		Settings.RodOfLyssa.stealFromVacantSlots = getBoolean("steal_from_vacant_slots", Names.item_and_block_settings + "." + Names.rod_of_lyssa, true);
		Settings.RodOfLyssa.failStealFromVacantSlots = getBoolean("fail_steal_from_vacant_slots", Names.item_and_block_settings + "." + Names.rod_of_lyssa, false);
		Settings.RodOfLyssa.angerOnStealFailure = getBoolean("anger_on_steal_failure", Names.item_and_block_settings + "." + Names.rod_of_lyssa, true);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.rod_of_lyssa, true);

		//sojourners staff configs
		List<String> torches = ImmutableList.of();
		Settings.SojournerStaff.torches = getStringList("torches", Names.item_and_block_settings + "." + Names.sojourner_staff, torches);
		Settings.SojournerStaff.maxCapacityPerItemType = getInt("max_capacity_per_item_type", Names.item_and_block_settings + "." + Names.sojourner_staff, 1500, 1, itemCap);
		Settings.SojournerStaff.maxRange = getInt("max_range", Names.item_and_block_settings + "." + Names.sojourner_staff, 30, 1, 30);
		Settings.SojournerStaff.tilePerCostMultiplier = getInt("tile_per_cost_multiplier", Names.item_and_block_settings + "." + Names.sojourner_staff, 6, 6, 30);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.sojourner_staff, true);

		//twilight cloak configs
		Settings.TwilightCloak.maxLightLevel = getInt("max_light_level", Names.item_and_block_settings + "." + Names.twilight_cloak, 4, 0, 15);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.twilight_cloak, true);

		//void tear configs
		Settings.VoidTear.itemLimit = getInt("item_limit", Names.item_and_block_settings + "." + Names.void_tear, 2000000000, 0, cleanIntMax);
		Settings.VoidTear.absorbWhenCreated = getBoolean("absorb_when_created", Names.item_and_block_settings + "." + Names.void_tear, true);
		setCategoryTranslations(Names.item_and_block_settings + "." + Names.void_tear, true);

		setCategoryTranslations(Names.item_and_block_settings, true);
	}

	private static void loadMobDropProbabilities()
	{
		HashMap<String, Integer> drops = new HashMap<>(  );

		drops.put(Names.zombie_heart + "_base", getInt(Names.zombie_heart + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.zombie_heart + "_looting", getInt(Names.zombie_heart + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.pigman_heart + "_base", getInt(Names.pigman_heart + "_base", Names.mob_drop_probability, 15, 0, 100));
		drops.put(Names.pigman_heart + "_looting", getInt(Names.pigman_heart + "_looting", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.rib_bone + "_base", getInt(Names.rib_bone + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.rib_bone + "_looting", getInt(Names.rib_bone + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.withered_rib + "_base", getInt(Names.withered_rib + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.withered_rib + "_looting", getInt(Names.withered_rib + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.spider_fangs + "_base", getInt(Names.spider_fangs + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.spider_fangs + "_looting", getInt(Names.spider_fangs + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.cave_spider_fangs + "_base", getInt(Names.cave_spider_fangs + "_base", Names.mob_drop_probability, 15, 0, 100));
		drops.put(Names.cave_spider_fangs + "_looting", getInt(Names.cave_spider_fangs + "_looting", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.blaze_molten_core + "_base", getInt(Names.blaze_molten_core + "_base", Names.mob_drop_probability, 15, 0, 100));
		drops.put(Names.blaze_molten_core + "_looting", getInt(Names.blaze_molten_core + "_looting", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.magma_cube_molten_core + "_base", getInt(Names.magma_cube_molten_core + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.magma_cube_molten_core + "_looting", getInt(Names.magma_cube_molten_core + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.frozen_core + "_base", getInt(Names.frozen_core + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.frozen_core + "_looting", getInt(Names.frozen_core + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.eye_of_the_storm + "_base", getInt(Names.eye_of_the_storm + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.eye_of_the_storm + "_looting", getInt(Names.eye_of_the_storm + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.bat_wing + "_base", getInt(Names.bat_wing + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.bat_wing + "_looting", getInt(Names.bat_wing + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.creeper_gland + "_base", getInt(Names.creeper_gland + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.creeper_gland + "_looting", getInt(Names.creeper_gland + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.ghast_gland + "_base", getInt(Names.ghast_gland + "_base", Names.mob_drop_probability, 15, 0, 100));
		drops.put(Names.ghast_gland + "_looting", getInt(Names.ghast_gland + "_looting", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.witch_hat + "_base", getInt(Names.witch_hat + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.witch_hat + "_looting", getInt(Names.witch_hat + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.squid_beak + "_base", getInt(Names.squid_beak + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.squid_beak + "_looting", getInt(Names.squid_beak + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.slime_pearl + "_base", getInt(Names.slime_pearl + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.slime_pearl + "_looting", getInt(Names.slime_pearl + "_looting", Names.mob_drop_probability, 5, 0, 100));
		drops.put(Names.ender_heart + "_base", getInt(Names.ender_heart + "_base", Names.mob_drop_probability, 10, 0, 100));
		drops.put(Names.ender_heart + "_looting", getInt(Names.ender_heart + "_looting", Names.mob_drop_probability, 5, 0, 100));

		Settings.mobDropProbabilities = drops;
		setCategoryTranslations(Names.mob_drop_probability, true);
	}

	private static void loadEasyModeSettings()
	{
		boolean easyModeDefault = true;

		Settings.EasyModeRecipes.fortuneCoin = getBoolean( Names.fortune_coin, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.altar = getBoolean(Names.altar, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.infernalChalice = getBoolean(Names.infernal_chalice, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.enderStaff = getBoolean(Names.ender_staff, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.salamanderEye = getBoolean(Names.salamander_eye, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.rodOfLyssa = getBoolean(Names.rod_of_lyssa, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.serpentStaff = getBoolean(Names.serpent_staff, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.rendingGale = getBoolean(Names.rending_gale, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.pyromancerStaff = getBoolean(Names.pyromancer_staff, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.magicBane = getBoolean(Names.magicbane, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.lanternOfParanoia = getBoolean(Names.lantern_of_paranoia, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.alkahestryTome = getBoolean(Names.alkahestry_tome, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.wraithNode = getBoolean(Names.wraith_node, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.glacialStaff = getBoolean(Names.glacial_staff, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.sojournerStaff = getBoolean(Names.sojourner_staff, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.krakenShell = getBoolean(Names.kraken_shell, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.angelicFeather = getBoolean(Names.angelic_feather, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.emperorChalice = getBoolean(Names.emperor_chalice, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.heroMedallion = getBoolean(Names.hero_medallion, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.iceMagusRod = getBoolean(Names.ice_magus_rod, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.infernalClaws = getBoolean(Names.infernal_claws, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.destructionCatalyst = getBoolean(Names.destruction_catalyst, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.interdictionTorch = getBoolean(Names.interdiction_torch, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.voidTear = getBoolean(Names.void_tear, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.infernalTear = getBoolean(Names.infernal_tear, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.fertileEssence = getBoolean(Names.fertile_essence, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.seekerShot = getBoolean(Names.seeker_shot, Names.easy_mode_recipes, easyModeDefault);

		setCategoryTranslations(Names.easy_mode_recipes, true);
	}

	private static void loadHudPositions()
	{
		Settings.HudPositions.sojournerStaff = getInt( Names.sojourner_staff, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.handgun = getInt(Names.handgun, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.alkahestryTome = getInt(Names.alkahestry_tome, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.destructionCatalyst = getInt(Names.destruction_catalyst, Names.hud_positions, 3, 0, 3);
		//Settings.HudPositions.elsewhereFlask = getInt(Names.elsewhere_flask, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.enderStaff = getInt(Names.ender_staff, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.iceMagusRod = getInt(Names.ice_magus_rod, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.glacialStaff = getInt(Names.glacial_staff, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.voidTear = getInt(Names.void_tear, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.midasTouchstone = getInt(Names.midas_touchstone, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.harvestRod = getInt(Names.harvest_rod, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.infernalChalice = getInt(Names.infernal_chalice, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.heroMedallion = getInt(Names.hero_medallion, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.pyromancerStaff = getInt(Names.pyromancer_staff, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.rendingGale = getInt(Names.rending_gale, Names.hud_positions, 3, 0, 3);

		setCategoryTranslations(Names.hud_positions, true);
	}

	//TODO refactor all of these out into configuration helper
	private static List<String> getStringList(String name, String category, List<String> defaultValue) {
		return Arrays.asList(configuration.getStringList(name, category, defaultValue.toArray(new String[defaultValue.size()]), getTranslatedComment(category, name), new String[]{}, getLabelLangRef(category, name)));
	}

	private static boolean getBoolean(String name, String category, boolean defaultValue) {
		return configuration.getBoolean(name, category, defaultValue, getTranslatedComment(category, name), getLabelLangRef( category, name));
	}

	private static int getInt(String name, String category, int defaultValue, int minValue, int maxValue) {
		return configuration.getInt(name, category, defaultValue, minValue, maxValue, getTranslatedComment(category, name) , getLabelLangRef(category, name));
	}

	private static String getString(String name, String category, String defaultValue) {
		return configuration.getString(name, category, defaultValue, getTranslatedComment(category, name), getLabelLangRef(category, name));
	}

	private static String getTranslatedComment(String category, String config) {
		return StatCollector.translateToLocal("config." + category + "." + config + ".comment");
	}

	private static String getLabelLangRef(String category, String config) {
		return "config." + category + "." + config + ".label";
	}

	private static void setCategoryTranslations(String categoryName, boolean setComment) {
		ConfigCategory category = configuration.getCategory(categoryName);

		category.setLanguageKey("config." + categoryName + ".label");
		if (setComment) {
			category.setComment( StatCollector.translateToLocal("config." + categoryName + ".comment"));
		}
	}


	@SubscribeEvent
	public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.modID.equalsIgnoreCase( Reference.MOD_ID))
		{
			loadConfiguration();
			postInit();
		}
	}
}
