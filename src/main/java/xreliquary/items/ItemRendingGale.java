package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;
import xreliquary.Reliquary;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Xeno on 10/11/2014.
 */
public class ItemRendingGale extends ItemToggleable {
	public ItemRendingGale() {
		super(Names.rending_gale);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxStackSize(1);
		canRepair = false;
	}

	@Override
	public void addInformation(ItemStack ist, EntityPlayer player, List list, boolean par4) {
		if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
			return;

		this.formatTooltip(ImmutableMap.of("charge", Integer.toString(NBTHelper.getInteger("feathers", ist))), ist, list);

		if(this.isEnabled(ist))
			LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", TextFormatting.WHITE + Items.feather.getItemStackDisplayName(new ItemStack(Items.feather))), ist, list);
		LanguageHelper.formatTooltip("tooltip.absorb", null, ist, list);
	}

	private static int getChargeLimit() {
		return Settings.RendingGale.chargeLimit;
	}

	public static int getChargeCost() {
		return Settings.RendingGale.castChargeCost;
	}

	private static int getFeathersWorth() {
		return Settings.RendingGale.chargeFeatherWorth;
	}

	private static int getBoltChargeCost() {
		return Settings.RendingGale.boltChargeCost;
	}

	private static int getBoltTargetRange() {
		return Settings.RendingGale.blockTargetRange;
	}

	private static int getRadialPushRadius() {
		return Settings.RendingGale.pushPullRadius;
	}

	private static boolean canPushProjectiles() {
		return Settings.RendingGale.canPushProjectiles;
	}

