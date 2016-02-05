/*
package xreliquary.client;


import net.minecraft.client.resources.model.IBakedModel;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


public class ModelBakeEventHandler
{
	public static final ModelBakeEventHandler instance = new ModelBakeEventHandler();

	public ModelBakeEventHandler() {};

	// Called after all the other baked models have been added to the modelRegistry
	// Allows us to manipulate the modelRegistry before BlockModelShapes caches them.
	@SubscribeEvent
	public void onModelBakeEvent(ModelBakeEvent event)
	{
		Object object =  event.modelRegistry.getObject(MortarSmartItemModel.modelResourceLocation);
		if (object instanceof IBakedModel) {
			IBakedModel existingModel = (IBakedModel)object;
			MortarSmartItemModel customModel = new MortarSmartItemModel(existingModel);
			event.modelRegistry.putObject(MortarSmartItemModel.modelResourceLocation, customModel);
		}

		object = event.modelRegistry.getObject( CauldronSmartBlockModel.modelResourceLocation );
		if (object instanceof IBakedModel) {
			IBakedModel existingModel = (IBakedModel)object;
			CauldronSmartBlockModel customModel = new CauldronSmartBlockModel(existingModel);
			event.modelRegistry.putObject(CauldronSmartBlockModel.modelResourceLocation, customModel);
		}
	}
}
*/
