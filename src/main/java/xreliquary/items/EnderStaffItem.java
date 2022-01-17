package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import xreliquary.entities.EnderStaffProjectileEntity;
import xreliquary.init.ModBlocks;
import xreliquary.items.util.FilteredBigItemStack;
import xreliquary.items.util.FilteredItemHandlerProvider;
import xreliquary.items.util.FilteredItemStackHandler;
import xreliquary.items.util.IScrollableItem;
import xreliquary.reference.Settings;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EnderStaffItem extends ToggleableItem implements IScrollableItem {

	private static final String DIMENSION_TAG = "dimensionID";
	private static final String NODE_X_TAG = "nodeX";
	private static final String NODE_Y_TAG = "nodeY";
	private static final String NODE_Z_TAG = "nodeZ";

	public EnderStaffItem() {
		super(new Properties().stacksTo(1).setNoRepair().rarity(Rarity.EPIC));
	}

	private int getEnderStaffPearlCost() {
		return Settings.COMMON.items.enderStaff.enderPearlCastCost.get();
	}

	private int getEnderStaffNodeWarpCost() {
		return Settings.COMMON.items.enderStaff.enderPearlNodeWarpCost.get();
	}

	private int getEnderPearlWorth() {
		return Settings.COMMON.items.enderStaff.enderPearlWorth.get();
	}

	private int getEnderPearlLimit() {
		return Settings.COMMON.items.enderStaff.enderPearlLimit.get();
	}

	private int getNodeWarpCastTime() {
		return Settings.COMMON.items.enderStaff.nodeWarpCastTime.get();
	}

	public Mode getMode(ItemStack stack) {
		return NBTHelper.getEnumConstant(stack, "mode", Mode::fromName).orElse(Mode.CAST);
	}

	private void setMode(ItemStack stack, Mode mode) {
		NBTHelper.putString("mode", stack, mode.getSerializedName());
	}

	private void cycleMode(ItemStack stack, boolean next) {
		if (next) {
			setMode(stack, getMode(stack).next());
		} else {
			setMode(stack, getMode(stack).previous());
		}
	}

	@Override
	public InteractionResult onMouseScrolled(ItemStack stack, LivingEntity entity, double scrollDelta) {
		if (entity.level.isClientSide) {
			return InteractionResult.PASS;
		}
		cycleMode(stack, scrollDelta > 0);
		return InteractionResult.SUCCESS;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return new FilteredItemHandlerProvider(Collections.singletonList(new FilteredItemStackHandler.RemovableStack(
				new FilteredBigItemStack(Items.ENDER_PEARL, Settings.COMMON.items.enderStaff.enderPearlLimit.get(),
						Settings.COMMON.items.enderStaff.enderPearlWorth.get())
				, false)));
	}

	@Override
	public void inventoryTick(ItemStack staff, Level world, Entity entity, int itemSlot, boolean isSelected) {
		if (world.isClientSide || world.getGameTime() % 10 != 0) {
			return;
		}

		if (!(entity instanceof Player player)) {
			return;
		}

		if (!isEnabled(staff)) {
			return;
		}

		int pearlCharge = getPearlCount(staff);
		consumeAndCharge(player, getEnderPearlLimit() - pearlCharge, getEnderPearlWorth(), Items.ENDER_PEARL, 16,
				chargeToAdd -> setPearlCount(staff, pearlCharge + chargeToAdd));
	}

	private void setPearlCount(ItemStack stack, int count) {
		stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(itemHandler -> {
			if (!(itemHandler instanceof FilteredItemStackHandler filteredHandler)) {
				return;
			}
			filteredHandler.setTotalAmount(0, count);
		});
	}

	private int getPearlCount(ItemStack staff) {
		return getPearlCount(staff, false);
	}

	public int getPearlCount(ItemStack staff, boolean isClient) {
		if (isClient) {
			return NBTHelper.getInt("count", staff);
		}

		return staff.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).map(itemHandler -> {
			if (!(itemHandler instanceof FilteredItemStackHandler filteredHandler)) {
				return 0;
			}
			return filteredHandler.getTotalAmount(0);
		}).orElse(0);
	}

	@Override
	public void onUsingTick(ItemStack stack, LivingEntity entityLivingBase, int unadjustedCount) {
		if (!(entityLivingBase instanceof Player player)) {
			return;
		}

		for (int particles = 0; particles < 2; particles++) {
			player.level.addParticle(ParticleTypes.PORTAL, player.getX(), player.getEyeY(), player.getZ(), player.level.random.nextGaussian(), player.level.random.nextGaussian(), player.level.random.nextGaussian());
		}
		if (unadjustedCount == 1) {
			player.releaseUsingItem();
		}
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BLOCK;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return getNodeWarpCastTime();
	}

	@Override
	public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
		if (!(entityLiving instanceof Player player)) {
			return;
		}

		if (timeLeft == 1) {
			doWraithNodeWarpCheck(stack, player.level, player);
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!player.isShiftKeyDown()) {
			if (getMode(stack) == Mode.CAST || getMode(stack) == Mode.LONG_CAST) {
				if (player.swinging || (getPearlCount(stack) < getEnderStaffPearlCost() && !player.isCreative())) {
					return new InteractionResultHolder<>(InteractionResult.FAIL, stack);
				}
				shootEnderStaffProjectile(world, player, hand, stack);
			} else {
				player.startUsingItem(hand);
			}
		}
		return super.use(world, player, hand);
	}

	private void shootEnderStaffProjectile(Level world, Player player, InteractionHand hand, ItemStack stack) {
		player.swing(hand);
		player.level.playSound(null, player.blockPosition(), SoundEvents.ENDER_PEARL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
		if (!player.level.isClientSide) {
			EnderStaffProjectileEntity enderStaffProjectile = new EnderStaffProjectileEntity(player.level, player, getMode(stack) != Mode.LONG_CAST);
			enderStaffProjectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
			player.level.addFreshEntity(enderStaffProjectile);
			if (!player.isCreative()) {
				setPearlCount(stack, getPearlCount(stack) - getEnderStaffPearlCost());
			}
		}
	}

	private void doWraithNodeWarpCheck(ItemStack stack, Level world, Player player) {
		CompoundTag tag = stack.getTag();
		if (tag == null || (getPearlCount(stack) < getEnderStaffNodeWarpCost() && !player.isCreative())) {
			return;
		}

		if (!tag.getString(DIMENSION_TAG).equals(getDimension(world))) {
			if (!world.isClientSide) {
				player.sendMessage(new TextComponent(ChatFormatting.DARK_RED + "Out of range!"), Util.NIL_UUID);
			}
			return;
		}

		BlockPos wraithNodePos = new BlockPos(tag.getInt(NODE_X_TAG + getDimension(world)), tag.getInt(NODE_Y_TAG + getDimension(world)), tag.getInt(NODE_Z_TAG + getDimension(world)));
		if (world.getBlockState(wraithNodePos).getBlock() == ModBlocks.WRAITH_NODE.get() && canTeleport(world, wraithNodePos)) {
			teleportPlayer(world, wraithNodePos, player);
			if (!player.isCreative() && !player.level.isClientSide) {
				setPearlCount(stack, getPearlCount(stack) - getEnderStaffNodeWarpCost());
			}
			return;
		}

		if (tag.contains(DIMENSION_TAG)) {
			tag.remove(DIMENSION_TAG);
			tag.remove(NODE_X_TAG);
			tag.remove(NODE_Y_TAG);
			tag.remove(NODE_Z_TAG);
			if (!world.isClientSide) {
				player.sendMessage(new TextComponent(ChatFormatting.DARK_RED + "Node doesn't exist!"), Util.NIL_UUID);
			} else {
				player.playSound(SoundEvents.ENDERMAN_DEATH, 1.0f, 1.0f);
			}
		}
	}

	private boolean canTeleport(Level world, BlockPos pos) {
		BlockPos up = pos.above();
		return world.isEmptyBlock(up) && world.isEmptyBlock(up.above());
	}

	private void teleportPlayer(Level world, BlockPos pos, Player player) {
		player.teleportTo(pos.getX() + 0.5, pos.getY() + 0.875, pos.getZ() + 0.5);
		player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0f, 1.0f);
		for (int particles = 0; particles < 2; particles++) {
			world.addParticle(ParticleTypes.PORTAL, player.getX(), player.getEyeY(), player.getZ(), world.random.nextGaussian(), world.random.nextGaussian(), world.random.nextGaussian());
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack staff, @Nullable Level world, List<Component> tooltip) {
		//added spacing here to make sure the tooltips didn't come out with weird punctuation derps.
		String charge = Integer.toString(getPearlCount(staff, true));
		String phrase = "Currently bound to ";
		String position = "";
		if (staff.getTag() != null && !staff.getTag().getString(DIMENSION_TAG).equals(getDimension(world))) {
			phrase = "Out of range!";
		} else if (staff.getTag() != null && staff.getTag().contains(NODE_X_TAG + getDimension(world)) && staff.getTag().contains(NODE_Y_TAG + getDimension(world)) && staff.getTag().contains(NODE_Z_TAG + getDimension(world))) {
			position = "X: " + staff.getTag().getInt(NODE_X_TAG + getDimension(world)) + " Y: " + staff.getTag().getInt(NODE_Y_TAG + getDimension(world)) + " Z: " + staff.getTag().getInt(NODE_Z_TAG + getDimension(world));
		} else {
			position = "nowhere.";
		}
		LanguageHelper.formatTooltip(getDescriptionId() + ".tooltip2", Map.of("phrase", phrase, "position", position, "charge", charge), tooltip);
		if (isEnabled(staff)) {
			LanguageHelper.formatTooltip("tooltip.absorb_active", Map.of("item", ChatFormatting.GREEN + Items.ENDER_PEARL.getName(new ItemStack(Items.ENDER_PEARL)).toString()), tooltip);
		}
		LanguageHelper.formatTooltip("tooltip.absorb", null, tooltip);
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return true;
	}

	@Override
	public InteractionResult useOn(UseOnContext itemUseContext) {
		ItemStack stack = itemUseContext.getItemInHand();
		Level world = itemUseContext.getLevel();
		BlockPos pos = itemUseContext.getClickedPos();

		// if right clicking on a wraith node, bind the eye to that wraith node.
		if ((stack.getTag() == null || !(stack.getTag().contains(DIMENSION_TAG))) && world.getBlockState(pos).getBlock() == ModBlocks.WRAITH_NODE.get()) {
			setWraithNode(stack, pos, getDimension(world));

			Player player = itemUseContext.getPlayer();
			if (player != null) {
				player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0f, 1.0f);
			}
			for (int particles = 0; particles < 12; particles++) {
				world.addParticle(ParticleTypes.PORTAL, pos.getX() + world.random.nextDouble(), pos.getY() + world.random.nextDouble(), pos.getZ() + world.random.nextDouble(), world.random.nextGaussian(), world.random.nextGaussian(), world.random.nextGaussian());
			}
			return InteractionResult.SUCCESS;
		} else {
			return InteractionResult.PASS;
		}
	}

	private String getDimension(@Nullable Level world) {
		return world != null ? world.dimension().getRegistryName().toString() : Level.OVERWORLD.getRegistryName().toString();
	}

	private void setWraithNode(ItemStack eye, BlockPos pos, String dimension) {
		NBTHelper.putInt(NODE_X_TAG + dimension, eye, pos.getX());
		NBTHelper.putInt(NODE_Y_TAG + dimension, eye, pos.getY());
		NBTHelper.putInt(NODE_Z_TAG + dimension, eye, pos.getZ());
		NBTHelper.putString(DIMENSION_TAG, eye, dimension);
	}

	@Nullable
	@Override
	public CompoundTag getShareTag(ItemStack staff) {
		CompoundTag nbt = super.getShareTag(staff);
		if (nbt == null) {
			nbt = new CompoundTag();
		}
		nbt.putInt("count", getPearlCount(staff));

		return nbt;
	}

	public enum Mode implements StringRepresentable {
		CAST("cast"),
		LONG_CAST("long_cast"),
		NODE_WARP("node_warp");

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

		private static final Map<String, Mode> NAME_VALUES;
		private static final Mode[] VALUES;

		static {
			ImmutableMap.Builder<String, Mode> builder = new ImmutableMap.Builder<>();
			for (Mode value : Mode.values()) {
				builder.put(value.getSerializedName(), value);
			}
			NAME_VALUES = builder.build();
			VALUES = values();
		}

		public static Mode fromName(String name) {
			return NAME_VALUES.getOrDefault(name, CAST);
		}
	}
}
