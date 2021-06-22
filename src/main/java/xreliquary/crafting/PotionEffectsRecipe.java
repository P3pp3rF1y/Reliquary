package xreliquary.crafting;

import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;
import xreliquary.items.util.IPotionItem;
import xreliquary.util.potions.XRPotionHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PotionEffectsRecipe implements ICraftingRecipe {
	public static final Serializer SERIALIZER = new Serializer();
	private final ShapedRecipe compose;
	private final float potionDurationFactor;

	private PotionEffectsRecipe(ShapedRecipe compose, float potionDurationFactor) {
		this.compose = compose;
		this.potionDurationFactor = potionDurationFactor;
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory inv) {
		ItemStack newOutput = compose.getRecipeOutput().copy();

		findMatchAndUpdateEffects(inv).ifPresent(targetEffects -> XRPotionHelper.addPotionEffectsToStack(newOutput, targetEffects));

		return newOutput;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width >= compose.getRecipeWidth() && height >= compose.getRecipeHeight();
	}

	private Optional<List<EffectInstance>> findMatchAndUpdateEffects(CraftingInventory inv) {
		List<EffectInstance> targetEffects;
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

	private boolean checkMatchAndUpdateEffects(CraftingInventory inv, List<EffectInstance> targetEffects, int startX, int startY, boolean mirror) {
		for (int x = 0; x < compose.getRecipeWidth(); x++) {
			for (int y = 0; y < compose.getRecipeHeight(); y++) {
				int subX = x - startX;
				int subY = y - startY;

				Ingredient target = getTarget(subX, subY, mirror);

				if (target.test(inv.getStackInSlot(x + y * inv.getWidth()))) {
					updateTargetEffects(inv, targetEffects, x, y);
				} else {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public boolean matches(CraftingInventory inv, World world) {
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

	private boolean checkMatch(CraftingInventory inv, int startX, int startY, boolean mirror) {
		List<EffectInstance> targetEffects = new ArrayList<>();
		for (int x = 0; x < inv.getWidth(); x++) {
			for (int y = 0; y < inv.getHeight(); y++) {
				int subX = x - startX;
				int subY = y - startY;

				Ingredient target = getTarget(subX, subY, mirror);

				if (!target.test(inv.getStackInSlot(x + y * inv.getWidth()))) {
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
	public ItemStack getRecipeOutput() {
		return compose.getRecipeOutput();
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

	private boolean updateTargetEffects(CraftingInventory inv, List<EffectInstance> targetEffects, int x, int y) {
		ItemStack invStack = inv.getStackInSlot(x + y * inv.getWidth());
		if (invStack.getItem() instanceof IPotionItem) {
			List<EffectInstance> effects = ((IPotionItem) invStack.getItem()).getEffects(invStack);
			if (effects.isEmpty()) {
				return true;
			}

			if (targetEffects.isEmpty()) {
				targetEffects.addAll(XRPotionHelper.changePotionEffectsDuration(effects, potionDurationFactor));
			} else {
				return XRPotionHelper.changePotionEffectsDuration(effects, potionDurationFactor).equals(targetEffects); // Two items with different Effects marked as to be copied
			}
		}
		return true;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<PotionEffectsRecipe> {
		@Override
		public PotionEffectsRecipe read(ResourceLocation recipeId, JsonObject json) {
			return new PotionEffectsRecipe(IRecipeSerializer.CRAFTING_SHAPED.read(recipeId, json), JSONUtils.getFloat(json, "duration_factor", 1.0f));
		}

		@Nullable
		@Override
		public PotionEffectsRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
			//noinspection ConstantConditions - shaped recipe serializer always returns an instance of recipe despite IRecipeSerializer's null allowing contract
			return new PotionEffectsRecipe(IRecipeSerializer.CRAFTING_SHAPED.read(recipeId, buffer), buffer.readFloat());
		}

		@Override
		public void write(PacketBuffer buffer, PotionEffectsRecipe recipe) {
			IRecipeSerializer.CRAFTING_SHAPED.write(buffer, recipe.compose);
			buffer.writeFloat(recipe.potionDurationFactor);
		}
	}
}
