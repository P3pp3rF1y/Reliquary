package xreliquary.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import xreliquary.items.util.FilteredItemStackHandler;

public class PacketItemHandlerSync implements IMessage, IMessageHandler<PacketItemHandlerSync, IMessage> {
	private int count;
	private int slotNumber;
	private EnumHand hand;

	private static final int INVALID_SLOT = -1;

	public PacketItemHandlerSync(){}

	public PacketItemHandlerSync(int count, EnumHand hand) {
		this(count, INVALID_SLOT, hand);
	}
	public PacketItemHandlerSync(int count, int slotNumber) {
		this(count, slotNumber, EnumHand.MAIN_HAND);
	}

	private PacketItemHandlerSync(int count, int slotNumber, EnumHand hand) {
		this.count = count;
		this.slotNumber = slotNumber;
		this.hand = hand;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		count = buf.readInt();
		slotNumber = buf.readInt();
		hand = buf.readBoolean() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(count);
		buf.writeInt(slotNumber);
		buf.writeBoolean(hand == EnumHand.MAIN_HAND);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(PacketItemHandlerSync message, MessageContext ctx) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;

		ItemStack stack;
		if (message.slotNumber > INVALID_SLOT) {
			stack = player.inventory.getStackInSlot(message.slotNumber);
		} else {
			stack = player.getHeldItem(message.hand);
		}

		if (stack != null) {
			IItemHandler itemHandler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			if (itemHandler != null && itemHandler instanceof FilteredItemStackHandler) {

				FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;

				filteredHandler.setTotalAmount(0, message.count);
			}
		}

		return null;
	}
}
