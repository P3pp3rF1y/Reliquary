package xreliquary.crafting.factories;

import com.google.gson.JsonObject;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.commons.lang3.tuple.MutablePair;
import xreliquary.reference.Reference;
import xreliquary.util.potions.XRPotionHelper;

import javax.annotation.Nonnull;
import java.util.List;

import static xreliquary.crafting.factories.UseMetaEffectsIngredientFactory.UseMetaEffectsIngredient;

@SuppressWarnings("unused")
@MethodsReturnNonnullByDefault
public class UseMetaEffectsRecipeFactory implements IRecipeFactory {
	@Override
	public IRecipe parse(JsonContext context, JsonObject json) {
		ShapedOreRecipe recipe = ShapedOreRecipe.factory(context, json);

		CraftingHelper.ShapedPrimer primer = new CraftingHelper.ShapedPrimer();
		primer.height = recipe.getHeight();
		primer.width = recipe.getWidth();
		primer.input = recipe.getIngredients();
		primer.mirrored = JsonUtils.getBoolean(json, "mirrored", true);

		return new UseMetaEffectsRecipe(new ResourceLocation(Reference.MOD_ID, "use_meta_effects"), recipe.getRecipeOutput(), primer);
	}

	public static class UseMetaEffectsRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
		private final ResourceLocation group;
		private final ItemStack output;
		private final int width;
		private final int height;
		private final NonNullList<Ingredient> input;
		private final boolean mirrored;

		public UseMetaEffectsRecipe(ResourceLocation group, @Nonnull ItemStack result, CraftingHelper.ShapedPrimer primer) {
			this.group = group;
			this.output = result.copy();
			this.width = primer.width;
			this.height = primer.height;
			this.input = primer.input;
			this.mirrored = primer.mirrored;
		}

		@Nonnull
		@Override
		public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
			ItemStack newOutput = this.output.copy();

			MutablePair<Integer, List<PotionEffect>> targetMetaEffects = findMatchAndUpdateMetaEffects(inv);

			if(targetMetaEffects.getLeft() != -1) {
				newOutput.setItemDamage(targetMetaEffects.getLeft());
			}
			if(targetMetaEffects.getRight() != null) {
				XRPotionHelper.addPotionEffectsToStack(newOutput, targetMetaEffects.getRight());
			}

