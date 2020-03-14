package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import xreliquary.entities.EnderStaffProjectileEntity;
import xreliquary.init.ModBlocks;
import xreliquary.items.util.FilteredBigItemStack;
import xreliquary.items.util.FilteredItemHandlerProvider;
import xreliquary.items.util.FilteredItemStackHandler;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class EnderStaffItem extends ToggleableItem {

	private static final String DIMENSION_ID_TAG = "dimensionID";
	private static final String NODE_X_TAG = "nodeX";
	private static final String NODE_Y_TAG = "nodeY";
	private static final String NODE_Z_TAG = "nodeZ";
	private static final String LONG_CAST_TAG = "long_cast";

	public EnderStaffItem() {
		super("ender_staff", new Properties().maxStackSize(1).setNoRepair().rarity(Rarity.EPIC));
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

	public String getMode(ItemStack stack) {
		if (NBTHelper.getString("mode", stack).equals("")) {
			setMode(stack, "cast");
		}
		return NBTHelper.getString("mode", stack);
	}

	private void setMode(ItemStack stack, String s) {
		NBTHelper.putString("mode", stack, s);
	}

	private void cycleMode(ItemStack stack) {
		if (getMode(stack).equals("cast")) {
			setMode(stack, LONG_CAST_TAG);
		} else if (getMode(stack).equals(LONG_CAST_TAG)) {
			setMode(stack, "node_warp");
		} else {
			setMode(stack, "cast");
		}
	}

	@Override
	public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
		if (entity.world.isRemote) {
			return true;
		}
		if (!(entity instanceof PlayerEntity)) {
			return true;
		}
		PlayerEntity player = (PlayerEntity) entity;
		if (player.isSneaking()) {
			cycleMode(stack);
		}
		return false;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return new FilteredItemHandlerProvider(Collections.singletonList(new FilteredItemStackHandler.RemovableStack(
				new FilteredBigItemStack(Items.ENDER_PEARL, Settings.COMMON.items.enderStaff.enderPearlLimit.get(),
						Settings.COMMON.items.enderStaff.enderPearlWorth.get())
				, false)));
	}

	@Override
	public void inventoryTick(ItemStack staff, World world, Entity entity, int itemSlot, boolean isSelected) {
		if (world.isRemote) {
			return;
		}

		PlayerEntity player = null;
		if (entity instanceof PlayerEntity) {
			player = (PlayerEntity) entity;
		}
		if (player == null) {
			return;
		}

		if (!isEnabled(staff)) {
			return;
		}
		if (getPearlCount(staff) + getEnderPearlWorth() <= getEnderPearlLimit() && InventoryHelper.consumeItem(new ItemStack(Items.ENDER_PEARL), player)) {
			setPearlCount(staff, getPearlCount(staff) + getEnderPearlWorth());
		}
	}

	private void setPearlCount(ItemStack stack, int count) {
		stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(itemHandler -> {
			if (!(itemHandler instanceof FilteredItemStackHandler)) {
				return;
			}
			FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;
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
			if (!(itemHandler instanceof FilteredItemStackHandler)) {
				return 0;
			}
			FilteredItemStackHandler filteredHandler = (FilteredItemStackHandler) itemHandler;
			return filteredHandler.getTotalAmount(0);
		}).orElse(0);
	}

	@Override
	public void onUsingTick(ItemStack stack, LivingEntity entityLivingBase, int unadjustedCount) {
		if (!(entityLivingBase instanceof PlayerEntity)) {
			return;
		}

		PlayerEntity player = (PlayerEntity) entityLivingBase;

		for (int particles = 0; particles < 2; particles++) {
			player.world.addParticle(ParticleTypes.PORTAL, player.posX, player.posY, player.posZ, player.world.rand.nextGaussian(), player.world.rand.nextGaussian(), player.world.rand.nextGaussian());
		}
		if (unadjustedCount == 1) {
			player.stopActiveHand();
		}
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BLOCK;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return getNodeWarpCastTime();
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
		if (!(entityLiving instanceof PlayerEntity)) {
			return;
		}

		PlayerEntity player = (PlayerEntity) entityLiving;

		if (timeLeft == 1) {
			doWraithNodeWarpCheck(stack, player.world, player);
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (!player.isSneaking()) {
			if (getMode(stack).equals("cast") || getMode(stack).equals(LONG_CAST_TAG)) {
				if (player.isSwingInProgress || (getPearlCount(stack) < getEnderStaffPearlCost() && !player.isCreative())) {
					return new ActionResult<>(ActionResultType.FAIL, stack);
				}
				player.swingArm(hand);
				player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ENDER_PEARL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
				if (!player.world.isRemote) {
					EnderStaffProjectileEntity enderStaffProjectile = new EnderStaffProjectileEntity(player.world, player, !getMode(stack).equals(LONG_CAST_TAG));
					enderStaffProjectile.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);
					player.world.addEntity(enderStaffProjectile);
					if (!player.isCreative()) {
						setPearlCount(stack, getPearlCount(stack) - getEnderStaffPearlCost());
					}
				}
			} else {
				player.setActiveHand(hand);
			}
		}
		return super.onItemRightClick(world, player, hand);
	}

	private void doWraithNodeWarpCheck(ItemStack stack, World world, PlayerEntity player) {
		if (getPearlCount(stack) < getEnderStaffNodeWarpCost() && !player.isCreative()) {
			return;
		}

		if (stack.getTag() != null && stack.getTag().getInt(DIMENSION_ID_TAG) != getDimension(world)) {
			if (!world.isRemote) {
				player.sendMessage(new StringTextComponent(TextFormatting.DARK_RED + "Out of range!"));
			}
		} else if (stack.getTag() != null && world.getBlockState(new BlockPos(stack.getTag().getInt(NODE_X_TAG + getDimension(world)), stack.getTag().getInt(NODE_Y_TAG + getDimension(world)), stack.getTag().getInt(NODE_Z_TAG + getDimension(world)))).getBlock() == ModBlocks.WRAITH_NODE) {
			if (canTeleport(world, stack.getTag().getInt(NODE_X_TAG + getDimension(world)), stack.getTag().getInt(NODE_Y_TAG + getDimension(world)), stack.getTag().getInt(NODE_Z_TAG + getDimension(world)))) {
				teleportPlayer(world, stack.getTag().getInt(NODE_X_TAG + getDimension(world)), stack.getTag().getInt(NODE_Y_TAG + getDimension(world)), stack.getTag().getInt(NODE_Z_TAG + getDimension(world)), player);
				if (!player.isCreative() && !player.world.isRemote) {
					setPearlCount(stack, getPearlCount(stack) - getEnderStaffNodeWarpCost());
				}
			}
		} else if (stack.getTag() != null && stack.getTag().contains(DIMENSION_ID_TAG)) {
			stack.getTag().remove(DIMENSION_ID_TAG);
			stack.getTag().remove(NODE_X_TAG);
			stack.getTag().remove(NODE_Y_TAG);
			stack.getTag().remove(NODE_Z_TAG);
			stack.getTag().remove("cooldown");
			if (!world.isRemote) {
				player.sendMessage(new StringTextComponent(TextFormatting.DARK_RED + "Node dosen't exist!"));
			} else {
				player.playSound(SoundEvents.ENTITY_ENDERMAN_DEATH, 1.0f, 1.0f);
			}
		}
	}

	private boolean canTeleport(World world, int x, int y, int z) {
		return !(!world.isAirBlock(new BlockPos(x, y + 1, z)) || !world.isAirBlock(new BlockPos(x, y + 2, z)));
	}

	private void teleportPlayer(World world, int x, int y, int z, PlayerEntity player) {
		player.setPositionAndUpdate(x + 0.5, y + 0.875, z + 0.5);
		player.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
		for (int particles = 0; particles < 2; particles++) {
			world.addParticle(ParticleTypes.PORTAL, player.posX, player.posY, player.posZ, world.rand.nextGaussian(), world.rand.nextGaussian(), world.rand.nextGaussian());
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack staff, @Nullable World world, List<ITextComponent> tooltip) {
		//added spacing here to make sure the tooltips didn't come out with weird punctuation derps.
		String charge = Integer.toString(getPearlCount(staff, true));
		String phrase = "Currently bound to ";
		String position = "";
		if (staff.getTag() != null && staff.getTag().getInt(DIMENSION_ID_TAG) != getDimension(world)) {
			phrase = "Out of range!";
		} else if (staff.getTag() != null && staff.getTag().contains(NODE_X_TAG + getDimension(world)) && staff.getTag().contains(NODE_Y_TAG + getDimension(world)) && staff.getTag().contains(NODE_Z_TAG + getDimension(world))) {
			position = "X: " + staff.getTag().getInt(NODE_X_TAG + getDimension(world)) + " Y: " + staff.getTag().getInt(NODE_Y_TAG + getDimension(world)) + " Z: " + staff.getTag().getInt(NODE_Z_TAG + getDimension(world));
		} else {
			position = "nowhere.";
		}
		LanguageHelper.formatTooltip(getTranslationKey() + ".tooltip2", ImmutableMap.of("phrase", phrase, "position", position, "charge", charge), tooltip);
		if (isEnabled(staff)) {
			LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", TextFormatting.GREEN + Items.ENDER_PEARL.getDisplayName(new ItemStack(Items.ENDER_PEARL)).toString()), tooltip);
		}
		LanguageHelper.formatTooltip("tooltip.absorb", null, tooltip);
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return true;
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext itemUseContext) {
		ItemStack stack = itemUseContext.getItem();
		World world = itemUseContext.getWorld();
		BlockPos pos = itemUseContext.getPos();

		// if right clicking on a wraith node, bind the eye to that wraith node.
		if ((stack.getTag() == null || !(stack.getTag().contains(DIMENSION_ID_TAG))) && world.getBlockState(pos).getBlock() == ModBlocks.WRAITH_NODE) {
			setWraithNode(stack, pos, getDimension(world));

			PlayerEntity player = itemUseContext.getPlayer();
			if (player != null) {
				player.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
			}
			for (int particles = 0; particles < 12; particles++) {
				world.addParticle(ParticleTypes.PORTAL, pos.getX() + world.rand.nextDouble(), pos.getY() + world.rand.nextDouble(), pos.getZ() + world.rand.nextDouble(), world.rand.nextGaussian(), world.rand.nextGaussian(), world.rand.nextGaussian());
			}
			return ActionResultType.SUCCESS;
		} else {
			return ActionResultType.PASS;
		}
	}

	private int getDimension(@Nullable World world) {
		return world != null ? world.getDimension().getType().getId() : 0;
	}

	private void setWraithNode(ItemStack eye, BlockPos pos, int dimensionID) {
		NBTHelper.putInt(NODE_X_TAG + dimensionID, eye, pos.getX());
		NBTHelper.putInt(NODE_Y_TAG + dimensionID, eye, pos.getY());
		NBTHelper.putInt(NODE_Z_TAG + dimensionID, eye, pos.getZ());
		NBTHelper.putInt(DIMENSION_ID_TAG, eye, dimensionID);
	}

	@Nullable
	@Override
	public CompoundNBT getShareTag(ItemStack staff) {
		CompoundNBT nbt = super.getShareTag(staff);
		if (nbt == null) {
			nbt = new CompoundNBT();
		}
		nbt.putInt("count", getPearlCount(staff));

		return nbt;
	}
}
