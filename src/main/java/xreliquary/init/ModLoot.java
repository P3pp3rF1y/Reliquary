package xreliquary.init;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.properties.EntityPropertyManager;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xreliquary.loot.EntityPowered;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;

import java.util.List;

public class ModLoot {
	private static final List<String> CHEST_TABLES = ImmutableList.of(); //"abandoned_mineshaft", "desert_pyramid", "jungle_temple", "simple_dungeon", "stronghold_corridor", "village_blacksmith");

	private static final List<String> ENTITY_TABLES = ImmutableList.of("bat", "blaze", "cave_spider", "creeper", "enderman", "ghast", "magma_cube", "skeleton", "slime", "snowman", "spider", "squid", "witch", "wither_skeleton", "zombie", "zombie_pigman");

	public static void init() {
		if(Settings.chestLootEnabled) {
			for(String s : CHEST_TABLES) {
				LootTableList.register(new ResourceLocation(Reference.MOD_ID, s));
			}
		}

		if(Settings.mobDropsEnabled) {
			for(String s : ENTITY_TABLES) {
				LootTableList.register(new ResourceLocation(Reference.MOD_ID, s));
			}

			EntityPropertyManager.registerProperty(new EntityPowered.Serializer());
		}
	}

	@SubscribeEvent
	public void lootLoad(LootTableLoadEvent evt) {
		String chests_prefix = "minecraft:chests/";
		String entities_prefix = "minecraft:entities/";
		String name = evt.getName().toString();

		if((Settings.chestLootEnabled && name.startsWith(chests_prefix) && CHEST_TABLES.contains(name.substring(chests_prefix.length())))
				|| (Settings.mobDropsEnabled && name.startsWith(entities_prefix) && ENTITY_TABLES.contains(name.substring(entities_prefix.length())))) {
			String file = name.substring("minecraft:".length());
			evt.getTable().addPool(getInjectPool(file));
		}
	}

	private LootPool getInjectPool(String entryName) {
		return new LootPool(new LootEntry[] {getInjectEntry(entryName, 1)}, new LootCondition[0], new RandomValueRange(1), new RandomValueRange(0, 1), "xreliquary_inject_pool");
	}

	private LootEntryTable getInjectEntry(String name, int weight) {
		return new LootEntryTable(new ResourceLocation(Reference.MOD_ID, "inject/" + name), weight, 0, new LootCondition[0], "xreliquary_inject_entry");
	}

/*
	public static void init() {
		if(Settings.chestLootEnabled) {
			//TODO: add back with loot table hooks

			String c = ChestGenHooks.MINESHAFT_CORRIDOR;
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.RIB_BONE, 1, 2, 10));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.SLIME_PEARL, 1, 3, 8));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.BAT_WING, 1, 3, 8));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.NEBULOUS_HEART, 1, 2, 4));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.SQUID_BEAK, 1, 4, 8));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.witchHat), 1, 2, 5));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.angelicFeather), 1, 1, 2));

			c = ChestGenHooks.PYRAMID_DESERT_CHEST;
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.RIB_BONE, 1, 3, 10));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.CHELICERAE, 1, 2, 10));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.CREEPER_GLAND, 1, 4, 8));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.ZOMBIE_HEART, 1, 5, 8));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.NEBULOUS_HEART, 1, 3, 5));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.MOLTEN_CORE, 1, 2, 5));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.SQUID_BEAK, 1, 4, 8));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.angelicFeather), 1, 1, 2));

			c = ChestGenHooks.PYRAMID_JUNGLE_CHEST;
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.RIB_BONE, 1, 3, 10));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.CREEPER_GLAND, 1, 3, 7));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.BAT_WING, 1, 3, 7));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.NEBULOUS_HEART, 1, 3, 6));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.angelicFeather), 1, 1, 2));

			c = ChestGenHooks.STRONGHOLD_CORRIDOR;
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.RIB_BONE, 1, 3, 7));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.SLIME_PEARL, 1, 4, 8));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.NEBULOUS_HEART, 1, 4, 8));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.FROZEN_CORE, 1, 4, 7));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.angelheartVial), 1, 2, 4));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.shearsOfWinter), 1, 1, 2));

			c = ChestGenHooks.STRONGHOLD_LIBRARY;
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.SLIME_PEARL, 1, 4, 10));

			c = ChestGenHooks.STRONGHOLD_CROSSING;
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.RIB_BONE, 1, 3, 7));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.WITHER_RIB, 1, 3, 5));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.SLIME_PEARL, 1, 3, 9));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.NEBULOUS_HEART, 1, 3, 9));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.FROZEN_CORE, 1, 3, 7));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.angelheartVial), 1, 4, 8));

			c = ChestGenHooks.VILLAGE_BLACKSMITH;
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.ZOMBIE_HEART, 1, 5, 10));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.witchHat), 1, 2, 5));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.glowingWater), 1, 4, 7));

			c = ChestGenHooks.DUNGEON_CHEST;
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.RIB_BONE, 1, 2, 8));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.CHELICERAE, 1, 2, 8));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.ZOMBIE_HEART, 1, 2, 8));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.witchHat), 1, 2, 3));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.glowingWater), 1, 2, 5));

			c = ChestGenHooks.NETHER_FORTRESS;
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.WITHER_RIB, 1, 2, 10));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.SLIME_PEARL, 1, 1, 8));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(XRRecipes.MOLTEN_CORE, 1, 2, 8));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.emptyVoidTear), 1, 1, 1));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModItems.salamanderEye), 1, 1, 1));
			ChestGenHooks.addItem(c, new WeightedRandomChestContent(new ItemStack(ModBlocks.interdictionTorch), 1, 1, 1));
		}
	}
*/
}
