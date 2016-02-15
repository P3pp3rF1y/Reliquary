package xreliquary.init;


import lib.enderwizards.sandstone.items.block.ItemBlockBase;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import xreliquary.Reliquary;
import xreliquary.blocks.*;
import xreliquary.blocks.tile.TileEntityAltar;
import xreliquary.blocks.tile.TileEntityCauldron;
import xreliquary.blocks.tile.TileEntityMortar;
import xreliquary.items.block.ItemFertileLilyPad;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;

public class ModBlocks {

    public static final BlockApothecaryCauldron apothecaryCauldron = new BlockApothecaryCauldron();
    public static final BlockAlkahestryAltar alkahestryAltar = new BlockAlkahestryAltar(false);
    public static final BlockAlkahestryAltar alkahestryAltarActive = new BlockAlkahestryAltar(true);
    public static final BlockBase apothecaryMortar = new BlockApothecaryMortar();
    public static final BlockFertileLilypad fertileLilypad = new BlockFertileLilypad();
    public static final BlockInterdictionTorch interdictionTorch = new BlockInterdictionTorch();
    public static final BlockWraithNode wraithNode = new BlockWraithNode();

    public static void init()
    {
        registerBlock(apothecaryCauldron, ItemBlockBase.class, Names.apothecary_cauldron);
        registerBlock(alkahestryAltar, ItemBlockBase.class,Names.altar_idle);
        registerBlock(alkahestryAltarActive, ItemBlockBase.class, Names.altar);
        registerBlock(apothecaryMortar, ItemBlockBase.class, Names.apothecary_mortar);
        registerBlock(fertileLilypad, ItemFertileLilyPad.class, Names.fertile_lilypad);
        registerBlock(interdictionTorch, ItemBlockBase.class, Names.interdiction_torch);
        registerBlock(wraithNode, ItemBlockBase.class, Names.wraith_node);
    }

    public static void initTileEntities() {
        GameRegistry.registerTileEntity(TileEntityAltar.class, Reference.MOD_ID + "." + "reliquaryAltar");
        GameRegistry.registerTileEntity(TileEntityMortar.class, Reference.MOD_ID + "." + "apothecaryMortar");
        GameRegistry.registerTileEntity(TileEntityCauldron.class, Reference.MOD_ID + "." + "reliquaryCauldron");
    }

    private static void registerBlock(Block block, Class<? extends ItemBlock> itemclass, String name) {
        GameRegistry.registerBlock(block, itemclass, Reference.DOMAIN + name);
        Reliquary.PROXY.registerJEI(block, name);
    }

}
