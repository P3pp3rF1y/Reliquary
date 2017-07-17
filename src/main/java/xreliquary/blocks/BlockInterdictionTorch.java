package xreliquary.blocks;

import net.minecraft.block.BlockTorch;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class BlockInterdictionTorch extends BlockTorch {

	public BlockInterdictionTorch() {
		super();
		BlockBase.init(this, Names.Blocks.INTERDICTION_TORCH);
		this.setHardness(0.0F);
		this.setLightLevel(1.0F);
		this.setTickRandomly(true);
		this.blockSoundType = SoundType.WOOD;
	}

	@Nonnull
	@Override
	public IBlockState getStateForPlacement(
			@Nonnull World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		world.scheduleBlockUpdate(pos, this, tickRate(), 1);
		return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer);
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
		super.updateTick(world, pos, state, random);
		world.scheduleBlockUpdate(pos, this, tickRate(), 1);
		if(world.isRemote)
			return;
		int radius = Settings.InterdictionTorch.pushRadius;

		List<String> pushableEntitiesBlacklist = Settings.InterdictionTorch.pushableEntitiesBlacklist;
		List<String> pushableProjectilesBlacklist = Settings.InterdictionTorch.pushableProjectilesBlacklist;

		List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.getX() - radius, pos.getY() - radius, pos.getZ() - radius, pos.getX() + radius, pos.getY() + radius, pos.getZ() + radius));
		for(Entity entity : entities) {
			if(entity instanceof EntityPlayer)
				continue;
			String entityName = EntityList.getEntityString(entity);
			if(!pushableEntitiesBlacklist.contains(entityName) || (Settings.InterdictionTorch.canPushProjectiles && !pushableProjectilesBlacklist.contains(entityName))) {
				double distance = entity.getDistance((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
				if(distance >= radius || distance == 0)
					continue;

				// the multiplier is based on a set rate added to an inverse
				// proportion to the distance.
				// we raise the distance to 1 if it's less than one, or it becomes a
				// crazy multiplier we don't want/need.
				if(distance < 1D)
					distance = 1D;
				double knockbackMultiplier = 1D + (1D / distance);

				// we also need a reduction coefficient because the above force is
				// WAY TOO MUCH to apply every tick.
				double reductionCoefficient = 0.04D;

				// the resultant vector between the two 3d coordinates is the
				// difference of each coordinate pair
				// note that we do not add 0.5 to the y coord, if we wanted to be
				// SUPER accurate, we would be using
				// the entity height offset to find its "center of mass"
				Vec3d angleOfAttack = new Vec3d(entity.posX - (pos.getX() + 0.5D), entity.posY - pos.getY(), entity.posZ - (pos.getZ() + 0.5D));

				// we use the resultant vector to determine the force to apply.
				double xForce = angleOfAttack.x * knockbackMultiplier * reductionCoefficient;
				double yForce = angleOfAttack.y * knockbackMultiplier * reductionCoefficient;
				double zForce = angleOfAttack.z * knockbackMultiplier * reductionCoefficient;
				entity.motionX += xForce;
				entity.motionY += yForce;
				entity.motionZ += zForce;
			}
		}
	}

	private int tickRate() {
		return 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random) {
		double xOffset = (double) ((float) pos.getX() + 0.5F);
		double yOffset = (double) ((float) pos.getY() + 0.7F);
		double zOffset = (double) ((float) pos.getZ() + 0.5F);
		double verticalModifier = 0.22D;
		double horizontalModifier = 0.27D;

		EnumFacing facing = state.getValue(FACING);
		if(facing.getAxis().isHorizontal()) {
			EnumFacing oppositeFacing = facing.getOpposite();
			world.spawnParticle(EnumParticleTypes.SPELL_MOB, xOffset + horizontalModifier * (double) oppositeFacing.getFrontOffsetX(), yOffset + verticalModifier, zOffset + horizontalModifier * (double) oppositeFacing.getFrontOffsetZ(), 0.0D, 0.0D, 0.0D);
			world.spawnParticle(EnumParticleTypes.FLAME, xOffset + horizontalModifier * (double) oppositeFacing.getFrontOffsetX(), yOffset + verticalModifier, zOffset + horizontalModifier * (double) oppositeFacing.getFrontOffsetZ(), 0.0D, 0.0D, 0.0D);
		} else {
			world.spawnParticle(EnumParticleTypes.SPELL_MOB, xOffset, yOffset, zOffset, 0.0D, 0.0D, 0.0D);
			world.spawnParticle(EnumParticleTypes.FLAME, xOffset, yOffset, zOffset, 0.0D, 0.0D, 0.0D);
		}
	}
}
