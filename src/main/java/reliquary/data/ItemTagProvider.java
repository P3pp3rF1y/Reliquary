package reliquary.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import reliquary.Reliquary;
import reliquary.init.ModItems;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ItemTagProvider extends ItemTagsProvider {
	ItemTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTagProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(packOutput, lookupProvider, blockTagProvider, Reliquary.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(ItemTags.ARROWS).add(ModItems.TIPPED_ARROW.get());
		tag(ItemTags.SWORDS).add(ModItems.MAGICBANE.get(), ModItems.MERCY_CROSS.get());
		tag(ItemTags.MINING_ENCHANTABLE).add(ModItems.SHEARS_OF_WINTER.get());
		tag(Tags.Items.TOOLS_SHEAR).add(ModItems.SHEARS_OF_WINTER.get());
	}

	@Override
	public String getName() {
		return Reliquary.MOD_ID + " item tags";
	}
}
