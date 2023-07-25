package reliquary.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import reliquary.reference.Reference;

public class DataGenerators {
	private DataGenerators() {}

	public static void gatherData(GatherDataEvent evt) {
		DataGenerator generator = evt.getGenerator();

		generator.addProvider(evt.includeServer(), new BlockLootProvider(generator));
		BlockTagProvider blockTagProvider = new BlockTagProvider(generator, Reference.MOD_ID, evt.getExistingFileHelper());
		generator.addProvider(evt.includeServer(), blockTagProvider);
		generator.addProvider(evt.includeServer(), new ItemTagProvider(generator, blockTagProvider, evt.getExistingFileHelper()));
		generator.addProvider(evt.includeServer(), new ModRecipeProvider(generator));
		generator.addProvider(evt.includeServer(), new ModFluidTagsProvider(generator, evt.getExistingFileHelper()));
		generator.addProvider(evt.includeServer(), new LootInjectProvider(generator));
	}
}
