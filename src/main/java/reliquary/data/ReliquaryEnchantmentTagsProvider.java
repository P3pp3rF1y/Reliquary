package reliquary.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.tags.EnchantmentTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import reliquary.Reliquary;

import java.util.concurrent.CompletableFuture;

public class ReliquaryEnchantmentTagsProvider extends EnchantmentTagsProvider {
	public ReliquaryEnchantmentTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(packOutput, lookupProvider, Reliquary.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(EnchantmentTags.NON_TREASURE).add(ReliquaryEnchantmentProvider.SEVERING);
		tag(Tags.Enchantments.INCREASE_ENTITY_DROPS).add(ReliquaryEnchantmentProvider.SEVERING);
	}
}
