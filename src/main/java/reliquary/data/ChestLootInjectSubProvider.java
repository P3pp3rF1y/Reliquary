package reliquary.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import reliquary.Reliquary;
import reliquary.init.ModBlocks;
import reliquary.init.ModItems;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class ChestLootInjectSubProvider implements LootTableSubProvider {

	protected static final Map<ResourceKey<LootTable>, ResourceKey<LootTable>> LOOT_INJECTS = new HashMap<>();

	private static final String INJECT_FOLDER = "inject/";
	public static final ResourceKey<LootTable> ABANDONED_MINESHAFT = createInjectLootTableRegistryKey(BuiltInLootTables.ABANDONED_MINESHAFT);
	public static final ResourceKey<LootTable> DESERT_PYRAMID = createInjectLootTableRegistryKey(BuiltInLootTables.DESERT_PYRAMID);
	public static final ResourceKey<LootTable> END_CITY_TREASURE = createInjectLootTableRegistryKey(BuiltInLootTables.END_CITY_TREASURE);
	public static final ResourceKey<LootTable> IGLOO_CHEST = createInjectLootTableRegistryKey(BuiltInLootTables.IGLOO_CHEST);
	public static final ResourceKey<LootTable> JUNGLE_TEMPLE = createInjectLootTableRegistryKey(BuiltInLootTables.JUNGLE_TEMPLE);
	public static final ResourceKey<LootTable> NETHER_BRIDGE = createInjectLootTableRegistryKey(BuiltInLootTables.NETHER_BRIDGE);
	public static final ResourceKey<LootTable> SIMPLE_DUNGEON = createInjectLootTableRegistryKey(BuiltInLootTables.SIMPLE_DUNGEON);
	public static final ResourceKey<LootTable> STRONGHOLD_CORRIDOR = createInjectLootTableRegistryKey(BuiltInLootTables.STRONGHOLD_CORRIDOR);
	public static final ResourceKey<LootTable> STRONGHOLD_CROSSING = createInjectLootTableRegistryKey(BuiltInLootTables.STRONGHOLD_CROSSING);
	public static final ResourceKey<LootTable> STRONGHOLD_LIBRARY = createInjectLootTableRegistryKey(BuiltInLootTables.STRONGHOLD_LIBRARY);
	public static final ResourceKey<LootTable> VILLAGE_WEAPONSMITH = createInjectLootTableRegistryKey(BuiltInLootTables.VILLAGE_WEAPONSMITH);

	private static ResourceKey<LootTable> createInjectLootTableRegistryKey(ResourceKey<LootTable> vanillaLootTable) {
		ResourceLocation location = Reliquary.getRL(INJECT_FOLDER + vanillaLootTable.location().getPath());
		ResourceKey<LootTable> injectLootTable = ResourceKey.create(Registries.LOOT_TABLE, location);
		LOOT_INJECTS.put(vanillaLootTable, injectLootTable);
		return injectLootTable;
	}

	public ChestLootInjectSubProvider(HolderLookup.Provider registries) {
	}

	@Override
	public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> tables) {
		tables.accept(ABANDONED_MINESHAFT,
				getLootTable(61, getItemLootEntry(ModItems.RIB_BONE.get(), 10, 2), getItemLootEntry(ModItems.SLIME_PEARL.get(), 8, 3),
						getItemLootEntry(ModItems.BAT_WING.get(), 8, 3), getItemLootEntry(ModItems.NEBULOUS_HEART.get(), 4, 2),
						getItemLootEntry(ModItems.SQUID_BEAK.get(), 8, 4), getItemLootEntry(ModItems.WITCH_HAT.get(), 5, 2),
						getItemLootEntry(ModItems.ANGELIC_FEATHER.get(), 2)));

		tables.accept(DESERT_PYRAMID,
				getLootTable(44, getItemLootEntry(ModItems.RIB_BONE.get(), 10, 3), getItemLootEntry(ModItems.CHELICERAE.get(), 10, 2),
						getItemLootEntry(ModItems.CATALYZING_GLAND.get(), 8, 4), getItemLootEntry(ModItems.ZOMBIE_HEART.get(), 8, 5),
						getItemLootEntry(ModItems.NEBULOUS_HEART.get(), 5, 3), getItemLootEntry(ModItems.MOLTEN_CORE.get(), 5, 2),
						getItemLootEntry(ModItems.SQUID_BEAK.get(), 8, 4), getItemLootEntry(ModItems.ANGELIC_FEATHER.get(), 2)));

		tables.accept(END_CITY_TREASURE, getLootTable(64, getItemLootEntry(ModItems.NEBULOUS_HEART.get(), 30, 5),
				getItemLootEntry(ModItems.ENDER_STAFF.get(), 5), getItemLootEntry(ModItems.RENDING_GALE.get(), 1)));

		tables.accept(IGLOO_CHEST, getLootTable(60, getItemLootEntry(ModItems.FROZEN_CORE.get(), 40, 5)));

		tables.accept(JUNGLE_TEMPLE,
				getLootTable(62, getItemLootEntry(ModItems.RIB_BONE.get(), 10, 3), getItemLootEntry(ModItems.CATALYZING_GLAND.get(), 7, 3),
						getItemLootEntry(ModItems.BAT_WING.get(), 7, 3), getItemLootEntry(ModItems.NEBULOUS_HEART.get(), 6, 3),
						getItemLootEntry(ModItems.ANGELIC_FEATHER.get(), 2)));

		tables.accept(NETHER_BRIDGE,
				getLootTable(61, getItemLootEntry(ModItems.WITHERED_RIB.get(), 20, 2), getItemLootEntry(ModItems.SLIME_PEARL.get(), 8),
						getItemLootEntry(ModItems.MOLTEN_CORE.get(), 8, 2), getItemLootEntry(ModItems.VOID_TEAR.get(), 1),
						getItemLootEntry(ModItems.SALAMANDER_EYE.get(), 1), getItemLootEntry(ModBlocks.INTERDICTION_TORCH_ITEM.get(), 1)));

		tables.accept(SIMPLE_DUNGEON,
				getLootTable(68, getItemLootEntry(ModItems.RIB_BONE.get(), 8, 2), getItemLootEntry(ModItems.CHELICERAE.get(), 8, 2),
						getItemLootEntry(ModItems.ZOMBIE_HEART.get(), 8, 2), getItemLootEntry(ModItems.WITCH_HAT.get(), 3, 2),
						getItemLootEntry(ModItems.GLOWING_WATER.get(), 5, 2)));

		tables.accept(STRONGHOLD_CORRIDOR,
				getLootTable(64, getItemLootEntry(ModItems.RIB_BONE.get(), 7, 3), getItemLootEntry(ModItems.SLIME_PEARL.get(), 8, 4),
						getItemLootEntry(ModItems.NEBULOUS_HEART.get(), 8, 4), getItemLootEntry(ModItems.FROZEN_CORE.get(), 7, 4),
						getItemLootEntry(ModItems.ANGELHEART_VIAL.get(), 4, 2), getItemLootEntry(ModItems.SHEARS_OF_WINTER.get(), 2)));

		tables.accept(STRONGHOLD_CROSSING,
				getLootTable(55, getItemLootEntry(ModItems.RIB_BONE.get(), 7, 3), getItemLootEntry(ModItems.WITHERED_RIB.get(), 5, 3),
						getItemLootEntry(ModItems.SLIME_PEARL.get(), 9, 3), getItemLootEntry(ModItems.NEBULOUS_HEART.get(), 9, 3),
						getItemLootEntry(ModItems.FROZEN_CORE.get(), 7, 3), getItemLootEntry(ModItems.ANGELHEART_VIAL.get(), 8, 4)));

		tables.accept(STRONGHOLD_LIBRARY, getLootTable(92, getItemLootEntry(ModItems.SLIME_PEARL.get(), 8, 4)));

		tables.accept(VILLAGE_WEAPONSMITH, getLootTable(78, getItemLootEntry(ModItems.ZOMBIE_HEART.get(), 10, 5),
				getItemLootEntry(ModItems.WITCH_HAT.get(), 5, 2), getItemLootEntry(ModItems.GLOWING_WATER.get(), 7, 4)));
	}

	private LootPoolEntryContainer.Builder<?> getItemLootEntry(Item item, int weight, int maxCount) {
		return LootItem.lootTableItem(item).setWeight(weight).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, maxCount)));
	}

	private LootPoolEntryContainer.Builder<?> getItemLootEntry(Item item, int weight) {
		return LootItem.lootTableItem(item).setWeight(weight);
	}

	private static LootTable.Builder getLootTable(int emptyWeight, LootPoolEntryContainer.Builder<?>... entries) {
		LootPool.Builder pool = LootPool.lootPool().name("main");
		for (LootPoolEntryContainer.Builder<?> entry : entries) {
			pool.add(entry);
		}
		pool.add(EmptyLootItem.emptyItem().setWeight(emptyWeight));
		return LootTable.lootTable().withPool(pool);
	}

}
