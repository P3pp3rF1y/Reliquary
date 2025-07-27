package reliquary.item;

import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import reliquary.init.ModItems;
import reliquary.reference.Config;
import reliquary.util.InventoryHelper;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class LanternOfParanoiaItem extends ToggleableItem {
	private static final int SUCCESS_COOLDOWN = 4;
	private static final int NOTHING_FOUND_COOLDOWN = 10;

	private static final Map<String, Block> TORCH_BLOCKS = new HashMap<>();

	public LanternOfParanoiaItem() {
		super(new Properties().stacksTo(1).rarity(Rarity.EPIC));
	}

	@Override
	public MutableComponent getName(ItemStack stack) {
		return super.getName(stack).withStyle(ChatFormatting.YELLOW);
	}

	public int getRange() {
		return Config.COMMON.items.lanternOfParanoia.placementScanRadius.get();
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
		if (level.isClientSide || !(entity instanceof Player player) || player.isSpectator() || !isEnabled(stack) || isInCooldown(stack, level)) {
			return;
		}
		if (getPositionsInRange(player).anyMatch(pos -> tryToPlaceAtPos(stack, level, player, pos))) {
			setCooldown(stack, level, SUCCESS_COOLDOWN);
		} else {
			setCooldown(stack, level, NOTHING_FOUND_COOLDOWN);
		}
	}

	private Stream<BlockPos> getPositionsInRange(Player player) {
		return BlockPos.betweenClosedStream(player.blockPosition().offset(-getRange(), -getRange() / 2, -getRange()), player.blockPosition().offset(getRange(), getRange() / 2, getRange()));
	}

	private boolean tryToPlaceAtPos(ItemStack stack, Level level, Player player, BlockPos pos) {
		int lightLevel = player.level().getBrightness(LightLayer.BLOCK, pos);
		if (lightLevel > Config.COMMON.items.lanternOfParanoia.minLightLevel.get()) {
			return false;
		}

		BlockState state = level.getBlockState(pos);
		Block block = state.getBlock();

		BlockPlaceContext context = new BlockPlaceContext(player, InteractionHand.MAIN_HAND, ItemStack.EMPTY, new BlockHitResult(Vec3.atBottomCenterOf(pos), Direction.UP, pos, false));
		if (isBadPlacementToTry(level, pos, state, block, context)) {
			return false;
		}
		return tryToPlaceTorchAround(stack, pos, player, level);
	}

	private boolean isBadPlacementToTry(Level level, BlockPos pos, BlockState state, Block block, BlockPlaceContext context) {
		return block instanceof LiquidBlock
				|| level.getBlockState(pos.below()).getBlock().hasDynamicShape()
				|| !state.getFluidState().isEmpty()
				|| (!state.isAir() && !state.canBeReplaced(BlockPlaceContext.at(context, pos, Direction.DOWN)));
	}

	private boolean isBlockBlockingView(Level level, Player player, BlockPos pos) {
		double playerEyeHeight = player.getY() + player.getEyeHeight();
		for (float xOff = -0.2F; xOff <= 0.2F; xOff += 0.4F) {
			for (float yOff = -0.2F; yOff <= 0.2F; yOff += 0.4F) {
				for (float zOff = -0.2F; zOff <= 0.2F; zOff += 0.4F) {

					Vec3 playerVec = new Vec3(player.getX() + xOff, playerEyeHeight + yOff, player.getZ() + zOff);
					Vec3 rayTraceVector = new Vec3(pos.getX(), pos.getY(), pos.getZ()).add(0.5D + xOff, 0.5D + yOff, 0.5D + zOff);

					HitResult rayTraceResult = level.clip(new ClipContext(playerVec, rayTraceVector, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));

					if (rayTraceResult.getType() == HitResult.Type.MISS) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private boolean tryToPlaceTorchAround(ItemStack stack, BlockPos pos, Player player, Level level) {
		if (isBlockBlockingView(level, player, pos)) {
			return false;
		}

		for (String torchRegistryName : Config.COMMON.items.lanternOfParanoia.torches.get()) {
			if (getTorchBlock(torchRegistryName).map(torch -> tryToPlaceTorchBlock(stack, pos, player, level, torch)).orElse(false)) {
				return true;
			}
		}
		return false;
	}

	private boolean tryToPlaceTorchBlock(ItemStack stack, BlockPos pos, Player player, Level level, Block torch) {
		if (torch.defaultBlockState().canSurvive(level, pos)) {
			List<Direction> trySides = Lists.newArrayList(Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST);
			ItemStack torchStack = new ItemStack(torch);
			for (Direction side : trySides) {
				BlockState torchBlockState = getTorchSideAttempt(player, torch, pos, side);
				if (torchBlockState == null || !torchBlockState.canSurvive(level, pos)
						|| !level.isUnobstructed(torchBlockState, pos, CollisionContext.empty())
						|| !(InventoryHelper.consumeItem(torchStack, player, 0, 1)
						|| findAndDrainSojournersStaff(player, torchStack.getItem()))) {
					continue;
				}

				if (placeBlockAt(stack, player, level, pos, torchBlockState)) {
					float gauss = 0.5F + level.random.nextFloat() / 2;
					level.addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, gauss, gauss, 0.0F), pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 0, 0, 0);
					SoundType torchSoundType = torch.getSoundType(torchBlockState, level, pos, null);
					level.playSound(null, pos, torchSoundType.getStepSound(), SoundSource.BLOCKS, (torchSoundType.getVolume() + 1.0F) / 2.0F, torchSoundType.getPitch() * 0.8F);
					return true;
				}
			}
		}
		return false;
	}

	private boolean findAndDrainSojournersStaff(Player player, Item torchItem) {
		if (player.isCreative()) {
			return true;
		}
		for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
			if (player.getInventory().getItem(slot).getItem() != ModItems.SOJOURNER_STAFF.get()) {
				continue;
			}
			if (ModItems.SOJOURNER_STAFF.get().removeTorch(player.getInventory().getItem(slot))) {
				return true;
			}
		}
		return false;
	}

	private Optional<Block> getTorchBlock(String registryName) {
		Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(registryName));
		if (block == Blocks.AIR) {
			return Optional.empty();
		}
		return Optional.of(TORCH_BLOCKS.computeIfAbsent(registryName, rn -> BuiltInRegistries.BLOCK.get(ResourceLocation.parse(rn))));
	}

	@Nullable
	private BlockState getTorchSideAttempt(Player player, Block torch, BlockPos pos, Direction side) {
		return torch.getStateForPlacement(new BlockPlaceContext(player, InteractionHand.MAIN_HAND, ItemStack.EMPTY, new BlockHitResult(Vec3.atBottomCenterOf(pos), side, pos, false)));
	}

	private boolean placeBlockAt(ItemStack stack, Player player, Level level, BlockPos pos, BlockState torchBlockState) {
		if (!level.setBlock(pos, torchBlockState, 3)) {
			return false;
		}

		torchBlockState.getBlock().setPlacedBy(level, pos, torchBlockState, player, stack);
		return true;
	}
}
