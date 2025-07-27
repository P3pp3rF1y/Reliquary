package reliquary.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.items.IItemHandler;
import reliquary.Reliquary;
import reliquary.api.IPedestal;
import reliquary.api.IPedestalActionItem;
import reliquary.init.ModDataComponents;
import reliquary.item.util.IScrollableItem;
import reliquary.item.util.ICuriosItem;
import reliquary.reference.Config;
import reliquary.util.InventoryHelper;
import reliquary.util.PlayerInventoryProvider;
import reliquary.util.TooltipBuilder;
import reliquary.util.XpHelper;

import javax.annotation.Nullable;
import java.util.List;

public class HeroMedallionItem extends ToggleableItem implements IPedestalActionItem, IScrollableItem, ICuriosItem {
	public HeroMedallionItem() {
		super(new Properties().durability(0).setNoRepair().rarity(Rarity.EPIC));
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return stack.getOrDefault(ModDataComponents.ENABLED, false);
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BLOCK;
	}

	@Override
	protected void addMoreInformation(ItemStack medallion, @Nullable HolderLookup.Provider registries, TooltipBuilder tooltipBuilder) {
		int experience = getExperience(medallion);
		int levels = XpHelper.getLevelForExperience(experience);
		int remainingExperience = experience - XpHelper.getExperienceForLevel(levels);
		tooltipBuilder.data(this, ".tooltip2", levels, remainingExperience);
		tooltipBuilder.description(this, ".tooltip.drain_levels", Component.literal(String.valueOf(getDrainXpLevels(medallion))).withStyle(ChatFormatting.RED));
		if (isEnabled(medallion)) {
			tooltipBuilder.absorbActive(Component.translatable("tooltip.reliquary.xp").withStyle(ChatFormatting.GREEN));
			tooltipBuilder.description(this, ".tooltip.fill_stop_level", Component.literal(String.valueOf(getStopAtXpLevel(medallion))).withStyle(ChatFormatting.GREEN));
		} else {
			tooltipBuilder.absorb();
		}
		tooltipBuilder.description(this, ".tooltip.scroll_to_change");
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return true;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
		if (level.isClientSide || !(entity instanceof Player player) || player.isSpectator() || !isEnabled(stack) || level.getGameTime() % 10 != 0) {
			return;
		}
		if ((!player.isUsingItem() || player.getUseItem() != stack)) {
			drainExperienceLevel(stack, player);
		}

		repairItemsInPlayersInventory(stack, player);
	}

	private void repairItemsInPlayersInventory(ItemStack heroMedallion, Player player) {
		long cooldownTime = heroMedallion.getOrDefault(ModDataComponents.COOLDOWN_TIME, 0L);
		if (cooldownTime > player.level().getGameTime()) {
			return;
		}
		heroMedallion.set(ModDataComponents.COOLDOWN_TIME, player.level().getGameTime() + Config.COMMON.items.heroMedallion.repairCoolDown.get());

		PlayerInventoryProvider.get().runOnPlayerInventoryHandlers(player, stack -> {
			if (canRepairWithXp(stack)) {
				repairItemWithXp(heroMedallion, stack);
			}
		});
	}

	private void drainExperienceLevel(ItemStack stack, Player player) {
		int totalPlayerExperience = XpHelper.getTotalPlayerExperience(player);
		int previousPlayerLevel = XpHelper.getLevelForExperience(totalPlayerExperience) - 1; //calculating this so that player levels over 21483 don't break the math
		int experiencePoints = player.isCreative() ? 100 : totalPlayerExperience - XpHelper.getExperienceForLevel(Math.max(getStopAtXpLevel(stack), previousPlayerLevel));
		experiencePoints = Math.min(experiencePoints, Integer.MAX_VALUE - getExperience(stack));
		if (experiencePoints > 0) {
			if (!player.isCreative()) {
				decreasePlayerExperience(player, experiencePoints);
			}
			increaseMedallionExperience(stack, experiencePoints);
		}
	}

	private void decreasePlayerExperience(Player player, int pointsToRemove) {
		player.giveExperiencePoints(-pointsToRemove);
	}

	private void decreaseMedallionExperience(ItemStack stack, int experience) {
		setExperience(stack, getExperience(stack) - experience);
	}

	private void increasePlayerExperience(Player player, int xpPoints) {
		player.giveExperiencePoints(xpPoints);
	}

	private void increaseMedallionExperience(ItemStack stack, int xpPoints) {
		setExperience(stack, getExperience(stack) + xpPoints);
	}

	public int getExperience(ItemStack stack) {
		return stack.getOrDefault(ModDataComponents.EXPERIENCE, 0);
	}

	public void setExperience(ItemStack stack, int experience) {
		stack.set(ModDataComponents.EXPERIENCE, experience);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (level.isClientSide) {
			return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
		}
		if (player.isShiftKeyDown()) {
			return super.use(level, player, hand);
		}

		drainExperience(stack, player, level, getDrainXpLevels(stack));

		return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
	}

	private void drainExperience(ItemStack stack, Player player, Level level, int xpLevels) {
		BlockHitResult rayTraceResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);

