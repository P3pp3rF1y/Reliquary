package xreliquary.items;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import xreliquary.init.ModItems;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LanternOfParanoiaItem extends ToggleableItem {
	private static final Map<String, Block> TORCH_BLOCKS = new HashMap<>();

	public LanternOfParanoiaItem() {
		super("lantern_of_paranoia", new Properties().maxStackSize(1));
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.EPIC;
	}

	public int getRange() {
		return Settings.COMMON.items.lanternOfParanoia.placementScanRadius.get();
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		if (!isEnabled(stack) || world.isRemote) {
			return;
		}
		if (entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entity;
			BlockPos.getAllInBox(player.getPosition().add(-getRange(), -getRange() / 2, -getRange()), player.getPosition().add(getRange(), getRange() / 2, getRange()))
					.anyMatch(pos -> {
						int lightLevel = player.world.getLightFor(LightType.BLOCK, pos);
						if (lightLevel > Settings.COMMON.items.lanternOfParanoia.minLightLevel.get()) {
							return false;
						}

						BlockState state = world.getBlockState(pos);
						Block block = state.getBlock();
						BlockItemUseContext context = new DirectionalPlaceContext(world, pos, Direction.DOWN, ItemStack.EMPTY, Direction.UP);
						if (block instanceof FlowingFluidBlock || (!block.isAir(state, world, pos) && !state.isReplaceable(BlockItemUseContext.func_221536_a(context, pos, Direction.DOWN)))) {
							return false;
						}
						return tryToPlaceTorchAround(stack, pos, player, world);
					});
		}
	}

	private boolean isBlockBlockingView(World world, PlayerEntity player, BlockPos pos) {
		double playerEyeHeight = player.posY + player.getEyeHeight();
		for (float xOff = -0.2F; xOff <= 0.2F; xOff += 0.4F) {
			for (float yOff = -0.2F; yOff <= 0.2F; yOff += 0.4F) {
				for (float zOff = -0.2F; zOff <= 0.2F; zOff += 0.4F) {

					Vec3d playerVec = new Vec3d(player.posX + xOff, playerEyeHeight + yOff, player.posZ + zOff);
					Vec3d rayTraceVector = new Vec3d(pos).add(0.5D + xOff, 0.5D + yOff, 0.5D + zOff);

					RayTraceResult rayTraceResult = world.rayTraceBlocks(new RayTraceContext(playerVec, rayTraceVector, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, player));

					if (rayTraceResult.getType() == RayTraceResult.Type.MISS) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private boolean tryToPlaceTorchAround(ItemStack stack, BlockPos pos, PlayerEntity player, World world) {
		if (isBlockBlockingView(world, player, pos)) {
			return false;
		}

		for (String torchRegistryName : Settings.COMMON.items.sojournerStaff.torches.get()) {
			Block torch = getTorchBlock(torchRegistryName);
			if (tryToPlaceTorchBlock(stack, pos, player, world, torch)) {
				return true;
			}
		}
		return false;
	}

	private boolean tryToPlaceTorchBlock(ItemStack stack, BlockPos pos, PlayerEntity player, World world, Block torch) {
		if (torch.getDefaultState().isValidPosition(world, pos)) {
			List<Direction> trySides = Lists.newArrayList(Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST);
			ItemStack torchStack = new ItemStack(torch);
			for (Direction side : trySides) {
				BlockState torchBlockState = getTorchSideAttempt(world, torch, pos, side);
				if (!world.func_217350_a(torchBlockState, pos, ISelectionContext.dummy()) || !(InventoryHelper.consumeItem(torchStack, player, 0, 1) || findAndDrainSojournersStaff(player, torch))) {
					continue;
				}

				if (placeBlockAt(stack, player, world, pos, torchBlockState)) {
					double gauss = 0.5D + world.rand.nextFloat() / 2;
					world.addParticle(ParticleTypes.ENTITY_EFFECT, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, gauss, gauss, 0.0F);
					SoundType torchSoundType = torch.getSoundType(torchBlockState, world, pos, null);
					world.playSound(null, pos, torchSoundType.getStepSound(), SoundCategory.BLOCKS, (torchSoundType.getVolume() + 1.0F) / 2.0F, torchSoundType.getPitch() * 0.8F);
					return true;
				}
			}
		}
		return false;
	}

	private boolean findAndDrainSojournersStaff(PlayerEntity player, Block torch) {
		if (player.isCreative()) {
			return true;
		}
		for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++) {
			if (player.inventory.getStackInSlot(slot).getItem() != ModItems.SOJOURNER_STAFF) {
				continue;
			}
			//noinspection ConstantConditions
			if (SojournerStaffItem.removeItemFromInternalStorage(player.inventory.getStackInSlot(slot), torch.getRegistryName().toString(), 1, player)) {
				return true;
			}
		}
		return false;
	}

	private Block getTorchBlock(String registryName) {
		if (!TORCH_BLOCKS.containsKey(registryName)) {
			TORCH_BLOCKS.put(registryName, ForgeRegistries.BLOCKS.getValue(new ResourceLocation(registryName)));
		}
		return TORCH_BLOCKS.get(registryName);
	}

	private BlockState getTorchSideAttempt(World world, Block torch, BlockPos pos, Direction side) {
		BlockPos facingPos = pos.offset(side);
		return torch.getStateForPlacement(torch.getDefaultState(), side, world.getBlockState(facingPos), world, pos, facingPos, Hand.MAIN_HAND);
	}

	private boolean placeBlockAt(ItemStack stack, PlayerEntity player, World world, BlockPos pos, BlockState torchBlockState) {
		if (!world.setBlockState(pos, torchBlockState, 3)) {
			return false;
		}

		torchBlockState.getBlock().onBlockPlacedBy(world, pos, torchBlockState, player, stack);
		return true;
	}
}
