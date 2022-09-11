package reliquary.client.init;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import reliquary.client.model.VoidTearModel;
import reliquary.init.ModItems;
import reliquary.util.RegistryHelper;

public class ItemModels {
	private ItemModels() {}

	public static void onModelBake(ModelEvent.BakingCompleted event) {
		ModelResourceLocation key = new ModelResourceLocation(RegistryHelper.getRegistryName(ModItems.VOID_TEAR.get()), "inventory");
		VoidTearModel voidTearModel = new VoidTearModel(event.getModelManager().getModel(key));

		event.getModels().put(key, voidTearModel);
	}
}
