package xreliquary.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
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

	public PacketItemHandlerSync(){}

	public PacketItemHandlerSync(int count, int slotNumber) {
		this.count = count;
		this.slotNumber = slotNumber;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		count = buf.readInt();
		slotNumber = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(count);
		buf.writeInt(slotNumber);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(PacketItemHandlerSync message, MessageContext ctx) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		IItemHandler itemHandler = player.inventory.getStackInSlot(slotNumber).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		if (itemHandler != null && itemHandler instanceof FilteredItemStackHandler) {

			FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;

			filteredHandler.setTotalAmount(0, message.count);
		}

		return null;
	}
}
