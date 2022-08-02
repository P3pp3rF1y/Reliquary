package reliquary.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import reliquary.common.gui.AlkahestTomeMenu;
import reliquary.crafting.AlkahestryChargingRecipe;
import reliquary.crafting.AlkahestryRecipeRegistry;
import reliquary.init.ModSounds;
import reliquary.reference.Settings;
import reliquary.util.LanguageHelper;
import reliquary.util.NBTHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class AlkahestryTomeItem extends ToggleableItem {
	public AlkahestryTomeItem() {
		super(new Properties().setNoRepair().rarity(Rarity.EPIC).stacksTo(1).durability(getChargeLimit() + 1), Settings.COMMON.disable.disableAlkahestry::get);
	}

	@Override
	public boolean canBeDepleted() {
		return true;
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
		return enchantment.category != EnchantmentCategory.BREAKABLE && super.canApplyAtEnchantingTable(stack, enchantment);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		ItemStack newStack = super.use(world, player, hand).getObject();
		if (player.isShiftKeyDown()) {
			return new InteractionResultHolder<>(InteractionResult.SUCCESS, newStack);
		}

		player.playSound(ModSounds.BOOK.get(), 1.0f, 1.0f);
		if (!world.isClientSide && player instanceof ServerPlayer serverPlayer) {
			NetworkHooks.openGui(serverPlayer, new SimpleMenuProvider((w, p, pl) -> new AlkahestTomeMenu(w), stack.getHoverName()));
		}
		return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
	}

	@Override
	public void inventoryTick(ItemStack tome, Level world, Entity entity, int itemSlot, boolean isSelected) {
		if (world.isClientSide || world.getGameTime() % 10 != 0 || !isEnabled(tome) || getCharge(tome) == getChargeLimit()) {
			return;
		}

		if (!(entity instanceof Player player)) {
			return;
		}

		for (AlkahestryChargingRecipe recipe : AlkahestryRecipeRegistry.getChargingRecipes()) {
			consumeAndCharge(player, getChargeLimit() - getCharge(tome), recipe.getChargeToAdd(),
					ist -> recipe.getChargingIngredient().test(ist), 16, chargeToAdd -> addCharge(tome, chargeToAdd));
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack tome, @Nullable Level world, List<Component> tooltip) {
		LanguageHelper.formatTooltip(getDescriptionId() + ".tooltip2",
				Map.of("chargeAmount", String.valueOf(getCharge(tome)), "chargeLimit", String.valueOf(getChargeLimit())), tooltip);

		if (isEnabled(tome)) {
			LanguageHelper.formatTooltip("tooltip.absorb_active", Map.of("item", ChatFormatting.RED + AlkahestryRecipeRegistry.getDrainRecipe()
					.map(r -> r.getResultItem().getHoverName().getString()).orElse("")), tooltip);
		} else {
			LanguageHelper.formatTooltip("tooltip.absorb", tooltip);
		}
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return true;
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

	public static void useCharge(ItemStack tome, int chargeToUse) {
		addCharge(tome, -chargeToUse);
	}
}
