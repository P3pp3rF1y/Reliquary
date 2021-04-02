package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xreliquary.entities.SpecialSnowballEntity;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;

import javax.annotation.Nullable;
import java.util.List;

public class IceMagusRodItem extends ToggleableItem {
	private static final String SNOWBALLS_TAG = "snowballs";

	public IceMagusRodItem() {
		super(new Properties().maxStackSize(1).setNoRepair());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack rod, @Nullable World world, List<ITextComponent> tooltip) {
		LanguageHelper.formatTooltip(getTranslationKey() + ".tooltip2", ImmutableMap.of("charge", Integer.toString(NBTHelper.getInt(SNOWBALLS_TAG, rod))), tooltip);
		if (isEnabled(rod)) {
			LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", TextFormatting.BLUE + Items.SNOWBALL.getDisplayName(new ItemStack(Items.SNOWBALL)).toString()), tooltip);
		}
		LanguageHelper.formatTooltip("tooltip.absorb", null, tooltip);
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return true;
	}

	private int getSnowballCap() {
		return this instanceof GlacialStaffItem ? Settings.COMMON.items.glacialStaff.snowballLimit.get() : Settings.COMMON.items.iceMagusRod.snowballLimit.get();
	}

	int getSnowballCost() {
		return this instanceof GlacialStaffItem ? Settings.COMMON.items.glacialStaff.snowballCost.get() : Settings.COMMON.items.iceMagusRod.snowballCost.get();
	}

	private int getSnowballWorth() {
		return this instanceof GlacialStaffItem ? Settings.COMMON.items.glacialStaff.snowballWorth.get() : Settings.COMMON.items.iceMagusRod.snowballWorth.get();
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		//acts as a cooldown.
		if (player.isSwingInProgress) {
			return new ActionResult<>(ActionResultType.PASS, stack);
		}
		player.swingArm(hand);
		if (!player.isSneaking() && (NBTHelper.getInt(SNOWBALLS_TAG, stack) >= getSnowballCost() || player.isCreative())) {
			world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
			SpecialSnowballEntity snowball = new SpecialSnowballEntity(world, player, this instanceof GlacialStaffItem);
			snowball.func_234612_a_(player, player.rotationPitch, player.rotationYaw, 0.0F, 2.4F, 1.0F);
			world.addEntity(snowball);
			if (!player.isCreative()) {
				NBTHelper.putInt(SNOWBALLS_TAG, stack, NBTHelper.getInt(SNOWBALLS_TAG, stack) - getSnowballCost());
			}
		}
		return super.onItemRightClick(world, player, hand);
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.EPIC;
	}

	@Override
	public void inventoryTick(ItemStack rod, World world, Entity entity, int itemSlot, boolean isSelected) {
		if (world.isRemote) {
			return;
		}
		if (!(entity instanceof PlayerEntity)) {
			return;
		}

		if (isEnabled(rod) && NBTHelper.getInt(SNOWBALLS_TAG, rod) + getSnowballWorth() <= getSnowballCap()
				&& InventoryHelper.consumeItem(new ItemStack(Items.SNOWBALL), (PlayerEntity) entity)) {
			NBTHelper.putInt(SNOWBALLS_TAG, rod, NBTHelper.getInt(SNOWBALLS_TAG, rod) + getSnowballWorth());
		}
	}
}
