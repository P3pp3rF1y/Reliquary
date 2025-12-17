package reliquary.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import reliquary.Reliquary;
import reliquary.util.StreamCodecHelper;

public record SpawnPhoenixDownParticlesPayload(Vec3 position) implements CustomPacketPayload {
	public static final Type<SpawnPhoenixDownParticlesPayload> TYPE = new Type<>(Reliquary.getIdentifier("spawn_phoenix_down_particles"));
	public static final StreamCodec<FriendlyByteBuf, SpawnPhoenixDownParticlesPayload> STREAM_CODEC = StreamCodec.composite(
			StreamCodecHelper.VEC_3_STREAM_CODEC,
			SpawnPhoenixDownParticlesPayload::position,
			SpawnPhoenixDownParticlesPayload::new);

	public static void handlePayload(SpawnPhoenixDownParticlesPayload payload) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) {
			return;
		}

		for (int particles = 0; particles <= 400; particles++) {
			RandomSource random = player.level().random;
			player.level().addParticle(ParticleTypes.FLAME, payload.position.x, payload.position.y, payload.position.z, random.nextGaussian() * 8, random.nextGaussian() * 8, random.nextGaussian() * 8);
		}
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
