package xreliquary.blocks.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;

/**
 * A small class for making tile entity code less repetitive. Takes care of client NBT loading.
 */
abstract class TileEntityBase extends TileEntity {

	@Nonnull
	@Override
	public NBTTagCompound getUpdateTag() {
		return this.writeToNBT(new NBTTagCompound());
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.pos, 1, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		readFromNBT(packet.getNbtCompound());

		IBlockState blockState = world.getBlockState(this.getPos());
		world.notifyBlockUpdate(this.getPos(), blockState, blockState, 3);
	}
}
