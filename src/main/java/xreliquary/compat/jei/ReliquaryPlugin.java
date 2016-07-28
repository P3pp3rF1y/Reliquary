package xreliquary.compat.jei;

import mezz.jei.api.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import xreliquary.compat.jei.alkahestry.*;
import xreliquary.compat.jei.cauldron.CauldronRecipeCategory;
import xreliquary.compat.jei.cauldron.CauldronRecipeHandler;
import xreliquary.compat.jei.cauldron.CauldronRecipeMaker;
import xreliquary.compat.jei.descriptions.DescriptionEntry;
import xreliquary.compat.jei.descriptions.JEIDescriptionRegistry;
import xreliquary.compat.jei.mortar.MortarRecipeCategory;
import xreliquary.compat.jei.mortar.MortarRecipeHandler;
import xreliquary.compat.jei.mortar.MortarRecipeMaker;
import xreliquary.init.ModItems;
import xreliquary.reference.Settings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@JEIPlugin
public class ReliquaryPlugin implements IModPlugin {

	@Override
	public void register(IModRegistry registry) {
		IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();

		boolean tomeEnabled = ModItems.alkahestryTome.getRegistryName() != null && !Settings.disabledItemsBlocks.contains(ModItems.alkahestryTome.getRegistryName().getResourcePath());

		if (tomeEnabled) {
			registry.addRecipeCategories(new AlkahestryCraftingRecipeCategory(guiHelper));
			registry.addRecipeCategories(new AlkahestryChargingRecipeCategory(guiHelper));

			registry.addRecipeHandlers(new AlkahestryCraftingRecipeHandler());
			registry.addRecipeHandlers(new AlkahestryChargingRecipeHandler());

			registry.addRecipes(AlkahestryCraftingRecipeMaker.getRecipes());
			registry.addRecipes(AlkahestryChargingRecipeMaker.getRecipes());
		}

		registry.addRecipeCategories(new MortarRecipeCategory(guiHelper));
		registry.addRecipeCategories(new CauldronRecipeCategory(guiHelper));

		registry.addRecipeHandlers(new MortarRecipeHandler());
		registry.addRecipeHandlers(new CauldronRecipeHandler());

		registry.addRecipes(MortarRecipeMaker.getRecipes());
		registry.addRecipes(CauldronRecipeMaker.getRecipes());

		for(DescriptionEntry entry : JEIDescriptionRegistry.entrySet())
			registry.addDescription(entry.itemStacks(), entry.langKey());

		//blacklisted items
		registry.getJeiHelpers().getItemBlacklist().addItemToBlacklist(new ItemStack(ModItems.filledVoidTear));
		for (byte i=0; i<13; i++) {
			registry.getJeiHelpers().getItemBlacklist().addItemToBlacklist(new ItemStack(ModItems.heartZhu, 1, i));
		}

		ISubtypeRegistry nbtRegistry = registry.getJeiHelpers().getSubtypeRegistry();

		nbtRegistry.useNbtForSubtypes(ModItems.nianZhu);
		nbtRegistry.useNbtForSubtypes(ModItems.potionEssence);

		nbtRegistry.registerNbtInterpreter(ModItems.potion, new ISubtypeRegistry.ISubtypeInterpreter() {
			@Nullable
			@Override
			public String getSubtypeInfo(@Nonnull ItemStack itemStack) {
				NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
				if(nbtTagCompound == null || nbtTagCompound.hasNoTags()) {
					return null;
				}
				return nbtTagCompound.toString();
			}
		});
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {

	}
}