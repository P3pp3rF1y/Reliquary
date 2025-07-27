package reliquary.init;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
	private ModBlocks() {}

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, Reliquary.MOD_ID);
	private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, Reliquary.MOD_ID);
	private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Reliquary.MOD_ID);

	private static final String ALKAHESTRY_ALTAR_REGISTRY_NAME = "alkahestry_altar";
	private static final String INTERDICTION_TORCH_REGISTRY_NAME = "interdiction_torch";
	private static final String APOTHECARY_CAULDRON_REGISTRY_NAME = "apothecary_cauldron";
	private static final String APOTHECARY_MORTAR_REGISTRY_NAME = "apothecary_mortar";
	private static final String FERTILE_LILY_PAD_REGISTRY_NAME = "fertile_lily_pad";
	private static final String WRAITH_NODE_REGISTRY_NAME = "wraith_node";

	public static final Supplier<AlkahestryAltarBlock> ALKAHESTRY_ALTAR = BLOCKS.register(ALKAHESTRY_ALTAR_REGISTRY_NAME, AlkahestryAltarBlock::new);
	public static final Supplier<ApothecaryCauldronBlock> APOTHECARY_CAULDRON = BLOCKS.register(APOTHECARY_CAULDRON_REGISTRY_NAME, ApothecaryCauldronBlock::new);
	public static final Supplier<ApothecaryMortarBlock> APOTHECARY_MORTAR = BLOCKS.register(APOTHECARY_MORTAR_REGISTRY_NAME, ApothecaryMortarBlock::new);
	public static final Supplier<FertileLilyPadBlock> FERTILE_LILY_PAD = BLOCKS.register(FERTILE_LILY_PAD_REGISTRY_NAME, FertileLilyPadBlock::new);
	public static final Supplier<InterdictionTorchBlock> INTERDICTION_TORCH = BLOCKS.register(INTERDICTION_TORCH_REGISTRY_NAME, InterdictionTorchBlock::new);
	public static final Supplier<WallInterdictionTorchBlock> WALL_INTERDICTION_TORCH = BLOCKS.register("wall_interdiction_torch", WallInterdictionTorchBlock::new);
	public static final Supplier<WraithNodeBlock> WRAITH_NODE = BLOCKS.register(WRAITH_NODE_REGISTRY_NAME, WraithNodeBlock::new);

	public static final Map<DyeColor, Supplier<PassivePedestalBlock>> PASSIVE_PEDESTALS;
	public static final Map<DyeColor, Supplier<PedestalBlock>> PEDESTALS;

	static {
		ImmutableMap.Builder<DyeColor, Supplier<PassivePedestalBlock>> passiveBuilder = ImmutableMap.builder();
		ImmutableMap.Builder<DyeColor, Supplier<PedestalBlock>> activeBuilder = ImmutableMap.builder();
		for (DyeColor color : DyeColor.values()) {
			passiveBuilder.put(color, BLOCKS.register("pedestals/passive/" + color.getName() + "_passive_pedestal", PassivePedestalBlock::new));
			activeBuilder.put(color, BLOCKS.register("pedestals/" + color.getName() + "_pedestal", PedestalBlock::new));
		}
		PASSIVE_PEDESTALS = passiveBuilder.build();
		PEDESTALS = activeBuilder.build();
	}

	public static final Supplier<BlockEntityType<AlkahestryAltarBlockEntity>> ALKAHESTRY_ALTAR_TILE_TYPE = BLOCK_ENTITY_TYPES.register(ALKAHESTRY_ALTAR_REGISTRY_NAME,
			() -> getTileEntityType(AlkahestryAltarBlockEntity::new, ALKAHESTRY_ALTAR.get()));
	public static final Supplier<BlockEntityType<PedestalBlockEntity>> PEDESTAL_TILE_TYPE = BLOCK_ENTITY_TYPES.register("pedestal",
			() -> getTileEntityType(PedestalBlockEntity::new, PEDESTALS.values().stream().map(Supplier::get).toArray(PedestalBlock[]::new)));
	public static final Supplier<BlockEntityType<PassivePedestalBlockEntity>> PASSIVE_PEDESTAL_TILE_TYPE = BLOCK_ENTITY_TYPES.register("passive_pedestal",
			() -> getTileEntityType(PassivePedestalBlockEntity::new, PASSIVE_PEDESTALS.values().stream().map(Supplier::get).toArray(PassivePedestalBlock[]::new)));
	public static final Supplier<BlockEntityType<ApothecaryCauldronBlockEntity>> APOTHECARY_CAULDRON_TILE_TYPE = BLOCK_ENTITY_TYPES.register(APOTHECARY_CAULDRON_REGISTRY_NAME,
			() -> getTileEntityType(ApothecaryCauldronBlockEntity::new, APOTHECARY_CAULDRON.get()));
	public static final Supplier<BlockEntityType<ApothecaryMortarBlockEntity>> APOTHECARY_MORTAR_TILE_TYPE = BLOCK_ENTITY_TYPES.register(APOTHECARY_MORTAR_REGISTRY_NAME,
			() -> getTileEntityType(ApothecaryMortarBlockEntity::new, APOTHECARY_MORTAR.get()));

	public static final Supplier<BlockItemBase> ALKAHESTRY_ALTAR_ITEM = ITEMS.register(ALKAHESTRY_ALTAR_REGISTRY_NAME, () -> new BlockItemBase(ALKAHESTRY_ALTAR.get()));
	public static final Supplier<BlockItemBase> APOTHECARY_CAULDRON_ITEM = ITEMS.register(APOTHECARY_CAULDRON_REGISTRY_NAME, () -> new BlockItemBase(APOTHECARY_CAULDRON.get()));
	public static final Supplier<BlockItemBase> APOTHECARY_MORTAR_ITEM = ITEMS.register(APOTHECARY_MORTAR_REGISTRY_NAME, () -> new BlockItemBase(APOTHECARY_MORTAR.get()));
	public static final Supplier<BlockItemBase> FERTILE_LILY_PAD_ITEM = ITEMS.register(FERTILE_LILY_PAD_REGISTRY_NAME, FertileLilyPadItem::new);
	public static final Supplier<BlockItemBase> WRAITH_NODE_ITEM = ITEMS.register(WRAITH_NODE_REGISTRY_NAME, () -> new BlockItemBase(WRAITH_NODE.get()));
	public static final Supplier<InterdictionTorchItem> INTERDICTION_TORCH_ITEM = ITEMS.register(INTERDICTION_TORCH_REGISTRY_NAME, InterdictionTorchItem::new);
	public static final Map<DyeColor, Supplier<BlockItem>> PEDESTAL_ITEMS;
	public static final Map<DyeColor, Supplier<BlockItem>> PASSIVE_PEDESTAL_ITEMS;

	private static final String BLOCK_PREFIX = "block.";

	static {
		ImmutableMap.Builder<DyeColor, Supplier<BlockItem>> passiveBuilder = ImmutableMap.builder();
		ImmutableMap.Builder<DyeColor, Supplier<BlockItem>> activeBuilder = ImmutableMap.builder();
		for (DyeColor color : DyeColor.values()) {
			passiveBuilder.put(color, ITEMS.register("pedestals/passive/" + color.getName() + "_passive_pedestal", () -> new BlockItemBase(PASSIVE_PEDESTALS.get(color).get(), new Item.Properties()) {
				@Override
				public Component getName(ItemStack stack) {
					return Component.translatable(BLOCK_PREFIX + Reliquary.MOD_ID + ".passive_pedestal");
				}

				@Override
				public String getDescriptionId() {
					return BLOCK_PREFIX + Reliquary.MOD_ID + ".passive_pedestal";
				}
			}));
			activeBuilder.put(color, ITEMS.register("pedestals/" + color.getName() + "_pedestal", () -> new BlockItemBase(PEDESTALS.get(color).get(), new Item.Properties()) {
				@Override
				public Component getName(ItemStack stack) {
					return Component.translatable(BLOCK_PREFIX + Reliquary.MOD_ID + ".pedestal");
				}

				@Override
				public String getDescriptionId() {
					return BLOCK_PREFIX + Reliquary.MOD_ID + ".pedestal";
				}
			}));
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

	@SuppressWarnings({"squid:S4449", "ConstantConditions"}) // no datafixer is defined for any of the tile entities so this is moot
	private static <T extends BlockEntity> BlockEntityType<T> getTileEntityType(BlockEntityType.BlockEntitySupplier<T> tileFactory, Block... validBlocks) {
		return BlockEntityType.Builder.of(tileFactory, validBlocks).build(null);
	}

	private static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, APOTHECARY_MORTAR_TILE_TYPE.get(), (mortar, direction) -> mortar.getItems());
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, PASSIVE_PEDESTAL_TILE_TYPE.get(), (pedestal, direction) -> pedestal.getItemHandler());
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, PEDESTAL_TILE_TYPE.get(), (pedestal, direction) -> pedestal.getItemHandler());

		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, PEDESTAL_TILE_TYPE.get(), (pedestal, direction) -> pedestal.getFluidHandler());
	}
}
