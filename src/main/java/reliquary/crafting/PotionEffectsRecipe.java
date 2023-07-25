package reliquary.crafting;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import reliquary.init.ModItems;
import reliquary.items.util.IPotionItem;
import reliquary.util.potions.XRPotionHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PotionEffectsRecipe implements CraftingRecipe {
	private final ShapedRecipe compose;
	private final float potionDurationFactor;

	private PotionEffectsRecipe(ShapedRecipe compose, float potionDurationFactor) {
		this.compose = compose;
		this.potionDurationFactor = potionDurationFactor;
	}

	@Override
	public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {
		ItemStack newOutput = compose.getResultItem(registryAccess).copy();

		findMatchAndUpdateEffects(inv).ifPresent(targetEffects -> XRPotionHelper.addPotionEffectsToStack(newOutput, targetEffects));

		return newOutput;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width >= compose.getRecipeWidth() && height >= compose.getRecipeHeight();
	}

	private Optional<List<MobEffectInstance>> findMatchAndUpdateEffects(CraftingContainer inv) {
		List<MobEffectInstance> targetEffects;
		for (int startX = 0; startX <= inv.getWidth() - compose.getRecipeWidth(); startX++) {
			for (int startY = 0; startY <= inv.getHeight() - compose.getRecipeHeight(); ++startY) {
				targetEffects = new ArrayList<>();
				if (checkMatchAndUpdateEffects(inv, targetEffects, startX, startY, false)) {
					return Optional.of(targetEffects);
				}
				targetEffects = new ArrayList<>();
				if (checkMatchAndUpdateEffects(inv, targetEffects, startX, startY, true)) {
					return Optional.of(targetEffects);
				}
			}
		}
		return Optional.empty();
	}

	private boolean checkMatchAndUpdateEffects(CraftingContainer inv, List<MobEffectInstance> targetEffects, int startX, int startY, boolean mirror) {
		for (int x = 0; x < compose.getRecipeWidth(); x++) {
			for (int y = 0; y < compose.getRecipeHeight(); y++) {
				int subX = x - startX;
				int subY = y - startY;

				Ingredient target = getTarget(subX, subY, mirror);

				if (target.test(inv.getItem(x + y * inv.getWidth()))) {
					updateTargetEffects(inv, targetEffects, x, y);
				} else {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public boolean matches(CraftingContainer inv, Level world) {
		for (int x = 0; x <= inv.getWidth() - compose.getRecipeWidth(); x++) {
			for (int y = 0; y <= inv.getHeight() - compose.getRecipeHeight(); ++y) {
				if (checkMatch(inv, x, y, false)) {
					return true;
				}

				if (checkMatch(inv, x, y, true)) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean checkMatch(CraftingContainer inv, int startX, int startY, boolean mirror) {
		List<MobEffectInstance> targetEffects = new ArrayList<>();
		for (int x = 0; x < inv.getWidth(); x++) {
			for (int y = 0; y < inv.getHeight(); y++) {
				int subX = x - startX;
				int subY = y - startY;

				Ingredient target = getTarget(subX, subY, mirror);

				if (!target.test(inv.getItem(x + y * inv.getWidth()))) {
					return false;
				}
				if (!updateTargetEffects(inv, targetEffects, x, y)) {
					return false;
				}

			}
		}
		return true;
	}

	@Override
	public ItemStack getResultItem(RegistryAccess registryAccess) {
		return compose.getResultItem(registryAccess);
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return compose.getIngredients();
	}

	@Override
	public ResourceLocation getId() {
		return compose.getId();
	}

	private Ingredient getTarget(int subX, int subY, boolean mirror) {
		if (subX >= 0 && subY >= 0 && subX < compose.getRecipeWidth() && subY < compose.getRecipeHeight()) {
			if (mirror) {
				return compose.getIngredients().get(compose.getRecipeWidth() - subX - 1 + subY * compose.getRecipeWidth());
			} else {
				return compose.getIngredients().get(subX + subY * compose.getRecipeWidth());
			}
		}
		return Ingredient.EMPTY;
	}

	private boolean updateTargetEffects(CraftingContainer inv, List<MobEffectInstance> targetEffects, int x, int y) {
		ItemStack invStack = inv.getItem(x + y * inv.getWidth());
		if (invStack.getItem() instanceof IPotionItem potionItem) {
			List<MobEffectInstance> effects = potionItem.getEffects(invStack);
			if (effects.isEmpty()) {
				return true;
			}

			if (targetEffects.isEmpty()) {
				targetEffects.addAll(XRPotionHelper.changePotionEffectsDuration(effects, potionDurationFactor));
			} else {
				return XRPotionHelper.changePotionEffectsDuration(effects, potionDurationFactor).equals(targetEffects); // Two items with different MobEffects marked as to be copied
			}
		}
		return true;
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModItems.POTION_EFFECTS_SERIALIZER.get();
	}

	@Override
	public CraftingBookCategory category() {
		return CraftingBookCategory.MISC;
	}

	public static class Serializer implements RecipeSerializer<PotionEffectsRecipe> {
		@Override
		public PotionEffectsRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			return new PotionEffectsRecipe(RecipeSerializer.SHAPED_RECIPE.fromJson(recipeId, json), GsonHelper.getAsFloat(json, "duration_factor", 1.0f));
		}

		@Nullable
		@Override
		public PotionEffectsRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			//noinspection ConstantConditions - shaped recipe serializer always returns an instance of recipe despite RecipeSerializer's null allowing contract
			return new PotionEffectsRecipe(RecipeSerializer.SHAPED_RECIPE.fromNetwork(recipeId, buffer), buffer.readFloat());
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, PotionEffectsRecipe recipe) {
			RecipeSerializer.SHAPED_RECIPE.toNetwork(buffer, recipe.compose);
			buffer.writeFloat(recipe.potionDurationFactor);
		}
	}
}