	public void attemptFlight(EntityLivingBase entityLiving) {
		if(!(entityLiving instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) entityLiving;

		Vec3d lookVec = player.getLook(0.66F);

		double x = lookVec.xCoord;
		double y = lookVec.yCoord;
		double z = lookVec.zCoord;

		//you're gonna clip into something, we're trying to prevent that.
		if(isAABBInAnythingButAir(player.worldObj, player.getEntityBoundingBox().offset(x, y, z))) {
			if(Math.abs(x) > Math.abs(y) && Math.abs(x) > Math.abs(z)) {
				if(!isAABBInAnythingButAir(player.worldObj, player.getEntityBoundingBox().offset(x, 0D, 0D))) {
					// x is fine
					if(Math.abs(z) > Math.abs(y)) {
						if(!isAABBInAnythingButAir(player.worldObj, player.getEntityBoundingBox().offset(0D, 0D, z))) {
							//z is fine
							y = 0D;
						} else if(!isAABBInAnythingButAir(player.worldObj, player.getEntityBoundingBox().offset(0D, y, 0D)))
							z = 0D;
					} else {
						if(!isAABBInAnythingButAir(player.worldObj, player.getEntityBoundingBox().offset(0D, y, 0D))) {
							//y is fine
							z = 0D;
						} else if(!isAABBInAnythingButAir(player.worldObj, player.getEntityBoundingBox().offset(0D, 0D, z)))
							y = 0D;
					}
				} else {
					//x is not fine
					x = 0D;
					//and also do the standard y/z checks
					if(Math.abs(z) > Math.abs(y)) {
						if(!isAABBInAnythingButAir(player.worldObj, player.getEntityBoundingBox().offset(0D, 0D, z))) {
							//z is fine
							y = 0D;
						} else if(!isAABBInAnythingButAir(player.worldObj, player.getEntityBoundingBox().offset(0D, y, 0D)))
							z = 0D;
					} else {
						if(!isAABBInAnythingButAir(player.worldObj, player.getEntityBoundingBox().offset(0D, y, 0D))) {
							//y is fine
							z = 0D;
						} else if(!isAABBInAnythingButAir(player.worldObj, player.getEntityBoundingBox().offset(0D, 0D, z)))
							y = 0D;
					}
				}
			} else if(Math.abs(z) > Math.abs(x) && Math.abs(z) > Math.abs(y)) {
				if(!isAABBInAnythingButAir(player.worldObj, player.getEntityBoundingBox().offset(0D, 0D, z))) {
					//z is fine
					if(Math.abs(x) > Math.abs(y)) {
						if(!isAABBInAnythingButAir(player.worldObj, player.getEntityBoundingBox().offset(x, 0D, 0D))) {
							//x is fine
							y = 0D;
						} else if(!isAABBInAnythingButAir(player.worldObj, player.getEntityBoundingBox().offset(0D, y, 0D)))
							x = 0D;
					} else {
						if(!isAABBInAnythingButAir(player.worldObj, player.getEntityBoundingBox().offset(0D, y, 0D))) {
							//y is fine
							x = 0D;
						} else if(!isAABBInAnythingButAir(player.worldObj, player.getEntityBoundingBox().offset(x, 0D, 0D)))
							y = 0D;
					}
				} else {
					//z is not fine
					z = 0D;
					if(Math.abs(x) > Math.abs(y)) {
						if(!isAABBInAnythingButAir(player.worldObj, player.getEntityBoundingBox().offset(x, 0D, 0D))) {
							//x is fine
							y = 0D;
						} else if(!isAABBInAnythingButAir(player.worldObj, player.getEntityBoundingBox().offset(0D, y, 0D)))
							x = 0D;
					} else {
						if(!isAABBInAnythingButAir(player.worldObj, player.getEntityBoundingBox().offset(0D, y, 0D))) {
							//y is fine
							x = 0D;
						} else if(!isAABBInAnythingButAir(player.worldObj, player.getEntityBoundingBox().offset(x, 0D, 0D)))
							y = 0D;
					}
				}
			} else if(Math.abs(y) > Math.abs(x) && Math.abs(y) > Math.abs(z)) {
				if(!isAABBInAnythingButAir(player.worldObj, player.getEntityBoundingBox().offset(0D, y, 0D))) {
					//y is fine
					if(Math.abs(x) > Math.abs(z)) {
						if(!isAABBInAnythingButAir(player.worldObj, player.getEntityBoundingBox().offset(x, 0D, 0D))) {
							//x is fine
							z = 0D;
						} else if(!isAABBInAnythingButAir(player.worldObj, player.getEntityBoundingBox().offset(0D, 0D, z)))
							x = 0D;
					} else {
						if(!isAABBInAnythingButAir(player.worldObj, player.getEntityBoundingBox().offset(x, 0D, 0D))) {
							//x is fine
							z = 0D;
						} else if(!isAABBInAnythingButAir(player.worldObj, player.getEntityBoundingBox().offset(0D, 0D, z)))
							x = 0D;
					}
				} else {
					//y is not fine
					y = 0D;
					if(Math.abs(x) > Math.abs(z)) {
						if(!isAABBInAnythingButAir(player.worldObj, player.getEntityBoundingBox().offset(x, 0D, 0D))) {
							//x is fine
							z = 0D;
						} else if(!isAABBInAnythingButAir(player.worldObj, player.getEntityBoundingBox().offset(0D, 0D, z)))
							x = 0D;
					} else {
						if(!isAABBInAnythingButAir(player.worldObj, player.getEntityBoundingBox().offset(0D, 0D, z))) {
							//x is fine
							x = 0D;
						} else if(!isAABBInAnythingButAir(player.worldObj, player.getEntityBoundingBox().offset(x, 0D, 0D)))
							z = 0D;
					}
				}
			}
			if(isAABBInAnythingButAir(player.worldObj, player.getEntityBoundingBox().offset(x, y, z))) {
				//we still failed, give up.
				return;
			}

		}

		//player.setVelocity(x, y, z);
		player.motionX = x;
		player.motionY = y;
		player.motionZ = z;

		player.setPosition(player.posX + x, player.posY + y, player.posZ + z);

		player.fallDistance = 0.0F;

		return;
	}

	public boolean isAABBInAnythingButAir(World worldObj, AxisAlignedBB aabb) {
		int minX = MathHelper.floor_double(aabb.minX);
		int maxX = MathHelper.floor_double(aabb.maxX + 1.0D);
		int minY = MathHelper.floor_double(aabb.minY);
		int maxY = MathHelper.floor_double(aabb.maxY + 1.0D);
		int minZ = MathHelper.floor_double(aabb.minZ);
		int maxZ = MathHelper.floor_double(aabb.maxZ + 1.0D);

		for(int xOff = minX; xOff < maxX; ++xOff) {
			for(int yOff = minY; yOff < maxY; ++yOff) {
				for(int zOff = minZ; zOff < maxZ; ++zOff) {
					IBlockState blockState = worldObj.getBlockState(new BlockPos(xOff, yOff, zOff));
					Block block = blockState.getBlock();

					if(block.getMaterial(blockState) != Material.air && block.getMaterial(blockState) != Material.water && block.getMaterial(blockState) != Material.lava &&
							block.getMaterial(blockState) != Material.fire && block.getMaterial(blockState) != Material.vine && block.getMaterial(blockState) != Material.plants && block.getMaterial(blockState) != Material.circuits && block != Blocks.snow_layer) {
						return true;
					}
				}
			}
		}

		return false;
	}

	@Override
	public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean b) {
		if(world.isRemote || !(e instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) e;

		if(this.isEnabled(ist)) {
			if(NBTHelper.getInteger("feathers", ist) + getFeathersWorth() <= getChargeLimit()) {
				if(InventoryHelper.consumeItem(new ItemStack(Items.feather), player)) {
					NBTHelper.setInteger("feathers", ist, NBTHelper.getInteger("feathers", ist) + getFeathersWorth());
				}
			}
		}
	}

	public String getMode(ItemStack ist) {
		if(NBTHelper.getString("mode", ist).equals("")) {
			setMode(ist, "flight");
		}
		return NBTHelper.getString("mode", ist);
	}

	public void setMode(ItemStack ist, String s) {
		NBTHelper.setString("mode", ist, s);
	}

	public void cycleMode(ItemStack ist, boolean isRaining) {
		if(getMode(ist).equals("flight"))
			setMode(ist, "push");
		else if(getMode(ist).equals("push"))
			setMode(ist, "pull");
		else if(getMode(ist).equals("pull") && isRaining)
			setMode(ist, "bolt");
		else
			setMode(ist, "flight");
	}

	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack ist) {
		if(entityLiving.worldObj.isRemote)
			return false;
		if(!(entityLiving instanceof EntityPlayer))
			return false;
		EntityPlayer player = (EntityPlayer) entityLiving;
		if(player.isSneaking()) {
			cycleMode(ist, player.worldObj.isRaining());
			return true;
		}
		return false;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return 64;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack ist) {
		return EnumAction.BLOCK;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack ist, World world, EntityPlayer player, EnumHand hand) {
		if(player.isSneaking())
			super.onItemRightClick(ist, world, player, hand);
		else
			player.setActiveHand(hand);
		return new ActionResult<>(EnumActionResult.SUCCESS, ist);
	}

