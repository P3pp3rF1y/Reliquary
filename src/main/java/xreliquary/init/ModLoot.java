package xreliquary.init;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.properties.EntityPropertyManager;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xreliquary.loot.EntityPowered;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;

import java.util.List;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ModLoot {
	private static final List<String> CHEST_TABLES = ImmutableList.of("abandoned_mineshaft", "desert_pyramid", "end_city_treasure", "igloo_chest", "jungle_temple", "nether_bridge", "simple_dungeon", "stronghold_corridor", "stronghold_crossing", "stronghold_library", "village_blacksmith");

	private static final List<String> ENTITY_TABLES = ImmutableList.of("bat", "blaze", "cave_spider", "creeper", "enderman", "ghast", "guardian", "husk", "magma_cube", "skeleton", "slime", "snowman", "spider", "stray", "squid", "witch", "wither_skeleton", "zombie", "zombie_pigman", "zombie_villager");

	public static void init() {
		if(Settings.chestLootEnabled) {
			for(String s : CHEST_TABLES) {
				LootTableList.register(new ResourceLocation(Reference.MOD_ID, "inject/chests/" + s));
			}
		}

		if(Settings.mobDropsEnabled) {
			for(String s : ENTITY_TABLES) {
				LootTableList.register(new ResourceLocation(Reference.MOD_ID, "inject/entities/" + s));
			}

			EntityPropertyManager.registerProperty(new EntityPowered.Serializer());
		}
	}

	@SubscribeEvent
	public static void lootLoad(LootTableLoadEvent evt) {
		String chests_prefix = "minecraft:chests/";
		String entities_prefix = "minecraft:entities/";
		String name = evt.getName().toString();

		if((Settings.chestLootEnabled && name.startsWith(chests_prefix) && CHEST_TABLES.contains(name.substring(chests_prefix.length())))
				|| (Settings.mobDropsEnabled && name.startsWith(entities_prefix) && ENTITY_TABLES.contains(name.substring(entities_prefix.length())))) {
			String file = name.substring("minecraft:".length());
			evt.getTable().addPool(getInjectPool(file));
		}
	}

	private static LootPool getInjectPool(String entryName) {
		return new LootPool(new LootEntry[] {getInjectEntry(entryName)}, new LootCondition[0], new RandomValueRange(1), new RandomValueRange(0, 1), "xreliquary_inject_pool");
	}

	private static LootEntryTable getInjectEntry(String name) {
		return new LootEntryTable(new ResourceLocation(Reference.MOD_ID, "inject/" + name), 1, 0, new LootCondition[0], "xreliquary_inject_entry");
	}
}
