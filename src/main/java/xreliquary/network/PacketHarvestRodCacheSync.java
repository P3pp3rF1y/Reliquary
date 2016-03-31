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
import xreliquary.init.ModCapabilities;
import xreliquary.items.util.IHarvestRodCache;

public class PacketHarvestRodCacheSync implements IMessage, IMessageHandler<PacketHarvestRodCacheSync, IMessage> {

	private int timesUsed;
	private EnumHand hand;

	public PacketHarvestRodCacheSync() {
	}

	public PacketHarvestRodCacheSync(int timesUsed, EnumHand hand) {
		this.timesUsed = timesUsed;
		this.hand = hand;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		timesUsed = buf.readInt();
		hand = buf.readBoolean() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(timesUsed);
		buf.writeBoolean(hand == EnumHand.MAIN_HAND);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(PacketHarvestRodCacheSync message, MessageContext ctx) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		IHarvestRodCache cache = player.getHeldItem(message.hand).getCapability(ModCapabilities.HARVEST_ROD_CACHE, null);

		if (cache != null) {
			cache.setTimesUsed(message.timesUsed);
		}

		return null;
	}
}
