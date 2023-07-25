package reliquary.data;

import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import reliquary.init.ModItems;
import reliquary.reference.Reference;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class EntityLootInjectSubProvider implements LootTableSubProvider {

	private static final String INJECT_FOLDER = "inject/";
	protected static final Map<ResourceLocation, ResourceLocation> LOOT_INJECTS = new HashMap<>();
	private static ResourceLocation registerLootInject(ResourceLocation vanillaLootTable) {
		return LOOT_INJECTS.computeIfAbsent(vanillaLootTable, k -> new ResourceLocation(Reference.MOD_ID, INJECT_FOLDER + vanillaLootTable.getPath()));
	}

	public static final ResourceLocation BAT = registerLootInject(EntityType.BAT.getDefaultLootTable());
	public static final ResourceLocation BLAZE = registerLootInject(EntityType.BLAZE.getDefaultLootTable());
	public static final ResourceLocation CAVE_SPIDER = registerLootInject(EntityType.CAVE_SPIDER.getDefaultLootTable());
	public static final ResourceLocation CREEPER = registerLootInject(EntityType.CREEPER.getDefaultLootTable());
	public static final ResourceLocation ENDERMAN = registerLootInject(EntityType.ENDERMAN.getDefaultLootTable());
	public static final ResourceLocation GHAST = registerLootInject(EntityType.GHAST.getDefaultLootTable());
	public static final ResourceLocation GUARDIAN = registerLootInject(EntityType.GUARDIAN.getDefaultLootTable());
	public static final ResourceLocation HUSK = registerLootInject(EntityType.HUSK.getDefaultLootTable());
	public static final ResourceLocation MAGMA_CUBE = registerLootInject(EntityType.MAGMA_CUBE.getDefaultLootTable());
	public static final ResourceLocation SKELETON = registerLootInject(EntityType.SKELETON.getDefaultLootTable());
	public static final ResourceLocation SNOW_GOLEM = registerLootInject(EntityType.SNOW_GOLEM.getDefaultLootTable());
	public static final ResourceLocation SLIME = registerLootInject(EntityType.SLIME.getDefaultLootTable());
	public static final ResourceLocation SPIDER = registerLootInject(EntityType.SPIDER.getDefaultLootTable());
	public static final ResourceLocation SQUID = registerLootInject(EntityType.SQUID.getDefaultLootTable());
	public static final ResourceLocation STRAY = registerLootInject(EntityType.STRAY.getDefaultLootTable());
	public static final ResourceLocation WITCH = registerLootInject(EntityType.WITCH.getDefaultLootTable());
	public static final ResourceLocation WITHER_SKELETON = registerLootInject(EntityType.WITHER_SKELETON.getDefaultLootTable());
	public static final ResourceLocation ZOMBIE = registerLootInject(EntityType.ZOMBIE.getDefaultLootTable());
	public static final ResourceLocation ZOMBIE_VILLAGER = registerLootInject(EntityType.ZOMBIE_VILLAGER.getDefaultLootTable());
	public static final ResourceLocation ZOMBIFIED_PIGLIN = registerLootInject(EntityType.ZOMBIFIED_PIGLIN.getDefaultLootTable());

	@Override
	public void generate(BiConsumer<ResourceLocation, LootTable.Builder> tables) {
		tables.accept(BAT, getEntityLootTable(0.02f, 0.03f, 0.1f,
				getItemLootEntry(ModItems.BAT_WING.get(), 1)));

		tables.accept(BLAZE, getEntityLootTable(0.03f, 0.04f, 0.12f,
				getItemLootEntry(ModItems.MOLTEN_CORE.get(), 1)));

		tables.accept(CAVE_SPIDER, getEntityLootTable(0.03f, 0.04f, 0.12f,
				getItemLootEntry(ModItems.CHELICERAE.get(), 1)));

		CompoundTag poweredTag = new CompoundTag();
		poweredTag.putBoolean("powered", true);
		tables.accept(CREEPER, getEntityLootTable(0.02f, 0.03f, 0.1f,
				getItemLootEntry(ModItems.CATALYZING_GLAND.get(), 1))
				.withPool(LootPool.lootPool().name("powered_creeper").add(LootItem.lootTableItem(ModItems.EYE_OF_THE_STORM.get()))
						.when(LootItemKilledByPlayerCondition.killedByPlayer())
						.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, new EntityPredicate.Builder().nbt(new NbtPredicate(poweredTag))))
						.when(RandomChanceLootingSeveringCondition.randomChanceLootingSevering(0.03f, 0.05f, 0.15f))
				));

		tables.accept(ENDERMAN, getEntityLootTable(0.02f, 0.03f, 0.1f,
				getItemLootEntry(ModItems.NEBULOUS_HEART.get(), 1)));

		tables.accept(GHAST, getEntityLootTable(0.03f, 0.04f, 0.12f,
				getItemLootEntry(ModItems.CATALYZING_GLAND.get(), 1)));

		tables.accept(GUARDIAN, getEntityLootTable(0.02f, 0.03f, 0.1f,
				getItemLootEntry(ModItems.GUARDIAN_SPIKE.get(), 1)));

		tables.accept(HUSK, getEntityLootTable(0.02f, 0.03f, 0.1f,
				getItemLootEntry(ModItems.ZOMBIE_HEART.get(), 1)));

		tables.accept(MAGMA_CUBE, getEntityLootTable(0.02f, 0.03f, 0.1f,
				getItemLootEntry(ModItems.MOLTEN_CORE.get(), 1)));

		tables.accept(SKELETON, getEntityLootTable(0.02f, 0.03f, 0.1f,
				getItemLootEntry(ModItems.RIB_BONE.get(), 1)));

		tables.accept(SLIME, getEntityLootTable(0.02f, 0.03f, 0.1f,
				getItemLootEntry(ModItems.SLIME_PEARL.get(), 1)));

		tables.accept(SNOW_GOLEM, getEntityLootTable(0.02f, 0.03f, 0.1f,
				getItemLootEntry(ModItems.FROZEN_CORE.get(), 1)));

		tables.accept(SPIDER, getEntityLootTable(0.02f, 0.03f, 0.1f,
				getItemLootEntry(ModItems.CHELICERAE.get(), 1)));

		tables.accept(SQUID, getEntityLootTable(0.02f, 0.03f, 0.1f,
				getItemLootEntry(ModItems.SQUID_BEAK.get(), 1)));

		tables.accept(STRAY, getEntityLootTable(0.02f, 0.03f, 0.1f,
				getItemLootEntry(ModItems.RIB_BONE.get(), 1)));

		tables.accept(WITCH, getEntityLootTable(0.02f, 0.03f, 0.1f,
				getItemLootEntry(ModItems.WITCH_HAT.get(), 1)));

		tables.accept(WITHER_SKELETON, getEntityLootTable(0.02f, 0.03f, 0.1f,
				getItemLootEntry(ModItems.WITHERED_RIB.get(), 1)));

		tables.accept(ZOMBIE, getEntityLootTable(0.02f, 0.03f, 0.1f,
				getItemLootEntry(ModItems.ZOMBIE_HEART.get(), 1)));

		tables.accept(ZOMBIE_VILLAGER, getEntityLootTable(0.02f, 0.03f, 0.1f,
				getItemLootEntry(ModItems.ZOMBIE_HEART.get(), 1)));

		tables.accept(ZOMBIFIED_PIGLIN, getEntityLootTable(0.03f, 0.04f, 0.12f,
				getItemLootEntry(ModItems.ZOMBIE_HEART.get(), 1)));
	}

	private LootPoolEntryContainer.Builder<?> getItemLootEntry(Item item, int weight) {
		return LootItem.lootTableItem(item).setWeight(weight);
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
