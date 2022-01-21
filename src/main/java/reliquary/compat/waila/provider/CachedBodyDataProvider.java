/* TODO readd waila integration
package reliquary.compat.waila.provider;

import com.mojang.realmsclient.gui.ChatFormatting;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import reliquary.reference.Settings;
import reliquary.util.LanguageHelper;

import java.util.List;

abstract class CachedBodyDataProvider implements IWailaDataProvider {

	private List<String> cachedBody = null;
	private BlockPos cachedPosition = null;


	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		if(Settings.wailaShiftForInfo && !accessor.getPlayer().isCrouching()) {
			currenttip.add(ChatFormatting.ITALIC + LanguageHelper.getLocalization("waila.reliquary.shift_for_more") + ChatFormatting.RESET);
			return currenttip;
		}

		IWailaDataChangeIndicator changeIndicator = null;

		if(accessor.getTileEntity() instanceof IWailaDataChangeIndicator) {
			changeIndicator = (IWailaDataChangeIndicator) accessor.getTileEntity();
		}

		if(changeIndicator == null || cachedBody == null || cachedPosition == null || !cachedPosition.equals(accessor.getPosition()) || changeIndicator.getDataChanged()) {
			cachedBody = getWailaBodyToCache(itemStack, accessor, config);
			cachedPosition = accessor.getPosition();
		}

		currenttip.addAll(cachedBody);

		return currenttip;
	}

	abstract List<String> getWailaBodyToCache(ItemStack itemStack, IWailaDataAccessor accessor, IWailaConfigHandler config);
}
*/
