package xreliquary.entities;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import xreliquary.reference.Settings;

import java.util.List;

public class EntityLyssaHook extends EntityFishHook {

	public EntityLyssaHook(World worldIn, EntityPlayer fishingPlayer) {
		super(worldIn, fishingPlayer);

		//faster speed of the hook except for casting down
		if(motionY >= 0) {
			motionX *= 2;
			motionY *= 2;
			motionZ *= 2;
		}
	}

	@Override
	public boolean isInRangeToRenderDist(double distance) {
		//much higher visible range than regular hook
		return distance < 16384;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		pullItemEntitiesTowardsHook();
	}

	private void pullItemEntitiesTowardsHook() {
		if(!this.isDead && this.caughtEntity == null) {
			float f = 0.0F;
			BlockPos blockpos = new BlockPos(this);
			IBlockState iblockstate = this.world.getBlockState(blockpos);

			if(iblockstate.getMaterial() == Material.WATER) {
				f = BlockLiquid.getBlockLiquidHeight(iblockstate, this.world, blockpos);
			}

			if(f <= 0F) {
				List<Entity> list = this.world.getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expandXyz(3.0D));

				for(Entity e : list) {
					Vec3d pullVector = new Vec3d(this.posX - e.posX, this.posY - e.posY, this.posZ - e.posZ).normalize();
					e.motionX = pullVector.xCoord * 0.4D;
					e.motionY = pullVector.yCoord * 0.4D;
					e.motionZ = pullVector.zCoord * 0.4D;
				}
			}
		}
	}

	@Override
	protected void bringInHookedEntity() {
		super.bringInHookedEntity();

		if(this.caughtEntity instanceof EntityItem) {
			this.caughtEntity.motionX *= 4;
			this.caughtEntity.motionY *= 4;
			this.caughtEntity.motionZ *= 4;
		}
	}

	@Override
	public int handleHookRetraction() {
		if(!this.world.isRemote) {
			if(this.caughtEntity != null && this.getAngler().isSneaking() && this.caughtEntity instanceof EntityLivingBase) {
				stealFromLivingEntity();
			}
		} else {
			super.handleHookRetraction();
		}

		pullItemEntitiesWithHook();

		return 0;
	}

	private void pullItemEntitiesWithHook() {
		List<EntityItem> pullingItemsList = this.world.getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));

		for(EntityItem e : pullingItemsList) {
			double d1 = this.getAngler().posX - this.posX;
			double d3 = this.getAngler().posY - this.posY;
			double d5 = this.getAngler().posZ - this.posZ;
			double d7 = (double) MathHelper.sqrt(d1 * d1 + d3 * d3 + d5 * d5);
			double d9 = 0.1D;
			e.motionX = d1 * d9;
			e.motionY = d3 * d9 + (double) MathHelper.sqrt(d7) * 0.08D;
			e.motionZ = d5 * d9;
		}
	}

	private void stealFromLivingEntity() {
		EntityLivingBase livingBase = (EntityLivingBase) this.caughtEntity;
		if(!(livingBase instanceof EntityPlayer)) {
			EntityEquipmentSlot slotBeingStolenFrom = EntityEquipmentSlot.values()[world.rand.nextInt(EntityEquipmentSlot.values().length)];

			ItemStack stolenStack = livingBase.getItemStackFromSlot(slotBeingStolenFrom);
			if(stolenStack.isEmpty() && !Settings.RodOfLyssa.stealFromVacantSlots) {
				for(EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
					stolenStack = livingBase.getItemStackFromSlot(slot);
					if(!stolenStack.isEmpty()) {
						slotBeingStolenFrom = slot;
						break;
					}
				}
			}

			float failProbabilityFactor;

			if(Settings.RodOfLyssa.useLeveledFailureRate)
				failProbabilityFactor = 1F / ((float) Math.sqrt((double) Math.max(1, Math.min(getAngler().experienceLevel, Settings.RodOfLyssa.levelCapForLeveledFormula))) * 2);
			else
				failProbabilityFactor = Settings.RodOfLyssa.flatStealFailurePercentRate / 100F;

			if(rand.nextFloat() <= failProbabilityFactor || (stolenStack.isEmpty() && Settings.RodOfLyssa.failStealFromVacantSlots)) {
				if(Settings.RodOfLyssa.angerOnStealFailure)
					livingBase.attackEntityFrom(DamageSource.causePlayerDamage(this.getAngler()), 0.0F);
			}
			if(!stolenStack.isEmpty()) {
				int randomItemDamage = world.rand.nextInt(3);
				stolenStack.damageItem(randomItemDamage, livingBase);
				EntityItem entityitem = new EntityItem(this.world, this.posX, this.posY, this.posZ, stolenStack);
				double d1 = this.getAngler().posX - this.posX;
				double d3 = this.getAngler().posY - this.posY;
				double d5 = this.getAngler().posZ - this.posZ;
				double d7 = (double) MathHelper.sqrt(d1 * d1 + d3 * d3 + d5 * d5);
				double d9 = 0.1D;
				entityitem.motionX = d1 * d9;
				entityitem.motionY = d3 * d9 + (double) MathHelper.sqrt(d7) * 0.08D;
				entityitem.motionZ = d5 * d9;
				this.world.spawnEntity(entityitem);

				livingBase.setItemStackToSlot(slotBeingStolenFrom, null);

			}
		}
	}
}
