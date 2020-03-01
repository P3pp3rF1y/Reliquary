package xreliquary.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;
import xreliquary.init.ModEntities;
import xreliquary.init.ModItems;
import xreliquary.reference.Settings;
import xreliquary.util.LogHelper;
import xreliquary.util.RandHelper;

import java.lang.reflect.Field;
import java.util.List;

public class LyssaBobberEntity extends Entity implements IEntityAdditionalSpawnData {
	private static final DataParameter<Integer> DATA_HOOKED_ENTITY = EntityDataManager.createKey(FishingBobberEntity.class, DataSerializers.VARINT);
	private boolean inGround;
	private int ticksInGround = 0;
	private PlayerEntity angler;
	private int ticksInAir;
	private int ticksCatchable;
	private int ticksCaughtDelay;
	private int ticksCatchableDelay;
	private float fishApproachAngle;
	private Entity caughtEntity;
	private State currentState = State.FLYING;
	private int lureSpeed;
	private int luck;

	public LyssaBobberEntity(EntityType<LyssaBobberEntity> entityType, World world) {
		super(entityType, world);
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
	}

	public LyssaBobberEntity(World world, PlayerEntity fishingPlayer, int lureSpeed, int luck) {
		super(ModEntities.LYSSA_HOOK, world);
		this.lureSpeed = lureSpeed;
		this.luck = luck;
		init(fishingPlayer);
		shoot();

		//Reliquary
		speedUp();
	}

	private void init(PlayerEntity player) {
		ignoreFrustumCheck = true;
		angler = player;
	}

