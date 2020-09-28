package xreliquary.blocks.tile;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.World;

import java.util.function.Function;

/**
 * A small class for making tile entity code less repetitive. Takes care of client NBT loading.
 */
abstract class TileEntityBase extends TileEntity {

	public TileEntityBase(TileEntityType<?> tileEntityType) {
		super(tileEntityType);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return write(new CompoundNBT());
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(pos, 1, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		BlockState blockState = world.getBlockState(getPos());
		read(blockState, packet.getNbtCompound());

		world.notifyBlockUpdate(getPos(), blockState, blockState, 3);
	}
}
