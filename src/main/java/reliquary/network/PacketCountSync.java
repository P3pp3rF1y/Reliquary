package reliquary.network;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import reliquary.util.NBTHelper;

import java.util.function.Supplier;

public class PacketCountSync {
	private final InteractionHand hand;
	private final short slot;
	private final ItemStack stack;
	private final int count;

	public PacketCountSync(InteractionHand hand, short slot, ItemStack stack, int count) {
		this.hand = hand;
		this.slot = slot;
		this.stack = stack;
		this.count = count;
	}

	static void encode(PacketCountSync msg, FriendlyByteBuf packetBuffer) {
		packetBuffer.writeBoolean(msg.hand == InteractionHand.MAIN_HAND);
		packetBuffer.writeShort(msg.slot);
		packetBuffer.writeNbt(msg.stack.save(new CompoundTag()));
		packetBuffer.writeInt(msg.count);
	}

	static PacketCountSync decode(FriendlyByteBuf packetBuffer) {
		boolean mainHand = packetBuffer.readBoolean();
		short slot = packetBuffer.readShort();
		CompoundTag stackNbt = packetBuffer.readNbt();
		return new PacketCountSync(mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, slot, stackNbt == null ? ItemStack.EMPTY : ItemStack.of(stackNbt), packetBuffer.readInt());
	}

	static void onMessage(PacketCountSync msg, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> handleMessage(msg));
		context.setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	private static void handleMessage(PacketCountSync message) {
		Player player = Minecraft.getInstance().player;
		if (player == null) {
			return;
		}

		ItemStack container = player.getItemInHand(message.hand);
		NBTHelper.updateContainedStack(container, message.slot, message.stack, message.count, message.stack.isEmpty());
	}
}
