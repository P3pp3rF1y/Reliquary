package xreliquary.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.handler.ClientEventHandler;

public class PacketMobCharmDamage implements IMessage, IMessageHandler<PacketMobCharmDamage, IMessage> {
	private byte type;
	private int damage;
	private int slot;

	@SuppressWarnings("unused")
	public PacketMobCharmDamage() {}

	public PacketMobCharmDamage(byte type, int damage, int slot){
		this.type = type;
		this.damage = damage;
		this.slot = slot;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		type = buf.readByte();
		damage = buf.readInt();
		slot = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeByte(type);
		buf.writeInt(damage);
		buf.writeInt(slot);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(PacketMobCharmDamage message, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(() -> handleMessage(message));

		return null;
	}

	@SideOnly(Side.CLIENT)
	private void handleMessage(PacketMobCharmDamage message) {
		ClientEventHandler.addCharmToDraw(message.type, message.damage, message.slot);
	}
}
