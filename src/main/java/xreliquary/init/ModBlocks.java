package xreliquary.init;

import net.minecraft.block.Block;
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

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ModBlocks {

	public static BlockApothecaryCauldron apothecaryCauldron;
	public static BlockAlkahestryAltar alkahestryAltar;
	public static BlockAlkahestryAltar alkahestryAltarActive;
	public static BlockBase apothecaryMortar;
	public static BlockFertileLilypad fertileLilypad;
	public static BlockInterdictionTorch interdictionTorch;
	public static BlockWraithNode wraithNode;
	public static BlockPedestal pedestal;
	public static BlockPedestalPassive pedestalPassive;

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> registry = event.getRegistry();

		apothecaryCauldron = registerBlock(registry, new BlockApothecaryCauldron(), Names.Blocks.APOTHECARY_CAULDRON, TileEntityCauldron.class);
		alkahestryAltar = registerBlock(registry, new BlockAlkahestryAltar(false), Names.Blocks.ALTAR_IDLE);
		alkahestryAltarActive = registerBlock(registry, new BlockAlkahestryAltar(true), Names.Blocks.ALTAR, TileEntityAltar.class);
		apothecaryMortar = registerBlock(registry, new BlockApothecaryMortar(), Names.Blocks.APOTHECARY_MORTAR, TileEntityMortar.class);
		fertileLilypad = registerBlock(registry, new BlockFertileLilypad(), Names.Blocks.FERTILE_LILYPAD);
		interdictionTorch = registerBlock(registry, new BlockInterdictionTorch(), Names.Blocks.INTERDICTION_TORCH);
		wraithNode = registerBlock(registry, new BlockWraithNode(), Names.Blocks.WRAITH_NODE);
		pedestal = registerBlock(registry, new BlockPedestal(), Names.Blocks.PEDESTAL, TileEntityPedestal.class, true);
		pedestalPassive = registerBlock(registry, new BlockPedestalPassive(), Names.Blocks.PEDESTAL_PASSIVE, TileEntityPedestalPassive.class, true);
	}

	@SubscribeEvent
	public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();

		registerItemBlock(registry, apothecaryCauldron);
		registerItemBlock(registry, alkahestryAltar);
		registerItemBlock(registry, alkahestryAltarActive);
		registerItemBlock(registry, apothecaryMortar);
		registerItemBlock(registry, fertileLilypad, new ItemFertileLilyPad());
		registerItemBlock(registry, interdictionTorch);
		registerItemBlock(registry, wraithNode);
		registerItemBlock(registry, pedestal, new ItemBlockPedestal(pedestal));
		registerItemBlock(registry, pedestalPassive, new ItemBlockPedestal(pedestalPassive));
	}

	private static <T extends Block> T registerBlock(IForgeRegistry<Block> registry, T block, String name) {
		return registerBlock(registry, block, name, null);
	}
	private static <T extends Block> T registerBlock(IForgeRegistry<Block> registry, T block, String name, Class<? extends TileEntity> tileClass) {
		return registerBlock(registry, block, name, tileClass, false);
	}
	private static <T extends Block> T registerBlock(IForgeRegistry<Block> registry, T block, String name, Class<? extends TileEntity> tileClass, boolean jeiOneDescription) {
		registry.register(block);

		if (tileClass != null)
			GameRegistry.registerTileEntity(tileClass, Reference.MOD_ID + ":tile_" + name);

		Reliquary.PROXY.registerJEI(block, name, jeiOneDescription);

		return block;
	}

	private static void registerItemBlock(IForgeRegistry<Item> registry, Block block) {
		registerItemBlock(registry, block, new ItemBlockBase(block));
	}
	private static void registerItemBlock(IForgeRegistry<Item> registry, Block block, ItemBlockBase itemBlock) {
		//noinspection ConstantConditions
		registry.register(itemBlock.setRegistryName(block.getRegistryName()));
	}
}
