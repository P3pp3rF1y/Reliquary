package reliquary.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import reliquary.reference.Reference;

public class PacketHandler {
	private PacketHandler() {}

	private static SimpleChannel networkWrapper;
	private static final String PROTOCOL = "1";

	public static void init() {
		networkWrapper = NetworkRegistry.newSimpleChannel(new ResourceLocation(Reference.MOD_ID, "channel"),
				() -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

		int idx = 0;
		networkWrapper.registerMessage(idx++, PacketFXThrownPotionImpact.class, PacketFXThrownPotionImpact::encode, PacketFXThrownPotionImpact::decode, PacketFXThrownPotionImpact::onMessage);
		networkWrapper.registerMessage(idx++, PacketFXConcussiveExplosion.class, PacketFXConcussiveExplosion::encode, PacketFXConcussiveExplosion::decode, PacketFXConcussiveExplosion::onMessage);
		networkWrapper.registerMessage(idx++, PacketMobCharmDamage.class, PacketMobCharmDamage::encode, PacketMobCharmDamage::decode, PacketMobCharmDamage::onMessage);
		networkWrapper.registerMessage(idx++, PacketPedestalFishHook.class, PacketPedestalFishHook::encode, PacketPedestalFishHook::decode, PacketPedestalFishHook::onMessage);
		networkWrapper.registerMessage(idx++, PacketFortuneCoinTogglePressed.class, PacketFortuneCoinTogglePressed::encode, PacketFortuneCoinTogglePressed::decode, PacketFortuneCoinTogglePressed::onMessage);
		networkWrapper.registerMessage(idx++, SpawnAngelheartVialParticlesPacket.class, (msg, packetBuffer) -> SpawnAngelheartVialParticlesPacket.encode(), packetBuffer1 -> SpawnAngelheartVialParticlesPacket.decode(), (spawnAngelheartVialParticlesPacket, contextSupplier) -> SpawnAngelheartVialParticlesPacket.onMessage(contextSupplier));
		networkWrapper.registerMessage(idx++, SpawnPhoenixDownParticlesPacket.class, SpawnPhoenixDownParticlesPacket::encode, packetBuffer2 -> SpawnPhoenixDownParticlesPacket.decode(), SpawnPhoenixDownParticlesPacket::onMessage);
		networkWrapper.registerMessage(idx, ScrolledItemPacket.class, ScrolledItemPacket::encode, ScrolledItemPacket::decode, ScrolledItemPacket::onMessage);
	}

	public static <M> void sendToClient(ServerPlayer player, M message) {
		networkWrapper.sendTo(message, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
	}

	public static <M> void sendToServer(M message) {
		networkWrapper.sendToServer(message);
	}

	public static <M> void sendToAllAround(M message, PacketDistributor.TargetPoint targetPoint) {
		networkWrapper.send(PacketDistributor.NEAR.with(() -> targetPoint), message);
	}
}
