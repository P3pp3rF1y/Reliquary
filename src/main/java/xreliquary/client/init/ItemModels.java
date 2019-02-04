package xreliquary.client.init;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import xreliquary.client.ItemModelLocations;
import xreliquary.client.model.ModelVoidTear;
import xreliquary.init.ModItems;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Side.CLIENT)
public class ItemModels {
	public static void registerItemModels() {
		if (!Settings.Disable.disableAlkahestry) {
			registerItemModel(ModItems.alkahestryTome, Names.Items.ALKAHESTRY_TOME);
		}
		registerItemModel(ModItems.mercyCross, Names.Items.MERCY_CROSS);
		registerItemModel(ModItems.angelheartVial, Names.Items.ANGELHEART_VIAL);
		registerItemModel(ModItems.angelicFeather, Names.Items.ANGELIC_FEATHER);
		registerItemModel(ModItems.destructionCatalyst, Names.Items.DESTRUCTION_CATALYST);
		registerItemModel(ModItems.emperorChalice, Names.Items.EMPEROR_CHALICE);
		registerItemModel(ModItems.enderStaff, Names.Items.ENDER_STAFF);
		registerItemModel(ModItems.fortuneCoin, Names.Items.FORTUNE_COIN);
		registerItemModel(ModItems.glacialStaff, Names.Items.GLACIAL_STAFF);
		registerItemModel(ModItems.glowingBread, Names.Items.GLOWING_BREAD);
		registerItemModel(ModItems.glowingWater, Names.Items.GLOWING_WATER);
		if (!Settings.Disable.disableHandgun) {
			registerItemModel(ModItems.handgun, Names.Items.HANDGUN);
		}
		registerItemModel(ModItems.harvestRod, Names.Items.HARVEST_ROD);
		registerItemModel(ModItems.heroMedallion, Names.Items.HERO_MEDALLION);
		registerItemModel(ModItems.holyHandGrenade, Names.Items.HOLY_HAND_GRENADE);
		registerItemModel(ModItems.iceMagusRod, Names.Items.ICE_MAGUS_ROD);
		registerItemModel(ModItems.infernalChalice, Names.Items.INFERNAL_CHALICE);
		registerItemModel(ModItems.infernalClaws, Names.Items.INFERNAL_CLAWS);
		registerItemModel(ModItems.krakenShell, Names.Items.KRAKEN_SHELL);
		registerItemModel(ModItems.lanternOfParanoia, Names.Items.LANTERN_OF_PARANOIA);
		registerItemModel(ModItems.magicbane, Names.Items.MAGICBANE);
		registerItemModel(ModItems.midasTouchstone, Names.Items.MIDAS_TOUCHSTONE);
		registerItemModel(ModItems.phoenixDown, Names.Items.PHOENIX_DOWN);
		registerItemModel(ModItems.pyromancerStaff, Names.Items.PYROMANCER_STAFF);
		registerItemModel(ModItems.rendingGale, Names.Items.RENDING_GALE);
		registerItemModel(ModItems.salamanderEye, Names.Items.SALAMANDER_EYE);
		registerItemModel(ModItems.serpentStaff, Names.Items.SERPENT_STAFF);
		registerItemModel(ModItems.shearsOfWinter, Names.Items.SHEARS_OF_WINTER);
		registerItemModel(ModItems.sojournerStaff, Names.Items.SOJOURNER_STAFF);
		registerItemModel(ModItems.twilightCloak, Names.Items.TWILIGHT_CLOAK);

		registerItemModel(ModItems.witchHat, Names.Items.WITCH_HAT);
		registerItemModel(ModItems.witherlessRose, Names.Items.WITHERLESS_ROSE);

		registerItemModel(ModItems.mobCharmBelt, Names.Items.MOB_CHARM_BELT);

		registerItemModel(ModItems.rodOfLyssa, Names.Items.ROD_OF_LYSSA);

		ModelBakery.registerItemVariants(ModItems.rodOfLyssa, ItemModelLocations.ROD_OF_LYSSA_CAST);

		for (int i = 0; i < 17; i++) {
			registerItemModel(ModItems.mobIngredient, Names.Items.MOB_INGREDIENT, i, true);
		}

		if (!Settings.Disable.disableHandgun) {
			registerItemModelForAllVariants(ModItems.magazine, Names.Items.MAGAZINE, stack -> new ModelResourceLocation(Reference.DOMAIN + Names.Items.MAGAZINE, "inventory"));

			for (int i = 0; i < 3; i++) {
				registerItemModel(ModItems.gunPart, Names.Items.GUN_PART, i, true);
			}

			registerItemModelForAllVariants(ModItems.bullet, Names.Items.BULLET, stack -> new ModelResourceLocation(Reference.DOMAIN + Names.Items.BULLET, "inventory"));
		}
		registerItemModelForAllVariants(ModItems.mobCharmFragment, Names.Items.MOB_CHARM_FRAGMENT, stack -> new ModelResourceLocation(Reference.DOMAIN + Names.Items.MOB_CHARM_FRAGMENT, "inventory"));
		registerItemModelForAllVariants(ModItems.mobCharm, Names.Items.MOB_CHARM, stack -> new ModelResourceLocation(Reference.DOMAIN + Names.Items.MOB_CHARM, "inventory"));
		registerItemModelForAllVariants(ModItems.infernalTear, Names.Items.INFERNAL_TEAR_EMPTY, stack -> {
			if (!ModItems.infernalTear.getStackFromTear(stack).isEmpty()) {
				return ItemModelLocations.INFERNAL_TEAR;
			}
			return ItemModelLocations.INFERNAL_TEAR_EMPTY;
		});
		ModelBakery.registerItemVariants(ModItems.infernalTear, ItemModelLocations.INFERNAL_TEAR);

		registerItemModelForAllVariants(ModItems.voidTear, Names.Items.VOID_TEAR_EMPTY, stack -> {
			if (!ModItems.voidTear.isEmpty(stack, true)) {
				return ItemModelLocations.VOID_TEAR;
			}
			return ItemModelLocations.VOID_TEAR_EMPTY;
		});
		ModelBakery.registerItemVariants(ModItems.voidTear, ItemModelLocations.VOID_TEAR);

		if (!Settings.Disable.disablePotions) {
			registerItemModel(ModItems.tippedArrow, Names.Items.TIPPED_ARROW);
			registerItemModel(ModItems.attractionPotion, Names.Items.ATTRACTION_POTION);
			registerItemModel(ModItems.fertilePotion, Names.Items.FERTILE_POTION);
			registerItemModelForAllVariants(ModItems.potionEssence, Names.Items.POTION_ESSENCE, stack -> new ModelResourceLocation(Reference.DOMAIN + Names.Items.POTION_ESSENCE, "inventory"));

			registerItemModelForAllVariants(ModItems.potion, Names.Items.POTION, stack -> {
				if (ModItems.potion.isSplash(stack)) {
					return ItemModelLocations.POTION_SPLASH;
				} else if (ModItems.potion.isLingering(stack)) {
					return ItemModelLocations.POTION_LINGERING;
				}
				return ItemModelLocations.POTION;
			});
			ModelBakery.registerItemVariants(ModItems.potion, ItemModelLocations.POTION_SPLASH);
			ModelBakery.registerItemVariants(ModItems.potion, ItemModelLocations.POTION_LINGERING);
		}
	}

	private static void registerItemModelForAllVariants(Item item, String resourceName, ItemMeshDefinition itemMeshDefinition) {

		resourceName = Reference.DOMAIN + resourceName;

		ModelBakery.registerItemVariants(item, new ResourceLocation(resourceName));

		ModelLoader.setCustomMeshDefinition(item, itemMeshDefinition);
	}

	private static void registerItemModel(Item item, String resourceName) {
		registerItemModel(item, resourceName, 0, false);
	}

	private static void registerItemModel(Item item, String resourceName, int meta, boolean hasSubTypes) {
		if (hasSubTypes) {
			resourceName = resourceName + "_" + meta;
		}

		resourceName = Reference.DOMAIN + resourceName;

		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(resourceName, "inventory"));
	}

	@SubscribeEvent
	public static void onModelBake(ModelBakeEvent event) {
		ModelResourceLocation key = new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID, Names.Items.VOID_TEAR), "inventory");
		ModelVoidTear voidTearModel = new ModelVoidTear(event.getModelRegistry().getObject(key));

		event.getModelRegistry().putObject(key, voidTearModel);
	}
}
