package xreliquary.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xreliquary.Reliquary;
import xreliquary.items.ItemFertileLilypad;
import xreliquary.items.XRItems;
import xreliquary.lib.Indexes;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class XRBlocks {
	
    public static Block altarActive;
    public static Block altarIdle;
    public static Block lilypad;
    public static Block wraithNode;

    public static void init() {
        altarActive = new BlockAltar(Reliquary.CONFIG.getBlock("AltarActive", Indexes.ALTAR_ACTIVE_DEFAULT_ID).getInt(Indexes.ALTAR_ACTIVE_DEFAULT_ID), true);
        altarIdle = new BlockAltar(Reliquary.CONFIG.getBlock("AltarIdle", Indexes.ALTAR_IDLE_DEFAULT_ID).getInt(Indexes.ALTAR_IDLE_DEFAULT_ID), false);
        lilypad = new BlockFertileLilypad(Reliquary.CONFIG.getBlock("Lilypad", Indexes.LILYPAD_DEFAULT_ID).getInt(Indexes.LILYPAD_DEFAULT_ID));
        wraithNode = new BlockWraithNode(Reliquary.CONFIG.getBlock("WraithNode", Indexes.WRAITH_NODE_DEFAULT_ID).getInt(Indexes.WRAITH_NODE_DEFAULT_ID));

        /*
        LanguageRegistry.addName(altarActive, Names.ALTAR_LOCAL);
        LanguageRegistry.addName(altarIdle, Names.ALTAR_LOCAL);
        LanguageRegistry.addName(lilypad, Names.LILYPAD_LOCAL);
        LanguageRegistry.addName(wraithNode, Names.WRAITHNODE_LOCAL);
        */

        GameRegistry.registerBlock(altarActive, Names.ALTAR_ACTIVE_NAME);
        GameRegistry.registerBlock(altarIdle, Names.ALTAR_IDLE_NAME);
        GameRegistry.registerBlock(lilypad, ItemFertileLilypad.class, Names.LILYPAD_NAME);
        GameRegistry.registerBlock(wraithNode, Names.WRAITHNODE_NAME);

        addRecipes();
    }

    public static void addRecipes() {
    	
        // altar
        GameRegistry.addRecipe(new ItemStack(altarIdle, 1), new Object[] {
                "olo", "lel", "olo", 'o', Block.obsidian, 'l',
                Block.redstoneLampIdle, 'e', Item.emerald });

        // lily
        GameRegistry.addRecipe(new ItemStack(lilypad, 1), new Object[] { "www",
                "wlw", "www", 'w', XRItems.potion(Reference.FERTILIZER_META),
                'l', Block.waterlily });

        // wraithnode
        GameRegistry.addRecipe(new ItemStack(wraithNode, 1), new Object[] {
                "vv", "vv", 'v', XRItems.emptyVoidTear });
    }
}
