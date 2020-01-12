package xreliquary.network;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import xreliquary.client.gui.hud.CharmPane;

import java.util.function.Supplier;

public class PacketMobCharmDamage {
	private ItemStack mobCharm;
	private int slot;

	public PacketMobCharmDamage() {}

	public PacketMobCharmDamage(ItemStack mobCharm, int slot) {
		this.mobCharm = mobCharm;
		this.slot = slot;
	}

	static void encode(PacketMobCharmDamage msg, PacketBuffer packetBuffer) {
		packetBuffer.writeCompoundTag(msg.mobCharm.write(new CompoundNBT()));
		packetBuffer.writeByte(msg.slot);
	}

	static PacketMobCharmDamage decode(PacketBuffer packetBuffer) {
		return new PacketMobCharmDamage(ItemStack.read(packetBuffer.readCompoundTag()), packetBuffer.readByte());
	}

	static void onMessage(PacketMobCharmDamage msg, Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> handleMessage(msg));
	}

	@OnlyIn(Dist.CLIENT)
	private static void handleMessage(PacketMobCharmDamage message) {
		CharmPane.addCharmToDraw(message.mobCharm, message.slot);
	}
}
