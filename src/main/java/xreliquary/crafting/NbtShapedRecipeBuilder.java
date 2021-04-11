package xreliquary.crafting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import xreliquary.util.RegistryHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class NbtShapedRecipeBuilder {
	private final Item result;
	private final int count;
	private final List<String> pattern = Lists.newArrayList();
	private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
	private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
	private String group;
	@Nullable
	private final CompoundNBT nbt;

	public NbtShapedRecipeBuilder(IItemProvider result, int count, @Nullable CompoundNBT nbt) {
		this.result = result.asItem();
		this.count = count;
		this.nbt = nbt;
	}

	public static NbtShapedRecipeBuilder shapedRecipe(ItemStack stack) {
		return shapedRecipe(stack.getItem(), stack.getCount(), stack.getTag());
	}

	public static NbtShapedRecipeBuilder shapedRecipe(IItemProvider resultIn, @Nullable CompoundNBT nbt) {
		return shapedRecipe(resultIn, 1, nbt);
	}

	public static NbtShapedRecipeBuilder shapedRecipe(IItemProvider resultIn, int countIn, @Nullable CompoundNBT nbt) {
		return new NbtShapedRecipeBuilder(resultIn, countIn, nbt);
	}

	public NbtShapedRecipeBuilder key(Character symbol, ITag<Item> tagIn) {
		return key(symbol, Ingredient.fromTag(tagIn));
	}

	public NbtShapedRecipeBuilder key(Character symbol, IItemProvider itemIn) {
		return key(symbol, Ingredient.fromItems(itemIn));
	}

	public NbtShapedRecipeBuilder key(Character symbol, Ingredient ingredientIn) {
		if (key.containsKey(symbol)) {
			throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
		} else if (symbol == ' ') {
			throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
		} else {
			key.put(symbol, ingredientIn);
			return this;
		}
	}

	public NbtShapedRecipeBuilder patternLine(String patternIn) {
		if (!pattern.isEmpty() && patternIn.length() != pattern.get(0).length()) {
			throw new IllegalArgumentException("Pattern must be the same width on every line!");
		} else {
			pattern.add(patternIn);
			return this;
		}
	}

	public NbtShapedRecipeBuilder addCriterion(String name, ICriterionInstance criterionIn) {
		advancementBuilder.withCriterion(name, criterionIn);
		return this;
	}

	public NbtShapedRecipeBuilder setGroup(String groupIn) {
		group = groupIn;
		return this;
	}

	public void build(Consumer<IFinishedRecipe> consumerIn) {
		build(consumerIn, RegistryHelper.getRegistryName(result));
	}

	public void build(Consumer<IFinishedRecipe> consumerIn, String save) {
		ResourceLocation resourcelocation = ForgeRegistries.ITEMS.getKey(result);
		if ((new ResourceLocation(save)).equals(resourcelocation)) {
			throw new IllegalStateException("Shaped Recipe " + save + " should remove its 'save' argument");
		} else {
			build(consumerIn, new ResourceLocation(save));
		}
	}

	public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
		validate(id);
		advancementBuilder.withParentId(new ResourceLocation("recipes/root")).withCriterion("has_the_recipe", RecipeUnlockedTrigger.create(id)).withRewards(AdvancementRewards.Builder.recipe(id)).withRequirementsStrategy(IRequirementsStrategy.OR);
		consumerIn.accept(new Result(id, result, count, nbt, group == null ? "" : group, pattern, key, advancementBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + result.getGroup().getPath() + "/" + id.getPath())));
	}

	private void validate(ResourceLocation id) {
		if (pattern.isEmpty()) {
			throw new IllegalStateException("No pattern is defined for shaped recipe " + id + "!");
		} else {
			Set<Character> set = Sets.newHashSet(key.keySet());
			set.remove(' ');

			for (String s : pattern) {
				for (int i = 0; i < s.length(); ++i) {
					char c0 = s.charAt(i);
					if (!key.containsKey(c0) && c0 != ' ') {
						throw new IllegalStateException("Pattern in recipe " + id + " uses undefined symbol '" + c0 + "'");
					}

					set.remove(c0);
				}
			}

			if (!set.isEmpty()) {
				throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + id);
			} else if (pattern.size() == 1 && pattern.get(0).length() == 1) {
				throw new IllegalStateException("Shaped recipe " + id + " only takes in a single item - should it be a shapeless recipe instead?");
			} else if (advancementBuilder.getCriteria().isEmpty()) {
				throw new IllegalStateException("No way of obtaining recipe " + id);
			}
		}
	}

	public static class Result implements IFinishedRecipe {
		private final ResourceLocation id;
		private final Item resultItem;
		private final int resultCount;
		@Nullable
		private final CompoundNBT resultNbt;
		private final String group;
		private final List<String> pattern;
		private final Map<Character, Ingredient> key;
		private final Advancement.Builder advancementBuilder;
		private final ResourceLocation advancementId;

		@SuppressWarnings("java:S107")
		public Result(ResourceLocation idIn, Item resultIn, int countIn, @Nullable
				CompoundNBT resultNbt, String groupIn, List<String> patternIn, Map<Character, Ingredient> keyIn, Advancement.Builder advancementBuilderIn, ResourceLocation advancementIdIn) {
			id = idIn;
			resultItem = resultIn;
			resultCount = countIn;
			this.resultNbt = resultNbt;
			group = groupIn;
			pattern = patternIn;
			key = keyIn;
			advancementBuilder = advancementBuilderIn;
			advancementId = advancementIdIn;
		}

		public void serialize(JsonObject json) {
			if (!group.isEmpty()) {
				json.addProperty("group", group);
			}

			JsonArray jsonarray = new JsonArray();

			for (String s : pattern) {
				jsonarray.add(s);
			}

			json.add("pattern", jsonarray);
			JsonObject jsonobject = new JsonObject();

			for (Map.Entry<Character, Ingredient> entry : key.entrySet()) {
				jsonobject.add(String.valueOf(entry.getKey()), entry.getValue().serialize());
			}

			json.add("key", jsonobject);
			JsonObject resultObject = new JsonObject();
			resultObject.addProperty("item", RegistryHelper.getRegistryName(resultItem).toString());
			if (resultCount > 1) {
				resultObject.addProperty("count", resultCount);
			}
			if (resultNbt != null) {
				resultObject.addProperty("nbt", resultNbt.toString());
			}

			json.add("result", resultObject);
		}

		public IRecipeSerializer<?> getSerializer() {
			return IRecipeSerializer.CRAFTING_SHAPED;
		}

		public ResourceLocation getID() {
			return id;
		}

		@Nullable
		public JsonObject getAdvancementJson() {
			return advancementBuilder.serialize();
		}

		@Nullable
		public ResourceLocation getAdvancementID() {
			return advancementId;
		}
	}
}
