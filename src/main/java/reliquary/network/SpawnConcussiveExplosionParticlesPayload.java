package reliquary.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import reliquary.Reliquary;
import reliquary.entity.ConcussiveExplosion;
import reliquary.util.StreamCodecHelper;

public record SpawnConcussiveExplosionParticlesPayload(float size, Vec3 pos) implements CustomPacketPayload {
	public static final Type<SpawnConcussiveExplosionParticlesPayload> TYPE = new Type<>(Reliquary.getRL("spawn_concussive_explosion_particles"));
	public static final StreamCodec<FriendlyByteBuf, SpawnConcussiveExplosionParticlesPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.FLOAT,
			SpawnConcussiveExplosionParticlesPayload::size,
			StreamCodecHelper.VEC_3_STREAM_CODEC,
			SpawnConcussiveExplosionParticlesPayload::pos,
			SpawnConcussiveExplosionParticlesPayload::new
	);

	public static void handlePayload(SpawnConcussiveExplosionParticlesPayload payload, IPayloadContext context) {
		ConcussiveExplosion explosion = new ConcussiveExplosion(context.player().level(), null, null, payload.pos, payload.size, false);
		explosion.finalizeExplosion(false);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
