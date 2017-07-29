package xreliquary.compat.waila.provider;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import xreliquary.blocks.BlockApothecaryMortar;
import xreliquary.blocks.tile.TileEntityMortar;
import xreliquary.init.ModBlocks;
import xreliquary.util.LanguageHelper;
import xreliquary.util.potions.PotionIngredient;
import xreliquary.util.potions.XRPotionHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class DataProviderMortar extends CachedBodyDataProvider {
	@Nonnull
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return new ItemStack(ModBlocks.apothecaryMortar);
	}

	@Override
	public List<String> getWailaBodyToCache(ItemStack itemStack, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		List<String> currenttip = new ArrayList<>();

		if(!(accessor.getBlock() instanceof BlockApothecaryMortar && accessor.getTileEntity() instanceof TileEntityMortar))
			return currenttip;

		TileEntityMortar mortar = (TileEntityMortar) accessor.getTileEntity();

		if(mortar == null)
			return currenttip;

		NonNullList<ItemStack> ingredients = mortar.getItemStacks();

		if(ingredients.get(0).isEmpty())
			return currenttip;

		String ingredient1 = ingredients.get(0).getDisplayName();
		String ingredient2 = ingredients.get(1).isEmpty() ? "" : ", " + ingredients.get(1).getDisplayName();
		String ingredient3 = ingredients.get(2).isEmpty() ? "" : ", " + ingredients.get(2).getDisplayName();

		currenttip.add(ingredient1 + ingredient2 + ingredient3);

		if(ingredients.get(1).isEmpty())
			return currenttip;

		List<PotionIngredient> potionIngredients = new ArrayList<>();
		potionIngredients.add(XRPotionHelper.getIngredient(ingredients.get(0)));
		potionIngredients.add(XRPotionHelper.getIngredient(ingredients.get(1)));
		if(!ingredients.get(2).isEmpty())
			potionIngredients.add(XRPotionHelper.getIngredient(ingredients.get(2)));

		List<PotionEffect> effects = XRPotionHelper.combineIngredients(potionIngredients);
		if(!effects.isEmpty()) {
			currenttip.add(TextFormatting.WHITE + LanguageHelper.getLocalization("waila.xreliquary.mortar.result") + TextFormatting.RESET);

			XRPotionHelper.addPotionTooltip(effects, currenttip, false, false);
		}
		return currenttip;
	}
}
