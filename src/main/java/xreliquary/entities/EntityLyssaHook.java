package xreliquary.entities;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.init.ModItems;
import xreliquary.reference.Settings;

import javax.annotation.Nonnull;
import java.util.List;

public class EntityLyssaHook extends Entity implements IEntityAdditionalSpawnData {
	private static final DataParameter<Integer> DATA_HOOKED_ENTITY = EntityDataManager.createKey(EntityFishHook.class, DataSerializers.VARINT);
	private boolean inGround;
	private int ticksInGround = 0;
	private EntityPlayer angler;
	private int ticksInAir;
	private int ticksCatchable;
	private int ticksCaughtDelay;
	private int ticksCatchableDelay;
	private float fishApproachAngle;
	private Entity caughtEntity;
	private State currentState = State.FLYING;

	@SuppressWarnings("unused")
	@SideOnly(Side.CLIENT)
	public EntityLyssaHook(World world) {
		super(world);
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
	}

	public EntityLyssaHook(World worldIn, EntityPlayer fishingPlayer) {
		super(worldIn);
		this.init(fishingPlayer);
		this.shoot();

		//Reliquary
		speedUp();
	}

	private void init(EntityPlayer player) {
		this.setSize(0.25F, 0.25F);
		this.ignoreFrustumCheck = true;
		this.angler = player;
	}

