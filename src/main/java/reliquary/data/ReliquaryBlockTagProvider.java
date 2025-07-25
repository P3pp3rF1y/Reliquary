package reliquary.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import reliquary.Reliquary;
import reliquary.block.PassivePedestalBlock;
import reliquary.block.PedestalBlock;
import reliquary.init.ModBlocks;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ReliquaryBlockTagProvider extends BlockTagsProvider {
	public ReliquaryBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries, Reliquary.MOD_ID);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.PEDESTALS.values().stream().map(Supplier::get).toArray(PedestalBlock[]::new));
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.PASSIVE_PEDESTALS.values().stream().map(Supplier::get).toArray(PassivePedestalBlock[]::new));
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.ALKAHESTRY_ALTAR.get(), ModBlocks.APOTHECARY_CAULDRON.get(), ModBlocks.APOTHECARY_MORTAR.get(), ModBlocks.WRAITH_NODE.get());
		tag(BlockTags.WALL_POST_OVERRIDE).add(ModBlocks.INTERDICTION_TORCH.get());
	}
}
