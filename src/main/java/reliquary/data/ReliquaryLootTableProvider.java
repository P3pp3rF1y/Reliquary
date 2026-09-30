package reliquary.data;

import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReliquaryLootTableProvider extends LootTableProvider {
	ReliquaryLootTableProvider() {
		super(getAllInjectLootTables(),
				List.of(new LootTableProvider.SubProviderEntry(BlockLootSubProvider::new, LootContextParamSets.BLOCK),
						new LootTableProvider.SubProviderEntry(ChestLootInjectSubProvider::new, LootContextParamSets.BLOCK),
						new LootTableProvider.SubProviderEntry(EntityLootInjectSubProvider::new, LootContextParamSets.ENTITY)));
	}

	private static Set<ResourceKey<LootTable>> getAllInjectLootTables() {
		Set<ResourceKey<LootTable>> injectLootTables = new HashSet<>();
		injectLootTables.addAll(ChestLootInjectSubProvider.LOOT_INJECTS.values());
		injectLootTables.addAll(EntityLootInjectSubProvider.LOOT_INJECTS.values());
		return injectLootTables;
	}
}
