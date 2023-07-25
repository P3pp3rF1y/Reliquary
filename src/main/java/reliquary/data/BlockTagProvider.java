package reliquary.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import reliquary.blocks.PassivePedestalBlock;
import reliquary.blocks.PedestalBlock;
import reliquary.init.ModBlocks;
import reliquary.reference.Reference;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends BlockTagsProvider {
	public BlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, Reference.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.PEDESTALS.values().stream().map(RegistryObject::get).toArray(PedestalBlock[]::new));
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.PASSIVE_PEDESTALS.values().stream().map(RegistryObject::get).toArray(PassivePedestalBlock[]::new));
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.ALKAHESTRY_ALTAR.get(), ModBlocks.APOTHECARY_CAULDRON.get(), ModBlocks.APOTHECARY_MORTAR.get(), ModBlocks.WRAITH_NODE.get());
	}
}
