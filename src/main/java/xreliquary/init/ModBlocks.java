package xreliquary.init;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WallOrFloorItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xreliquary.Reliquary;
import xreliquary.blocks.AlkahestryAltarBlock;
import xreliquary.blocks.ApothecaryCauldronBlock;
import xreliquary.blocks.ApothecaryMortarBlock;
import xreliquary.blocks.FertileLilyPadBlock;
import xreliquary.blocks.InterdictionTorchBlock;
import xreliquary.blocks.PassivePedestalBlock;
import xreliquary.blocks.PedestalBlock;
import xreliquary.blocks.WallInterdictionTorchBlock;
import xreliquary.blocks.WraithNodeBlock;
import xreliquary.blocks.tile.AlkahestryAltarTileEntity;
import xreliquary.blocks.tile.ApothecaryCauldronTileEntity;
import xreliquary.blocks.tile.ApothecaryMortarTileEntity;
import xreliquary.blocks.tile.PassivePedestalTileEntity;
import xreliquary.blocks.tile.PedestalTileEntity;
import xreliquary.items.block.BlockItemBase;
import xreliquary.items.block.FertileLilyPadItem;
import xreliquary.reference.Reference;
import xreliquary.util.LanguageHelper;

import java.util.Map;
import java.util.function.Supplier;

public class ModBlocks {
	private ModBlocks() {}

	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MOD_ID);
	private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Reference.MOD_ID);
	private static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Reference.MOD_ID);

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
			passiveBuilder.put(color, BLOCKS.register("pedestals/passive/" + color.getTranslationKey() + "_passive_pedestal", PassivePedestalBlock::new));
			activeBuilder.put(color, BLOCKS.register("pedestals/" + color.getTranslationKey() + "_pedestal", PedestalBlock::new));
		}
		PASSIVE_PEDESTALS = passiveBuilder.build();
		PEDESTALS = activeBuilder.build();
	}

	public static final RegistryObject<TileEntityType<AlkahestryAltarTileEntity>> ALKAHESTRY_ALTAR_TILE_TYPE = TILE_ENTITIES.register(ALKAHESTRY_ALTAR_REGISTRY_NAME,
			() -> getTileEntityType(AlkahestryAltarTileEntity::new, ALKAHESTRY_ALTAR.get()));
	public static final RegistryObject<TileEntityType<PedestalTileEntity>> PEDESTAL_TILE_TYPE = TILE_ENTITIES.register("pedestal",
			() -> getTileEntityType(PedestalTileEntity::new, PEDESTALS.values().stream().map(RegistryObject::get).toArray(PedestalBlock[]::new)));
	public static final RegistryObject<TileEntityType<PassivePedestalTileEntity>> PASSIVE_PEDESTAL_TILE_TYPE = TILE_ENTITIES.register("passive_pedestal",
			() -> getTileEntityType(PassivePedestalTileEntity::new, PASSIVE_PEDESTALS.values().stream().map(RegistryObject::get).toArray(PassivePedestalBlock[]::new)));
	public static final RegistryObject<TileEntityType<ApothecaryCauldronTileEntity>> APOTHECARY_CAULDRON_TILE_TYPE = TILE_ENTITIES.register(APOTHECARY_CAULDRON_REGISTRY_NAME,
			() -> getTileEntityType(ApothecaryCauldronTileEntity::new, APOTHECARY_CAULDRON.get()));
	public static final RegistryObject<TileEntityType<ApothecaryMortarTileEntity>> APOTHECARY_MORTAR_TILE_TYPE = TILE_ENTITIES.register(APOTHECARY_MORTAR_REGISTRY_NAME,
			() -> getTileEntityType(ApothecaryMortarTileEntity::new, APOTHECARY_MORTAR.get()));

	public static final RegistryObject<BlockItem> ALKAHESTRY_ALTAR_ITEM = ITEMS.register(ALKAHESTRY_ALTAR_REGISTRY_NAME, () -> new BlockItemBase(ALKAHESTRY_ALTAR.get()));
	public static final RegistryObject<BlockItem> APOTHECARY_CAULDRON_ITEM = ITEMS.register(APOTHECARY_CAULDRON_REGISTRY_NAME, () -> new BlockItemBase(APOTHECARY_CAULDRON.get()));
	public static final RegistryObject<BlockItem> APOTHECARY_MORTAR_ITEM = ITEMS.register(APOTHECARY_MORTAR_REGISTRY_NAME, () -> new BlockItemBase(APOTHECARY_MORTAR.get()));
	public static final RegistryObject<BlockItem> FERTILE_LILY_PAD_ITEM = ITEMS.register(FERTILE_LILY_PAD_REGISTRY_NAME, FertileLilyPadItem::new);
	public static final RegistryObject<BlockItem> WRAITH_NODE_ITEM = ITEMS.register(WRAITH_NODE_REGISTRY_NAME,() -> new BlockItemBase(WRAITH_NODE.get()));
	public static final RegistryObject<BlockItem> INTERDICTION_TORCH_ITEM = ITEMS.register(INTERDICTION_TORCH_REGISTRY_NAME,
			() -> new WallOrFloorItem(INTERDICTION_TORCH.get(), WALL_INTERDICTION_TORCH.get(), new Item.Properties().group(Reliquary.ITEM_GROUP)) {
		@Override
		public ITextComponent getDisplayName(ItemStack stack) {
			return new StringTextComponent(LanguageHelper.getLocalization(getTranslationKey(stack)));
		}
	});
	public static final Map<DyeColor, RegistryObject<BlockItem>> PEDESTAL_ITEMS;
	public static final Map<DyeColor, RegistryObject<BlockItem>> PASSIVE_PEDESTAL_ITEMS;
	static {
		ImmutableMap.Builder<DyeColor, RegistryObject<BlockItem>> passiveBuilder = ImmutableMap.builder();
		ImmutableMap.Builder<DyeColor, RegistryObject<BlockItem>> activeBuilder = ImmutableMap.builder();
		for (DyeColor color : DyeColor.values()) {
			passiveBuilder.put(color, ITEMS.register("pedestals/passive/" + color.getTranslationKey() + "_passive_pedestal", () -> new BlockItemBase(PASSIVE_PEDESTALS.get(color).get(), new Item.Properties()) {
				@Override
				public ITextComponent getDisplayName(ItemStack stack) {
					return new TranslationTextComponent("block." + Reference.MOD_ID + ".passive_pedestal");
				}
			}));
			activeBuilder.put(color, ITEMS.register("pedestals/" + color.getTranslationKey() + "_pedestal", () -> new BlockItemBase(PEDESTALS.get(color).get(), new Item.Properties()) {
				@Override
				public ITextComponent getDisplayName(ItemStack stack) {
					return new TranslationTextComponent("block." + Reference.MOD_ID + ".pedestal");
				}
			}));
		}
		PASSIVE_PEDESTAL_ITEMS = passiveBuilder.build();
		PEDESTAL_ITEMS = activeBuilder.build();
	}

	public static void registerListeners(IEventBus modBus) {
		ITEMS.register(modBus);
		BLOCKS.register(modBus);
		TILE_ENTITIES.register(modBus);
	}

	@SuppressWarnings({"squid:S4449", "ConstantConditions"}) // no datafixer is defined for any of the tile entities so this is moot
	private static <T extends TileEntity> TileEntityType<T> getTileEntityType(Supplier<T> tileFactory, Block... validBlocks) {
		return TileEntityType.Builder.create(tileFactory, validBlocks).build(null);
	}
}
