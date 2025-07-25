package reliquary.block.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

abstract class BlockEntityBase extends BlockEntity {
	protected BlockEntityBase(BlockEntityType<?> tileEntityType, BlockPos pos, BlockState state) {
		super(tileEntityType, pos, state);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag tag = new CompoundTag();
		saveAdditional(tag, registries);
		return tag;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries) {
		if (level == null || packet.getTag() == null) {
			super.onDataPacket(net, packet, registries);
			return;
		}

		BlockState blockState = level.getBlockState(getBlockPos());
		loadAdditional(packet.getTag(), registries);

		level.sendBlockUpdated(getBlockPos(), blockState, blockState, 3);
	}
}