			return newOutput;
		}

		@Override
		public boolean canFit(int width, int height) {
			return width >= this.width && height >= this.height;
		}

		private MutablePair<Integer, List<PotionEffect>> findMatchAndUpdateMetaEffects(@Nonnull InventoryCrafting inv) {
			MutablePair<Integer, List<PotionEffect>> targetMetaEffects = new MutablePair<>(-1, null);
			for(int startX = 0; startX <= ShapedOreRecipe.MAX_CRAFT_GRID_WIDTH - width; startX++) {
				for(int startY = 0; startY <= ShapedOreRecipe.MAX_CRAFT_GRID_HEIGHT - height; ++startY) {
					reset(targetMetaEffects);
					if(checkMatchAndUpdateMetaEffects(inv, targetMetaEffects, startX, startY, false)) {
						return targetMetaEffects;
					}
					targetMetaEffects = new MutablePair<>(-1, null);
					if(checkMatchAndUpdateMetaEffects(inv, targetMetaEffects, startX, startY, true)) {
						return targetMetaEffects;
					}
				}
			}
			return new MutablePair<>(-1, null);
		}

		private void reset(MutablePair<Integer, List<PotionEffect>> targetMetaEffects) {
			targetMetaEffects.setLeft(-1);
			targetMetaEffects.setRight(null);
		}

		private boolean checkMatchAndUpdateMetaEffects(
				@Nonnull InventoryCrafting inv, MutablePair<Integer, List<PotionEffect>> targetMetaEffects, int startX, int startY, boolean mirror) {
			for(int x = 0; x < ShapedOreRecipe.MAX_CRAFT_GRID_WIDTH; x++) {
				for(int y = 0; y < ShapedOreRecipe.MAX_CRAFT_GRID_HEIGHT; y++) {
					int subX = x - startX;
					int subY = y - startY;

					Ingredient target = getTarget(subX, subY, mirror);

					if(target.apply(inv.getStackInRowAndColumn(x, y))) {
						updateTargetMetaEffects(inv, targetMetaEffects, x, y, target);
					} else {
						return false;
					}
				}
			}
			return true;
		}

		@Override
		public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world) {
			for(int x = 0; x <= ShapedOreRecipe.MAX_CRAFT_GRID_WIDTH - width; x++) {
				for(int y = 0; y <= ShapedOreRecipe.MAX_CRAFT_GRID_HEIGHT - height; ++y) {
					if(checkMatch(inv, x, y, false)) {
						return true;
					}

					if(mirrored && checkMatch(inv, x, y, true)) {
						return true;
					}
				}
			}

			return false;
		}

		private boolean checkMatch(InventoryCrafting inv, int startX, int startY, boolean mirror) {
			MutablePair<Integer, List<PotionEffect>> targetMetaEffects = new MutablePair<>(-1, null);

			for(int x = 0; x < ShapedOreRecipe.MAX_CRAFT_GRID_WIDTH; x++) {
				for(int y = 0; y < ShapedOreRecipe.MAX_CRAFT_GRID_HEIGHT; y++) {
					int subX = x - startX;
					int subY = y - startY;

					Ingredient target = getTarget(subX, subY, mirror);

					if(!target.apply(inv.getStackInRowAndColumn(x, y))) {
						return false;
					}
					if(!updateTargetMetaEffects(inv, targetMetaEffects, x, y, target)) {
						return false;
					}

				}
			}

			return true;
		}

		@Override
		public ItemStack getRecipeOutput() {
			return output;
		}

		@Override
		public NonNullList<Ingredient> getIngredients() {
			return input;
		}

		@Override
		public String getGroup() {
			return group.toString();
		}

		private Ingredient getTarget(int subX, int subY, boolean mirror) {
			if(subX >= 0 && subY >= 0 && subX < width && subY < height) {
				if(mirror) {
					return input.get(width - subX - 1 + subY * width);
				} else {
					return input.get(subX + subY * width);
				}
			}
			return Ingredient.EMPTY;
		}

		private boolean updateTargetMetaEffects(InventoryCrafting inv, MutablePair<Integer, List<PotionEffect>> targetMetaEffects, int x, int y, Ingredient target) {
			if(target instanceof UseMetaEffectsIngredient) {
				UseMetaEffectsIngredient useMetaEffectsTarget = (UseMetaEffectsIngredient) target;

				ItemStack invStack = inv.getStackInRowAndColumn(x, y);
				if(useMetaEffectsTarget.isUseMeta()) {
					if(targetMetaEffects.getLeft() == -1) {
						targetMetaEffects.setLeft(invStack.getMetadata());
					} else if (targetMetaEffects.getLeft() != invStack.getMetadata()) {
						return false; // Two items with different meta marked as to be copied
					}
				}

				if(useMetaEffectsTarget.isUseEffects()) {
					if(targetMetaEffects.getRight() == null) {
						targetMetaEffects.setRight(XRPotionHelper.changePotionEffectsDuration(XRPotionHelper.getPotionEffectsFromCompoundTag(invStack.getTagCompound()), useMetaEffectsTarget.getFactor()));
					} else if (!XRPotionHelper.changePotionEffectsDuration(XRPotionHelper.getPotionEffectsFromCompoundTag(invStack.getTagCompound()), useMetaEffectsTarget.getFactor()).equals(targetMetaEffects.getRight())){
						return false; // Two items with different Effects marked as to be copied
					}
				}
			}
			return true;
		}

		@Override
		public boolean isDynamic() {
			return true;
		}
	}
}
