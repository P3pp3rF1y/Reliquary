package xreliquary.compat.jei;

import mezz.jei.api.*;
import net.minecraft.item.ItemStack;
import xreliquary.compat.jei.alkahestry.*;
import xreliquary.compat.jei.descriptions.DescriptionEntry;
import xreliquary.compat.jei.descriptions.JEIDescriptionRegistry;
import xreliquary.compat.jei.potions.*;
import xreliquary.init.ModItems;

@JEIPlugin
public class ReliquaryPlugin implements IModPlugin {

    public static IJeiHelpers jeiHelper;

    @Override
    public void register(IModRegistry registry)
    {
        registry.addRecipeCategories(new AlkahestryCraftingRecipeCategory());
        registry.addRecipeCategories(new AlkahestryChargingRecipeCategory());
        registry.addRecipeCategories(new MortarRecipeCategory());
        registry.addRecipeCategories(new CauldronRecipeCategory());

        registry.addRecipeHandlers(new AlkahestryCraftingRecipeHandler());
        registry.addRecipeHandlers(new AlkahestryChargingRecipeHandler());
        registry.addRecipeHandlers(new MortarRecipeHandler());
        registry.addRecipeHandlers(new CauldronRecipeHandler());

        registry.addRecipes(AlkahestryCraftingRecipeMaker.getRecipes());
        registry.addRecipes(AlkahestryChargingRecipeMaker.getRecipes());
        registry.addRecipes(MortarRecipeMaker.getRecipes());
        registry.addRecipes(CauldronRecipeMaker.getRecipes());

        for(DescriptionEntry entry : JEIDescriptionRegistry.entrySet())
            registry.addDescription(entry.itemStacks(), entry.langKey());

        //blacklist filled void tear
        jeiHelper.getItemBlacklist().addItemToBlacklist(new ItemStack(ModItems.filledVoidTear));
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