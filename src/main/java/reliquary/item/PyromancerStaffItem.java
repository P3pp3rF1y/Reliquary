package reliquary.item;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import reliquary.init.ModDataComponents;
import reliquary.item.util.IScrollableItem;
import reliquary.reference.Config;
import reliquary.util.RandHelper;
import reliquary.util.TooltipBuilder;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PyromancerStaffItem extends ChargeableItem implements IScrollableItem {
	private static final int EFFECT_COOLDOWN = 2;
	private static final int INVENTORY_SEARCH_COOLDOWN = EFFECT_COOLDOWN * 5;
	public static final int BLAZE_POWDER_SLOT = 0;
	public static final int FIRE_CHARGE_SLOT = 1;

	public PyromancerStaffItem() {
		super(new Properties().stacksTo(1));
	}

	@Override
	public MutableComponent getName(ItemStack stack) {
		return super.getName(stack).withStyle(ChatFormatting.RED);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int i, boolean f) {
		if (level.isClientSide() || !(entity instanceof Player player) || player.isSpectator() || level.getGameTime() % EFFECT_COOLDOWN != 0) {
			return;
		}

		doFireballAbsorbEffect(stack, player);

		if (!isEnabled(stack)) {
			doExtinguishEffect(player);
		} else {
			scanForFireChargeAndBlazePowder(stack, player);
		}
	}

	@Override
	protected void addMoreInformation(ItemStack staff, @Nullable HolderLookup.Provider registries, TooltipBuilder tooltipBuilder) {
		AtomicInteger charges = new AtomicInteger(0);
		AtomicInteger blaze = new AtomicInteger(0);
		runOnHandler(staff, handler -> {
			charges.set(handler.getCountInSlot(FIRE_CHARGE_SLOT));
			blaze.set(handler.getCountInSlot(BLAZE_POWDER_SLOT));
		});
		tooltipBuilder.charge(this, ".tooltip.charges", charges.get());
		tooltipBuilder.charge(this, ".tooltip.blaze", blaze.get());
		tooltipBuilder.description(this, ".tooltip.controls");
		if (isEnabled(staff)) {
			tooltipBuilder.absorbActive(Items.BLAZE_POWDER.getName(new ItemStack(Items.BLAZE_POWDER)).getString() + " & " + Items.FIRE_CHARGE.getName(new ItemStack(Items.FIRE_CHARGE)).getString());
		} else {
			tooltipBuilder.absorb();
		}
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return true;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity livingEntity) {
		return 11;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BLOCK;
	}

	public Mode getMode(ItemStack stack) {
		return stack.getOrDefault(ModDataComponents.PYROMANCER_STAFF_MODE, Mode.BLAZE);
	}

	private void setMode(ItemStack stack, Mode mode) {
		stack.set(ModDataComponents.PYROMANCER_STAFF_MODE, mode);
	}

	private void cycleMode(ItemStack stack, boolean next) {
		setMode(stack, next ? getMode(stack).next() : getMode(stack).previous());
	}

	@Override
	public InteractionResult onMouseScrolled(ItemStack stack, Player player, double scrollDelta) {
		if (player.level().isClientSide) {
			return InteractionResult.PASS;
		}
		cycleMode(stack, scrollDelta > 0);
		return InteractionResult.SUCCESS;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (player.isShiftKeyDown()) {
			super.use(level, player, hand);
		} else {
			if (getMode(stack) == Mode.BLAZE) {
				player.swing(hand);
				shootBlazeFireball(player, stack);
			} else if (getMode(stack) == Mode.FIRE_CHARGE) {
				player.swing(hand);
				Vec3 lookVec = player.getLookAngle();
				shootGhastFireball(player, stack, lookVec);
			} else {
				player.startUsingItem(hand);
			}
		}
		return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
	}

	private void shootGhastFireball(Player player, ItemStack stack, Vec3 lookVec) {
		if (getFireChargeCount(stack) >= getFireChargeCost()) {
			player.level().levelEvent(player, 1016, player.blockPosition(), 0);
			if (!player.level().isClientSide() && useCharge(stack, FIRE_CHARGE_SLOT, getFireChargeCost())) {
				LargeFireball fireball = new LargeFireball(player.level(), player, lookVec, 1);
				fireball.setPos(fireball.getX() + lookVec.x, player.getY() + player.getEyeHeight(), fireball.getZ() + lookVec.z);
				player.level().addFreshEntity(fireball);
			}
		}
	}

	private void shootBlazeFireball(Player player, ItemStack stack) {
		Vec3 lookVec = player.getLookAngle();
		//blaze fireball!
		if (getBlazePowderCount(stack) >= getBlazePowderCost()) {
			player.level().levelEvent(player, 1018, player.blockPosition(), 0);
			if (!player.level().isClientSide() && useCharge(stack, BLAZE_POWDER_SLOT, getBlazePowderCost())) {
				SmallFireball fireball = new SmallFireball(player.level(), player, lookVec);
				fireball.setPos(fireball.getX() + lookVec.x, player.getY() + player.getEyeHeight(), fireball.getZ() + lookVec.z);
				player.level().addFreshEntity(fireball);
			}
		}
	}

	@Override
	public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
		if (!(livingEntity instanceof Player)) {
			return;
		}
		if (getMode(stack) == Mode.ERUPTION && getBlazePowderCount(stack) > 0) {
			Player player = (Player) livingEntity;
			HitResult rayTraceResult = player.pick(12, 1, true);

			if (rayTraceResult.getType() == HitResult.Type.BLOCK) {
				remainingUseDuration -= 1;
				remainingUseDuration = getUseDuration(stack, livingEntity) - remainingUseDuration;

				BlockHitResult blockRayTraceResult = (BlockHitResult) rayTraceResult;
				doEruptionAuxEffects(player, blockRayTraceResult.getBlockPos().getX(), blockRayTraceResult.getBlockPos().getY(), blockRayTraceResult.getBlockPos().getZ());
				if (remainingUseDuration % 10 == 0 && removeItemFromInternalStorage(stack, BLAZE_POWDER_SLOT, getBlazePowderCost(), player.level().isClientSide, player)) {
					doEruptionEffect(player, blockRayTraceResult.getBlockPos().getX(), blockRayTraceResult.getBlockPos().getY(), blockRayTraceResult.getBlockPos().getZ());
				}
			}
		}
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		if (player == null) {
			return InteractionResult.PASS;
		}

		ItemStack stack = player.getItemInHand(context.getHand());
		if (getMode(stack) == Mode.FLINT_AND_STEEL) {
			return Items.FLINT_AND_STEEL.useOn(new UseOnContext(context.getLevel(), player, context.getHand(), new ItemStack(Items.FLINT_AND_STEEL), context.getHitResult()));
		}
		return InteractionResult.PASS;
	}

	private void doEruptionAuxEffects(Player player, int soundX, int soundY, int soundZ) {
		player.level().playLocalSound(soundX + 0.5D, soundY + 0.5D, soundZ + 0.5D, SoundEvents.GHAST_SHOOT, SoundSource.NEUTRAL, 0.2F, 0.03F + (0.07F * player.level().random.nextFloat()), false);
		spawnLavaParticles(player, soundX, soundY, soundZ);
		spawnFlameParticles(player, soundX, soundY, soundZ);
	}

	private void spawnFlameParticles(Player player, int soundX, int soundY, int soundZ) {
		for (int particleCount = 0; particleCount < 6; ++particleCount) {
			double randX = soundX + 0.5D + (player.level().random.nextFloat() - 0.5F) * 5D;
			double randZ = soundZ + 0.5D + (player.level().random.nextFloat() - 0.5F) * 5D;
			if (Math.abs(randX - (soundX + 0.5D)) < 4.0D && Math.abs(randZ - (soundZ + 0.5D)) < 4.0D) {
				player.level().addParticle(ParticleTypes.FLAME, randX, soundY + 1D, randZ, player.level().random.nextGaussian() * 0.2D, player.level().random.nextGaussian() * 0.2D, player.level().random.nextGaussian() * 0.2D);
			}
		}
		for (int particleCount = 0; particleCount < 8; ++particleCount) {
			double randX = soundX + 0.5D + (player.level().random.nextFloat() - 0.5F) * 5D / 2D;
			double randZ = soundZ + 0.5D + (player.level().random.nextFloat() - 0.5F) * 5D / 2D;
			if (Math.abs(randX - (soundX + 0.5D)) < 4.0D && Math.abs(randZ - (soundZ + 0.5D)) < 4.0D) {
				player.level().addParticle(ParticleTypes.FLAME, randX, soundY + 1D, randZ, player.level().random.nextGaussian() * 0.2D, player.level().random.nextGaussian() * 0.2D, player.level().random.nextGaussian() * 0.2D);
			}
		}
	}

	private void spawnLavaParticles(Player player, int soundX, int soundY, int soundZ) {
		for (int particleCount = 0; particleCount < 2; ++particleCount) {
			double randX = (soundX + 0.5D) + (player.level().random.nextFloat() - 0.5F) * 5D;
			double randZ = (soundZ + 0.5D) + (player.level().random.nextFloat() - 0.5F) * 5D;
			if (Math.abs(randX - (soundX + 0.5D)) < 4.0D && Math.abs(randZ - (soundZ + 0.5D)) < 4.0D) {
				player.level().addParticle(ParticleTypes.LAVA, randX, soundY + 1D, randZ, 0D, 0D, 0D);
			}
		}
		for (int particleCount = 0; particleCount < 4; ++particleCount) {
			double randX = soundX + 0.5D + (player.level().random.nextFloat() - 0.5F) * 5D / 2D;
			double randZ = soundZ + 0.5D + (player.level().random.nextFloat() - 0.5F) * 5D / 2D;
			if (Math.abs(randX - (soundX + 0.5D)) < 4.0D && Math.abs(randZ - (soundZ + 0.5D)) < 4.0D) {
				player.level().addParticle(ParticleTypes.LAVA, randX, soundY + 1D, randZ, 0D, 0D, 0D);
			}
		}
	}

	private void doEruptionEffect(Player player, int x, int y, int z) {
		double lowerX = x - 5D + 0.5D;
		double lowerZ = z - 5D + 0.5D;
		double upperX = x + 5D + 0.5D;
		double upperY = y + 5D;
		double upperZ = z + 5D + 0.5D;
		List<Mob> entities = player.level().getEntitiesOfClass(Mob.class, new AABB(lowerX, y, lowerZ, upperX, upperY, upperZ));

		entities.stream().filter(e -> !e.is(player)).forEach(e -> {
			e.igniteForSeconds(40);
			if (!e.fireImmune()) {
				e.hurt(player.damageSources().playerAttack(player), 4F);
			}
		});
	}

	private void scanForFireChargeAndBlazePowder(ItemStack staff, Player player) {
		if (player.level().getGameTime() % INVENTORY_SEARCH_COOLDOWN != 0) {
			return;
		}

		int currentFireChargeCount = getFireChargeCount(staff);
		consumeAndCharge(staff, FIRE_CHARGE_SLOT, player, getFireChargeLimit() - currentFireChargeCount, 1, 16);

		int currentBlazePowderCount = getBlazePowderCount(staff);
		consumeAndCharge(staff, BLAZE_POWDER_SLOT, player, getBlazePowderLimit() - currentBlazePowderCount, 1, 16);
	}

	@Override
	public void addStoredCharge(ItemStack containerStack, int slot, int chargeToAdd, @Nullable ItemStack chargeStack) {
		if (chargeStack == null || chargeStack.isEmpty()) {
			return;
		}
		runOnHandler(containerStack, handler -> handler.insertItemOrAddIntoNewSlotIfNoStackMatches(chargeStack));
	}

	@Override
	protected void extractStoredCharge(ItemStack containerStack, int slot, int chargeToExtract) {
		runOnHandler(containerStack, h -> h.extractItem(slot, chargeToExtract, false));
	}

	@Override
	public int getStoredCharge(ItemStack containerStack, int slot) {
		if (slot == BLAZE_POWDER_SLOT) {
			return getBlazePowderCount(containerStack);
		} else if (slot == FIRE_CHARGE_SLOT) {
			return getFireChargeCount(containerStack);
		}

		return 0;
	}

	private int getFireChargeWorth() {
		return Config.COMMON.items.pyromancerStaff.fireChargeWorth.get();
	}

	private int getFireChargeCost() {
		return Config.COMMON.items.pyromancerStaff.fireChargeCost.get();
	}

	private int getFireChargeLimit() {
		return Config.COMMON.items.pyromancerStaff.fireChargeLimit.get();
	}

	private int getBlazePowderWorth() {
		return Config.COMMON.items.pyromancerStaff.blazePowderWorth.get();
	}

	private int getBlazePowderCost() {
		return Config.COMMON.items.pyromancerStaff.blazePowderCost.get();
	}

	private int getBlazePowderLimit() {
		return Config.COMMON.items.pyromancerStaff.blazePowderLimit.get();
	}

	private int getBlazeAbsorbWorth() {
		return Config.COMMON.items.pyromancerStaff.blazeAbsorbWorth.get();
	}

	private int getGhastAbsorbWorth() {
		return Config.COMMON.items.pyromancerStaff.ghastAbsorbWorth.get();
	}

	private void doExtinguishEffect(Player player) {
		if (player.isOnFire()) {
			player.clearFire();
		}
		BlockPos.betweenClosed(player.blockPosition().offset(-3, -3, -3), player.blockPosition().offset(3, 3, 3)).forEach(pos -> {
			Block block = player.level().getBlockState(pos).getBlock();
			if (block instanceof BaseFireBlock) {
				player.level().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
				player.level().playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + RandHelper.getRandomMinusOneToOne(player.level().random) * 0.8F);
			}
		});
	}

	private void doFireballAbsorbEffect(ItemStack stack, Player player) {
		if (player.level().isClientSide) {
			return;
		}
		absorbGhastFireballs(stack, player);
		absorbBlazeFireballs(stack, player);
	}

	private void absorbBlazeFireballs(ItemStack stack, Player player) {
		List<SmallFireball> blazeFireballs = player.level().getEntitiesOfClass(SmallFireball.class, player.getBoundingBox().inflate(3));
		for (SmallFireball fireball : blazeFireballs) {
			if (fireball.getOwner() == player) {
				continue;
			}
			if (addPartialCharge(stack, BLAZE_POWDER_SLOT, getBlazeAbsorbWorth())) {
				for (int particles = 0; particles < 4; particles++) {
					player.level().addParticle(DustParticleOptions.REDSTONE, fireball.getX(), fireball.getY(), fireball.getZ(), 0.0D, 1.0D, 1.0D);
				}
				player.level().playLocalSound(fireball.getX(), fireball.getY(), fireball.getZ(), SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + RandHelper.getRandomMinusOneToOne(player.level().random) * 0.8F, false);
			}
			fireball.discard();
		}
	}

	private void absorbGhastFireballs(ItemStack stack, Player player) {
		List<LargeFireball> ghastFireballs = player.level().getEntitiesOfClass(LargeFireball.class, player.getBoundingBox().inflate(4));
		for (LargeFireball fireball : ghastFireballs) {
			if (fireball.getOwner() != player) {
				if (addPartialCharge(stack, FIRE_CHARGE_SLOT, getGhastAbsorbWorth())) {
					player.level().playLocalSound(fireball.getX(), fireball.getY(), fireball.getZ(), SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + RandHelper.getRandomMinusOneToOne(player.level().random) * 0.8F, false);
				}
				fireball.discard();
			}
		}
	}

	@Override
	protected int getContainerInitialSize() {
		return 2;
	}

	@Override
	protected int getSlotWorth(int slot) {
		return slot == BLAZE_POWDER_SLOT ? getBlazePowderWorth() : getFireChargeWorth();
	}

	@Override
	protected int getContainerSlotLimit(int slot) {
		if (slot == BLAZE_POWDER_SLOT) {
			return Config.COMMON.items.pyromancerStaff.blazePowderLimit.get();
		} else {
			return Config.COMMON.items.pyromancerStaff.fireChargeLimit.get();
		}
	}

	@Override
	protected boolean isItemValidForContainerSlot(ItemStack containerStack, int slot, ItemStack stack) {
		if (stack.isEmpty()) {
			return true;
		}
		if (slot == BLAZE_POWDER_SLOT) {
			return stack.is(Items.BLAZE_POWDER);
		} else {
			return stack.is(Items.FIRE_CHARGE);
		}
	}

	public int getBlazePowderCount(ItemStack staff) {
		return getFromHandler(staff, handler -> handler.getCountInSlot(BLAZE_POWDER_SLOT));
	}

	public int getFireChargeCount(ItemStack staff) {
		return getFromHandler(staff, handler -> handler.getCountInSlot(FIRE_CHARGE_SLOT));
	}

	public enum Mode implements StringRepresentable {
		BLAZE, FIRE_CHARGE, ERUPTION, FLINT_AND_STEEL;

		public static final Codec<Mode> CODEC = StringRepresentable.fromEnum(Mode::values);
		public static final StreamCodec<FriendlyByteBuf, Mode> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(Mode.class);

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
