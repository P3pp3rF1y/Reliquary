package reliquary.crafting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import reliquary.init.ModItems;
import reliquary.reference.Reference;
import reliquary.util.RegistryHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

public class MobCharmRecipeBuilder {
   private final List<String> pattern = Lists.newArrayList();
   private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
   private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();
   private String group;

   private MobCharmRecipeBuilder() {}

   public static MobCharmRecipeBuilder charmRecipe() {
      return new MobCharmRecipeBuilder();
   }

   public MobCharmRecipeBuilder key(Character symbol, Tag<Item> tagIn) {
      return key(symbol, Ingredient.of(tagIn));
   }

   public MobCharmRecipeBuilder key(Character symbol, ItemLike itemIn) {
      return key(symbol, Ingredient.of(itemIn));
   }

   public MobCharmRecipeBuilder key(Character symbol, Ingredient ingredientIn) {
      if (key.containsKey(symbol)) {
         throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
      } else if (symbol == ' ') {
         throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
      } else {
         key.put(symbol, ingredientIn);
         return this;
      }
   }

   public MobCharmRecipeBuilder patternLine(String patternIn) {
      if (!pattern.isEmpty() && patternIn.length() != pattern.get(0).length()) {
         throw new IllegalArgumentException("Pattern must be the same width on every line!");
      } else {
         pattern.add(patternIn);
         return this;
      }
   }

   public MobCharmRecipeBuilder addCriterion(String name, CriterionTriggerInstance criterionIn) {
      advancementBuilder.addCriterion(name, criterionIn);
      return this;
   }

   public MobCharmRecipeBuilder setGroup(String groupIn) {
      group = groupIn;
      return this;
   }

   public void build(Consumer<FinishedRecipe> consumerIn) {
      ResourceLocation id = new ResourceLocation(Reference.MOD_ID, "mob_charm");
      validate(id);
      advancementBuilder.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(RequirementsStrategy.OR);
      consumerIn.accept(new Result(id, group == null ? "" : group, pattern, key, advancementBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + id.getPath())));
   }

   private void validate(ResourceLocation id) {
      if (pattern.isEmpty()) {
         throw new IllegalStateException("No pattern is defined for shaped recipe " + id + "!");
      } else {
         Set<Character> set = Sets.newHashSet(key.keySet());
         set.remove(' ');

         for(String s : pattern) {
            for(int i = 0; i < s.length(); ++i) {
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

   public static class Result implements FinishedRecipe {
      private final ResourceLocation id;
      private final String group;
      private final List<String> pattern;
      private final Map<Character, Ingredient> key;
      private final Advancement.Builder advancementBuilder;
      private final ResourceLocation advancementId;

      public Result(ResourceLocation idIn, String groupIn, List<String> patternIn, Map<Character, Ingredient> keyIn, Advancement.Builder advancementBuilderIn, ResourceLocation advancementIdIn) {
         id = idIn;
         group = groupIn;
         pattern = patternIn;
         key = keyIn;
         advancementBuilder = advancementBuilderIn;
         advancementId = advancementIdIn;
      }

      public void serializeRecipeData(JsonObject json) {
         if (!group.isEmpty()) {
            json.addProperty("group", group);
         }

         JsonArray jsonarray = new JsonArray();

         for(String s : pattern) {
            jsonarray.add(s);
         }

         json.add("pattern", jsonarray);
         JsonObject jsonobject = new JsonObject();

         for(Entry<Character, Ingredient> entry : key.entrySet()) {
            jsonobject.add(String.valueOf(entry.getKey()), entry.getValue().toJson());
         }

         json.add("key", jsonobject);
         JsonObject jsonobject1 = new JsonObject();
         jsonobject1.addProperty("item", RegistryHelper.getRegistryName(ModItems.MOB_CHARM.get()).toString());
         json.add("result", jsonobject1);
      }

      public RecipeSerializer<?> getType() {
         return MobCharmRecipe.SERIALIZER;
      }

      public ResourceLocation getId() {
         return id;
      }

      @Nullable
      public JsonObject serializeAdvancement() {
         return advancementBuilder.serializeToJson();
      }

      @Nullable
      public ResourceLocation getAdvancementId() {
         return advancementId;
      }
   }
}
