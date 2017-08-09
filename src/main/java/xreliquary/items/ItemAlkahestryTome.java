package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import xreliquary.Reliquary;
import xreliquary.common.gui.GUIHandler;
import xreliquary.crafting.factories.AlkahestryChargingRecipeFactory.AlkahestryChargingRecipe;
import xreliquary.init.ModSounds;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemAlkahestryTome extends ItemToggleable {
	public ItemAlkahestryTome() {
		super(Names.Items.ALKAHESTRY_TOME);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxStackSize(1);
		this.setMaxDamage(getChargeLimit() + 1); //to always display damage bar
		this.canRepair = false;
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		ItemStack newStack = super.onItemRightClick(world, player, hand).getResult();
		if(player.isSneaking())
			return new ActionResult<>(EnumActionResult.SUCCESS, newStack);

		player.playSound(ModSounds.book, 1.0f, 1.0f);
		player.openGui(Reliquary.INSTANCE, GUIHandler.ALKAHESTRY_TOME, world, (int) player.posX, (int) player.posY, (int) player.posZ);
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public void onUpdate(ItemStack tome, World world, Entity entity, int i, boolean f) {
		if(world.isRemote)
			return;
		if(!this.isEnabled(tome))
			return;

		EntityPlayer player;
		if(entity instanceof EntityPlayer) {
			player = (EntityPlayer) entity;
		} else {
			return;
		}

		for(AlkahestryChargingRecipe recipe : Settings.AlkahestryTome.chargingRecipes) {
			if(getCharge(tome) + recipe.getChargeToAdd() <= getChargeLimit() && consumeItem(recipe, player)) {
				addCharge(tome, recipe.getChargeToAdd());
			}
		}
	}

	private boolean consumeItem(AlkahestryChargingRecipe recipe, EntityPlayer player) {
		return InventoryHelper.consumeItem(is -> recipe.getChargingIngredient().apply(is), player);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack tome, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
			return;
		this.formatTooltip(ImmutableMap.of("chargeAmount", String.valueOf(getCharge(tome)), "chargeLimit", String.valueOf(getChargeLimit())), tome, tooltip);

		if(this.isEnabled(tome))
			LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", TextFormatting.RED + Items.REDSTONE.getItemStackDisplayName(Settings.AlkahestryTome.baseItem)), tooltip);
		LanguageHelper.formatTooltip("tooltip.absorb", null, tooltip);
	}

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
	}

	@Override
	public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
		if (!isInCreativeTab(tab))
			return;

		ItemStack stack = new ItemStack(this);
		stack.setItemDamage(stack.getMaxDamage());
		subItems.add(stack);
	}

	private static int getChargeLimit() {
		return Settings.AlkahestryTome.chargeLimit;
	}

	public void setCharge(ItemStack tome, int charge) {
		NBTHelper.setInteger("charge", tome, charge);

		tome.setItemDamage(tome.getMaxDamage() - charge);
	}

	public int getCharge(ItemStack tome) {
		return NBTHelper.getInteger("charge", tome);
	}

	public void addCharge(ItemStack tome, int chageToAdd) {
		setCharge(tome, getCharge(tome) + chageToAdd);
	}

	public void useCharge(ItemStack tome, int chargeToUse) {
		addCharge(tome, -chargeToUse);
	}
}
