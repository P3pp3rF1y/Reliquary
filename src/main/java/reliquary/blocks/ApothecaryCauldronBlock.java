package reliquary.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import reliquary.blocks.tile.ApothecaryCauldronBlockEntity;
import reliquary.init.ModBlocks;
import reliquary.reference.Settings;
import reliquary.util.BlockEntityHelper;
import reliquary.util.WorldHelper;

import javax.annotation.Nullable;

public class ApothecaryCauldronBlock extends Block implements EntityBlock {

	public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 3);
	private static final VoxelShape INSIDE = box(2.0D, 4.0D, 2.0D, 14.0D, 16.0D, 14.0D);
	private static final VoxelShape SHAPE = Shapes.join(Shapes.block(), Shapes.or(box(0.0D, 0.0D, 4.0D, 16.0D, 3.0D, 12.0D), box(4.0D, 0.0D, 0.0D, 12.0D, 3.0D, 16.0D), box(2.0D, 0.0D, 2.0D, 14.0D, 3.0D, 14.0D), INSIDE), BooleanOp.ONLY_FIRST);

	public ApothecaryCauldronBlock() {
		super(Properties.of(Material.METAL).strength(1.5F, 5.0F).noOcclusion());
		registerDefaultState(stateDefinition.any().setValue(LEVEL, 0));
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		if (Boolean.TRUE.equals(Settings.COMMON.disable.disablePotions.get())) {
			return;
		}
		super.fillItemCategory(group, items);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(LEVEL);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public VoxelShape getInteractionShape(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return INSIDE;
	}

	@Override
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
		if (!world.isClientSide) {
			ApothecaryCauldronBlockEntity cauldron = (ApothecaryCauldronBlockEntity) world.getBlockEntity(pos);
			if (cauldron != null) {
				cauldron.handleCollidingEntity(world, pos, entity);
			}
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		ItemStack heldItem = player.getItemInHand(hand);
		if (world.isClientSide) {
			return !heldItem.isEmpty() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
		} else {
			if (heldItem.isEmpty()) {
				return InteractionResult.CONSUME;
			} else {
				ApothecaryCauldronBlockEntity cauldron = (ApothecaryCauldronBlockEntity) world.getBlockEntity(pos);

				if (cauldron != null) {
					return cauldron.handleBlockActivation(world, player, hand, pos);
				}
			}
		}
		return InteractionResult.CONSUME;
	}

	@Override
	public void handlePrecipitation(BlockState state, Level level, BlockPos pos, Biome.Precipitation precipitation) {
		if (precipitation == Biome.Precipitation.RAIN && level.getRandom().nextFloat() < 0.05F) {
			WorldHelper.getBlockEntity(level, pos, ApothecaryCauldronBlockEntity.class).ifPresent(ApothecaryCauldronBlockEntity::fillWithRain);
		}
	}

	/**
	 * If this returns true, then comparators facing away from this block will
	 * use the value from getComparatorInputOverride instead of the actual
	 * redstone signal strength.
	 */
	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	/**
	 * If hasComparatorInputOverride returns true, the return value from this is
	 * used instead of the redstone signal strength when this block inputs to a
	 * comparator.
	 */
	@Override
	public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
		ApothecaryCauldronBlockEntity cauldron = (ApothecaryCauldronBlockEntity) world.getBlockEntity(pos);
		if (cauldron != null) {
			return cauldron.getLiquidLevel();
		}
		return 0;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ApothecaryCauldronBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return BlockEntityHelper.createTickerHelper(blockEntityType, ModBlocks.APOTHECARY_CAULDRON_TILE_TYPE.get(), (l, p, s, be) -> be.serverTick(l, p));
	}
}
