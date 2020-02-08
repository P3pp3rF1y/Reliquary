package xreliquary.data;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import xreliquary.reference.Reference;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
	private DataGenerators() {}

	@SubscribeEvent
	public static void gatherData(GatherDataEvent evt) {
		evt.getGenerator().addProvider(new BlockLootProvider(evt.getGenerator()));
	}
}
