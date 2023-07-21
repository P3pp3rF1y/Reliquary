package reliquary.init;

import com.google.common.collect.ImmutableMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import reliquary.Reliquary;
import reliquary.blocks.AlkahestryAltarBlock;
import reliquary.blocks.ApothecaryCauldronBlock;
import reliquary.blocks.ApothecaryMortarBlock;
import reliquary.blocks.FertileLilyPadBlock;
import reliquary.blocks.InterdictionTorchBlock;
import reliquary.blocks.PassivePedestalBlock;
import reliquary.blocks.PedestalBlock;
import reliquary.blocks.WallInterdictionTorchBlock;
import reliquary.blocks.WraithNodeBlock;
import reliquary.blocks.tile.AlkahestryAltarBlockEntity;
import reliquary.blocks.tile.ApothecaryCauldronBlockEntity;
import reliquary.blocks.tile.ApothecaryMortarBlockEntity;
import reliquary.blocks.tile.PassivePedestalBlockEntity;
import reliquary.blocks.tile.PedestalBlockEntity;
import reliquary.items.block.BlockItemBase;
import reliquary.items.block.FertileLilyPadItem;
import reliquary.reference.Reference;

import java.util.Map;

public class ModBlocks {
	private ModBlocks() {}

	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MOD_ID);
	private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Reference.MOD_ID);
	private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Reference.MOD_ID);

	private static final String ALKAHESTRY_ALTAR_REGISTRY_NAME = "alkahestry_altar";
	private static final String INTERDICTION_TORCH_REGISTRY_NAME = "interdiction_torch";
	private static final String APOTHECARY_CAULDRON_REGISTRY_NAME = "apothecary_cauldron";
	private static final String APOTHECARY_MORTAR_REGISTRY_NAME = "apothecary_mortar";
	private static final String FERTILE_LILY_PAD_REGISTRY_NAME = "fertile_lily_pad";
	private static final String WRAITH_NODE_REGISTRY_NAME = "wraith_node";

	public static final RegistryObject<AlkahestryAltarBlock> ALKAHESTRY_ALTAR = BLOCKS.register(ALKAHESTRY_ALTAR_REGISTRY_NAME, AlkahestryAltarBlock::new);
	public static final RegistryObject<ApothecaryCauldronBlock> APOTHECARY_CAULDRON = BLOCKS.register(APOTHECARY_CAULDRON_REGISTRY_NAME, ApothecaryCauldronBlock::new);
	public static final RegistryObject<ApothecaryMortarBlock> APOTHECARY_MORTAR = BLOCKS.register(APOTHECARY_MORTAR_REGISTRY_NAME, ApothecaryMortarBlock::new);
	public static final RegistryObject<FertileLilyPadBlock> FERTILE_LILY_PAD = BLOCKS.register(FERTILE_LILY_PAD_REGISTRY_NAME, FertileLilyPadBlock::new);
	public static final RegistryObject<InterdictionTorchBlock> INTERDICTION_TORCH = BLOCKS.register(INTERDICTION_TORCH_REGISTRY_NAME, InterdictionTorchBlock::new);
	public static final RegistryObject<WallInterdictionTorchBlock> WALL_INTERDICTION_TORCH = BLOCKS.register("wall_interdiction_torch", WallInterdictionTorchBlock::new);
	public static final RegistryObject<WraithNodeBlock> WRAITH_NODE = BLOCKS.register(WRAITH_NODE_REGISTRY_NAME, WraithNodeBlock::new);

	public static final Map<DyeColor, RegistryObject<PassivePedestalBlock>> PASSIVE_PEDESTALS;
	public static final Map<DyeColor, RegistryObject<PedestalBlock>> PEDESTALS;

	static {
		ImmutableMap.Builder<DyeColor, RegistryObject<PassivePedestalBlock>> passiveBuilder = ImmutableMap.builder();
		ImmutableMap.Builder<DyeColor, RegistryObject<PedestalBlock>> activeBuilder = ImmutableMap.builder();
		for (DyeColor color : DyeColor.values()) {
			passiveBuilder.put(color, BLOCKS.register("pedestals/passive/" + color.getName() + "_passive_pedestal", PassivePedestalBlock::new));
			activeBuilder.put(color, BLOCKS.register("pedestals/" + color.getName() + "_pedestal", PedestalBlock::new));
		}
		PASSIVE_PEDESTALS = passiveBuilder.build();
		PEDESTALS = activeBuilder.build();
	}

	public static final RegistryObject<BlockEntityType<AlkahestryAltarBlockEntity>> ALKAHESTRY_ALTAR_TILE_TYPE = BLOCK_ENTITY_TYPES.register(ALKAHESTRY_ALTAR_REGISTRY_NAME,
			() -> getTileEntityType(AlkahestryAltarBlockEntity::new, ALKAHESTRY_ALTAR.get()));
	public static final RegistryObject<BlockEntityType<PedestalBlockEntity>> PEDESTAL_TILE_TYPE = BLOCK_ENTITY_TYPES.register("pedestal",
			() -> getTileEntityType(PedestalBlockEntity::new, PEDESTALS.values().stream().map(RegistryObject::get).toArray(PedestalBlock[]::new)));
	public static final RegistryObject<BlockEntityType<PassivePedestalBlockEntity>> PASSIVE_PEDESTAL_TILE_TYPE = BLOCK_ENTITY_TYPES.register("passive_pedestal",
			() -> getTileEntityType(PassivePedestalBlockEntity::new, PASSIVE_PEDESTALS.values().stream().map(RegistryObject::get).toArray(PassivePedestalBlock[]::new)));
	public static final RegistryObject<BlockEntityType<ApothecaryCauldronBlockEntity>> APOTHECARY_CAULDRON_TILE_TYPE = BLOCK_ENTITY_TYPES.register(APOTHECARY_CAULDRON_REGISTRY_NAME,
			() -> getTileEntityType(ApothecaryCauldronBlockEntity::new, APOTHECARY_CAULDRON.get()));
	public static final RegistryObject<BlockEntityType<ApothecaryMortarBlockEntity>> APOTHECARY_MORTAR_TILE_TYPE = BLOCK_ENTITY_TYPES.register(APOTHECARY_MORTAR_REGISTRY_NAME,
			() -> getTileEntityType(ApothecaryMortarBlockEntity::new, APOTHECARY_MORTAR.get()));

	public static final RegistryObject<BlockItem> ALKAHESTRY_ALTAR_ITEM = ITEMS.register(ALKAHESTRY_ALTAR_REGISTRY_NAME, () -> new BlockItemBase(ALKAHESTRY_ALTAR.get()));
	public static final RegistryObject<BlockItem> APOTHECARY_CAULDRON_ITEM = ITEMS.register(APOTHECARY_CAULDRON_REGISTRY_NAME, () -> new BlockItemBase(APOTHECARY_CAULDRON.get()));
	public static final RegistryObject<BlockItem> APOTHECARY_MORTAR_ITEM = ITEMS.register(APOTHECARY_MORTAR_REGISTRY_NAME, () -> new BlockItemBase(APOTHECARY_MORTAR.get()));
	public static final RegistryObject<BlockItem> FERTILE_LILY_PAD_ITEM = ITEMS.register(FERTILE_LILY_PAD_REGISTRY_NAME, FertileLilyPadItem::new);
	public static final RegistryObject<BlockItem> WRAITH_NODE_ITEM = ITEMS.register(WRAITH_NODE_REGISTRY_NAME, () -> new BlockItemBase(WRAITH_NODE.get()));
	public static final RegistryObject<BlockItem> INTERDICTION_TORCH_ITEM = ITEMS.register(INTERDICTION_TORCH_REGISTRY_NAME,
			() -> new StandingAndWallBlockItem(INTERDICTION_TORCH.get(), WALL_INTERDICTION_TORCH.get(), new Item.Properties().tab(Reliquary.ITEM_GROUP)));
	public static final Map<DyeColor, RegistryObject<BlockItem>> PEDESTAL_ITEMS;
	public static final Map<DyeColor, RegistryObject<BlockItem>> PASSIVE_PEDESTAL_ITEMS;

	private static final String BLOCK_PREFIX = "block.";

	static {
		ImmutableMap.Builder<DyeColor, RegistryObject<BlockItem>> passiveBuilder = ImmutableMap.builder();
		ImmutableMap.Builder<DyeColor, RegistryObject<BlockItem>> activeBuilder = ImmutableMap.builder();
		for (DyeColor color : DyeColor.values()) {
			passiveBuilder.put(color, ITEMS.register("pedestals/passive/" + color.getName() + "_passive_pedestal", () -> new BlockItemBase(PASSIVE_PEDESTALS.get(color).get(), new Item.Properties()) {
				@Override
				public Component getName(ItemStack stack) {
					return Component.translatable(BLOCK_PREFIX + Reference.MOD_ID + ".passive_pedestal");
				}

				@Override
				public String getDescriptionId() {
					return BLOCK_PREFIX + Reference.MOD_ID + ".passive_pedestal";
				}
			}));
			activeBuilder.put(color, ITEMS.register("pedestals/" + color.getName() + "_pedestal", () -> new BlockItemBase(PEDESTALS.get(color).get(), new Item.Properties()) {
				@Override
				public Component getName(ItemStack stack) {
					return Component.translatable(BLOCK_PREFIX + Reference.MOD_ID + ".pedestal");
				}

				@Override
				public String getDescriptionId() {
					return BLOCK_PREFIX + Reference.MOD_ID + ".pedestal";
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
	}

	@SuppressWarnings({"squid:S4449", "ConstantConditions"}) // no datafixer is defined for any of the tile entities so this is moot
	private static <T extends BlockEntity> BlockEntityType<T> getTileEntityType(BlockEntityType.BlockEntitySupplier<T> tileFactory, Block... validBlocks) {
		return BlockEntityType.Builder.of(tileFactory, validBlocks).build(null);
	}
}
