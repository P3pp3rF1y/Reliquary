package reliquary.compat.jade;

import reliquary.blocks.AlkahestryAltarBlock;
import reliquary.blocks.ApothecaryCauldronBlock;
import reliquary.blocks.ApothecaryMortarBlock;
import reliquary.blocks.PedestalBlock;
import reliquary.blocks.tile.AlkahestryAltarBlockEntity;
import reliquary.blocks.tile.ApothecaryMortarBlockEntity;
import reliquary.compat.jade.provider.DataProviderAltar;
import reliquary.compat.jade.provider.DataProviderCauldron;
import reliquary.compat.jade.provider.DataProviderMortar;
import reliquary.compat.jade.provider.DataProviderPedestal;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadeCompat implements IWailaPlugin {
	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.registerBlockComponent(new DataProviderMortar(), ApothecaryMortarBlock.class);
		registration.registerBlockComponent(new DataProviderCauldron(), ApothecaryCauldronBlock.class);
		registration.registerBlockComponent(new DataProviderAltar(), AlkahestryAltarBlock.class);
		registration.registerBlockComponent(new DataProviderPedestal(), PedestalBlock.class);
	}

	@Override
	public void register(IWailaCommonRegistration registration) {
		registration.registerBlockDataProvider(new DataProviderMortar(), ApothecaryMortarBlockEntity.class);
		registration.registerBlockDataProvider(new DataProviderAltar(), AlkahestryAltarBlockEntity.class);
	}
}
