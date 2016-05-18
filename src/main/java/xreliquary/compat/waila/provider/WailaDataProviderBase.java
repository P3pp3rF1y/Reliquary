package xreliquary.compat.waila.provider;

import com.mojang.realmsclient.gui.ChatFormatting;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import xreliquary.reference.Settings;

import java.util.List;

public abstract class WailaDataProviderBase implements IWailaDataProvider {

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		if(Settings.wailaShiftForInfo && !accessor.getPlayer().isSneaking()) {
			currenttip.add(ChatFormatting.ITALIC + I18n.translateToLocal("waila.xreliquary.shift_for_more") + ChatFormatting.RESET);
			return currenttip;
		}

		return getWailaBodyInternal(itemStack, currenttip, accessor, config);
	}

	abstract List<String> getWailaBodyInternal(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config);
}
