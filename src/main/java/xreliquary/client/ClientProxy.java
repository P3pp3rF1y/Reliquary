package xreliquary.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import xreliquary.Reliquary;
import xreliquary.blocks.tile.TileEntityMortar;
import xreliquary.blocks.tile.TileEntityPedestal;
import xreliquary.blocks.tile.TileEntityPedestalPassive;
import xreliquary.client.init.ItemBlockModels;
import xreliquary.client.init.ItemModels;
import xreliquary.client.init.ModBlockColors;
import xreliquary.client.init.ModItemColors;
import xreliquary.client.registry.PedestalClientRegistry;
import xreliquary.client.render.MobCharmBeltLayerRenderer;
import xreliquary.client.render.RenderApothecaryMortar;
import xreliquary.client.render.RenderLyssaHook;
import xreliquary.client.render.RenderPedestalFishHook;
import xreliquary.client.render.RenderShot;
import xreliquary.client.render.RenderThrownKrakenSlime;
import xreliquary.client.render.RenderThrownXRPotion;
import xreliquary.client.render.RenderXRTippedArrow;
import xreliquary.client.render.TileEntityPedestalPassiveRenderer;
import xreliquary.client.render.TileEntityPedestalRenderer;
import xreliquary.common.CommonProxy;
import xreliquary.compat.jei.descriptions.JEIDescriptionRegistry;
import xreliquary.entities.EntityEnderStaffProjectile;
import xreliquary.entities.EntityGlowingWater;
import xreliquary.entities.EntityHolyHandGrenade;
import xreliquary.entities.EntityKrakenSlime;
import xreliquary.entities.EntityLyssaHook;
import xreliquary.entities.EntitySpecialSnowball;
import xreliquary.entities.EntityXRTippedArrow;
import xreliquary.entities.potion.EntityAttractionPotion;
import xreliquary.entities.potion.EntityFertilePotion;
import xreliquary.entities.potion.EntityThrownXRPotion;
import xreliquary.entities.shot.EntityBlazeShot;
import xreliquary.entities.shot.EntityBusterShot;
import xreliquary.entities.shot.EntityConcussiveShot;
import xreliquary.entities.shot.EntityEnderShot;
import xreliquary.entities.shot.EntityExorcismShot;
import xreliquary.entities.shot.EntityNeutralShot;
import xreliquary.entities.shot.EntitySandShot;
import xreliquary.entities.shot.EntitySeekerShot;
import xreliquary.entities.shot.EntityStormShot;
import xreliquary.handler.ClientEventHandler;
import xreliquary.init.ModFluids;
import xreliquary.init.ModItems;
import xreliquary.reference.Compatibility;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.XRPotionHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

