package xreliquary.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import xreliquary.client.gui.hud.CharmPane;

import java.util.function.Supplier;

public class PacketMobCharmDamage {
	private final ItemStack mobCharm;
	private final int slot;

	public PacketMobCharmDamage(ItemStack mobCharm, int slot) {
		this.mobCharm = mobCharm;
		this.slot = slot;
	}

	static void encode(PacketMobCharmDamage msg, FriendlyByteBuf packetBuffer) {
		packetBuffer.writeNbt(msg.mobCharm.save(new CompoundTag()));
		packetBuffer.writeByte(msg.slot);
	}

	static PacketMobCharmDamage decode(FriendlyByteBuf packetBuffer) {
		CompoundTag stackNbt = packetBuffer.readNbt();
		return new PacketMobCharmDamage(stackNbt == null ? ItemStack.EMPTY : ItemStack.of(stackNbt), packetBuffer.readByte());
	}

	static void onMessage(PacketMobCharmDamage msg, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> handleMessage(msg));
		context.setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	private static void handleMessage(PacketMobCharmDamage message) {
		CharmPane.addCharmToDraw(message.mobCharm, message.slot);
	}
}
