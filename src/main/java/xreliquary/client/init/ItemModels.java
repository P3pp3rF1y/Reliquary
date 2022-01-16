package xreliquary.client.init;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import xreliquary.client.model.VoidTearModel;
import xreliquary.init.ModItems;
import xreliquary.util.RegistryHelper;

public class ItemModels {
	private ItemModels() {}

	public static void onModelBake(ModelBakeEvent event) {
		ModelResourceLocation key = new ModelResourceLocation(RegistryHelper.getRegistryName(ModItems.VOID_TEAR.get()), "inventory");
		VoidTearModel voidTearModel = new VoidTearModel(event.getModelRegistry().get(key));

		event.getModelRegistry().put(key, voidTearModel);
	}
}
