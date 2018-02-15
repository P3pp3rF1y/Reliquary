package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.entities.EntitySpecialSnowball;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemIceMagusRod extends ItemToggleable {

	public ItemIceMagusRod() {
		super(Names.Items.ICE_MAGUS_ROD);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxStackSize(1);
		canRepair = false;
	}

	@Override
	protected void addMoreInformation(ItemStack rod, @Nullable World world, List<String> tooltip) {
		LanguageHelper.formatTooltip(getUnlocalizedNameInefficiently(rod) + ".tooltip2", ImmutableMap.of("charge", Integer.toString(NBTHelper.getInteger("snowballs", rod))), tooltip);
		if(this.isEnabled(rod))
			LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", TextFormatting.BLUE + Items.SNOWBALL.getItemStackDisplayName(new ItemStack(Items.SNOWBALL))), tooltip);
		LanguageHelper.formatTooltip("tooltip.absorb", null, tooltip);
	}

	ItemIceMagusRod(String langName) {
		super(langName);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxStackSize(1);
		canRepair = false;
	}

	private int getSnowballCap() {
		return this instanceof ItemGlacialStaff ? Settings.Items.GlacialStaff.snowballLimit : Settings.Items.IceMagusRod.snowballLimit;
	}

	int getSnowballCost() {
		return this instanceof ItemGlacialStaff ? Settings.Items.GlacialStaff.snowballCost : Settings.Items.IceMagusRod.snowballCost;
	}

	private int getSnowballWorth() {
		return this instanceof ItemGlacialStaff ? Settings.Items.GlacialStaff.snowballWorth : Settings.Items.IceMagusRod.snowballWorth;
	}

	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack ist) {
		return false;
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack ist = player.getHeldItem(hand);
		//acts as a cooldown.
		if(player.isSwingInProgress)
			return new ActionResult<>(EnumActionResult.PASS, ist);
		player.swingArm(hand);
		if(!player.isSneaking()) {
			if(NBTHelper.getInteger("snowballs", ist) >= getSnowballCost() || player.capabilities.isCreativeMode) {
				world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
				EntitySpecialSnowball snowball = new EntitySpecialSnowball(world, player, this instanceof ItemGlacialStaff);
				snowball.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 2.4F, 1.0F);
				world.spawnEntity(snowball);
				if(!player.capabilities.isCreativeMode)
					NBTHelper.setInteger("snowballs", ist, NBTHelper.getInteger("snowballs", ist) - getSnowballCost());
			}
		}
		return super.onItemRightClick(world, player, hand);
	}

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
	}

	@Override
	public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean b) {
		if(world.isRemote)
			return;
		EntityPlayer player = null;
		if(e instanceof EntityPlayer) {
			player = (EntityPlayer) e;
		}
		if(player == null)
			return;

		if(this.isEnabled(ist)) {
			if(NBTHelper.getInteger("snowballs", ist) + getSnowballWorth() <= getSnowballCap()) {
				if(InventoryHelper.consumeItem(new ItemStack(Items.SNOWBALL), player)) {
					NBTHelper.setInteger("snowballs", ist, NBTHelper.getInteger("snowballs", ist) + getSnowballWorth());
				}
			}
		}
	}
}
