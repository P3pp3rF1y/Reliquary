package xreliquary.items.alkahestry;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.registry.GameRegistry;

import xreliquary.items.XRItems;
import xreliquary.util.AlkahestDictionaryRecipe;
import xreliquary.util.AlkahestRecipe;

public class AlkahestryRegistry {
	
	private static Map<Integer, AlkahestRecipe> registry = new HashMap<Integer, AlkahestRecipe>();
	private static Map<String, AlkahestDictionaryRecipe> dictRegistry = new HashMap<String, AlkahestDictionaryRecipe>();
	
	public static void addKey(AlkahestRecipe recipe) {
		registry.put(recipe.item.itemID, recipe);
	}
	
	public static void addDictionaryKey(AlkahestDictionaryRecipe recipe) {
		dictRegistry.put(recipe.dictionaryName, recipe);
	}
	
	public static AlkahestDictionaryRecipe getDictionaryKey(ItemStack stack) {
		for(AlkahestDictionaryRecipe recipe : dictRegistry.values()) {
			for(ItemStack dict : OreDictionary.getOres(recipe.dictionaryName)) {
				if(OreDictionary.itemMatches(dict, stack, false))
					return recipe;
			}
		}
		return null;
	}
	
	public static void init() {
		
        addKey(new AlkahestRecipe(new ItemStack(Block.dirt), 32, AlkahestRecipe.LOW_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Block.cobblestone), 32, AlkahestRecipe.LOW_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Block.sand), 32, AlkahestRecipe.LOW_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Block.gravel), 16, AlkahestRecipe.LOW_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Block.sandStone, 1, -1), 8, AlkahestRecipe.LOW_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Block.blockClay), 2, AlkahestRecipe.LOW_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Block.netherrack), 8, AlkahestRecipe.LOW_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Item.coal, 1, 1), 4, AlkahestRecipe.LOW_TIER));

        addKey(new AlkahestRecipe(new ItemStack(Block.obsidian), 4, AlkahestRecipe.MIDDLE_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Block.slowSand), 8, AlkahestRecipe.MIDDLE_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Block.netherBrick), 4, AlkahestRecipe.MIDDLE_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Block.whiteStone), 16, AlkahestRecipe.MIDDLE_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Item.coal, 1, 0), 4, AlkahestRecipe.MIDDLE_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Item.gunpowder), 2, AlkahestRecipe.MIDDLE_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Item.flint), 8, AlkahestRecipe.MIDDLE_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Item.dyePowder, 1, 4), 1, AlkahestRecipe.MIDDLE_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Item.glowstone), 1, 9));

        addKey(new AlkahestRecipe(new ItemStack(Item.ingotGold), 1, AlkahestRecipe.HIGH_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Item.ingotIron), 1, AlkahestRecipe.HIGH_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Item.emerald), 1, AlkahestRecipe.HIGH_TIER));
        addDictionaryKey(new AlkahestDictionaryRecipe("ingotTin", 1, AlkahestRecipe.HIGH_TIER));
        addDictionaryKey(new AlkahestDictionaryRecipe("ingotSilver", 1, AlkahestRecipe.HIGH_TIER));
        addDictionaryKey(new AlkahestDictionaryRecipe("ingotCopper", 1, AlkahestRecipe.HIGH_TIER));
        addDictionaryKey(new AlkahestDictionaryRecipe("ingotSteel", 1, AlkahestRecipe.HIGH_TIER));
        
        addKey(new AlkahestRecipe(new ItemStack(Item.diamond), 1, AlkahestRecipe.UBER_TIER));

        /* GameRegistry.addRecipe(new ItemStack(Block.dragonEgg, 1), new Object[] {
                "ddd", "dtd", "ddd", 'd', Block.blockDiamond, 't',
                XRItems.alkahestryTome });
        GameRegistry.addRecipe(new ItemStack(Item.netherStar, 2), new Object[] {
                "tds", "www", "hhh", 'd', Item.diamond, 'w',
                new ItemStack(Item.skull, 1, 1), 't', XRItems.alkahestryTome,
                's', Item.netherStar, 'h', XRItems.glowingWater });
        */
	}
	
	public static Map<Integer, AlkahestRecipe> getRegistry() {
		return registry;
	}

}
