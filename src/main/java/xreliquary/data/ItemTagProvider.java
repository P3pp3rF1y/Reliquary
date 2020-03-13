package xreliquary.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import xreliquary.init.ModItems;
import xreliquary.reference.Reference;

public class ItemTagProvider extends ItemTagsProvider {
	ItemTagProvider(DataGenerator generator) {
		super(generator);
	}

	@Override
	protected void registerTags() {
		getBuilder(ItemTags.ARROWS).add(ModItems.TIPPED_ARROW);
	}

	@Override
	public String getName() {
		return Reference.MOD_ID + " item tags";
	}
}
