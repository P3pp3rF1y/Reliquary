package reliquary.data;

import net.minecraft.client.data.models.EquipmentAssetProvider;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.EquipmentAsset;
import reliquary.Reliquary;
import reliquary.item.WitchHatItem;

import java.util.function.BiConsumer;

public class ReliquaryEquipmentAssetProvider extends EquipmentAssetProvider {
	public ReliquaryEquipmentAssetProvider(PackOutput packOutput) {
		super(packOutput);
	}

	@Override
	protected void registerModels(BiConsumer<ResourceKey<EquipmentAsset>, EquipmentClientInfo> output) {
		output.accept(WitchHatItem.EQUIPMENT_ASSET, EquipmentClientInfo.builder().addHumanoidLayers(Reliquary.getIdentifier("witch_hat")).build());
	}
}
