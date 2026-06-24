package reliquary.util;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public class StreamCodecHelper {
	public static final StreamCodec<FriendlyByteBuf, Vec3> VEC_3_STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.DOUBLE, Vec3::x, ByteBufCodecs.DOUBLE,
			Vec3::y, ByteBufCodecs.DOUBLE, Vec3::z, Vec3::new);
}
