package reliquary.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import reliquary.Reliquary;
import reliquary.init.ModBlocks;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ReliquaryBlockTagProvider extends BlockTagsProvider {
	public ReliquaryBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries, Reliquary.MOD_ID);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(BlockTags.MINEABLE_WITH_PICKAXE).addAll(ModBlocks.PEDESTALS.values().stream().map(Supplier::get).map(block -> block.builtInRegistryHolder().key()));
		tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.addAll(ModBlocks.PASSIVE_PEDESTALS.values().stream().map(Supplier::get).map(block -> block.builtInRegistryHolder().key()));
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.ALKAHESTRY_ALTAR.get().builtInRegistryHolder().key(),
				ModBlocks.APOTHECARY_CAULDRON.get().builtInRegistryHolder().key(), ModBlocks.APOTHECARY_MORTAR.get().builtInRegistryHolder().key(),
				ModBlocks.WRAITH_NODE.get().builtInRegistryHolder().key());
		tag(BlockTags.WALL_POST_OVERRIDE).add(ModBlocks.INTERDICTION_TORCH.get().builtInRegistryHolder().key());
	}
}
