package reliquary.data;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class DataGenerators {
	private DataGenerators() {
	}

	public static void gatherData(GatherDataEvent.Client evt) {
		evt.createDatapackRegistryObjects(new RegistrySetBuilder().add(Registries.ENCHANTMENT, ReliquaryEnchantmentProvider::bootstrap));
		evt.createBlockAndItemTags(ReliquaryBlockTagProvider::new,
				(packOutput, registries, blockTagProvider) -> new ReliquaryItemTagProvider(packOutput, registries));
		evt.createProvider(ReliquaryLootTableProvider::new);
		evt.createProvider(ReliquaryRecipeProvider.Runner::new);
		evt.createProvider(ReliquaryFluidTagProvider::new);
		evt.createProvider(ReliquaryLootModifierProvider::new);
		evt.createProvider(ReliquaryModelProvider::new);
		evt.createProvider(ReliquaryEquipmentAssetProvider::new);
		evt.createProvider(ReliquaryEnchantmentTagsProvider::new);
	}
}
