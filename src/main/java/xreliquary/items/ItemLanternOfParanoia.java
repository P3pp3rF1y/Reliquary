package xreliquary.items;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.init.ModItems;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.RegistryHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ItemLanternOfParanoia extends ItemToggleable {

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
		return Settings.LanternOfParanoia.placementScanRadius;
	}
	// event driven item, does nothing here.

	// minor jump buff
	@Override
	public void onUpdate(ItemStack stack, World world, Entity e, int i, boolean f) {
		if(!this.isEnabled(stack))
			return;
		if(world.isRemote)
			return;
		if(e instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) e;

			int playerX = MathHelper.floor(player.posX);
			int playerY = MathHelper.floor(player.getEntityBoundingBox().minY);
			int playerZ = MathHelper.floor(player.posZ);

			placement:
			for(int xDiff = -getRange(); xDiff <= getRange(); xDiff++) {
				for(int zDiff = -getRange(); zDiff <= getRange(); zDiff++) {
					for(int yDiff = getRange() / 2; yDiff >= -getRange() / 2; yDiff--) {
						int x = playerX + xDiff;
						int y = playerY + yDiff;
						int z = playerZ + zDiff;
						if(!player.world.isAirBlock(new BlockPos(x, y, z)))
							continue;
						int lightLevel = player.world.getLightFromNeighbors(new BlockPos(x, y, z));
						if(lightLevel > Settings.LanternOfParanoia.minLightLevel)
							continue;
						if(tryToPlaceTorchAround(stack, x, y, z, player, world))
							break placement;
					}
				}
			}

			//attemptPlacementByLookVector(player);

		}
	}

	//	TODO review torch placement if it needs changes otherwise just remove all of this commented out code
	//    public void attemptPlacementByLookVector(EntityPlayer player) {
	//        RayTraceResult mop = getMovingObjectPositionFromPlayer(player.world, player, false);
	//        if (!player.canPlayerEdit(x, y, z, side, ist))
	//            return;
	//
	//    }
	//
	//    //experimenting with a look vector based version of the lantern to avoid some really annoying stuff I can't figure out because I'm dumb.
	//    @Override
	//    protected RayTraceResult getMovingObjectPositionFromPlayer(World world, EntityPlayer player, boolean weirdBucketBoolean) {
	//        float movementCoefficient = 1.0F;
	//        float pitchOff = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * movementCoefficient;
	//        float yawOff = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * movementCoefficient;
	//        double xOff = player.prevPosX + (player.posX - player.prevPosX) * movementCoefficient;
	//        double yOff = player.prevPosY + (player.posY - player.prevPosY) * movementCoefficient + 1.62D - player.yOffset;
	//        double zOff = player.prevPosZ + (player.posZ - player.prevPosZ) * movementCoefficient;
	//        Vec3d playerVector = Vec3d.createVectorHelper(xOff, yOff, zOff);
	//        float cosTraceYaw = MathHelper.cos(-yawOff * 0.017453292F - (float) Math.PI);
	//        float sinTraceYaw = MathHelper.sin(-yawOff * 0.017453292F - (float) Math.PI);
	//        float cosTracePitch = -MathHelper.cos(-pitchOff * 0.017453292F);
	//        float sinTracePitch = MathHelper.sin(-pitchOff * 0.017453292F);
	//        float pythagoraStuff = sinTraceYaw * cosTracePitch;
	//        float pythagoraStuff2 = cosTraceYaw * cosTracePitch;
	//        double distCoeff = getRange();
	//        Vec3d rayTraceVector = playerVector.addVector(pythagoraStuff * distCoeff, sinTracePitch * distCoeff, pythagoraStuff2 * distCoeff);
	//        return world.rayTraceBlocks(playerVector, rayTraceVector, weirdBucketBoolean);
	//    }
	//
	private boolean findAndDrainSojournersStaff(EntityPlayer player) {
		if(player.capabilities.isCreativeMode)
			return true;
		for(int slot = 0; slot < player.inventory.getSizeInventory(); slot++) {
			if(player.inventory.getStackInSlot(slot).isEmpty())
				continue;
			if(!(player.inventory.getStackInSlot(slot).getItem() == ModItems.sojournerStaff))
				continue;
			Item torch = ItemBlock.getItemFromBlock(Blocks.TORCH);
			if(ItemSojournerStaff.removeItemFromInternalStorage(player.inventory.getStackInSlot(slot), torch, 1, player))
				return true;
		}
		return false;
	}

	private boolean tryToPlaceTorchAround(@Nonnull ItemStack stack, int xO, int yO, int zO, EntityPlayer player, World world) {

		double playerEyeHeight = player.posY + player.getEyeHeight();

		for(float xOff = -0.2F; xOff <= 0.2F; xOff += 0.4F) {
			for(float yOff = -0.2F; yOff <= 0.2F; yOff += 0.4F) {
				for(float zOff = -0.2F; zOff <= 0.2F; zOff += 0.4F) {

					Vec3d playerVec = new Vec3d(player.posX + xOff, playerEyeHeight + yOff, player.posZ + zOff);
					Vec3d rayTraceVector = new Vec3d((float) xO + 0.5D + xOff, (float) yO + 0.5D + yOff, (float) zO + 0.5D + zOff);

					RayTraceResult rayTraceResult = world.rayTraceBlocks(playerVec, rayTraceVector, false, false, true);

					if(rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
						IBlockState blockState = world.getBlockState(rayTraceResult.getBlockPos());

						if(blockState.getBlock().canCollideCheck(blockState, false))
							return false;
					}
				}
			}
		}

		if(Blocks.TORCH.canPlaceBlockAt(world, new BlockPos(xO, yO, zO))) {
			int rotation = ((MathHelper.floor((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) + 2) % 4;
			EnumFacing trySide = EnumFacing.DOWN;
			switch(rotation) {
				case (0):
					trySide = EnumFacing.EAST;
					break;
				case (1):
					trySide = EnumFacing.SOUTH;
					break;
				case (2):
					trySide = EnumFacing.WEST;
					break;
				case (3):
					trySide = EnumFacing.NORTH;
					break;
			}

			List<EnumFacing> trySides = new ArrayList<>();
			trySides.add(trySide);
			trySides.add(EnumFacing.DOWN);

			//TODO: alright this seems like there's way too much code and logic here for something that always adds all 4 sides of a block
			// once the mod is working this should be reviewed

			EnumFacing[] tryOtherSides = {EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST};
			for(EnumFacing tryOtherSide : tryOtherSides) {
				if(trySides.contains(tryOtherSide))
					continue;
				trySides.add(tryOtherSide);
			}
			ItemStack torchStack = new ItemStack(Blocks.TORCH);
			for(EnumFacing side : trySides) {
				if(!world.mayPlace(Blocks.TORCH, new BlockPos(xO, yO, zO), false, side, player))
					continue;
				if(!(InventoryHelper.consumeItem(torchStack, player, 0, 1) || findAndDrainSojournersStaff(player)))
					continue;
				IBlockState torchBlockState = getTorchSideAttempt(world, new BlockPos(xO, yO, zO), side, player);

				if(placeBlockAt(stack, player, world, new BlockPos(xO, yO, zO), torchBlockState)) {
					Blocks.TORCH.onBlockAdded(world, new BlockPos(xO, yO, zO), torchBlockState);
					double gauss = 0.5D + world.rand.nextFloat() / 2;
					world.spawnParticle(EnumParticleTypes.SPELL_MOB, xO + 0.5D, yO + 0.5D, zO + 0.5D, gauss, gauss, 0.0F);
					SoundType torchSoundType = Blocks.TORCH.getSoundType(torchBlockState, world, new BlockPos(xO, yO, zO), null);
					world.playSound(null, new BlockPos(xO, yO, zO), torchSoundType.getStepSound(), SoundCategory.BLOCKS, (torchSoundType.getVolume() + 1.0F) / 2.0F, torchSoundType.getPitch() * 0.8F);
					return true;
				}
			}
		}
		return false;
	}

	private IBlockState getTorchSideAttempt(World world, BlockPos pos, EnumFacing side, EntityPlayer player) {
		return Blocks.TORCH.getStateForPlacement(world, pos, side, pos.getX(), pos.getY(), pos.getZ(), 0, player, EnumHand.MAIN_HAND);
	}

	private boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, IBlockState torchBlockState) {
		if(!world.setBlockState(pos, torchBlockState, 3))
			return false;

		if(torchBlockState.getBlock() == Blocks.TORCH) {
			//noinspection deprecation
			Blocks.TORCH.neighborChanged(torchBlockState, world, pos, torchBlockState.getBlock(), pos);
			Blocks.TORCH.onBlockPlacedBy(world, pos, torchBlockState, player, stack);
		}

		return true;
	}
}
