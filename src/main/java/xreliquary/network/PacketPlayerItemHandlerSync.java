package xreliquary.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.EncoderException;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import xreliquary.items.util.FilteredItemStackHandler;

import java.io.IOException;

public class PacketPlayerItemHandlerSync implements IMessage, IMessageHandler<PacketPlayerItemHandlerSync, IMessage> {
	private EnumFacing facing;
	private NBTTagCompound itemHandlerNBT;

	private static final int INVALID_SLOT = -1;

	public PacketPlayerItemHandlerSync() {
	}

	public PacketPlayerItemHandlerSync(EnumFacing facing, NBTTagCompound itemHandlerNBT) {
		this.facing = facing;
		this.itemHandlerNBT = itemHandlerNBT;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		facing = EnumFacing.getFront(buf.readShort());
		try {
			itemHandlerNBT = CompressedStreamTools.read(new ByteBufInputStream(buf), new NBTSizeTracker(2097152L));
		}
		catch(IOException e) {
			throw new EncoderException(e);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeShort(facing.getIndex());
		try {
			CompressedStreamTools.write(itemHandlerNBT, new ByteBufOutputStream(buf));
		}
		catch(IOException e) {
			throw new EncoderException(e);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(PacketPlayerItemHandlerSync message, MessageContext ctx) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;

		IItemHandler itemHandler = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, message.facing);
		if(itemHandler != null && itemHandler instanceof FilteredItemStackHandler) {

			FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;

			if (filteredHandler != null) {
				filteredHandler.deserializeNBT(message.itemHandlerNBT);
			}
		}

		return null;
	}
}
