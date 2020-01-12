package xreliquary.client.init;

import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xreliquary.client.model.VoidTearModel;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemModels {
	private ItemModels() {}

	@SubscribeEvent
	public static void onModelBake(ModelBakeEvent event) {
		ModelResourceLocation key = new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID, Names.Items.VOID_TEAR), "inventory");
		VoidTearModel voidTearModel = new VoidTearModel(event.getModelRegistry().get(key));

		event.getModelRegistry().put(key, voidTearModel);
	}
}
