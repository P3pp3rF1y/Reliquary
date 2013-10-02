package xreliquary.items;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class AlkahestMap {

    private static HashMap<Integer, Integer> alkahestMap = new HashMap<Integer, Integer>();

    public static void init() {
        alkahestMap.put(Block.dirt.blockID, 1);
        alkahestMap.put(Block.cobblestone.blockID, 1);
        alkahestMap.put(Block.sand.blockID, 1);
        alkahestMap.put(Block.gravel.blockID, 4);
        alkahestMap.put(Block.wood.blockID, 8);
        alkahestMap.put(Block.sandStone.blockID, 4);
        alkahestMap.put(Block.blockClay.blockID, 8);
        alkahestMap.put(Block.obsidian.blockID, 8);
        alkahestMap.put(Block.netherrack.blockID, 1);
        alkahestMap.put(Block.slowSand.blockID, 4);
        alkahestMap.put(Block.netherBrick.blockID, 1);
        alkahestMap.put(Block.whiteStone.blockID, 1);
        alkahestMap.put(Block.melon.blockID, 8);
        alkahestMap.put(Block.pumpkin.blockID, 8);
        alkahestMap.put(Block.sapling.blockID, 2);
        alkahestMap.put(Block.cloth.blockID, 4);
        alkahestMap.put(Item.silk.itemID, 1);
        alkahestMap.put(Item.spiderEye.itemID, 4);
        alkahestMap.put(Item.coal.itemID, 8);
        alkahestMap.put(Item.diamond.itemID, 4608);
        alkahestMap.put(Item.ingotGold.itemID, 1152);
        alkahestMap.put(Item.emerald.itemID, 1152);
        alkahestMap.put(Item.ingotIron.itemID, 512);
        alkahestMap.put(Item.gunpowder.itemID, 128);
        alkahestMap.put(Item.flint.itemID, 4);
        alkahestMap.put(Item.netherStalkSeeds.itemID, 2);
        alkahestMap.put(Item.pumpkinSeeds.itemID, 2);
        alkahestMap.put(Item.melonSeeds.itemID, 2);
        alkahestMap.put(Item.carrot.itemID, 4);
        alkahestMap.put(Item.potato.itemID, 4);
        alkahestMap.put(Item.wheat.itemID, 2);
        alkahestMap.put(Item.seeds.itemID, 1);
        alkahestMap.put(Item.reed.itemID, 1);
        alkahestMap.put(Item.sugar.itemID, 1);
        alkahestMap.put(Item.paper.itemID, 1);
        alkahestMap.put(Item.blazeRod.itemID, 16);
        alkahestMap.put(Item.blazePowder.itemID, 8);
        alkahestMap.put(Item.slimeBall.itemID, 8);
        alkahestMap.put(Item.magmaCream.itemID, 16);
        alkahestMap.put(Item.rottenFlesh.itemID, 4);
        alkahestMap.put(Item.fishRaw.itemID, 8);
        alkahestMap.put(Item.fishCooked.itemID, 8);
        alkahestMap.put(Item.porkRaw.itemID, 8);
        alkahestMap.put(Item.porkCooked.itemID, 8);
        alkahestMap.put(Item.beefRaw.itemID, 8);
        alkahestMap.put(Item.beefCooked.itemID, 8);
        alkahestMap.put(Item.chickenRaw.itemID, 8);
        alkahestMap.put(Item.chickenCooked.itemID, 8);
        alkahestMap.put(Item.bone.itemID, 8);
        alkahestMap.put(Item.egg.itemID, 4);
        alkahestMap.put(Item.feather.itemID, 8);
        alkahestMap.put(Item.leather.itemID, 8);
    }

    public static int getMappingValue(int i) {
        if (alkahestMap.containsKey(Integer.valueOf(i)))
            return alkahestMap.get(i).intValue();
        return 0;
    }
}
