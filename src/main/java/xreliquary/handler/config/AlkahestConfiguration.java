package xreliquary.handler.config;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
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
		String registryName = ConfigurationHandler.getString("base_item", Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.ALKAHESTRY_TOME, Items.REDSTONE.getRegistryName().toString(), "Base Item name in format \"ModId:item\"");
		int meta = ConfigurationHandler.getInt("base_item_meta", Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.ALKAHESTRY_TOME, 0, 0, 16, "meta of the Base Item");
		String[] splitName = registryName.split(":");
		Settings.AlkahestryTome.baseItem = StackHelper.getItemStackFromNameMeta(splitName[0], splitName[1], meta);

		Settings.AlkahestryTome.baseItemWorth = ConfigurationHandler.getInt("base_item_worth", Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.ALKAHESTRY_TOME, 1, 1, 1000, "How much charge the Base Item is worth");
	}

	public static void loadAlkahestChargingRecipes() {
		String categoryKey = Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.ALKAHESTRY_TOME + "." + Names.Configs.CHARGING_RECIPES;
		ConfigCategory category = ConfigurationHandler.configuration.getCategory(categoryKey);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(categoryKey));
		category.setComment("List of recipes that can be used with Alkahestry Tome to charge it. The values are item name \"modID:name\", meta, charge points.\n");

		if(category.isEmpty()) {
			addDefaultAlkahestChargingRecipes(category);
		}

		loadAlkahestChargingRecipesIntoSettings(category);

	}

	private static void loadAlkahestChargingRecipesIntoSettings(ConfigCategory category) {
		Settings.AlkahestryTome.chargingRecipes.clear();

		for(Map.Entry<String, Property> entry : category.getValues().entrySet()) {
			String[] nameParts = entry.getKey().split(":");
			int[] values = entry.getValue().getIntList();

			String modId = nameParts[0];
			String name = nameParts[1];
			int meta = 0;
			boolean allSubitems = false;

			//allows specifying without meta in which case meta 0 is assumed
			if(name.contains("|")) {
				nameParts = name.split("\\|");
				name = nameParts[0];
				if("*".equals(nameParts[1]))
					allSubitems = true;
				else
					meta = Integer.parseInt(nameParts[1]);
			} else if(values.length > 1) {
				meta = values[0];
			}

			//using last because of legacy configs that have meta as first
			int charge = values[values.length - 1];

			if(allSubitems) {
				Item item = Item.REGISTRY.getObject(new ResourceLocation(modId, name));
				Block block = Block.REGISTRY.getObject(new ResourceLocation(modId, name));
				NonNullList<ItemStack> subItems = NonNullList.create();
				if(item != null) {
					item.getSubItems(item, null, subItems);
				} else {
					block.getSubBlocks(Item.getItemFromBlock(block), null, subItems);
				}

				for(ItemStack stack : subItems) {
					Settings.AlkahestryTome.chargingRecipes.put(entry.getKey().replace("*", String.valueOf(stack.getMetadata())), new AlkahestChargeRecipe(stack, charge));
				}
			} else {
				ItemStack stack = StackHelper.getItemStackFromNameMeta(modId, name, meta);

				if(stack != null) {
					Settings.AlkahestryTome.chargingRecipes.put(entry.getKey(), new AlkahestChargeRecipe(stack, charge));
				}
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
		Property prop = new Property(item, new String[] {charge.toString()}, Property.Type.INTEGER);

		category.put(item, prop);
	}

	public static void loadAlkahestCraftingRecipes() {
		String categoryKey = Names.Configs.ITEM_AND_BLOCK_SETTINGS + "." + Names.Items.ALKAHESTRY_TOME + "." + Names.Configs.CRAFTING_RECIPES;
		ConfigCategory category = ConfigurationHandler.configuration.getCategory(categoryKey);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(categoryKey));
		category.setComment("List of recipes that can be used with Alkahestry Tome to craft items. The values are item name \"modID:name\", meta, yield, cost.");

		if(category.isEmpty()) {
			addDefaultAlkahestCraftingRecipes(category);
		}

		loadAlkahestCraftingRecipesIntoSettings(category);
	}

	private static void loadAlkahestCraftingRecipesIntoSettings(ConfigCategory category) {
		Settings.AlkahestryTome.craftingRecipes.clear();

		for(Map.Entry<String, Property> entry : category.getValues().entrySet()) {
			String[] nameParts = entry.getKey().split(":");
			int[] values = entry.getValue().getIntList();

			String modId = nameParts[0];
			String name = nameParts[1];
			int meta = 0;
			boolean allSubitems = false;

			//allows specifying without meta in which case meta 0 is assumed
			if(name.contains("|")) {
				nameParts = name.split("\\|");
				name = nameParts[0];
				if("*".equals(nameParts[1]))
					allSubitems = true;
				else
					meta = Integer.parseInt(nameParts[1]);
			} else if(values.length > 2) {
				meta = values[0];
			}
			int yield = values[values.length - 2];
			int cost = values[values.length - 1];

			if(modId.toLowerCase().equals("oredictionary")) {
				Settings.AlkahestryTome.craftingRecipes.put(entry.getKey(), new AlkahestCraftRecipe(name, yield, cost));
			} else {
				if(allSubitems) {
					Item item = Item.REGISTRY.getObject(new ResourceLocation(modId, name));
					Block block = Block.REGISTRY.getObject(new ResourceLocation(modId, name));
					NonNullList<ItemStack> subItems = NonNullList.create();
					if(item != null) {
						item.getSubItems(item, null, subItems);
					} else {
						block.getSubBlocks(Item.getItemFromBlock(block), null, subItems);
					}

					for(ItemStack stack : subItems) {
						Settings.AlkahestryTome.craftingRecipes.put(entry.getKey().replace("*", String.valueOf(stack.getMetadata())), new AlkahestCraftRecipe(stack, yield, cost));
					}
				} else {
					ItemStack stack = StackHelper.getItemStackFromNameMeta(modId, name, meta);

					String key = modId + ":" + name + "|" + meta;

					if(stack != null) {
						Settings.AlkahestryTome.craftingRecipes.put(key, new AlkahestCraftRecipe(stack, yield, cost));
					}
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
		Property prop = new Property(item + "|" + meta, new String[] {yield.toString(), cost.toString()}, Property.Type.INTEGER);

		category.put(item + "|" + meta, prop);
	}
}
