package xreliquary.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import xreliquary.init.ModBlocks;
import xreliquary.init.ModItems;
import xreliquary.init.RandomChanceLootingSeveringCondition;
import xreliquary.reference.Reference;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class LootInjectProvider implements DataProvider {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private final DataGenerator generator;

	LootInjectProvider(DataGenerator generator) {
		this.generator = generator;
	}

	@Override
	public void run(HashCache cache) throws IOException {
		Map<ResourceLocation, LootTable.Builder> tables = new HashMap<>();

		tables.put(BuiltInLootTables.ABANDONED_MINESHAFT, getLootTable(61,
				getItemLootEntry(ModItems.RIB_BONE.get(), 10, 2),
				getItemLootEntry(ModItems.SLIME_PEARL.get(), 8, 3),
				getItemLootEntry(ModItems.BAT_WING.get(), 8, 3),
				getItemLootEntry(ModItems.NEBULOUS_HEART.get(), 4, 2),
				getItemLootEntry(ModItems.SQUID_BEAK.get(), 8, 4),
				getItemLootEntry(ModItems.WITCH_HAT.get(), 5, 2),
				getItemLootEntry(ModItems.ANGELIC_FEATHER.get(), 2)));

		tables.put(BuiltInLootTables.DESERT_PYRAMID, getLootTable(44,
				getItemLootEntry(ModItems.RIB_BONE.get(), 10, 3),
				getItemLootEntry(ModItems.CHELICERAE.get(), 10, 2),
				getItemLootEntry(ModItems.CATALYZING_GLAND.get(), 8, 4),
				getItemLootEntry(ModItems.ZOMBIE_HEART.get(), 8, 5),
				getItemLootEntry(ModItems.NEBULOUS_HEART.get(), 5, 3),
				getItemLootEntry(ModItems.MOLTEN_CORE.get(), 5, 2),
				getItemLootEntry(ModItems.SQUID_BEAK.get(), 8, 4),
				getItemLootEntry(ModItems.ANGELIC_FEATHER.get(), 2)));

		tables.put(BuiltInLootTables.END_CITY_TREASURE, getLootTable(64,
				getItemLootEntry(ModItems.NEBULOUS_HEART.get(), 30, 5),
				getItemLootEntry(ModItems.ENDER_STAFF.get(), 5),
				getItemLootEntry(ModItems.RENDING_GALE.get(), 1)));

		tables.put(BuiltInLootTables.IGLOO_CHEST, getLootTable(60,
				getItemLootEntry(ModItems.FROZEN_CORE.get(), 40, 5)));

		tables.put(BuiltInLootTables.JUNGLE_TEMPLE, getLootTable(62,
				getItemLootEntry(ModItems.RIB_BONE.get(), 10, 3),
				getItemLootEntry(ModItems.CATALYZING_GLAND.get(), 7, 3),
				getItemLootEntry(ModItems.BAT_WING.get(), 7, 3),
				getItemLootEntry(ModItems.NEBULOUS_HEART.get(), 6, 3),
				getItemLootEntry(ModItems.ANGELIC_FEATHER.get(), 2)));

		tables.put(BuiltInLootTables.NETHER_BRIDGE, getLootTable(61,
				getItemLootEntry(ModItems.WITHERED_RIB.get(), 20, 2),
				getItemLootEntry(ModItems.SLIME_PEARL.get(), 8),
				getItemLootEntry(ModItems.MOLTEN_CORE.get(), 8, 2),
				getItemLootEntry(ModItems.VOID_TEAR.get(), 1),
				getItemLootEntry(ModItems.SALAMANDER_EYE.get(), 1),
				getItemLootEntry(ModBlocks.INTERDICTION_TORCH_ITEM.get(), 1)));

		tables.put(BuiltInLootTables.SIMPLE_DUNGEON, getLootTable(68,
				getItemLootEntry(ModItems.RIB_BONE.get(), 8, 2),
				getItemLootEntry(ModItems.CHELICERAE.get(), 8, 2),
				getItemLootEntry(ModItems.ZOMBIE_HEART.get(), 8, 2),
				getItemLootEntry(ModItems.WITCH_HAT.get(), 3, 2),
				getItemLootEntry(ModItems.GLOWING_WATER.get(), 5, 2)));

		tables.put(BuiltInLootTables.STRONGHOLD_CORRIDOR, getLootTable(64,
				getItemLootEntry(ModItems.RIB_BONE.get(), 7, 3),
				getItemLootEntry(ModItems.SLIME_PEARL.get(), 8, 4),
				getItemLootEntry(ModItems.NEBULOUS_HEART.get(), 8, 4),
				getItemLootEntry(ModItems.FROZEN_CORE.get(), 7, 4),
				getItemLootEntry(ModItems.ANGELHEART_VIAL.get(), 4, 2),
				getItemLootEntry(ModItems.SHEARS_OF_WINTER.get(), 2)));

		tables.put(BuiltInLootTables.STRONGHOLD_CROSSING, getLootTable(55,
				getItemLootEntry(ModItems.RIB_BONE.get(), 7, 3),
				getItemLootEntry(ModItems.WITHERED_RIB.get(), 5, 3),
				getItemLootEntry(ModItems.SLIME_PEARL.get(), 9, 3),
				getItemLootEntry(ModItems.NEBULOUS_HEART.get(), 9, 3),
				getItemLootEntry(ModItems.FROZEN_CORE.get(), 7, 3),
				getItemLootEntry(ModItems.ANGELHEART_VIAL.get(), 8, 4)));

		tables.put(BuiltInLootTables.STRONGHOLD_LIBRARY, getLootTable(92,
				getItemLootEntry(ModItems.SLIME_PEARL.get(), 8, 4)));

		tables.put(BuiltInLootTables.VILLAGE_WEAPONSMITH, getLootTable(78,
				getItemLootEntry(ModItems.ZOMBIE_HEART.get(), 10, 5),
				getItemLootEntry(ModItems.WITCH_HAT.get(), 5, 2),
				getItemLootEntry(ModItems.GLOWING_WATER.get(), 7, 4)));

		for (Map.Entry<ResourceLocation, LootTable.Builder> e : tables.entrySet()) {
			Path path = getPath(generator.getOutputFolder(), e.getKey());
			DataProvider.save(GSON, cache, LootTables.serialize(e.getValue().setParamSet(LootContextParamSets.CHEST).build()), path);
		}

		tables.clear();

		tables.put(EntityType.BAT.getDefaultLootTable(), getEntityLootTable(0.02f, 0.03f, 0.1f,
				getItemLootEntry(ModItems.BAT_WING.get(), 1)));

		tables.put(EntityType.BLAZE.getDefaultLootTable(), getEntityLootTable(0.03f, 0.04f, 0.12f,
				getItemLootEntry(ModItems.MOLTEN_CORE.get(), 1)));

		tables.put(EntityType.CAVE_SPIDER.getDefaultLootTable(), getEntityLootTable(0.03f, 0.04f, 0.12f,
				getItemLootEntry(ModItems.CHELICERAE.get(), 1)));

		CompoundTag poweredTag = new CompoundTag();
		poweredTag.putBoolean("powered", true);
		tables.put(EntityType.CREEPER.getDefaultLootTable(), getEntityLootTable(0.02f, 0.03f, 0.1f,
				getItemLootEntry(ModItems.CATALYZING_GLAND.get(), 1))
				.withPool(LootPool.lootPool().name("powered_creeper").add(LootItem.lootTableItem(ModItems.EYE_OF_THE_STORM.get()))
						.when(LootItemKilledByPlayerCondition.killedByPlayer())
						.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, new EntityPredicate.Builder().nbt(new NbtPredicate(poweredTag))))
						.when(RandomChanceLootingSeveringCondition.randomChanceLootingSevering(0.03f, 0.05f, 0.15f))
				));

		tables.put(EntityType.ENDERMAN.getDefaultLootTable(), getEntityLootTable(0.02f, 0.03f, 0.1f,
				getItemLootEntry(ModItems.NEBULOUS_HEART.get(), 1)));

		tables.put(EntityType.GHAST.getDefaultLootTable(), getEntityLootTable(0.03f, 0.04f, 0.12f,
				getItemLootEntry(ModItems.CATALYZING_GLAND.get(), 1)));

		tables.put(EntityType.GUARDIAN.getDefaultLootTable(), getEntityLootTable(0.02f, 0.03f, 0.1f,
				getItemLootEntry(ModItems.GUARDIAN_SPIKE.get(), 1)));

		tables.put(EntityType.HUSK.getDefaultLootTable(), getEntityLootTable(0.02f, 0.03f, 0.1f,
				getItemLootEntry(ModItems.ZOMBIE_HEART.get(), 1)));

		tables.put(EntityType.MAGMA_CUBE.getDefaultLootTable(), getEntityLootTable(0.02f, 0.03f, 0.1f,
				getItemLootEntry(ModItems.MOLTEN_CORE.get(), 1)));

		tables.put(EntityType.SKELETON.getDefaultLootTable(), getEntityLootTable(0.02f, 0.03f, 0.1f,
				getItemLootEntry(ModItems.RIB_BONE.get(), 1)));

		tables.put(EntityType.SLIME.getDefaultLootTable(), getEntityLootTable(0.02f, 0.03f, 0.1f,
				getItemLootEntry(ModItems.SLIME_PEARL.get(), 1)));

		tables.put(EntityType.SNOW_GOLEM.getDefaultLootTable(), getEntityLootTable(0.02f, 0.03f, 0.1f,
				getItemLootEntry(ModItems.FROZEN_CORE.get(), 1)));

		tables.put(EntityType.SPIDER.getDefaultLootTable(), getEntityLootTable(0.02f, 0.03f, 0.1f,
				getItemLootEntry(ModItems.CHELICERAE.get(), 1)));

		tables.put(EntityType.SQUID.getDefaultLootTable(), getEntityLootTable(0.02f, 0.03f, 0.1f,
				getItemLootEntry(ModItems.SQUID_BEAK.get(), 1)));

		tables.put(EntityType.STRAY.getDefaultLootTable(), getEntityLootTable(0.02f, 0.03f, 0.1f,
				getItemLootEntry(ModItems.RIB_BONE.get(), 1)));

		tables.put(EntityType.WITCH.getDefaultLootTable(), getEntityLootTable(0.02f, 0.03f, 0.1f,
				getItemLootEntry(ModItems.WITCH_HAT.get(), 1)));

		tables.put(EntityType.WITHER_SKELETON.getDefaultLootTable(), getEntityLootTable(0.02f, 0.03f, 0.1f,
				getItemLootEntry(ModItems.WITHERED_RIB.get(), 1)));

		tables.put(EntityType.ZOMBIE.getDefaultLootTable(), getEntityLootTable(0.02f, 0.03f, 0.1f,
				getItemLootEntry(ModItems.ZOMBIE_HEART.get(), 1)));

		tables.put(EntityType.ZOMBIE_VILLAGER.getDefaultLootTable(), getEntityLootTable(0.02f, 0.03f, 0.1f,
				getItemLootEntry(ModItems.ZOMBIE_HEART.get(), 1)));

		tables.put(EntityType.ZOMBIFIED_PIGLIN.getDefaultLootTable(), getEntityLootTable(0.03f, 0.04f, 0.12f,
				getItemLootEntry(ModItems.ZOMBIE_HEART.get(), 1)));

		for (Map.Entry<ResourceLocation, LootTable.Builder> e : tables.entrySet()) {
			Path path = getPath(generator.getOutputFolder(), e.getKey());
			DataProvider.save(GSON, cache, LootTables.serialize(e.getValue().setParamSet(LootContextParamSets.ENTITY).build()), path);
		}
	}

	@Override
	public String getName() {
		return "SophisticatedBackpacks chest loot additions";
	}

	private static Path getPath(Path root, ResourceLocation id) {
		return root.resolve("data/" + Reference.MOD_ID + "/loot_tables/inject/" + id.getPath() + ".json");
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

	private static LootTable.Builder getEntityLootTable(float baseChance, float lootingMultiplier, float severingMultiplier, LootPoolEntryContainer.Builder<?>... entries) {
		LootPool.Builder pool = LootPool.lootPool().name("main");
		for (LootPoolEntryContainer.Builder<?> entry : entries) {
			pool.add(entry);
		}
		pool.when(LootItemKilledByPlayerCondition.killedByPlayer());
		pool.when(RandomChanceLootingSeveringCondition.randomChanceLootingSevering(baseChance, lootingMultiplier, severingMultiplier));
		return LootTable.lootTable().withPool(pool);
	}
}
