package reliquary.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import reliquary.blocks.tile.AlkahestryAltarBlockEntity;
import reliquary.init.ModBlocks;
import reliquary.items.AlkahestryTomeItem;
import reliquary.reference.Settings;
import reliquary.util.BlockEntityHelper;
import reliquary.util.NBTHelper;

import javax.annotation.Nullable;
import java.util.Random;

public class AlkahestryAltarBlock extends Block implements EntityBlock {
	private static final BooleanProperty ACTIVE = BooleanProperty.create("active");
	private static final String REDSTONE_TAG = "redstone";

	public AlkahestryAltarBlock() {
		super(Properties.of(Material.STONE).strength(1.5F, 5.0F).lightLevel(state -> Boolean.TRUE.equals(state.getValue(ACTIVE)) ? getAltarActiveLightLevel() : 0));
		registerDefaultState(stateDefinition.any().setValue(ACTIVE, false));
	}

	private static int getAltarActiveLightLevel() {
		return Settings.COMMON.blocks.altar.outputLightLevelWhileActive.get();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(ACTIVE);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new AlkahestryAltarBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		if (level.isClientSide) {
			return null;
		}

		return BlockEntityHelper.createTickerHelper(blockEntityType, ModBlocks.ALKAHESTRY_ALTAR_TILE_TYPE.get(), (l, p, s, be) -> be.serverTick(l, p));
	}

	@Override
	public void animateTick(BlockState state, Level world, BlockPos pos, Random rand) {
		if (Boolean.FALSE.equals(state.getValue(ACTIVE)) || world.getDayTime() >= 12000 || !world.canSeeSkyFromBelowWater(pos.above()) || rand.nextInt(3) != 0) {
			return;
		}
		world.addParticle(ParticleTypes.ENTITY_EFFECT, pos.getX() + 0.5D + rand.nextGaussian() / 8, pos.getY() + 1.1D, pos.getZ() + 0.5D + rand.nextGaussian() / 8, 0.9D, 0.9D, 0.0D);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		ItemStack heldItem = player.getItemInHand(hand);
		if (Boolean.TRUE.equals(state.getValue(ACTIVE))) {
			return InteractionResult.CONSUME;
		}
		AlkahestryAltarBlockEntity altar = (AlkahestryAltarBlockEntity) level.getBlockEntity(pos);
		if (altar == null || heldItem.isEmpty()) {
			return InteractionResult.SUCCESS;
		}
		if (heldItem.getItem() == Items.REDSTONE) {
			int slot = getSlotWithRedstoneDust(player);
			if (slot == -1) {
				return InteractionResult.SUCCESS;
			}
			level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.3F, 0.5F + 0.5F * altar.getRedstoneCount() + (float) (level.random.nextGaussian() / 8));
			for (int particles = level.random.nextInt(3); particles < 3 + altar.getRedstoneCount() * 4 + altar.getRedstoneCount(); particles++) {
				level.addParticle(DustParticleOptions.REDSTONE, pos.getX() + 0.5D + level.random.nextGaussian() / 5, pos.getY() + 1.2D, pos.getZ() + 0.5D + level.random.nextGaussian() / 5, 1D, 0D, 0D);
			}
			if (level.isClientSide) {
				return InteractionResult.SUCCESS;
			}
			player.getInventory().removeItem(slot, 1);
			altar.addRedstone(level, pos);
		} else if (heldItem.getItem() instanceof AlkahestryTomeItem && NBTHelper.getInt(REDSTONE_TAG, heldItem) > 0) {
			level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.3F, 0.5F + 0.5F * altar.getRedstoneCount() + (float) (level.random.nextGaussian() / 8));
			for (int particles = level.random.nextInt(3); particles < 3 + altar.getRedstoneCount() * 4 + altar.getRedstoneCount(); particles++) {
				level.addParticle(DustParticleOptions.REDSTONE, pos.getX() + 0.5D + level.random.nextGaussian() / 5, pos.getY() + 1.2D, pos.getZ() + 0.5D + level.random.nextGaussian() / 5, 1D, 0D, 0D);
			}
			if (level.isClientSide) {
				return InteractionResult.SUCCESS;
			}
			NBTHelper.putInt(REDSTONE_TAG, heldItem, NBTHelper.getInt(REDSTONE_TAG, heldItem) - 1);
			altar.addRedstone(level, pos);
		}
		return InteractionResult.CONSUME;
	}

	private int getSlotWithRedstoneDust(Player player) {
		for (int slot = 0; slot < player.getInventory().items.size(); slot++) {
			if (player.getInventory().items.get(slot).isEmpty()) {
				continue;
			}
			if (player.getInventory().items.get(slot).getItem() == Items.REDSTONE) {
				return slot;
			}
		}
		return -1;
	}

	public static void updateAltarBlockState(boolean active, Level level, BlockPos pos) {
		level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(ACTIVE, active));
		AlkahestryAltarBlockEntity te = (AlkahestryAltarBlockEntity) level.getBlockEntity(pos);
		if (te != null) {
			if (active) {
				te.startCycle(level);
			} else {
				te.stopCycle();
			}
		}
	}
}
