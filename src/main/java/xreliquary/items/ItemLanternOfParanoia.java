package xreliquary.items;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.init.ModItems;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemLanternOfParanoia extends ItemToggleable {
	private static final Map<String, Block> TORCH_BLOCKS = new HashMap<>();

	public ItemLanternOfParanoia() {
		super(Names.Items.LANTERN_OF_PARANOIA);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxStackSize(1);
		canRepair = false;
	}

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
	}

	public int getRange() {
		return Settings.Items.LanternOfParanoia.placementScanRadius;
	}
	// event driven item, does nothing here.

	// minor jump buff
	@Override
	public void onUpdate(ItemStack stack, World world, Entity e, int i, boolean f) {
		if (!this.isEnabled(stack))
			return;
		if (world.isRemote)
			return;
		if (e instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) e;

			int playerX = MathHelper.floor(player.posX);
			int playerY = MathHelper.floor(player.getEntityBoundingBox().minY);
			int playerZ = MathHelper.floor(player.posZ);

			placement:
			for (int xDiff = -getRange(); xDiff <= getRange(); xDiff++) {
				for (int zDiff = -getRange(); zDiff <= getRange(); zDiff++) {
					for (int yDiff = getRange() / 2; yDiff >= -getRange() / 2; yDiff--) {
						int x = playerX + xDiff;
						int y = playerY + yDiff;
						int z = playerZ + zDiff;

						BlockPos pos = new BlockPos(x, y, z);
						IBlockState state = world.getBlockState(pos);
						Block block = state.getBlock();

						if (block instanceof BlockLiquid || block instanceof IFluidBlock || !block.isAir(state, world, pos) && !block.isReplaceable(world, pos))
							continue;
						int lightLevel = player.world.getLightFor(EnumSkyBlock.BLOCK, pos);
						if (lightLevel > Settings.Items.LanternOfParanoia.minLightLevel)
							continue;
						if (tryToPlaceTorchAround(stack, x, y, z, player, world))
							break placement;
					}
				}
			}
		}
	}

	private boolean findAndDrainSojournersStaff(EntityPlayer player, Block torch) {
		if (player.capabilities.isCreativeMode)
			return true;
		for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++) {
			if (player.inventory.getStackInSlot(slot).getItem() != ModItems.sojournerStaff)
				continue;
			//noinspection ConstantConditions
			if (ItemSojournerStaff.removeItemFromInternalStorage(player.inventory.getStackInSlot(slot), torch.getRegistryName().toString(), 1, player)) {
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

	private boolean tryToPlaceTorchAround(@Nonnull ItemStack stack, int xO, int yO, int zO, EntityPlayer player, World world) {

		double playerEyeHeight = player.posY + player.getEyeHeight();

		for (float xOff = -0.2F; xOff <= 0.2F; xOff += 0.4F) {
			for (float yOff = -0.2F; yOff <= 0.2F; yOff += 0.4F) {
				for (float zOff = -0.2F; zOff <= 0.2F; zOff += 0.4F) {

					Vec3d playerVec = new Vec3d(player.posX + xOff, playerEyeHeight + yOff, player.posZ + zOff);
					Vec3d rayTraceVector = new Vec3d((float) xO + 0.5D + xOff, (float) yO + 0.5D + yOff, (float) zO + 0.5D + zOff);

					RayTraceResult rayTraceResult = world.rayTraceBlocks(playerVec, rayTraceVector, false, false, true);

					if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
						IBlockState blockState = world.getBlockState(rayTraceResult.getBlockPos());

						if (blockState.getBlock().canCollideCheck(blockState, false))
							return false;
					}
				}
			}
		}

		for (String torchRegistryName : Settings.Items.SojournerStaff.torches) {
			Block torch = getTorchBlock(torchRegistryName);
			if (torch.canPlaceBlockAt(world, new BlockPos(xO, yO, zO))) {
				List<EnumFacing> trySides = Lists.newArrayList(EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST);
				ItemStack torchStack = new ItemStack(torch);
				for (EnumFacing side : trySides) {
					if (!world.mayPlace(torch, new BlockPos(xO, yO, zO), false, side, player))
						continue;
					if (!(InventoryHelper.consumeItem(torchStack, player, 0, 1) || findAndDrainSojournersStaff(player, torch)))
						continue;
					IBlockState torchBlockState = getTorchSideAttempt(world, torch, new BlockPos(xO, yO, zO), side, player);

					if (placeBlockAt(stack, player, world, new BlockPos(xO, yO, zO), torchBlockState)) {
						torch.onBlockAdded(world, new BlockPos(xO, yO, zO), torchBlockState);
						double gauss = 0.5D + world.rand.nextFloat() / 2;
						world.spawnParticle(EnumParticleTypes.SPELL_MOB, xO + 0.5D, yO + 0.5D, zO + 0.5D, gauss, gauss, 0.0F);
						SoundType torchSoundType = torch.getSoundType(torchBlockState, world, new BlockPos(xO, yO, zO), null);
						world.playSound(null, new BlockPos(xO, yO, zO), torchSoundType.getStepSound(), SoundCategory.BLOCKS, (torchSoundType.getVolume() + 1.0F) / 2.0F, torchSoundType.getPitch() * 0.8F);
						return true;
					}
				}
			}
		}
		return false;
	}

	private IBlockState getTorchSideAttempt(World world, Block torch, BlockPos pos, EnumFacing side, EntityPlayer player) {
		return torch.getStateForPlacement(world, pos, side, pos.getX(), pos.getY(), pos.getZ(), 0, player, EnumHand.MAIN_HAND);
	}

	private boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, IBlockState torchBlockState) {
		if (!world.setBlockState(pos, torchBlockState, 3))
			return false;

		if (torchBlockState.getBlock() == torchBlockState.getBlock()) {
			//noinspection deprecation
			torchBlockState.getBlock().neighborChanged(torchBlockState, world, pos, torchBlockState.getBlock(), pos);
			torchBlockState.getBlock().onBlockPlacedBy(world, pos, torchBlockState, player, stack);
		}

		return true;
	}
}
