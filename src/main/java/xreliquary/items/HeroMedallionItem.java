package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
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

	public HeroMedallionItem() {
		super("hero_medallion", new Properties().maxDamage(0).setNoRepair());
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
	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack medallion, @Nullable World world, List<ITextComponent> tooltip) {
		int experience = NBTHelper.getInt("experience", medallion);
		int levels = XpHelper.getLevelForExperience(experience);
		int remainingExperience = experience - XpHelper.getExperienceForLevel(levels);

		LanguageHelper.formatTooltip(getTranslationKey() + ".tooltip2", ImmutableMap.of("levels", String.valueOf(levels), "experience", String.valueOf(remainingExperience)), tooltip);
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
		if (!isEnabled(stack)) {
			return;
		}
		if (entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entity;
			// in order to make this stop at a specific level, we will need to do
			// a preemptive check for a specific level.
			for (int levelLoop = 0; levelLoop <= Math.sqrt(!player.isCreative() ? player.experienceLevel : 30); ++levelLoop) {
				if ((player.experienceLevel > getExperienceMinimum() || player.experience >= (1F / player.xpBarCap()) || player.isCreative()) && getExperience(stack) < Settings.COMMON.items.heroMedallion.experienceLimit.get()) {
					if (!player.isCreative()) {
						decreasePlayerExperience(player);
					}
					increaseMedallionExperience(stack);
				}
			}
		}
	}

	private void decreasePlayerExperience(PlayerEntity player) {
		player.experience -= 1F / (float) player.xpBarCap();
		player.experienceTotal -= Math.min(1, player.experienceTotal);

		if (player.experience < 0F) {
			decreasePlayerLevel(player);
		}
	}

	private void decreaseMedallionExperience(ItemStack stack) {
		decreaseMedallionExperience(stack, 1);
	}

	private void decreaseMedallionExperience(ItemStack stack, int experience) {
		setExperience(stack, getExperience(stack) - experience);
	}

	private void decreasePlayerLevel(PlayerEntity player) {
		float experienceToRemove = -player.experience * player.xpBarCap();
		player.experienceLevel -= 1;
		player.experience = 1F - (experienceToRemove / player.xpBarCap());
	}

	private void increasePlayerExperience(PlayerEntity player) {
		player.giveExperiencePoints(1);
	}

	private void increaseMedallionExperience(ItemStack stack) {
		setExperience(stack, getExperience(stack) + 1);
	}

	public int getExperience(ItemStack stack) {
		return NBTHelper.getInt("experience", stack);
	}

	public void setExperience(ItemStack stack, int i) {
		NBTHelper.putInt("experience", stack, i);
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

		BlockRayTraceResult rayTraceResult = rayTrace(world, player, RayTraceContext.FluidMode.ANY);

		if (rayTraceResult.getType() != RayTraceResult.Type.BLOCK) {
			int playerLevel = player.experienceLevel;
			while (player.experienceLevel < getExperienceMaximum() && playerLevel == player.experienceLevel && (getExperience(stack) > 0 || player.isCreative())) {
				increasePlayerExperience(player);
				if (!player.isCreative()) {
					decreaseMedallionExperience(stack);
				}
			}
		} else {
			BlockPos hitPos = rayTraceResult.getPos().add(rayTraceResult.getFace().getDirectionVec());
			spawnXpOnGround(stack, world, hitPos);
		}

		return new ActionResult<>(ActionResultType.SUCCESS, stack);
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
