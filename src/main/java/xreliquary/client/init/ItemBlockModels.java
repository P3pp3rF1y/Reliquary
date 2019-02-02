package xreliquary.client.init;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import xreliquary.blocks.BlockPedestalPassive;
import xreliquary.init.ModBlocks;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;

public class ItemBlockModels {
	public static void registerItemBlockModels() {
		registerBlockItemModel(ModBlocks.apothecaryCauldron, Names.Blocks.APOTHECARY_CAULDRON);
		registerBlockItemModel(ModBlocks.apothecaryMortar, Names.Blocks.APOTHECARY_MORTAR);
    registerBlockItemModel(ModBlocks.alkahestryAltar, Names.Blocks.ALTAR);
		registerBlockItemModel(ModBlocks.interdictionTorch, Names.Blocks.INTERDICTION_TORCH);
		registerBlockItemModel(ModBlocks.wraithNode, Names.Blocks.WRAITH_NODE);
		registerBlockItemModel(ModBlocks.fertileLilypad, Names.Blocks.FERTILE_LILYPAD);
		registerPedestalBlockItemModels(ModBlocks.pedestal, Names.Blocks.PEDESTAL);
		registerPedestalBlockItemModels(ModBlocks.pedestalPassive, Names.Blocks.PEDESTAL_PASSIVE);
	}

	private static void registerBlockItemModel(Block block, String resourceName) {
		//noinspection ConstantConditions
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(Reference.DOMAIN + resourceName, "inventory"));
	}

	private static void registerPedestalBlockItemModels(BlockPedestalPassive block, String resourceName) {
		//noinspection ConstantConditions
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(Reference.DOMAIN + resourceName, "inventory"));

		for (int i=0; i < 16 ; i++) {
			//noinspection ConstantConditions
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), i, new ModelResourceLocation(Reference.DOMAIN + resourceName + "_" + EnumDyeColor.byMetadata(i).getName(), "inventory"));
		}
	}
}
