/* TODO readd waila integration
package reliquary.compat.waila;

import mcp.mobius.waila.api.IWailaRegistrar;
import reliquary.blocks.AlkahestryAltarBlock;
import reliquary.blocks.ApothecaryCauldronBlock;
import reliquary.blocks.ApothecaryMortarBlock;
import reliquary.blocks.PedestalBlock;
import reliquary.compat.waila.provider.DataProviderAltar;
import reliquary.compat.waila.provider.DataProviderCauldron;
import reliquary.compat.waila.provider.DataProviderMortar;
import reliquary.compat.waila.provider.DataProviderPedestal;

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
