package reliquary.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jspecify.annotations.Nullable;
import reliquary.api.IPedestal;
import reliquary.api.IPedestalActionItem;
import reliquary.block.tile.PedestalBlockEntity;
import reliquary.init.ModDataComponents;
import reliquary.init.ModFluids;
import reliquary.init.ModItems;
import reliquary.item.util.ICuriosItem;
import reliquary.pedestal.PedestalRegistry;
import reliquary.reference.Config;
import reliquary.util.RandHelper;
import reliquary.util.TooltipBuilder;
import reliquary.util.XpHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FortuneCoinItem extends ItemBase implements IPedestalActionItem, ICuriosItem {
	private static final String PREVENT_REMOTE_MOVEMENT = "PreventRemoteMovement";
	private static final String ALLOW_MACHINE_MOVEMENT = "AllowMachineRemoteMovement";

	private static final Set<IFortuneCoinPickupChecker> pickupCheckers = new HashSet<>();

	public static void addFortuneCoinPickupChecker(IFortuneCoinPickupChecker checker) {
		pickupCheckers.add(checker);
	}

	public interface IFortuneCoinPickupChecker {
		boolean canPickup(ItemEntity itemEntity);
	}

	public FortuneCoinItem(Properties properties) {
		super(properties.stacksTo(1).rarity(Rarity.EPIC));
	}

	@Override
	public void onEquipped(String identifier, LivingEntity player) {
		if (player.level().isClientSide()) {
			player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.1F, 0.5F * (RandHelper.getRandomMinusOneToOne(player.level().getRandom()) * 0.7F + 2.2F));
		}
	}

	@Override
	public ICuriosItem.Type getCuriosType() {
		return Type.NECKLACE;
	}

	@Override
	public void onWornServerTick(ItemStack stack, ServerLevel serverLevel, LivingEntity player) {
		inventoryTick(stack, serverLevel, player, null);
	}

	@Override
	protected void addMoreInformation(ItemStack stack, HolderLookup.@Nullable Provider registries, TooltipBuilder tooltipBuilder) {
		tooltipBuilder.description(this, ".tooltip2");
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return true;
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return isEnabled(stack);
	}

	public static boolean isEnabled(ItemStack stack) {
		return stack.getOrDefault(ModDataComponents.ENABLED, false);
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
		if (level.isClientSide() || !(entity instanceof Player player) || player.isSpectator() || level.getGameTime() % 2 != 0) {
			return;
		}
		if (!isEnabled(stack)) {
			return;
		}
		scanForEntitiesInRange(level, player, getStandardPullDistance());
	}

	private void scanForEntitiesInRange(Level level, Player player, double d) {
		List<BlockPos> disablePositions = getDisablePositions(level, player.blockPosition());
		List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, player.getBoundingBox().inflate(d));
		for (ItemEntity item : items) {
			if (canPickupItem(item, disablePositions, false) && checkForRoom(item.getItem(), player)) {
				item.setPickUpDelay(0);
				if (player.distanceTo(item) >= 1.5D) {
					teleportEntityToPlayer(item, player);
					break;
				}
			}
		}
		List<ExperienceOrb> xpOrbs = level.getEntitiesOfClass(ExperienceOrb.class, player.getBoundingBox().inflate(d));
		for (ExperienceOrb xpOrb : xpOrbs) {
			if (player.takeXpDelay > 0) {
				player.takeXpDelay = 0;
			}
			if (player.distanceTo(xpOrb) >= 1.5D) {
				teleportEntityToPlayer(xpOrb, player);
				break;
			}
		}
	}

	private boolean canPickupItem(ItemEntity item, List<BlockPos> disablePositions, boolean isInPedestal) {
		CompoundTag data = item.getPersistentData();
		if (data.getBooleanOr(PREVENT_REMOTE_MOVEMENT, false) && (!isInPedestal || !data.getBooleanOr(ALLOW_MACHINE_MOVEMENT, false))) {
			return false;
		}
		if (isInDisabledRange(item, disablePositions)) {
			return false;
		}
		for (IFortuneCoinPickupChecker pickupChecker : pickupCheckers) {
			if (!pickupChecker.canPickup(item)) {
				return false;
			}
		}
		return true;
	}

	private boolean isInDisabledRange(ItemEntity item, List<BlockPos> disablePositions) {
		for (BlockPos disablePos : disablePositions) {
			if (Math.abs(item.blockPosition().getX() - disablePos.getX()) < 5
					&& Math.abs(item.blockPosition().getY() - disablePos.getY()) < 5
					&& Math.abs(item.blockPosition().getZ() - disablePos.getZ()) < 5) {
				return true;
			}
		}
		return false;
	}

	private List<BlockPos> getDisablePositions(Level level, BlockPos coinPos) {
		List<BlockPos> disablePositions = new ArrayList<>();
		List<BlockPos> pedestalPositions = PedestalRegistry.getPositionsInRange(level.dimension().registry(), coinPos, 10);

		for (BlockPos pos : pedestalPositions) {
			BlockEntity te = level.getBlockEntity(pos);
			if (te instanceof PedestalBlockEntity pedestal && pedestal.switchedOn()) {
				ItemStack stack = pedestal.getItem();
				if (!stack.isEmpty() && stack.getItem() == this && !isEnabled(stack)) {
					disablePositions.add(pos);
				}
			}
		}
		return disablePositions;
	}

	private void teleportEntityToPlayer(Entity item, Player player) {
		player.level().addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.9F, 0.9F, 0.0F), item.getX() + 0.5D + player.level().getRandom().nextGaussian() / 8, item.getY() + 0.2D, item.getZ() + 0.5D + player.level().getRandom().nextGaussian() / 8, 0, 0, 0);
		player.getLookAngle();
		double x = player.getX() + player.getLookAngle().x * 0.2D;
		double y = player.getY();
		double z = player.getZ() + player.getLookAngle().z * 0.2D;
		item.setPos(x, y, z);
	}

	private boolean checkForRoom(ItemStack stackToPickup, Player player) {
		int remaining = stackToPickup.getCount();
		for (ItemStack inventoryStack : player.getInventory().getNonEquipmentItems()) {
			if (inventoryStack.isEmpty()) {
				return true;
			}

			if (inventoryStack.getItem() == stackToPickup.getItem() && inventoryStack.getDamageValue() == stackToPickup.getDamageValue()) {
				if (inventoryStack.getCount() + remaining <= inventoryStack.getMaxStackSize()) {
					return true;
				} else {
					remaining -= (inventoryStack.getMaxStackSize() - inventoryStack.getCount());
				}
			} else if (inventoryStack.getItem() == ModItems.VOID_TEAR.get() && ModItems.VOID_TEAR.get().isEnabled(inventoryStack) && ModItems.VOID_TEAR.get().canAbsorbStack(stackToPickup, inventoryStack)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
		if (!(livingEntity instanceof Player player)) {
			return;
		}

		scanForEntitiesInRange(player.level(), player, getLongRangePullDistance());
	}

	private double getLongRangePullDistance() {
		return Config.COMMON.items.fortuneCoin.longRangePullDistance.get();
	}

	private double getStandardPullDistance() {
		return Config.COMMON.items.fortuneCoin.standardPullDistance.get();
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity livingEntity) {
		return 64;
	}

	@Override
	public ItemUseAnimation getUseAnimation(ItemStack stack) {
		return ItemUseAnimation.BLOCK;
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (player.isShiftKeyDown()) {
			toggle(stack);
			player.level().playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.1F, 0.5F * (RandHelper.getRandomMinusOneToOne(player.level().getRandom()) * 0.7F + 1.8F));
			return InteractionResult.SUCCESS.heldItemTransformedTo(stack);
		}

		player.startUsingItem(hand);
		return InteractionResult.SUCCESS;
	}

	@Override
	public void update(ItemStack stack, Level level, IPedestal pedestal) {
		if (level.isClientSide()) {
			return;
		}

		if (isEnabled(stack)) {
			BlockPos pos = pedestal.getBlockPosition();
			pickupItems(pedestal, level, pos);
			pickupXp(pedestal, level, pos);
		}
	}

	private void pickupItems(IPedestal pedestal, Level level, BlockPos pos) {
		List<BlockPos> disablePositions = getDisablePositions(level, pos);
		List<ItemEntity> entities = level.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(getStandardPullDistance()));
		for (ItemEntity entityItem : entities) {

			//if entity is marked not to be picked up by magnets leave it alone - IE thing but may be more than that
			if (!canPickupItem(entityItem, disablePositions, true)) {
				continue;
			}

			int numberAdded = pedestal.addToConnectedInventory(level, entityItem.getItem().copy());
			if (numberAdded > 0) {
				entityItem.getItem().setCount(entityItem.getItem().getCount() - numberAdded);

				if (entityItem.getItem().getCount() <= 0) {
					entityItem.discard();
				}
			} else {
				pedestal.setActionCoolDown(20);
			}
		}
	}

	private void pickupXp(IPedestal pedestal, Level level, BlockPos pos) {
		List<ExperienceOrb> xpOrbs = level.getEntitiesOfClass(ExperienceOrb.class, new AABB(pos).inflate(getStandardPullDistance()));
		for (ExperienceOrb xpOrb : xpOrbs) {
			int amountToTransfer = XpHelper.experienceToLiquid(xpOrb.getValue());
			int amountAdded = pedestal.fillConnectedTank(FluidResource.of(ModFluids.XP_STILL.get()), amountToTransfer);

			if (amountAdded > 0) {
				xpOrb.discard();

				if (amountToTransfer > amountAdded) {
					level.addFreshEntity(new ExperienceOrb(level, pos.getX(), pos.getY(), pos.getZ(), XpHelper.liquidToExperience(amountToTransfer - amountAdded)));
				}
			} else {
				pedestal.setActionCoolDown(20);
			}
		}
	}

	@Override
	public void onRemoved(ItemStack stack, Level level, IPedestal pedestal) {
		//noop
	}

	@Override
	public void stop(ItemStack stack, Level level, IPedestal pedestal) {
		//noop
	}

	public void toggle(ItemStack stack) {
		stack.set(ModDataComponents.ENABLED, !isEnabled(stack));
	}
}
