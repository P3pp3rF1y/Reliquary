package xreliquary.data;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import xreliquary.init.ModItems;
import xreliquary.reference.Reference;

public class ItemTagProvider extends ItemTagsProvider {
	ItemTagProvider(DataGenerator generator, BlockTagsProvider blockTagsProvider) {
		super(generator, blockTagsProvider, Reference.MOD_ID, null);
	}

	@Override
	protected void registerTags() {
		getOrCreateBuilder(ItemTags.ARROWS).add(ModItems.TIPPED_ARROW.get());
	}

	@Override
	public String getName() {
		return Reference.MOD_ID + " item tags";
	}
}
