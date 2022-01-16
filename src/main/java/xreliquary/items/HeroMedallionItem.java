package xreliquary.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.IItemHandler;
import xreliquary.api.IPedestal;
import xreliquary.api.IPedestalActionItem;
import xreliquary.items.util.fluid.FluidHandlerHeroMedallion;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;
import xreliquary.util.XpHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class HeroMedallionItem extends ToggleableItem implements IPedestalActionItem {
	private static final String EXPERIENCE_TAG = "experience";

	public HeroMedallionItem() {
		super(new Properties().durability(0).setNoRepair());
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.EPIC;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isFoil(ItemStack stack) {
		return NBTHelper.getBoolean("enabled", stack);
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 500;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BLOCK;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack medallion, @Nullable Level world, List<Component> tooltip) {
		int experience = NBTHelper.getInt(EXPERIENCE_TAG, medallion);
		int levels = XpHelper.getLevelForExperience(experience);
		int remainingExperience = experience - XpHelper.getExperienceForLevel(levels);

		LanguageHelper.formatTooltip(getDescriptionId() + ".tooltip2", Map.of("levels", String.valueOf(levels), EXPERIENCE_TAG, String.valueOf(remainingExperience)), tooltip);
		if (isEnabled(medallion)) {
			LanguageHelper.formatTooltip("tooltip.absorb_active", Map.of("item", ChatFormatting.GREEN + "XP"), tooltip);
		}
		LanguageHelper.formatTooltip("tooltip.absorb", null, tooltip);
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return true;
	}

	private int getExperienceMinimum() {
		return Settings.COMMON.items.heroMedallion.experienceLevelMinimum.get();
	}

	private int getExperienceMaximum() {
		return Settings.COMMON.items.heroMedallion.experienceLevelMaximum.get();
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
		if (world.isClientSide || !isEnabled(stack) || world.getGameTime() % 10 != 0) {
			return;
		}
		if (entity instanceof Player player && (!player.isUsingItem() || player.getUseItem() != stack)) {
			drainExperienceLevels(stack, player, 1);
		}
	}

	private void drainExperienceLevels(ItemStack stack, Player player, int levelsToDrain) {
		int experiencePoints = player.isCreative() ? 100 : player.totalExperience - XpHelper.getExperienceForLevel(Math.max(getExperienceMinimum(), player.experienceLevel - levelsToDrain));
		if (experiencePoints > 0) {
			if (!player.isCreative()) {
				decreasePlayerExperience(player, experiencePoints);
			}
			increaseMedallionExperience(stack, experiencePoints);
		}
	}

	private void decreasePlayerExperience(Player player, int pointsToRemove) {
		player.totalExperience -= pointsToRemove;
		int newLevel = XpHelper.getLevelForExperience(player.totalExperience);
		player.experienceLevel = newLevel;
		player.experienceProgress = (float) (player.totalExperience - XpHelper.getExperienceForLevel(newLevel)) / player.getXpNeededForNextLevel();
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
		return NBTHelper.getInt(EXPERIENCE_TAG, stack);
	}

	public void setExperience(ItemStack stack, int i) {
		NBTHelper.putInt(EXPERIENCE_TAG, stack, i);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (world.isClientSide) {
			return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
		}
		if (player.isShiftKeyDown()) {
			return super.use(world, player, hand);
		}

		player.startUsingItem(hand);

		return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
	}

	@Override
	public void onUsingTick(ItemStack stack, LivingEntity entity, int count) {
		int cooldown = count > 20 ? 10 : 20;
		if (entity.level.isClientSide || !(entity instanceof Player player) || entity.level.getGameTime() % cooldown != 0) {
			return;
		}

		Level world = player.level;
		if (isEnabled(stack)) {
			drainExperienceLevels(stack, player, 10);
			return;
		}
		increaseExperience(stack, player, world, 10);
	}

	private void increaseExperience(ItemStack stack, Player player, Level world, int levels) {
		BlockHitResult rayTraceResult = getPlayerPOVHitResult(world, player, ClipContext.Fluid.ANY);

		if (rayTraceResult.getType() == HitResult.Type.BLOCK) {
			BlockPos hitPos = rayTraceResult.getBlockPos().offset(rayTraceResult.getDirection().getNormal());
			spawnXpOnGround(stack, world, hitPos);
		} else if (player.experienceLevel < getExperienceMaximum()) {
			levels += Math.round(player.experienceProgress);
			int maxPoints = XpHelper.getExperienceForLevel(player.experienceLevel + levels) - player.totalExperience;
			int pointsToAdd = player.isCreative() ? maxPoints : Math.min(maxPoints, getExperience(stack));
			increasePlayerExperience(player, pointsToAdd);
			if (!player.isCreative()) {
				decreaseMedallionExperience(stack, pointsToAdd);
			}
		}
	}

	@Override
	public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
		if (entityLiving.level.isClientSide || isEnabled(stack) || !(entityLiving instanceof Player) || getUseDuration(stack) - timeLeft > 10) {
			return;
		}

		increaseExperience(stack, (Player) entityLiving, entityLiving.level, 1);
	}

	private void spawnXpOnGround(ItemStack stack, Level world, BlockPos hitPos) {
		int xp = Math.min(Settings.COMMON.items.heroMedallion.experienceDrop.get(), getExperience(stack));

		if (getExperience(stack) >= xp) {
			decreaseMedallionExperience(stack, xp);

			while (xp > 0) {
				int j = ExperienceOrb.getExperienceValue(xp);
				xp -= j;
				world.addFreshEntity(new ExperienceOrb(world, hitPos.getX(), hitPos.getY(), hitPos.getZ(), j));
			}
		}
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return new FluidHandlerHeroMedallion(stack);
	}

	@Override
	public void update(ItemStack stack, Level level, IPedestal pedestal) {
		List<BlockPos> posInRange = pedestal.getPedestalsInRange(level, Settings.COMMON.items.heroMedallion.pedestalRange.get());
		for (BlockPos pedestalPos : posInRange) {
			InventoryHelper.getInventoryAtPos(level, pedestalPos).ifPresent(pedestalInventory -> {
				List<ItemStack> toRepair = getMendingItemsForRepair(pedestalInventory);

				for (ItemStack itemToRepair : toRepair) {
					int xpToRepair = Math.min(Settings.COMMON.items.heroMedallion.pedestalRepairStepXP.get(), getExperience(stack));
					int durabilityToRepair = Math.min(XpHelper.xpToDurability(xpToRepair), itemToRepair.getDamageValue());

					setExperience(stack, getExperience(stack) - XpHelper.durabilityToXp(durabilityToRepair));
					itemToRepair.setDamageValue(itemToRepair.getDamageValue() - durabilityToRepair);
				}
			});
		}
		pedestal.setActionCoolDown(Settings.COMMON.items.heroMedallion.pedestalCoolDown.get());
	}

	private List<ItemStack> getMendingItemsForRepair(IItemHandler inventory) {
		NonNullList<ItemStack> stacksToReturn = NonNullList.create();

		for (int slot = 0; slot < inventory.getSlots(); slot++) {
			ItemStack stack = inventory.getStackInSlot(slot);

			//only getting items that are more than 1 damaged to not waste xp
			if (stack.isDamaged() && stack.getDamageValue() > 1 && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MENDING, stack) > 0) {
				stacksToReturn.add(stack);
			}
		}

		return stacksToReturn;
	}

	@Override
	public void onRemoved(ItemStack stack, Level level, IPedestal pedestal) {
		//nothing needed
	}

	@Override
	public void stop(ItemStack stack, Level level, IPedestal pedestal) {
		//nothing needed
	}
}
