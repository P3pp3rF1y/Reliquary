package reliquary.data;

import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import reliquary.reference.Reference;

import java.util.Map;
import java.util.Set;

class BlockLootSubProvider extends net.minecraft.data.loot.BlockLootSubProvider {
	protected BlockLootSubProvider() {
		super(Set.of(), FeatureFlags.REGISTRY.allFlags());
	}

	@Override
	public void generate() {
		ForgeRegistries.BLOCKS.getEntries().stream()
				.filter(e -> e.getKey().location().getNamespace().equals(Reference.MOD_ID))
				.map(Map.Entry::getValue).forEach(this::dropSelf);
	}

	@Override
	protected Iterable<Block> getKnownBlocks() {
		return ForgeRegistries.BLOCKS.getEntries().stream()
				.filter(e -> e.getKey().location().getNamespace().equals(Reference.MOD_ID))
				.map(Map.Entry::getValue)
				.toList();
	}
}
