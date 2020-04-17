package xreliquary.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import xreliquary.reference.Reference;

/**
 * Created by Xeno on 9/21/2014.
 */
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
		networkWrapper.registerMessage(idx++, PacketCountSync.class, PacketCountSync::encode, PacketCountSync::decode, PacketCountSync::onMessage);
		networkWrapper.registerMessage(idx++, PacketMobCharmDamage.class, PacketMobCharmDamage::encode, PacketMobCharmDamage::decode, PacketMobCharmDamage::onMessage);
		networkWrapper.registerMessage(idx++, PacketPedestalFishHook.class, PacketPedestalFishHook::encode, PacketPedestalFishHook::decode, PacketPedestalFishHook::onMessage);
		networkWrapper.registerMessage(idx++, PacketFortuneCoinTogglePressed.class, PacketFortuneCoinTogglePressed::encode, PacketFortuneCoinTogglePressed::decode, PacketFortuneCoinTogglePressed::onMessage);
		networkWrapper.registerMessage(idx++, SpawnAngelheartVialParticlesPacket.class, SpawnAngelheartVialParticlesPacket::encode, SpawnAngelheartVialParticlesPacket::decode, SpawnAngelheartVialParticlesPacket::onMessage);
		networkWrapper.registerMessage(idx, SpawnPhoenixDownParticlesPacket.class, SpawnPhoenixDownParticlesPacket::encode, SpawnPhoenixDownParticlesPacket::decode, SpawnPhoenixDownParticlesPacket::onMessage);
	}

	public static <M> void sendToClient(ServerPlayerEntity player, M message) {
		networkWrapper.sendTo(message, player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
	}

	public static <M> void sendToServer(M message) {
		networkWrapper.sendToServer(message);
	}

	public static <M> void sendToAllAround(M message, PacketDistributor.TargetPoint targetPoint) {
		networkWrapper.send(PacketDistributor.NEAR.with(() -> targetPoint), message);
	}
}
