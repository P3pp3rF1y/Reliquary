package xreliquary.compat.waila.provider;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;

import java.util.List;

public abstract class CachedBodyDataProvider implements IWailaDataProvider {

    List<String> cachedBody = null;
    BlockPos cachedPosition = null;

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {

        IWailaDataChangeIndicator changeIndicator = (IWailaDataChangeIndicator) accessor.getTileEntity();
        if (cachedBody == null || cachedPosition == null || !cachedPosition.equals(accessor.getPosition()) || changeIndicator.getDataChanged()) {
            cachedBody = getWailaBodyToCache(itemStack, currenttip, accessor, config);
            cachedPosition = accessor.getPosition();
        }

        return cachedBody;
    }

    abstract List<String> getWailaBodyToCache(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config);
}
