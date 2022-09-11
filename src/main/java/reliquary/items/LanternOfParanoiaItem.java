package reliquary.items;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.registries.ForgeRegistries;
import reliquary.init.ModItems;
import reliquary.reference.Settings;
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
		super(new Properties().stacksTo(1));
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.EPIC;
	}

	public int getRange() {
		return Settings.COMMON.items.lanternOfParanoia.placementScanRadius.get();
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
		if (world.isClientSide || !isEnabled(stack) || isInCooldown(stack, world)) {
			return;
		}
		if (entity instanceof Player player) {
			if (getPositionsInRange(player).anyMatch(pos -> tryToPlaceAtPos(stack, world, player, pos))) {
				setCooldown(stack, world, SUCCESS_COOLDOWN);
			} else {
				setCooldown(stack, world, NOTHING_FOUND_COOLDOWN);
			}
		}
	}

	private Stream<BlockPos> getPositionsInRange(Player player) {
		return BlockPos.betweenClosedStream(player.blockPosition().offset(-getRange(), -getRange() / 2, -getRange()), player.blockPosition().offset(getRange(), getRange() / 2, getRange()));
	}

	private boolean tryToPlaceAtPos(ItemStack stack, Level world, Player player, BlockPos pos) {
		int lightLevel = player.level.getBrightness(LightLayer.BLOCK, pos);
		if (lightLevel > Settings.COMMON.items.lanternOfParanoia.minLightLevel.get()) {
			return false;
		}

		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		BlockPlaceContext context = new BlockPlaceContext(player, InteractionHand.MAIN_HAND, ItemStack.EMPTY, new BlockHitResult(Vec3.atBottomCenterOf(pos), Direction.UP, pos, false));
		if (isBadPlacementToTry(world, pos, state, block, context)) {
			return false;
		}
		return tryToPlaceTorchAround(stack, pos, player, world);
	}

	private boolean isBadPlacementToTry(Level world, BlockPos pos, BlockState state, Block block, BlockPlaceContext context) {
		return block instanceof LiquidBlock
				|| world.getBlockState(pos.below()).getBlock().hasDynamicShape()
				|| !state.getFluidState().isEmpty()
				|| (!state.isAir() && !state.canBeReplaced(BlockPlaceContext.at(context, pos, Direction.DOWN)));
	}

	private boolean isBlockBlockingView(Level world, Player player, BlockPos pos) {
		double playerEyeHeight = player.getY() + player.getEyeHeight();
		for (float xOff = -0.2F; xOff <= 0.2F; xOff += 0.4F) {
			for (float yOff = -0.2F; yOff <= 0.2F; yOff += 0.4F) {
				for (float zOff = -0.2F; zOff <= 0.2F; zOff += 0.4F) {

					Vec3 playerVec = new Vec3(player.getX() + xOff, playerEyeHeight + yOff, player.getZ() + zOff);
					Vec3 rayTraceVector = new Vec3(pos.getX(), pos.getY(), pos.getZ()).add(0.5D + xOff, 0.5D + yOff, 0.5D + zOff);

					HitResult rayTraceResult = world.clip(new ClipContext(playerVec, rayTraceVector, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));

					if (rayTraceResult.getType() == HitResult.Type.MISS) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private boolean tryToPlaceTorchAround(ItemStack stack, BlockPos pos, Player player, Level world) {
		if (isBlockBlockingView(world, player, pos)) {
			return false;
		}

		for (String torchRegistryName : Settings.COMMON.items.lanternOfParanoia.torches.get()) {
			if (getTorchBlock(torchRegistryName).map(torch -> tryToPlaceTorchBlock(stack, pos, player, world, torch)).orElse(false)) {
				return true;
			}
		}
		return false;
	}

	private boolean tryToPlaceTorchBlock(ItemStack stack, BlockPos pos, Player player, Level world, Block torch) {
		if (torch.defaultBlockState().canSurvive(world, pos)) {
			List<Direction> trySides = Lists.newArrayList(Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST);
			ItemStack torchStack = new ItemStack(torch);
			for (Direction side : trySides) {
				BlockState torchBlockState = getTorchSideAttempt(player, torch, pos, side);
				if (torchBlockState == null || !torchBlockState.canSurvive(world, pos)
						|| !world.isUnobstructed(torchBlockState, pos, CollisionContext.empty())
						|| !(InventoryHelper.consumeItem(torchStack, player, 0, 1)
						|| findAndDrainSojournersStaff(player, torchStack.getItem()))) {
					continue;
				}

				if (placeBlockAt(stack, player, world, pos, torchBlockState)) {
					double gauss = 0.5D + world.random.nextFloat() / 2;
					world.addParticle(ParticleTypes.ENTITY_EFFECT, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, gauss, gauss, 0.0F);
					SoundType torchSoundType = torch.getSoundType(torchBlockState, world, pos, null);
					world.playSound(null, pos, torchSoundType.getStepSound(), SoundSource.BLOCKS, (torchSoundType.getVolume() + 1.0F) / 2.0F, torchSoundType.getPitch() * 0.8F);
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
			if (ModItems.SOJOURNER_STAFF.get().removeItemFromInternalStorage(player.getInventory().getItem(slot), torchItem, 1, false, player)) {
				return true;
			}
		}
		return false;
	}

	private Optional<Block> getTorchBlock(String registryName) {
		return Optional.ofNullable(TORCH_BLOCKS.computeIfAbsent(registryName, rn -> ForgeRegistries.BLOCKS.getValue(new ResourceLocation(rn))));
	}

	@Nullable
	private BlockState getTorchSideAttempt(Player player, Block torch, BlockPos pos, Direction side) {
		return torch.getStateForPlacement(new BlockPlaceContext(player, InteractionHand.MAIN_HAND, ItemStack.EMPTY, new BlockHitResult(Vec3.atBottomCenterOf(pos), side, pos, false)));
	}

	private boolean placeBlockAt(ItemStack stack, Player player, Level world, BlockPos pos, BlockState torchBlockState) {
		if (!world.setBlock(pos, torchBlockState, 3)) {
			return false;
		}

		torchBlockState.getBlock().setPlacedBy(world, pos, torchBlockState, player, stack);
		return true;
	}
}
