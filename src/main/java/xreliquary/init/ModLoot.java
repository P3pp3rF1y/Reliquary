package xreliquary.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;

import java.util.List;

public class ModLoot {
	private ModLoot() {}

	private static final List<String> CHEST_TABLES = List.of("abandoned_mineshaft", "desert_pyramid", "end_city_treasure", "igloo_chest", "jungle_temple", "nether_bridge", "simple_dungeon", "stronghold_corridor", "stronghold_crossing", "stronghold_library", "village_blacksmith");

	private static final List<String> ENTITY_TABLES = List.of("bat", "blaze", "cave_spider", "creeper", "enderman", "ghast", "guardian", "husk", "magma_cube", "skeleton", "slime", "snow_golem", "spider", "stray", "squid", "witch", "wither_skeleton", "zombie", "zombified_piglin", "zombie_villager");

	public static void registerEventBusListeners(IEventBus modBus) {
		modBus.addListener(ModLoot::lootLoad);
	}

	public static void lootLoad(LootTableLoadEvent evt) {
		String chestsPrefix = "minecraft:chests/";
		String entitiesPrefix = "minecraft:entities/";
		String name = evt.getName().toString();

		if ((Settings.COMMON.chestLootEnabled.get() && name.startsWith(chestsPrefix) && CHEST_TABLES.contains(name.substring(chestsPrefix.length())))
				|| (Settings.COMMON.mobDropsEnabled.get() && name.startsWith(entitiesPrefix) && ENTITY_TABLES.contains(name.substring(entitiesPrefix.length())))) {
			String file = name.substring("minecraft:".length());
			evt.getTable().addPool(getInjectPool(file));
		}
	}

	private static LootPool getInjectPool(String entryName) {
		return LootPool.lootPool().add(getInjectEntry(entryName)).setBonusRolls(UniformGenerator.between(0, 1)).name("xreliquary_inject_pool").build();
	}

	private static LootPoolEntryContainer.Builder<?> getInjectEntry(String name) {
		return LootTableReference.lootTableReference(new ResourceLocation(Reference.MOD_ID, "inject/" + name)).setWeight(1);
	}
}