//TODO refactor proxy so that it has functionally named methods that get called from main mod class
@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ClientProxy extends CommonProxy {

	public static final KeyBinding FORTUNE_COIN_TOGGLE_KEYBIND = new KeyBinding("xreliquary.keybind.fortune_coin", Keyboard.KEY_NONE, "xreliquary.keybind.category");

	@Override
	public void registerJEI(Block block, String name, boolean oneDescription) {
		if(Loader.isModLoaded(Compatibility.MOD_ID.JEI)) {
			if(oneDescription) {
				NonNullList<ItemStack> subBlocks = NonNullList.create();

				block.getSubBlocks(Reliquary.CREATIVE_TAB, subBlocks);

				JEIDescriptionRegistry.register(subBlocks, name);
			} else {
				JEIDescriptionRegistry.register(Item.getItemFromBlock(block), name);
			}
		}
	}

	@Override
	public void initSpecialJEIDescriptions() {
		if(!Loader.isModLoaded(Compatibility.MOD_ID.JEI))
			return;

		NonNullList<ItemStack> subItems = NonNullList.create();
		if (ModItems.potionEssence != null) {
			//noinspection ConstantConditions
			ModItems.potionEssence.getSubItems(ModItems.potionEssence.getCreativeTab(), subItems);
			JEIDescriptionRegistry.register(subItems, Names.Items.POTION_ESSENCE);
		}

		JEIDescriptionRegistry.register(Collections.singletonList(new ItemStack(ModItems.potion)), "potion0");

		List<ItemStack> potions = new ArrayList<>();
		List<ItemStack> splashPotions = new ArrayList<>();
		List<ItemStack> lingeringPotions = new ArrayList<>();
		List<ItemStack> tippedArrows = new ArrayList<>();
		List<ItemStack> potionShots = new ArrayList<>();
		List<ItemStack> potionMagazines = new ArrayList<>();

		for(PotionEssence essence : Settings.Potions.uniquePotions) {
			ItemStack potion = new ItemStack(ModItems.potion, 1);
			XRPotionHelper.addPotionEffectsToStack(potion, essence.getEffects());
			NBTHelper.setBoolean("hasPotion", potion, true);
			potions.add(potion);

			ItemStack splashPotion = potion.copy();
			NBTHelper.setBoolean("splash", splashPotion, true);
			splashPotions.add(splashPotion);

			ItemStack lingeringPotion = potion.copy();
			NBTHelper.setBoolean("lingering", lingeringPotion, true);
			lingeringPotions.add(lingeringPotion);

			ItemStack tippedArrow = new ItemStack(ModItems.tippedArrow);
			XRPotionHelper.addPotionEffectsToStack(tippedArrow, XRPotionHelper.changePotionEffectsDuration(essence.getEffects(), 0.125F));
			tippedArrows.add(tippedArrow);

			ItemStack potionShot = new ItemStack(ModItems.bullet, 1, 1);
			XRPotionHelper.addPotionEffectsToStack(potionShot, XRPotionHelper.changePotionEffectsDuration(essence.getEffects(), 0.2F));
			potionShots.add(potionShot);

			ItemStack potionMagazine = new ItemStack(ModItems.magazine, 1, 1);
			XRPotionHelper.addPotionEffectsToStack(potionMagazine, XRPotionHelper.changePotionEffectsDuration(essence.getEffects(), 0.2F));
			potionMagazines.add(potionMagazine);
		}
		JEIDescriptionRegistry.register(potions, Names.Items.POTION);
		JEIDescriptionRegistry.register(splashPotions, Names.Items.POTION_SPLASH);
		JEIDescriptionRegistry.register(lingeringPotions, Names.Items.POTION_LINGERING);
		JEIDescriptionRegistry.register(tippedArrows, Names.Items.TIPPED_ARROW);
		JEIDescriptionRegistry.register(potionShots, "bullet1_potion");
		JEIDescriptionRegistry.register(potionMagazines, "magazine1_potion");

		NonNullList<ItemStack> mobCharms = NonNullList.create();
		ModItems.mobCharm.getSubItems(Reliquary.CREATIVE_TAB, mobCharms);

		int meta = 0;
		for(ItemStack mobCharm : mobCharms) {
			JEIDescriptionRegistry.register(Collections.singletonList(mobCharm), Names.Items.MOB_CHARM + meta);
			meta++;
		}

	}

	@Override
	public void registerJEI(Item item, String name) {
		if(Loader.isModLoaded(Compatibility.MOD_ID.JEI))
			JEIDescriptionRegistry.register(item, name);
	}

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		ItemBlockModels.registerItemBlockModels();
		ItemModels.registerItemModels();
		registerEntityRenderers();
	}

	private static void registerEntityRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntityLyssaHook.class, RenderLyssaHook::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityBlazeShot.class, RenderShot::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityBusterShot.class, RenderShot::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityConcussiveShot.class, RenderShot::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityEnderShot.class, RenderShot::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityExorcismShot.class, RenderShot::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityNeutralShot.class, RenderShot::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySeekerShot.class, RenderShot::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySandShot.class, RenderShot::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityStormShot.class, RenderShot::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityXRTippedArrow.class, RenderXRTippedArrow::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityGlowingWater.class, renderManager -> new RenderSnowball(renderManager, ModItems.glowingWater, Minecraft.getMinecraft().getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityAttractionPotion.class, renderManager -> new RenderSnowball(renderManager, ModItems.attractionPotion, Minecraft.getMinecraft().getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityFertilePotion.class, renderManager -> new RenderSnowball(renderManager, ModItems.fertilePotion, Minecraft.getMinecraft().getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityHolyHandGrenade.class, renderManager -> new RenderSnowball(renderManager, ModItems.holyHandGrenade, Minecraft.getMinecraft().getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityKrakenSlime.class, RenderThrownKrakenSlime::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySpecialSnowball.class, renderManager -> new RenderSnowball(renderManager, Items.SNOWBALL, Minecraft.getMinecraft().getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityEnderStaffProjectile.class, renderManager -> new RenderSnowball(renderManager, Items.ENDER_PEARL, Minecraft.getMinecraft().getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityThrownXRPotion.class, renderManager -> new RenderThrownXRPotion(renderManager, Minecraft.getMinecraft().getRenderItem()));
	}

	@Override
	public void init() {
		super.init();
		ModItemColors.init();
		ModBlockColors.init();
		RegisterBeltRender();
		this.registerRenderers();
		ClientRegistry.registerKeyBinding(FORTUNE_COIN_TOGGLE_KEYBIND);
	}

	@Override
	public void postInit() {
		super.postInit();
		PedestalClientRegistry.registerItemRenderer(ItemFishingRod.class, RenderPedestalFishHook.class);
	}

	private void RegisterBeltRender() {
		Map<String, RenderPlayer> skinMap = Minecraft.getMinecraft().getRenderManager().getSkinMap();
		RenderPlayer render;
		render = skinMap.get("default");
		render.addLayer(new MobCharmBeltLayerRenderer());
	}

	private void registerRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMortar.class, new RenderApothecaryMortar());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPedestal.class, new TileEntityPedestalRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPedestalPassive.class, new TileEntityPedestalPassiveRenderer());
	}

	@Override
	public void initColors() {
		 /* Unicode colors that you can use in the tooltips/names lang files.
		 Use by calling {{!name}}, with name being the name being colors.color. */
		LanguageHelper.globals.put("colors.black", "\u00A70");
		LanguageHelper.globals.put("colors.navy", "\u00A71");
		LanguageHelper.globals.put("colors.green", "\u00A72");
		LanguageHelper.globals.put("colors.blue", "\u00A73");
		LanguageHelper.globals.put("colors.red", "\u00A74");
		LanguageHelper.globals.put("colors.purple", "\u00A75");
		LanguageHelper.globals.put("colors.gold", "\u00A76");
		LanguageHelper.globals.put("colors.light_gray", "\u00A77");
		LanguageHelper.globals.put("colors.gray", "\u00A78");
		LanguageHelper.globals.put("colors.dark_purple", "\u00A79");
		LanguageHelper.globals.put("colors.light_green", "\u00A7a");
		LanguageHelper.globals.put("colors.light_blue", "\u00A7b");
		LanguageHelper.globals.put("colors.rose", "\u00A7c");
		LanguageHelper.globals.put("colors.light_purple", "\u00A7d");
		LanguageHelper.globals.put("colors.yellow", "\u00A7e");
		LanguageHelper.globals.put("colors.white", "\u00A7f");
		LanguageHelper.globals.put("colors.reset", TextFormatting.RESET.toString());

	}
}
