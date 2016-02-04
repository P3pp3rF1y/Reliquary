package xreliquary.client.init;

import net.minecraft.block.Block;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import xreliquary.init.ModBlocks;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;

public class ItemBlockModels {
    public static void registerItemBlockModels()
    {
        registerBlockItemModel(ModBlocks.apothecaryCauldron, Names.apothecary_cauldron);
        registerBlockItemModel(ModBlocks.alkahestryAltar, Names.altar_idle);
        registerBlockItemModel(ModBlocks.alkahestryAltarActive, Names.altar);
        registerBlockItemModel(ModBlocks.interdictionTorch, Names.interdiction_torch);
        registerBlockItemModel(ModBlocks.wraithNode, Names.wraith_node);

        registerBlockItemModel(ModBlocks.fertileLilypad, Names.fertile_lilypad);

    }

    private static void registerBlockItemModel(Block block, String resourceName){
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(Reference.DOMAIN + resourceName, "inventory"));
    }


}
