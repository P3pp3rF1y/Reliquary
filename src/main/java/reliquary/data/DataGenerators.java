package reliquary.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import reliquary.Reliquary;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DataGenerators {
	private DataGenerators() {
	}

	public static void gatherData(GatherDataEvent evt) {
		DataGenerator generator = evt.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		ExistingFileHelper existingFileHelper = evt.getExistingFileHelper();
		CompletableFuture<HolderLookup.Provider> registries = evt.getLookupProvider();

		DatapackBuiltinEntriesProvider builtinEntriesProvider = new DatapackBuiltinEntriesProvider(packOutput, evt.getLookupProvider(),
				new RegistrySetBuilder().add(Registries.ENCHANTMENT, ReliquaryEnchantmentProvider::bootstrap), Set.of(Reliquary.MOD_ID));
		generator.addProvider(evt.includeServer(), builtinEntriesProvider);

		generator.addProvider(evt.includeServer(), new ReliquaryLootTableProvider(packOutput, builtinEntriesProvider.getRegistryProvider()));
		BlockTagProvider blockTagProvider = new BlockTagProvider(packOutput, registries, existingFileHelper);
		generator.addProvider(evt.includeServer(), blockTagProvider);
		generator.addProvider(evt.includeServer(), new ItemTagProvider(packOutput, registries, blockTagProvider.contentsGetter(), existingFileHelper));
		generator.addProvider(evt.includeServer(), new ModRecipeProvider(packOutput, registries));
		generator.addProvider(evt.includeServer(), new ModFluidTagsProvider(packOutput, registries, existingFileHelper));
		generator.addProvider(evt.includeServer(), new ReliquaryLootModifierProvider(packOutput, registries));
		generator.addProvider(evt.includeServer(), new ReliquaryEnchantmentTagsProvider(packOutput, builtinEntriesProvider.getRegistryProvider(), existingFileHelper));
	}
}
