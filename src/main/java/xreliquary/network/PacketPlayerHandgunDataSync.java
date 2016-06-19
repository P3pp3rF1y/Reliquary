package xreliquary.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.EncoderException;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
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
import xreliquary.items.util.handgun.IHandgunData;

import java.io.IOException;

public class PacketPlayerHandgunDataSync implements IMessage, IMessageHandler<PacketPlayerHandgunDataSync, IMessage> {
	private EnumHand hand = EnumHand.MAIN_HAND;
	private short bulletCount;
	private short bulletType;
	private long coolDownTime;
	private boolean inCoolDown;

	public PacketPlayerHandgunDataSync() {
	}

	public PacketPlayerHandgunDataSync(EnumHand hand, short bulletCount, short bulletType, long coolDownTime, boolean inCoolDown) {
		this.hand = hand;
		this.bulletCount = bulletCount;
		this.bulletType = bulletType;
		this.coolDownTime = coolDownTime;
		this.inCoolDown = inCoolDown;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		hand = buf.readBoolean() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
		bulletCount = buf.readShort();
		bulletType = buf.readShort();
		coolDownTime = buf.readLong();
		inCoolDown = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(hand == EnumHand.MAIN_HAND);
		buf.writeShort(bulletCount);
		buf.writeShort(bulletType);
		buf.writeLong(coolDownTime);
		buf.writeBoolean(inCoolDown);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(PacketPlayerHandgunDataSync message, MessageContext ctx) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;

		EnumFacing facing = message.hand == EnumHand.MAIN_HAND ? EnumFacing.EAST : EnumFacing.WEST;

		IHandgunData handgunData = player.getCapability(ModCapabilities.HANDGUN_DATA_CAPABILITY, facing);
		if(handgunData != null) {

			handgunData.setBulletCount(message.bulletCount);
			handgunData.setBulletType(message.bulletType);
			handgunData.setCoolDownTime(message.coolDownTime);
			handgunData.setInCoolDown(message.inCoolDown);
		}

		return null;
	}
}
