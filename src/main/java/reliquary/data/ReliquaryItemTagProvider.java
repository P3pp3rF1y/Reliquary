package reliquary.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import reliquary.Reliquary;
import reliquary.init.ModItems;

import java.util.concurrent.CompletableFuture;

public class ReliquaryItemTagProvider extends ItemTagsProvider {
	ReliquaryItemTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
		super(packOutput, registries, Reliquary.MOD_ID);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(ItemTags.ARROWS).add(ModItems.TIPPED_ARROW.get().builtInRegistryHolder().key());
		tag(ItemTags.SWORDS).add(ModItems.MAGICBANE.get().builtInRegistryHolder().key(), ModItems.MERCY_CROSS.get().builtInRegistryHolder().key());
		tag(ItemTags.MINING_ENCHANTABLE).add(ModItems.SHEARS_OF_WINTER.get().builtInRegistryHolder().key());
		tag(Tags.Items.TOOLS_SHEAR).add(ModItems.SHEARS_OF_WINTER.get().builtInRegistryHolder().key());
		tag(ItemTags.FISHING_ENCHANTABLE).add(ModItems.ROD_OF_LYSSA.get().builtInRegistryHolder().key());
	}

	@Override
	public String getName() {
		return Reliquary.MOD_ID + " item tags";
	}
}
