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
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import xreliquary.items.util.FilteredItemStackHandler;

import java.io.IOException;

public class PacketItemHandlerSync implements IMessage, IMessageHandler<PacketItemHandlerSync, IMessage> {
	private int playerSlotNumber;
	private EnumHand hand = EnumHand.MAIN_HAND;
	private NBTTagCompound itemHandlerNBT;

	private static final int INVALID_SLOT = -1;

	public PacketItemHandlerSync() {
	}

	public PacketItemHandlerSync(EnumHand hand, NBTTagCompound itemHandlerNBT) {
		this.hand = hand;
		this.itemHandlerNBT = itemHandlerNBT;
	}
	public PacketItemHandlerSync(int playerSlotNumber, NBTTagCompound itemHandlerNBT) {
		this.playerSlotNumber = playerSlotNumber;
		this.itemHandlerNBT = itemHandlerNBT;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		playerSlotNumber = buf.readInt();
		hand = buf.readBoolean() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
		try {
			itemHandlerNBT = CompressedStreamTools.read(new ByteBufInputStream(buf), new NBTSizeTracker(2097152L));
		}
		catch(IOException e) {
			throw new EncoderException(e);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(playerSlotNumber);
		buf.writeBoolean(hand == EnumHand.MAIN_HAND);
		try {
			CompressedStreamTools.write(itemHandlerNBT, new ByteBufOutputStream(buf));
		}
		catch(IOException e) {
			throw new EncoderException(e);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(PacketItemHandlerSync message, MessageContext ctx) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;

		ItemStack stack;
		if(message.playerSlotNumber > INVALID_SLOT) {
			stack = player.inventory.getStackInSlot(message.playerSlotNumber);
		} else {
			stack = player.getHeldItem(message.hand);
		}

		if(stack != null) {
			IItemHandler itemHandler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			if(itemHandler != null && itemHandler instanceof FilteredItemStackHandler) {

				FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;

				if (filteredHandler != null) {
					filteredHandler.deserializeNBT(message.itemHandlerNBT);
				}
			}
		}

		return null;
	}
}
