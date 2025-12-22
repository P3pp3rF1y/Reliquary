package reliquary.compat.jei;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.*;

class ComponentSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
	private final Set<DataComponentType<?>> componentsToConsider;

	ComponentSubtypeInterpreter(DataComponentType<?>... componentsToConsider) {
		this.componentsToConsider = new HashSet<>(Arrays.asList(componentsToConsider));
	}

	@Override
	public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
		boolean allNulls = true;
		List<Object> results = new ArrayList<>(componentsToConsider.size());
		for (Map.Entry<DataComponentType<?>, Optional<?>> entry : ingredient.getComponentsPatch().entrySet()) {
			if (componentsToConsider.contains(entry.getKey())) {
				allNulls = false;
				results.add(entry.getValue());
			}
		}
		return allNulls ? null : results;
	}
}
