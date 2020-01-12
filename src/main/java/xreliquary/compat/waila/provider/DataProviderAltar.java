/* TODO readd waila integration
package xreliquary.compat.waila.provider;

import com.mojang.realmsclient.gui.ChatFormatting;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import xreliquary.blocks.AlkahestryAltarBlock;
import xreliquary.blocks.tile.AlkahestryAltarTileEntity;
import xreliquary.init.ModBlocks;
import xreliquary.reference.Settings;
import xreliquary.util.LanguageHelper;

import java.text.SimpleDateFormat;
import java.util.List;

public class DataProviderAltar implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return new ItemStack(ModBlocks.ALKAHESTRY_ALTAR);
	}


	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		if(Settings.wailaShiftForInfo && !accessor.getPlayer().isSneaking()) {
			currenttip.add(ChatFormatting.ITALIC + LanguageHelper.getLocalization("waila.xreliquary.shift_for_more") + ChatFormatting.RESET);
			return currenttip;
		}

		if(!(accessor.getBlock() instanceof AlkahestryAltarBlock && accessor.getTileEntity() instanceof AlkahestryAltarTileEntity))
			return currenttip;

		AlkahestryAltarTileEntity altar = (AlkahestryAltarTileEntity) accessor.getTileEntity();

		if(!altar.isActive()) {
			currenttip.add(ChatFormatting.RED + LanguageHelper.getLocalization("waila.xreliquary.altar.inactive") + ChatFormatting.RESET);
			currenttip.add(altar.getRedstoneCount() + "x" + (new ItemStack(Items.REDSTONE).getDisplayName()));
			return currenttip;
		}

		currenttip.add(ChatFormatting.GREEN + LanguageHelper.getLocalization("waila.xreliquary.altar.active"));
		currenttip.add(LanguageHelper.getLocalization("waila.xreliquary.altar.time_remaining", new SimpleDateFormat("mm:ss").format(altar.getCycleTime() * 50)));

		return currenttip;
	}
}
*/
