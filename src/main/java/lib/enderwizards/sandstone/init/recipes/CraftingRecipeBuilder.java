package lib.enderwizards.sandstone.init.recipes;

import net.minecraftforge.fml.common.registry.GameRegistry;
import lib.enderwizards.sandstone.Sandstone;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

/**
 * A builder for crafting table recipes. Why use it? Because it makes your code look very, very clean. And awesome. :D
 *
 * @author TheMike
 * @author Unh0ly_Tigg
 */
public abstract class CraftingRecipeBuilder {

    public static CraftingRecipeBuilder instance;
    protected ItemStack output;

    public static class ShapelessCraftingRecipeBuilder extends CraftingRecipeBuilder {

        private List<ItemStack> input = new ArrayList<ItemStack>();

        public ShapelessCraftingRecipeBuilder output(ItemStack stack) {
            if (output != null)
                Sandstone.LOGGER.warn("ShapelessCraftingRecipeBuilder: It's bad practice to replace a preexisting output()! Replacing.");
            output = stack;
            return this;
        }

        public ShapelessCraftingRecipeBuilder output(Item item) {
            return this.output(new ItemStack(item));
        }

        public ShapelessCraftingRecipeBuilder output(Block block) {
            return this.output(new ItemStack(block));
        }

        public ShapelessCraftingRecipeBuilder input(ItemStack stack) {
            this.input.add(stack);
            return this;
        }

        public ShapelessCraftingRecipeBuilder input(Item item) {
            return this.input(new ItemStack(item));
        }

        public ShapelessCraftingRecipeBuilder input(Block block) {
            return this.input(new ItemStack(block));
        }

        @Override
        public void build() {
            GameRegistry.addShapelessRecipe(output, input.toArray());
        }

    }

    public static class ShapedCraftingRecipeBuilder extends CraftingRecipeBuilder {

        private String[] input;
        private Map<Character, Object> replacements = new HashMap<Character, Object>();

        public ShapedCraftingRecipeBuilder output(ItemStack stack) {
            if (output != null)
                Sandstone.LOGGER.warn("ShapedCraftingRecipeBuilder: It's bad practice to replace a preexisting output()! Replacing.");
            output = stack;
            return this;
        }

        public ShapedCraftingRecipeBuilder output(Item item) {
            return this.output(new ItemStack(item));
        }

        public ShapedCraftingRecipeBuilder output(Block block) {
            return this.output(new ItemStack(block));
        }

        public ShapedCraftingRecipeBuilder input(String... input) {
            if (input.length > 3)
                Sandstone.LOGGER.warn("ShapedCraftingRecipeBuilder: Input array larger than 3! Truncating array.");
            this.input = ArrayUtils.subarray(input, 0, 3);
            return this;
        }

        private void where(char key, Object object) {
            if (replacements.containsKey(key)) {
                Sandstone.LOGGER.warn("ShapedCraftingRecipeBuilder: It's bad practice to have two where()s with the same key! Replacing.");
                replacements.remove(key);
            }
            replacements.put(key, object);
        }

        public ShapedCraftingRecipeBuilder where(char key, ItemStack stack) {
            this.where(key, (Object) stack);
            return this;
        }

        public ShapedCraftingRecipeBuilder where(char key, Item item) {
            this.where(key, (Object) item);
            return this;
        }

        public ShapedCraftingRecipeBuilder where(char key, Block block) {
            this.where(key, (Object) block);
            return this;
        }

        @Override
        public void build() {
            List<Object> object = new ArrayList<Object>();
            Collections.addAll(object, input);
            for (Map.Entry<Character, Object> replacement : replacements.entrySet()) {
                object.add(replacement.getKey());
                object.add(replacement.getValue());
            }

            GameRegistry.addShapedRecipe(output, object.toArray());
        }
    }

    public static ShapedCraftingRecipeBuilder shaped() {
        return new ShapedCraftingRecipeBuilder();
    }

    public static ShapelessCraftingRecipeBuilder shapeless() {
        return new ShapelessCraftingRecipeBuilder();
    }

    abstract public void build();

}
