package reliquary.data;

import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import reliquary.Reliquary;
import reliquary.init.ModItems;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class EntityLootInjectSubProvider implements LootTableSubProvider {

	private static final String INJECT_FOLDER = "inject/";
	protected static final Map<ResourceKey<LootTable>, ResourceKey<LootTable>> LOOT_INJECTS = new HashMap<>();

	public static final ResourceKey<LootTable> BAT = createInjectLootTableRegistryKey(EntityType.BAT.getDefaultLootTable());
	public static final ResourceKey<LootTable> BLAZE = createInjectLootTableRegistryKey(EntityType.BLAZE.getDefaultLootTable());
	public static final ResourceKey<LootTable> CAVE_SPIDER = createInjectLootTableRegistryKey(EntityType.CAVE_SPIDER.getDefaultLootTable());
	public static final ResourceKey<LootTable> CREEPER = createInjectLootTableRegistryKey(EntityType.CREEPER.getDefaultLootTable());
	public static final ResourceKey<LootTable> ENDERMAN = createInjectLootTableRegistryKey(EntityType.ENDERMAN.getDefaultLootTable());
	public static final ResourceKey<LootTable> GHAST = createInjectLootTableRegistryKey(EntityType.GHAST.getDefaultLootTable());
	public static final ResourceKey<LootTable> GUARDIAN = createInjectLootTableRegistryKey(EntityType.GUARDIAN.getDefaultLootTable());
	public static final ResourceKey<LootTable> HUSK = createInjectLootTableRegistryKey(EntityType.HUSK.getDefaultLootTable());
	public static final ResourceKey<LootTable> MAGMA_CUBE = createInjectLootTableRegistryKey(EntityType.MAGMA_CUBE.getDefaultLootTable());
	public static final ResourceKey<LootTable> SKELETON = createInjectLootTableRegistryKey(EntityType.SKELETON.getDefaultLootTable());
	public static final ResourceKey<LootTable> SNOW_GOLEM = createInjectLootTableRegistryKey(EntityType.SNOW_GOLEM.getDefaultLootTable());
	public static final ResourceKey<LootTable> SLIME = createInjectLootTableRegistryKey(EntityType.SLIME.getDefaultLootTable());
	public static final ResourceKey<LootTable> SPIDER = createInjectLootTableRegistryKey(EntityType.SPIDER.getDefaultLootTable());
	public static final ResourceKey<LootTable> SQUID = createInjectLootTableRegistryKey(EntityType.SQUID.getDefaultLootTable());
	public static final ResourceKey<LootTable> STRAY = createInjectLootTableRegistryKey(EntityType.STRAY.getDefaultLootTable());
	public static final ResourceKey<LootTable> WITCH = createInjectLootTableRegistryKey(EntityType.WITCH.getDefaultLootTable());
	public static final ResourceKey<LootTable> WITHER_SKELETON = createInjectLootTableRegistryKey(EntityType.WITHER_SKELETON.getDefaultLootTable());
	public static final ResourceKey<LootTable> ZOMBIE = createInjectLootTableRegistryKey(EntityType.ZOMBIE.getDefaultLootTable());
	public static final ResourceKey<LootTable> ZOMBIE_VILLAGER = createInjectLootTableRegistryKey(EntityType.ZOMBIE_VILLAGER.getDefaultLootTable());
	public static final ResourceKey<LootTable> ZOMBIFIED_PIGLIN = createInjectLootTableRegistryKey(EntityType.ZOMBIFIED_PIGLIN.getDefaultLootTable());
	private final HolderLookup.Provider registries;

	private static ResourceKey<LootTable> createInjectLootTableRegistryKey(ResourceKey<LootTable> vanillaLootTable) {
		ResourceLocation location = Reliquary.getRL(INJECT_FOLDER + vanillaLootTable.location().getPath());
		ResourceKey<LootTable> injectLootTable = ResourceKey.create(Registries.LOOT_TABLE, location);
		LOOT_INJECTS.put(vanillaLootTable, injectLootTable);
		return injectLootTable;
	}

	public EntityLootInjectSubProvider(HolderLookup.Provider registries) {
		this.registries = registries;
	}

	@Override
	public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> tables) {

		tables.accept(BAT, getEntityLootTable(0.02f, 0.03f, 0.1f, getItemLootEntry(ModItems.BAT_WING.get(), 1)));

		tables.accept(BLAZE, getEntityLootTable(0.03f, 0.04f, 0.12f, getItemLootEntry(ModItems.MOLTEN_CORE.get(), 1)));

		tables.accept(CAVE_SPIDER, getEntityLootTable(0.03f, 0.04f, 0.12f, getItemLootEntry(ModItems.CHELICERAE.get(), 1)));

		CompoundTag poweredTag = new CompoundTag();
		poweredTag.putBoolean("powered", true);
		tables.accept(CREEPER, addLootPools(getEntityLootTable(0.02f, 0.03f, 0.1f, getItemLootEntry(ModItems.CATALYZING_GLAND.get(), 1)),
				"reliquary_powered_creeper_", 0.03f, 0.05f, 0.15f, getItemLootEntry(ModItems.EYE_OF_THE_STORM.get(), 1),
				LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, new EntityPredicate.Builder().nbt(new NbtPredicate(poweredTag)))));

		tables.accept(ENDERMAN, getEntityLootTable(0.02f, 0.03f, 0.1f, getItemLootEntry(ModItems.NEBULOUS_HEART.get(), 1)));

		tables.accept(GHAST, getEntityLootTable(0.03f, 0.04f, 0.12f, getItemLootEntry(ModItems.CATALYZING_GLAND.get(), 1)));

		tables.accept(GUARDIAN, getEntityLootTable(0.02f, 0.03f, 0.1f, getItemLootEntry(ModItems.GUARDIAN_SPIKE.get(), 1)));

		tables.accept(HUSK, getEntityLootTable(0.02f, 0.03f, 0.1f, getItemLootEntry(ModItems.ZOMBIE_HEART.get(), 1)));

		tables.accept(MAGMA_CUBE, getEntityLootTable(0.02f, 0.03f, 0.1f, getItemLootEntry(ModItems.MOLTEN_CORE.get(), 1)));

		tables.accept(SKELETON, getEntityLootTable(0.02f, 0.03f, 0.1f, getItemLootEntry(ModItems.RIB_BONE.get(), 1)));

		tables.accept(SLIME, getEntityLootTable(0.02f, 0.03f, 0.1f, getItemLootEntry(ModItems.SLIME_PEARL.get(), 1)));

		tables.accept(SNOW_GOLEM, getEntityLootTable(0.02f, 0.03f, 0.1f, getItemLootEntry(ModItems.FROZEN_CORE.get(), 1)));

		tables.accept(SPIDER, getEntityLootTable(0.02f, 0.03f, 0.1f, getItemLootEntry(ModItems.CHELICERAE.get(), 1)));

		tables.accept(SQUID, getEntityLootTable(0.02f, 0.03f, 0.1f, getItemLootEntry(ModItems.SQUID_BEAK.get(), 1)));

		tables.accept(STRAY, getEntityLootTable(0.02f, 0.03f, 0.1f, getItemLootEntry(ModItems.RIB_BONE.get(), 1)));

		tables.accept(WITCH, getEntityLootTable(0.02f, 0.03f, 0.1f, getItemLootEntry(ModItems.WITCH_HAT.get(), 1)));

		tables.accept(WITHER_SKELETON, getEntityLootTable(0.02f, 0.03f, 0.1f, getItemLootEntry(ModItems.WITHERED_RIB.get(), 1)));

		tables.accept(ZOMBIE, getEntityLootTable(0.02f, 0.03f, 0.1f, getItemLootEntry(ModItems.ZOMBIE_HEART.get(), 1)));

		tables.accept(ZOMBIE_VILLAGER, getEntityLootTable(0.02f, 0.03f, 0.1f, getItemLootEntry(ModItems.ZOMBIE_HEART.get(), 1)));

		tables.accept(ZOMBIFIED_PIGLIN, getEntityLootTable(0.03f, 0.04f, 0.12f, getItemLootEntry(ModItems.ZOMBIE_HEART.get(), 1)));
	}

	private LootPoolEntryContainer.Builder<?> getItemLootEntry(Item item, int weight) {
		return LootItem.lootTableItem(item).setWeight(weight);
	}

	private LootTable.Builder getEntityLootTable(float baseChance, float perLevelLooting, float perLevelSevering, LootPoolEntryContainer.Builder<?> entry,
			LootItemCondition.Builder... extraConditions) {
		LootTable.Builder lootTableBuilder = LootTable.lootTable();

		return addLootPools(lootTableBuilder, "reliquary_", baseChance, perLevelLooting, perLevelSevering, entry, extraConditions);
	}

	private LootTable.Builder addLootPools(LootTable.Builder lootTableBuilder, String lootPoolPrefix, float baseChance, float perLevelLooting,
			float perLevelSevering, LootPoolEntryContainer.Builder<?> entry, LootItemCondition.Builder... extraConditions) {
		LootPool.Builder lootingPool = LootPool.lootPool().name(lootPoolPrefix + "looting");
		lootingPool.add(entry);
		lootingPool.when(LootItemKilledByPlayerCondition.killedByPlayer());
		lootingPool.when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(registries, baseChance, perLevelLooting));
		for (LootItemCondition.Builder extraCondition : extraConditions) {
			lootingPool.when(extraCondition);
		}

		LootPool.Builder severingPool = LootPool.lootPool().name(lootPoolPrefix + "severing");
		severingPool.add(entry);
		severingPool.when(LootItemKilledByPlayerCondition.killedByPlayer());
		severingPool.when(LootItemRandomChanceWithSeveringBonusCondition.randomChanceAndSeveringBoost(registries, baseChance, perLevelSevering));
		for (LootItemCondition.Builder extraCondition : extraConditions) {
			severingPool.when(extraCondition);
		}

		return lootTableBuilder.withPool(lootingPool).withPool(severingPool);
	}
}
