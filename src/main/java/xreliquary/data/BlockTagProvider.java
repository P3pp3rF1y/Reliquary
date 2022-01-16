package xreliquary.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import xreliquary.blocks.PassivePedestalBlock;
import xreliquary.blocks.PedestalBlock;
import xreliquary.init.ModBlocks;
import xreliquary.reference.Reference;

import javax.annotation.Nullable;

public class BlockTagProvider extends BlockTagsProvider {
	public BlockTagProvider(DataGenerator dataGenerator, String modId,
			@Nullable ExistingFileHelper existingFileHelper) {
		super(dataGenerator, Reference.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.PEDESTALS.values().stream().map(RegistryObject::get).toArray(PedestalBlock[]::new));
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.PASSIVE_PEDESTALS.values().stream().map(RegistryObject::get).toArray(PassivePedestalBlock[]::new));
	}
}
