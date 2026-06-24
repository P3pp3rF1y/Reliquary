package reliquary.init;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import reliquary.Reliquary;
import reliquary.block.*;
import reliquary.block.tile.*;
import reliquary.item.block.BlockItemBase;
import reliquary.item.block.FertileLilyPadItem;
import reliquary.item.block.InterdictionTorchItem;

import java.util.Map;
import java.util.function.Supplier;

public class ModBlocks {
	private ModBlocks() {
	}

	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Reliquary.MOD_ID);
	private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Reliquary.MOD_ID);
	private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE,
			Reliquary.MOD_ID);

	private static final String ALKAHESTRY_ALTAR_REGISTRY_NAME = "alkahestry_altar";
	private static final String INTERDICTION_TORCH_REGISTRY_NAME = "interdiction_torch";
	private static final String APOTHECARY_CAULDRON_REGISTRY_NAME = "apothecary_cauldron";
	private static final String APOTHECARY_MORTAR_REGISTRY_NAME = "apothecary_mortar";
	private static final String FERTILE_LILY_PAD_REGISTRY_NAME = "fertile_lily_pad";
	private static final String WRAITH_NODE_REGISTRY_NAME = "wraith_node";

	public static final Supplier<AlkahestryAltarBlock> ALKAHESTRY_ALTAR = BLOCKS.registerBlock(ALKAHESTRY_ALTAR_REGISTRY_NAME, AlkahestryAltarBlock::new);
	public static final Supplier<ApothecaryCauldronBlock> APOTHECARY_CAULDRON = BLOCKS.registerBlock(APOTHECARY_CAULDRON_REGISTRY_NAME,
			ApothecaryCauldronBlock::new);
	public static final Supplier<ApothecaryMortarBlock> APOTHECARY_MORTAR = BLOCKS.registerBlock(APOTHECARY_MORTAR_REGISTRY_NAME, ApothecaryMortarBlock::new);
	public static final Supplier<FertileLilyPadBlock> FERTILE_LILY_PAD = BLOCKS.registerBlock(FERTILE_LILY_PAD_REGISTRY_NAME, FertileLilyPadBlock::new);
	public static final Supplier<InterdictionTorchBlock> INTERDICTION_TORCH = BLOCKS.registerBlock(INTERDICTION_TORCH_REGISTRY_NAME,
			InterdictionTorchBlock::new);
	public static final Supplier<WallInterdictionTorchBlock> WALL_INTERDICTION_TORCH = BLOCKS.registerBlock("wall_interdiction_torch",
			WallInterdictionTorchBlock::new);
	public static final Supplier<WraithNodeBlock> WRAITH_NODE = BLOCKS.registerBlock(WRAITH_NODE_REGISTRY_NAME, WraithNodeBlock::new);

	public static final Map<DyeColor, Supplier<PassivePedestalBlock>> PASSIVE_PEDESTALS;
	public static final Map<DyeColor, Supplier<PedestalBlock>> PEDESTALS;

	static {
		ImmutableMap.Builder<DyeColor, Supplier<PassivePedestalBlock>> passiveBuilder = ImmutableMap.builder();
		ImmutableMap.Builder<DyeColor, Supplier<PedestalBlock>> activeBuilder = ImmutableMap.builder();
		for (DyeColor color : DyeColor.values()) {
			passiveBuilder.put(color, BLOCKS.registerBlock("pedestals/passive/" + color.getName() + "_passive_pedestal",
					properties -> new PassivePedestalBlock(properties.overrideDescription("block." + Reliquary.MOD_ID + ".passive_pedestal"))));
			activeBuilder.put(color, BLOCKS.registerBlock("pedestals/" + color.getName() + "_pedestal",
					properties -> new PedestalBlock(properties.overrideDescription("block." + Reliquary.MOD_ID + ".pedestal"))));
		}
		PASSIVE_PEDESTALS = passiveBuilder.build();
		PEDESTALS = activeBuilder.build();
	}

	public static final Supplier<BlockEntityType<AlkahestryAltarBlockEntity>> ALKAHESTRY_ALTAR_TILE_TYPE = BLOCK_ENTITY_TYPES
			.register(ALKAHESTRY_ALTAR_REGISTRY_NAME, () -> getBlockEntityType(AlkahestryAltarBlockEntity::new, ALKAHESTRY_ALTAR.get()));
	public static final Supplier<BlockEntityType<PedestalBlockEntity>> PEDESTAL_TILE_TYPE = BLOCK_ENTITY_TYPES.register("pedestal",
			() -> getBlockEntityType(PedestalBlockEntity::new, PEDESTALS.values().stream().map(Supplier::get).toArray(PedestalBlock[]::new)));
	public static final Supplier<BlockEntityType<PassivePedestalBlockEntity>> PASSIVE_PEDESTAL_TILE_TYPE = BLOCK_ENTITY_TYPES.register("passive_pedestal",
			() -> getBlockEntityType(PassivePedestalBlockEntity::new,
					PASSIVE_PEDESTALS.values().stream().map(Supplier::get).toArray(PassivePedestalBlock[]::new)));
	public static final Supplier<BlockEntityType<ApothecaryCauldronBlockEntity>> APOTHECARY_CAULDRON_TILE_TYPE = BLOCK_ENTITY_TYPES
			.register(APOTHECARY_CAULDRON_REGISTRY_NAME, () -> getBlockEntityType(ApothecaryCauldronBlockEntity::new, APOTHECARY_CAULDRON.get()));
	public static final Supplier<BlockEntityType<ApothecaryMortarBlockEntity>> APOTHECARY_MORTAR_TILE_TYPE = BLOCK_ENTITY_TYPES
			.register(APOTHECARY_MORTAR_REGISTRY_NAME, () -> getBlockEntityType(ApothecaryMortarBlockEntity::new, APOTHECARY_MORTAR.get()));

	public static final Supplier<BlockItemBase> ALKAHESTRY_ALTAR_ITEM = ITEMS.registerItem(ALKAHESTRY_ALTAR_REGISTRY_NAME,
			properties -> new BlockItemBase(ALKAHESTRY_ALTAR.get(), properties));
	public static final Supplier<BlockItemBase> APOTHECARY_CAULDRON_ITEM = ITEMS.registerItem(APOTHECARY_CAULDRON_REGISTRY_NAME,
			properties -> new BlockItemBase(APOTHECARY_CAULDRON.get(), properties));
	public static final Supplier<BlockItemBase> APOTHECARY_MORTAR_ITEM = ITEMS.registerItem(APOTHECARY_MORTAR_REGISTRY_NAME,
			properties -> new BlockItemBase(APOTHECARY_MORTAR.get(), properties));
	public static final Supplier<BlockItemBase> FERTILE_LILY_PAD_ITEM = ITEMS.registerItem(FERTILE_LILY_PAD_REGISTRY_NAME, FertileLilyPadItem::new);
	public static final Supplier<BlockItemBase> WRAITH_NODE_ITEM = ITEMS.registerItem(WRAITH_NODE_REGISTRY_NAME,
			properties -> new BlockItemBase(WRAITH_NODE.get(), properties));
	public static final Supplier<InterdictionTorchItem> INTERDICTION_TORCH_ITEM = ITEMS.registerItem(INTERDICTION_TORCH_REGISTRY_NAME,
			InterdictionTorchItem::new);
	public static final Map<DyeColor, Supplier<BlockItem>> PEDESTAL_ITEMS;
	public static final Map<DyeColor, Supplier<BlockItem>> PASSIVE_PEDESTAL_ITEMS;

	private static final String BLOCK_PREFIX = "block.";

	static {
		ImmutableMap.Builder<DyeColor, Supplier<BlockItem>> passiveBuilder = ImmutableMap.builder();
		ImmutableMap.Builder<DyeColor, Supplier<BlockItem>> activeBuilder = ImmutableMap.builder();
		for (DyeColor color : DyeColor.values()) {
			passiveBuilder.put(color,
					ITEMS.registerItem("pedestals/passive/" + color.getName() + "_passive_pedestal",
							properties -> new BlockItemBase(PASSIVE_PEDESTALS.get(color).get(),
									properties.overrideDescription(BLOCK_PREFIX + Reliquary.MOD_ID + ".passive_pedestal"))));
			activeBuilder.put(color,
					ITEMS.registerItem("pedestals/" + color.getName() + "_pedestal", properties -> new BlockItemBase(PEDESTALS.get(color).get(),
							properties.overrideDescription(BLOCK_PREFIX + Reliquary.MOD_ID + ".pedestal"))));
		}
		PASSIVE_PEDESTAL_ITEMS = passiveBuilder.build();
		PEDESTAL_ITEMS = activeBuilder.build();
	}

	public static void registerListeners(IEventBus modBus) {
		ITEMS.register(modBus);
		BLOCKS.register(modBus);
		BLOCK_ENTITY_TYPES.register(modBus);
		modBus.addListener(ModBlocks::registerCapabilities);
	}

	@SuppressWarnings({"squid:S4449", "ConstantConditions"})
	// no datafixer is defined for any of the tile entities so this is moot
	private static <T extends BlockEntity> BlockEntityType<T> getBlockEntityType(BlockEntityType.BlockEntitySupplier<T> tileFactory, Block... validBlocks) {
		return new BlockEntityType<>(tileFactory, validBlocks);
	}

	private static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, APOTHECARY_MORTAR_TILE_TYPE.get(), (mortar, direction) -> mortar.getItems());
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, PASSIVE_PEDESTAL_TILE_TYPE.get(), (pedestal, direction) -> pedestal.getItemHandler());
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, PEDESTAL_TILE_TYPE.get(), (pedestal, direction) -> pedestal.getItemHandler());

		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, PEDESTAL_TILE_TYPE.get(), (pedestal, direction) -> pedestal.getFluidHandler());
	}
}
