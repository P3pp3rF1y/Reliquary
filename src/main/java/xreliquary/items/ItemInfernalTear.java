package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import xreliquary.Reliquary;
import xreliquary.crafting.factories.AlkahestryCraftingRecipeFactory.AlkahestryCraftingRecipe;
import xreliquary.init.XRRecipes;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemInfernalTear extends ItemToggleable {

	public ItemInfernalTear() {
		super(Names.Items.INFERNAL_TEAR);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxStackSize(1);
		canRepair = false;
	}

	@Override
	public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean flag) {
		if(world.isRemote || !isEnabled(ist))
			return;
		if(!(e instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) e;

		if(getStackFromTear(ist).isEmpty()) {
			resetTear(ist);
			return;
		}

		AlkahestryCraftingRecipe recipe = matchAlkahestryRecipe(getStackFromTear(ist));
		if(recipe == null) {
			resetTear(ist);
			return;
		}

		// You need above Cobblestone level to get XP.
		if(!(recipe.getRecipeOutput().getCount() == 33 && recipe.getChargeNeeded() == 4)) {
			if(InventoryHelper.consumeItem(this.getStackFromTear(ist), player)) {
				player.addExperience((int) (Math.ceil((((double) recipe.getChargeNeeded()) / (double) (recipe.getRecipeOutput().getCount() - 1)) / 256d * 500d)));
			}
		}
	}

	private AlkahestryCraftingRecipe matchAlkahestryRecipe(ItemStack stack) {
		for(AlkahestryCraftingRecipe recipe : XRRecipes.craftingRecipes) {
			if(recipe.getInput().apply(stack))
				return recipe;
		}
		return null;
	}

	private void resetTear(ItemStack ist) {
		NBTTagCompound tag = ist.getTagCompound();
		if (tag != null) {
			tag.removeTag("item");
			tag.removeTag("enabled");
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
			return;
		this.formatTooltip(null, stack, tooltip);

		if(this.getStackFromTear(stack).isEmpty()) {
			LanguageHelper.formatTooltip("tooltip.infernal_tear.tear_empty", null, tooltip);
		} else {
			ItemStack contents = this.getStackFromTear(stack);
			String itemName = contents.getDisplayName();

			LanguageHelper.formatTooltip("tooltip.tear", ImmutableMap.of("item", itemName), tooltip);

			if(this.isEnabled(stack)) {
				LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", TextFormatting.YELLOW + itemName), tooltip);
			}
			tooltip.add(LanguageHelper.getLocalization("tooltip.absorb"));
			tooltip.add(LanguageHelper.getLocalization("tooltip.infernal_tear.absorb_unset"));
		}
	}

	@Nonnull
	public ItemStack getStackFromTear(@Nonnull ItemStack tear) {
		NBTTagCompound itemNBT = NBTHelper.getTagCompound("item", tear);
		if(itemNBT.hasNoTags())
			return ItemStack.EMPTY;

		return new ItemStack(itemNBT);
	}

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		ActionResult<ItemStack> actionResult = super.onItemRightClick(world, player, hand);
		if(player.isSneaking() && !this.isEnabled(stack))
			return actionResult;

		ItemStack itemStack = actionResult.getResult();

		//empty the tear if player is not sneaking and the tear is not empty
		NBTTagCompound nbt = itemStack.getTagCompound();
		if(!player.isSneaking() && !getStackFromTear(itemStack).isEmpty()) {
			NBTHelper.removeTag(nbt, "item");
			NBTHelper.removeTag(nbt, "enabled");

			return actionResult;
		}

		//nothing more to do with a filled tear here
		if(!getStackFromTear(itemStack).isEmpty()) {
			return actionResult;
		}

		//if user is sneaking or just enabled the tear, let's fill it
		if(player.isSneaking() || !this.isEnabled(itemStack)) {
			ItemStack returnStack = this.buildTear(itemStack, player.inventory);
			if(!returnStack.isEmpty())
				return new ActionResult<>(EnumActionResult.SUCCESS, returnStack);
		}

		//by this time the tear is still empty and there wasn't anything to put in it
		// so let's disable it if it got enabled
		if(this.isEnabled(itemStack))
			this.toggleEnabled(itemStack);
		return actionResult;
	}

	@Nonnull
	private ItemStack buildTear(@Nonnull ItemStack stack, IInventory inventory) {
		ItemStack tear = new ItemStack(this, 1);

		ItemStack target = getTargetAlkahestItem(stack, inventory);
		if(target.isEmpty())
			return ItemStack.EMPTY;

		NBTHelper.setTagCompound("item", tear, target.writeToNBT(new NBTTagCompound()));

		if(Settings.Items.InfernalTear.absorbWhenCreated)
			NBTHelper.setBoolean("enabled", stack, true);

		return tear;
	}

	@Nonnull
	private ItemStack getTargetAlkahestItem(@Nonnull ItemStack self, IInventory inventory) {
		ItemStack targetItem = ItemStack.EMPTY;
		int itemQuantity = 0;
		for(int slot = 0; slot < inventory.getSizeInventory(); slot++) {
			ItemStack stack = inventory.getStackInSlot(slot);
			if(stack.isEmpty()) {
				continue;
			}
			if(self.isItemEqual(stack)) {
				continue;
			}
			if(stack.getMaxStackSize() == 1) {
				continue;
			}
			if(stack.getTagCompound() != null) {
				continue;
			}
			AlkahestryCraftingRecipe recipe = matchAlkahestryRecipe(stack);
			if(recipe == null || (recipe.getRecipeOutput().getCount() == 33 && recipe.getChargeNeeded() == 4))
				continue;
			if(InventoryHelper.getItemQuantity(stack, inventory) > itemQuantity) {
				itemQuantity = InventoryHelper.getItemQuantity(stack, inventory);
				targetItem = stack.copy();
			}
		}
		inventory.markDirty();
		return targetItem;
	}

}