	private void shoot() {
		float f = angler.rotationPitch;
		float f1 = angler.rotationYaw;
		float f2 = MathHelper.cos(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
		float f3 = MathHelper.sin(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
		float f4 = -MathHelper.cos(-f * ((float) Math.PI / 180F));
		float f5 = MathHelper.sin(-f * ((float) Math.PI / 180F));
		double d0 = angler.posX - (double) f3 * 0.3D;
		double d1 = angler.posY + (double) angler.getEyeHeight();
		double d2 = angler.posZ - (double) f2 * 0.3D;
		setLocationAndAngles(d0, d1, d2, f1, f);
		Vec3d vec3d = new Vec3d(-f3, MathHelper.clamp(-(f5 / f4), -5.0F, 5.0F), -f2);
		double d3 = vec3d.length();
		vec3d = vec3d.mul(0.6D / d3 + 0.5D + rand.nextGaussian() * 0.0045D, 0.6D / d3 + 0.5D + rand.nextGaussian() * 0.0045D, 0.6D / d3 + 0.5D + rand.nextGaussian() * 0.0045D);
		setMotion(vec3d);
		rotationYaw = (float) (MathHelper.atan2(vec3d.x, vec3d.z) * (double) (180F / (float) Math.PI));
		rotationPitch = (float) (MathHelper.atan2(vec3d.y, MathHelper.sqrt(func_213296_b(vec3d))) * (double) (180F / (float) Math.PI));
		prevRotationYaw = rotationYaw;
		prevRotationPitch = rotationPitch;
	}

	@Override
	protected void registerData() {
		getDataManager().register(DATA_HOOKED_ENTITY, 0);
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		if (DATA_HOOKED_ENTITY.equals(key)) {
			int i = getDataManager().get(DATA_HOOKED_ENTITY);
			caughtEntity = i > 0 ? world.getEntityByID(i - 1) : null;
		}

		super.notifyDataManagerChange(key);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
		//noop
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	private void tickOriginal() {
		super.tick();

		if (angler == null) {
			remove();
		} else if (world.isRemote || !shouldStopFishing()) {
			if (inGround) {
				++ticksInGround;

				if (ticksInGround >= 1200) {
					remove();
					return;
				}
			}

			float f = 0.0F;
			BlockPos blockpos = new BlockPos(this);
			IFluidState ifluidstate = world.getFluidState(blockpos);
			if (ifluidstate.isTagged(FluidTags.WATER)) {
				f = ifluidstate.func_215679_a(world, blockpos);
			}

			if (currentState == State.FLYING) {
				if (caughtEntity != null) {
					setMotion(Vec3d.ZERO);
					currentState = State.HOOKED_IN_ENTITY;
					return;
				}

				if (f > 0.0F) {
					setMotion(getMotion().mul(0.3D, 0.2D, 0.3D));
					currentState = State.BOBBING;
					return;
				}

				if (!world.isRemote) {
					checkCollision();
				}

				if (!inGround && !onGround && !collidedHorizontally) {
					++ticksInAir;
				} else {
					ticksInAir = 0;
					setMotion(Vec3d.ZERO);
				}
			} else {
				if (currentState == State.HOOKED_IN_ENTITY) {
					if (caughtEntity != null) {
						if (!caughtEntity.isAlive()) {
							caughtEntity = null;
							currentState = State.FLYING;
						} else {
							posX = caughtEntity.posX;
							posY = caughtEntity.getBoundingBox().minY + (double) caughtEntity.getHeight() * 0.8D;
							posZ = caughtEntity.posZ;
							setPosition(posX, posY, posZ);
						}
					}

					return;
				}

				if (currentState == State.BOBBING) {
					Vec3d vec3d = getMotion();
					double d0 = posY + vec3d.y - (double) blockpos.getY() - (double) f;
					if (Math.abs(d0) < 0.01D) {
						d0 += Math.signum(d0) * 0.1D;
					}

					setMotion(vec3d.x * 0.9D, vec3d.y - d0 * (double) rand.nextFloat() * 0.2D, vec3d.z * 0.9D);
					if (!world.isRemote && f > 0.0F) {
						catchingFish(blockpos);
					}
				}
			}

			if (!ifluidstate.isTagged(FluidTags.WATER)) {
				setMotion(getMotion().add(0.0D, -0.03D, 0.0D));
			}

			move(MoverType.SELF, getMotion());
			updateRotation();
			setMotion(getMotion().scale(0.92D));
			setPosition(posX, posY, posZ);
		}
	}

	private void updateRotation() {
		Vec3d vec3d = getMotion();
		float f = MathHelper.sqrt(func_213296_b(vec3d));
		rotationYaw = (float) (MathHelper.atan2(vec3d.x, vec3d.z) * (double) (180F / (float) Math.PI));

		rotationPitch = (float) (MathHelper.atan2(vec3d.y, f) * (double) (180F / (float) Math.PI));
		while (rotationPitch - prevRotationPitch < -180.0F) {
			prevRotationPitch -= 360.0F;
		}

		while (rotationPitch - prevRotationPitch >= 180.0F) {
			prevRotationPitch += 360.0F;
		}

		while (rotationYaw - prevRotationYaw < -180.0F) {
			prevRotationYaw -= 360.0F;
		}

		while (rotationYaw - prevRotationYaw >= 180.0F) {
			prevRotationYaw += 360.0F;
		}

		rotationPitch = MathHelper.lerp(0.2F, prevRotationPitch, rotationPitch);
		rotationYaw = MathHelper.lerp(0.2F, prevRotationYaw, rotationYaw);
	}

	private void checkCollision() {
		RayTraceResult raytraceresult = ProjectileHelper.func_221267_a(this, getBoundingBox().expand(getMotion()).grow(1.0D), e -> !e.isSpectator() && (e.canBeCollidedWith() || e instanceof ItemEntity) && (e != angler || ticksInAir >= 5), RayTraceContext.BlockMode.COLLIDER, true);
		if (raytraceresult.getType() != RayTraceResult.Type.MISS) {
			if (raytraceresult.getType() == RayTraceResult.Type.ENTITY) {
				caughtEntity = ((EntityRayTraceResult) raytraceresult).getEntity();
				setHookedEntity();
			} else {
				inGround = true;
			}
		}
	}

	private void setHookedEntity() {
		getDataManager().set(DATA_HOOKED_ENTITY, caughtEntity.getEntityId() + 1);
	}

	private void catchingFish(BlockPos pos) {
		ServerWorld serverworld = (ServerWorld) world;
		int i = 1;
		BlockPos blockpos = pos.up();
		if (rand.nextFloat() < 0.25F && world.isRainingAt(blockpos)) {
			++i;
		}

		if (rand.nextFloat() < 0.5F && !world.isSkyLightMax(blockpos)) {
			--i;
		}

		if (ticksCatchable > 0) {
			--ticksCatchable;
			if (ticksCatchable <= 0) {
				ticksCaughtDelay = 0;
				ticksCatchableDelay = 0;
			} else {
				setMotion(getMotion().add(0.0D, -0.2D * (double) rand.nextFloat() * (double) rand.nextFloat(), 0.0D));
			}
		} else if (ticksCatchableDelay > 0) {
			ticksCatchableDelay -= i;
			if (ticksCatchableDelay > 0) {
				fishApproachAngle = (float) ((double) fishApproachAngle + rand.nextGaussian() * 4.0D);
				float f = fishApproachAngle * ((float) Math.PI / 180F);
				float f1 = MathHelper.sin(f);
				float f2 = MathHelper.cos(f);
				double d0 = posX + (double) (f1 * (float) ticksCatchableDelay * 0.1F);
				double d1 = (float) MathHelper.floor(getBoundingBox().minY) + 1.0F;
				double d2 = posZ + (double) (f2 * (float) ticksCatchableDelay * 0.1F);
				if (serverworld.getBlockState(new BlockPos((int) d0, (int) d1 - 1, (int) d2)).getMaterial() == net.minecraft.block.material.Material.WATER) {
					if (rand.nextFloat() < 0.15F) {
						serverworld.spawnParticle(ParticleTypes.BUBBLE, d0, d1 - (double) 0.1F, d2, 1, f1, 0.1D, f2, 0.0D);
					}

					float f3 = f1 * 0.04F;
					float f4 = f2 * 0.04F;
					serverworld.spawnParticle(ParticleTypes.FISHING, d0, d1, d2, 0, f4, 0.01D, -f3, 1.0D);
					serverworld.spawnParticle(ParticleTypes.FISHING, d0, d1, d2, 0, -f4, 0.01D, f3, 1.0D);
				}
			} else {
				Vec3d vec3d = getMotion();
				setMotion(vec3d.x, -0.4F * MathHelper.nextFloat(rand, 0.6F, 1.0F), vec3d.z);
				playSound(SoundEvents.ENTITY_FISHING_BOBBER_SPLASH, 0.25F, 1.0F + RandHelper.getRandomMinusOneToOne(rand) * 0.4F);
				double d3 = getBoundingBox().minY + 0.5D;
				serverworld.spawnParticle(ParticleTypes.BUBBLE, posX, d3, posZ, (int) (1.0F + getWidth() * 20.0F), getWidth(), 0.0D, getWidth(), 0.2F);
				serverworld.spawnParticle(ParticleTypes.FISHING, posX, d3, posZ, (int) (1.0F + getWidth() * 20.0F), getWidth(), 0.0D, getWidth(), 0.2F);
				ticksCatchable = MathHelper.nextInt(rand, 20, 40);
			}
		} else if (ticksCaughtDelay > 0) {
			ticksCaughtDelay -= i;
			float f5 = 0.15F;
			if (ticksCaughtDelay < 20) {
				f5 = (float) ((double) f5 + (double) (20 - ticksCaughtDelay) * 0.05D);
			} else if (ticksCaughtDelay < 40) {
				f5 = (float) ((double) f5 + (double) (40 - ticksCaughtDelay) * 0.02D);
			} else if (ticksCaughtDelay < 60) {
				f5 = (float) ((double) f5 + (double) (60 - ticksCaughtDelay) * 0.01D);
			}

			if (rand.nextFloat() < f5) {
				float f6 = MathHelper.nextFloat(rand, 0.0F, 360.0F) * ((float) Math.PI / 180F);
				float f7 = MathHelper.nextFloat(rand, 25.0F, 60.0F);
				double d4 = posX + (double) (MathHelper.sin(f6) * f7 * 0.1F);
				double d5 = (float) MathHelper.floor(getBoundingBox().minY) + 1.0F;
				double d6 = posZ + (double) (MathHelper.cos(f6) * f7 * 0.1F);
				if (serverworld.getBlockState(new BlockPos(d4, d5 - 1.0D, d6)).getMaterial() == net.minecraft.block.material.Material.WATER) {
					serverworld.spawnParticle(ParticleTypes.SPLASH, d4, d5, d6, 2 + rand.nextInt(2), 0.1F, 0.0D, 0.1F, 0.0D);
				}
			}

			if (ticksCaughtDelay <= 0) {
				fishApproachAngle = MathHelper.nextFloat(rand, 0.0F, 360.0F);
				ticksCatchableDelay = MathHelper.nextInt(rand, 20, 80);
			}
		} else {
			ticksCaughtDelay = MathHelper.nextInt(rand, 100, 600);
			ticksCaughtDelay -= lureSpeed * 20 * 5;
		}
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	@Override
	public void writeAdditional(CompoundNBT compound) {
		//noop
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	public void readAdditional(CompoundNBT compound) {
		//noop
	}

	private int handleHookRetractionOriginal(ItemStack stack) {
		if (!world.isRemote && angler != null) {
			int i = 0;
			if (caughtEntity != null) {
				bringInHookedEntity();
				world.setEntityState(this, (byte) 31);
				i = caughtEntity instanceof ItemEntity ? 3 : 5;
			} else if (ticksCatchable > 0) {
				LootContext.Builder builder = (new LootContext.Builder((ServerWorld) world)).withParameter(LootParameters.POSITION, new BlockPos(this)).withParameter(LootParameters.TOOL, stack).withRandom(rand).withLuck((float) luck + angler.getLuck());
				builder.withParameter(LootParameters.KILLER_ENTITY, angler).withParameter(LootParameters.THIS_ENTITY, this);
				LootTable loottable = world.getServer().getLootTableManager().getLootTableFromLocation(LootTables.GAMEPLAY_FISHING);
				List<ItemStack> list = loottable.generate(builder.build(LootParameterSets.FISHING));

				for (ItemStack itemstack : list) {
					ItemEntity itementity = new ItemEntity(world, posX, posY, posZ, itemstack);
					double d0 = angler.posX - posX;
					double d1 = angler.posY - posY;
					double d2 = angler.posZ - posZ;
					itementity.setMotion(d0 * 0.1D, d1 * 0.1D + Math.sqrt(Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2)) * 0.08D, d2 * 0.1D);
					world.addEntity(itementity);
					angler.world.addEntity(new ExperienceOrbEntity(angler.world, angler.posX, angler.posY + 0.5D, angler.posZ + 0.5D, rand.nextInt(6) + 1));
					if (itemstack.getItem().isIn(ItemTags.FISHES)) {
						angler.addStat(Stats.FISH_CAUGHT, 1);
					}
				}

				i = 1;
			}

			if (inGround) {
				i = 2;
			}

			remove();
			return i;
		} else {
			return 0;
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleStatusUpdate(byte id) {
		if (id == 31 && world.isRemote && caughtEntity instanceof PlayerEntity && ((PlayerEntity) caughtEntity).isUser()) {
			bringInHookedEntity();
		}

		super.handleStatusUpdate(id);
	}

	private void bringInHookedEntityOriginal() {
		if (angler != null) {
			Vec3d vec3d = (new Vec3d(angler.posX - posX, angler.posY - posY, angler.posZ - posZ)).scale(0.1D);
			caughtEntity.setMotion(caughtEntity.getMotion().add(vec3d));
		}
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
	public void remove(boolean keepData) {
		super.remove(keepData);
		if (angler != null) {
			angler.fishingBobber = null;
		}

	}

	public PlayerEntity getAngler() {
		return angler;
	}

	private enum State {
		FLYING, HOOKED_IN_ENTITY, BOBBING

	}

	/*
		Reliquary customizations to the default EntityFishHook behavior
	*/

	private void speedUp() {
		//faster speed of the hook except for casting down

		if (getMotion().getY() >= 0) {
			setMotion(getMotion().mul(2, 2, 2));
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean isInRangeToRenderDist(double distance) {
		//much higher visible range than regular hook
		return distance < 16384;
	}

	@Override
	public void tick() {
		tickOriginal();

		pullItemEntitiesTowardsHook();
	}

	private boolean shouldStopFishing() {
		ItemStack itemstack = angler.getHeldItemMainhand();
		ItemStack itemstack1 = angler.getHeldItemOffhand();
		boolean flag = itemstack.getItem() == ModItems.ROD_OF_LYSSA;
		boolean flag1 = itemstack1.getItem() == ModItems.ROD_OF_LYSSA;
		if (angler.isAlive() && (flag || flag1) && getDistanceSq(angler) <= 1024.0D) {
			return false;
		} else {
			remove();
			return true;
		}
	}

	private void pullItemEntitiesTowardsHook() {
		if (isAlive() && caughtEntity == null) {
			float f = 0.0F;
			BlockPos blockpos = new BlockPos(this);

			IFluidState ifluidstate = world.getFluidState(blockpos);
			if (ifluidstate.isTagged(FluidTags.WATER)) {
				f = ifluidstate.func_215679_a(world, blockpos);
			}

			if (f <= 0F) {
				List<Entity> list = world.getEntitiesWithinAABB(ItemEntity.class, getBoundingBox().expand(getMotion()).grow(3.0D));

				for (Entity e : list) {
					Vec3d pullVector = new Vec3d(posX - e.posX, posY - e.posY, posZ - e.posZ).normalize();
					e.setMotion(pullVector.mul(0.4D, 0.4D, 0.4D));
				}
			}
		}
	}

	private void bringInHookedEntity() {
		bringInHookedEntityOriginal();

		if (caughtEntity instanceof ItemEntity) {
			caughtEntity.setMotion(caughtEntity.getMotion().mul(4D, 4D, 4D));
		} else if (caughtEntity instanceof LivingEntity) {
			caughtEntity.setMotion(caughtEntity.getMotion().mul(1, 1.5D, 1));
		}
	}

	public void handleHookRetraction(ItemStack stack) {
		if (!world.isRemote) {
			if (caughtEntity != null && getAngler().isSneaking() && canStealFromEntity()) {
				stealFromLivingEntity();
				remove();
			} else {
				handleHookRetractionOriginal(stack);
			}

			pullItemEntitiesWithHook();
		}

	}

	private boolean canStealFromEntity() {
		return caughtEntity instanceof LivingEntity && (Settings.COMMON.items.rodOfLyssa.stealFromPlayers.get() || !(caughtEntity instanceof PlayerEntity));
	}

	private void pullItemEntitiesWithHook() {
		List<ItemEntity> pullingItemsList = world.getEntitiesWithinAABB(ItemEntity.class, getBoundingBox().expand(getMotion()).grow(1.0D, 1.0D, 1.0D));

		for (ItemEntity e : pullingItemsList) {
			double d1 = getAngler().posX - posX;
			double d3 = getAngler().posY - posY;
			double d5 = getAngler().posZ - posZ;
			double d7 = MathHelper.sqrt(d1 * d1 + d3 * d3 + d5 * d5);
			double d9 = 0.1D;
			e.setMotion(d1 * d9, d3 * d9 + (double) MathHelper.sqrt(d7) * 0.08D, d5 * d9);
		}
	}

	private void stealFromLivingEntity() {
		LivingEntity livingEntity = (LivingEntity) caughtEntity;
		EquipmentSlotType slotBeingStolenFrom = EquipmentSlotType.values()[world.rand.nextInt(EquipmentSlotType.values().length)];

		ItemStack stolenStack = livingEntity.getItemStackFromSlot(slotBeingStolenFrom);
		if (stolenStack.isEmpty() && Boolean.TRUE.equals(Settings.COMMON.items.rodOfLyssa.stealFromVacantSlots.get())) {
			for (EquipmentSlotType slot : EquipmentSlotType.values()) {
				stolenStack = livingEntity.getItemStackFromSlot(slot);
				if (!stolenStack.isEmpty() && canDropFromSlot(livingEntity, slot)) {
					slotBeingStolenFrom = slot;
					break;
				}
			}
		}

		float failProbabilityFactor;

		if (Boolean.TRUE.equals(Settings.COMMON.items.rodOfLyssa.useLeveledFailureRate.get())) {
			failProbabilityFactor = 1F / ((float) Math.sqrt(Math.max(1, Math.min(getAngler().experienceLevel, Settings.COMMON.items.rodOfLyssa.levelCapForLeveledFormula.get()))) * 2);
		} else {
			failProbabilityFactor = Settings.COMMON.items.rodOfLyssa.flatStealFailurePercentRate.get() / 100F;
		}

		if ((rand.nextFloat() <= failProbabilityFactor || (stolenStack.isEmpty() && Settings.COMMON.items.rodOfLyssa.failStealFromVacantSlots.get())) && Boolean.TRUE.equals(Settings.COMMON.items.rodOfLyssa.angerOnStealFailure.get())) {
			livingEntity.attackEntityFrom(DamageSource.causePlayerDamage(getAngler()), 0.0F);
		}
		if (!stolenStack.isEmpty()) {
			int randomItemDamage = world.rand.nextInt(3);
			stolenStack.damageItem(randomItemDamage, livingEntity, e -> {});
			ItemEntity entityitem = new ItemEntity(world, posX, posY, posZ, stolenStack);
			entityitem.setPickupDelay(5);
			double d1 = getAngler().posX - posX;
			double d3 = getAngler().posY - posY;
			double d5 = getAngler().posZ - posZ;
			double d7 = MathHelper.sqrt(d1 * d1 + d3 * d3 + d5 * d5);
			double d9 = 0.1D;
			entityitem.setMotion(d1 * d9, d3 * d9 + (double) MathHelper.sqrt(d7) * 0.08D, d5 * d9);
			world.addEntity(entityitem);

			livingEntity.setItemStackToSlot(slotBeingStolenFrom, ItemStack.EMPTY);
		}
	}

	private boolean canDropFromSlot(LivingEntity entity, EquipmentSlotType slot) {
		if (!(entity instanceof MobEntity)) {
			return true;
		}
		MobEntity livingEntity = (MobEntity) entity;

		try {
			if (slot.getSlotType() == EquipmentSlotType.Group.HAND) {
				return ((float[]) HANDS_CHANCES.get(livingEntity))[slot.getIndex()] > -1;
			} else {
				return ((float[]) ARMOR_CHANCES.get(livingEntity))[slot.getIndex()] > -1;
			}
		}
		catch (IllegalAccessException e) {
			LogHelper.error(e);
		}

		return false;
	}

	private static final Field HANDS_CHANCES = ObfuscationReflectionHelper.findField(MobEntity.class, "field_82174_bp");
	private static final Field ARMOR_CHANCES = ObfuscationReflectionHelper.findField(MobEntity.class, "field_184655_bs");

	@Override
	public void writeSpawnData(PacketBuffer buffer) {
		buffer.writeInt(angler.getEntityId());
	}

	@Override
	public void readSpawnData(PacketBuffer additionalData) {
		Entity entity = world.getEntityByID(additionalData.readInt());

		if (entity instanceof PlayerEntity) {
			angler = (PlayerEntity) entity;
		}
	}

	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
