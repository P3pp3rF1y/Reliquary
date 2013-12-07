package xreliquary.items.alkahestry;

import java.util.HashMap;
import java.util.Map;

import xreliquary.util.AlkahestRecipe;

public class AlkahestryRegistry {
	
	private static Map<Integer, AlkahestRecipe> registry = new HashMap<Integer, AlkahestRecipe>();
	
	public static void addKey(AlkahestRecipe recipe) {
		registry.put(recipe.item.itemID, recipe);
	}
	
	public static AlkahestRecipe getKey(int ID) {
		if(registry.containsKey(ID)) {
			return registry.get(ID);
		} else {
			return null;
		}
	}
	
	public static Map<Integer, AlkahestRecipe> getRegistry() {
		return registry;
	}

}
