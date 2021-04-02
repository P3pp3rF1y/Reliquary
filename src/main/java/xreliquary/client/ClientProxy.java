package xreliquary.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import xreliquary.client.init.ModBlockColors;
import xreliquary.client.init.ModItemColors;
import xreliquary.client.init.ModParticles;
import xreliquary.client.registry.PedestalClientRegistry;
import xreliquary.client.render.ApothecaryMortarRenderer;
import xreliquary.client.render.LyssaHookRenderer;
import xreliquary.client.render.PassivePedestalRenderer;
import xreliquary.client.render.PedestalFishHookRenderer;
import xreliquary.client.render.PedestalRenderer;
import xreliquary.client.render.ShotRenderer;
import xreliquary.client.render.XRTippedArrowRenderer;
import xreliquary.common.CommonProxy;
import xreliquary.init.ModBlocks;
import xreliquary.init.ModEntities;
import xreliquary.init.ModItems;
import xreliquary.items.FortuneCoinToggler;
import xreliquary.items.InfernalTearItem;
import xreliquary.items.RodOfLyssaItem;
import xreliquary.items.VoidTearItem;
import xreliquary.reference.Reference;
import xreliquary.util.potions.XRPotionHelper;

@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {
	public static final KeyBinding FORTUNE_COIN_TOGGLE_KEYBIND = new KeyBinding("keybind.xreliquary.fortune_coin", InputMappings.INPUT_INVALID.getKeyCode(), "keybind.xreliquary.category");

	@Override
	public void registerHandlers() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addListener(this::clientSetup);
		modBus.addListener(this::loadComplete);
		modBus.addListener(ModParticles.FactoryHandler::registerFactories);
		modBus.addListener(this::registerEntityRenderers);
		modBus.addListener(this::registerTileRenderers);
	}

	private void registerEntityRenderers(FMLClientSetupEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.LYSSA_HOOK, LyssaHookRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.BLAZE_SHOT, ShotRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.BUSTER_SHOT, ShotRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.CONCUSSIVE_SHOT, ShotRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.ENDER_SHOT, ShotRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.EXORCISM_SHOT, ShotRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.NEUTRAL_SHOT, ShotRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.SEEKER_SHOT, ShotRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.SAND_SHOT, ShotRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.STORM_SHOT, ShotRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.TIPPED_ARROW, XRTippedArrowRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.GLOWING_WATER, renderManager -> new SpriteRenderer<>(renderManager, Minecraft.getInstance().getItemRenderer()));
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.APHRODITE_POTION, renderManager -> new SpriteRenderer<>(renderManager, Minecraft.getInstance().getItemRenderer()));
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.FERTILE_POTION, renderManager -> new SpriteRenderer<>(renderManager, Minecraft.getInstance().getItemRenderer()));
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.HOLY_HAND_GRENADE, renderManager -> new SpriteRenderer<>(renderManager, Minecraft.getInstance().getItemRenderer()));
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.KRAKEN_SLIME, renderManager -> new SpriteRenderer<>(renderManager, Minecraft.getInstance().getItemRenderer()));
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.SPECIAL_SNOWBALL, renderManager -> new SpriteRenderer<>(renderManager, Minecraft.getInstance().getItemRenderer()));
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.ENDER_STAFF_PROJECTILE, renderManager -> new SpriteRenderer<>(renderManager, Minecraft.getInstance().getItemRenderer()));
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.THROWN_POTION, renderManager -> new SpriteRenderer<>(renderManager, Minecraft.getInstance().getItemRenderer()));
	}

	private void clientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> ClientRegistry.registerKeyBinding(FORTUNE_COIN_TOGGLE_KEYBIND));
		event.enqueueWork(() -> ItemModelsProperties.registerProperty(ModItems.ROD_OF_LYSSA.get(), new ResourceLocation("cast"), (stack, world, entity) -> {
			if (entity == null) {
				return 0.0F;
			} else {
				if (world == null) {
					return 0.0F;
				}
				int entityId = RodOfLyssaItem.getHookEntityId(stack);
				return (entity.getHeldItemMainhand() == stack || entity.getHeldItemOffhand() == stack) && entityId > 0 && world.getEntityByID(entityId) != null ? 1.0F : 0.0F;
			}
		}));
		event.enqueueWork(() -> ItemModelsProperties.registerProperty(ModItems.INFERNAL_TEAR.get(), new ResourceLocation("empty"),
				(stack, world, entity) -> InfernalTearItem.getStackFromTear(stack).isEmpty() ? 1.0F : 0.0F));
		event.enqueueWork(() -> ItemModelsProperties.registerProperty(ModItems.VOID_TEAR.get(), new ResourceLocation("empty"),
				(stack, world, entity) -> VoidTearItem.isEmpty(stack, true) ? 1.0F : 0.0F));
		event.enqueueWork(() -> registerPropertyToItems(new ResourceLocation(Reference.MOD_ID, "potion"), (stack, world, livingEntity) -> isPotionAttached(stack) ? 1 : 0,
				ModItems.BLAZE_BULLET.get(), ModItems.BUSTER_BULLET.get(), ModItems.CONCUSSIVE_BULLET.get(), ModItems.ENDER_BULLET.get(), ModItems.EXORCISM_BULLET.get(),
				ModItems.NEUTRAL_BULLET.get(), ModItems.SAND_BULLET.get(), ModItems.SEEKER_BULLET.get(), ModItems.STORM_BULLET.get(),
				ModItems.BLAZE_MAGAZINE.get(), ModItems.BUSTER_MAGAZINE.get(), ModItems.CONCUSSIVE_MAGAZINE.get(), ModItems.ENDER_MAGAZINE.get(), ModItems.EXORCISM_MAGAZINE.get(),
				ModItems.NEUTRAL_MAGAZINE.get(), ModItems.SAND_MAGAZINE.get(), ModItems.SEEKER_MAGAZINE.get(), ModItems.STORM_MAGAZINE.get()));

		RenderTypeLookup.setRenderLayer(ModBlocks.FERTILE_LILY_PAD.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.INTERDICTION_TORCH.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.WALL_INTERDICTION_TORCH.get(), RenderType.getCutout());
	}

	public void registerPropertyToItems(ResourceLocation registryName, IItemPropertyGetter propertyGetter, Item... items) {
		for (Item item : items) {
			ItemModelsProperties.registerProperty(item, registryName, propertyGetter);
		}
	}

	private boolean isPotionAttached(ItemStack stack) {
		return !XRPotionHelper.getPotionEffectsFromStack(stack).isEmpty();
	}

	private void loadComplete(FMLLoadCompleteEvent event) {
		event.enqueueWork(() -> {
			ModItemColors.init();
			ModBlockColors.init();
			PedestalClientRegistry.registerItemRenderer(FishingRodItem.class, PedestalFishHookRenderer.class);
			MinecraftForge.EVENT_BUS.addListener(FortuneCoinToggler::handleKeyInputEvent);
		});
	}

	private void registerTileRenderers(FMLClientSetupEvent event) {
		ClientRegistry.bindTileEntityRenderer(ModBlocks.APOTHECARY_MORTAR_TILE_TYPE.get(), ApothecaryMortarRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModBlocks.PEDESTAL_TILE_TYPE.get(), PedestalRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModBlocks.PASSIVE_PEDESTAL_TILE_TYPE.get(), PassivePedestalRenderer::new);
	}
}
