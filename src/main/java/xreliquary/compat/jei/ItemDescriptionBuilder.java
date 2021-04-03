package xreliquary.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import xreliquary.Reliquary;
import xreliquary.init.ModBlocks;
import xreliquary.init.ModItems;
import xreliquary.items.MobCharmFragmentItem;
import xreliquary.items.MobCharmItem;
import xreliquary.reference.Reference;
import xreliquary.util.LanguageHelper;
import xreliquary.util.RegistryHelper;
import xreliquary.util.potions.XRPotionHelper;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ItemDescriptionBuilder {
	private ItemDescriptionBuilder() {}

	public static void addIngredientInfo(IRecipeRegistration registration) {
		registerItemDescription(registration, ModItems.ALKAHESTRY_TOME.get());
		registerItemDescription(registration, ModItems.MERCY_CROSS.get());
		registerItemDescription(registration, ModItems.ANGELHEART_VIAL.get());
		registerItemDescription(registration, ModItems.ANGELIC_FEATHER.get());
		registerItemDescription(registration, ModItems.ATTRACTION_POTION.get());
		registerItemDescription(registration, ModItems.POTION_ESSENCE.get());
		registerItemDescription(registration, ModItems.DESTRUCTION_CATALYST.get());
		registerItemDescription(registration, ModItems.EMPEROR_CHALICE.get());
		registerItemDescription(registration, ModItems.ENDER_STAFF.get());
		registerItemDescription(registration, ModItems.FERTILE_POTION.get());
		registerItemDescription(registration, ModItems.FORTUNE_COIN.get());
		registerItemDescription(registration, ModItems.GLACIAL_STAFF.get());
		registerItemDescription(registration, ModItems.GLOWING_WATER.get());
		registerItemDescription(registration, ModItems.HOLY_HAND_GRENADE.get());
		registerItemDescription(registration, ModItems.HANDGUN.get());
		registerItemDescription(registration, ModItems.GRIP_ASSEMBLY.get());
		registerItemDescription(registration, ModItems.BARREL_ASSEMBLY.get());
		registerItemDescription(registration, ModItems.HAMMER_ASSEMBLY.get());
		registerItemDescription(registration, ModItems.HARVEST_ROD.get());
		registerItemDescription(registration, ModItems.HERO_MEDALLION.get());
		registerItemDescription(registration, ModItems.ICE_MAGUS_ROD.get());
		registerItemDescription(registration, ModItems.INFERNAL_CHALICE.get());
		registerItemDescription(registration, ModItems.INFERNAL_CLAWS.get());
		registerItemDescription(registration, ModItems.INFERNAL_TEAR.get());
		registerItemDescription(registration, ModItems.KRAKEN_SHELL.get());
		registerItemDescription(registration, ModItems.MIDAS_TOUCHSTONE.get());
		registerItemDescription(registration, ModItems.PHOENIX_DOWN.get());
		registerItemDescription(registration, ModItems.PYROMANCER_STAFF.get());
		registerItemDescription(registration, ModItems.RENDING_GALE.get());
		registerItemDescription(registration, ModItems.ROD_OF_LYSSA.get());
		registerItemDescription(registration, ModItems.SOJOURNER_STAFF.get());
		registerItemDescription(registration, ModItems.TIPPED_ARROW.get());
		registerItemDescription(registration, ModItems.VOID_TEAR.get());
		registerItemDescription(registration, ModItems.WITCH_HAT.get());
		registerItemDescription(registration, ModItems.WITHERLESS_ROSE.get());
		registerItemDescription(registration, ModItems.EMPTY_POTION_VIAL.get());
		registerItemDescription(registration, ModItems.POTION.get());
		registerItemDescription(registration, ModItems.SPLASH_POTION.get());
		registerItemDescription(registration, ModItems.LINGERING_POTION.get());
		registerItemDescription(registration, ModItems.MOB_CHARM_BELT.get());
		registerItemDescription(registration, ModItems.ZOMBIE_HEART.get());
		registerItemDescription(registration, ModItems.SQUID_BEAK.get());
		registerItemDescription(registration, ModItems.RIB_BONE.get());
		registerItemDescription(registration, ModItems.CATALYZING_GLAND.get());
		registerItemDescription(registration, ModItems.CHELICERAE.get());
		registerItemDescription(registration, ModItems.SLIME_PEARL.get());
		registerItemDescription(registration, ModItems.KRAKEN_SHELL_FRAGMENT.get());
		registerItemDescription(registration, ModItems.BAT_WING.get());
		registerItemDescription(registration, ModItems.WITHERED_RIB.get());
		registerItemDescription(registration, ModItems.MOLTEN_CORE.get());
		registerItemDescription(registration, ModItems.EYE_OF_THE_STORM.get());
		registerItemDescription(registration, ModItems.FERTILE_ESSENCE.get());
		registerItemDescription(registration, ModItems.FROZEN_CORE.get());
		registerItemDescription(registration, ModItems.NEBULOUS_HEART.get());
		registerItemDescription(registration, ModItems.INFERNAL_CLAW.get());
		registerItemDescription(registration, ModItems.GUARDIAN_SPIKE.get());
		registerItemDescription(registration, ModItems.CRIMSON_CLOTH.get());
		registerItemDescription(registration, ModItems.LANTERN_OF_PARANOIA.get());
		registerItemDescription(registration, ModItems.MAGICBANE.get());
		registerItemDescription(registration, ModItems.SALAMANDER_EYE.get());
		registerItemDescription(registration, ModItems.SERPENT_STAFF.get());
		registerItemDescription(registration, ModItems.SHEARS_OF_WINTER.get());
		registerItemDescription(registration, ModItems.TWILIGHT_CLOAK.get());
		registerItemDescription(registration, ModItems.GLOWING_BREAD.get());

		registerCharmFragmentItemsDescription(registration);
		registerCharmItemsDescription(registration);

		registerItemDescription(registration, ModItems.EMPTY_MAGAZINE.get());
		registerItemDescription(registration, ModItems.EMPTY_BULLET.get());
		registerPotionAmmoItemsDescription(registration, ModItems.NEUTRAL_MAGAZINE.get());
		registerPotionAmmoItemsDescription(registration, ModItems.EXORCISM_MAGAZINE.get());
		registerPotionAmmoItemsDescription(registration, ModItems.BLAZE_MAGAZINE.get());
		registerPotionAmmoItemsDescription(registration, ModItems.ENDER_MAGAZINE.get());
		registerPotionAmmoItemsDescription(registration, ModItems.CONCUSSIVE_MAGAZINE.get());
		registerPotionAmmoItemsDescription(registration, ModItems.BUSTER_MAGAZINE.get());
		registerPotionAmmoItemsDescription(registration, ModItems.SEEKER_MAGAZINE.get());
		registerPotionAmmoItemsDescription(registration, ModItems.SAND_MAGAZINE.get());
		registerPotionAmmoItemsDescription(registration, ModItems.STORM_MAGAZINE.get());
		registerPotionAmmoItemsDescription(registration, ModItems.NEUTRAL_BULLET.get());
		registerPotionAmmoItemsDescription(registration, ModItems.EXORCISM_BULLET.get());
		registerPotionAmmoItemsDescription(registration, ModItems.BLAZE_BULLET.get());
		registerPotionAmmoItemsDescription(registration, ModItems.ENDER_BULLET.get());
		registerPotionAmmoItemsDescription(registration, ModItems.CONCUSSIVE_BULLET.get());
		registerPotionAmmoItemsDescription(registration, ModItems.BUSTER_BULLET.get());
		registerPotionAmmoItemsDescription(registration, ModItems.SEEKER_BULLET.get());
		registerPotionAmmoItemsDescription(registration, ModItems.SAND_BULLET.get());
		registerPotionAmmoItemsDescription(registration, ModItems.STORM_BULLET.get());

		registerItemDescription(registration, ModBlocks.ALKAHESTRY_ALTAR_ITEM.get());
		registerItemDescription(registration, ModBlocks.APOTHECARY_CAULDRON_ITEM.get());
		registerItemDescription(registration, ModBlocks.APOTHECARY_MORTAR_ITEM.get());
		registerItemDescription(registration, ModBlocks.INTERDICTION_TORCH_ITEM.get());
		registerItemDescription(registration, ModBlocks.WRAITH_NODE_ITEM.get());

		addStacksIngredientInfo(registration, ModBlocks.PEDESTAL_ITEMS.values().stream().map(ro -> new ItemStack(ro.get())).collect(Collectors.toList()), "pedestal");
		addStacksIngredientInfo(registration, ModBlocks.PASSIVE_PEDESTAL_ITEMS.values().stream().map(ro -> new ItemStack(ro.get())).collect(Collectors.toList()), "passive_pedestal");
	}

	private static void registerItemDescription(IRecipeRegistration registration, Item item) {
		NonNullList<ItemStack> subItems = NonNullList.create();
		item.fillItemGroup(Reliquary.ITEM_GROUP, subItems);
		if (!subItems.isEmpty()) {
			addStacksIngredientInfo(registration, item, subItems);
		}
	}

	private static void addStacksIngredientInfo(IRecipeRegistration registration, Item item, List<ItemStack> items, String... additionalKeys) {
		String[] langKeys;
		String regName = RegistryHelper.getRegistryName(item).getPath();
		if (additionalKeys.length > 0) {
			langKeys = new String[additionalKeys.length + 1];
			langKeys[0] = regName;
			System.arraycopy(additionalKeys, 0, langKeys, 1, additionalKeys.length);
		} else {
			langKeys = new String[] {regName};
		}

		addStacksIngredientInfo(registration, items, langKeys);
	}

	private static void addStacksIngredientInfo(IRecipeRegistration registration, List<ItemStack> items, String... langKeys) {
		registration.addIngredientInfo(items, VanillaTypes.ITEM, getTranslationKeys(langKeys));
	}

	private static String[] getTranslationKeys(String... langKeys) {
		String[] keys = new String[langKeys.length];
		for (int i = 0; i < langKeys.length; i++) {
			keys[i] = String.format("jei.%s.description.%s", Reference.MOD_ID, langKeys[i].replace('/', '.'));
		}

		return keys;
	}

	private static void registerCharmFragmentItemsDescription(IRecipeRegistration registration) {
		MobCharmFragmentItem item = ModItems.MOB_CHARM_FRAGMENT.get();
		BinaryOperator<String> getLocalization = (itemDescriptionKey, entityName) -> LanguageHelper.getLocalization(itemDescriptionKey, entityName, entityName);
		registerCharmBasedItems(registration, item, getLocalization, MobCharmFragmentItem::getEntityEggRegistryName);
	}

	private static void registerCharmItemsDescription(IRecipeRegistration registration) {
		MobCharmItem item = ModItems.MOB_CHARM.get();
		BinaryOperator<String> getLocalization = LanguageHelper::getLocalization;
		registerCharmBasedItems(registration, item, getLocalization, MobCharmItem::getEntityEggRegistryName);
	}

	private static void registerCharmBasedItems(IRecipeRegistration registration, Item item, BinaryOperator<String> getLocalization, Function<ItemStack, ResourceLocation> getEntityRegistryName) {
		NonNullList<ItemStack> subItems = NonNullList.create();
		item.fillItemGroup(Reliquary.ITEM_GROUP, subItems);
		for (ItemStack subItem : subItems) {
			EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(getEntityRegistryName.apply(subItem));
			if (entityType == null) {
				continue;
			}
			String path = RegistryHelper.getRegistryName(item).getPath();
			String itemDescriptionKey = String.format("jei.%s.description.%s", Reference.MOD_ID, path.replace('/', '.'));
			String entityName = entityType.getName().getString();
			registration.addIngredientInfo(subItem, VanillaTypes.ITEM, getLocalization.apply(itemDescriptionKey, entityName));
		}
	}

	private static void registerPotionAmmoItemsDescription(IRecipeRegistration registration, Item item) {
		NonNullList<ItemStack> subItems = NonNullList.create();
		NonNullList<ItemStack> potionItems = NonNullList.create();
		item.fillItemGroup(Reliquary.ITEM_GROUP, subItems);
		for (ItemStack subItem : subItems) {
			if (!XRPotionHelper.getPotionEffectsFromStack(subItem).isEmpty()) {
				potionItems.add(subItem);
			}
		}
		if (!potionItems.isEmpty()) {
			addStacksIngredientInfo(registration, item, potionItems, "ammo_potion");
		}

		subItems = NonNullList.create();
		NonNullList<ItemStack> nonPotionItems = NonNullList.create();
		item.fillItemGroup(Reliquary.ITEM_GROUP, subItems);
		for (ItemStack subItem : subItems) {
			if (XRPotionHelper.getPotionEffectsFromStack(subItem).isEmpty()) {
				nonPotionItems.add(subItem);
			}
		}
		if (!nonPotionItems.isEmpty()) {
			addStacksIngredientInfo(registration, item, nonPotionItems);
		}
	}
}
