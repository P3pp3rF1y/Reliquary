package reliquary.data;

import net.minecraft.client.color.item.Potion;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.ConditionBuilder;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.block.model.multipart.CombinedCondition;
import net.minecraft.client.renderer.block.model.multipart.Condition;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.conditional.FishingRodCast;
import net.minecraft.client.renderer.item.properties.conditional.HasComponent;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.item.DynamicFluidContainerModel;
import reliquary.Reliquary;
import reliquary.block.AlkahestryAltarBlock;
import reliquary.block.ApothecaryCauldronBlock;
import reliquary.block.FertileLilyPadBlock;
import reliquary.block.PedestalBlock;
import reliquary.client.color.item.CharmTintSources;
import reliquary.client.model.VoidTearItemModel;
import reliquary.init.ModBlocks;
import reliquary.init.ModFluids;
import reliquary.init.ModItems;
import reliquary.item.*;
import reliquary.item.properties.conditional.InfernalTearEmpty;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class ReliquaryModelProvider extends ModelProvider {
	private static final TextureSlot WOOL_SLOT = TextureSlot.create("wool");
	private static final ModelTemplate PEDESTAL_TEMPLATE = ModelTemplates.create(Reliquary.getRL("pedestal").toString(), WOOL_SLOT);
	private static final ModelTemplate PASSIVE_PEDESTAL_TEMPLATE = ModelTemplates.create(Reliquary.getRL("passive_pedestal").toString(), WOOL_SLOT);
	private final Set<Item> itemsWithGeneratedModels = new HashSet<>();

	public ReliquaryModelProvider(PackOutput output) {
		super(output, Reliquary.MOD_ID);
	}

	@Override
	protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
		itemsWithGeneratedModels.clear();

		generateVoidTear(itemModels);
		generateRodOfLyssa(itemModels);
		generateInfernalTear(itemModels);
		generateMagazinesAndBullets(itemModels);
		generatePotion(itemModels, ModItems.POTION.get());
		generatePotion(itemModels, ModItems.SPLASH_POTION.get());
		generatePotion(itemModels, ModItems.LINGERING_POTION.get());
		generatePotionEssence(itemModels);
		generateTippedArrow(itemModels);
		generateCharmAndFragment(itemModels);
		generateHandHeldModel(itemModels, ModItems.MERCY_CROSS.get());
		generateHandHeldModel(itemModels, ModItems.PYROMANCER_STAFF.get());
		generateHandHeldModel(itemModels, ModItems.ENDER_STAFF.get());
		generateHandHeldModel(itemModels, ModItems.GLACIAL_STAFF.get());
		generateHandHeldModel(itemModels, ModItems.HARVEST_ROD.get());
		generateHandHeldModel(itemModels, ModItems.ICE_MAGUS_ROD.get());
		generateHandHeldModel(itemModels, ModItems.MAGICBANE.get());
		generateHandHeldModel(itemModels, ModItems.RENDING_GALE.get());
		generateHandHeldModel(itemModels, ModItems.SERPENT_STAFF.get());
		generateHandHeldModel(itemModels, ModItems.SOJOURNER_STAFF.get());
		generateCustomModel(itemModels, ModItems.HANDGUN.get(), ItemModelUtils.plainModel(Reliquary.getRL("handgun").withPrefix("item/")));

		generateItemBaseFlatItemModels(itemModels);
		itemModels.generateFlatItem(ModItems.WITCH_HAT.get(), ModelTemplates.FLAT_ITEM);
		itemModels.generateFlatItem(ModItems.SHEARS_OF_WINTER.get(), ModelTemplates.FLAT_ITEM);
		generateXpBucket(itemModels);

		generateAlkahestryAltar(blockModels);
		generateCustomModelBlockWithVariants(blockModels, ModBlocks.APOTHECARY_CAULDRON.get(), createLevelDispatch());
		generateCustomModelBlockWithHorizontalFacing(blockModels, ModBlocks.APOTHECARY_MORTAR.get());
		generateFertileLilypad(blockModels);
		blockModels.createNormalTorch(ModBlocks.INTERDICTION_TORCH.get(), ModBlocks.WALL_INTERDICTION_TORCH.get());
		generateCustomModelBlock(blockModels, ModBlocks.WRAITH_NODE.get());
		ModBlocks.PEDESTALS.forEach((dye, pedestal) -> generatePedestal(blockModels, pedestal.get(), dye));
		ModBlocks.PASSIVE_PEDESTALS.forEach((dye, pedestal) -> generatePassivePedestal(blockModels, pedestal.get(), dye));
	}

	private void generateXpBucket(ItemModelGenerators itemModels) {
		itemModels.itemModelOutput.accept(ModItems.XP_BUCKET.get(),
				new DynamicFluidContainerModel.Unbaked(
						new DynamicFluidContainerModel.Textures(Optional.of(ResourceLocation.withDefaultNamespace("item/bucket")),
								Optional.of(ResourceLocation.withDefaultNamespace("item/bucket")),
								Optional.of(ResourceLocation.fromNamespaceAndPath("neoforge", "item/mask/bucket_fluid")), Optional.empty()),
						ModFluids.XP_STILL.get(), false, false, false));
	}

	private void generateAlkahestryAltar(BlockModelGenerators blockModels) {
		Block block = ModBlocks.ALKAHESTRY_ALTAR.get();
		ResourceLocation blockModelId = TexturedModel.CUBE.create(block, blockModels.modelOutput);
		ResourceLocation activeAlkahestryAltar = Reliquary.getRL("alkahestry_altar_active").withPrefix("block/");
		ResourceLocation activeBlockModelId = ModelTemplates.CUBE_ALL.create(activeAlkahestryAltar, TextureMapping.cube(activeAlkahestryAltar),
				blockModels.modelOutput);
		blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block).with(PropertyDispatch.initial(AlkahestryAltarBlock.ACTIVE)
				.select(true, BlockModelGenerators.plainVariant(activeBlockModelId)).select(false, BlockModelGenerators.plainVariant(blockModelId))));
		blockModels.itemModelOutput.accept(block.asItem(), ItemModelUtils.plainModel(blockModelId));
	}

	private PropertyDispatch<MultiVariant> createLevelDispatch() {
		return PropertyDispatch.initial(ApothecaryCauldronBlock.LEVEL)
				.select(0, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(ModBlocks.APOTHECARY_CAULDRON.get())))
				.select(1, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(ModBlocks.APOTHECARY_CAULDRON.get(), "_level1")))
				.select(2, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(ModBlocks.APOTHECARY_CAULDRON.get(), "_level2")))
				.select(3, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(ModBlocks.APOTHECARY_CAULDRON.get(), "_level3")));
	}

	private void generateCharmAndFragment(ItemModelGenerators itemModels) {
		Item charmItem = ModItems.MOB_CHARM.get();
		ResourceLocation modelLocation = ModelLocationUtils.getModelLocation(charmItem);
		itemModels.generateLayeredItem(modelLocation, TextureMapping.getItemTexture(charmItem), TextureMapping.getItemTexture(charmItem, "_overlay_1"),
				TextureMapping.getItemTexture(charmItem, "_overlay_2"));
		generateCustomModel(itemModels, charmItem,
				ItemModelUtils.tintedModel(modelLocation, ItemModelGenerators.BLANK_LAYER, CharmTintSources.Main.INSTANCE, CharmTintSources.Accent.INSTANCE));

		Item fragmentItem = ModItems.MOB_CHARM_FRAGMENT.get();
		generateCustomModel(itemModels, fragmentItem,
				ItemModelUtils.tintedModel(itemModels.generateLayeredItem(fragmentItem, TextureMapping.getItemTexture(fragmentItem),
						TextureMapping.getItemTexture(fragmentItem, "_overlay")), CharmTintSources.Main.INSTANCE, CharmTintSources.Accent.INSTANCE));
	}

	private void generateMagazinesAndBullets(ItemModelGenerators itemModels) {
		ModItems.ITEMS.getEntries().stream().filter(
				entry -> entry.get() instanceof BulletItem && entry.get() != ModItems.EMPTY_BULLET.get() && entry.get() != ModItems.EMPTY_MAGAZINE.get())
				.forEach(entry -> {
					ResourceLocation onTrueLocation = ModelLocationUtils.getModelLocation(entry.get(), "_potion");
					ResourceLocation baseTextureLocation = TextureMapping.getItemTexture(entry.get());
					String type = entry.get() instanceof MagazineItem ? "magazine" : "bullet";
					String folder = entry.get() instanceof MagazineItem ? "magazines/" : "bullets/";
					ResourceLocation potionOverlayLocation = Reliquary.getRL(type + "_potion_overlay").withPrefix("item/" + folder);

					ItemModel.Unbaked onTrue = ItemModelUtils.tintedModel(
							itemModels.generateLayeredItem(onTrueLocation, baseTextureLocation, potionOverlayLocation), ItemModelGenerators.BLANK_LAYER,
							new Potion());
					ItemModel.Unbaked onFalse = ItemModelUtils.tintedModel(itemModels.createFlatItemModel(entry.get(), ModelTemplates.FLAT_ITEM));
					generateConditional(itemModels, entry.get(), new HasComponent(DataComponents.POTION_CONTENTS, false), onTrue, onFalse);
				});
	}

	private void generateTippedArrow(ItemModelGenerators itemModels) {
		itemModels.generateTippedArrow(ModItems.TIPPED_ARROW.get());
		itemsWithGeneratedModels.add(ModItems.TIPPED_ARROW.get());
	}

	private void generatePotionEssence(ItemModelGenerators itemModels) {
		generateCustomModel(itemModels, ModItems.POTION_ESSENCE.get(),
				ItemModelUtils.tintedModel(itemModels.createFlatItemModel(ModItems.POTION_ESSENCE.get(), ModelTemplates.FLAT_ITEM), new Potion()));
	}

	private void generatePotion(ItemModelGenerators itemModels, PotionItemBase potionItem) {
		generateCustomModel(itemModels, potionItem, ItemModelUtils.tintedModel(
				itemModels.generateLayeredItem(potionItem, TextureMapping.getItemTexture(potionItem), TextureMapping.getItemTexture(potionItem, "_overlay")),
				ItemModelGenerators.BLANK_LAYER, new Potion()));
	}

	private void generateRodOfLyssa(ItemModelGenerators itemModels) {
		ItemModel.Unbaked onTrue = ItemModelUtils
				.plainModel(itemModels.createFlatItemModel(ModItems.ROD_OF_LYSSA.get(), "_cast", ModelTemplates.FLAT_HANDHELD_ROD_ITEM));
		ItemModel.Unbaked onFalse = ItemModelUtils
				.plainModel(itemModels.createFlatItemModel(ModItems.ROD_OF_LYSSA.get(), ModelTemplates.FLAT_HANDHELD_ROD_ITEM));
		generateConditional(itemModels, ModItems.ROD_OF_LYSSA.get(), new FishingRodCast(), onTrue, onFalse);
	}

	private void generateInfernalTear(ItemModelGenerators itemModels) {
		ItemModel.Unbaked onTrue = ItemModelUtils.plainModel(itemModels.createFlatItemModel(ModItems.INFERNAL_TEAR.get(), "_empty", ModelTemplates.FLAT_ITEM));
		ItemModel.Unbaked onFalse = ItemModelUtils.plainModel(itemModels.createFlatItemModel(ModItems.INFERNAL_TEAR.get(), ModelTemplates.FLAT_ITEM));
		generateConditional(itemModels, ModItems.INFERNAL_TEAR.get(), new InfernalTearEmpty(), onTrue, onFalse);
	}

	private void generateConditional(ItemModelGenerators itemModels, Item item, ConditionalItemModelProperty property, ItemModel.Unbaked onTrue,
			ItemModel.Unbaked onFalse) {
		generateCustomModel(itemModels, item, ItemModelUtils.conditional(property, onTrue, onFalse));
	}

	private void generateVoidTear(ItemModelGenerators itemModels) {
		VoidTearItem voidTearItem = ModItems.VOID_TEAR.get();
		ItemModel.Unbaked filledTear = ItemModelUtils.plainModel(itemModels.createFlatItemModel(voidTearItem, ModelTemplates.FLAT_ITEM));
		ItemModel.Unbaked emptyTear = ItemModelUtils.plainModel(itemModels.createFlatItemModel(voidTearItem, "_empty", ModelTemplates.FLAT_ITEM));
		VoidTearItemModel.Unbaked unbakedSpecialModel = new VoidTearItemModel.Unbaked(emptyTear, filledTear);
		generateCustomModel(itemModels, voidTearItem, unbakedSpecialModel);
	}

	private void generateCustomModel(ItemModelGenerators itemModels, Item item, ItemModel.Unbaked unbakedCustomModel) {
		itemsWithGeneratedModels.add(item);
		itemModels.itemModelOutput.accept(item, unbakedCustomModel);
	}

	private void generateHandHeldModel(ItemModelGenerators itemModels, Item item) {
		itemsWithGeneratedModels.add(item);
		itemModels.generateFlatItem(item, ModelTemplates.FLAT_HANDHELD_ITEM);
	}

	protected void generateItemBaseFlatItemModels(ItemModelGenerators itemModels) {
		ModItems.ITEMS.getEntries().stream().filter(entry -> entry.get() instanceof ItemBase && !(itemsWithGeneratedModels.contains(entry.get())))
				.forEach(entry -> itemModels.generateFlatItem(entry.get(), ModelTemplates.FLAT_ITEM));
	}

	private static void generateCustomModelBlockWithVariants(BlockModelGenerators blockModels, Block block, PropertyDispatch<MultiVariant> propertyDispatch) {
		ResourceLocation blockModelId = ModelLocationUtils.getModelLocation(block);
		blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block).with(propertyDispatch));
		blockModels.itemModelOutput.accept(block.asItem(), ItemModelUtils.plainModel(blockModelId));
	}

	private static void generateCustomModelBlockWithHorizontalFacing(BlockModelGenerators blockModels, Block block) {
		ResourceLocation blockModelId = ModelLocationUtils.getModelLocation(block);
		blockModels.blockStateOutput.accept(
				MultiVariantGenerator.dispatch(block, BlockModelGenerators.plainVariant(blockModelId)).with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING));
		blockModels.itemModelOutput.accept(block.asItem(), ItemModelUtils.plainModel(blockModelId));
	}

	private static void generatePassivePedestal(BlockModelGenerators blockModels, Block pedestal, DyeColor color) {
		ResourceLocation blockModelId = PASSIVE_PEDESTAL_TEMPLATE.create(pedestal,
				new TextureMapping().put(WOOL_SLOT, ResourceLocation.parse(color.getName() + "_wool").withPrefix("block/")), blockModels.modelOutput);
		blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(pedestal, BlockModelGenerators.plainVariant(blockModelId))
				.with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING));
		blockModels.itemModelOutput.accept(pedestal.asItem(), ItemModelUtils.plainModel(blockModelId));
	}

	private static void generatePedestal(BlockModelGenerators blockModels, Block pedestal, DyeColor color) {
		ResourceLocation blockModelId = PEDESTAL_TEMPLATE.create(pedestal,
				new TextureMapping().put(WOOL_SLOT, ResourceLocation.parse(color.getName() + "_wool").withPrefix("block/")), blockModels.modelOutput);
		ResourceLocation buttonOnModel = Reliquary.getRL("block/pedestal_button_on");
		ResourceLocation buttonOffModel = Reliquary.getRL("block/pedestal_button_off");
		blockModels.blockStateOutput.accept(MultiPartGenerator.multiPart(pedestal)
				.with(BlockModelGenerators.condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH),
						BlockModelGenerators.plainVariant(blockModelId))
				.with(BlockModelGenerators.condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST),
						BlockModelGenerators.plainVariant(blockModelId).with(BlockModelGenerators.Y_ROT_90))
				.with(BlockModelGenerators.condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH),
						BlockModelGenerators.plainVariant(blockModelId).with(BlockModelGenerators.Y_ROT_180))
				.with(BlockModelGenerators.condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST),
						BlockModelGenerators.plainVariant(blockModelId).with(BlockModelGenerators.Y_ROT_270))
				.with(and(BlockModelGenerators.condition().term(PedestalBlock.ENABLED, true),
						BlockModelGenerators.condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)),
						BlockModelGenerators.plainVariant(buttonOnModel))
				.with(and(BlockModelGenerators.condition().term(PedestalBlock.ENABLED, true),
						BlockModelGenerators.condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)),
						BlockModelGenerators.plainVariant(buttonOnModel).with(BlockModelGenerators.Y_ROT_90))
				.with(and(BlockModelGenerators.condition().term(PedestalBlock.ENABLED, true),
						BlockModelGenerators.condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)),
						BlockModelGenerators.plainVariant(buttonOnModel).with(BlockModelGenerators.Y_ROT_180))
				.with(and(BlockModelGenerators.condition().term(PedestalBlock.ENABLED, true),
						BlockModelGenerators.condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST)),
						BlockModelGenerators.plainVariant(buttonOnModel).with(BlockModelGenerators.Y_ROT_270))
				.with(and(BlockModelGenerators.condition().term(PedestalBlock.ENABLED, false),
						BlockModelGenerators.condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)),
						BlockModelGenerators.plainVariant(buttonOffModel))
				.with(and(BlockModelGenerators.condition().term(PedestalBlock.ENABLED, false),
						BlockModelGenerators.condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)),
						BlockModelGenerators.plainVariant(buttonOffModel).with(BlockModelGenerators.Y_ROT_90))
				.with(and(BlockModelGenerators.condition().term(PedestalBlock.ENABLED, false),
						BlockModelGenerators.condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)),
						BlockModelGenerators.plainVariant(buttonOffModel).with(BlockModelGenerators.Y_ROT_180))
				.with(and(BlockModelGenerators.condition().term(PedestalBlock.ENABLED, false),
						BlockModelGenerators.condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST)),
						BlockModelGenerators.plainVariant(buttonOffModel).with(BlockModelGenerators.Y_ROT_270)));
		blockModels.itemModelOutput.accept(pedestal.asItem(), ItemModelUtils.plainModel(blockModelId));
	}

	public static Condition and(ConditionBuilder... conditionBuilders) {
		return new CombinedCondition(CombinedCondition.Operation.AND, Stream.of(conditionBuilders).map(ConditionBuilder::build).toList());
	}

	private static void generateCustomModelBlock(BlockModelGenerators blockModels, Block block) {
		ResourceLocation blockModelId = ModelLocationUtils.getModelLocation(block);
		blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, BlockModelGenerators.plainVariant(blockModelId)));
		blockModels.itemModelOutput.accept(block.asItem(), ItemModelUtils.plainModel(blockModelId));
	}

	private static void generateFertileLilypad(BlockModelGenerators blockModels) {
		FertileLilyPadBlock block = ModBlocks.FERTILE_LILY_PAD.get();
		ResourceLocation blockModelId = ModelLocationUtils.getModelLocation(block);
		blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block,
				BlockModelGenerators.variants(BlockModelGenerators.plainModel(blockModelId),
						BlockModelGenerators.plainModel(blockModelId).with(BlockModelGenerators.Y_ROT_90),
						BlockModelGenerators.plainModel(blockModelId).with(BlockModelGenerators.Y_ROT_180),
						BlockModelGenerators.plainModel(blockModelId).with(BlockModelGenerators.Y_ROT_270))));

		ResourceLocation itemModelId = blockModels.createFlatItemModelWithBlockTexture(block.asItem(), block);
		blockModels.itemModelOutput.accept(block.asItem(), ItemModelUtils.plainModel(itemModelId));
	}
}
