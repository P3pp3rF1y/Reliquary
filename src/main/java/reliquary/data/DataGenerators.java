package reliquary.data;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class DataGenerators {
	private DataGenerators() {
	}

	public static void gatherData(GatherDataEvent.Client evt) {
		evt.createWorldRegistryObjects(new RegistrySetBuilder().add(Registries.ENCHANTMENT, ReliquaryEnchantmentProvider::bootstrap));
		evt.createReloadableRegistryObjects(new RegistrySetBuilder().add(Registries.LOOT_TABLE, new ReliquaryLootTableProvider())
				.add(RecipeProvider.asBootstrap(ReliquaryRecipeProvider::new)));
		evt.createBlockAndItemTags(ReliquaryBlockTagProvider::new,
				(packOutput, registries, blockTagProvider) -> new ReliquaryItemTagProvider(packOutput, registries));
		evt.createProvider(ReliquaryFluidTagProvider::new);
		evt.createProvider(ReliquaryLootModifierProvider::new);
		evt.createProvider(ReliquaryModelProvider::new);
		evt.createProvider(ReliquaryEquipmentAssetProvider::new);
		evt.createProvider(ReliquaryEnchantmentTagsProvider::new);
	}
}
