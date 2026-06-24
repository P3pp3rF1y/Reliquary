package reliquary.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import reliquary.Reliquary;
import reliquary.init.ModItems;

import java.util.concurrent.CompletableFuture;

public class ReliquaryItemTagProvider extends ItemTagsProvider {
	ReliquaryItemTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries, CompletableFuture<TagLookup<Block>> blockTagProvider) {
		super(packOutput, registries, blockTagProvider, Reliquary.MOD_ID);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(ItemTags.ARROWS).add(ModItems.TIPPED_ARROW.get());
		tag(ItemTags.SWORDS).add(ModItems.MAGICBANE.get(), ModItems.MERCY_CROSS.get());
		tag(ItemTags.MINING_ENCHANTABLE).add(ModItems.SHEARS_OF_WINTER.get());
		tag(Tags.Items.TOOLS_SHEAR).add(ModItems.SHEARS_OF_WINTER.get());
		tag(ItemTags.FISHING_ENCHANTABLE).add(ModItems.ROD_OF_LYSSA.get());
	}

	@Override
	public String getName() {
		return Reliquary.MOD_ID + " item tags";
	}
}
