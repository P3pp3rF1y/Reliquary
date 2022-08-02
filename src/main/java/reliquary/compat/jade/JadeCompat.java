package reliquary.compat.jade;

import mcp.mobius.waila.api.*;
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

@WailaPlugin
public class JadeCompat implements IWailaPlugin {
	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.registerComponentProvider(new DataProviderMortar(), TooltipPosition.BODY, ApothecaryMortarBlock.class);
		registration.registerComponentProvider(new DataProviderCauldron(), TooltipPosition.BODY, ApothecaryCauldronBlock.class);
		registration.registerComponentProvider(new DataProviderAltar(), TooltipPosition.BODY, AlkahestryAltarBlock.class);
		registration.registerComponentProvider(new DataProviderPedestal(), TooltipPosition.BODY, PedestalBlock.class);

		//registration.registerIconProvider(new DataProviderCauldron(), ApothecaryCauldronBlock.class);
		//registration.registerIconProvider(new DataProviderPedestal(), PedestalBlock.class);
	}

	@Override
	public void register(IWailaCommonRegistration registration) {
		registration.registerBlockDataProvider(new DataProviderMortar(), ApothecaryMortarBlockEntity.class);
		registration.registerBlockDataProvider(new DataProviderAltar(), AlkahestryAltarBlockEntity.class);
	}
}
