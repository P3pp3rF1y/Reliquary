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
import xreliquary.init.ModItems;
import xreliquary.init.ModSounds;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;
import xreliquary.util.alkahestry.AlkahestChargeRecipe;
import xreliquary.util.alkahestry.AlkahestRecipeType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

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
	public void onUpdate(ItemStack ist, World world, Entity entity, int i, boolean f) {
		if(world.isRemote)
			return;
		if(!this.isEnabled(ist))
			return;

		EntityPlayer player;
		if(entity instanceof EntityPlayer) {
			player = (EntityPlayer) entity;
		} else {
			return;
		}

		for(Map.Entry<String, AlkahestChargeRecipe> entry : Settings.AlkahestryTome.chargingRecipes.entrySet()) {
			AlkahestChargeRecipe recipe = entry.getValue();
			if(NBTHelper.getInteger("charge", ist) + recipe.charge <= getChargeLimit() && consumeItem(recipe, player)) {
				NBTHelper.setInteger("charge", ist, NBTHelper.getInteger("charge", ist) + recipe.charge);
			}
		}

		ist.setItemDamage(ist.getMaxDamage() - NBTHelper.getInteger("charge", ist));
	}

	private boolean consumeItem(AlkahestChargeRecipe recipe, EntityPlayer player) {
		if(recipe.type == AlkahestRecipeType.OREDICT) {
			return InventoryHelper.consumeOreDictItem(recipe.name, player);
		} else {
			return InventoryHelper.consumeItem(recipe.name, recipe.meta, recipe.type == AlkahestRecipeType.WILDCARD, player);
		}
	}

	@Override
	public void addInformation(ItemStack ist, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
			return;
		this.formatTooltip(ImmutableMap.of("chargeAmount", String.valueOf(NBTHelper.getInteger("charge", ist)), "chargeLimit", String.valueOf(getChargeLimit())), ist, tooltip);

		if(this.isEnabled(ist))
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

		ItemStack stack = new ItemStack(this);
		stack.setItemDamage(stack.getMaxDamage());
		subItems.add(stack);
	}

	private static int getChargeLimit() {
		return Settings.AlkahestryTome.chargeLimit;
	}

}