	private void shoot() {
		float f = this.angler.prevRotationPitch + (this.angler.rotationPitch - this.angler.prevRotationPitch);
		float f1 = this.angler.prevRotationYaw + (this.angler.rotationYaw - this.angler.prevRotationYaw);
		float f2 = MathHelper.cos(-f1 * 0.017453292F - (float) Math.PI);
		float f3 = MathHelper.sin(-f1 * 0.017453292F - (float) Math.PI);
		float f4 = -MathHelper.cos(-f * 0.017453292F);
		float f5 = MathHelper.sin(-f * 0.017453292F);
		double d0 = this.angler.prevPosX + (this.angler.posX - this.angler.prevPosX) - (double) f3 * 0.3D;
		double d1 = this.angler.prevPosY + (this.angler.posY - this.angler.prevPosY) + (double) this.angler.getEyeHeight();
		double d2 = this.angler.prevPosZ + (this.angler.posZ - this.angler.prevPosZ) - (double) f2 * 0.3D;
		this.setLocationAndAngles(d0, d1, d2, f1, f);
		this.motionX = (double) (-f3);
		this.motionY = (double) MathHelper.clamp(-(f5 / f4), -5.0F, 5.0F);
		this.motionZ = (double) (-f2);
		float f6 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
		this.motionX *= 0.6D / (double) f6 + 0.5D + this.rand.nextGaussian() * 0.0045D;
		this.motionY *= 0.6D / (double) f6 + 0.5D + this.rand.nextGaussian() * 0.0045D;
		this.motionZ *= 0.6D / (double) f6 + 0.5D + this.rand.nextGaussian() * 0.0045D;
		float f7 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
		this.rotationYaw = (float) (MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));
		this.rotationPitch = (float) (MathHelper.atan2(this.motionY, (double) f7) * (180D / Math.PI));
		this.prevRotationYaw = this.rotationYaw;
		this.prevRotationPitch = this.rotationPitch;
	}

	@Override
	protected void entityInit() {
		this.getDataManager().register(DATA_HOOKED_ENTITY, 0);
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		if(DATA_HOOKED_ENTITY.equals(key)) {
			int i = this.getDataManager().get(DATA_HOOKED_ENTITY);
			this.caughtEntity = i > 0 ? this.world.getEntityByID(i - 1) : null;
		}

		super.notifyDataManagerChange(key);
	}

	/**
	 * Set the position and rotation values directly without any clamping.
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	private void onUpdateOriginal() {
		super.onUpdate();

		if(this.angler == null) {
			this.setDead();
		} else if(this.world.isRemote || !this.shouldStopFishing()) {
			if(this.inGround) {
				++this.ticksInGround;

				if(this.ticksInGround >= 1200) {
					this.setDead();
					return;
				}
			}

			float f = 0.0F;
			BlockPos blockpos = new BlockPos(this);
			IBlockState iblockstate = this.world.getBlockState(blockpos);

			if(iblockstate.getMaterial() == Material.WATER) {
				f = BlockLiquid.getBlockLiquidHeight(iblockstate, this.world, blockpos);
			}

			if(this.currentState == State.FLYING) {
				if(this.caughtEntity != null) {
					this.motionX = 0.0D;
					this.motionY = 0.0D;
					this.motionZ = 0.0D;
					this.currentState = State.HOOKED_IN_ENTITY;
					return;
				}

				if(f > 0.0F) {
					this.motionX *= 0.3D;
					this.motionY *= 0.2D;
					this.motionZ *= 0.3D;
					this.currentState = State.BOBBING;
					return;
				}

				if(!this.world.isRemote) {
					this.checkCollision();
				}

				if(!this.inGround && !this.onGround && !this.isCollidedHorizontally) {
					++this.ticksInAir;
				} else {
					this.ticksInAir = 0;
					this.motionX = 0.0D;
					this.motionY = 0.0D;
					this.motionZ = 0.0D;
				}
			} else {
				if(this.currentState == State.HOOKED_IN_ENTITY) {
					if(this.caughtEntity != null) {
						if(this.caughtEntity.isDead) {
							this.caughtEntity = null;
							this.currentState = State.FLYING;
						} else {
							this.posX = this.caughtEntity.posX;
							double d2 = (double) this.caughtEntity.height;
							this.posY = this.caughtEntity.getEntityBoundingBox().minY + d2 * 0.8D;
							this.posZ = this.caughtEntity.posZ;
							this.setPosition(this.posX, this.posY, this.posZ);
						}
					}

					return;
				}

				if(this.currentState == State.BOBBING) {
					this.motionX *= 0.9D;
					this.motionZ *= 0.9D;
					double d0 = this.posY + this.motionY - (double) blockpos.getY() - (double) f;

					if(Math.abs(d0) < 0.01D) {
						d0 += Math.signum(d0) * 0.1D;
					}

					this.motionY -= d0 * (double) this.rand.nextFloat() * 0.2D;

					if(!this.world.isRemote && f > 0.0F) {
						this.catchingFish(blockpos);
					}
				}
			}

			if(iblockstate.getMaterial() != Material.WATER) {
				this.motionY -= 0.03D;
			}

			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			this.updateRotation();
			this.motionX *= 0.92D;
			this.motionY *= 0.92D;
			this.motionZ *= 0.92D;
			this.setPosition(this.posX, this.posY, this.posZ);
		}
	}

	private void updateRotation() {
		float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
		this.rotationYaw = (float) (MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));

		//noinspection StatementWithEmptyBody
		for(this.rotationPitch = (float) (MathHelper.atan2(this.motionY, (double) f) * (180D / Math.PI)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
		}

		while(this.rotationPitch - this.prevRotationPitch >= 180.0F) {
			this.prevRotationPitch += 360.0F;
		}

		while(this.rotationYaw - this.prevRotationYaw < -180.0F) {
			this.prevRotationYaw -= 360.0F;
		}

		while(this.rotationYaw - this.prevRotationYaw >= 180.0F) {
			this.prevRotationYaw += 360.0F;
		}

		this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
		this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
	}

	private void checkCollision() {
		Vec3d vec3d = new Vec3d(this.posX, this.posY, this.posZ);
		Vec3d vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
		RayTraceResult raytraceresult = this.world.rayTraceBlocks(vec3d, vec3d1, false, true, false);
		vec3d = new Vec3d(this.posX, this.posY, this.posZ);
		vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

		if(raytraceresult != null) {
			vec3d1 = new Vec3d(raytraceresult.hitVec.xCoord, raytraceresult.hitVec.yCoord, raytraceresult.hitVec.zCoord);
		}

		Entity entity = null;
		List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expandXyz(1.0D));
		double d0 = 0.0D;

		for(Entity entity1 : list) {
			if(this.canBeHooked(entity1) && (entity1 != this.angler || this.ticksInAir >= 5)) {
				AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expandXyz(0.30000001192092896D);
				RayTraceResult raytraceresult1 = axisalignedbb.calculateIntercept(vec3d, vec3d1);

				if(raytraceresult1 != null) {
					double d1 = vec3d.squareDistanceTo(raytraceresult1.hitVec);

					if(d1 < d0 || d0 == 0.0D) {
						entity = entity1;
						d0 = d1;
					}
				}
			}
		}

		if(entity != null) {
			raytraceresult = new RayTraceResult(entity);
		}

		if(raytraceresult != null && raytraceresult.typeOfHit != RayTraceResult.Type.MISS) {
			if(raytraceresult.typeOfHit == RayTraceResult.Type.ENTITY) {
				this.caughtEntity = raytraceresult.entityHit;
				this.setHookedEntity();
			} else {
				this.inGround = true;
			}
		}
	}

	private void setHookedEntity() {
		this.getDataManager().set(DATA_HOOKED_ENTITY, this.caughtEntity.getEntityId() + 1);
	}

	private void catchingFish(BlockPos p_190621_1_) {
		WorldServer worldserver = (WorldServer) this.world;
		int i = 1;
		BlockPos blockpos = p_190621_1_.up();

		if(this.rand.nextFloat() < 0.25F && this.world.isRainingAt(blockpos)) {
			++i;
		}

		if(this.rand.nextFloat() < 0.5F && !this.world.canSeeSky(blockpos)) {
			--i;
		}

		if(this.ticksCatchable > 0) {
			--this.ticksCatchable;

			if(this.ticksCatchable <= 0) {
				this.ticksCaughtDelay = 0;
				this.ticksCatchableDelay = 0;
			} else {
				this.motionY -= 0.2D * (double) this.rand.nextFloat() * (double) this.rand.nextFloat();
			}
		} else if(this.ticksCatchableDelay > 0) {
			this.ticksCatchableDelay -= i;

			if(this.ticksCatchableDelay > 0) {
				this.fishApproachAngle = (float) ((double) this.fishApproachAngle + this.rand.nextGaussian() * 4.0D);
				float f = this.fishApproachAngle * 0.017453292F;
				float f1 = MathHelper.sin(f);
				float f2 = MathHelper.cos(f);
				double d0 = this.posX + (double) (f1 * (float) this.ticksCatchableDelay * 0.1F);
				double d1 = (double) ((float) MathHelper.floor(this.getEntityBoundingBox().minY) + 1.0F);
				double d2 = this.posZ + (double) (f2 * (float) this.ticksCatchableDelay * 0.1F);
				Block block = worldserver.getBlockState(new BlockPos(d0, d1 - 1.0D, d2)).getBlock();

				if(block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
					if(this.rand.nextFloat() < 0.15F) {
						worldserver.spawnParticle(EnumParticleTypes.WATER_BUBBLE, d0, d1 - 0.10000000149011612D, d2, 1, (double) f1, 0.1D, (double) f2, 0.0D);
					}

					float f3 = f1 * 0.04F;
					float f4 = f2 * 0.04F;
					worldserver.spawnParticle(EnumParticleTypes.WATER_WAKE, d0, d1, d2, 0, (double) f4, 0.01D, (double) (-f3), 1.0D);
					worldserver.spawnParticle(EnumParticleTypes.WATER_WAKE, d0, d1, d2, 0, (double) (-f4), 0.01D, (double) f3, 1.0D);
				}
			} else {
				this.motionY = (double) (-0.4F * MathHelper.nextFloat(this.rand, 0.6F, 1.0F));
				this.playSound(SoundEvents.ENTITY_BOBBER_SPLASH, 0.25F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
				double d3 = this.getEntityBoundingBox().minY + 0.5D;
				worldserver.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX, d3, this.posZ, (int) (1.0F + this.width * 20.0F), (double) this.width, 0.0D, (double) this.width, 0.20000000298023224D);
				worldserver.spawnParticle(EnumParticleTypes.WATER_WAKE, this.posX, d3, this.posZ, (int) (1.0F + this.width * 20.0F), (double) this.width, 0.0D, (double) this.width, 0.20000000298023224D);
				this.ticksCatchable = MathHelper.getInt(this.rand, 20, 40);
			}
		} else if(this.ticksCaughtDelay > 0) {
			this.ticksCaughtDelay -= i;
			float f5 = 0.15F;

			if(this.ticksCaughtDelay < 20) {
				f5 = (float) ((double) f5 + (double) (20 - this.ticksCaughtDelay) * 0.05D);
			} else if(this.ticksCaughtDelay < 40) {
				f5 = (float) ((double) f5 + (double) (40 - this.ticksCaughtDelay) * 0.02D);
			} else if(this.ticksCaughtDelay < 60) {
				f5 = (float) ((double) f5 + (double) (60 - this.ticksCaughtDelay) * 0.01D);
			}

			if(this.rand.nextFloat() < f5) {
				float f6 = MathHelper.nextFloat(this.rand, 0.0F, 360.0F) * 0.017453292F;
				float f7 = MathHelper.nextFloat(this.rand, 25.0F, 60.0F);
				double d4 = this.posX + (double) (MathHelper.sin(f6) * f7 * 0.1F);
				double d5 = (double) ((float) MathHelper.floor(this.getEntityBoundingBox().minY) + 1.0F);
				double d6 = this.posZ + (double) (MathHelper.cos(f6) * f7 * 0.1F);
				Block block1 = worldserver.getBlockState(new BlockPos((int) d4, (int) d5 - 1, (int) d6)).getBlock();

				if(block1 == Blocks.WATER || block1 == Blocks.FLOWING_WATER) {
					worldserver.spawnParticle(EnumParticleTypes.WATER_SPLASH, d4, d5, d6, 2 + this.rand.nextInt(2), 0.10000000149011612D, 0.0D, 0.10000000149011612D, 0.0D);
				}
			}

			if(this.ticksCaughtDelay <= 0) {
				this.fishApproachAngle = MathHelper.nextFloat(this.rand, 0.0F, 360.0F);
				this.ticksCatchableDelay = MathHelper.getInt(this.rand, 20, 80);
			}
		} else {
			this.ticksCaughtDelay = MathHelper.getInt(this.rand, 100, 600);
			this.ticksCaughtDelay -= EnchantmentHelper.getLureModifier(this.angler) * 20 * 5;
		}
	}

	private boolean canBeHooked(Entity entity) {
		return entity.canBeCollidedWith() || entity instanceof EntityItem;
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	@Override
	public void writeEntityToNBT(@Nonnull NBTTagCompound compound) {
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	public void readEntityFromNBT(@Nonnull NBTTagCompound compound) {
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte id) {
		if(id == 31 && this.world.isRemote && this.caughtEntity instanceof EntityPlayer && ((EntityPlayer) this.caughtEntity).isUser()) {
			this.bringInHookedEntity();
		}

		super.handleStatusUpdate(id);
	}

	/**
	 * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
	 * prevent them from trampling crops
	 */
	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	/**
	 * Will get destroyed next tick.
	 */
	@Override
	public void setDead() {
		super.setDead();

		if(this.angler != null) {
			this.angler.fishEntity = null;
		}
	}

	public EntityPlayer getAngler() {
		return this.angler;
	}



	private enum State {
		FLYING, HOOKED_IN_ENTITY, BOBBING
	}

	private void bringInHookedEntityOriginal() {
		if(this.angler != null) {
			double d0 = this.angler.posX - this.posX;
			double d1 = this.angler.posY - this.posY;
			double d2 = this.angler.posZ - this.posZ;
			this.caughtEntity.motionX += d0 * 0.1D;
			this.caughtEntity.motionY += d1 * 0.1D;
			this.caughtEntity.motionZ += d2 * 0.1D;
		}
	}

	private int handleHookRetractionOriginal() {
		if(!this.world.isRemote && this.angler != null) {
			int i = 0;

			if(this.caughtEntity != null) {
				this.bringInHookedEntity();
				this.world.setEntityState(this, (byte) 31);
				i = this.caughtEntity instanceof EntityItem ? 3 : 5;
			} else if(this.ticksCatchable > 0) {
				LootContext.Builder lootcontext$builder = new LootContext.Builder((WorldServer) this.world);
				lootcontext$builder.withLuck((float) EnchantmentHelper.getLuckOfSeaModifier(this.angler) + this.angler.getLuck());

				for(ItemStack itemstack : this.world.getLootTableManager().getLootTableFromLocation(LootTableList.GAMEPLAY_FISHING).generateLootForPools(this.rand, lootcontext$builder.build())) {
					EntityItem entityitem = new EntityItem(this.world, this.posX, this.posY, this.posZ, itemstack);
					double d0 = this.angler.posX - this.posX;
					double d1 = this.angler.posY - this.posY;
					double d2 = this.angler.posZ - this.posZ;
					double d3 = (double) MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
					entityitem.motionX = d0 * 0.1D;
					entityitem.motionY = d1 * 0.1D + (double) MathHelper.sqrt(d3) * 0.08D;
					entityitem.motionZ = d2 * 0.1D;
					this.world.spawnEntity(entityitem);
					this.angler.world.spawnEntity(new EntityXPOrb(this.angler.world, this.angler.posX, this.angler.posY + 0.5D, this.angler.posZ + 0.5D, this.rand.nextInt(6) + 1));
				}

				i = 1;
			}

			if(this.inGround) {
				i = 2;
			}

			this.setDead();
			return i;
		} else {
			return 0;
		}
	}

	/*
		Reliquary customizations to the default EntityFishHook behavior
	*/

	private void speedUp() {
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
		onUpdateOriginal();

		pullItemEntitiesTowardsHook();
	}

	private boolean shouldStopFishing() {
		ItemStack itemstack = this.getAngler().getHeldItemMainhand();
		ItemStack itemstack1 = this.getAngler().getHeldItemOffhand();
		boolean flag = itemstack.getItem() == ModItems.rodOfLyssa;
		boolean flag1 = itemstack1.getItem() == ModItems.rodOfLyssa;

		if(!this.getAngler().isDead && this.getAngler().isEntityAlive() && (flag || flag1) && this.getDistanceSqToEntity(this.getAngler()) <= 1024.0D) {
			return false;
		} else {
			this.setDead();
			return true;
		}
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

	private void bringInHookedEntity() {
		bringInHookedEntityOriginal();

		if(this.caughtEntity instanceof EntityItem) {
			this.caughtEntity.motionX *= 4D;
			this.caughtEntity.motionY *= 4D;
			this.caughtEntity.motionZ *= 4D;
		} else if (this.caughtEntity instanceof EntityLivingBase) {
			this.caughtEntity.motionY *= 1.5D;
		}
	}

	public int handleHookRetraction() {
		if(!this.world.isRemote) {
			if(this.caughtEntity != null && this.getAngler().isSneaking() && this.caughtEntity instanceof EntityLivingBase) {
				stealFromLivingEntity();
				this.setDead();
			} else {
				handleHookRetractionOriginal();
			}

			pullItemEntitiesWithHook();
		}

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
			if(stolenStack.isEmpty() && Settings.RodOfLyssa.stealFromVacantSlots) {
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

				livingBase.setItemStackToSlot(slotBeingStolenFrom, ItemStack.EMPTY);
			}
		}
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeInt(angler.getEntityId());
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		Entity entity = world.getEntityByID(additionalData.readInt());

		if (entity instanceof EntityPlayer) {
			angler = (EntityPlayer) entity;
		}
	}
}
