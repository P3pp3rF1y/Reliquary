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
import xreliquary.reference.Settings;

public class ItemBlockModels {
	public static void registerItemBlockModels() {
		registerBlockItemModel(ModBlocks.apothecaryCauldron, Names.apothecary_cauldron);
		registerBlockItemModel(ModBlocks.apothecaryMortar, Names.apothecary_mortar);
		registerBlockItemModel(ModBlocks.alkahestryAltar, Names.altar_idle);
		registerBlockItemModel(ModBlocks.alkahestryAltarActive, Names.altar);
		registerBlockItemModel(ModBlocks.interdictionTorch, Names.interdiction_torch);
		registerBlockItemModel(ModBlocks.wraithNode, Names.wraith_node);
		registerBlockItemModel(ModBlocks.fertileLilypad, Names.fertile_lilypad);
		registerPedestalBlockItemModels(ModBlocks.pedestal, Names.pedestal);
		registerPedestalBlockItemModels(ModBlocks.pedestalPassive, Names.pedestal_passive);
	}

	private static void registerBlockItemModel(Block block, String resourceName) {
		if(Settings.disabledItemsBlocks.contains(resourceName))
			return;

		//noinspection ConstantConditions
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(Reference.DOMAIN + resourceName, "inventory"));
	}

	private static void registerPedestalBlockItemModels(BlockPedestalPassive block, String resourceName) {
		if(Settings.disabledItemsBlocks.contains(resourceName))
			return;

		//noinspection ConstantConditions
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(Reference.DOMAIN + resourceName, "inventory"));

		for (int i=0; i < 16 ; i++) {
			//noinspection ConstantConditions
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), i, new ModelResourceLocation(Reference.DOMAIN + resourceName + "_" + EnumDyeColor.byMetadata(i).getName(), "inventory"));
		}
	}
}
