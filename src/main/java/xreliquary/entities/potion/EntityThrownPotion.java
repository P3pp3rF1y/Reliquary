package xreliquary.entities.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.network.PacketFXThrownPotionImpact;
import xreliquary.network.PacketHandler;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

abstract class EntityThrownPotion extends EntityThrowable {
	EntityThrownPotion(World world) {
		super(world);
	}

	EntityThrownPotion(World world, EntityPlayer player) {
		super(world, player);
	}

	EntityThrownPotion(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	/**
	 * Gets the amount of gravity to apply to the thrown entity with each tick.
	 */
	@Override
	protected float getGravityVelocity() {
		return 0.05F;
	}

	/**
	 * Called when this EntityThrowable hits a block or entity.
	 */
	@Override
	protected void onImpact(@Nonnull RayTraceResult result) {
		if (!world.isRemote) {
			this.spawnParticles();
			this.doSplashEffect();
			this.setDead();
		}
	}

	// called by the splashEffect so that it can skip the bounding box and
	// entity iteration, just keeps things moving.
	abstract boolean hasLivingEntityEffect();

	private void doSplashEffect() {
		this.doGroundSplashEffect();
		if(!this.hasLivingEntityEffect())
			return;
		AxisAlignedBB bb = this.getEntityBoundingBox().expand(4.0D, 2.0D, 4.0D);
		List<EntityLivingBase> eList = world.getEntitiesWithinAABB(EntityLivingBase.class, bb);
		eList.forEach(this::doLivingSplashEffect);
	}

	// called by doSplash effect, allows user to override the ground effect,
	// since most of the potion don't have one.
	abstract void doGroundSplashEffect();

	// most of these are the same in every potion, the only thing that isn't is
	// the coloration of the particles.
	private void spawnParticles() {
		if(world.isRemote)
			return;

		Random rand = this.rand;
		for(int i = 0; i < 8; ++i) {
			world.spawnParticle(EnumParticleTypes.ITEM_CRACK, this.posX, this.posY, this.posZ, rand.nextGaussian() * 0.15D, rand.nextDouble() * 0.2D, rand.nextGaussian() * 0.15D, Item.getIdFromItem(Items.POTIONITEM));
		}

		world.playSound(null, getPosition(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);

		PacketHandler.networkWrapper.sendToAllAround(new PacketFXThrownPotionImpact(getColor(), this.posX, this.posY, this.posZ), new NetworkRegistry.TargetPoint(this.dimension, this.posX, this.posY, this.posZ, 32.0D));
	}

	// this gets called inside the on-impact method on EVERY living entity
	// within the AOE
	abstract void doLivingSplashEffect(EntityLivingBase e);

	// these are just the getters for the particle coloration. They're all the
	// same particle style, so it's really just a matter of coloration.
	abstract int getColor();
}
