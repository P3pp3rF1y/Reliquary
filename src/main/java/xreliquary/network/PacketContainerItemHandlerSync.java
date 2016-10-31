package xreliquary.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.EncoderException;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import xreliquary.items.util.FilteredItemStackHandler;

import java.io.IOException;

public class PacketContainerItemHandlerSync implements IMessage, IMessageHandler<PacketContainerItemHandlerSync, IMessage> {
	private int slot;
	private NBTTagCompound itemHandlerNBT;
	private int windowId;

	private static final int INVALID_SLOT = -1;

	public PacketContainerItemHandlerSync() {
	}

	public PacketContainerItemHandlerSync(int slot, NBTTagCompound itemHandlerNBT, int windowId) {
		this.slot = slot;
		this.itemHandlerNBT = itemHandlerNBT;
		this.windowId = windowId;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		slot = buf.readInt();
		windowId = buf.readInt();
		try {
			itemHandlerNBT = CompressedStreamTools.read(new ByteBufInputStream(buf), new NBTSizeTracker(2097152L));
		}
		catch(IOException e) {
			throw new EncoderException(e);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(slot);
		buf.writeInt(windowId);
		try {
			CompressedStreamTools.write(itemHandlerNBT, new ByteBufOutputStream(buf));
		}
		catch(IOException e) {
			throw new EncoderException(e);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(PacketContainerItemHandlerSync message, MessageContext ctx) {

		Minecraft.getMinecraft().addScheduledTask(() -> handleMessage(message, ctx));

		return null;
	}

	@SideOnly(Side.CLIENT)
	public void handleMessage(PacketContainerItemHandlerSync message, MessageContext ctx) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;

		if (player.openContainer == null || player.openContainer.windowId != message.windowId)
			return;

		Slot s = player.openContainer.getSlot(message.slot);
		if (s == null)
			return;

		ItemStack stack = s.getStack();

		if(stack != null) {
			IItemHandler itemHandler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			if(itemHandler != null && itemHandler instanceof FilteredItemStackHandler) {

				FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;

				if (filteredHandler != null) {
					filteredHandler.deserializeNBT(message.itemHandlerNBT);
				}
			}
		}
	}
}
