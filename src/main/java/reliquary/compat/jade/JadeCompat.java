package reliquary.compat.jade;

import reliquary.block.AlkahestryAltarBlock;
import reliquary.block.ApothecaryCauldronBlock;
import reliquary.block.ApothecaryMortarBlock;
import reliquary.block.PedestalBlock;
import reliquary.block.tile.AlkahestryAltarBlockEntity;
import reliquary.block.tile.ApothecaryMortarBlockEntity;
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
