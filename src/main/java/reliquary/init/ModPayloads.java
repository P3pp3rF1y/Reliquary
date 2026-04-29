package reliquary.init;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import reliquary.Reliquary;
import reliquary.network.*;

public class ModPayloads {
	private ModPayloads() {
	}

	public static void registerPackets(final RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar(Reliquary.MOD_ID).versioned(Reliquary.getNetworkProtocolVersion());
		registrar.playToClient(SpawnThrownPotionImpactParticlesPayload.TYPE, SpawnThrownPotionImpactParticlesPayload.STREAM_CODEC, (payload1, context1) -> SpawnThrownPotionImpactParticlesPayload.handlePayload(payload1));
		registrar.playToClient(SpawnAngelheartVialParticlesPayload.TYPE, SpawnAngelheartVialParticlesPayload.STREAM_CODEC, (payload, context) -> SpawnAngelheartVialParticlesPayload.handlePayload(payload));
		registrar.playToClient(SpawnPhoenixDownParticlesPayload.TYPE, SpawnPhoenixDownParticlesPayload.STREAM_CODEC, (payload, context) -> SpawnPhoenixDownParticlesPayload.handlePayload(payload));
		registrar.playToServer(ScrolledItemPayload.TYPE, ScrolledItemPayload.STREAM_CODEC, ScrolledItemPayload::handlePayload);
		registrar.playToClient(SpawnConcussiveExplosionParticlesPayload.TYPE, SpawnConcussiveExplosionParticlesPayload.STREAM_CODEC, SpawnConcussiveExplosionParticlesPayload::handlePayload);
		registrar.playToClient(MobCharmDamagePayload.TYPE, MobCharmDamagePayload.STREAM_CODEC, (payload, context) -> MobCharmDamagePayload.handlePayload(payload));
		registrar.playToClient(PedestalFishHookPayload.TYPE, PedestalFishHookPayload.STREAM_CODEC, (payload, context) -> PedestalFishHookPayload.handlePayload(payload));
		registrar.playToServer(FortuneCoinTogglePressedPayload.TYPE, FortuneCoinTogglePressedPayload.STREAM_CODEC, FortuneCoinTogglePressedPayload::handlePayload);
	}
}
