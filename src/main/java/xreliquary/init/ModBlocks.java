package xreliquary.init;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import xreliquary.Reliquary;
import xreliquary.blocks.*;
import xreliquary.blocks.tile.*;
import xreliquary.items.block.ItemBlockBase;
import xreliquary.items.block.ItemBlockPedestal;
import xreliquary.items.block.ItemFertileLilyPad;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;

public class ModBlocks {

	public static final BlockApothecaryCauldron apothecaryCauldron = new BlockApothecaryCauldron();
	public static final BlockAlkahestryAltar alkahestryAltar = new BlockAlkahestryAltar(false);
	public static final BlockAlkahestryAltar alkahestryAltarActive = new BlockAlkahestryAltar(true);
	public static final BlockBase apothecaryMortar = new BlockApothecaryMortar();
	public static final BlockFertileLilypad fertileLilypad = new BlockFertileLilypad();
	public static final BlockInterdictionTorch interdictionTorch = new BlockInterdictionTorch();
	public static final BlockWraithNode wraithNode = new BlockWraithNode();
	public static final BlockPedestal pedestal = new BlockPedestal();
	public static final BlockPedestalPassive pedestalPassive = new BlockPedestalPassive();

	public static void init() {
		//TODO move itemblock definitions into blocks and just call getItemBlock in register method
		registerBlock(apothecaryCauldron, new ItemBlockBase(apothecaryCauldron), Names.Blocks.APOTHECARY_CAULDRON);
		registerBlock(alkahestryAltar, new ItemBlockBase(alkahestryAltar), Names.Blocks.ALTAR_IDLE);
		registerBlock(alkahestryAltarActive, new ItemBlockBase(alkahestryAltarActive), Names.Blocks.ALTAR);
		registerBlock(apothecaryMortar, new ItemBlockBase(apothecaryMortar), Names.Blocks.APOTHECARY_MORTAR);
		registerBlock(fertileLilypad, new ItemFertileLilyPad(), Names.Blocks.FERTILE_LILYPAD);
		registerBlock(interdictionTorch, new ItemBlockBase(interdictionTorch), Names.Blocks.INTERDICTION_TORCH);
		registerBlock(wraithNode, new ItemBlockBase(wraithNode), Names.Blocks.WRAITH_NODE);
		registerBlock(pedestal, new ItemBlockPedestal(pedestal), Names.Blocks.PEDESTAL, true);
		registerBlock(pedestalPassive, new ItemBlockPedestal(pedestalPassive), Names.Blocks.PEDESTAL_PASSIVE, true);
	}

	public static void initTileEntities() {
		registerTileEntity(TileEntityAltar.class, "reliquaryAltar");
		registerTileEntity(TileEntityMortar.class, "apothecaryMortar");
		registerTileEntity(TileEntityCauldron.class, "reliquaryCauldron");
		registerTileEntity(TileEntityPedestal.class, "reliquaryPedestal");
		registerTileEntity(TileEntityPedestalPassive.class, "reliquaryPedestalPassive");
	}

	private static void registerTileEntity(Class<? extends TileEntity> clazz, String name) {
		if(Settings.disabledItemsBlocks.contains(name))
			return;

		GameRegistry.registerTileEntity(clazz, Reference.MOD_ID + "." + name);
	}

	private static void registerBlock(Block block, ItemBlock itemBlock, String name) {
		registerBlock(block, itemBlock, name, false);
	}
	private static void registerBlock(Block block, ItemBlock itemBlock, String name, boolean jeiOneDescription) {
		if(Settings.disabledItemsBlocks.contains(name))
			return;

		block.setRegistryName(new ResourceLocation(Reference.MOD_ID, name));
		GameRegistry.register(block);
		GameRegistry.register(itemBlock.setRegistryName(block.getRegistryName()));

		Reliquary.PROXY.registerJEI(block, name, jeiOneDescription);
	}

}
