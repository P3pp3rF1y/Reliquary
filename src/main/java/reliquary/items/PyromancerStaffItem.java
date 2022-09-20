package reliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import reliquary.items.util.IScrollableItem;
import reliquary.reference.Settings;
import reliquary.util.LanguageHelper;
import reliquary.util.NBTHelper;
import reliquary.util.RandHelper;
import reliquary.util.RegistryHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PyromancerStaffItem extends ToggleableItem implements IScrollableItem {
	private static final String BLAZE_CHARGES_TAG = "blaze";
	private static final int EFFECT_COOLDOWN = 2;
	private static final int INVENTORY_SEARCH_COOLDOWN = EFFECT_COOLDOWN * 5;

	public PyromancerStaffItem() {
		super(new Properties().stacksTo(1));
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity e, int i, boolean f) {
		if (!(e instanceof Player player) || world.getGameTime() % EFFECT_COOLDOWN != 0) {
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
	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack staff, @Nullable Level world, List<Component> tooltip) {
		AtomicInteger charges = new AtomicInteger(0);
		AtomicInteger blaze = new AtomicInteger(0);
		iterateItems(staff, tag -> {
			String itemName = tag.getString(ITEM_NAME_TAG);
			int quantity = tag.getInt(QUANTITY_TAG);

			if (itemName.equals(RegistryHelper.getItemRegistryName(Items.BLAZE_POWDER))) {
				blaze.set(quantity);
			} else if (itemName.equals(RegistryHelper.getItemRegistryName(Items.FIRE_CHARGE))) {
				charges.set(quantity);
			}
		}, () -> false);
		LanguageHelper.formatTooltip(getDescriptionId() + ".tooltip2", Map.of("charges",
				Integer.toString(charges.get()), BLAZE_CHARGES_TAG, Integer.toString(blaze.get())), tooltip);
		if (isEnabled(staff)) {
			LanguageHelper.formatTooltip("tooltip.absorb_active",
					Map.of("item", ChatFormatting.RED + Items.BLAZE_POWDER.getName(new ItemStack(Items.BLAZE_POWDER)).getString()
							+ ChatFormatting.WHITE + " & " + ChatFormatting.RED + Items.FIRE_CHARGE.getName(new ItemStack(Items.FIRE_CHARGE)).getString()), tooltip);
		}

		LanguageHelper.formatTooltip("tooltip.absorb", null, tooltip);
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return true;
	}

	@Override
	public int getUseDuration(ItemStack par1ItemStack) {
		return 11;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BLOCK;
	}

	public Mode getMode(ItemStack stack) {
		return NBTHelper.getEnumConstant(stack, "mode", Mode::valueOf).orElse(Mode.BLAZE);
	}

	private void setMode(ItemStack stack, Mode mode) {
		NBTHelper.putString("mode", stack, mode.getSerializedName());
	}

	private void cycleMode(ItemStack stack, boolean next) {
		setMode(stack, next ? getMode(stack).next() : getMode(stack).previous());
	}

	@Override
	public InteractionResult onMouseScrolled(ItemStack stack, Player player, double scrollDelta) {
		if (player.level.isClientSide) {
			return InteractionResult.PASS;
		}
		cycleMode(stack, scrollDelta > 0);
		return InteractionResult.SUCCESS;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (player.isShiftKeyDown()) {
			super.use(world, player, hand);
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
		if (removeItemFromInternalStorage(stack, Items.FIRE_CHARGE, getFireChargeCost(), player.level.isClientSide, player)) {
			player.level.levelEvent(player, 1016, player.blockPosition(), 0);
			LargeFireball fireball = new LargeFireball(player.level, player, lookVec.x, lookVec.y, lookVec.z, 1);
			fireball.xPower = lookVec.x / 3;
			fireball.yPower = lookVec.y / 3;
			fireball.zPower = lookVec.z / 3;
			fireball.setPos(fireball.getX() + lookVec.x, player.getY() + player.getEyeHeight(), fireball.getZ() + lookVec.z);
			player.level.addFreshEntity(fireball);

		}
	}

	private void shootBlazeFireball(Player player, ItemStack stack) {
		Vec3 lookVec = player.getLookAngle();
		//blaze fireball!
		if (removeItemFromInternalStorage(stack, Items.BLAZE_POWDER, getBlazePowderCost(), player.level.isClientSide, player)) {
			player.level.levelEvent(player, 1018, player.blockPosition(), 0);
			SmallFireball fireball = new SmallFireball(player.level, player, lookVec.x, lookVec.y, lookVec.z);
			fireball.xPower = lookVec.x / 3;
			fireball.yPower = lookVec.y / 3;
			fireball.zPower = lookVec.z / 3;
			fireball.setPos(fireball.getX() + lookVec.x, player.getY() + player.getEyeHeight(), fireball.getZ() + lookVec.z);
			player.level.addFreshEntity(fireball);
		}
	}

	@Override
	public void onUsingTick(ItemStack stack, LivingEntity entity, int count) {
		if (!(entity instanceof Player)) {
			return;
		}
		if (getMode(stack) == Mode.ERUPTION) {
			Player player = (Player) entity;
			HitResult rayTraceResult = player.pick(12, 1, true);

			if (rayTraceResult.getType() == HitResult.Type.BLOCK) {
				count -= 1;
				count = getUseDuration(stack) - count;

				BlockHitResult blockRayTraceResult = (BlockHitResult) rayTraceResult;
				doEruptionAuxEffects(player, blockRayTraceResult.getBlockPos().getX(), blockRayTraceResult.getBlockPos().getY(), blockRayTraceResult.getBlockPos().getZ());
				if (count % 10 == 0 && removeItemFromInternalStorage(stack, Items.BLAZE_POWDER, getBlazePowderCost(), player.level.isClientSide, player)) {
					doEruptionEffect(player, blockRayTraceResult.getBlockPos().getX(), blockRayTraceResult.getBlockPos().getY(), blockRayTraceResult.getBlockPos().getZ());
				}
			}
		}
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		Direction face = context.getClickedFace();
		Level level = context.getLevel();

		if (player == null) {
			return InteractionResult.PASS;
		}

		ItemStack stack = player.getItemInHand(context.getHand());
		if (getMode(stack) == Mode.FLINT_AND_STEEL) {
			BlockPos placeFireAt = context.getClickedPos().relative(face);
			if (!player.mayUseItemAt(placeFireAt, face, stack)) {
				return InteractionResult.PASS;
			} else {
				if (level.isEmptyBlock(placeFireAt)) {
					level.playLocalSound(placeFireAt.getX() + 0.5D, placeFireAt.getY() + 0.5D, placeFireAt.getZ() + 0.5D, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.4F + 0.8F, false);
					level.setBlockAndUpdate(placeFireAt, Blocks.FIRE.defaultBlockState());
				}
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.PASS;
	}

	private void doEruptionAuxEffects(Player player, int soundX, int soundY, int soundZ) {
		player.level.playLocalSound(soundX + 0.5D, soundY + 0.5D, soundZ + 0.5D, SoundEvents.GHAST_SHOOT, SoundSource.NEUTRAL, 0.2F, 0.03F + (0.07F * player.level.random.nextFloat()), false);

		for (int particleCount = 0; particleCount < 2; ++particleCount) {
			double randX = (soundX + 0.5D) + (player.level.random.nextFloat() - 0.5F) * 5D;
			double randZ = (soundZ + 0.5D) + (player.level.random.nextFloat() - 0.5F) * 5D;
			if (Math.abs(randX - (soundX + 0.5D)) < 4.0D && Math.abs(randZ - (soundZ + 0.5D)) < 4.0D) {
				player.level.addParticle(ParticleTypes.LAVA, randX, soundY + 1D, randZ, 0D, 0D, 0D);
			}
		}
		for (int particleCount = 0; particleCount < 4; ++particleCount) {
			double randX = soundX + 0.5D + (player.level.random.nextFloat() - 0.5F) * 5D / 2D;
			double randZ = soundZ + 0.5D + (player.level.random.nextFloat() - 0.5F) * 5D / 2D;
			if (Math.abs(randX - (soundX + 0.5D)) < 4.0D && Math.abs(randZ - (soundZ + 0.5D)) < 4.0D) {
				player.level.addParticle(ParticleTypes.LAVA, randX, soundY + 1D, randZ, 0D, 0D, 0D);
			}
		}
		for (int particleCount = 0; particleCount < 6; ++particleCount) {
			double randX = soundX + 0.5D + (player.level.random.nextFloat() - 0.5F) * 5D;
			double randZ = soundZ + 0.5D + (player.level.random.nextFloat() - 0.5F) * 5D;
			if (Math.abs(randX - (soundX + 0.5D)) < 4.0D && Math.abs(randZ - (soundZ + 0.5D)) < 4.0D) {
				player.level.addParticle(ParticleTypes.FLAME, randX, soundY + 1D, randZ, player.level.random.nextGaussian() * 0.2D, player.level.random.nextGaussian() * 0.2D, player.level.random.nextGaussian() * 0.2D);
			}
		}
		for (int particleCount = 0; particleCount < 8; ++particleCount) {
			double randX = soundX + 0.5D + (player.level.random.nextFloat() - 0.5F) * 5D / 2D;
			double randZ = soundZ + 0.5D + (player.level.random.nextFloat() - 0.5F) * 5D / 2D;
			if (Math.abs(randX - (soundX + 0.5D)) < 4.0D && Math.abs(randZ - (soundZ + 0.5D)) < 4.0D) {
				player.level.addParticle(ParticleTypes.FLAME, randX, soundY + 1D, randZ, player.level.random.nextGaussian() * 0.2D, player.level.random.nextGaussian() * 0.2D, player.level.random.nextGaussian() * 0.2D);
			}
		}
	}

	private void doEruptionEffect(Player player, int x, int y, int z) {
		double lowerX = x - 5D + 0.5D;
		double lowerZ = z - 5D + 0.5D;
		double upperX = x + 5D + 0.5D;
		double upperY = y + 5D;
		double upperZ = z + 5D + 0.5D;
		List<Mob> entities = player.level.getEntitiesOfClass(Mob.class, new AABB(lowerX, y, lowerZ, upperX, upperY, upperZ));

		entities.stream().filter(e -> !e.is(player)).forEach(e -> {
			e.setSecondsOnFire(40);
			if (!e.fireImmune()) {
				e.hurt(DamageSource.playerAttack(player), 4F);
			}
		});
	}

	private void scanForFireChargeAndBlazePowder(ItemStack staff, Player player) {
		if (player.level.getGameTime() % INVENTORY_SEARCH_COOLDOWN != 0) {
			return;
		}

		int currentFireChargeCount = getInternalStorageItemCount(staff, Items.FIRE_CHARGE);
		consumeAndCharge(player, getFireChargeLimit() - currentFireChargeCount, getFireChargeWorth(), Items.FIRE_CHARGE, 16,
				chargeToAdd -> addItemToInternalStorage(staff, Items.FIRE_CHARGE, chargeToAdd));

		int currentBlazePowderCount = getInternalStorageItemCount(staff, Items.BLAZE_POWDER);
		consumeAndCharge(player, getBlazePowderLimit() - currentBlazePowderCount, getBlazePowderWorth(), Items.BLAZE_POWDER, 16,
				chargeToAdd -> addItemToInternalStorage(staff, Items.BLAZE_POWDER, chargeToAdd));
	}

	private int getFireChargeWorth() {
		return Settings.COMMON.items.pyromancerStaff.fireChargeWorth.get();
	}

	private int getFireChargeCost() {
		return Settings.COMMON.items.pyromancerStaff.fireChargeCost.get();
	}

	private int getFireChargeLimit() {
		return Settings.COMMON.items.pyromancerStaff.fireChargeLimit.get();
	}

	private int getBlazePowderWorth() {
		return Settings.COMMON.items.pyromancerStaff.blazePowderWorth.get();
	}

	private int getBlazePowderCost() {
		return Settings.COMMON.items.pyromancerStaff.blazePowderCost.get();
	}

	private int getBlazePowderLimit() {
		return Settings.COMMON.items.pyromancerStaff.blazePowderLimit.get();
	}

	private int getBlazeAbsorbWorth() {
		return Settings.COMMON.items.pyromancerStaff.blazeAbsorbWorth.get();
	}

	private int getGhastAbsorbWorth() {
		return Settings.COMMON.items.pyromancerStaff.ghastAbsorbWorth.get();
	}

	private void doExtinguishEffect(Player player) {
		if (player.isOnFire()) {
			player.clearFire();
		}
		BlockPos.betweenClosed(player.blockPosition().offset(-3, -3, -3), player.blockPosition().offset(3, 3, 3)).forEach(pos -> {
			Block block = player.level.getBlockState(pos).getBlock();
			if (block == Blocks.FIRE) {
				player.level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
				player.level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + RandHelper.getRandomMinusOneToOne(player.level.random) * 0.8F);
			}
		});
	}

	private void doFireballAbsorbEffect(ItemStack stack, Player player) {
		if (player.level.isClientSide) {
			return;
		}
		absorbGhastFireballs(stack, player);
		absorbBlazeFireballs(stack, player);
	}

	private void absorbBlazeFireballs(ItemStack stack, Player player) {
		List<SmallFireball> blazeFireballs = player.level.getEntitiesOfClass(SmallFireball.class, player.getBoundingBox().inflate(3));
		for (SmallFireball fireball : blazeFireballs) {
			if (fireball.getOwner() == player) {
				continue;
			}
			if (hasSpaceForItem(stack, Items.BLAZE_POWDER, getBlazePowderLimit())) {
				for (int particles = 0; particles < 4; particles++) {
					player.level.addParticle(DustParticleOptions.REDSTONE, fireball.getX(), fireball.getY(), fireball.getZ(), 0.0D, 1.0D, 1.0D);
				}
				player.level.playLocalSound(fireball.getX(), fireball.getY(), fireball.getZ(), SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + RandHelper.getRandomMinusOneToOne(player.level.random) * 0.8F, false);

				addItemToInternalStorage(stack, Items.BLAZE_POWDER, getBlazeAbsorbWorth());
			}
			fireball.discard();
		}
	}

	private void absorbGhastFireballs(ItemStack stack, Player player) {
		List<LargeFireball> ghastFireballs = player.level.getEntitiesOfClass(LargeFireball.class, player.getBoundingBox().inflate(4));
		for (LargeFireball fireball : ghastFireballs) {
			if (fireball.getOwner() != player) {
				if (hasSpaceForItem(stack, Items.FIRE_CHARGE, getFireChargeLimit())) {
					addItemToInternalStorage(stack, Items.FIRE_CHARGE, getGhastAbsorbWorth());
					player.level.playLocalSound(fireball.getX(), fireball.getY(), fireball.getZ(), SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + RandHelper.getRandomMinusOneToOne(player.level.random) * 0.8F, false);
				}
				fireball.discard();
			}
		}
	}

	public enum Mode implements StringRepresentable {
		BLAZE, FIRE_CHARGE, ERUPTION, FLINT_AND_STEEL;

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
