package xreliquary.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import xreliquary.util.NBTHelper;

import java.util.function.Supplier;

public class PacketCountSync {
	private Hand hand = Hand.MAIN_HAND;
	private short slot;
	private ItemStack stack;
	private int count;

	public PacketCountSync(Hand hand, short slot, ItemStack stack, int count) {
		this.hand = hand;
		this.slot = slot;
		this.stack = stack;
		this.count = count;
	}

	static void encode(PacketCountSync msg, PacketBuffer packetBuffer) {
		packetBuffer.writeBoolean(msg.hand == Hand.MAIN_HAND);
		packetBuffer.writeShort(msg.slot);
		packetBuffer.writeCompoundTag(msg.stack.write(new CompoundNBT()));
		packetBuffer.writeInt(msg.count);
	}

	static PacketCountSync decode(PacketBuffer packetBuffer) {
		return new PacketCountSync(packetBuffer.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND, packetBuffer.readShort(), ItemStack.read(packetBuffer.readCompoundTag()), packetBuffer.readInt());
	}

	static void onMessage(PacketCountSync msg, Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> handleMessage(msg));
	}

	@OnlyIn(Dist.CLIENT)
	private static void handleMessage(PacketCountSync message) {
		PlayerEntity player = Minecraft.getInstance().player;

		ItemStack container = player.getHeldItem(message.hand);
		NBTHelper.updateContainedStack(container, message.slot, message.stack, message.count, message.stack.isEmpty());
	}
}
