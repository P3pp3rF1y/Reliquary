package reliquary.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;

public class DataGenerators {
	private DataGenerators() {}

	public static void gatherData(GatherDataEvent evt) {
		DataGenerator generator = evt.getGenerator();
		PackOutput packOutput = generator.getPackOutput();

		generator.addProvider(evt.includeServer(), new ReliquaryLootProvider(packOutput));
		BlockTagProvider blockTagProvider = new BlockTagProvider(packOutput, evt.getLookupProvider(), evt.getExistingFileHelper());
		generator.addProvider(evt.includeServer(), blockTagProvider);
		generator.addProvider(evt.includeServer(), new ItemTagProvider(packOutput, evt.getLookupProvider(), blockTagProvider.contentsGetter(), evt.getExistingFileHelper()));
		generator.addProvider(evt.includeServer(), new ModRecipeProvider(packOutput));
		generator.addProvider(evt.includeServer(), new ModFluidTagsProvider(packOutput, evt.getLookupProvider(), evt.getExistingFileHelper()));
		generator.addProvider(evt.includeServer(), new ReliquaryLootModifierProvider(packOutput));
	}
}
