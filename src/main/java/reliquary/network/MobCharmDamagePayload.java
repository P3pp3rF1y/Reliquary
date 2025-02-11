package reliquary.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import reliquary.Reliquary;
import reliquary.client.gui.hud.CharmPane;

public record MobCharmDamagePayload(ItemStack mobCharm, int slot) implements CustomPacketPayload {
	public static final Type<MobCharmDamagePayload> TYPE = new Type<>(Reliquary.getRL("mob_charm_damage"));
	public static final StreamCodec<RegistryFriendlyByteBuf, MobCharmDamagePayload> STREAM_CODEC = StreamCodec.composite(
			ItemStack.OPTIONAL_STREAM_CODEC,
			MobCharmDamagePayload::mobCharm,
			ByteBufCodecs.INT,
			MobCharmDamagePayload::slot,
			MobCharmDamagePayload::new);

	public static void handlePayload(MobCharmDamagePayload payload) {
		CharmPane.addCharmToDraw(payload.mobCharm, payload.slot);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
