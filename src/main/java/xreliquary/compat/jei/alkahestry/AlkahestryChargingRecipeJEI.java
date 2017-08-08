package xreliquary.compat.jei.alkahestry;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.ItemStack;
import xreliquary.crafting.factories.AlkahestryChargingRecipeFactory.AlkahestryChargingRecipe;
import xreliquary.init.ModItems;

import javax.annotation.Nonnull;
import java.util.List;

class AlkahestryChargingRecipeJEI extends BlankRecipeWrapper {
	private final ItemStack output;
	private final List<List<ItemStack>> inputs;

	public AlkahestryChargingRecipeJEI(IStackHelper stackHelper, AlkahestryChargingRecipe recipe) {
		this.inputs = stackHelper.expandRecipeItemStackInputs(recipe.getIngredients());
		ItemStack tome = new ItemStack(ModItems.alkahestryTome);
		ModItems.alkahestryTome.addCharge(tome, recipe.getChargeToAdd());
		this.output = tome;
	}

	@Override
	public void getIngredients(@Nonnull IIngredients ingredients) {
		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setOutput(ItemStack.class, output);
	}
}
