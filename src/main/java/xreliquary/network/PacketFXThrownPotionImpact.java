package xreliquary.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Random;

public class PacketFXThrownPotionImpact implements IMessage, IMessageHandler<PacketFXThrownPotionImpact, IMessage> {
	private int color;
	private double posX;
	private double posY;
	private double posZ;

	public PacketFXThrownPotionImpact() {
	}

	public PacketFXThrownPotionImpact(int color, double x, double y, double z) {
		this.color = color;
		this.posX = x;
		this.posY = y;
		this.posZ = z;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.color = buf.readInt();
		this.posX = buf.readDouble();
		this.posY = buf.readDouble();
		this.posZ = buf.readDouble();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.color);
		buf.writeDouble(this.posX);
		buf.writeDouble(this.posY);
		buf.writeDouble(this.posZ);
	}

	@Override
	public IMessage onMessage(PacketFXThrownPotionImpact message, MessageContext ctx) {
		Minecraft mc = FMLClientHandler.instance().getClient();
		int color = message.color;
		Random rand = new Random();

		float red = (((color >> 16) & 255) / 256F);
		float green = (((color >> 8) & 255) / 256F);
		float blue = (((color >> 0) & 255) / 256F);

		for(int var20 = 0; var20 < 100; ++var20) {
			double var39 = rand.nextDouble() * 4.0D;
			double var23 = rand.nextDouble() * Math.PI * 2.0D;
			double var25 = Math.cos(var23) * var39;
			double var27 = 0.01D + rand.nextDouble() * 0.5D;
			double var29 = Math.sin(var23) * var39;
			EntityFX var31 = mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.SPELL.getParticleID(), message.posX + var25 * 0.1D, message.posY + 0.3D, message.posZ + var29 * 0.1D, var25, var27, var29, new int[0]);
			if(var31 != null) {
				float var32 = 0.75F + rand.nextFloat() * 0.25F;
				var31.setRBGColorF(red * var32, green * var32, blue * var32);
				var31.multiplyVelocity((float) var39);
			}
		}

		return null;
	}
}
