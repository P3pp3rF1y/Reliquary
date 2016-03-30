package xreliquary.items.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import xreliquary.network.PacketHandler;
import xreliquary.network.PacketHarvestRodExtPropsSync;

import java.util.ArrayDeque;
import java.util.Queue;

public class HarvestRodPlayerProps implements IExtendedEntityProperties {
	public final static String EXT_PROP_NAME = "HarvestRodPlayerProps";

	private BlockPos startBlockPos;
	private Queue<BlockPos> blockQueue;
	private int timesUsed;
	private EntityPlayer player;

	public HarvestRodPlayerProps(EntityPlayer player) {
		this.player = player;
		blockQueue = new ArrayDeque<>();
	}

	public void setStartBlockPos(BlockPos pos) {
		startBlockPos = pos;
	}

	public BlockPos getStartBlockPos() {
		return startBlockPos;
	}

	public void addBlockToQueue(BlockPos pos) {
		blockQueue.add(pos);
	}

	public BlockPos getNextBlockInQueue() {
		return blockQueue.poll();
	}

	public void clearBlockQueue() {
		blockQueue.clear();
	}

	public boolean isQueueEmpty() {
		return blockQueue.isEmpty();
	}

	public void incrementTimesUsed() {
		timesUsed++;

		sync();
	}

	public int getTimesUsed() {
		return timesUsed;
	}

	public void setTimesUsed(int timesUsed) {
		this.timesUsed = timesUsed;
	}

	public void reset() {
		startBlockPos = null;
		blockQueue.clear();
		timesUsed = 0;

		sync();
	}

	private void sync() {
		if(!player.worldObj.isRemote)
			PacketHandler.networkWrapper.sendTo(new PacketHarvestRodExtPropsSync(this.getTimesUsed()), (EntityPlayerMP) player);
	}

	public static final HarvestRodPlayerProps get(EntityPlayer player) {
		return (HarvestRodPlayerProps) player.getExtendedProperties(EXT_PROP_NAME);
	}

	public static final void register(EntityPlayer player) {
		player.registerExtendedProperties(EXT_PROP_NAME, new HarvestRodPlayerProps(player));
	}

	@Override
	public void saveNBTData(NBTTagCompound compound) {
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
	}

	@Override
	public void init(Entity entity, World world) {

	}
}
