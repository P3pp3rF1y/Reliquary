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
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.util.NBTHelper;

import java.io.IOException;

public class PacketCountSync implements IMessage, IMessageHandler<PacketCountSync, IMessage> {
	private EnumHand hand = EnumHand.MAIN_HAND;
	private short slot;
	private ItemStack stack;
	private int count;

	@SuppressWarnings("unused")
	public PacketCountSync() {
	}

	public PacketCountSync(EnumHand hand, short slot, int count) {
		this(hand, slot, ItemStack.EMPTY, count);
	}
	public PacketCountSync(EnumHand hand, short slot, ItemStack stack, int count) {
		this.hand = hand;
		this.slot = slot;
		this.stack = stack;
		this.count = count;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		hand = buf.readBoolean() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
		slot = buf.readShort();
		try {
			stack = new ItemStack(CompressedStreamTools.read(new ByteBufInputStream(buf), new NBTSizeTracker(2097152L)));
		}
		catch(IOException e) {
			throw new EncoderException(e);
		}
		count = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(hand == EnumHand.MAIN_HAND);
		buf.writeShort(slot);
		try {
			CompressedStreamTools.write(stack.writeToNBT(new NBTTagCompound()), new ByteBufOutputStream(buf));
		}
		catch(IOException e) {
			throw new EncoderException(e);
		}
		buf.writeInt(count);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(PacketCountSync message, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(() -> handleMessage(message));

		return null;
	}

	@SideOnly(Side.CLIENT)
	private void handleMessage(PacketCountSync message) {
		EntityPlayer player = Minecraft.getMinecraft().player;

		ItemStack container = player.getHeldItem(message.hand);

		NBTHelper.updateContainedStack(container, message.slot, message.stack, message.count, message.stack.isEmpty());
	}
}
