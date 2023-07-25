package reliquary.data;

import net.minecraft.data.loot.LootTableSubProvider;
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
import reliquary.init.ModBlocks;
import reliquary.init.ModItems;
import reliquary.reference.Reference;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class ChestLootInjectSubProvider implements LootTableSubProvider {

	protected static final Map<ResourceLocation, ResourceLocation> LOOT_INJECTS = new HashMap<>();

	private static ResourceLocation registerLootInject(ResourceLocation vanillaLootTable) {
		return LOOT_INJECTS.computeIfAbsent(vanillaLootTable, k -> new ResourceLocation(Reference.MOD_ID, INJECT_FOLDER + vanillaLootTable.getPath()));
	}

	private static final String INJECT_FOLDER = "inject/";
	public static final ResourceLocation ABANDONED_MINESHAFT = registerLootInject(BuiltInLootTables.ABANDONED_MINESHAFT);
	public static final ResourceLocation DESERT_PYRAMID = registerLootInject(BuiltInLootTables.DESERT_PYRAMID);
	public static final ResourceLocation END_CITY_TREASURE = registerLootInject(BuiltInLootTables.END_CITY_TREASURE);
	public static final ResourceLocation IGLOO_CHEST = registerLootInject(BuiltInLootTables.IGLOO_CHEST);
	public static final ResourceLocation JUNGLE_TEMPLE = registerLootInject(BuiltInLootTables.JUNGLE_TEMPLE);
	public static final ResourceLocation NETHER_BRIDGE = registerLootInject(BuiltInLootTables.NETHER_BRIDGE);
	public static final ResourceLocation SIMPLE_DUNGEON = registerLootInject(BuiltInLootTables.SIMPLE_DUNGEON);
	public static final ResourceLocation STRONGHOLD_CORRIDOR = registerLootInject(BuiltInLootTables.STRONGHOLD_CORRIDOR);
	public static final ResourceLocation STRONGHOLD_CROSSING = registerLootInject(BuiltInLootTables.STRONGHOLD_CROSSING);
	public static final ResourceLocation STRONGHOLD_LIBRARY = registerLootInject(BuiltInLootTables.STRONGHOLD_LIBRARY);
	public static final ResourceLocation VILLAGE_WEAPONSMITH = registerLootInject(BuiltInLootTables.VILLAGE_WEAPONSMITH);

	@Override
	public void generate(BiConsumer<ResourceLocation, LootTable.Builder> tables) {
		tables.accept(ABANDONED_MINESHAFT, getLootTable(61,
				getItemLootEntry(ModItems.RIB_BONE.get(), 10, 2),
				getItemLootEntry(ModItems.SLIME_PEARL.get(), 8, 3),
				getItemLootEntry(ModItems.BAT_WING.get(), 8, 3),
				getItemLootEntry(ModItems.NEBULOUS_HEART.get(), 4, 2),
				getItemLootEntry(ModItems.SQUID_BEAK.get(), 8, 4),
				getItemLootEntry(ModItems.WITCH_HAT.get(), 5, 2),
				getItemLootEntry(ModItems.ANGELIC_FEATHER.get(), 2)));

		tables.accept(DESERT_PYRAMID, getLootTable(44,
				getItemLootEntry(ModItems.RIB_BONE.get(), 10, 3),
				getItemLootEntry(ModItems.CHELICERAE.get(), 10, 2),
				getItemLootEntry(ModItems.CATALYZING_GLAND.get(), 8, 4),
				getItemLootEntry(ModItems.ZOMBIE_HEART.get(), 8, 5),
				getItemLootEntry(ModItems.NEBULOUS_HEART.get(), 5, 3),
				getItemLootEntry(ModItems.MOLTEN_CORE.get(), 5, 2),
				getItemLootEntry(ModItems.SQUID_BEAK.get(), 8, 4),
				getItemLootEntry(ModItems.ANGELIC_FEATHER.get(), 2)));

		tables.accept(END_CITY_TREASURE, getLootTable(64,
				getItemLootEntry(ModItems.NEBULOUS_HEART.get(), 30, 5),
				getItemLootEntry(ModItems.ENDER_STAFF.get(), 5),
				getItemLootEntry(ModItems.RENDING_GALE.get(), 1)));

		tables.accept(IGLOO_CHEST, getLootTable(60,
				getItemLootEntry(ModItems.FROZEN_CORE.get(), 40, 5)));

		tables.accept(JUNGLE_TEMPLE, getLootTable(62,
				getItemLootEntry(ModItems.RIB_BONE.get(), 10, 3),
				getItemLootEntry(ModItems.CATALYZING_GLAND.get(), 7, 3),
				getItemLootEntry(ModItems.BAT_WING.get(), 7, 3),
				getItemLootEntry(ModItems.NEBULOUS_HEART.get(), 6, 3),
				getItemLootEntry(ModItems.ANGELIC_FEATHER.get(), 2)));

		tables.accept(NETHER_BRIDGE, getLootTable(61,
				getItemLootEntry(ModItems.WITHERED_RIB.get(), 20, 2),
				getItemLootEntry(ModItems.SLIME_PEARL.get(), 8),
				getItemLootEntry(ModItems.MOLTEN_CORE.get(), 8, 2),
				getItemLootEntry(ModItems.VOID_TEAR.get(), 1),
				getItemLootEntry(ModItems.SALAMANDER_EYE.get(), 1),
				getItemLootEntry(ModBlocks.INTERDICTION_TORCH_ITEM.get(), 1)));

		tables.accept(SIMPLE_DUNGEON, getLootTable(68,
				getItemLootEntry(ModItems.RIB_BONE.get(), 8, 2),
				getItemLootEntry(ModItems.CHELICERAE.get(), 8, 2),
				getItemLootEntry(ModItems.ZOMBIE_HEART.get(), 8, 2),
				getItemLootEntry(ModItems.WITCH_HAT.get(), 3, 2),
				getItemLootEntry(ModItems.GLOWING_WATER.get(), 5, 2)));

		tables.accept(STRONGHOLD_CORRIDOR, getLootTable(64,
				getItemLootEntry(ModItems.RIB_BONE.get(), 7, 3),
				getItemLootEntry(ModItems.SLIME_PEARL.get(), 8, 4),
				getItemLootEntry(ModItems.NEBULOUS_HEART.get(), 8, 4),
				getItemLootEntry(ModItems.FROZEN_CORE.get(), 7, 4),
				getItemLootEntry(ModItems.ANGELHEART_VIAL.get(), 4, 2),
				getItemLootEntry(ModItems.SHEARS_OF_WINTER.get(), 2)));

		tables.accept(STRONGHOLD_CROSSING, getLootTable(55,
				getItemLootEntry(ModItems.RIB_BONE.get(), 7, 3),
				getItemLootEntry(ModItems.WITHERED_RIB.get(), 5, 3),
				getItemLootEntry(ModItems.SLIME_PEARL.get(), 9, 3),
				getItemLootEntry(ModItems.NEBULOUS_HEART.get(), 9, 3),
				getItemLootEntry(ModItems.FROZEN_CORE.get(), 7, 3),
				getItemLootEntry(ModItems.ANGELHEART_VIAL.get(), 8, 4)));

		tables.accept(STRONGHOLD_LIBRARY, getLootTable(92,
				getItemLootEntry(ModItems.SLIME_PEARL.get(), 8, 4)));

		tables.accept(VILLAGE_WEAPONSMITH, getLootTable(78,
				getItemLootEntry(ModItems.ZOMBIE_HEART.get(), 10, 5),
				getItemLootEntry(ModItems.WITCH_HAT.get(), 5, 2),
				getItemLootEntry(ModItems.GLOWING_WATER.get(), 7, 4)));
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
