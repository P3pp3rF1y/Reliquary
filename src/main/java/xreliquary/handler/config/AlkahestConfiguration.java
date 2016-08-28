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
		String registryName = ConfigurationHandler.getString("base_item", Names.item_and_block_settings + "." + Names.alkahestry_tome, Items.REDSTONE.getRegistryName().toString());
		int meta = ConfigurationHandler.getInt("base_item_meta", Names.item_and_block_settings + "." + Names.alkahestry_tome, 0, 0, 16);
		String[] splitName = registryName.split(":");
		Settings.AlkahestryTome.baseItem = StackHelper.getItemStackFromNameMeta(splitName[0], splitName[1], meta);

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
		addConfigAlkahestChargingRecipe(category, Blocks.REDSTONE_BLOCK.getRegistryName().toString(), 9);
		addConfigAlkahestChargingRecipe(category, Items.REDSTONE.getRegistryName().toString(), 1);
		addConfigAlkahestChargingRecipe(category, Blocks.GLOWSTONE.getRegistryName().toString(), 4);
		addConfigAlkahestChargingRecipe(category, Items.GLOWSTONE_DUST.getRegistryName().toString(), 1);
	}

	private static void addConfigAlkahestChargingRecipe(ConfigCategory category, String item, Integer charge) {
		addConfigAlkahestChargingRecipe(category, item, 0, charge);
	}

	private static void addConfigAlkahestChargingRecipe(ConfigCategory category, String item, @SuppressWarnings("SameParameterValue") Integer meta, Integer charge) {
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

		addConfigAlkahestCraftingRecipe(category, Blocks.DIRT.getRegistryName().toString(), 32, TOME_COST_LOW_TIER);

		addConfigAlkahestCraftingRecipe(category, Blocks.COBBLESTONE.getRegistryName().toString(), 32, TOME_COST_LOW_TIER);
		addConfigAlkahestCraftingRecipe(category, Blocks.SAND.getRegistryName().toString(), 32, TOME_COST_LOW_TIER);
		addConfigAlkahestCraftingRecipe(category, Blocks.GRAVEL.getRegistryName().toString(), 16, TOME_COST_LOW_TIER);
		addConfigAlkahestCraftingRecipe(category, Blocks.SANDSTONE.getRegistryName().toString(), 8, TOME_COST_LOW_TIER);
		addConfigAlkahestCraftingRecipe(category, Blocks.CLAY.getRegistryName().toString(), 2, TOME_COST_LOW_TIER);
		addConfigAlkahestCraftingRecipe(category, Blocks.NETHERRACK.getRegistryName().toString(), 8, TOME_COST_LOW_TIER);
		addConfigAlkahestCraftingRecipe(category, Items.COAL.getRegistryName().toString(), 1, 4, TOME_COST_LOW_TIER);
		addConfigAlkahestCraftingRecipe(category, Items.DYE.getRegistryName().toString(), 4, 1, TOME_COST_LOW_TIER);

		addConfigAlkahestCraftingRecipe(category, Blocks.OBSIDIAN.getRegistryName().toString(), 4, TOME_COST_MIDDLE_TIER);
		addConfigAlkahestCraftingRecipe(category, Blocks.SOUL_SAND.getRegistryName().toString(), 8, TOME_COST_MIDDLE_TIER);
		addConfigAlkahestCraftingRecipe(category, Blocks.NETHER_BRICK.getRegistryName().toString(), 4, TOME_COST_MIDDLE_TIER);
		addConfigAlkahestCraftingRecipe(category, Blocks.END_STONE.getRegistryName().toString(), 16, TOME_COST_MIDDLE_TIER);
		addConfigAlkahestCraftingRecipe(category, Items.COAL.getRegistryName().toString(), 4, TOME_COST_MIDDLE_TIER);
		addConfigAlkahestCraftingRecipe(category, Items.GUNPOWDER.getRegistryName().toString(), 2, TOME_COST_MIDDLE_TIER);
		addConfigAlkahestCraftingRecipe(category, Items.FLINT.getRegistryName().toString(), 8, TOME_COST_MIDDLE_TIER);

		//high tier
		addConfigAlkahestCraftingRecipe(category, Items.GOLD_INGOT.getRegistryName().toString(), 1, TOME_COST_HIGH_TIER);
		addConfigAlkahestCraftingRecipe(category, Items.IRON_INGOT.getRegistryName().toString(), 1, TOME_COST_HIGH_TIER);
		addConfigAlkahestCraftingRecipe(category, Items.EMERALD.getRegistryName().toString(), 1, TOME_COST_HIGH_TIER);

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

		addConfigAlkahestCraftingRecipe(category, Items.DIAMOND.getRegistryName().toString(), 1, TOME_COST_UBER_TIER);

		//above uber
		addConfigAlkahestCraftingRecipe(category, Items.NETHER_STAR.getRegistryName().toString(), 1, TOME_COST_UBER_TIER * 4);
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
