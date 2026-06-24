package reliquary.compat.jei;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

class ComponentSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
	private final Set<DataComponentType<?>> componentsToConsider;

	ComponentSubtypeInterpreter(DataComponentType<?>... componentsToConsider) {
		this.componentsToConsider = new HashSet<>(Arrays.asList(componentsToConsider));
	}

	@Override
	public String getLegacyStringSubtypeInfo(ItemStack itemStack, UidContext context) {
		DataComponentPatch componentsPatch = itemStack.getComponentsPatch();
		return componentsPatch.isEmpty() ? "" : getComponentsString(componentsPatch);
	}

	private static final Pattern SIMPLE_VALUE = Pattern.compile("[A-Za-z0-9._+-]+");

	private String handleEscape(String keyName) {
		return SIMPLE_VALUE.matcher(keyName).matches() ? keyName : StringTag.quoteAndEscape(keyName);
	}

	private String getComponentsString(DataComponentPatch componentsPatch) {
		StringBuilder stringbuilder = new StringBuilder("{");
		componentsPatch.entrySet().stream().filter(entry -> componentsToConsider.contains(entry.getKey())).forEach(entry -> {
			if (stringbuilder.length() != 1) {
				stringbuilder.append(',');
			}
			stringbuilder.append(handleEscape(entry.getKey().toString())).append(':').append(entry.getValue().toString());
		});
		return stringbuilder.append('}').toString();
	}

	@Override
	public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
		boolean allNulls = true;
		List<@Nullable Object> results = new ArrayList<>(componentsToConsider.size());
		for (Map.Entry<DataComponentType<?>, Optional<?>> entry : ingredient.getComponentsPatch().entrySet()) {
			if (componentsToConsider.contains(entry.getKey())) {
				allNulls = false;
				results.add(entry.getValue());
			}
		}
		return allNulls ? null : results;
	}
}
