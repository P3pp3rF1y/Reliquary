package reliquary.items;

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
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.IItemHandler;
import reliquary.api.IPedestal;
import reliquary.api.IPedestalActionItem;
import reliquary.items.util.IScrollableItem;
import reliquary.items.util.fluid.FluidHandlerHeroMedallion;
import reliquary.reference.Reference;
import reliquary.reference.Settings;
import reliquary.util.InventoryHelper;
import reliquary.util.NBTHelper;
import reliquary.util.TooltipBuilder;
import reliquary.util.XpHelper;

import javax.annotation.Nullable;
import java.util.List;

public class HeroMedallionItem extends ToggleableItem implements IPedestalActionItem, IScrollableItem {
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
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BLOCK;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack medallion, @Nullable Level world, TooltipBuilder tooltipBuilder) {
		int experience = NBTHelper.getInt(EXPERIENCE_TAG, medallion);
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
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
		if (world.isClientSide || !isEnabled(stack) || world.getGameTime() % 10 != 0) {
			return;
		}
		if (entity instanceof Player player && (!player.isUsingItem() || player.getUseItem() != stack)) {
			drainExperienceLevel(stack, player);
		}
	}

	private void drainExperienceLevel(ItemStack stack, Player player) {
		int experiencePoints = player.isCreative() ? 100 : XpHelper.getTotalPlayerExperience(player) - XpHelper.getExperienceForLevel(Math.max(getStopAtXpLevel(stack), player.experienceLevel - 1));
		if (experiencePoints > 0) {
			if (!player.isCreative()) {
				decreasePlayerExperience(player, experiencePoints);
			}
			increaseMedallionExperience(stack, experiencePoints);
		}
	}

	private void decreasePlayerExperience(Player player, int pointsToRemove) {
		player.totalExperience = XpHelper.getTotalPlayerExperience(player) - pointsToRemove;
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
			int maxPoints = XpHelper.getExperienceForLevel(player.experienceLevel + xpLevels) - XpHelper.getTotalPlayerExperience(player);
			int pointsToAdd = player.isCreative() ? maxPoints : Math.min(maxPoints, getExperience(stack));
			increasePlayerExperience(player, pointsToAdd);
			if (!player.isCreative()) {
				decreaseMedallionExperience(stack, pointsToAdd);
			}
		}
	}

	@Override
	public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
		if (entityLiving.level().isClientSide || isEnabled(stack) || !(entityLiving instanceof Player) || getUseDuration(stack) - timeLeft > 10) {
			return;
		}

		drainExperience(stack, (Player) entityLiving, entityLiving.level(), 1);
	}

	private void spawnXpOnGround(ItemStack stack, Level world, BlockPos hitPos, int xpLevels) {
		int xp = XpHelper.getExperienceForLevel(xpLevels);

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
			if (stack.isDamaged() && stack.getDamageValue() > 1 && stack.getEnchantmentLevel(Enchantments.MENDING) > 0) {
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

	@Override
	public InteractionResult onMouseScrolled(ItemStack stack, Player player, double scrollDelta) {
		if (player.level().isClientSide) {
			return InteractionResult.PASS;
		}

		int levelAddition = scrollDelta > 0 ? 1 : -1;
		if (isEnabled(stack)) {
			int newLevels = Math.max(0, getStopAtXpLevel(stack) + levelAddition);
			setStopAtXpLevel(stack, newLevels);
			player.displayClientMessage(Component.translatable("chat." + Reference.MOD_ID + ".hero_medallion.fill_levels", Component.literal(String.valueOf(newLevels)).withStyle(ChatFormatting.GREEN)), true);
		} else {
			int newLevels = Math.max(1, getDrainXpLevels(stack) + levelAddition);
			setDrainXpLevels(stack, newLevels);
			player.displayClientMessage(Component.translatable("chat." + Reference.MOD_ID + ".hero_medallion.drain_levels", Component.literal(String.valueOf(newLevels)).withStyle(ChatFormatting.RED)), true);
		}

		return InteractionResult.SUCCESS;
	}

	private int getDrainXpLevels(ItemStack stack) {
		return NBTHelper.getInt(stack, "drainXpLevels").orElse(1);
	}

	private int getStopAtXpLevel(ItemStack stack) {
		return NBTHelper.getInt(stack, "stopAtXpLevel").orElse(0);
	}

	private void setDrainXpLevels(ItemStack stack, int levels) {
		NBTHelper.putInt("drainXpLevels", stack, levels);
	}

	private void setStopAtXpLevel(ItemStack stack, int levels) {
		NBTHelper.putInt("stopAtXpLevel", stack, levels);
	}
}
