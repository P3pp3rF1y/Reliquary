package xreliquary.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import xreliquary.items.util.HarvestRodPlayerProps;

public class PacketHarvestRodExtPropsSync implements IMessage, IMessageHandler<PacketHarvestRodExtPropsSync, IMessage> {

	private int timesUsed;

	public PacketHarvestRodExtPropsSync(){}

	public PacketHarvestRodExtPropsSync(int timesUsed) {
		this.timesUsed = timesUsed;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		timesUsed = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(timesUsed);
	}

	@Override
	public IMessage onMessage(PacketHarvestRodExtPropsSync message, MessageContext ctx) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		HarvestRodPlayerProps props = HarvestRodPlayerProps.get(player);
		if (props == null) {
			HarvestRodPlayerProps.register(player);
			props = HarvestRodPlayerProps.get(player);
		}

		props.setTimesUsed(message.timesUsed);

		return null;
	}
}
