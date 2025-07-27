package reliquary.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import reliquary.init.ModItems;
import reliquary.item.util.IPotionItem;
import reliquary.util.potions.PotionHelper;

import java.util.Optional;

public class PotionEffectsRecipe implements CraftingRecipe {
	private final ShapedRecipePattern pattern;
	private final ItemStack result;
	private final String group;
	private final float potionDurationFactor;

	public PotionEffectsRecipe(String group, ShapedRecipePattern pattern, ItemStack result, float potionDurationFactor) {
		this.group = group;
		this.pattern = pattern;
		this.result = result;
		this.potionDurationFactor = potionDurationFactor;
	}

	@Override
	public ItemStack assemble(CraftingInput inv, HolderLookup.Provider registries) {
		ItemStack newOutput = result.copy();

		findMatchAndUpdatePotionContents(inv).ifPresent(potionContents -> PotionHelper.addPotionContentsToStack(newOutput, potionContents));

		return newOutput;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width >= pattern.width() && height >= pattern.height();
	}

	private Optional<PotionContents> findMatchAndUpdatePotionContents(CraftingInput inv) {
		for (int startX = 0; startX <= inv.width() - pattern.width(); startX++) {
			for (int startY = 0; startY <= inv.width() - pattern.height(); ++startY) {
				Optional<PotionContents> ret = checkMatchAndUpdatePotionContents(inv, startX, startY, false);
				if (ret.isPresent()) {
					return ret;
				}
				ret = checkMatchAndUpdatePotionContents(inv, startX, startY, true);
				if (ret.isPresent()) {
					return ret;
				}
			}
		}
		return Optional.empty();
	}

	private Optional<PotionContents> checkMatchAndUpdatePotionContents(CraftingInput inv, int startX, int startY, boolean mirror) {
		PotionContents targetPotionContents = PotionContents.EMPTY;
		for (int x = 0; x < pattern.width(); x++) {
			for (int y = 0; y < pattern.height(); y++) {
				int subX = x - startX;
				int subY = y - startY;

				Ingredient target = getTarget(subX, subY, mirror);

				ItemStack stack = inv.getItem(x + y * inv.width());
				if (target.test(stack)) {
					targetPotionContents = updateTargetEffects(stack, targetPotionContents).getB();
				} else {
					return Optional.empty();
				}
			}
		}
		return Optional.of(targetPotionContents);
	}

	@Override
	public boolean matches(CraftingInput inv, Level level) {
		for (int x = 0; x <= inv.width() - pattern.width(); x++) {
			for (int y = 0; y <= inv.height() - pattern.height(); ++y) {
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

	private boolean checkMatch(CraftingInput inv, int startX, int startY, boolean mirror) {
		PotionContents targetPotionContents = PotionContents.EMPTY;
		for (int x = 0; x < inv.width(); x++) {
			for (int y = 0; y < inv.height(); y++) {
				int subX = x - startX;
				int subY = y - startY;

				Ingredient target = getTarget(subX, subY, mirror);

				ItemStack stack = inv.getItem(x + y * inv.width());
				if (!target.test(stack)) {
					return false;
				}
				Tuple<Boolean, PotionContents> result = updateTargetEffects(stack, targetPotionContents);
				if (!result.getA()) {
					return false;
				}
				targetPotionContents = result.getB();
			}
		}
		return true;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return result;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return pattern.ingredients();
	}

	private Ingredient getTarget(int subX, int subY, boolean mirror) {
		if (subX >= 0 && subY >= 0 && subX < pattern.width() && subY < pattern.height()) {
			if (mirror) {
				return pattern.ingredients().get(pattern.width() - subX - 1 + subY * pattern.width());
			} else {
				return pattern.ingredients().get(subX + subY * pattern.width());
			}
		}
		return Ingredient.EMPTY;
	}

	private Tuple<Boolean, PotionContents> updateTargetEffects(ItemStack stack, PotionContents targetPotionContents) {
		if (stack.getItem() instanceof IPotionItem potionItem) {
			PotionContents potionContents = potionItem.getPotionContents(stack);
			if (!potionContents.hasEffects()) {
				return new Tuple<>(true, targetPotionContents);
			}

			if (!targetPotionContents.hasEffects()) {
				targetPotionContents = PotionHelper.changePotionEffectsDuration(potionContents, potionDurationFactor);
			} else {
				return new Tuple<>(PotionHelper.changePotionEffectsDuration(potionContents, potionDurationFactor).equals(targetPotionContents), targetPotionContents); // Two items with different MobEffects marked as to be copied
			}
		}
		return new Tuple<>(true, targetPotionContents);
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

	public ShapedRecipePattern getPattern() {
		return pattern;
	}

	public ItemStack getResult() {
		return result;
	}

	public float getPotionDurationFactor() {
		return potionDurationFactor;
	}

	public static class Serializer implements RecipeSerializer<PotionEffectsRecipe> {
		private static final MapCodec<PotionEffectsRecipe> CODEC = RecordCodecBuilder.mapCodec(
				instance -> instance.group(
								Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
								ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.pattern),
								ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
								Codec.FLOAT.fieldOf("duration_factor").forGetter(recipe -> recipe.potionDurationFactor)
						)
						.apply(instance, PotionEffectsRecipe::new));

		private static final StreamCodec<RegistryFriendlyByteBuf, PotionEffectsRecipe> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8,
				PotionEffectsRecipe::getGroup,
				ShapedRecipePattern.STREAM_CODEC,
				PotionEffectsRecipe::getPattern,
				ItemStack.STREAM_CODEC,
				PotionEffectsRecipe::getResult,
				ByteBufCodecs.FLOAT,
				PotionEffectsRecipe::getPotionDurationFactor,
				PotionEffectsRecipe::new
		);

		@Override
		public MapCodec<PotionEffectsRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, PotionEffectsRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
