/* TODO readd waila integration
package xreliquary.compat.waila;

import mcp.mobius.waila.api.IWailaRegistrar;
import xreliquary.blocks.AlkahestryAltarBlock;
import xreliquary.blocks.ApothecaryCauldronBlock;
import xreliquary.blocks.ApothecaryMortarBlock;
import xreliquary.blocks.PedestalBlock;
import xreliquary.compat.waila.provider.DataProviderAltar;
import xreliquary.compat.waila.provider.DataProviderCauldron;
import xreliquary.compat.waila.provider.DataProviderMortar;
import xreliquary.compat.waila.provider.DataProviderPedestal;

public class WailaCallbackHandler {
	public static void callbackRegister(IWailaRegistrar registrar) {
		registrar.registerBodyProvider(new DataProviderMortar(), ApothecaryMortarBlock.class);
		registrar.registerBodyProvider(new DataProviderCauldron(), ApothecaryCauldronBlock.class);
		registrar.registerBodyProvider(new DataProviderAltar(), AlkahestryAltarBlock.class);
		registrar.registerBodyProvider(new DataProviderPedestal(), PedestalBlock.class);

		registrar.registerStackProvider(new DataProviderCauldron(), ApothecaryCauldronBlock.class);
		registrar.registerStackProvider(new DataProviderPedestal(), PedestalBlock.class);
	}
}
*/
