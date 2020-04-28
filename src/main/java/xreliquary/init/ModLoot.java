package xreliquary.init;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.TableLootEntry;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;

import java.util.List;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ModLoot {
	private ModLoot() {}

	private static final List<String> CHEST_TABLES = ImmutableList.of("abandoned_mineshaft", "desert_pyramid", "end_city_treasure", "igloo_chest", "jungle_temple", "nether_bridge", "simple_dungeon", "stronghold_corridor", "stronghold_crossing", "stronghold_library", "village_blacksmith");

	private static final List<String> ENTITY_TABLES = ImmutableList.of("bat", "blaze", "cave_spider", "creeper", "enderman", "ghast", "guardian", "husk", "magma_cube", "skeleton", "slime", "snow_golem", "spider", "stray", "squid", "witch", "wither_skeleton", "zombie", "zombie_pigman", "zombie_villager");

	@SubscribeEvent
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
		return LootPool.builder().addEntry(getInjectEntry(entryName)).bonusRolls(0, 1).name("xreliquary_inject_pool").build();
	}

	private static LootEntry.Builder getInjectEntry(String name) {
		return TableLootEntry.builder(new ResourceLocation(Reference.MOD_ID, "inject/" + name)).weight(1);
	}
}
