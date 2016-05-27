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

import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by Xeno on 3/11/14.
 */
public abstract class EntityThrownPotion extends EntityThrowable {
	public EntityThrownPotion(World par1World) {
		super(par1World);
	}

	public EntityThrownPotion(World par1World, EntityPlayer par2EntityPlayer) {
		super(par1World, par2EntityPlayer);
	}

	@SideOnly(Side.CLIENT)
	public EntityThrownPotion(World par1World, double par2, double par4, double par6, int par8) {
		this(par1World, par2, par4, par6);
	}

	public EntityThrownPotion(World par1World, double par2, double par4, double par6) {
		super(par1World, par2, par4, par6);
	}

	/**
	 * Gets the amount of gravity to apply to the thrown entity with each tick.
	 */
	@Override
	protected float getGravityVelocity() {
		return 0.07F;
	}

	/**
	 * Called when this EntityThrowable hits a block or entity.
	 */
	@Override
	protected void onImpact(RayTraceResult result) {
		this.spawnParticles();
		this.doSplashEffect();
		this.setDead();
	}

	// called by the splashEffect so that it can skip the bounding box and
	// entity iteration, just keeps things moving.
	abstract boolean hasLivingEntityEffect();

	protected void doSplashEffect() {
		this.doGroundSplashEffect();
		if(!this.hasLivingEntityEffect())
			return;
		AxisAlignedBB bb = this.getEntityBoundingBox().expand(4.0D, 2.0D, 4.0D);
		List eList = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, bb);
		Iterator i = eList.iterator();
		while(i.hasNext()) {
			EntityLivingBase e = (EntityLivingBase) i.next();
			this.doLivingSplashEffect(e);
		}
	}

	// called by doSplash effect, allows user to override the ground effect,
	// since most of the potion don't have one.
	abstract void doGroundSplashEffect();

	// most of these are the same in every potion, the only thing that isn't is
	// the coloration of the particles.
	protected void spawnParticles() {
		if(worldObj.isRemote)
			return;

		Random rand = this.rand;
		for(int i = 0; i < 8; ++i) {
			worldObj.spawnParticle(EnumParticleTypes.ITEM_CRACK, this.posX, this.posY, this.posZ, rand.nextGaussian() * 0.15D, rand.nextDouble() * 0.2D, rand.nextGaussian() * 0.15D, Item.getIdFromItem(Items.POTIONITEM));
		}

		worldObj.playSound(null, getPosition(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 1.0F, worldObj.rand.nextFloat() * 0.1F + 0.9F);

		PacketHandler.networkWrapper.sendToAllAround(new PacketFXThrownPotionImpact(getColor(), this.posX, this.posY, this.posZ), new NetworkRegistry.TargetPoint(this.dimension, this.posX, this.posY, this.posZ, 32.0D));
	}

	// this gets called inside the on-impact method on EVERY living entity
	// within the AOE
	abstract void doLivingSplashEffect(EntityLivingBase e);

	// these are just the getters for the particle coloration. They're all the
	// same particle style, so it's really just a matter of coloration.
	abstract int getColor();
}
