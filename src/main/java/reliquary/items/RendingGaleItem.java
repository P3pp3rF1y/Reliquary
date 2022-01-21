package reliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.items.CapabilityItemHandler;
import reliquary.items.util.FilteredBigItemStack;
import reliquary.items.util.FilteredItemHandlerProvider;
import reliquary.items.util.FilteredItemStackHandler;
import reliquary.items.util.IScrollableItem;
import reliquary.reference.Settings;
import reliquary.util.LanguageHelper;
import reliquary.util.LogHelper;
import reliquary.util.NBTHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RendingGaleItem extends ToggleableItem implements IScrollableItem {
	private static final String COUNT_TAG = "count";
	private static final int NO_DAMAGE_ELYTRA_TICKS = 3;

	public RendingGaleItem() {
		super(new Properties().stacksTo(1));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack rendingGale, @Nullable Level world, List<Component> tooltip) {
		LanguageHelper.formatTooltip(getDescriptionId() + ".tooltip2",
				Map.of("charge", String.valueOf(getFeatherCount(rendingGale, true) / 100)), tooltip);

		if (isEnabled(rendingGale)) {
			LanguageHelper.formatTooltip("tooltip.absorb_active", Map.of("item", ChatFormatting.RED + Items.FEATHER.getName(new ItemStack(Items.FEATHER)).getString()), tooltip);
		} else {
			LanguageHelper.formatTooltip("tooltip.absorb", tooltip);
		}
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return true;
	}

	private static int getChargeLimit() {
		return Settings.COMMON.items.rendingGale.chargeLimit.get();
	}

	public static int getChargeCost() {
		return Settings.COMMON.items.rendingGale.castChargeCost.get();
	}

	private static int getFeathersWorth() {
		return Settings.COMMON.items.rendingGale.chargeFeatherWorth.get();
	}

	private static int getBoltChargeCost() {
		return Settings.COMMON.items.rendingGale.boltChargeCost.get();
	}

	private static int getBoltTargetRange() {
		return Settings.COMMON.items.rendingGale.blockTargetRange.get();
	}

	private static int getRadialPushRadius() {
		return Settings.COMMON.items.rendingGale.pushPullRadius.get();
	}

	private void attemptFlight(LivingEntity entityLiving) {
		if (!(entityLiving instanceof Player player)) {
			return;
		}

		BlockHitResult rayTrace = getPlayerPOVHitResult(player.level, player, ClipContext.Fluid.NONE);

		Vec3 motion = player.getLookAngle().multiply(2, 2, 2);
		if (rayTrace.getType() == HitResult.Type.BLOCK) {
			double distance = player.blockPosition().distSqr(rayTrace.getBlockPos());
			if (distance < 40) {
				double slowDownFactor = distance / 40;
				motion = player.getLookAngle().multiply(slowDownFactor, slowDownFactor, slowDownFactor);
			}
		}
		player.setDeltaMovement(motion);
		player.fallDistance = 0.0F;

		if (player.isFallFlying()) {
			preventElytraDamage(player);
		}
	}

	private static final Field TICKS_ELYTRA_FLYING = ObfuscationReflectionHelper.findField(LivingEntity.class, "fallFlyTicks");

	@SuppressWarnings("java:S3011") //the reflection accessibility bypass here is the only way one can set the value
	private static void preventElytraDamage(Player player) {
		try {
			TICKS_ELYTRA_FLYING.set(player, NO_DAMAGE_ELYTRA_TICKS);
		}
		catch (IllegalAccessException e) {
			LogHelper.error("Error setting ticksElytraFlying on player ", e);
		}
	}

	@Override
	public void inventoryTick(ItemStack rendingGale, Level world, Entity entity, int slotNumber, boolean isSelected) {
		if (world.isClientSide || !(entity instanceof Player player) || world.getGameTime() % 10 != 0) {
			return;
		}

		if (isEnabled(rendingGale)) {
			int currentFeatherCharge = getFeatherCount(rendingGale);
			consumeAndCharge(player, getChargeLimit() - currentFeatherCharge, getFeathersWorth(), Items.FEATHER, 16,
					chargeToAdd -> setFeatherCount(rendingGale, currentFeatherCharge + chargeToAdd, !player.isUsingItem()));
		}
	}

	public Mode getMode(ItemStack stack) {
		return NBTHelper.getEnumConstant(stack, "mode", Mode::valueOf).orElse(Mode.FLIGHT);
	}

	private void setMode(ItemStack stack, Mode mode) {
		NBTHelper.putString("mode", stack, mode.getSerializedName());
	}

	private void cycleMode(ItemStack stack, boolean isRaining, boolean next) {
		Mode currentMode = getMode(stack);
		Mode modeToSet = next ? currentMode.next() : currentMode.previous();
		if (!isRaining && modeToSet == Mode.BOLT) {
			modeToSet = next ? modeToSet.next() : modeToSet.previous();
		}
		setMode(stack, modeToSet);
	}

	@Override
	public InteractionResult onMouseScrolled(ItemStack stack, Player player, double scrollDelta) {
		if (player.level.isClientSide) {
			return InteractionResult.PASS;
		}
		cycleMode(stack, player.level.isRaining(), scrollDelta > 0);
		return InteractionResult.SUCCESS;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return new FilteredItemHandlerProvider(Collections.singletonList(new FilteredItemStackHandler.RemovableStack(
				new FilteredBigItemStack(Items.FEATHER, Settings.COMMON.items.rendingGale.chargeLimit.get(),
						Settings.COMMON.items.rendingGale.chargeFeatherWorth.get())
				, false)));
	}

	@Override
	public int getUseDuration(ItemStack par1ItemStack) {
		return 6000;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BLOCK;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack rendingGale = player.getItemInHand(hand);
		if (player.isShiftKeyDown()) {
			super.use(world, player, hand);
		} else {
			player.startUsingItem(hand);
		}
		return new InteractionResultHolder<>(InteractionResult.SUCCESS, rendingGale);
	}

	@Override
	public void onUsingTick(ItemStack rendingGale, LivingEntity entity, int count) {
		if (!(entity instanceof Player player)) {
			return;
		}

		if (getFeatherCount(rendingGale, player.level.isClientSide) <= 0) {
			player.releaseUsingItem();
			return;
		}

		if (getMode(rendingGale) == Mode.BOLT) {
			if (count % 8 == 0) {
				spawnBolt(rendingGale, player);
			}
		} else {
			if (getMode(rendingGale) == Mode.FLIGHT) {
				attemptFlight(player);
				spawnFlightParticles(player.level, player.getX(), player.getY() + player.getEyeHeight(), player.getZ(), player);
			} else if (getMode(rendingGale) == Mode.PUSH) {
				doRadialPush(player.level, player.getX(), player.getY(), player.getZ(), player, false);
			} else if (getMode(rendingGale) == Mode.PULL) {
				doRadialPush(player.level, player.getX(), player.getY(), player.getZ(), player, true);
			}
			if (!player.level.isClientSide) {
				setFeatherCount(rendingGale, Math.max(0, getFeatherCount(rendingGale) - getChargeCost()), false);
			}
		}
	}

	private void spawnBolt(ItemStack rendingGale, Player player) {
		HitResult rayTraceResult = player.pick(getBoltTargetRange(), 1, true);
		if (rayTraceResult.getType() == HitResult.Type.BLOCK) {
			BlockHitResult blockRayTraceResult = (BlockHitResult) rayTraceResult;
			BlockPos pos = blockRayTraceResult.getBlockPos();
			int attemptedY = pos.getY();
			if (!player.level.isRainingAt(pos)) {
				attemptedY++;
			}
			if (!player.level.isClientSide && player.level.isRainingAt(new BlockPos(pos.getX(), attemptedY, pos.getZ()))) {
				LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(player.level);
				if (bolt != null) {
					bolt.moveTo(pos.getX(), pos.getY(), pos.getZ());
					player.level.addFreshEntity(bolt);
					setFeatherCount(rendingGale, Math.max(0, getFeatherCount(rendingGale) - (getBoltChargeCost())), false);
				}
			}
		}
	}

	@Override
	public void releaseUsing(ItemStack rendingGale, Level world, LivingEntity entityLiving, int timeLeft) {
		if (world.isClientSide) {
			return;
		}
		NBTHelper.putInt(COUNT_TAG, rendingGale, getFeatherCount(rendingGale));
	}

	public boolean hasFlightCharge(ItemStack stack) {
		return getFeatherCount(stack) > 0;
	}

	public int getFeatherCount(ItemStack rendingGale) {
		return getFeatherCount(rendingGale, false);
	}

	private int getFeatherCount(ItemStack rendingGale, boolean isClient) {
		if (isClient) {
			return NBTHelper.getInt(COUNT_TAG, rendingGale);
		}

		return rendingGale.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
				.filter(FilteredItemStackHandler.class::isInstance).map(handler -> ((FilteredItemStackHandler) handler).getTotalAmount(0)).orElse(0);
	}

	public void setFeatherCount(ItemStack stack, int featherCount, boolean updateNBT) {
		stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).filter(FilteredItemStackHandler.class::isInstance)
				.ifPresent(handler -> {
					((FilteredItemStackHandler) handler).setTotalAmount(0, featherCount);
					if (updateNBT) {
						NBTHelper.putInt(COUNT_TAG, stack, featherCount);
					}
				});
	}

	public void doRadialPush(Level world, double posX, double posY, double posZ, @Nullable Player player, boolean pull) {
		//push effect free at the moment, if you restore cost, remember to change this to getFeatherCount
		spawnRadialHurricaneParticles(world, posX, posY, posZ, player, pull);
		if (world.isClientSide) {
			return;
		}

		double lowerX = posX - getRadialPushRadius();
		double lowerY = posY - getRadialPushRadius() / 5D;
		double lowerZ = posZ - getRadialPushRadius();
		double upperX = posX + getRadialPushRadius();
		double upperY = posY + getRadialPushRadius() / 5D;
		double upperZ = posZ + getRadialPushRadius();

		List<Entity> entities = world.getEntitiesOfClass(Entity.class, new AABB(lowerX, lowerY, lowerZ, upperX, upperY, upperZ),
				e -> (e instanceof Mob || e instanceof Projectile));

		for (Entity entity : entities) {
			double distance = getDistanceToEntity(posX, posY, posZ, entity);
			if (distance < getRadialPushRadius()) {
				if (isBlacklistedEntity(entity) || entity.equals(player)) {
					continue;
				}

				Vec3 pushVector;
				if (pull) {
					pushVector = new Vec3(posX - entity.getX(), posY - entity.getY(), posZ - entity.getZ());
				} else {
					pushVector = new Vec3(entity.getX() - posX, entity.getY() - posY, entity.getZ() - posZ);
				}
				pushVector = pushVector.normalize();
				entity.move(MoverType.PLAYER, new Vec3(0.0D, 0.2D, 0.0D));
				entity.move(MoverType.PLAYER, new Vec3(pushVector.x, Math.min(pushVector.y, 0.1D) * 1.5D, pushVector.z));
			}
		}
	}

	private boolean isBlacklistedEntity(Entity entity) {
		if (entity.getType().getRegistryName() == null) {
			return false;
		}

		String entityName = entity.getType().getRegistryName().toString();
		return isBlacklistedLivingEntity(entity, entityName) || Settings.COMMON.items.rendingGale.canPushProjectiles.get() && isBlacklistedProjectile(entity, entityName);
	}

	private boolean isBlacklistedProjectile(Entity entity, String entityName) {
		return entity instanceof Projectile && Settings.COMMON.items.rendingGale.pushableProjectilesBlacklist.get().contains(entityName);
	}

	private boolean isBlacklistedLivingEntity(Entity entity, String entityName) {
		return entity instanceof Mob && Settings.COMMON.items.rendingGale.pushableEntitiesBlacklist.get().contains(entityName);
	}

	private float getDistanceToEntity(double posX, double posY, double posZ, Entity entityIn) {
		float f = (float) (posX - entityIn.getX());
		float f1 = (float) (posY - entityIn.getY());
		float f2 = (float) (posZ - entityIn.getZ());
		return Mth.sqrt(f * f + f1 * f1 + f2 * f2);
	}

	private void spawnFlightParticles(Level level, double x, double y, double z, Player player) {
		Vec3 lookVector = player.getLookAngle();

		BlockParticleOption blockParticleData = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SNOW_BLOCK.defaultBlockState());

		//spawn a whole mess of particles every tick.
		for (int i = 0; i < 8; ++i) {
			float randX = 10F * (level.random.nextFloat() - 0.5F);
			float randY = 10F * (level.random.nextFloat() - 0.5F);
			float randZ = 10F * (level.random.nextFloat() - 0.5F);

			level.addParticle(blockParticleData, x + randX + lookVector.x * 20, y + randY + lookVector.y * 20, z + randZ + lookVector.z * 20, -lookVector.x * 5, -lookVector.y * 5, -lookVector.z * 5);
		}
	}

	private void spawnRadialHurricaneParticles(Level world, double posX, double posY, double posZ, @Nullable Player player, boolean pull) {
		BlockParticleOption blockParticleData = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SNOW_BLOCK.defaultBlockState());

		//spawn a whole mess of particles every tick.
		for (int i = 0; i < 3; ++i) {
			float randX = world.random.nextFloat() - 0.5F;
			float randZ = world.random.nextFloat() - 0.5F;
			float motX = randX * 10F;
			float motZ = randZ * 10F;
			if (pull) {
				randX *= 10F;
				randZ *= 10F;
				motX *= -1F;
				motZ *= -1F;
			}

			double posYAdjusted = player == null ? posY : (posY + player.getEyeHeight()) - (player.getBbHeight() / 2);

			world.addParticle(blockParticleData, posX + randX, posYAdjusted, posZ + randZ, motX, 0.0D, motZ);
		}
	}

	public int getFeatherCountClient(ItemStack rendingGale, Player player) {
		int featherCount = getFeatherCount(rendingGale, true);
		Mode mode = getMode(rendingGale);
		int ticksInUse = getUseDuration(rendingGale) - player.getUseItemRemainingTicks();
		if (player.isUsingItem()) {
			featherCount = Math.max(0, featherCount - (mode == Mode.BOLT ? getBoltChargeCost() * (ticksInUse / 8) : (getChargeCost() * ticksInUse)));
		}
		return featherCount;
	}

	public enum Mode implements StringRepresentable {
		FLIGHT, PUSH, PULL, BOLT;

		@Override
		public String getSerializedName() {
			return name();
		}

		public Mode next() {
			return VALUES[(ordinal() + 1) % VALUES.length];
		}

		public Mode previous() {
			return VALUES[Math.floorMod(ordinal() - 1, VALUES.length)];
		}

		private static final Mode[] VALUES;

		static {
			ImmutableMap.Builder<String, Mode> builder = new ImmutableMap.Builder<>();
			for (Mode value : Mode.values()) {
				builder.put(value.getSerializedName(), value);
			}
			VALUES = values();
		}
	}
}
