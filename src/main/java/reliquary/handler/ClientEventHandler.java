package reliquary.handler;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.locale.Language;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import reliquary.Reliquary;
import reliquary.client.gui.components.Box;
import reliquary.client.gui.components.Component;
import reliquary.client.gui.components.ItemStackPane;
import reliquary.client.gui.components.TextPane;
import reliquary.client.gui.hud.*;
import reliquary.client.init.ItemModels;
import reliquary.client.init.ModBlockColors;
import reliquary.client.init.ModItemColors;
import reliquary.client.init.ModParticles;
import reliquary.client.model.MobCharmBeltModel;
import reliquary.client.model.WitchHatModel;
import reliquary.client.registry.PedestalClientRegistry;
import reliquary.client.render.*;
import reliquary.init.ModBlocks;
import reliquary.init.ModEntities;
import reliquary.init.ModFluids;
import reliquary.init.ModItems;
import reliquary.item.*;
import reliquary.item.util.IScrollableItem;
import reliquary.network.ScrolledItemPayload;
import reliquary.reference.Colors;
import reliquary.reference.Config;
import reliquary.util.InventoryHelper;
import reliquary.util.potions.PotionHelper;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public class ClientEventHandler {
	private ClientEventHandler() {
	}

	private static final int KEY_UNKNOWN = -1;
	public static final KeyMapping FORTUNE_COIN_TOGGLE_KEYBIND = new KeyMapping("keybind.reliquary.fortune_coin", KeyConflictContext.UNIVERSAL, InputConstants.Type.KEYSYM.getOrCreate(KEY_UNKNOWN), "keybind.reliquary.category");
	private static final String VOID_TEAR_MODE_TRANSLATION = "item." + Reliquary.MOD_ID + ".void_tear.mode.";
	public static final ModelLayerLocation WITCH_HAT_LAYER = new ModelLayerLocation(Reliquary.getRL("witch_hat"), "main");
	public static final ModelLayerLocation MOB_CHARM_BELT_LAYER = new ModelLayerLocation(Reliquary.getRL("mob_charm_belt"), "main");

	public static void registerHandlers(ModContainer container) {
		IEventBus modBus = ModLoadingContext.get().getActiveContainer().getEventBus();
		if (modBus == null) {
			return;
		}

		modBus.addListener(ClientEventHandler::clientSetup);
		modBus.addListener(ClientEventHandler::registerKeyMappings);
		modBus.addListener(ClientEventHandler::loadComplete);
		modBus.addListener(ModParticles.ProviderHandler::registerProviders);
		modBus.addListener(ClientEventHandler::registerEntityRenderers);
		modBus.addListener(ItemModels::onModelBake);
		modBus.addListener(ClientEventHandler::registerLayer);
		modBus.addListener(ModBlockColors::registerBlockColors);
		modBus.addListener(ModItemColors::registerItemColors);
		modBus.addListener(ClientEventHandler::registerOverlay);
		modBus.addListener(ClientEventHandler::registerBackpackClientExtension);

		IEventBus eventBus = NeoForge.EVENT_BUS;
		eventBus.addListener(ClientEventHandler::onRenderLiving);
		eventBus.addListener(ClientEventHandler::onMouseScrolled);

		//container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new); TODO add but requires adding a ton of translations and translation keys (so that they follow config setting levels)
	}

	private static void onRenderLiving(RenderLivingEvent.Pre<Player, PlayerModel<Player>> event) {
		if (event.getEntity() instanceof Player player) {

			boolean handgunInOff = player.getItemInHand(InteractionHand.OFF_HAND).getItem() == ModItems.HANDGUN.get();
			boolean handgunInMain = player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == ModItems.HANDGUN.get();

			if (handgunInOff || handgunInMain) {
				setHandgunArmPoses(event, player, handgunInOff, handgunInMain);
			}
		}
	}

	private static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(WITCH_HAT_LAYER, WitchHatModel::createBodyLayer);
		event.registerLayerDefinition(MOB_CHARM_BELT_LAYER, MobCharmBeltModel::createBodyLayer);
	}

	private static void setHandgunArmPoses(RenderLivingEvent.Pre<Player, PlayerModel<Player>> event, Player player, boolean handgunInOff, boolean handgunInMain) {
		PlayerModel<Player> model = event.getRenderer().getModel();

		if (isHandgunActive(player, handgunInMain, handgunInOff)) {
			InteractionHand hand = getActiveHandgunHand(player, handgunInMain, handgunInOff);
			HumanoidArm primaryHand = player.getMainArm();

			if (((hand == InteractionHand.MAIN_HAND && primaryHand == HumanoidArm.RIGHT) || (hand == InteractionHand.OFF_HAND && primaryHand == HumanoidArm.LEFT)) && model.rightArmPose != HumanoidModel.ArmPose.BOW_AND_ARROW) {
				model.rightArmPose = HumanoidModel.ArmPose.BOW_AND_ARROW;
			} else if (((hand == InteractionHand.OFF_HAND && primaryHand == HumanoidArm.RIGHT) || (hand == InteractionHand.MAIN_HAND && primaryHand == HumanoidArm.LEFT)) && model.leftArmPose != HumanoidModel.ArmPose.BOW_AND_ARROW) {
				model.leftArmPose = HumanoidModel.ArmPose.BOW_AND_ARROW;
			}
		} else {
			if (model.rightArmPose == HumanoidModel.ArmPose.BOW_AND_ARROW) {
				model.rightArmPose = HumanoidModel.ArmPose.ITEM;
			}
			if (model.leftArmPose == HumanoidModel.ArmPose.BOW_AND_ARROW) {
				model.leftArmPose = HumanoidModel.ArmPose.ITEM;
			}
		}
	}

	private static InteractionHand getActiveHandgunHand(Player player, boolean handgunInMain, boolean handgunInOff) {
		if (handgunInMain != handgunInOff) {
			return handgunInMain ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
		}

		boolean mainValid = isValidTimeFrame(player, player.getMainHandItem());
		boolean offValid = isValidTimeFrame(player, player.getOffhandItem());

		if (mainValid != offValid) {
			return mainValid ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
		}

		return ModItems.HANDGUN.get().getCooldown(player.getMainHandItem()) < ModItems.HANDGUN.get().getCooldown(player.getOffhandItem()) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
	}

	private static boolean isHandgunActive(Player player, boolean handgunInMain, boolean handgunInOff) {
		return handgunInMain && isValidTimeFrame(player, player.getMainHandItem()) || handgunInOff && isValidTimeFrame(player, player.getOffhandItem());

	}

	private static boolean isValidTimeFrame(Player player, ItemStack handgun) {
		long cooldownTime = ModItems.HANDGUN.get().getCooldown(handgun) + 5;
		Level level = player.level();

		return cooldownTime - level.getGameTime() <= ModItems.HANDGUN.get().getUseDuration(handgun, player) && cooldownTime >= level.getGameTime();
	}

	private static final List<Tuple<Component, HUDPosition>> hudComponents = Lists.newArrayList();

	private static void registerOverlay(RegisterGuiLayersEvent event) {
		event.registerAbove(VanillaGuiLayers.HOTBAR, Reliquary.getRL("reliquary_hud"), (guiGraphics, deltaTracker) -> {
			if (hudComponents.isEmpty()) {
				initHUDComponents();
			}
			renderHUDComponents(guiGraphics);
		});
	}

	private static void onMouseScrolled(InputEvent.MouseScrollingEvent evt) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.screen != null || !Screen.hasShiftDown()) {
			return;
		}
		LocalPlayer player = mc.player;
		if (player == null) {
			return;
		}
		ItemStack stack = player.getMainHandItem();
		double scrollDelta = evt.getScrollDeltaY();
		if (stack.getItem() instanceof IScrollableItem scrollableItem && scrollableItem.onMouseScrolled(stack, player, scrollDelta) == InteractionResult.PASS) {
			PacketDistributor.sendToServer(new ScrolledItemPayload(scrollDelta));
			evt.setCanceled(true);
		}
	}

	private static void renderHUDComponents(GuiGraphics guiGraphics) {
		for (Tuple<Component, HUDPosition> component : hudComponents) {
			HUDRenderrer.render(guiGraphics, component.getA(), component.getB());
		}
	}

	private static void initHUDComponents() {
		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.ALKAHESTRY_TOME.get(), Config.CLIENT.hudPositions.alkahestryTome.get(), new ItemStack(Items.REDSTONE), AlkahestryTomeItem::getCharge),
				Config.CLIENT.hudPositions.alkahestryTome.get()));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.DESTRUCTION_CATALYST.get(), Config.CLIENT.hudPositions.destructionCatalyst.get(), new ItemStack(Items.GUNPOWDER), DestructionCatalystItem::getGunpowder),
				Config.CLIENT.hudPositions.destructionCatalyst.get()));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.MIDAS_TOUCHSTONE.get(), Config.CLIENT.hudPositions.midasTouchstone.get(), new ItemStack(Items.GLOWSTONE_DUST), MidasTouchstoneItem::getGlowstoneCharge),
				Config.CLIENT.hudPositions.midasTouchstone.get()));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.INFERNAL_CHALICE.get(), Config.CLIENT.hudPositions.infernalChalice.get(), new ItemStack(Items.LAVA_BUCKET), InfernalChaliceItem::getFluidBucketAmount, Colors.get(Colors.RED)),
				Config.CLIENT.hudPositions.infernalChalice.get()));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.ICE_MAGUS_ROD.get(), Config.CLIENT.hudPositions.iceMagusRod.get(), new ItemStack(Items.SNOWBALL), IceMagusRodItem::getSnowballs),
				Config.CLIENT.hudPositions.iceMagusRod.get()));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.GLACIAL_STAFF.get(), Config.CLIENT.hudPositions.glacialStaff.get(), new ItemStack(Items.SNOWBALL), GlacialStaffItem::getSnowballs),
				Config.CLIENT.hudPositions.glacialStaff.get()));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.ENDER_STAFF.get(), Config.CLIENT.hudPositions.enderStaff.get(), is -> ModItems.ENDER_STAFF.get().getMode(is).getSerializedName(),
				Map.of(
						EnderStaffItem.Mode.CAST.getSerializedName(), new ChargePane(ModItems.ENDER_STAFF.get(), new ItemStack(Items.ENDER_PEARL), is -> ModItems.ENDER_STAFF.get().getPearlCount(is)),
						EnderStaffItem.Mode.NODE_WARP.getSerializedName(), new ChargePane(ModItems.ENDER_STAFF.get(), new ItemStack(ModBlocks.WRAITH_NODE.get()), is -> ModItems.ENDER_STAFF.get().getPearlCount(is)),
						EnderStaffItem.Mode.LONG_CAST.getSerializedName(), new ChargePane(ModItems.ENDER_STAFF.get(), new ItemStack(Items.ENDER_EYE), is -> ModItems.ENDER_STAFF.get().getPearlCount(is))
				)), Config.CLIENT.hudPositions.enderStaff.get()));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.PYROMANCER_STAFF.get(), Config.CLIENT.hudPositions.pyromancerStaff.get(), is -> ModItems.PYROMANCER_STAFF.get().getMode(is).getSerializedName(),
				Map.of(
						PyromancerStaffItem.Mode.BLAZE.getSerializedName(), new ChargePane(ModItems.PYROMANCER_STAFF.get(), new ItemStack(Items.BLAZE_POWDER), staff -> ModItems.PYROMANCER_STAFF.get().getBlazePowderCount(staff)),
						PyromancerStaffItem.Mode.FIRE_CHARGE.getSerializedName(), new ChargePane(ModItems.PYROMANCER_STAFF.get(), new ItemStack(Items.FIRE_CHARGE), staff -> ModItems.PYROMANCER_STAFF.get().getFireChargeCount(staff)),
						PyromancerStaffItem.Mode.ERUPTION.getSerializedName(), Box.createVertical(Box.Alignment.RIGHT, new TextPane("ERUPT"), new ChargePane(ModItems.PYROMANCER_STAFF.get(), new ItemStack(Items.BLAZE_POWDER), staff -> ModItems.PYROMANCER_STAFF.get().getBlazePowderCount(staff))),
						PyromancerStaffItem.Mode.FLINT_AND_STEEL.getSerializedName(), new ItemStackPane(Items.FLINT_AND_STEEL)
				)), Config.CLIENT.hudPositions.pyromancerStaff.get()));

		ChargePane rendingGaleFeatherPane = new ChargePane(ModItems.RENDING_GALE.get(), new ItemStack(Items.FEATHER), is -> {
			LocalPlayer player = Minecraft.getInstance().player;
			return player == null ? 0 : ModItems.RENDING_GALE.get().getFeatherCount(is);
		});
		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.RENDING_GALE.get(), Config.CLIENT.hudPositions.rendingGale.get(), is -> ModItems.RENDING_GALE.get().getMode(is).getSerializedName(),
				Map.of(
						RendingGaleItem.Mode.PUSH.getSerializedName(), Box.createVertical(Box.Alignment.RIGHT, new TextPane("PUSH"), rendingGaleFeatherPane),
						RendingGaleItem.Mode.PULL.getSerializedName(), Box.createVertical(Box.Alignment.RIGHT, new TextPane("PULL"), rendingGaleFeatherPane),
						RendingGaleItem.Mode.BOLT.getSerializedName(), Box.createVertical(Box.Alignment.RIGHT, new TextPane("BOLT"), rendingGaleFeatherPane),
						RendingGaleItem.Mode.FLIGHT.getSerializedName(), Box.createVertical(Box.Alignment.RIGHT, new TextPane("FLIGHT"), rendingGaleFeatherPane)
				)), Config.CLIENT.hudPositions.rendingGale.get()));

		Component contentsPane = new DynamicChargePane(ModItems.VOID_TEAR.get(),
				VoidTearItem::getTearContents, is -> VoidTearItem.getTearContents(is).getCount());
		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.VOID_TEAR.get(), Config.CLIENT.hudPositions.voidTear.get(), is -> ModItems.VOID_TEAR.get().getMode(is).getSerializedName(),
				Map.of(
						VoidTearItem.Mode.FULL_INVENTORY.getSerializedName(), Box.createVertical(Box.Alignment.RIGHT, new TextPane(Language.getInstance().getOrDefault(VOID_TEAR_MODE_TRANSLATION + VoidTearItem.Mode.FULL_INVENTORY.getSerializedName().toLowerCase())), contentsPane),
						VoidTearItem.Mode.NO_REFILL.getSerializedName(), Box.createVertical(Box.Alignment.RIGHT, new TextPane(Language.getInstance().getOrDefault(VOID_TEAR_MODE_TRANSLATION + VoidTearItem.Mode.NO_REFILL.getSerializedName().toLowerCase())), contentsPane),
						VoidTearItem.Mode.ONE_STACK.getSerializedName(), Box.createVertical(Box.Alignment.RIGHT, new TextPane(Language.getInstance().getOrDefault(VOID_TEAR_MODE_TRANSLATION + VoidTearItem.Mode.ONE_STACK.getSerializedName().toLowerCase())), contentsPane)
				)) {
			@Override
			public boolean shouldRender() {
				LocalPlayer player = Minecraft.getInstance().player;
				return player != null && !ModItems.VOID_TEAR.get().isEmpty(InventoryHelper.getCorrectItemFromEitherHand(player, ModItems.VOID_TEAR.get()));
			}
		}, Config.CLIENT.hudPositions.voidTear.get()));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.HARVEST_ROD.get(), Config.CLIENT.hudPositions.harvestRod.get(), is -> ModItems.HARVEST_ROD.get().getMode(is).getSerializedName(),
				Map.of(
						HarvestRodItem.Mode.BONE_MEAL.getSerializedName(), new ChargePane(ModItems.HARVEST_ROD.get(), new ItemStack(Items.BONE_MEAL), is -> ModItems.HARVEST_ROD.get().getBoneMealCount(is)),
						HarvestRodItem.Mode.HOE.getSerializedName(), new ItemStackPane(Items.WOODEN_HOE),
						ChargeableItemInfoPane.DYNAMIC_PANE, new DynamicChargePane(ModItems.HARVEST_ROD.get(), is -> ModItems.HARVEST_ROD.get().getCurrentPlantable(is), is -> ModItems.HARVEST_ROD.get().getPlantableQuantity(is, ModItems.HARVEST_ROD.get().getCurrentPlantableSlot(is)))
				)), Config.CLIENT.hudPositions.harvestRod.get()));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.SOJOURNER_STAFF.get(), Config.CLIENT.hudPositions.sojournerStaff.get(), is -> ChargeableItemInfoPane.DYNAMIC_PANE,
				Map.of(
						ChargeableItemInfoPane.DYNAMIC_PANE, new DynamicChargePane(ModItems.SOJOURNER_STAFF.get(), stack -> {
							ItemStack currentTorch = ModItems.SOJOURNER_STAFF.get().getCurrentTorch(stack);
							return currentTorch.isEmpty() ? new ItemStack(Items.TORCH) : currentTorch;
						}, ModItems.SOJOURNER_STAFF.get()::getTorchCount)
				)), Config.CLIENT.hudPositions.sojournerStaff.get()));

		hudComponents.add(new Tuple<>(new HeroMedallionPane(), Config.CLIENT.hudPositions.heroMedallion.get()));

		hudComponents.add(new Tuple<>(Box.createVertical(Box.Alignment.RIGHT, new HandgunPane(InteractionHand.OFF_HAND), new HandgunPane(InteractionHand.MAIN_HAND)), Config.CLIENT.hudPositions.handgun.get()));

		hudComponents.add(new Tuple<>(new CharmPane(), Config.CLIENT.hudPositions.mobCharm.get()));
	}

	private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(ModBlocks.APOTHECARY_MORTAR_TILE_TYPE.get(), context1 -> new ApothecaryMortarRenderer());
		event.registerBlockEntityRenderer(ModBlocks.PEDESTAL_TILE_TYPE.get(), context -> new PedestalRenderer());
		event.registerBlockEntityRenderer(ModBlocks.PASSIVE_PEDESTAL_TILE_TYPE.get(), context -> new PassivePedestalRenderer());

		event.registerEntityRenderer(ModEntities.LYSSA_HOOK.get(), FishingHookRenderer::new);
		event.registerEntityRenderer(ModEntities.BLAZE_SHOT.get(), ShotRenderer::new);
		event.registerEntityRenderer(ModEntities.BUSTER_SHOT.get(), ShotRenderer::new);
		event.registerEntityRenderer(ModEntities.CONCUSSIVE_SHOT.get(), ShotRenderer::new);
		event.registerEntityRenderer(ModEntities.ENDER_SHOT.get(), ShotRenderer::new);
		event.registerEntityRenderer(ModEntities.EXORCISM_SHOT.get(), ShotRenderer::new);
		event.registerEntityRenderer(ModEntities.NEUTRAL_SHOT.get(), ShotRenderer::new);
		event.registerEntityRenderer(ModEntities.SEEKER_SHOT.get(), ShotRenderer::new);
		event.registerEntityRenderer(ModEntities.SAND_SHOT.get(), ShotRenderer::new);
		event.registerEntityRenderer(ModEntities.STORM_SHOT.get(), ShotRenderer::new);
		event.registerEntityRenderer(ModEntities.TIPPED_ARROW.get(), TippedArrowRenderer::new);
		event.registerEntityRenderer(ModEntities.GLOWING_WATER.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(ModEntities.APHRODITE_POTION.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(ModEntities.FERTILE_POTION.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(ModEntities.HOLY_HAND_GRENADE.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(ModEntities.KRAKEN_SLIME.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(ModEntities.SPECIAL_SNOWBALL.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(ModEntities.ENDER_STAFF_PROJECTILE.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(ModEntities.THROWN_POTION.get(), ThrownItemRenderer::new);
	}

	private static void clientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(ClientEventHandler::registerLyssaRodItemProperties);
		event.enqueueWork(ClientEventHandler::registerInfernalTearItemProperties);
		event.enqueueWork(ClientEventHandler::registerVoidTearItemProperties);
		event.enqueueWork(ClientEventHandler::registerBulletAndMagazineItemProperties);
	}

	private static void registerBulletAndMagazineItemProperties() {
		registerPropertyToItems(Reliquary.getRL("potion"), (stack, level, livingEntity, seed) -> isPotionAttached(stack) ? 1 : 0,
				ModItems.BLAZE_BULLET.get(), ModItems.BUSTER_BULLET.get(), ModItems.CONCUSSIVE_BULLET.get(), ModItems.ENDER_BULLET.get(), ModItems.EXORCISM_BULLET.get(),
				ModItems.NEUTRAL_BULLET.get(), ModItems.SAND_BULLET.get(), ModItems.SEEKER_BULLET.get(), ModItems.STORM_BULLET.get(),
				ModItems.BLAZE_MAGAZINE.get(), ModItems.BUSTER_MAGAZINE.get(), ModItems.CONCUSSIVE_MAGAZINE.get(), ModItems.ENDER_MAGAZINE.get(), ModItems.EXORCISM_MAGAZINE.get(),
				ModItems.NEUTRAL_MAGAZINE.get(), ModItems.SAND_MAGAZINE.get(), ModItems.SEEKER_MAGAZINE.get(), ModItems.STORM_MAGAZINE.get());
	}

	private static void registerVoidTearItemProperties() {
		ItemProperties.register(ModItems.VOID_TEAR.get(), ResourceLocation.parse("empty"),
				(stack, level, entity, seed) -> ModItems.VOID_TEAR.get().isEmpty(stack) ? 1.0F : 0.0F);
	}

	private static void registerInfernalTearItemProperties() {
		ItemProperties.register(ModItems.INFERNAL_TEAR.get(), ResourceLocation.parse("empty"),
				(stack, level, entity, seed) -> InfernalTearItem.getStackFromTear(stack).isEmpty() ? 1.0F : 0.0F);
	}

	private static void registerLyssaRodItemProperties() {
		ItemProperties.register(ModItems.ROD_OF_LYSSA.get(), ResourceLocation.parse("cast"), (stack, level, entity, seed) -> {
			if (entity == null) {
				return 0.0F;
			} else {
				if (level == null) {
					return 0.0F;
				}
				int entityId = RodOfLyssaItem.getHookEntityId(stack);
				return (entity.getMainHandItem() == stack || entity.getOffhandItem() == stack) && entityId > 0 && level.getEntity(entityId) != null ? 1.0F : 0.0F;
			}
		});
	}

	private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(FORTUNE_COIN_TOGGLE_KEYBIND);
	}

	private static void registerPropertyToItems(ResourceLocation registryName, @SuppressWarnings("deprecation") ItemPropertyFunction propertyGetter, Item... items) {
		for (Item item : items) {
			ItemProperties.register(item, registryName, propertyGetter);
		}
	}

	private static boolean isPotionAttached(ItemStack stack) {
		return PotionHelper.hasPotionContents(stack);
	}

	private static void loadComplete(FMLLoadCompleteEvent event) {
		event.enqueueWork(() -> {
			PedestalClientRegistry.registerItemRenderer(FishingRodItem.class, PedestalFishHookRenderer::new);
			PedestalClientRegistry.registerItemRenderer(RodOfLyssaItem.class, PedestalFishHookRenderer::new);
			NeoForge.EVENT_BUS.addListener(FortuneCoinToggler::handleKeyInputEvent);
		});
	}

	private static void registerBackpackClientExtension(RegisterClientExtensionsEvent event) {
		event.registerItem(new IClientItemExtensions() {
			private WitchHatModel hatModel = null;

			@Override
			public @Nonnull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
				if (hatModel == null) {
					EntityModelSet entityModels = Minecraft.getInstance().getEntityModels();
					hatModel = new WitchHatModel(entityModels.bakeLayer(ClientEventHandler.WITCH_HAT_LAYER));
				}
				return hatModel;
			}
		}, ModItems.WITCH_HAT.get());

		event.registerFluidType(new IClientFluidTypeExtensions() {
			private static final ResourceLocation XP_STILL_TEXTURE = ResourceLocation.fromNamespaceAndPath(Reliquary.MOD_ID, "block/xp_still");
			private static final ResourceLocation XP_FLOWING_TEXTURE = ResourceLocation.fromNamespaceAndPath(Reliquary.MOD_ID, "block/xp_flowing");

			@Override
			public ResourceLocation getStillTexture() {
				return XP_STILL_TEXTURE;
			}

			@Override
			public ResourceLocation getFlowingTexture() {
				return XP_FLOWING_TEXTURE;
			}
		}, ModFluids.EXPERIENCE_FLUID_TYPE.get());
	}
}
