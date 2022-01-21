package reliquary.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import reliquary.reference.Reference;

public class DataGenerators {
	private DataGenerators() {}

	public static void gatherData(GatherDataEvent evt) {
		DataGenerator generator = evt.getGenerator();

		generator.addProvider(new BlockLootProvider(generator));
		generator.addProvider(new ItemTagProvider(generator, new BlockTagsProvider(generator, Reference.MOD_ID, evt.getExistingFileHelper()), evt.getExistingFileHelper()));
		generator.addProvider(new ModRecipeProvider(generator));
		generator.addProvider(new ModFluidTagsProvider(generator, evt.getExistingFileHelper()));
		generator.addProvider(new LootInjectProvider(generator));
	}
}
