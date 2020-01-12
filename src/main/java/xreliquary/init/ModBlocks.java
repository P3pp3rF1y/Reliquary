package xreliquary.init;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.WallOrFloorItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import xreliquary.Reliquary;
import xreliquary.blocks.AlkahestryAltarBlock;
import xreliquary.blocks.ApothecaryCauldronBlock;
import xreliquary.blocks.ApothecaryMortarBlock;
import xreliquary.blocks.BaseBlock;
import xreliquary.blocks.FertileLilypadBlock;
import xreliquary.blocks.InterdictionTorchBlock;
import xreliquary.blocks.PassivePedestalBlock;
import xreliquary.blocks.PedestalBlock;
import xreliquary.blocks.WallInterdictionTorchBlock;
import xreliquary.blocks.WraithNodeBlock;
import xreliquary.blocks.tile.AlkahestryAltarTileEntity;
import xreliquary.blocks.tile.ApothecaryCauldronTileEntity;
import xreliquary.blocks.tile.ApothecaryMortarTileEntity;
import xreliquary.blocks.tile.PedestalPassiveTileEntity;
import xreliquary.blocks.tile.PedestalTileEntity;
import xreliquary.items.block.BlockItemBase;
import xreliquary.items.block.FertileLilyPadItem;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;
import xreliquary.util.InjectionHelper;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Reference.MOD_ID)
public class ModBlocks {
	public static final AlkahestryAltarBlock ALKAHESTRY_ALTAR = InjectionHelper.nullValue();
	public static final ApothecaryCauldronBlock APOTHECARY_CAULDRON = InjectionHelper.nullValue();
	public static final BaseBlock APOTHECARY_MORTAR = InjectionHelper.nullValue();
	public static final FertileLilypadBlock FERTILE_LILYPAD = InjectionHelper.nullValue();
	public static final InterdictionTorchBlock INTERDICTION_TORCH = InjectionHelper.nullValue();
	public static final WallInterdictionTorchBlock WALL_INTERDICTION_TORCH = InjectionHelper.nullValue();
	public static final WraithNodeBlock WRAITH_NODE = InjectionHelper.nullValue();

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> registry = event.getRegistry();

		registry.register(new AlkahestryAltarBlock());
		if (!Settings.COMMON.disable.disablePotions.get()) {
			registry.register(new ApothecaryCauldronBlock());
			registry.register(new ApothecaryMortarBlock());
		}

		registry.register(new FertileLilypadBlock());
		registry.register(new InterdictionTorchBlock());
		registry.register(new WallInterdictionTorchBlock());
		registry.register(new WraithNodeBlock());

		if (!Settings.COMMON.disable.disablePedestal.get()) {
			for (DyeColor dyecolor : DyeColor.values()) {
				registry.register(new PedestalBlock(dyecolor));
			}
		}
		if (!Settings.COMMON.disable.disablePassivePedestal.get()) {
			for (DyeColor dyecolor : DyeColor.values()) {
				registry.register(new PassivePedestalBlock(dyecolor));
			}
		}
	}

	@SubscribeEvent
	public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
		registerTileEntity(event, Names.Blocks.ALKAHESTRY_ALTAR, AlkahestryAltarTileEntity::new, ALKAHESTRY_ALTAR);
		if (!Settings.COMMON.disable.disablePedestal.get()) {
			registerTileEntity(event, Names.Blocks.PEDESTAL, PedestalTileEntity::new,
					PedestalBlock.ALL_PEDESTAL_BLOCKS.toArray(new Block[PedestalBlock.ALL_PEDESTAL_BLOCKS.size()]));
		}
		if (!Settings.COMMON.disable.disablePassivePedestal.get()) {
			registerTileEntity(event, Names.Blocks.PASSIVE_PEDESTAL, PedestalPassiveTileEntity::new,
					PassivePedestalBlock.ALL_PEDESTAL_BLOCKS.toArray(new Block[PassivePedestalBlock.ALL_PEDESTAL_BLOCKS.size()]));
		}
		if (!Settings.COMMON.disable.disablePotions.get()) {
			registerTileEntity(event, Names.Blocks.APOTHECARY_CAULDRON, ApothecaryCauldronTileEntity::new, APOTHECARY_CAULDRON);
			registerTileEntity(event, Names.Blocks.APOTHECARY_MORTAR, ApothecaryMortarTileEntity::new, APOTHECARY_MORTAR);
		}
	}

	private static void registerTileEntity(RegistryEvent.Register<TileEntityType<?>> event, String tileRegistryName, Supplier<? extends TileEntity> tileFactory, Block... validBlocks) {
		event.getRegistry().register(TileEntityType.Builder.create(tileFactory, validBlocks).build(null).setRegistryName(Reference.MOD_ID, tileRegistryName));
	}

	@SubscribeEvent
	public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();

		registerItemBlock(registry, ALKAHESTRY_ALTAR, Names.Blocks.ALKAHESTRY_ALTAR);

		if (!Settings.COMMON.disable.disablePotions.get()) {
			registerItemBlock(registry, APOTHECARY_CAULDRON, Names.Blocks.APOTHECARY_CAULDRON);
			registerItemBlock(registry, APOTHECARY_MORTAR, Names.Blocks.APOTHECARY_MORTAR);
		}

		registerItemBlock(registry, FERTILE_LILYPAD, new FertileLilyPadItem(), Names.Blocks.FERTILE_LILYPAD, false);
		registerItemBlock(registry, INTERDICTION_TORCH, new WallOrFloorItem(INTERDICTION_TORCH, WALL_INTERDICTION_TORCH, new Item.Properties().group(Reliquary.ITEM_GROUP)), Names.Blocks.INTERDICTION_TORCH, true);
		registerItemBlock(registry, WRAITH_NODE, Names.Blocks.WRAITH_NODE);

		if (!Settings.COMMON.disable.disablePedestal.get()) {
			PedestalBlock.ALL_PEDESTAL_BLOCKS.forEach(b -> registerItemBlock(registry, b, new BlockItemBase(b, new Item.Properties()), "pedestal", true));
		}

		if (!Settings.COMMON.disable.disablePassivePedestal.get()) {
			PassivePedestalBlock.ALL_PEDESTAL_BLOCKS.forEach(b -> registerItemBlock(registry, b, new BlockItemBase(b, new Item.Properties()), "pedestal_passive", true));
		}
	}

	private static void registerItemBlock(IForgeRegistry<Item> registry, Block block, String jeiDescriptionKey) {
		registerItemBlock(registry, block, new BlockItemBase(block, new Item.Properties()), jeiDescriptionKey, false);
	}

	private static void registerItemBlock(IForgeRegistry<Item> registry, Block block, BlockItem itemBlock, String jeiDescriptionKey, boolean jeiOneDescription) {
		//noinspection ConstantConditions
		registry.register(itemBlock.setRegistryName(block.getRegistryName()));

		Reliquary.proxy.registerJEI(block, jeiDescriptionKey, jeiOneDescription);
	}
}
