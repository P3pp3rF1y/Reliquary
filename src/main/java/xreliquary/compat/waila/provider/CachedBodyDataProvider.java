package xreliquary.compat.waila.provider;

import com.mojang.realmsclient.gui.ChatFormatting;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import xreliquary.reference.Settings;

import java.util.List;

public abstract class CachedBodyDataProvider implements IWailaDataProvider {

	List<String> cachedBody = null;
	BlockPos cachedPosition = null;

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		if(Settings.wailaShiftForInfo && !accessor.getPlayer().isSneaking()) {
			currenttip.add(ChatFormatting.ITALIC + I18n.translateToLocal("waila.xreliquary.shift_for_more") + ChatFormatting.RESET);
			return currenttip;
		}

		IWailaDataChangeIndicator changeIndicator = null;

		if(accessor.getTileEntity() instanceof IWailaDataChangeIndicator) {
			changeIndicator = (IWailaDataChangeIndicator) accessor.getTileEntity();
		}

		if(changeIndicator == null || cachedBody == null || cachedPosition == null || !cachedPosition.equals(accessor.getPosition()) || changeIndicator.getDataChanged()) {
			cachedBody = getWailaBodyToCache(itemStack, currenttip, accessor, config);
			cachedPosition = accessor.getPosition();
		}

		return cachedBody;
	}

	abstract List<String> getWailaBodyToCache(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config);
}
