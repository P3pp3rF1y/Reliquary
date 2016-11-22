package xreliquary.entities;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.init.ModItems;
import xreliquary.network.PacketFXThrownPotionImpact;
import xreliquary.network.PacketHandler;
import xreliquary.reference.Colors;

import javax.annotation.Nonnull;
import java.util.List;

public class EntityGlowingWater extends EntityThrowable {
	@SuppressWarnings("unused")
	public EntityGlowingWater(World par1World) {
		super(par1World);
	}

	public EntityGlowingWater(World par1World, EntityPlayer par2EntityPlayer) {
		super(par1World, par2EntityPlayer);
	}

	@SideOnly(Side.CLIENT)
	@SuppressWarnings("unused")
	public EntityGlowingWater(World par1World, double par2, double par4, double par6, int par8) {
		this(par1World, par2, par4, par6);
	}

	public EntityGlowingWater(World par1World, double par2, double par4, double par6) {
		super(par1World, par2, par4, par6);
	}

	/**
	 * Gets the amount of gravity to apply to the thrown entity with each tick.
	 */
	@Override
	protected float getGravityVelocity() {
		return 0.03F;
	}

	private boolean isUndead(EntityLivingBase e) {
		return e.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD;
	}

	/**
	 * Called when this EntityThrowable hits a block or entity.
	 */
	@Override
	protected void onImpact(@Nonnull RayTraceResult result) {
		if(!world.isRemote) {
			this.spawnParticles();
			AxisAlignedBB bb = this.getEntityBoundingBox().expand(4.0D, 2.0D, 4.0D);
			List<EntityLiving> eList = world.getEntitiesWithinAABB(EntityLiving.class, bb);
			eList.stream().filter(this::isUndead).forEach(e -> {
				float amount = 18 + rand.nextInt(17);
				if(this.getThrower() != null && this.getThrower() instanceof EntityPlayer) {
					e.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) this.getThrower()), amount);
				} else {
					e.attackEntityFrom(DamageSource.MAGIC, amount);
				}
			});

			world.playEvent(2002, new BlockPos(this), 0);
			this.setDead();
		}
	}

	private void spawnParticles() {
		double x = posX;
		double y = posY;
		double z = posZ;

		for(int particleNum = 0; particleNum < 8; ++particleNum) {
			world.spawnParticle(EnumParticleTypes.ITEM_CRACK, x, y, z, rand.nextGaussian() * 0.15D, rand.nextDouble() * 0.2D, rand.nextGaussian() * 0.15D, Item.getIdFromItem(ModItems.glowingWater));
		}

		world.playSound(null, getPosition(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.NEUTRAL, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
		PacketHandler.networkWrapper.sendToAllAround(new PacketFXThrownPotionImpact(Colors.get(Colors.BLUE), this.posX, this.posY, this.posZ), new NetworkRegistry.TargetPoint(this.dimension, this.posX, this.posY, this.posZ, 32.0D));

	}
}
