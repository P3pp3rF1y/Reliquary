package reliquary.handler;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.locale.Language;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;
import reliquary.Reliquary;
import reliquary.client.color.item.CharmTintSources;
import reliquary.client.gui.components.Box;
import reliquary.client.gui.components.Component;
import reliquary.client.gui.components.ItemStackPane;
import reliquary.client.gui.components.TextPane;
import reliquary.client.gui.hud.*;
import reliquary.client.init.ModBlockColors;
import reliquary.client.init.ModParticles;
import reliquary.client.model.MobCharmBeltModel;
import reliquary.client.model.VoidTearItemModel;
import reliquary.client.model.WitchHatModel;
import reliquary.client.registry.PedestalClientRegistry;
import reliquary.client.render.*;
import reliquary.init.ModBlocks;
import reliquary.init.ModEntities;
import reliquary.init.ModFluids;
import reliquary.init.ModItems;
import reliquary.item.*;
import reliquary.item.properties.conditional.InfernalTearEmpty;
import reliquary.item.properties.conditional.LyssaRodCast;
import reliquary.item.util.IScrollableItem;
import reliquary.network.ScrolledItemPayload;
import reliquary.reference.ClientReference;
import reliquary.reference.Colors;
import reliquary.reference.Config;
import reliquary.util.InventoryHelper;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class ClientEventHandler {
	private ClientEventHandler() {
	}

	public static final  BiConsumer<Avatar, AvatarRenderState> RENDER_STATE_MODIFIER = (avatar, avatarRenderState) -> {
		HumanoidArm primaryHand = avatar.getMainArm();
		if (isActiveHandgun(avatar, avatar.getMainHandItem())) {
			if (primaryHand == HumanoidArm.RIGHT && avatarRenderState.rightArmPose != HumanoidModel.ArmPose.BOW_AND_ARROW) {
				avatarRenderState.rightArmPose = HumanoidModel.ArmPose.BOW_AND_ARROW;
			} else if (primaryHand == HumanoidArm.LEFT && avatarRenderState.leftArmPose != HumanoidModel.ArmPose.BOW_AND_ARROW) {
				avatarRenderState.leftArmPose = HumanoidModel.ArmPose.BOW_AND_ARROW;
			}
		} else if (isActiveHandgun(avatar, avatar.getOffhandItem())) {
			if (primaryHand == HumanoidArm.RIGHT && avatarRenderState.leftArmPose != HumanoidModel.ArmPose.BOW_AND_ARROW) {
				avatarRenderState.leftArmPose = HumanoidModel.ArmPose.BOW_AND_ARROW;
			} else if (primaryHand == HumanoidArm.LEFT && avatarRenderState.rightArmPose != HumanoidModel.ArmPose.BOW_AND_ARROW) {
				avatarRenderState.rightArmPose = HumanoidModel.ArmPose.BOW_AND_ARROW;
			}
		}
	};

	private static final int KEY_UNKNOWN = -1;
	public static final KeyMapping.Category KEY_MAPPING_CATEGORY = new KeyMapping.Category(Reliquary.getIdentifier("main"));
	public static final KeyMapping FORTUNE_COIN_TOGGLE_KEYBIND = new KeyMapping("key.reliquary.fortune_coin", KeyConflictContext.UNIVERSAL, InputConstants.Type.KEYSYM.getOrCreate(KEY_UNKNOWN), KEY_MAPPING_CATEGORY);
	private static final String VOID_TEAR_MODE_TRANSLATION = "item." + Reliquary.MOD_ID + ".void_tear.mode.";
	public static final ModelLayerLocation WITCH_HAT_LAYER = new ModelLayerLocation(Reliquary.getIdentifier("witch_hat"), "main");
	public static final ModelLayerLocation MOB_CHARM_BELT_LAYER = new ModelLayerLocation(Reliquary.getIdentifier("mob_charm_belt"), "main");

	public static void registerHandlers() {
		IEventBus modBus = ModLoadingContext.get().getActiveContainer().getEventBus();
		if (modBus == null) {
			return;
		}

		modBus.addListener(ClientEventHandler::registerKeyMappings);
		modBus.addListener(ClientEventHandler::loadComplete);
		modBus.addListener(ModParticles.ProviderHandler::registerProviders);
		modBus.addListener(ClientEventHandler::registerEntityRenderers);
		modBus.addListener(ClientEventHandler::registerLayer);
		modBus.addListener(ModBlockColors::registerBlockColors);
		modBus.addListener(ClientEventHandler::registerOverlay);
		modBus.addListener(ClientEventHandler::registerWitchHatClientExtension);
		modBus.addListener(ClientEventHandler::registerVoidTearItemModel);
		modBus.addListener(ClientEventHandler::registerConditionalItemModelProperties);
		modBus.addListener(ClientEventHandler::registerTintSources);
		modBus.addListener(ClientEventHandler::registerMovingStorageRenderStateModifiers);

		IEventBus eventBus = NeoForge.EVENT_BUS;
		eventBus.addListener(ClientEventHandler::onMouseScrolled);

		//container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new); TODO add but requires adding a ton of translations and translation keys (so that they follow config setting levels)
	}

	private static void registerTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
		event.register(Reliquary.getIdentifier("charm_main_tint"), CharmTintSources.Main.MAP_CODEC);
		event.register(Reliquary.getIdentifier("charm_accent_tint"), CharmTintSources.Accent.MAP_CODEC);
	}

	private static void registerVoidTearItemModel(RegisterItemModelsEvent event) {
		event.register(Reliquary.getIdentifier("void_tear"), VoidTearItemModel.Unbaked.MAP_CODEC);
	}

	private static void registerMovingStorageRenderStateModifiers(RegisterRenderStateModifiersEvent event) {
		//noinspection unchecked,RedundantCast - actually necessary to prevent compiler error
		event.registerEntityModifier((Class<? extends EntityRenderer<? extends Avatar, ? extends AvatarRenderState>>) (Class<?>) AvatarRenderer.class, RENDER_STATE_MODIFIER);
	}

	private static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(WITCH_HAT_LAYER, WitchHatModel::createBodyLayer);
		event.registerLayerDefinition(MOB_CHARM_BELT_LAYER, MobCharmBeltModel::createBodyLayer);
	}

	private static boolean isActiveHandgun(Avatar avatar, ItemStack stack) {
		if (stack.getItem() != ModItems.HANDGUN.get()) {
			return false;
		}

		long cooldownTime = ModItems.HANDGUN.get().getCooldown(stack) + 5;
		Level level = avatar.level();

		return cooldownTime - level.getGameTime() <= ModItems.HANDGUN.get().getUseDuration(stack, avatar) && cooldownTime >= level.getGameTime();
	}

	private static final List<Tuple<Component, HUDPosition>> hudComponents = Lists.newArrayList();

	private static void registerOverlay(RegisterGuiLayersEvent event) {
		event.registerAbove(VanillaGuiLayers.HOTBAR, Reliquary.getIdentifier("reliquary_hud"), (guiGraphics, deltaTracker) -> {
			if (hudComponents.isEmpty()) {
				initHUDComponents();
			}
			renderHUDComponents(guiGraphics);
		});
	}

	private static void onMouseScrolled(InputEvent.MouseScrollingEvent evt) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.screen != null || !mc.hasShiftDown()) {
			return;
		}
		LocalPlayer player = mc.player;
		if (player == null) {
			return;
		}
		ItemStack stack = player.getMainHandItem();
		double scrollDelta = evt.getScrollDeltaY();
		if (stack.getItem() instanceof IScrollableItem scrollableItem && scrollableItem.onMouseScrolled(stack, player, scrollDelta) == InteractionResult.PASS) {
			ClientPacketDistributor.sendToServer(new ScrolledItemPayload(scrollDelta));
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
		event.registerBlockEntityRenderer(ModBlocks.APOTHECARY_MORTAR_TILE_TYPE.get(), ApothecaryMortarRenderer::new);
		event.registerBlockEntityRenderer(ModBlocks.PEDESTAL_TILE_TYPE.get(), PedestalRenderer::new);
		event.registerBlockEntityRenderer(ModBlocks.PASSIVE_PEDESTAL_TILE_TYPE.get(), PassivePedestalRenderer::new);

		event.registerEntityRenderer(ModEntities.LYSSA_HOOK.get(), FishingHookRenderer::new);
		event.registerEntityRenderer(ModEntities.BLAZE_SHOT.get(), context -> new ShotRenderer<>(context, ClientReference.BLAZE));
		event.registerEntityRenderer(ModEntities.BUSTER_SHOT.get(), context -> new ShotRenderer<>(context, ClientReference.BUSTER));
		event.registerEntityRenderer(ModEntities.CONCUSSIVE_SHOT.get(), context -> new ShotRenderer<>(context, ClientReference.CONCUSSIVE));
		event.registerEntityRenderer(ModEntities.ENDER_SHOT.get(), context -> new ShotRenderer<>(context, ClientReference.ENDER));
		event.registerEntityRenderer(ModEntities.EXORCISM_SHOT.get(), context -> new ShotRenderer<>(context, ClientReference.EXORCISM));
		event.registerEntityRenderer(ModEntities.NEUTRAL_SHOT.get(), context -> new ShotRenderer<>(context, ClientReference.NEUTRAL));
		event.registerEntityRenderer(ModEntities.SEEKER_SHOT.get(), context -> new ShotRenderer<>(context, ClientReference.SEEKER));
		event.registerEntityRenderer(ModEntities.SAND_SHOT.get(), context -> new ShotRenderer<>(context, ClientReference.SAND));
		event.registerEntityRenderer(ModEntities.STORM_SHOT.get(), context -> new ShotRenderer<>(context, ClientReference.STORM));
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

	private static void registerConditionalItemModelProperties(RegisterConditionalItemModelPropertyEvent event) {
		event.register(Reliquary.getIdentifier("lyssa_rod_cast"), LyssaRodCast.MAP_CODEC);
		event.register(Reliquary.getIdentifier("infernal_tear_empty"), InfernalTearEmpty.MAP_CODEC);
	}

	private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.registerCategory(KEY_MAPPING_CATEGORY);
		event.register(FORTUNE_COIN_TOGGLE_KEYBIND);
	}

	private static void loadComplete(FMLLoadCompleteEvent event) {
		event.enqueueWork(() -> {
			PedestalClientRegistry.registerItemRenderer(FishingRodItem.class, PedestalFishHookRenderer::new);
			PedestalClientRegistry.registerItemRenderer(RodOfLyssaItem.class, PedestalFishHookRenderer::new);
			NeoForge.EVENT_BUS.addListener(FortuneCoinToggler::handleKeyInputEvent);
		});
	}

	private static void registerWitchHatClientExtension(RegisterClientExtensionsEvent event) {
		event.registerItem(new IClientItemExtensions() {
			private WitchHatModel hatModel = null;

			@Override
			public Model getHumanoidArmorModel(ItemStack itemStack, EquipmentClientInfo.LayerType layerType, Model original) {
				if (hatModel == null) {
					EntityModelSet entityModels = Minecraft.getInstance().getEntityModels();
					hatModel = new WitchHatModel(entityModels.bakeLayer(ClientEventHandler.WITCH_HAT_LAYER));
				}
				return hatModel;
			}
		}, ModItems.WITCH_HAT.get());

		event.registerFluidType(new IClientFluidTypeExtensions() {
			private static final Identifier XP_STILL_TEXTURE = Identifier.fromNamespaceAndPath(Reliquary.MOD_ID, "block/xp_still");
			private static final Identifier XP_FLOWING_TEXTURE = Identifier.fromNamespaceAndPath(Reliquary.MOD_ID, "block/xp_flowing");

			@Override
			public Identifier getStillTexture() {
				return XP_STILL_TEXTURE;
			}

			@Override
			public Identifier getFlowingTexture() {
				return XP_FLOWING_TEXTURE;
			}
		}, ModFluids.EXPERIENCE_FLUID_TYPE.get());
	}
}
