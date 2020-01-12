package xreliquary.blocks.tile;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

/**
 * A small class for making tile entity code less repetitive. Takes care of client NBT loading.
 */
abstract class TileEntityBase extends TileEntity {

	public TileEntityBase(TileEntityType<?> tileEntityType) {
		super(tileEntityType);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return this.write(new CompoundNBT());
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.pos, 1, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		read(packet.getNbtCompound());

		BlockState blockState = world.getBlockState(this.getPos());
		world.notifyBlockUpdate(this.getPos(), blockState, blockState, 3);
	}
}
