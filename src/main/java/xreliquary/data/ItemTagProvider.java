package xreliquary.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import xreliquary.init.ModItems;
import xreliquary.reference.Reference;

import javax.annotation.Nullable;

public class ItemTagProvider extends ItemTagsProvider {
	ItemTagProvider(DataGenerator generator, BlockTagsProvider blockTagsProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(generator, blockTagsProvider, Reference.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		tag(ItemTags.ARROWS).add(ModItems.TIPPED_ARROW.get());
	}

	@Override
	public String getName() {
		return Reference.MOD_ID + " item tags";
	}
}
