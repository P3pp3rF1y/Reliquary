package xreliquary.entities;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.init.ModItems;
import xreliquary.reference.Settings;

import java.util.Iterator;
import java.util.List;

public class EntityLyssaHook extends EntityFishHook {
	private static final DataParameter<Integer> field_184528_c = EntityDataManager.<Integer>createKey(EntityFishHook.class, DataSerializers.VARINT);
	private int xTile;
	private int yTile;
	private int zTile;
	private Block inTile;
	private boolean inGround;
	private int ticksInGround;
	private int ticksInAir;
	private int ticksCatchable;
	private int ticksCaughtDelay;
	private int ticksCatchableDelay;
	private float fishApproachAngle;
	private Entity caughtEntity;
	private int fishPosRotationIncrements;
	private double fishX;
	private double fishY;
	private double fishZ;
	private double fishYaw;
	private double fishPitch;
	@SideOnly(Side.CLIENT)
	private double clientMotionX;
	@SideOnly(Side.CLIENT)
	private double clientMotionY;
	@SideOnly(Side.CLIENT)
	private double clientMotionZ;

	public EntityLyssaHook(World worldIn, EntityPlayer fishingPlayer) {
		super(worldIn);
		this.xTile = -1;
		this.yTile = -1;
		this.zTile = -1;
		this.ignoreFrustumCheck = true;
		this.angler = fishingPlayer;
		this.angler.fishEntity = this;
		this.setSize(0.25F, 0.25F);
		this.setLocationAndAngles(fishingPlayer.posX, fishingPlayer.posY + (double) fishingPlayer.getEyeHeight(), fishingPlayer.posZ, fishingPlayer.rotationYaw, fishingPlayer.rotationPitch);
		this.posX -= (double) (MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F);
		this.posY -= 0.10000000149011612D;
		this.posZ -= (double) (MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F);
		this.setPosition(this.posX, this.posY, this.posZ);
		float f = 0.4F;
		this.motionX = (double) (-MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * f);
		this.motionZ = (double) (MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * f);
		this.motionY = (double) (-MathHelper.sin(this.rotationPitch / 180.0F * (float) Math.PI) * f);
		this.handleHookCasting(this.motionX, this.motionY, this.motionZ, 3F, 1.0F);
	}

	protected void entityInit() {
		this.getDataManager().register(field_184528_c, Integer.valueOf(0));
	}

	public void notifyDataManagerChange(DataParameter<?> key) {
		if(field_184528_c.equals(key)) {
			int i = ((Integer) this.getDataManager().get(field_184528_c)).intValue();

			if(i > 0 && this.caughtEntity != null) {
				this.caughtEntity = null;
			}
		}

		super.notifyDataManagerChange(key);
	}

