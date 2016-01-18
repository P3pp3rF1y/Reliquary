package xreliquary.init;

import lib.enderwizards.sandstone.blocks.BlockBase;
import lib.enderwizards.sandstone.items.ItemBase;
import lib.enderwizards.sandstone.items.block.ItemBlockBase;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import xreliquary.blocks.*;
import xreliquary.blocks.tile.TileEntityMortar;
import xreliquary.client.render.RenderApothecaryMortar;
import xreliquary.items.ItemFertileLilyPad;
import xreliquary.lib.Reference;

public class ModBlocks {

    public static final BlockBase apothecaryCauldron = new BlockApothecaryCauldron();
    public static final BlockAlkahestryAltar alkahestryAltar = new BlockAlkahestryAltar(false);
    public static final BlockAlkahestryAltar alkahestryAltarActive = new BlockAlkahestryAltar(true);
    public static final BlockBase apothecaryMortar = new BlockApothecaryMortar();
    public static final BlockFertileLilypad fertileLilypad = new BlockFertileLilypad();
    public static final BlockInterdictionTorch interdictionTorch = new BlockInterdictionTorch();
    public static final BlockWraithNode wraithNode = new BlockWraithNode();

    public static void init()
    {
        //TODO: refactor to get rid of so many magic strings
        GameRegistry.registerBlock(apothecaryCauldron, ItemBlockBase.class, apothecaryCauldron.getUnwrappedUnlocalizedName());
        GameRegistry.registerBlock(alkahestryAltar, ItemBlockBase.class,Reference.DOMAIN + "altar_idle");
        GameRegistry.registerBlock(alkahestryAltarActive, Reference.DOMAIN + "altar");
        GameRegistry.registerBlock(apothecaryMortar, ItemBlockBase.class, apothecaryMortar.getUnwrappedUnlocalizedName());
        GameRegistry.registerBlock(fertileLilypad, ItemFertileLilyPad.class, Reference.DOMAIN + "fertile_lilypad");
        GameRegistry.registerBlock(interdictionTorch, ItemBlockBase.class, Reference.DOMAIN + "interdiction_torch");
        GameRegistry.registerBlock(wraithNode, ItemBlockBase.class, Reference.DOMAIN + "wraith_node");
    }

    //TODO: figure out if this should be pulled to a separate class
    public static void initTileEntities() {
        GameRegistry.registerTileEntity(TileEntityMortar.class, apothecaryMortar +"_tile");

    }

    public static void initModels()
    {
        registerBlockItemModel(apothecaryCauldron);
        registerBlockItemModel(alkahestryAltar, "altar_idle");
        registerBlockItemModel(alkahestryAltarActive, "altar");
        registerBlockItemModel(interdictionTorch, "interdiction_torch");
        registerBlockItemModel(wraithNode, "wraith_node");

        registerBlockItemModel(fertileLilypad, "fertile_lilypad");

    }

    private static void registerBlockItemModel(Block block) {
        //TODO: replace substring(5) of unlocalized name with more proper base block implementation
        registerBlockItemModel(block, block.getUnlocalizedName().substring(5));
    }
    private static void registerBlockItemModel(Block block, String resourceName){
        //TODO: figure out what is the best way to register item models

        Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
                .register(Item.getItemFromBlock(block), 0, new ModelResourceLocation(Reference.DOMAIN + resourceName, "inventory"));
    }

}
