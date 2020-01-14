package xreliquary.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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
import xreliquary.client.render.ThrownKrakenSlimeRenderer;
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
import xreliquary.init.ModItems;
import xreliquary.reference.Compatibility;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;
import xreliquary.util.NBTHelper;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.PotionMap;
import xreliquary.util.potions.XRPotionHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy extends CommonProxy {
	public static final KeyBinding FORTUNE_COIN_TOGGLE_KEYBIND = new KeyBinding("xreliquary.keybind.fortune_coin", InputMappings.INPUT_INVALID.getKeyCode(), "xreliquary.keybind.category");

	@Override
	public void registerHandlers() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
	}

	@Override
	public void registerJEI(Block block, String name, boolean oneDescription) {
		if (ModList.get().isLoaded(Compatibility.MOD_ID.JEI)) {
			JEIDescriptionRegistry.register(Item.getItemFromBlock(block), name);
		}
	}

	private void initSpecialJEIDescriptions() {
		if (!ModList.get().isLoaded(Compatibility.MOD_ID.JEI)) {
			return;
		}

		if (Boolean.FALSE.equals(Settings.COMMON.disable.disablePotions.get())) {
			NonNullList<ItemStack> subItems = NonNullList.create();

			//noinspection ConstantConditions
			ModItems.POTION_ESSENCE.fillItemGroup(ModItems.POTION_ESSENCE.getGroup(), subItems);
			JEIDescriptionRegistry.register(subItems, Names.Items.POTION_ESSENCE);

			JEIDescriptionRegistry.register(ModItems.POTION, "potion0");

			List<ItemStack> potions = new ArrayList<>();
			List<ItemStack> splashPotions = new ArrayList<>();
			List<ItemStack> lingeringPotions = new ArrayList<>();
			List<ItemStack> tippedArrows = new ArrayList<>();
			List<ItemStack> potionShots = new ArrayList<>();
			List<ItemStack> potionMagazines = new ArrayList<>();

			for (PotionEssence essence : PotionMap.uniquePotions) {
				ItemStack potion = new ItemStack(ModItems.POTION, 1);
				XRPotionHelper.addPotionEffectsToStack(potion, essence.getEffects());
				NBTHelper.putBoolean("hasPotion", potion, true);
				potions.add(potion);

				ItemStack splashPotion = potion.copy();
				NBTHelper.putBoolean("splash", splashPotion, true);
				splashPotions.add(splashPotion);

				ItemStack lingeringPotion = potion.copy();
				NBTHelper.putBoolean("lingering", lingeringPotion, true);
				lingeringPotions.add(lingeringPotion);

				ItemStack tippedArrow = new ItemStack(ModItems.TIPPED_ARROW);
				XRPotionHelper.addPotionEffectsToStack(tippedArrow, XRPotionHelper.changePotionEffectsDuration(essence.getEffects(), 0.125F));
				tippedArrows.add(tippedArrow);

				ItemStack potionShot = new ItemStack(ModItems.NEUTRAL_BULLET);
				XRPotionHelper.addPotionEffectsToStack(potionShot, XRPotionHelper.changePotionEffectsDuration(essence.getEffects(), 0.2F));
				potionShots.add(potionShot);

				ItemStack potionMagazine = new ItemStack(ModItems.NEUTRAL_MAGAZINE);
				XRPotionHelper.addPotionEffectsToStack(potionMagazine, XRPotionHelper.changePotionEffectsDuration(essence.getEffects(), 0.2F));
				potionMagazines.add(potionMagazine);
			}
			JEIDescriptionRegistry.register(potions, Names.Items.POTION);
			JEIDescriptionRegistry.register(splashPotions, Names.Items.POTION_SPLASH);
			JEIDescriptionRegistry.register(lingeringPotions, Names.Items.POTION_LINGERING);
			JEIDescriptionRegistry.register(tippedArrows, Names.Items.TIPPED_ARROW);
			JEIDescriptionRegistry.register(potionShots, "bullet1_potion");
			JEIDescriptionRegistry.register(potionMagazines, "magazine1_potion");
		}

		if (ModItems.isEnabled(ModItems.MOB_CHARM)) {
			NonNullList<ItemStack> mobCharms = NonNullList.create();
			//noinspection ConstantConditions - enabled check above
			ModItems.MOB_CHARM.fillItemGroup(ModItems.MOB_CHARM.getGroup(), mobCharms);

			int meta = 0;
			for (ItemStack mobCharm : mobCharms) {
				JEIDescriptionRegistry.register(Collections.singletonList(mobCharm), Names.Items.MOB_CHARM + meta);
				meta++;
			}
		}
	}

	@Override
	public void registerJEI(Item item, String name) {
		if (ModList.get().isLoaded(Compatibility.MOD_ID.JEI)) {
			JEIDescriptionRegistry.register(item, name);
		}
	}

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(LyssaBobberEntity.class, LyssaHookRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(BlazeShotEntity.class, ShotRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(BusterShotEntity.class, ShotRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ConcussiveShotEntity.class, ShotRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(EnderShotEntity.class, ShotRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ExorcismShotEntity.class, ShotRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(NeutralShotEntity.class, ShotRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(SeekerShotEntity.class, ShotRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(SandShotEntity.class, ShotRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(StormShotEntity.class, ShotRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(XRTippedArrowEntity.class, XRTippedArrowRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(GlowingWaterEntity.class, renderManager -> new SpriteRenderer<>(renderManager, Minecraft.getInstance().getItemRenderer()));
		RenderingRegistry.registerEntityRenderingHandler(AttractionPotionEntity.class, renderManager -> new SpriteRenderer<>(renderManager, Minecraft.getInstance().getItemRenderer()));
		RenderingRegistry.registerEntityRenderingHandler(FertilePotionEntity.class, renderManager -> new SpriteRenderer<>(renderManager, Minecraft.getInstance().getItemRenderer()));
		RenderingRegistry.registerEntityRenderingHandler(HolyHandGrenadeEntity.class, renderManager -> new SpriteRenderer<>(renderManager, Minecraft.getInstance().getItemRenderer()));
		RenderingRegistry.registerEntityRenderingHandler(KrakenSlimeEntity.class, ThrownKrakenSlimeRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(SpecialSnowballEntity.class, renderManager -> new SpriteRenderer<>(renderManager, Minecraft.getInstance().getItemRenderer()));
		RenderingRegistry.registerEntityRenderingHandler(EnderStaffProjectileEntity.class, renderManager -> new SpriteRenderer<>(renderManager, Minecraft.getInstance().getItemRenderer()));
		RenderingRegistry.registerEntityRenderingHandler(ThrownXRPotionEntity.class, renderManager -> new SpriteRenderer<>(renderManager, Minecraft.getInstance().getItemRenderer()));

		registerTileRenderers();
		registerBeltRender();
	}

	private void clientSetup(FMLClientSetupEvent event) {
		//TODO replace with new interface from event when that is implemented in forge
		DeferredWorkQueue.runLater(() -> {
			ClientRegistry.registerKeyBinding(FORTUNE_COIN_TOGGLE_KEYBIND);
			initSpecialJEIDescriptions();
		});
	}

	private void loadComplete(FMLLoadCompleteEvent event) {
		DeferredWorkQueue.runLater(() -> {
			ModItemColors.init();
			ModBlockColors.init();
			PedestalClientRegistry.registerItemRenderer(FishingRodItem.class, PedestalFishHookRenderer.class);
		});
	}

	private static void registerTileRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(ApothecaryMortarTileEntity.class, new ApothecaryMortarRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(PedestalTileEntity.class, new PedestalRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(PassivePedestalTileEntity.class, new PassivePedestalRenderer());
	}

	private static void registerBeltRender() {
/* TODO add baubles replacement compatibility
		Map<String, PlayerRenderer> skinMap = Minecraft.getInstance().getRenderManager().getSkinMap();
		PlayerRenderer render;
		render = skinMap.get("default");
		render.addLayer(new MobCharmBeltLayerRenderer());
*/
	}
}
