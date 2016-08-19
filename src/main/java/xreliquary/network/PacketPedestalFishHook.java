package xreliquary.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import xreliquary.api.IPedestal;
import xreliquary.client.render.RenderPedestalFishHook;

public class PacketPedestalFishHook implements IMessage, IMessageHandler<PacketPedestalFishHook, IMessage> {

	private BlockPos pedestalPos;
	private int itemIndex;
	private double hookX;
	private double hookY;
	private double hookZ;
	private float yawOffset;

	public PacketPedestalFishHook() {
	}

	public PacketPedestalFishHook(BlockPos pedestalPos, int itemIndex, double hookX, double hookY, double hookZ) {

		this.pedestalPos = pedestalPos;
		this.itemIndex = itemIndex;
		this.hookX = hookX;
		this.hookY = hookY;
		this.hookZ = hookZ;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pedestalPos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
		itemIndex = buf.readInt();
		hookX = buf.readDouble();
		hookY = buf.readDouble();
		hookZ = buf.readDouble();

	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(pedestalPos.getX());
		buf.writeInt(pedestalPos.getY());
		buf.writeInt(pedestalPos.getZ());
		buf.writeInt(itemIndex);
		buf.writeDouble(hookX);
		buf.writeDouble(hookY);
		buf.writeDouble(hookZ);
		buf.writeFloat(yawOffset);
	}

	@Override
	public IMessage onMessage(PacketPedestalFishHook message, MessageContext ctx) {
		WorldClient world = Minecraft.getMinecraft().theWorld;
		TileEntity te = world.getTileEntity(message.pedestalPos);

		if(te != null && te instanceof IPedestal) {
			IPedestal pedestal = (IPedestal) te;

			RenderPedestalFishHook.HookRenderingData data = null;
			if (message.hookY > 0)
				data = new RenderPedestalFishHook.HookRenderingData(message.hookX, message.hookY, message.hookZ);

			pedestal.setItemData(message.itemIndex, data);
		}

		return null;
	}
}
