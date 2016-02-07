package xreliquary.compat.jei;

import mezz.jei.api.*;
import xreliquary.compat.jei.alkahestry.*;

@JEIPlugin
public class ReliquaryPlugin implements IModPlugin {

    public static IJeiHelpers jeiHelper;

    @Override
    public void register(IModRegistry registry) {
        registry.addRecipeCategories(new AlkahestryCraftingRecipeCategory());
        registry.addRecipeCategories(new AlkahestryChargingRecipeCategory());

        registry.addRecipeHandlers(new AlkahestryCraftingRecipeHandler());
        registry.addRecipeHandlers(new AlkahestryChargingRecipeHandler());

        registry.addRecipes(AlkahestryCraftingRecipeMaker.getRecipes());
        registry.addRecipes(AlkahestryChargingRecipeMaker.getRecipes());
    }

    @Override
    public void onJeiHelpersAvailable(IJeiHelpers jeiHelpers) {
        jeiHelper = jeiHelpers;
    }

    @Override
    public void onItemRegistryAvailable(IItemRegistry itemRegistry) {

    }

    @Override
    public void onRecipeRegistryAvailable(IRecipeRegistry recipeRegistry) {

    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {

    }
}
