package xreliquary.init;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import xreliquary.Reliquary;
import xreliquary.blocks.BlockAlkahestryAltar;
import xreliquary.blocks.BlockApothecaryCauldron;
import xreliquary.blocks.BlockApothecaryMortar;
import xreliquary.blocks.BlockBase;
import xreliquary.blocks.BlockFertileLilypad;
import xreliquary.blocks.BlockInterdictionTorch;
import xreliquary.blocks.BlockPedestal;
import xreliquary.blocks.BlockPedestalPassive;
import xreliquary.blocks.BlockWraithNode;
import xreliquary.blocks.tile.TileEntityAltar;
import xreliquary.blocks.tile.TileEntityCauldron;
import xreliquary.blocks.tile.TileEntityMortar;
import xreliquary.blocks.tile.TileEntityPedestal;
import xreliquary.blocks.tile.TileEntityPedestalPassive;
import xreliquary.items.block.ItemBlockBase;
import xreliquary.items.block.ItemBlockPedestal;
import xreliquary.items.block.ItemFertileLilyPad;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ModBlocks {

	public static BlockApothecaryCauldron apothecaryCauldron;
	public static BlockAlkahestryAltar alkahestryAltar;
	public static BlockBase apothecaryMortar;
	public static BlockFertileLilypad fertileLilypad;
	public static BlockInterdictionTorch interdictionTorch;
	public static BlockWraithNode wraithNode;
	public static BlockPedestal pedestal;
	public static BlockPedestalPassive pedestalPassive;
	//TODO not ideal place - figure out something better and move there with the init method
	public static int snowStateId = 0;

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> registry = event.getRegistry();
		
		alkahestryAltar = registerBlock(registry, new BlockAlkahestryAltar(), Names.Blocks.ALTAR, TileEntityAltar.class);       
    if(!Settings.Disable.disablePotions) {
  		apothecaryCauldron = registerBlock(registry, new BlockApothecaryCauldron(), Names.Blocks.APOTHECARY_CAULDRON, TileEntityCauldron.class);
  		apothecaryMortar = registerBlock(registry, new BlockApothecaryMortar(), Names.Blocks.APOTHECARY_MORTAR, TileEntityMortar.class);
    }
    fertileLilypad = registerBlock(registry, new BlockFertileLilypad(), Names.Blocks.FERTILE_LILYPAD);
		interdictionTorch = registerBlock(registry, new BlockInterdictionTorch(), Names.Blocks.INTERDICTION_TORCH);
		wraithNode = registerBlock(registry, new BlockWraithNode(), Names.Blocks.WRAITH_NODE);
		if(!Settings.Disable.disablePedestal) {
		  pedestal = registerBlock(registry, new BlockPedestal(), Names.Blocks.PEDESTAL, TileEntityPedestal.class);
		}
		if(!Settings.Disable.disablePedestal) {
	    pedestalPassive = registerBlock(registry, new BlockPedestalPassive(), Names.Blocks.PEDESTAL_PASSIVE, TileEntityPedestalPassive.class);	
		}
	}
	
	@SubscribeEvent
	public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();

    registerItemBlock(registry, alkahestryAltar, Names.Blocks.ALTAR);
    if(!Settings.Disable.disablePotions) {
  		registerItemBlock(registry, apothecaryCauldron, Names.Blocks.APOTHECARY_CAULDRON);
  	  registerItemBlock(registry, apothecaryMortar, Names.Blocks.APOTHECARY_MORTAR);
    }
    registerItemBlock(registry, fertileLilypad, new ItemFertileLilyPad(), Names.Blocks.FERTILE_LILYPAD, false);
		registerItemBlock(registry, interdictionTorch, Names.Blocks.INTERDICTION_TORCH);
		registerItemBlock(registry, wraithNode, Names.Blocks.WRAITH_NODE);
    if(!Settings.Disable.disablePedestal) {
      registerItemBlock(registry, pedestal, new ItemBlockPedestal(pedestal), Names.Blocks.PEDESTAL, true);
    }
    if(!Settings.Disable.disablePedestalPassive) {
      registerItemBlock(registry, pedestalPassive, new ItemBlockPedestal(pedestalPassive), Names.Blocks.PEDESTAL_PASSIVE, true);
    }
	}

	private static <T extends Block> T registerBlock(IForgeRegistry<Block> registry, T block, String name) {
		return registerBlock(registry, block, name, null);
	}
	private static <T extends Block> T registerBlock(IForgeRegistry<Block> registry, T block, String name, Class<? extends TileEntity> tileClass) {
		registry.register(block);

		if (tileClass != null)
			GameRegistry.registerTileEntity(tileClass, Reference.MOD_ID + ":tile_" + name);

		return block;
	}

	private static void registerItemBlock(IForgeRegistry<Item> registry, Block block, String name) {
		registerItemBlock(registry, block, new ItemBlockBase(block), name, false);
	}
	private static void registerItemBlock(IForgeRegistry<Item> registry, Block block, ItemBlockBase itemBlock, String name, boolean jeiOneDescription) {
		//noinspection ConstantConditions
		registry.register(itemBlock.setRegistryName(block.getRegistryName()));

		Reliquary.PROXY.registerJEI(block, name, jeiOneDescription);
	}

	public static void initSnowStateId() {
		snowStateId = Block.getStateId(Blocks.SNOW.getDefaultState());
	}
}
