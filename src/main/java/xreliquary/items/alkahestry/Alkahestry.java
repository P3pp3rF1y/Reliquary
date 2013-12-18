package xreliquary.items.alkahestry;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import xreliquary.util.AlkahestRecipe;

public class Alkahestry {
	
	public static int LOW_TIER = 4;
	public static int MIDDLE_TIER = 8;
	public static int HIGH_TIER = 32;
	public static int UBER_TIER = 64;
	
	private static Map<Integer, AlkahestRecipe> REGISTRY = new HashMap<Integer, AlkahestRecipe>();
	
	public static void addKey(AlkahestRecipe recipe) {
		if(recipe.dictionaryName == null)
			REGISTRY.put(recipe.item.itemID, recipe);
		else
			REGISTRY.put(-OreDictionary.getOreID(recipe.dictionaryName), recipe);
	}
	
	public static AlkahestRecipe getDictionaryKey(ItemStack stack) {
		for(AlkahestRecipe recipe : getRegistry().values()) {
			if(recipe.dictionaryName == null)
				return null;
			for(ItemStack dict : OreDictionary.getOres(recipe.dictionaryName)) {
				if(OreDictionary.itemMatches(dict, stack, false))
					return recipe;
			}
		}
		return null;
	}
	
	public static void init() {
		
        addKey(new AlkahestRecipe(new ItemStack(Block.dirt), 32, LOW_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Block.cobblestone), 32, LOW_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Block.sand), 32, LOW_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Block.gravel), 16, LOW_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Block.sandStone, 1, -1), 8, LOW_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Block.blockClay), 2, LOW_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Block.netherrack), 8, LOW_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Item.coal, 1, 1), 4, LOW_TIER));

        addKey(new AlkahestRecipe(new ItemStack(Block.obsidian), 4, MIDDLE_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Block.slowSand), 8, MIDDLE_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Block.netherBrick), 4, MIDDLE_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Block.whiteStone), 16, MIDDLE_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Item.coal, 1, 0), 4, MIDDLE_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Item.gunpowder), 2, MIDDLE_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Item.flint), 8, MIDDLE_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Item.dyePowder, 1, 4), 1, MIDDLE_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Item.glowstone), 1, 9));

        addKey(new AlkahestRecipe(new ItemStack(Item.ingotGold), 1, HIGH_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Item.ingotIron), 1, HIGH_TIER));
        addKey(new AlkahestRecipe(new ItemStack(Item.emerald), 1, HIGH_TIER));
        addKey(new AlkahestRecipe("ingotTin", 1, HIGH_TIER));
        addKey(new AlkahestRecipe("ingotSilver", 1, HIGH_TIER));
        addKey(new AlkahestRecipe("ingotCopper", 1, HIGH_TIER));
        addKey(new AlkahestRecipe("ingotSteel", 1, HIGH_TIER));
        
        addKey(new AlkahestRecipe(new ItemStack(Item.diamond), 1, UBER_TIER));

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
		return REGISTRY;
	}

}
