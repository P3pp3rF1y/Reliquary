package reliquary.item;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import reliquary.entity.EnderStaffProjectile;
import reliquary.init.ModBlocks;
import reliquary.init.ModDataComponents;
import reliquary.item.util.IScrollableItem;
import reliquary.reference.Config;
import reliquary.util.TooltipBuilder;

import javax.annotation.Nullable;

public class EnderStaffItem extends ChargeableItem implements IScrollableItem {
	public EnderStaffItem() {
		super(new Properties().stacksTo(1).setNoRepair().rarity(Rarity.EPIC));
	}

	private int getEnderStaffPearlCost() {
		return Config.COMMON.items.enderStaff.enderPearlCastCost.get();
	}

	private int getEnderStaffNodeWarpCost() {
		return Config.COMMON.items.enderStaff.enderPearlNodeWarpCost.get();
	}

	private int getEnderPearlWorth() {
		return Config.COMMON.items.enderStaff.enderPearlWorth.get();
	}

	private int getEnderPearlLimit() {
		return Config.COMMON.items.enderStaff.enderPearlLimit.get();
	}

	private int getNodeWarpCastTime() {
		return Config.COMMON.items.enderStaff.nodeWarpCastTime.get();
	}

	public Mode getMode(ItemStack stack) {
		return stack.getOrDefault(ModDataComponents.ENDER_STAFF_MODE, Mode.CAST);
	}

	private void setMode(ItemStack stack, Mode mode) {
		stack.set(ModDataComponents.ENDER_STAFF_MODE, mode);
	}

	private void cycleMode(ItemStack stack, boolean next) {
		if (next) {
			setMode(stack, getMode(stack).next());
		} else {
			setMode(stack, getMode(stack).previous());
		}
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
	public void inventoryTick(ItemStack staff, Level level, Entity entity, int itemSlot, boolean isSelected) {
		if (level.isClientSide || !(entity instanceof Player player) || player.isSpectator() || level.getGameTime() % 10 != 0) {
			return;
		}

		if (!isEnabled(staff)) {
			return;
		}

		int pearlCharge = getPearlCount(staff);
		consumeAndCharge(staff, 0, player, getEnderPearlLimit() - pearlCharge, 1, 16);
	}

	@Override
	public void addStoredCharge(ItemStack staff, int slot, int chargeToAdd, @Nullable ItemStack chargeStack) {
		runOnHandler(staff, handler -> handler.setStackInSlot(0, new ItemStack(Items.ENDER_PEARL, getPearlCount(staff) + chargeToAdd)));
	}

	@Override
	public int getStoredCharge(ItemStack staff, int slot) {
		return getPearlCount(staff);
	}

	@Override
	protected int getSlotWorth(int slot) {
		return slot == 0 ? getEnderPearlWorth() : 0;
	}

	@Override
	protected boolean isItemValidForContainerSlot(ItemStack containerStack, int slot, ItemStack stack) {
		return stack.isEmpty() || stack.is(Tags.Items.ENDER_PEARLS);
	}

	@Override
	protected int getContainerSlotLimit(int slot) {
		return getEnderPearlLimit();
	}

	public int getPearlCount(ItemStack staff) {
		return getFromHandler(staff, handler -> handler.getCountInSlot(0));
	}

	@Override
	public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
		if (!(livingEntity instanceof Player player)) {
			return;
		}

		for (int particles = 0; particles < 2; particles++) {
			level.addParticle(ParticleTypes.PORTAL, player.getX(), player.getEyeY(), player.getZ(), player.level().random.nextGaussian(), player.level().random.nextGaussian(), player.level().random.nextGaussian());
		}
		if (remainingUseDuration == 1) {
			player.releaseUsingItem();
		}
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BLOCK;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity livingEntity) {
		return getMode(stack) == Mode.NODE_WARP ? getNodeWarpCastTime() : 0;
	}

	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
		if (!(livingEntity instanceof Player player)) {
			return;
		}

