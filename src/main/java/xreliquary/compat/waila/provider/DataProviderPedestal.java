package xreliquary.compat.waila.provider;

import com.mojang.realmsclient.gui.ChatFormatting;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xreliquary.blocks.BlockPedestal;
import xreliquary.blocks.tile.TileEntityPedestal;
import xreliquary.init.ModBlocks;

import javax.annotation.Nonnull;
import java.text.MessageFormat;
import java.util.List;

public class DataProviderPedestal implements IWailaDataProvider {
	@Nonnull
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return new ItemStack(ModBlocks.pedestal);
	}

	@Nonnull
	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		if(!(accessor.getBlock() instanceof BlockPedestal && accessor.getTileEntity() instanceof TileEntityPedestal))
			return currenttip;

		TileEntityPedestal pedestal = (TileEntityPedestal) accessor.getTileEntity();
		IBlockState pedestalState = accessor.getBlockState();

		if(pedestalState.getValue(BlockPedestal.ENABLED)) {
			//TODO translate in lang file if there will be a need for that in the future
			currenttip.add(ChatFormatting.GREEN + "ON");
			if(pedestal.isSwitchedOn()) {
				currenttip.add("Switch");
			}
			if(pedestal.isPowered()) {
				currenttip.add("Redstone");
			}
			if(pedestal.getOnSwitches().size() > 0) {
				for(long loc : pedestal.getOnSwitches()) {
					BlockPos pos = BlockPos.fromLong(loc);
					currenttip.add(MessageFormat.format("Remote at: {0}, {1}, {2}", pos.getX(), pos.getY(), pos.getZ()));
				}
			}
		} else {
			currenttip.add(ChatFormatting.RED + "OFF");
		}

		return currenttip;
	}
}
