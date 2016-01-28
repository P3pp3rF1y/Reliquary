package xreliquary.init;

import lib.enderwizards.sandstone.blocks.BlockBase;
import lib.enderwizards.sandstone.items.block.ItemBlockBase;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import xreliquary.blocks.*;
import xreliquary.blocks.tile.TileEntityAltar;
import xreliquary.blocks.tile.TileEntityCauldron;
import xreliquary.blocks.tile.TileEntityMortar;
import xreliquary.items.block.ItemFertileLilyPad;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;

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
        GameRegistry.registerBlock(apothecaryCauldron, ItemBlockBase.class, apothecaryCauldron.getUnwrappedUnlocalizedName());
        GameRegistry.registerBlock(alkahestryAltar, ItemBlockBase.class,Reference.DOMAIN + Names.altar_idle);
        GameRegistry.registerBlock(alkahestryAltarActive, Reference.DOMAIN + Names.altar);
        GameRegistry.registerBlock(apothecaryMortar, ItemBlockBase.class, apothecaryMortar.getUnwrappedUnlocalizedName());
        GameRegistry.registerBlock(fertileLilypad, ItemFertileLilyPad.class, Reference.DOMAIN + Names.fertile_lilypad);
        GameRegistry.registerBlock(interdictionTorch, ItemBlockBase.class, Reference.DOMAIN + Names.interdiction_torch);
        GameRegistry.registerBlock(wraithNode, ItemBlockBase.class, Reference.DOMAIN + Names.wraith_node);
    }

    public static void initTileEntities() {
        GameRegistry.registerTileEntity(TileEntityAltar.class, Reference.MOD_ID + "." + "reliquaryAltar");
        GameRegistry.registerTileEntity(TileEntityMortar.class, Reference.MOD_ID + "." + "apothecaryMortar");
        GameRegistry.registerTileEntity(TileEntityCauldron.class, Reference.MOD_ID + "." + "reliquaryCauldron");
    }

    public static void initModels()
    {
        registerBlockItemModel(apothecaryCauldron);
        registerBlockItemModel(alkahestryAltar, Names.altar_idle);
        registerBlockItemModel(alkahestryAltarActive, Names.altar);
        registerBlockItemModel(interdictionTorch, Names.interdiction_torch);
        registerBlockItemModel(wraithNode, Names.wraith_node);

        registerBlockItemModel(fertileLilypad, Names.fertile_lilypad);

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
