package reliquary.data;

import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.data.event.GatherDataRegistryEntriesEvent;

public class DataGenerators {
	private DataGenerators() {
	}

	public static void gatherRegistryEntries(GatherDataRegistryEntriesEvent evt) {
		evt.add(Registries.ENCHANTMENT, ReliquaryEnchantmentProvider::bootstrap).add(Registries.LOOT_TABLE, new ReliquaryLootTableProvider())
				.recipe(ReliquaryRecipeProvider::new);
	}

	public static void gatherData(GatherDataEvent.Client evt) {
		evt.createBlockAndItemTags(ReliquaryBlockTagProvider::new,
				(packOutput, registries, blockTagProvider) -> new ReliquaryItemTagProvider(packOutput, registries));
		evt.createProvider(EntityTypeTagProvider::new);
		evt.createProvider(ReliquaryFluidTagProvider::new);
		evt.createProvider(ReliquaryLootModifierProvider::new);
		evt.createProvider(ReliquaryModelProvider::new);
		evt.createProvider(ReliquaryEquipmentAssetProvider::new);
		evt.createProvider(ReliquaryEnchantmentTagsProvider::new);
	}
}