		if (rayTraceResult.getType() == HitResult.Type.BLOCK) {
			BlockPos hitPos = rayTraceResult.getBlockPos().offset(rayTraceResult.getDirection().getNormal());
			spawnXpOnGround(stack, level, hitPos, xpLevels);
		} else {
			xpLevels += Math.round(player.experienceProgress);

			while (xpLevels > 0) {
				int xp = Math.max(0, (int) ((1 - player.experienceProgress) * XpHelper.getExperienceLimitOnLevel(player.experienceLevel)) + 1);
				int pointsToAdd = player.isCreative() ? xp : Math.min(xp, getExperience(stack));
				player.giveExperiencePoints(pointsToAdd);
				if (!player.isCreative()) {
					decreaseMedallionExperience(stack, pointsToAdd);
				}
				xpLevels--;
			}
		}
	}

	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
		if (livingEntity.level().isClientSide || isEnabled(stack) || !(livingEntity instanceof Player) || getUseDuration(stack, livingEntity) - timeLeft > 10) {
			return;
		}

		drainExperience(stack, (Player) livingEntity, livingEntity.level(), 1);
	}

	private void spawnXpOnGround(ItemStack stack, Level level, BlockPos hitPos, int xpLevels) {
		int xp = XpHelper.getExperienceForLevel(xpLevels);

		if (getExperience(stack) >= xp) {
			decreaseMedallionExperience(stack, xp);

			while (xp > 0) {
				int j = ExperienceOrb.getExperienceValue(xp);
				xp -= j;
				level.addFreshEntity(new ExperienceOrb(level, hitPos.getX(), hitPos.getY(), hitPos.getZ(), j));
			}
		}
	}

	@Override
	public void update(ItemStack stack, Level level, IPedestal pedestal) {
		List<BlockPos> posInRange = pedestal.getPedestalsInRange(level, Config.COMMON.items.heroMedallion.pedestalRange.get());
		for (BlockPos pedestalPos : posInRange) {
			InventoryHelper.runOnInventoryAt(level, pedestalPos, pedestalInventory -> repairItemsWithXp(stack, pedestalInventory));
		}
		pedestal.setActionCoolDown(Config.COMMON.items.heroMedallion.repairCoolDown.get());
	}

	private void repairItemsWithXp(ItemStack stack, IItemHandler inventory) {
		for (ItemStack itemToRepair : getItemsRepairedWithXp(inventory)) {
			repairItemWithXp(stack, itemToRepair);
		}
	}

	private void repairItemWithXp(ItemStack heroMedallion, ItemStack itemToRepair) {
		int xpToRepair = Math.min(Config.COMMON.items.heroMedallion.repairStepXP.get(), getExperience(heroMedallion));
		int durabilityToRepair = Math.min(XpHelper.xpToDurability(xpToRepair), itemToRepair.getDamageValue());

		setExperience(heroMedallion, getExperience(heroMedallion) - XpHelper.durabilityToXp(durabilityToRepair));
		itemToRepair.setDamageValue(itemToRepair.getDamageValue() - durabilityToRepair);
	}

	private List<ItemStack> getItemsRepairedWithXp(IItemHandler inventory) {
		NonNullList<ItemStack> stacksToReturn = NonNullList.create();

		for (int slot = 0; slot < inventory.getSlots(); slot++) {
			ItemStack stack = inventory.getStackInSlot(slot);
			//only getting items that are more than 1 damaged to not waste xp
			if (canRepairWithXp(stack)) {
				stacksToReturn.add(stack);
			}
		}
		return stacksToReturn;
	}

	private boolean canRepairWithXp(ItemStack stack) {
		if (stack.isDamaged() && stack.getDamageValue() > 1) {
			ItemEnchantments itemenchantments = stack.getTagEnchantments();

			for (Holder<Enchantment> enchantment : itemenchantments.keySet()) {
				if (enchantment.value().effects().has(EnchantmentEffectComponents.REPAIR_WITH_XP)) {
					return true;
				}
			}

		}
		return false;
	}

	@Override
	public void onRemoved(ItemStack stack, Level level, IPedestal pedestal) {
		//nothing needed
	}

	@Override
	public void stop(ItemStack stack, Level level, IPedestal pedestal) {
		//nothing needed
	}

	@Override
	public InteractionResult onMouseScrolled(ItemStack stack, Player player, double scrollDelta) {
		if (player.level().isClientSide) {
			return InteractionResult.PASS;
		}

		int levelAddition = scrollDelta > 0 ? 1 : -1;
		if (isEnabled(stack)) {
			int newLevels = Math.max(0, getStopAtXpLevel(stack) + levelAddition);
			setStopAtXpLevel(stack, newLevels);
			player.displayClientMessage(Component.translatable("chat." + Reliquary.MOD_ID + ".hero_medallion.fill_levels", Component.literal(String.valueOf(newLevels)).withStyle(ChatFormatting.GREEN)), true);
		} else {
			int newLevels = Math.max(1, getDrainXpLevels(stack) + levelAddition);
			setDrainXpLevels(stack, newLevels);
			player.displayClientMessage(Component.translatable("chat." + Reliquary.MOD_ID + ".hero_medallion.drain_levels", Component.literal(String.valueOf(newLevels)).withStyle(ChatFormatting.RED)), true);
		}

		return InteractionResult.SUCCESS;
	}

	private int getDrainXpLevels(ItemStack stack) {
		return stack.getOrDefault(ModDataComponents.DRAIN_XP_LEVELS, 1);
	}

	private int getStopAtXpLevel(ItemStack stack) {
		return stack.getOrDefault(ModDataComponents.STOP_AT_XP_LEVEL, 0);
	}

	private void setDrainXpLevels(ItemStack stack, int levels) {
		stack.set(ModDataComponents.DRAIN_XP_LEVELS, levels);
	}

	private void setStopAtXpLevel(ItemStack stack, int levels) {
		stack.set(ModDataComponents.STOP_AT_XP_LEVEL, levels);
	}

	@Override
	public Type getCuriosType() {
		return Type.NECKLACE;
	}

	@Override
	public void onWornTick(ItemStack stack, LivingEntity player) {
		inventoryTick(stack, player.level(), player, 0, false);
	}
}
