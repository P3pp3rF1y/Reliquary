package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import xreliquary.common.gui.ContainerAlkahestTome;
import xreliquary.crafting.AlkahestryChargingRecipe;
import xreliquary.crafting.AlkahestryRecipeRegistry;
import xreliquary.init.ModSounds;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;

import javax.annotation.Nullable;
import java.util.List;

public class AlkahestryTomeItem extends ToggleableItem {
	public AlkahestryTomeItem() {
		super(Names.Items.ALKAHESTRY_TOME, new Properties().setNoRepair().rarity(Rarity.EPIC));
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return getChargeLimit() + 1;
	}

	@Override
	public int getDamage(ItemStack stack) {
		return getMaxDamage(stack) - getCharge(stack);
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return enchantment.type != EnchantmentType.BREAKABLE && super.canApplyAtEnchantingTable(stack, enchantment);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		ItemStack newStack = super.onItemRightClick(world, player, hand).getResult();
		if (player.isShiftKeyDown()) {
			return new ActionResult<>(ActionResultType.SUCCESS, newStack);
		}

		player.playSound(ModSounds.book, 1.0f, 1.0f);
		if (!world.isRemote && player instanceof ServerPlayerEntity) {
			NetworkHooks.openGui((ServerPlayerEntity) player, new SimpleNamedContainerProvider((w, p, pl) -> new ContainerAlkahestTome(w), stack.getDisplayName()));
		}
		return new ActionResult<>(ActionResultType.SUCCESS, stack);
	}

	@Override
	public void inventoryTick(ItemStack tome, World world, Entity entity, int itemSlot, boolean isSelected) {
		if (world.isRemote || !isEnabled(tome)) {
			return;
		}

		PlayerEntity player;
		if (entity instanceof PlayerEntity) {
			player = (PlayerEntity) entity;
		} else {
			return;
		}

		for (AlkahestryChargingRecipe recipe : AlkahestryRecipeRegistry.getChargingRecipes()) {
			if (getCharge(tome) + recipe.getChargeToAdd() <= getChargeLimit() && consumeItem(recipe, player)) {
				addCharge(tome, recipe.getChargeToAdd());
			}
		}
	}

	private boolean consumeItem(AlkahestryChargingRecipe recipe, PlayerEntity player) {
		return InventoryHelper.consumeItem(is -> recipe.getChargingIngredient().test(is), player);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack tome, @Nullable World world, List<ITextComponent> tooltip) {
		LanguageHelper.formatTooltip(getTranslationKey() + ".tooltip2",
				ImmutableMap.of("chargeAmount", String.valueOf(getCharge(tome)), "chargeLimit", String.valueOf(getChargeLimit())), tooltip);

		if (isEnabled(tome)) {
			LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", TextFormatting.RED + AlkahestryRecipeRegistry.getDrainRecipe().getRecipeOutput().getDisplayName().getString()), tooltip);
		} else {
			LanguageHelper.formatTooltip("tooltip.absorb", tooltip);
		}
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return true;
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if (!isInGroup(group)) {
			return;
		}

		ItemStack stack = new ItemStack(this);
		items.add(stack);
	}

	public static int getChargeLimit() {
		return Settings.COMMON.items.alkahestryTome.chargeLimit.get();
	}

	public static ItemStack setCharge(ItemStack tome, int charge) {
		NBTHelper.putInt("charge", tome, charge);
		return tome;
	}

	public static int getCharge(ItemStack tome) {
		return NBTHelper.getInt("charge", tome);
	}

	public static void addCharge(ItemStack tome, int chageToAdd) {
		setCharge(tome, getCharge(tome) + chageToAdd);
	}

	public void useCharge(ItemStack tome, int chargeToUse) {
		addCharge(tome, -chargeToUse);
	}
}