	/**
	 * Checks if the entity is in range to render by using the past in distance and comparing it to its average edge
	 * length * 64 * renderDistanceWeight Args: distance
	 */
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double distance) {
		double d0 = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0D;

		if(Double.isNaN(d0)) {
			d0 = 4.0D;
		}

		d0 = d0 * 128.0D;
		return distance < d0 * d0;
	}

	public void handleHookCasting(double setX, double setY, double setZ, float velCoefficient, float velCoefficient2) {
		float f2 = MathHelper.sqrt_double(setX * setX + setY * setY + setZ * setZ);
		setX = setX / (double) f2;
		setY = setY / (double) f2;
		setZ = setZ / (double) f2;
		setX = setX + this.rand.nextGaussian() * 0.007499999832361937D * (double) velCoefficient2;
		setY = setY + this.rand.nextGaussian() * 0.007499999832361937D * (double) velCoefficient2;
		setZ = setZ + this.rand.nextGaussian() * 0.007499999832361937D * (double) velCoefficient2;
		setX = setX * (setY < 0 ? (double) (velCoefficient / 2) : (double) velCoefficient);
		setY = setY * (setY < 0 ? (double) (velCoefficient / 2) : (double) velCoefficient);
		setZ = setZ * (setY < 0 ? (double) (velCoefficient / 2) : (double) velCoefficient);
		this.motionX = setX;
		this.motionY = setY;
		this.motionZ = setZ;
		float f1 = MathHelper.sqrt_double(setX * setX + setZ * setZ);
		this.prevRotationYaw = this.rotationYaw = (float) (MathHelper.atan2(setX, setZ) * 180.0D / Math.PI);
		this.prevRotationPitch = this.rotationPitch = (float) (MathHelper.atan2(setY, (double) f1) * 180.0D / Math.PI);
		this.ticksInGround = 0;
	}

	@SideOnly(Side.CLIENT)
	public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean p_180426_10_) {
		this.fishX = x;
		this.fishY = y;
		this.fishZ = z;
		this.fishYaw = (double) yaw;
		this.fishPitch = (double) pitch;
		this.fishPosRotationIncrements = posRotationIncrements;
		this.motionX = this.clientMotionX;
		this.motionY = this.clientMotionY;
		this.motionZ = this.clientMotionZ;
	}

	/**
	 * Sets the velocity to the args. Args: x, y, z
	 */
	@SideOnly(Side.CLIENT)
	public void setVelocity(double x, double y, double z) {
		this.clientMotionX = this.motionX = x;
		this.clientMotionY = this.motionY = y;
		this.clientMotionZ = this.motionZ = z;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate() {
		this.prevDistanceWalkedModified = this.distanceWalkedModified;
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.prevRotationPitch = this.rotationPitch;
		this.prevRotationYaw = this.rotationYaw;

		//TODO likely has to be called and this class should extend entity
		//super.onUpdate();

		//pulling items toward it routine
		List pullingItemsList = this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expand(3.0D, 3.0D, 3.0D));

		Iterator itemIterator = pullingItemsList.iterator();
		while(itemIterator.hasNext()) {
			Entity e = (Entity) itemIterator.next();
			if(!(e instanceof EntityItem))
				continue;
			//if (e.getDistance(this.posX, this.posY, this.posZ) < 0.5D) {
			//EntityItem itemEntity = (EntityItem) e;
			//pulledItems.add(itemEntity.getEntityItem());
			//itemEntity.setDead();
			//} else {
			Vec3d pullVector = new Vec3d(this.posX - e.posX, this.posY - e.posY, this.posZ - e.posZ).normalize();
			e.motionX = pullVector.xCoord * 0.4D;
			e.motionY = pullVector.yCoord * 0.4D;
			e.motionZ = pullVector.zCoord * 0.4D;

		}

		if(this.worldObj.isRemote) {
			int i = this.getDataManager().get(field_184528_c).intValue();

			if(i > 0 && this.caughtEntity == null) {
				this.caughtEntity = this.worldObj.getEntityByID(i - 1);
			}
		} else {
			ItemStack itemstack = this.angler.getHeldItemMainhand();

			if(this.angler.isDead || !this.angler.isEntityAlive() || itemstack == null || itemstack.getItem() != ModItems.rodOfLyssa || this.getDistanceSqToEntity(this.angler) > 1024.0D) {
				this.setDead();
				this.angler.fishEntity = null;
				return;
			}
		}

		if(this.caughtEntity != null) {
			if(!this.caughtEntity.isDead) {
				this.posX = this.caughtEntity.posX;
				double d17 = (double) this.caughtEntity.height;
				this.posY = this.caughtEntity.getEntityBoundingBox().minY + d17 * 0.8D;
				this.posZ = this.caughtEntity.posZ;
				return;
			}

			this.caughtEntity = null;
		}

		if(this.fishPosRotationIncrements > 0) {
			double d7 = this.posX + (this.fishX - this.posX) / (double) this.fishPosRotationIncrements;
			double d8 = this.posY + (this.fishY - this.posY) / (double) this.fishPosRotationIncrements;
			double d9 = this.posZ + (this.fishZ - this.posZ) / (double) this.fishPosRotationIncrements;
			double d1 = MathHelper.wrapDegrees(this.fishYaw - (double) this.rotationYaw);
			this.rotationYaw = (float) ((double) this.rotationYaw + d1 / (double) this.fishPosRotationIncrements);
			this.rotationPitch = (float) ((double) this.rotationPitch + (this.fishPitch - (double) this.rotationPitch) / (double) this.fishPosRotationIncrements);
			--this.fishPosRotationIncrements;
			this.setPosition(d7, d8, d9);
			this.setRotation(this.rotationYaw, this.rotationPitch);
		} else {
			if(this.inGround) {
				if(this.worldObj.getBlockState(new BlockPos(this.xTile, this.yTile, this.zTile)).getBlock() == this.inTile) {
					++this.ticksInGround;

					if(this.ticksInGround == 1200) {
						this.setDead();
					}

					return;
				}

				this.inGround = false;
				this.motionX *= (double) (this.rand.nextFloat() * 0.2F);
				this.motionY *= (double) (this.rand.nextFloat() * 0.2F);
				this.motionZ *= (double) (this.rand.nextFloat() * 0.2F);
				this.ticksInGround = 0;
				this.ticksInAir = 0;
			} else {
				++this.ticksInAir;
			}

			Vec3d vec31 = new Vec3d(this.posX, this.posY, this.posZ);
			Vec3d vec3 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
			RayTraceResult rayTraceResult = this.worldObj.rayTraceBlocks(vec31, vec3);
			vec31 = new Vec3d(this.posX, this.posY, this.posZ);
			vec3 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

			if(rayTraceResult != null) {
				vec3 = new Vec3d(rayTraceResult.hitVec.xCoord, rayTraceResult.hitVec.yCoord, rayTraceResult.hitVec.zCoord);
			}

			Entity entity = null;
			List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
			double d0 = 0.0D;

			for(int i = 0; i < list.size(); ++i) {
				Entity entity1 = list.get(i);

				if(entity1.canBeCollidedWith() && (entity1 != this.angler || this.ticksInAir >= 5)) {
					float f = 0.3F;
					AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand((double) f, (double) f, (double) f);
					RayTraceResult rayTraceResult1 = axisalignedbb.calculateIntercept(vec31, vec3);

					if(rayTraceResult1 != null) {
						double d2 = vec31.squareDistanceTo(rayTraceResult1.hitVec);

						if(d2 < d0 || d0 == 0.0D) {
							entity = entity1;
							d0 = d2;
						}
					}
				}
			}

			if(entity != null) {
				rayTraceResult = new RayTraceResult(entity);
			}

			if(rayTraceResult != null) {
				if(rayTraceResult.entityHit != null) {
					double lowerX = this.posX - 7D;
					double lowerY = this.posY - 5D;
					double lowerZ = this.posZ - 7D;
					double upperX = this.posX + 7D;
					double upperY = this.posY + 5D;
					double upperZ = this.posZ + 7D;

					List eList = worldObj.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(lowerX, lowerY, lowerZ, upperX, upperY, upperZ));
					Iterator iterator = eList.iterator();
					while(iterator.hasNext()) {
						Entity e = (Entity) iterator.next();
						if(e instanceof EntityLivingBase && !e.isEntityEqual(angler)) {
							if(angler.isSneaking() || rayTraceResult.entityHit.attackEntityFrom(DamageSource.causeMobDamage((EntityLivingBase) e), 0.0F)) {
								this.caughtEntity = rayTraceResult.entityHit;
								break;
							}
						}
					}
				} else {
					this.inGround = true;
				}
			}

			if(!this.inGround) {
				this.moveEntity(this.motionX, this.motionY, this.motionZ);
				float f5 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
				this.rotationYaw = (float) (MathHelper.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

				for(this.rotationPitch = (float) (MathHelper.atan2(this.motionY, (double) f5) * 180.0D / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
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
				float f6 = 0.92F;

				if(this.onGround || this.isCollidedHorizontally) {
					f6 = 0.5F;
				}

				int b0 = 5;
				double d10 = 0.0D;

				for(int j = 0; j < b0; ++j) {
					AxisAlignedBB axisalignedbb1 = this.getEntityBoundingBox();
					double d3 = axisalignedbb1.maxY - axisalignedbb1.minY;
					double d4 = axisalignedbb1.minY + d3 * (double) j / (double) b0;
					double d5 = axisalignedbb1.minY + d3 * (double) (j + 1) / (double) b0;
					AxisAlignedBB axisalignedbb2 = new AxisAlignedBB(axisalignedbb1.minX, d4, axisalignedbb1.minZ, axisalignedbb1.maxX, d5, axisalignedbb1.maxZ);

					if(this.worldObj.isAABBInMaterial(axisalignedbb2, Material.WATER)) {
						d10 += 1.0D / (double) b0;
					}
				}

				if(!this.worldObj.isRemote && d10 > 0.0D) {
					WorldServer worldserver = (WorldServer) this.worldObj;
					int k = 1;
					BlockPos blockpos = (new BlockPos(this)).up();

					if(this.rand.nextFloat() < 0.25F && this.worldObj.isRainingAt(blockpos)) {
						k = 2;
					}

					if(this.rand.nextFloat() < 0.5F && !this.worldObj.canSeeSky(blockpos)) {
						--k;
					}

					if(this.ticksCatchable > 0) {
						--this.ticksCatchable;

						if(this.ticksCatchable <= 0) {
							this.ticksCaughtDelay = 0;
							this.ticksCatchableDelay = 0;
						}
					} else if(this.ticksCatchableDelay > 0) {
						this.ticksCatchableDelay -= k;

						if(this.ticksCatchableDelay <= 0) {
							this.motionY -= 0.20000000298023224D;
							this.playSound(SoundEvents.ENTITY_BOBBER_SPLASH, 0.25F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
							float f8 = (float) MathHelper.floor_double(this.getEntityBoundingBox().minY);
							worldserver.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX, (double) (f8 + 1.0F), this.posZ, (int) (1.0F + this.width * 20.0F), (double) this.width, 0.0D, (double) this.width, 0.20000000298023224D, new int[0]);
							worldserver.spawnParticle(EnumParticleTypes.WATER_WAKE, this.posX, (double) (f8 + 1.0F), this.posZ, (int) (1.0F + this.width * 20.0F), (double) this.width, 0.0D, (double) this.width, 0.20000000298023224D, new int[0]);
							this.ticksCatchable = MathHelper.getRandomIntegerInRange(this.rand, 10, 30);
						} else {
							this.fishApproachAngle = (float) ((double) this.fishApproachAngle + this.rand.nextGaussian() * 4.0D);
							float f7 = this.fishApproachAngle * 0.017453292F;
							float f10 = MathHelper.sin(f7);
							float f11 = MathHelper.cos(f7);
							double d13 = this.posX + (double) (f10 * (float) this.ticksCatchableDelay * 0.1F);
							double d15 = (double) ((float) MathHelper.floor_double(this.getEntityBoundingBox().minY) + 1.0F);
							double d16 = this.posZ + (double) (f11 * (float) this.ticksCatchableDelay * 0.1F);
							Block block1 = worldserver.getBlockState(new BlockPos((int) d13, (int) d15 - 1, (int) d16)).getBlock();

							if(block1 == Blocks.WATER || block1 == Blocks.FLOWING_WATER) {
								if(this.rand.nextFloat() < 0.15F) {
									worldserver.spawnParticle(EnumParticleTypes.WATER_BUBBLE, d13, d15 - 0.10000000149011612D, d16, 1, (double) f10, 0.1D, (double) f11, 0.0D, new int[0]);
								}

								float f3 = f10 * 0.04F;
								float f4 = f11 * 0.04F;
								worldserver.spawnParticle(EnumParticleTypes.WATER_WAKE, d13, d15, d16, 0, (double) f4, 0.01D, (double) (-f3), 1.0D, new int[0]);
								worldserver.spawnParticle(EnumParticleTypes.WATER_WAKE, d13, d15, d16, 0, (double) (-f4), 0.01D, (double) f3, 1.0D, new int[0]);
							}
						}
					} else if(this.ticksCaughtDelay > 0) {
						this.ticksCaughtDelay -= k;
						float f1 = 0.15F;

						if(this.ticksCaughtDelay < 20) {
							f1 = (float) ((double) f1 + (double) (20 - this.ticksCaughtDelay) * 0.05D);
						} else if(this.ticksCaughtDelay < 40) {
							f1 = (float) ((double) f1 + (double) (40 - this.ticksCaughtDelay) * 0.02D);
						} else if(this.ticksCaughtDelay < 60) {
							f1 = (float) ((double) f1 + (double) (60 - this.ticksCaughtDelay) * 0.01D);
						}

						if(this.rand.nextFloat() < f1) {
							float f9 = MathHelper.randomFloatClamp(this.rand, 0.0F, 360.0F) * 0.017453292F;
							float f2 = MathHelper.randomFloatClamp(this.rand, 25.0F, 60.0F);
							double d12 = this.posX + (double) (MathHelper.sin(f9) * f2 * 0.1F);
							double d14 = (double) ((float) MathHelper.floor_double(this.getEntityBoundingBox().minY) + 1.0F);
							double d6 = this.posZ + (double) (MathHelper.cos(f9) * f2 * 0.1F);
							Block block = worldserver.getBlockState(new BlockPos((int) d12, (int) d14 - 1, (int) d6)).getBlock();

							if(block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
								worldserver.spawnParticle(EnumParticleTypes.WATER_SPLASH, d12, d14, d6, 2 + this.rand.nextInt(2), 0.10000000149011612D, 0.0D, 0.10000000149011612D, 0.0D, new int[0]);
							}
						}

						if(this.ticksCaughtDelay <= 0) {
							this.fishApproachAngle = MathHelper.randomFloatClamp(this.rand, 0.0F, 360.0F);
							this.ticksCatchableDelay = MathHelper.getRandomIntegerInRange(this.rand, 20, 80);
						}
					} else {
						this.ticksCaughtDelay = MathHelper.getRandomIntegerInRange(this.rand, 100, 900);
						this.ticksCaughtDelay -= EnchantmentHelper.getLureModifier(this.angler) * 20 * 5;
					}

					if(this.ticksCatchable > 0) {
						this.motionY -= (double) (this.rand.nextFloat() * this.rand.nextFloat() * this.rand.nextFloat()) * 0.2D;
					}
				}

				double d11 = d10 * 2.0D - 1.0D;
				this.motionY += 0.03999999910593033D * d11;

				if(d10 > 0.0D) {
					f6 = (float) ((double) f6 * 0.9D);
					this.motionY *= 0.8D;
				}

				this.motionX *= (double) f6;
				this.motionY *= (double) f6;
				this.motionZ *= (double) f6;
				this.setPosition(this.posX, this.posY, this.posZ);
			}
		}
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	public void writeEntityToNBT(NBTTagCompound tagCompound) {
		tagCompound.setInteger("xTile", (short) this.xTile);
		tagCompound.setInteger("yTile", (short) this.yTile);
		tagCompound.setInteger("zTile", (short) this.zTile);
		ResourceLocation resourcelocation = (ResourceLocation) Block.REGISTRY.getNameForObject(this.inTile);
		tagCompound.setString("inTile", resourcelocation == null ? "" : resourcelocation.toString());
		tagCompound.setByte("inGround", (byte) (this.inGround ? 1 : 0));
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readEntityFromNBT(NBTTagCompound tagCompund) {
		this.xTile = tagCompund.getInteger("xTile");
		this.yTile = tagCompund.getInteger("yTile");
		this.zTile = tagCompund.getInteger("zTile");

		if(tagCompund.hasKey("inTile", 8)) {
			this.inTile = Block.getBlockFromName(tagCompund.getString("inTile"));
		} else {
			this.inTile = Block.getBlockById(tagCompund.getByte("inTile") & 255);
		}

		this.inGround = tagCompund.getByte("inGround") == 1;
	}

	public int handleHookRetraction() {
		if(this.worldObj.isRemote) {
			return 0;
		} else {
			int i = 0;

			if(this.caughtEntity != null) {

				this.func_184527_k();
				this.worldObj.setEntityState(this, (byte) 31);
				i = this.caughtEntity instanceof EntityItem ? 3 : 5;

				if(angler.isSneaking() && this.caughtEntity instanceof EntityLivingBase) {
					EntityLivingBase livingBase = (EntityLivingBase) this.caughtEntity;
					if(!(livingBase instanceof EntityPlayer)) {
						EntityEquipmentSlot[] slots = new EntityEquipmentSlot[] {EntityEquipmentSlot.MAINHAND,
								EntityEquipmentSlot.FEET,
								EntityEquipmentSlot.LEGS,
								EntityEquipmentSlot.CHEST,
								EntityEquipmentSlot.HEAD};

						int slotBeingStolenFrom = worldObj.rand.nextInt(5);
						ItemStack stolenStack = livingBase.getItemStackFromSlot(slots[slotBeingStolenFrom]);
						if(stolenStack == null && !Settings.RodOfLyssa.stealFromVacantSlots) {
							for(int j = 0; j < 5; ++j) {
								stolenStack = livingBase.getItemStackFromSlot(slots[j]);
								if(stolenStack != null) {
									slotBeingStolenFrom = j;
									break;
								}
							}
						}

						float failProbabilityFactor;

						if(Settings.RodOfLyssa.useLeveledFailureRate)
							failProbabilityFactor = 1F / ((float) Math.sqrt((double) Math.max(1, Math.min(angler.experienceLevel, Settings.RodOfLyssa.levelCapForLeveledFormula))) * 2);
						else
							failProbabilityFactor = Settings.RodOfLyssa.flatStealFailurePercentRate / 100F;

						if(rand.nextFloat() <= failProbabilityFactor || (stolenStack == null && Settings.RodOfLyssa.failStealFromVacantSlots)) {
							if(Settings.RodOfLyssa.angerOnStealFailure)
								livingBase.attackEntityFrom(DamageSource.causePlayerDamage(this.angler), 0.0F);
						}
						if(stolenStack != null) {
							int randomItemDamage = worldObj.rand.nextInt(3);
							stolenStack.damageItem(randomItemDamage, livingBase);
							EntityItem entityitem = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, stolenStack);
							double d1 = this.angler.posX - this.posX;
							double d3 = this.angler.posY - this.posY;
							double d5 = this.angler.posZ - this.posZ;
							double d7 = (double) MathHelper.sqrt_double(d1 * d1 + d3 * d3 + d5 * d5);
							double d9 = 0.1D;
							entityitem.motionX = d1 * d9;
							entityitem.motionY = d3 * d9 + (double) MathHelper.sqrt_double(d7) * 0.08D;
							entityitem.motionZ = d5 * d9;
							this.worldObj.spawnEntityInWorld(entityitem);

							livingBase.setItemStackToSlot(slots[slotBeingStolenFrom], null);
						}
					}
				}
			} else if(this.ticksCatchable > 0) {
				LootContext.Builder lootcontext$builder = new LootContext.Builder((WorldServer) this.worldObj);
				lootcontext$builder.withLuck((float) EnchantmentHelper.getLuckOfSeaModifier(this.angler) + this.angler.getLuck());

				for(ItemStack itemstack : this.worldObj.getLootTableManager().getLootTableFromLocation(LootTableList.GAMEPLAY_FISHING).generateLootForPools(this.rand, lootcontext$builder.build())) {
					EntityItem entityitem = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, itemstack);
					double d0 = this.angler.posX - this.posX;
					double d1 = this.angler.posY - this.posY;
					double d2 = this.angler.posZ - this.posZ;
					double d3 = (double) MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
					double d4 = 0.1D;
					entityitem.motionX = d0 * d4;
					entityitem.motionY = d1 * d4 + (double) MathHelper.sqrt_double(d3) * 0.08D;
					entityitem.motionZ = d2 * d4;
					this.worldObj.spawnEntityInWorld(entityitem);
					this.angler.worldObj.spawnEntityInWorld(new EntityXPOrb(this.angler.worldObj, this.angler.posX, this.angler.posY + 0.5D, this.angler.posZ + 0.5D, this.rand.nextInt(6) + 1));
				}
				i = 1;
			}

			List pullingItemsList = this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));

			Iterator itemIterator = pullingItemsList.iterator();
			while(itemIterator.hasNext()) {
				Entity e = (Entity) itemIterator.next();
				if(!(e instanceof EntityItem))
					continue;

				double d1 = this.angler.posX - this.posX;
				double d3 = this.angler.posY - this.posY;
				double d5 = this.angler.posZ - this.posZ;
				double d7 = (double) MathHelper.sqrt_double(d1 * d1 + d3 * d3 + d5 * d5);
				double d9 = 0.1D;
				((EntityItem) e).motionX = d1 * d9;
				((EntityItem) e).motionY = d3 * d9 + (double) MathHelper.sqrt_double(d7) * 0.08D;
				((EntityItem) e).motionZ = d5 * d9;
			}

			this.setDead();
			this.angler.fishEntity = null;
			return i;
		}
	}

	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte id) {
		if(id == 31 && this.worldObj.isRemote && this.caughtEntity instanceof EntityPlayer && ((EntityPlayer) this.caughtEntity).isUser()) {
			this.func_184527_k();
		}

		super.handleStatusUpdate(id);
	}

	private void func_184527_k() {
		double d0 = this.angler.posX - this.posX;
		double d2 = this.angler.posY - this.posY;
		double d4 = this.angler.posZ - this.posZ;
		double d6 = (double) MathHelper.sqrt_double(d0 * d0 + d2 * d2 + d4 * d4);
		double d8 = angler.isSneaking() ? 0.0D : 0.1D;
		this.caughtEntity.motionX += d0 * d8;
		this.caughtEntity.motionY += d2 * d8 + (double) MathHelper.sqrt_double(d6) * 0.12D;
		this.caughtEntity.motionZ += d4 * d8;
	}

	/**
	 * Will get destroyed next tick.
	 */
	public void setDead() {
		super.setDead();

		if(this.angler != null) {
			this.angler.fishEntity = null;
		}
	}
}