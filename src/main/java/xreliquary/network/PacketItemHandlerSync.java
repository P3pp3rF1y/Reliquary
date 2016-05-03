package xreliquary.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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

public class PacketItemHandlerSync implements IMessage, IMessageHandler<PacketItemHandlerSync, IMessage> {
	private int count;
	private int playerSlotNumber;
	private EnumHand hand;
	private int handlerParentSlotNumber;
	private ItemStack itemStack;

	private static final int INVALID_SLOT = -1;

	public PacketItemHandlerSync(){}

	private PacketItemHandlerSync(int count, int playerSlotNumber, EnumHand hand, int handlerParentSlotNumber, ItemStack itemStack) {
		this.count = count;
		this.playerSlotNumber = playerSlotNumber;
		this.hand = hand;
		this.handlerParentSlotNumber = handlerParentSlotNumber;
		this.itemStack = itemStack;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		count = buf.readInt();
		playerSlotNumber = buf.readInt();
		hand = buf.readBoolean() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
		handlerParentSlotNumber = buf.readInt();
		itemStack = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(count);
		buf.writeInt(playerSlotNumber);
		buf.writeBoolean(hand == EnumHand.MAIN_HAND);
		buf.writeInt(handlerParentSlotNumber);
		ByteBufUtils.writeItemStack(buf, itemStack);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(PacketItemHandlerSync message, MessageContext ctx) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;

		ItemStack stack;
		if (message.playerSlotNumber > INVALID_SLOT) {
			stack = player.inventory.getStackInSlot(message.playerSlotNumber);
		} else {
			stack = player.getHeldItem(message.hand);
		}

		if (stack != null) {
			IItemHandler itemHandler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			if (itemHandler != null && itemHandler instanceof FilteredItemStackHandler) {

				FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;

				if (message.itemStack != null) {
					filteredHandler.setParentSlotStack(message.handlerParentSlotNumber, message.itemStack);
				}

				filteredHandler.setTotalAmount(message.handlerParentSlotNumber, message.count, false);
			}
		}

		return null;
	}

	public static class Builder {
		private int count;
		private int playerSlotNumber = INVALID_SLOT;
		private EnumHand hand = EnumHand.MAIN_HAND;
		private int handlerParentSlotNumber = 0;
		private ItemStack itemStack = null;

		public Builder(int count) {
			this.count = count;
		}

		public Builder playerSlot(int playerSlotNumber) {
			this.playerSlotNumber = playerSlotNumber;
			return this;
		}

		public Builder hand(EnumHand hand) {
			this.hand = hand;
			return this;
		}

		public Builder itemStackInfo(int handlerParentSlotNumber, ItemStack itemStack) {
			this.handlerParentSlotNumber = handlerParentSlotNumber;
			this.itemStack = itemStack;
			return this;
		}

		public PacketItemHandlerSync build() {
			return new PacketItemHandlerSync(this.count, this.playerSlotNumber, this.hand, this.handlerParentSlotNumber, this.itemStack);
		}
	}
}
