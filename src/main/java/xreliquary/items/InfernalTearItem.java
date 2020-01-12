package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;
import xreliquary.crafting.AlkahestryCraftingRecipe;
import xreliquary.init.XRRecipes;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class InfernalTearItem extends ToggleableItem {
	private static final String ENABLED_TAG = "enabled";

	public InfernalTearItem() {
		super(Names.Items.INFERNAL_TEAR, new Properties().maxStackSize(1).setNoRepair());
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		if (world.isRemote || !isEnabled(stack) || !(entity instanceof PlayerEntity)) {
			return;
		}
		PlayerEntity player = (PlayerEntity) entity;

		if (getStackFromTear(stack).isEmpty()) {
			resetTear(stack);
			return;
		}

		Optional<AlkahestryCraftingRecipe> recipe = matchAlkahestryRecipe(getStackFromTear(stack));
		if (!recipe.isPresent()) {
			resetTear(stack);
			return;
		}

		AlkahestryCraftingRecipe matchedRecipe = recipe.get();

		// You need above Cobblestone level to get XP.
		if (!(matchedRecipe.getRecipeOutput().getCount() == 33 && matchedRecipe.getChargeNeeded() == 4) && InventoryHelper.consumeItem(getStackFromTear(stack), player)) {
			player.giveExperiencePoints((int) (Math.ceil((((double) matchedRecipe.getChargeNeeded()) / (double) (matchedRecipe.getRecipeOutput().getCount() - 1)) / 256d * 500d)));
		}
	}

	private Optional<AlkahestryCraftingRecipe> matchAlkahestryRecipe(ItemStack stack) {
		for (AlkahestryCraftingRecipe recipe : XRRecipes.craftingRecipes) {
			if (recipe.getCraftingIngredient().test(stack)) {
				return Optional.of(recipe);
			}
		}
		return Optional.empty();
	}

	private void resetTear(ItemStack stack) {
		CompoundNBT tag = stack.getTag();
		if (tag != null) {
			tag.remove("item");
			tag.remove(ENABLED_TAG);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip) {
		if (getStackFromTear(stack).isEmpty()) {
			LanguageHelper.formatTooltip("tooltip.infernal_tear.tear_empty", null, tooltip);
		} else {
			ItemStack contents = getStackFromTear(stack);
			String itemName = contents.getDisplayName().getString();

			LanguageHelper.formatTooltip("tooltip.tear", ImmutableMap.of("item", itemName), tooltip);

			if (isEnabled(stack)) {
				LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", TextFormatting.YELLOW + itemName), tooltip);
			}
			tooltip.add(new StringTextComponent(LanguageHelper.getLocalization("tooltip.absorb")));
			tooltip.add(new StringTextComponent(LanguageHelper.getLocalization("tooltip.infernal_tear.absorb_unset")));
		}
	}

	private ItemStack getStackFromTear(ItemStack tear) {
		CompoundNBT itemNBT = NBTHelper.getTagCompound("item", tear);
		if (itemNBT.isEmpty()) {
			return ItemStack.EMPTY;
		}

		return ItemStack.read(itemNBT);
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.EPIC;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		ActionResult<ItemStack> actionResult = super.onItemRightClick(world, player, hand);
		if (player.isSneaking() && !isEnabled(stack)) {
			return actionResult;
		}

		ItemStack itemStack = actionResult.getResult();

		//empty the tear if player is not sneaking and the tear is not empty
		CompoundNBT nbt = itemStack.getTag();
		if (!player.isSneaking() && !getStackFromTear(itemStack).isEmpty()) {
			NBTHelper.remove(nbt, "item");
			NBTHelper.remove(nbt, ENABLED_TAG);

			return actionResult;
		}

		//nothing more to do with a filled tear here
		if (!getStackFromTear(itemStack).isEmpty()) {
			return actionResult;
		}

		//if user is sneaking or just enabled the tear, let's fill it
		if (player.isSneaking() || !isEnabled(itemStack)) {
			ItemStack returnStack = InventoryHelper.getItemHandlerFrom(player).map(handler -> buildTear(itemStack, handler)).orElse(ItemStack.EMPTY);
			if (!returnStack.isEmpty()) {
				return new ActionResult<>(ActionResultType.SUCCESS, returnStack);
			}
		}

		//by this time the tear is still empty and there wasn't anything to put in it
		// so let's disable it if it got enabled
		if (isEnabled(itemStack)) {
			toggleEnabled(itemStack);
		}
		return actionResult;
	}

	private ItemStack buildTear(ItemStack stack, IItemHandler inventory) {
		ItemStack tear = new ItemStack(this, 1);

		ItemStack target = getTargetAlkahestItem(stack, inventory);
		if (target.isEmpty()) {
			return ItemStack.EMPTY;
		}

		NBTHelper.putTagCompound("item", tear, target.write(new CompoundNBT()));

		if (Settings.COMMON.items.infernalTear.absorbWhenCreated.get()) {
			NBTHelper.putBoolean(ENABLED_TAG, stack, true);
		}

		return tear;
	}

	private ItemStack getTargetAlkahestItem(ItemStack self, IItemHandler inventory) {
		ItemStack targetItem = ItemStack.EMPTY;
		int itemQuantity = 0;
		for (int slot = 0; slot < inventory.getSlots(); slot++) {
			ItemStack stack = inventory.getStackInSlot(slot);
			if (stack.isEmpty() || self.isItemEqual(stack) || stack.getMaxStackSize() == 1 || stack.getTag() != null
					|| !matchAlkahestryRecipe(stack).filter(recipe -> recipe.getRecipeOutput().getCount() != 33 || recipe.getChargeNeeded() != 4).isPresent()) {
				continue;
			}
			if (InventoryHelper.getItemQuantity(stack, inventory) > itemQuantity) {
				itemQuantity = InventoryHelper.getItemQuantity(stack, inventory);
				targetItem = stack.copy();
			}
		}
		return targetItem;
	}
}
