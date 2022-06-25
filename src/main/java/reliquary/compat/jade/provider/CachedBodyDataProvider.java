package reliquary.compat.jade.provider;

import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import mcp.mobius.waila.api.ui.IElement;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import reliquary.compat.waila.provider.IWailaDataChangeIndicator;
import reliquary.reference.Settings;
import reliquary.util.LanguageHelper;

import java.util.List;

public abstract class CachedBodyDataProvider implements IComponentProvider {

	private List<List<IElement>> cachedBody = null;
	private BlockPos cachedPosition = null;

	@Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig pluginConfig) {
        if(Settings.CLIENT.wailaShiftForInfo.get() && !accessor.getPlayer().isCrouching()) {
            tooltip.add(Component.nullToEmpty(ChatFormatting.ITALIC + LanguageHelper.getLocalization("waila.reliquary.shift_for_more") + ChatFormatting.RESET));
            return;
		}

		IWailaDataChangeIndicator changeIndicator = null;

		if(accessor.getBlockEntity() instanceof IWailaDataChangeIndicator) {
			changeIndicator = (IWailaDataChangeIndicator) accessor.getBlockEntity();
		}

		if(changeIndicator == null || cachedBody == null || cachedPosition == null || !cachedPosition.equals(accessor.getPosition()) || changeIndicator.getDataChanged()) {
			cachedBody = getWailaBodyToCache(accessor, pluginConfig);
			cachedPosition = accessor.getPosition();
		}

		cachedBody = updateCache(accessor, cachedBody);

		for (List<IElement> line : cachedBody) {
			tooltip.add(line);
		}
	}

	abstract public List<List<IElement>> getWailaBodyToCache(BlockAccessor accessor, IPluginConfig config);

	public List<List<IElement>> updateCache(BlockAccessor accessor, List<List<IElement>> cached)
	{
		return cached;
	}
}

