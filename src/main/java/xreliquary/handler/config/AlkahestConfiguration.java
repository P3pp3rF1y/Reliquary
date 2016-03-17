package xreliquary.handler.config;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;
import xreliquary.handler.ConfigurationHandler;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.StackHelper;
import xreliquary.util.alkahestry.AlkahestChargeRecipe;
import xreliquary.util.alkahestry.AlkahestCraftRecipe;

import java.util.Map;

public class AlkahestConfiguration {
	private static final int TOME_COST_LOW_TIER = 4;
	private static final int TOME_COST_MIDDLE_TIER = 8;
	private static final int TOME_COST_HIGH_TIER = 32;
	private static final int TOME_COST_UBER_TIER = 64;

	public static void loadAlkahestBaseItem() {
		String registryName = ConfigurationHandler.getString("base_item", Names.item_and_block_settings + "." + Names.alkahestry_tome, Items.redstone.getRegistryName());
		int meta = ConfigurationHandler.getInt("base_item_meta", Names.item_and_block_settings + "." + Names.alkahestry_tome, 0, 0, 16);
		String[] splitName = registryName.split(":");
		ItemStack stack = StackHelper.getItemStackFromNameMeta(splitName[0], splitName[1], meta);
		Settings.AlkahestryTome.baseItem = stack;

		Settings.AlkahestryTome.baseItemWorth = ConfigurationHandler.getInt("base_item_worth", Names.item_and_block_settings + "." + Names.alkahestry_tome, 1, 1, 1000);
	}

	public static void loadAlkahestChargingRecipes() {
		ConfigCategory category = ConfigurationHandler.configuration.getCategory(Names.item_and_block_settings + "." + Names.alkahestry_tome + "." + Names.charging_recipes);

		if(category.isEmpty()) {
			addDefaultAlkahestChargingRecipes(category);
		}

		loadAlkahestChargingRecipesIntoSettings(category);

		ConfigurationHandler.setCategoryTranslations(Names.item_and_block_settings + "." + Names.alkahestry_tome + "." + Names.charging_recipes, true);
	}

	private static void loadAlkahestChargingRecipesIntoSettings(ConfigCategory category) {
		Settings.AlkahestryTome.chargingRecipes.clear();

		for(Map.Entry<String, Property> entry : category.getValues().entrySet()) {
			String[] nameParts = entry.getKey().split(":");
			int[] values = entry.getValue().getIntList();

			String modId = nameParts[0];
			String name = nameParts[1];
			int meta = values[0];
			int charge = values[1];

			ItemStack stack = StackHelper.getItemStackFromNameMeta(modId, name, meta);

			if(stack != null) {
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
		Property prop = new Property(item, new String[] {meta.toString(), charge.toString()}, Property.Type.INTEGER);

		category.put(item, prop);
	}

	public static void loadAlkahestCraftingRecipes() {
		ConfigCategory category = ConfigurationHandler.configuration.getCategory(Names.item_and_block_settings + "." + Names.alkahestry_tome + "." + Names.crafting_recipes);

		if(category.isEmpty()) {
			addDefaultAlkahestCraftingRecipes(category);
		}

		loadAlkahestCraftingRecipesIntoSettings(category);

		ConfigurationHandler.setCategoryTranslations(Names.item_and_block_settings + "." + Names.alkahestry_tome + "." + Names.crafting_recipes, true);
	}

	private static void loadAlkahestCraftingRecipesIntoSettings(ConfigCategory category) {
		Settings.AlkahestryTome.craftingRecipes.clear();

		for(Map.Entry<String, Property> entry : category.getValues().entrySet()) {
			String[] nameParts = entry.getKey().split(":");
			int[] values = entry.getValue().getIntList();

			String modId = nameParts[0];
			String name = nameParts[1];
			int meta = values[0];
			int yield = values[1];
			int cost = values[2];

			if(modId.toLowerCase().equals("oredictionary")) {
				Settings.AlkahestryTome.craftingRecipes.put(entry.getKey(), new AlkahestCraftRecipe(name, yield, cost));
			} else {
				ItemStack stack = StackHelper.getItemStackFromNameMeta(modId, name, meta);

				String key = entry.getKey() + (stack.getItem().getHasSubtypes() ? "|" + meta : "");

				if(stack != null) {
					Settings.AlkahestryTome.craftingRecipes.put(key, new AlkahestCraftRecipe(stack, yield, cost));
				}
			}
		}
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
		addConfigAlkahestCraftingRecipe(category, Items.nether_star.getRegistryName(), 1, TOME_COST_UBER_TIER * 4);
	}

	private static String oreDictionary(String name) {
		return "OreDictionary:" + name;
	}

	private static void addConfigAlkahestCraftingRecipe(ConfigCategory category, String item, Integer yield, Integer cost) {
		addConfigAlkahestCraftingRecipe(category, item, 0, yield, cost);
	}

	private static void addConfigAlkahestCraftingRecipe(ConfigCategory category, String item, Integer meta, Integer yield, Integer cost) {

		Property prop = new Property(item, new String[] {meta.toString(), yield.toString(), cost.toString()}, Property.Type.INTEGER);

		category.put(item, prop);
	}
}
