package reliquary.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import reliquary.init.ModItems;
import reliquary.item.util.IPotionItem;
import reliquary.util.potions.PotionHelper;

import java.util.Optional;

public class PotionEffectsRecipe implements CraftingRecipe {
	private final ShapedRecipePattern pattern;
	private final ItemStackTemplate result;
	private final String group;
	private final float potionDurationFactor;

	public PotionEffectsRecipe(String group, ShapedRecipePattern pattern, ItemStackTemplate result, float potionDurationFactor) {
		this.group = group;
		this.pattern = pattern;
		this.result = result;
		this.potionDurationFactor = potionDurationFactor;
	}

	@Override
	public ItemStack assemble(CraftingInput inv) {
		ItemStack newOutput = result.create();

		findMatchAndUpdatePotionContents(inv).ifPresent(potionContents -> PotionHelper.addPotionContentsToStack(newOutput, potionContents));

		return newOutput;
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

				ItemStack stack = inv.getItem(x + y * inv.width());
				if (testIngredient(subX, subY, mirror, stack)) {
					targetPotionContents = updateTargetEffects(stack, targetPotionContents).getSecond();
				} else {
					return Optional.empty();
				}
			}
		}
		return Optional.of(targetPotionContents);
	}

	private boolean testIngredient(int subX, int subY, boolean mirror, ItemStack stack) {
		if (subX >= 0 && subY >= 0 && subX < pattern.width() && subY < pattern.height()) {
			if (mirror) {
				return pattern.ingredients().get(pattern.width() - subX - 1 + subY * pattern.width()).map(i -> i.test(stack)).orElse(stack.isEmpty());
			} else {
				return pattern.ingredients().get(subX + subY * pattern.width()).map(i -> i.test(stack)).orElse(stack.isEmpty());
			}
		}
		return false;
	}

	@Override
	public boolean matches(CraftingInput inv, Level level) {
		return pattern.matches(inv);
	}

	@Override
	public PlacementInfo placementInfo() {
		return PlacementInfo.createFromOptionals(pattern.ingredients());
	}

	private Pair<Boolean, PotionContents> updateTargetEffects(ItemStack stack, PotionContents targetPotionContents) {
		if (stack.getItem() instanceof IPotionItem potionItem) {
			PotionContents potionContents = potionItem.getPotionContents(stack);
			if (!potionContents.hasEffects()) {
				return Pair.of(true, targetPotionContents);
			}

			if (!targetPotionContents.hasEffects()) {
				targetPotionContents = PotionHelper.changePotionEffectsDuration(potionContents, potionDurationFactor);
			} else {
				return Pair.of(PotionHelper.changePotionEffectsDuration(potionContents, potionDurationFactor).equals(targetPotionContents), targetPotionContents); // Two items with different MobEffects marked as to be copied
			}
		}
		return Pair.of(true, targetPotionContents);
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public boolean showNotification() {
		return false;
	}

	@Override
	public RecipeSerializer<? extends CraftingRecipe> getSerializer() {
		return ModItems.POTION_EFFECTS_SERIALIZER.get();
	}

	@Override
	public CraftingBookCategory category() {
		return CraftingBookCategory.MISC;
	}

	@Override
	public String group() {
		return group;
	}

	public ShapedRecipePattern getPattern() {
		return pattern;
	}

	public ItemStackTemplate getResult() {
		return result;
	}

	public float getPotionDurationFactor() {
		return potionDurationFactor;
	}

	public static final MapCodec<PotionEffectsRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
				instance -> instance.group(
							Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
							ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.pattern),
							ItemStackTemplate.CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
								Codec.FLOAT.fieldOf("duration_factor").forGetter(recipe -> recipe.potionDurationFactor)
					)
					.apply(instance, PotionEffectsRecipe::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, PotionEffectsRecipe> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8,
				PotionEffectsRecipe::group,
				ShapedRecipePattern.STREAM_CODEC,
				PotionEffectsRecipe::getPattern,
				ItemStackTemplate.STREAM_CODEC,
				PotionEffectsRecipe::getResult,
				ByteBufCodecs.FLOAT,
				PotionEffectsRecipe::getPotionDurationFactor,
				PotionEffectsRecipe::new
		);
}
