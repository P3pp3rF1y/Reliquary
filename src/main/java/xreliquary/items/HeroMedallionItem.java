package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
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

public class HeroMedallionItem extends ToggleableItem implements IPedestalActionItem {
	private static final String EXPERIENCE_TAG = "experience";

	public HeroMedallionItem() {
		super(new Properties().maxDamage(0).setNoRepair());
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.EPIC;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return NBTHelper.getBoolean("enabled", stack);
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 500;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BLOCK;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack medallion, @Nullable World world, List<ITextComponent> tooltip) {
		int experience = NBTHelper.getInt(EXPERIENCE_TAG, medallion);
		int levels = XpHelper.getLevelForExperience(experience);
		int remainingExperience = experience - XpHelper.getExperienceForLevel(levels);

		LanguageHelper.formatTooltip(getTranslationKey() + ".tooltip2", ImmutableMap.of("levels", String.valueOf(levels), EXPERIENCE_TAG, String.valueOf(remainingExperience)), tooltip);
		if (isEnabled(medallion)) {
			LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", TextFormatting.GREEN + "XP"), tooltip);
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
	public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		if (world.isRemote || !isEnabled(stack) || world.getGameTime() % 10 != 0) {
			return;
		}
		if (entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entity;
			if (!player.isHandActive() || player.getActiveItemStack() != stack) {
				drainExperienceLevels(stack, player, 1);
			}
		}
	}

	private void drainExperienceLevels(ItemStack stack, PlayerEntity player, int levelsToDrain) {
		int experiencePoints = player.isCreative() ? 100 : player.experienceTotal - XpHelper.getExperienceForLevel(Math.max(getExperienceMinimum(), player.experienceLevel - levelsToDrain));
		if (experiencePoints > 0) {
			if (!player.isCreative()) {
				decreasePlayerExperience(player, experiencePoints);
			}
			increaseMedallionExperience(stack, experiencePoints);
		}
	}

	private void decreasePlayerExperience(PlayerEntity player, int pointsToRemove) {
		player.experienceTotal -= pointsToRemove;
		int newLevel = XpHelper.getLevelForExperience(player.experienceTotal);
		player.experienceLevel = newLevel;
		player.experience = (float) (player.experienceTotal - XpHelper.getExperienceForLevel(newLevel)) / player.xpBarCap();
	}

	private void decreaseMedallionExperience(ItemStack stack, int experience) {
		setExperience(stack, getExperience(stack) - experience);
	}

	private void increasePlayerExperience(PlayerEntity player, int xpPoints) {
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
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (world.isRemote) {
			return new ActionResult<>(ActionResultType.SUCCESS, stack);
		}
		if (player.isSneaking()) {
			return super.onItemRightClick(world, player, hand);
		}

		player.setActiveHand(hand);

		return new ActionResult<>(ActionResultType.SUCCESS, stack);
	}

	@Override
	public void onUsingTick(ItemStack stack, LivingEntity entity, int count) {
		int cooldown = count > 20 ? 10 : 20;
		if (entity.world.isRemote || !(entity instanceof PlayerEntity) || entity.world.getGameTime() % cooldown != 0) {
			return;
		}

		PlayerEntity player = (PlayerEntity) entity;

		World world = player.world;
		if (isEnabled(stack)) {
			drainExperienceLevels(stack, player, 10);
			return;
		}
		increaseExperience(stack, player, world, 10);
	}

	private void increaseExperience(ItemStack stack, PlayerEntity player, World world, int levels) {
		BlockRayTraceResult rayTraceResult = rayTrace(world, player, RayTraceContext.FluidMode.ANY);

		if (rayTraceResult.getType() == RayTraceResult.Type.BLOCK) {
			BlockPos hitPos = rayTraceResult.getPos().add(rayTraceResult.getFace().getDirectionVec());
			spawnXpOnGround(stack, world, hitPos);
		} else if (player.experienceLevel < getExperienceMaximum()) {
			levels += Math.round(player.experience);
			int maxPoints = XpHelper.getExperienceForLevel(player.experienceLevel + levels) - player.experienceTotal;
			int pointsToAdd = player.isCreative() ? maxPoints : Math.min(maxPoints, getExperience(stack));
			increasePlayerExperience(player, pointsToAdd);
			if (!player.isCreative()) {
				decreaseMedallionExperience(stack, pointsToAdd);
			}
		}
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
		if (entityLiving.world.isRemote || isEnabled(stack) || !(entityLiving instanceof PlayerEntity) || getUseDuration(stack) - timeLeft > 10) {
			return;
		}

		increaseExperience(stack, (PlayerEntity) entityLiving, entityLiving.world, 1);
	}

	private void spawnXpOnGround(ItemStack stack, World world, BlockPos hitPos) {
		int xp = Math.min(Settings.COMMON.items.heroMedallion.experienceDrop.get(), getExperience(stack));

		if (getExperience(stack) >= xp) {
			decreaseMedallionExperience(stack, xp);

			while (xp > 0) {
				int j = ExperienceOrbEntity.getXPSplit(xp);
				xp -= j;
				world.addEntity(new ExperienceOrbEntity(world, hitPos.getX(), hitPos.getY(), hitPos.getZ(), j));
			}
		}
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return new FluidHandlerHeroMedallion(stack);
	}

	@Override
	public void update(ItemStack stack, IPedestal pedestal) {
		List<BlockPos> posInRange = pedestal.getPedestalsInRange(Settings.COMMON.items.heroMedallion.pedestalRange.get());
		World world = pedestal.getTheWorld();

		for (BlockPos pedestalPos : posInRange) {
			InventoryHelper.getInventoryAtPos(world, pedestalPos).ifPresent(pedestalInventory -> {
				List<ItemStack> toRepair = getMendingItemsForRepair(pedestalInventory);

				for (ItemStack itemToRepair : toRepair) {
					int xpToRepair = Math.min(Settings.COMMON.items.heroMedallion.pedestalRepairStepXP.get(), getExperience(stack));
					int durabilityToRepair = Math.min(XpHelper.xpToDurability(xpToRepair), itemToRepair.getDamage());

					setExperience(stack, getExperience(stack) - XpHelper.durabilityToXp(durabilityToRepair));
					itemToRepair.setDamage(itemToRepair.getDamage() - durabilityToRepair);
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
			if (stack.isDamaged() && stack.getDamage() > 1 && EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack) > 0) {
				stacksToReturn.add(stack);
			}
		}

		return stacksToReturn;
	}

	@Override
	public void onRemoved(ItemStack stack, IPedestal pedestal) {
		//nothing needed
	}

	@Override
	public void stop(ItemStack stack, IPedestal pedestal) {
		//nothing needed
	}
}
