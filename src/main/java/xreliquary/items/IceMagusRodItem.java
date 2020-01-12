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
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;

import javax.annotation.Nullable;
import java.util.List;

public class IceMagusRodItem extends ToggleableItem {
	public IceMagusRodItem() {
		this(Names.Items.ICE_MAGUS_ROD);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack rod, @Nullable World world, List<ITextComponent> tooltip) {
		LanguageHelper.formatTooltip(getRegistryName() + ".tooltip2", ImmutableMap.of("charge", Integer.toString(NBTHelper.getInt("snowballs", rod))), tooltip);
		if (isEnabled(rod)) {
			LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", TextFormatting.BLUE + Items.SNOWBALL.getDisplayName(new ItemStack(Items.SNOWBALL)).toString()), tooltip);
		}
		LanguageHelper.formatTooltip("tooltip.absorb", null, tooltip);
	}

	IceMagusRodItem(String langName) {
		super(langName, new Properties().maxStackSize(1).setNoRepair());
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
		if (!player.isSneaking()) {
			if (NBTHelper.getInt("snowballs", stack) >= getSnowballCost() || player.isCreative()) {
				world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
				SpecialSnowballEntity snowball = new SpecialSnowballEntity(world, player, this instanceof GlacialStaffItem);
				snowball.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 2.4F, 1.0F);
				world.addEntity(snowball);
				if (!player.isCreative()) {
					NBTHelper.putInt("snowballs", stack, NBTHelper.getInt("snowballs", stack) - getSnowballCost());
				}
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

		if (isEnabled(rod)) {
			if (NBTHelper.getInt("snowballs", rod) + getSnowballWorth() <= getSnowballCap()) {
				if (InventoryHelper.consumeItem(new ItemStack(Items.SNOWBALL), (PlayerEntity) entity)) {
					NBTHelper.putInt("snowballs", rod, NBTHelper.getInt("snowballs", rod) + getSnowballWorth());
				}
			}
		}
	}
}
