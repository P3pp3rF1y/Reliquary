package reliquary.compat.jei.mortar;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public record MortarRecipeJEI(List<ItemStack> inputs, ItemStack output) {
}
