package xreliquary.compat.waila.provider;

import com.mojang.realmsclient.gui.ChatFormatting;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xreliquary.blocks.BlockAlkahestryAltar;
import xreliquary.blocks.tile.TileEntityAltar;
import xreliquary.init.ModBlocks;
import xreliquary.reference.Settings;
import xreliquary.util.LanguageHelper;

import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;
import java.util.List;

public class DataProviderAltar implements IWailaDataProvider {
	@Nonnull
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return new ItemStack(ModBlocks.alkahestryAltar);
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		if(Settings.wailaShiftForInfo && !accessor.getPlayer().isSneaking()) {
			currenttip.add(ChatFormatting.ITALIC + LanguageHelper.getLocalization("waila.xreliquary.shift_for_more") + ChatFormatting.RESET);
			return currenttip;
		}

		if(!(accessor.getBlock() instanceof BlockAlkahestryAltar && accessor.getTileEntity() instanceof TileEntityAltar))
			return currenttip;

		TileEntityAltar altar = (TileEntityAltar) accessor.getTileEntity();

		if(!altar.isActive()) {
			currenttip.add(ChatFormatting.RED + LanguageHelper.getLocalization("waila.xreliquary.altar.inactive") + ChatFormatting.RESET);
			currenttip.add(altar.getRedstoneCount() + "x" + (new ItemStack(Items.REDSTONE).getDisplayName()));
			return currenttip;
		}

		currenttip.add(ChatFormatting.GREEN + LanguageHelper.getLocalization("waila.xreliquary.altar.active") + ChatFormatting.RESET);
		currenttip.add(String.format(LanguageHelper.getLocalization("waila.xreliquary.altar.time_remaining"), new SimpleDateFormat("mm:ss").format(altar.getCycleTime() * 50)));

		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
		return null;
	}

}