		if (timeLeft == 1) {
			doWraithNodeWarpCheck(stack, player.level(), player);
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!player.isShiftKeyDown()) {
			if (getMode(stack) == Mode.CAST || getMode(stack) == Mode.LONG_CAST) {
				if (getPearlCount(stack) < getEnderStaffPearlCost() && !player.isCreative()) {
					return new InteractionResultHolder<>(InteractionResult.FAIL, stack);
				}
				shootEnderStaffProjectile(level, player, hand, stack);
			} else {
				player.startUsingItem(hand);
			}
		}
		return super.use(level, player, hand);
	}

	private void shootEnderStaffProjectile(Level level, Player player, InteractionHand hand, ItemStack stack) {
		player.swing(hand);
		player.level().playSound(null, player.blockPosition(), SoundEvents.ENDER_PEARL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
		if (!player.level().isClientSide) {
			EnderStaffProjectile enderStaffProjectile = new EnderStaffProjectile(player.level(), player, getMode(stack) != Mode.LONG_CAST);
			enderStaffProjectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
			player.level().addFreshEntity(enderStaffProjectile);
			if (!player.isCreative()) {
				useCharge(stack, FIRST_SLOT, getEnderStaffPearlCost());
			}
		}
	}

	private void doWraithNodeWarpCheck(ItemStack stack, Level level, Player player) {
		if (!stack.has(ModDataComponents.WARP_DIMENSION) || !stack.has(ModDataComponents.WARP_POSITION) || (getPearlCount(stack) < getEnderStaffNodeWarpCost() && !player.isCreative())) {
			return;
		}

		ResourceLocation wraithNodeDimension = Preconditions.checkNotNull(stack.get(ModDataComponents.WARP_DIMENSION));
		BlockPos wraithNodePos = Preconditions.checkNotNull(stack.get(ModDataComponents.WARP_POSITION));
		if (!player.level().dimension().location().equals(wraithNodeDimension) && player.level() instanceof ServerLevel serverLevel) {
			ServerLevel destination = serverLevel.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, wraithNodeDimension));
			if (destination != null && canTeleport(destination, wraithNodePos)) {
				teleportToDimension(player, destination, wraithNodePos);
				if (!player.isCreative() && !player.level().isClientSide) {
					useCharge(stack, FIRST_SLOT, getEnderStaffNodeWarpCost());
				}
			}
		} else {
			if (canTeleport(level, wraithNodePos)) {
				teleportPlayer(level, wraithNodePos, player);
				if (!player.isCreative() && !player.level().isClientSide) {
					useCharge(stack, FIRST_SLOT, getEnderStaffNodeWarpCost());
				}
			}
		}
	}

	private static void teleportToDimension(Player player, ServerLevel destination, BlockPos wraithNodePos) {
		player.changeDimension(new DimensionTransition(destination, wraithNodePos.above().getBottomCenter(), Vec3.ZERO,
				player.getYRot(), player.getXRot(), DimensionTransition.DO_NOTHING));
	}

	private static boolean canTeleport(Level level, BlockPos pos) {
		if (level.getBlockState(pos).getBlock() != ModBlocks.WRAITH_NODE.get()) {
			return false;
		}

		BlockPos up = pos.above();
		return level.isEmptyBlock(up) && level.isEmptyBlock(up.above());
	}

	private static void teleportPlayer(Level level, BlockPos pos, Player player) {
		player.teleportTo(pos.getX() + 0.5, pos.getY() + 0.875, pos.getZ() + 0.5);
		player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0f, 1.0f);
		for (int particles = 0; particles < 2; particles++) {
			level.addParticle(ParticleTypes.PORTAL, player.getX(), player.getEyeY(), player.getZ(), level.random.nextGaussian(), level.random.nextGaussian(), level.random.nextGaussian());
		}
	}

	@Override
	protected void addMoreInformation(ItemStack staff, @Nullable HolderLookup.Provider registries, TooltipBuilder tooltipBuilder) {
		tooltipBuilder.description(this, ".tooltip2");
		tooltipBuilder.charge(this, ".tooltip.charge", getPearlCount(staff));

		if (staff.has(ModDataComponents.WARP_POSITION)) {
			ResourceLocation dimension = staff.getOrDefault(ModDataComponents.WARP_DIMENSION, Level.OVERWORLD.location());
			BlockPos pos = staff.getOrDefault(ModDataComponents.WARP_POSITION, BlockPos.ZERO);
			tooltipBuilder.data(this, ".tooltip.position", pos.getX(), pos.getY(), pos.getZ(), dimension);
		} else {
			tooltipBuilder.description(this, ".tooltip.position.nowhere");
		}

		if (isEnabled(staff)) {
			tooltipBuilder.absorbActive(Items.ENDER_PEARL.getName(new ItemStack(Items.ENDER_PEARL)).getString());
		} else {
			tooltipBuilder.absorb();
		}
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return true;
	}

	@Override
	public InteractionResult useOn(UseOnContext itemUseContext) {
		ItemStack stack = itemUseContext.getItemInHand();
		Level level = itemUseContext.getLevel();
		BlockPos pos = itemUseContext.getClickedPos();

		// if right clicking on a wraith node, bind the eye to that wraith node.
		if (level.getBlockState(pos).getBlock() == ModBlocks.WRAITH_NODE.get()) {
			setWraithNode(stack, pos, level.dimension().location());

			Player player = itemUseContext.getPlayer();
			if (player != null) {
				player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0f, 1.0f);
			}
			for (int particles = 0; particles < 12; particles++) {
				level.addParticle(ParticleTypes.PORTAL, pos.getX() + level.random.nextDouble(), pos.getY() + level.random.nextDouble(), pos.getZ() + level.random.nextDouble(), level.random.nextGaussian(), level.random.nextGaussian(), level.random.nextGaussian());
			}
			return InteractionResult.SUCCESS;
		} else {
			return InteractionResult.PASS;
		}
	}

	private void setWraithNode(ItemStack eye, BlockPos pos, ResourceLocation dimension) {
		eye.set(ModDataComponents.WARP_DIMENSION, dimension);
		eye.set(ModDataComponents.WARP_POSITION, pos);
	}

	public enum Mode implements StringRepresentable {
		CAST("cast"),
		LONG_CAST("long_cast"),
		NODE_WARP("node_warp");

		public static final Codec<Mode> CODEC = StringRepresentable.fromEnum(Mode::values);
		public static final StreamCodec<FriendlyByteBuf, Mode> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(Mode.class);

		private final String name;

		Mode(String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return name;
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
