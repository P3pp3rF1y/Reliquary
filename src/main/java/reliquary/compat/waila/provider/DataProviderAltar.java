/* TODO readd waila integration
package reliquary.compat.waila.provider;

import com.mojang.realmsclient.gui.ChatFormatting;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import reliquary.blocks.AlkahestryAltarBlock;
import reliquary.blocks.tile.AlkahestryAltarTileEntity;
import reliquary.init.ModBlocks;
import reliquary.reference.Settings;
import reliquary.util.LanguageHelper;

import java.text.SimpleDateFormat;
import java.util.List;

public class DataProviderAltar implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return new ItemStack(ModBlocks.ALKAHESTRY_ALTAR);
	}


	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		if(Settings.wailaShiftForInfo && !accessor.getPlayer().isCrouching()) {
			currenttip.add(ChatFormatting.ITALIC + LanguageHelper.getLocalization("waila.reliquary.shift_for_more") + ChatFormatting.RESET);
			return currenttip;
		}

		if(!(accessor.getBlock() instanceof AlkahestryAltarBlock && accessor.getTileEntity() instanceof AlkahestryAltarTileEntity))
			return currenttip;

		AlkahestryAltarTileEntity altar = (AlkahestryAltarTileEntity) accessor.getTileEntity();

		if(!altar.isActive()) {
			currenttip.add(ChatFormatting.RED + LanguageHelper.getLocalization("waila.reliquary.altar.inactive") + ChatFormatting.RESET);
			currenttip.add(altar.getRedstoneCount() + "x" + (new ItemStack(Items.REDSTONE).getDisplayName()));
			return currenttip;
		}

		currenttip.add(ChatFormatting.GREEN + LanguageHelper.getLocalization("waila.reliquary.altar.active"));
		currenttip.add(LanguageHelper.getLocalization("waila.reliquary.altar.time_remaining", new SimpleDateFormat("mm:ss").format(altar.getCycleTime() * 50)));

		return currenttip;
	}
}
*/