	//a longer ranged version of "getMovingObjectPositionFromPlayer" basically
	public RayTraceResult getCycloneBlockTarget(World world, EntityPlayer player) {
		float f = 1.0F;
		float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
		float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
		double d0 = player.prevPosX + (player.posX - player.prevPosX) * (double) f;
		double d1 = player.prevPosY + (player.posY - player.prevPosY) * (double) f + (double) (world.isRemote ? player.getEyeHeight() - player.getDefaultEyeHeight() : player.getEyeHeight()); // isRemote check to revert changes to ray trace position due to adding the eye height clientside and player yOffset differences
		double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) f;
		Vec3d vec3 = new Vec3d(d0, d1, d2);
		float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
		float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
		float f5 = -MathHelper.cos(-f1 * 0.017453292F);
		float f6 = MathHelper.sin(-f1 * 0.017453292F);
		float f7 = f4 * f5;
		float f8 = f3 * f5;
		double d3 = (double) getBoltTargetRange();
		Vec3d vec31 = vec3.addVector((double) f7 * d3, (double) f6 * d3, (double) f8 * d3);
		return world.rayTraceBlocks(vec3, vec31, true, false, false);
	}

	@Override
	public void onUsingTick(ItemStack ist, EntityLivingBase entity, int count) {
		if(!(entity instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) entity;

		if(NBTHelper.getInteger("feathers", ist) < getChargeCost() && !player.capabilities.isCreativeMode)
			return;
		count -= 1;
		count = getMaxItemUseDuration(ist) - count;
		if(count == getMaxItemUseDuration(ist) || ((getMaxItemUseDuration(ist) - count) * getChargeCost() >= NBTHelper.getInteger("feathers", ist) && !player.capabilities.isCreativeMode)) {
			int chargeUsed = count * getChargeCost();
			if(!player.capabilities.isCreativeMode)
				NBTHelper.setInteger("feathers", ist, NBTHelper.getInteger("feathers", ist) - chargeUsed);
			player.stopActiveHand();
		}

		if(getMode(ist).equals("flight")) {
			attemptFlight(player);
			spawnFlightParticles(player.worldObj, player.posX, player.posY + player.getEyeHeight(), player.posZ, player.getLookVec());
		} else if(getMode(ist).equals("push")) {
			doRadialPush(player.worldObj, player.posX, player.posY, player.posZ, player, false);
		} else if(getMode(ist).equals("pull")) {
			doRadialPush(player.worldObj, player.posX, player.posY, player.posZ, player, true);
			//doPushEffect(player, player.worldObj, player.posX, player.posY + player.getEyeHeight(), player.posZ, player.getLookVec());
			//spawnFlightParticles(player.worldObj, player.posX, player.posY + player.getEyeHeight(), player.posZ, player.getLookVec());

		} else if(getMode(ist).equals("bolt")) {

			RayTraceResult mop = this.getCycloneBlockTarget(player.worldObj, player);

			if(mop != null) {
				if(count % 8 == 0) {
					int attemptedY = mop.getBlockPos().getY();
					if(!player.worldObj.isRainingAt(mop.getBlockPos())) {
						attemptedY++;
					}
					if(player.worldObj.isRainingAt(new BlockPos(mop.getBlockPos().getX(), attemptedY, mop.getBlockPos().getZ()))) {
						if(NBTHelper.getInteger("feathers", ist) >= getBoltChargeCost() || player.capabilities.isCreativeMode) {
							if(!player.capabilities.isCreativeMode)
								NBTHelper.setInteger("feathers", ist, NBTHelper.getInteger("feathers", ist) - getBoltChargeCost());
							player.worldObj.addWeatherEffect(new EntityLightningBolt(player.worldObj, (double) mop.getBlockPos().getX(), (double) mop.getBlockPos().getY(), (double) mop.getBlockPos().getZ(), false));
						}
					}
				}
			}
		}
	}

	//experimenting with a more sophisticated charge/drain mechanism
	@Override
	public void onPlayerStoppedUsing(ItemStack ist, World world, EntityLivingBase entity, int count) {
		if(world.isRemote || !(entity instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) entity;
		//count starts at 64 instead of 63, so it needs to account for its first used tick.
		count -= 1;
		int chargeUsed = (getMaxItemUseDuration(ist) - count) * getChargeCost();
		if(!player.capabilities.isCreativeMode)
			NBTHelper.setInteger("feathers", ist, NBTHelper.getInteger("feathers", ist) - Math.min(chargeUsed, NBTHelper.getInteger("feathers", ist)));
	}

	public void doRadialPush(World worldObj, double posX, double posY, double posZ, EntityPlayer player, boolean pull) {
		//push effect free at the moment, if you restore cost, remember to change this to NBTHelper.getInteger("feathers", ist)
		spawnRadialHurricaneParticles(worldObj, posX, posY, posZ, player, pull);
		if(worldObj.isRemote)
			return;

		double lowerX = posX - getRadialPushRadius();
		double lowerY = posY - (double) getRadialPushRadius() / 5D;
		double lowerZ = posZ - getRadialPushRadius();
		double upperX = posX + getRadialPushRadius();
		double upperY = posY + (double) getRadialPushRadius() / 2D;
		double upperZ = posZ + getRadialPushRadius();

		List<String> entitiesThatCanBePushed = Settings.RendingGale.entitiesThatCanBePushed;
		List<String> projectilesThatCanBePushed = Settings.RendingGale.projectilesThatCanBePushed;

		List eList = worldObj.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(lowerX, lowerY, lowerZ, upperX, upperY, upperZ));

		Iterator iterator = eList.iterator();
		while(iterator.hasNext()) {
			Entity e = (Entity) iterator.next();
			Class entityClass = e.getClass();
			String entityName = EntityList.classToStringMapping.get(entityClass);
			if(entitiesThatCanBePushed.contains(entityName) || (!pull && canPushProjectiles() && projectilesThatCanBePushed.contains(entityName))) {
				double distance = getDistanceToEntity(posX, posY, posZ, e);
				if(distance >= getRadialPushRadius())
					continue;

				if(e.equals(player))
					continue;
				Vec3d pushVector;
				if(pull) {
					pushVector = new Vec3d(posX - e.posX, posY - e.posY, posZ - e.posZ);
				} else {
					pushVector = new Vec3d(e.posX - posX, e.posY - posY, e.posZ - posZ);
				}
				pushVector = pushVector.normalize();
				e.moveEntity(0.0D, 0.2D, 0.0D);
				e.moveEntity(pushVector.xCoord, Math.min(pushVector.yCoord, 0.1D) * 1.5D, pushVector.zCoord);
			}
		}
	}

	private float getDistanceToEntity(double posX, double posY, double posZ, Entity entityIn) {
		float f = (float) (posX - entityIn.posX);
		float f1 = (float) (posY - entityIn.posY);
		float f2 = (float) (posZ - entityIn.posZ);
		return MathHelper.sqrt_float(f * f + f1 * f1 + f2 * f2);
	}

	public void spawnFlightParticles(World world, double x, double y, double z, Vec3d lookVector) {
		//spawn a whole mess of particles every tick.
		for(int i = 0; i < 8; ++i) {
			float randX = 10F * (itemRand.nextFloat() - 0.5F);
			float randY = 10F * (itemRand.nextFloat() - 0.5F);
			float randZ = 10F * (itemRand.nextFloat() - 0.5F);

			world.spawnParticle(EnumParticleTypes.BLOCK_DUST, x + randX, y + randY, z + randZ, lookVector.xCoord * 5, lookVector.yCoord * 5, lookVector.zCoord * 5, Block.getStateId(Blocks.snow_layer.getStateFromMeta(0)));
		}
	}

	public void spawnRadialHurricaneParticles(World worldObj, double posX, double posY, double posZ, EntityPlayer player, boolean pull) {
		//spawn a whole mess of particles every tick.
		for(int i = 0; i < 3; ++i) {
			float randX = worldObj.rand.nextFloat() - 0.5F;
			float randZ = worldObj.rand.nextFloat() - 0.5F;
			float motX = randX * 10F;
			float motZ = randZ * 10F;
			if(pull) {
				randX *= 10F;
				randZ *= 10F;
				motX *= -1F;
				motZ *= -1F;
			}

			double posYAdjusted = player == null ? posY : (posY + player.getEyeHeight()) - (player.height / 2);

			worldObj.spawnParticle(EnumParticleTypes.BLOCK_DUST, posX + randX, posYAdjusted, posZ + randZ, motX, 0.0D, motZ, Block.getStateId(Blocks.snow_layer.getStateFromMeta(0)));
		}
	}
}
