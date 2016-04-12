package xreliquary.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import xreliquary.init.ModCapabilities;
import xreliquary.items.util.FilteredItemStackHandler;
import xreliquary.items.util.IHarvestRodCache;

public class PacketEnderStaffItemSync implements IMessage, IMessageHandler<PacketEnderStaffItemSync, IMessage> {
	private int count;
	private EnumHand hand;

	public PacketEnderStaffItemSync(){}

	public PacketEnderStaffItemSync(int count, EnumHand hand) {
		this.count = count;
		this.hand = hand;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		count = buf.readInt();
		hand = buf.readBoolean() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(count);
		buf.writeBoolean(hand == EnumHand.MAIN_HAND);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(PacketEnderStaffItemSync message, MessageContext ctx) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		IItemHandler itemHandler = player.getHeldItem(message.hand).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		if (itemHandler != null && itemHandler instanceof FilteredItemStackHandler) {

			FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;

			filteredHandler.setTotalAmount(0, message.count);
		}

		return null;
	}
}
