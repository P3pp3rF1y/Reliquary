package reliquary.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import reliquary.Reliquary;

import java.util.Map;
import java.util.Set;

class BlockLootSubProvider extends net.minecraft.data.loot.BlockLootSubProvider {
	protected BlockLootSubProvider(LootTableSubProvider.Context context) {
		super(Set.of(), FeatureFlags.REGISTRY.allFlags(), context);
	}

	@Override
	public void generate() {
		BuiltInRegistries.BLOCK.entrySet().stream().filter(e -> e.getKey().identifier().getNamespace().equals(Reliquary.MOD_ID)).map(Map.Entry::getValue)
				.forEach(this::dropSelf);
	}

	@Override
	protected Iterable<Block> getKnownBlocks() {
		return BuiltInRegistries.BLOCK.entrySet().stream().filter(e -> e.getKey().identifier().getNamespace().equals(Reliquary.MOD_ID)).map(Map.Entry::getValue)
				.toList();
	}
}
