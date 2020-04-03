package xreliquary.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import xreliquary.blocks.PassivePedestalBlock;
import xreliquary.blocks.PedestalBlock;
import xreliquary.blocks.tile.ApothecaryMortarTileEntity;
import xreliquary.blocks.tile.PassivePedestalTileEntity;
import xreliquary.blocks.tile.PedestalTileEntity;
import xreliquary.client.init.ModBlockColors;
import xreliquary.client.init.ModItemColors;
import xreliquary.client.registry.PedestalClientRegistry;
import xreliquary.client.render.ApothecaryMortarRenderer;
import xreliquary.client.render.LyssaHookRenderer;
import xreliquary.client.render.PassivePedestalRenderer;
import xreliquary.client.render.PedestalFishHookRenderer;
import xreliquary.client.render.PedestalRenderer;
import xreliquary.client.render.ShotRenderer;
import xreliquary.client.render.XRTippedArrowRenderer;
import xreliquary.common.CommonProxy;
import xreliquary.compat.jei.descriptions.JEIDescriptionRegistry;
import xreliquary.entities.EnderStaffProjectileEntity;
import xreliquary.entities.GlowingWaterEntity;
import xreliquary.entities.HolyHandGrenadeEntity;
import xreliquary.entities.KrakenSlimeEntity;
import xreliquary.entities.LyssaBobberEntity;
import xreliquary.entities.SpecialSnowballEntity;
import xreliquary.entities.XRTippedArrowEntity;
import xreliquary.entities.potion.AttractionPotionEntity;
import xreliquary.entities.potion.FertilePotionEntity;
import xreliquary.entities.potion.ThrownXRPotionEntity;
import xreliquary.entities.shot.BlazeShotEntity;
import xreliquary.entities.shot.BusterShotEntity;
import xreliquary.entities.shot.ConcussiveShotEntity;
import xreliquary.entities.shot.EnderShotEntity;
import xreliquary.entities.shot.ExorcismShotEntity;
import xreliquary.entities.shot.NeutralShotEntity;
import xreliquary.entities.shot.SandShotEntity;
import xreliquary.entities.shot.SeekerShotEntity;
import xreliquary.entities.shot.StormShotEntity;
import xreliquary.init.ModBlocks;
import xreliquary.init.ModEntities;
import xreliquary.items.FortuneCoinToggler;
import xreliquary.reference.Compatibility;
import xreliquary.reference.Reference;

import java.util.List;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy extends CommonProxy {
	public static final KeyBinding FORTUNE_COIN_TOGGLE_KEYBIND = new KeyBinding("keybind.xreliquary.fortune_coin", InputMappings.INPUT_INVALID.getKeyCode(), "keybind.xreliquary.category");

	@Override
	public void registerHandlers() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
	}

	@Override
	public void registerJEI(Block block, String name) {
		if (ModList.get().isLoaded(Compatibility.MOD_ID.JEI)) {
			JEIDescriptionRegistry.register(Item.getItemFromBlock(block), name);
		}
	}

	@Override
	public void registerJEI(Supplier<List<ItemStack>> items, String... names) {
		if (ModList.get().isLoaded(Compatibility.MOD_ID.JEI)) {
			JEIDescriptionRegistry.register(items, names);
		}
	}

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
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

		registerTileRenderers();
	}

	private void clientSetup(FMLClientSetupEvent event) {
		//TODO replace with new interface from event when that is implemented in forge
		DeferredWorkQueue.runLater(() -> ClientRegistry.registerKeyBinding(FORTUNE_COIN_TOGGLE_KEYBIND));

		RenderTypeLookup.setRenderLayer(ModBlocks.FERTILE_LILY_PAD, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.INTERDICTION_TORCH, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.WALL_INTERDICTION_TORCH, RenderType.getCutout());
	}

	private void loadComplete(FMLLoadCompleteEvent event) {
		DeferredWorkQueue.runLater(() -> {
			ModItemColors.init();
			ModBlockColors.init();
			PedestalClientRegistry.registerItemRenderer(FishingRodItem.class, PedestalFishHookRenderer.class);
			MinecraftForge.EVENT_BUS.addListener(FortuneCoinToggler::handleKeyInputEvent);
		});
	}

	private static void registerTileRenderers() {
		ClientRegistry.bindTileEntityRenderer(ApothecaryMortarTileEntity.TYPE, ApothecaryMortarRenderer::new);
		ClientRegistry.bindTileEntityRenderer(PedestalTileEntity.TYPE, PedestalRenderer::new);
		ClientRegistry.bindTileEntityRenderer(PassivePedestalTileEntity.TYPE, PassivePedestalRenderer::new);
	}
}
