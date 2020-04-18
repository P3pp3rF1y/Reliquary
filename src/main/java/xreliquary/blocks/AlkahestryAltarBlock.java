package xreliquary.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.World;
import xreliquary.blocks.tile.AlkahestryAltarTileEntity;
import xreliquary.items.AlkahestryTomeItem;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.NBTHelper;

import javax.annotation.Nullable;
import java.util.Random;

public class AlkahestryAltarBlock extends BaseBlock {
	private static final BooleanProperty ACTIVE = BooleanProperty.create("active");
	private static final String REDSTONE_TAG = "redstone";

	public AlkahestryAltarBlock() {
		super(Names.Blocks.ALKAHESTRY_ALTAR, Properties.create(Material.ROCK).hardnessAndResistance(1.5F, 5.0F));
		setDefaultState(stateContainer.getBaseState().with(ACTIVE, false));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(ACTIVE);
	}

	@Override
	public int getLightValue(BlockState state, IEnviromentBlockReader world, BlockPos pos) {
		return Boolean.TRUE.equals(state.get(ACTIVE)) ? getAltarActiveLightLevel() : 0;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new AlkahestryAltarTileEntity();
	}

	private int getAltarActiveLightLevel() {
		return Settings.COMMON.blocks.altar.outputLightLevelWhileActive.get();
	}

	@Override
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		if (Boolean.FALSE.equals(state.get(ACTIVE)) || world.getDayTime() >= 12000 || !world.canBlockSeeSky(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ())) || rand.nextInt(3) != 0) {
			return;
		}
		world.addParticle(ParticleTypes.ENTITY_EFFECT, pos.getX() + 0.5D + rand.nextGaussian() / 8, pos.getY() + 1.1D, pos.getZ() + 0.5D + rand.nextGaussian() / 8, 0.9D, 0.9D, 0.0D);
	}

	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		ItemStack heldItem = player.getHeldItem(hand);
		if (Boolean.TRUE.equals(state.get(ACTIVE))) {
			return true;
		}
		AlkahestryAltarTileEntity altar = (AlkahestryAltarTileEntity) world.getTileEntity(pos);
		if (altar == null || heldItem.isEmpty()) {
			return true;
		}
		if (heldItem.getItem() == Items.REDSTONE) {
			int slot = getSlotWithRedstoneDust(player);
			if (slot == -1) {
				return true;
			}
			world.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.3F, 0.5F + 0.5F * altar.getRedstoneCount() + (float) (world.rand.nextGaussian() / 8));
			for (int particles = world.rand.nextInt(3); particles < 3 + altar.getRedstoneCount() * 4 + altar.getRedstoneCount(); particles++) {
				world.addParticle(RedstoneParticleData.REDSTONE_DUST, pos.getX() + 0.5D + world.rand.nextGaussian() / 5, pos.getY() + 1.2D, pos.getZ() + 0.5D + world.rand.nextGaussian() / 5, 1D, 0D, 0D);
			}
			if (world.isRemote) {
				return true;
			}
			player.inventory.decrStackSize(slot, 1);
			altar.addRedstone();
		} else if (heldItem.getItem() instanceof AlkahestryTomeItem && NBTHelper.getInt(REDSTONE_TAG, heldItem) > 0) {
			world.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.3F, 0.5F + 0.5F * altar.getRedstoneCount() + (float) (world.rand.nextGaussian() / 8));
			for (int particles = world.rand.nextInt(3); particles < 3 + altar.getRedstoneCount() * 4 + altar.getRedstoneCount(); particles++) {
				world.addParticle(RedstoneParticleData.REDSTONE_DUST, pos.getX() + 0.5D + world.rand.nextGaussian() / 5, pos.getY() + 1.2D, pos.getZ() + 0.5D + world.rand.nextGaussian() / 5, 1D, 0D, 0D);
			}
			if (world.isRemote) {
				return true;
			}
			NBTHelper.putInt(REDSTONE_TAG, heldItem, NBTHelper.getInt(REDSTONE_TAG, heldItem) - 1);
			altar.addRedstone();
		}
		return true;
	}

	private int getSlotWithRedstoneDust(PlayerEntity player) {
		for (int slot = 0; slot < player.inventory.mainInventory.size(); slot++) {
			if (player.inventory.mainInventory.get(slot).isEmpty()) {
				continue;
			}
			if (player.inventory.mainInventory.get(slot).getItem() == Items.REDSTONE) {
				return slot;
			}
		}
		return -1;
	}

	public static void updateAltarBlockState(boolean active, World world, BlockPos pos) {
		world.setBlockState(pos, world.getBlockState(pos).with(ACTIVE, active));
		AlkahestryAltarTileEntity te = (AlkahestryAltarTileEntity) world.getTileEntity(pos);
		if (te != null) {
			if (active) {
				te.startCycle();
			} else {
				te.stopCycle();
			}
		}
	}
}
