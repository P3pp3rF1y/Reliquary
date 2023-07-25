package reliquary.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import reliquary.init.ModItems;
import reliquary.reference.Reference;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ItemTagProvider extends ItemTagsProvider {
	ItemTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTagProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(packOutput, lookupProvider, blockTagProvider, Reference.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(ItemTags.ARROWS).add(ModItems.TIPPED_ARROW.get());
	}

	@Override
	public String getName() {
		return Reference.MOD_ID + " item tags";
	}
}
