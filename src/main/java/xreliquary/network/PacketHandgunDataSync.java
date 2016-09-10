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
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.init.ModCapabilities;
import xreliquary.items.util.handgun.IHandgunData;
import xreliquary.util.potions.XRPotionHelper;

import java.io.IOException;
import java.util.List;

public class PacketHandgunDataSync implements IMessage, IMessageHandler<PacketHandgunDataSync, IMessage> {
	private int playerSlotNumber;
	private EnumHand hand = EnumHand.MAIN_HAND;
	private short bulletCount;
	private short bulletType;
	private boolean isInCooldown;
	private long cooldownTime;
	private List<PotionEffect> potionEffects;

	private static final int INVALID_SLOT = -1;

	public PacketHandgunDataSync() {
	}

	public PacketHandgunDataSync(int playerSlotNumber, short bulletCount, short bulletType, boolean isInCooldown, long cooldownTime, List<PotionEffect> potionEffects) {
		this.playerSlotNumber = playerSlotNumber;
		this.bulletCount = bulletCount;
		this.bulletType = bulletType;
		this.isInCooldown = isInCooldown;
		this.cooldownTime = cooldownTime;
		this.potionEffects = potionEffects;
	}

	public PacketHandgunDataSync(EnumHand hand, short bulletCount, short bulletType, boolean isInCooldown, long cooldownTime, List<PotionEffect> potionEffects) {
		this.hand = hand;
		this.isInCooldown = isInCooldown;
		this.cooldownTime = cooldownTime;
		this.playerSlotNumber = INVALID_SLOT;
		this.bulletCount = bulletCount;
		this.bulletType = bulletType;
		this.potionEffects = potionEffects;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		playerSlotNumber = buf.readInt();
		hand = buf.readBoolean() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
		bulletCount = buf.readShort();
		bulletType = buf.readShort();
		isInCooldown = buf.readBoolean();
		cooldownTime = buf.readLong();
		if (buf.readBoolean()) {
			try {
				potionEffects = PotionUtils.getFullEffectsFromTag(CompressedStreamTools.read(new ByteBufInputStream(buf), new NBTSizeTracker(2097152L)));
			}
			catch(IOException e) {
				throw new EncoderException(e);
			}
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(playerSlotNumber);
		buf.writeBoolean(hand == EnumHand.MAIN_HAND);
		buf.writeShort(bulletCount);
		buf.writeShort(bulletType);
		buf.writeBoolean(isInCooldown);
		buf.writeLong(cooldownTime);
		buf.writeBoolean(potionEffects.size()>0);
		if (potionEffects.size() > 0) {
			NBTTagCompound potionTag = new NBTTagCompound();
			XRPotionHelper.appendEffectsToNBT(potionTag, potionEffects);
			try {
				CompressedStreamTools.write(potionTag, new ByteBufOutputStream(buf));
			}
			catch(IOException e) {
				throw new EncoderException(e);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(PacketHandgunDataSync message, MessageContext ctx) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;

		ItemStack stack;
		if(message.playerSlotNumber > INVALID_SLOT) {
			stack = player.inventory.getStackInSlot(message.playerSlotNumber);
		} else {
			stack = player.getHeldItem(message.hand);
		}

		if(stack != null) {
			IHandgunData data = stack.getCapability(ModCapabilities.HANDGUN_DATA_CAPABILITY, null);

			if(data != null) {
				data.setBulletCount(message.bulletCount);
				data.setBulletType(message.bulletType);
				data.setInCoolDown(message.isInCooldown);
				data.setCoolDownTime(message.cooldownTime);
				data.setPotionEffects(message.potionEffects);
			}
		}

		return null;
	}
}
