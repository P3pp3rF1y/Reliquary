package xreliquary.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.entities.ConcussiveExplosion;

public class PacketFXConcussiveExplosion implements IMessage, IMessageHandler<PacketFXConcussiveExplosion, IMessage> {
	private float size;
	private double posX;
	private double posY;
	private double posZ;

	public PacketFXConcussiveExplosion() {
	}

	public PacketFXConcussiveExplosion(float size, double x, double y, double z) {
		this.size = size;
		this.posX = x;
		this.posY = y;
		this.posZ = z;

	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.size = buf.readFloat();
		this.posX = buf.readDouble();
		this.posY = buf.readDouble();
		this.posZ = buf.readDouble();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeFloat(this.size);
		buf.writeDouble(this.posX);
		buf.writeDouble(this.posY);
		buf.writeDouble(this.posZ);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(PacketFXConcussiveExplosion message, MessageContext ctx) {
		ConcussiveExplosion explosion = new ConcussiveExplosion(Minecraft.getMinecraft().theWorld, null, null, message.posX, message.posY, message.posZ, message.size, false, true);

		explosion.doExplosionB(false);

		return null;
	}
}
