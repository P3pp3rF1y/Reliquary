package reliquary.init;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import reliquary.client.gui.AlkahestryTomeGui;
import reliquary.client.gui.MobCharmBeltGui;
import reliquary.common.gui.AlkahestTomeMenu;
import reliquary.common.gui.MobCharmBeltMenu;
import reliquary.crafting.AlkahestryChargingRecipe;
import reliquary.crafting.AlkahestryCraftingRecipe;
import reliquary.crafting.AlkahestryDrainRecipe;
import reliquary.crafting.FragmentToSpawnEggRecipe;
import reliquary.crafting.MobCharmRecipe;
import reliquary.crafting.MobCharmRepairRecipe;
import reliquary.crafting.PotionEffectsRecipe;
import reliquary.crafting.conditions.AlkahestryEnabledCondition;
import reliquary.crafting.conditions.HandgunEnabledCondition;
import reliquary.crafting.conditions.MobDropsCraftableCondition;
import reliquary.crafting.conditions.PassivePedestalEnabledCondition;
import reliquary.crafting.conditions.PedestalEnabledCondition;
import reliquary.crafting.conditions.PotionsEnabledCondition;
import reliquary.crafting.conditions.SpawnEggEnabledCondition;
import reliquary.entities.GlowingWaterEntity;
import reliquary.entities.HolyHandGrenadeEntity;
import reliquary.entities.XRTippedArrowEntity;
import reliquary.entities.potion.AphroditePotionEntity;
import reliquary.entities.potion.FertilePotionEntity;
import reliquary.entities.potion.ThrownXRPotionEntity;
import reliquary.entities.shot.BlazeShotEntity;
import reliquary.entities.shot.BusterShotEntity;
import reliquary.entities.shot.ConcussiveShotEntity;
import reliquary.entities.shot.EnderShotEntity;
import reliquary.entities.shot.ExorcismShotEntity;
import reliquary.entities.shot.NeutralShotEntity;
import reliquary.entities.shot.SandShotEntity;
import reliquary.entities.shot.SeekerShotEntity;
import reliquary.entities.shot.StormShotEntity;
import reliquary.items.AlkahestryTomeItem;
import reliquary.items.AngelheartVialItem;
import reliquary.items.AngelicFeatherItem;
import reliquary.items.AphroditePotionItem;
import reliquary.items.BulletItem;
import reliquary.items.DestructionCatalystItem;
import reliquary.items.EmperorChaliceItem;
import reliquary.items.EnderStaffItem;
import reliquary.items.FertilePotionItem;
import reliquary.items.FortuneCoinItem;
import reliquary.items.GlacialStaffItem;
import reliquary.items.GlowingWaterItem;
import reliquary.items.HandgunItem;
import reliquary.items.HarvestRodItem;
import reliquary.items.HeroMedallionItem;
import reliquary.items.HolyHandGrenadeItem;
import reliquary.items.IceMagusRodItem;
import reliquary.items.InfernalChaliceItem;
import reliquary.items.InfernalClawsItem;
import reliquary.items.InfernalTearItem;
import reliquary.items.ItemBase;
import reliquary.items.KrakenShellItem;
import reliquary.items.LanternOfParanoiaItem;
import reliquary.items.MagazineItem;
import reliquary.items.MagicbaneItem;
import reliquary.items.MercyCrossItem;
import reliquary.items.MidasTouchstoneItem;
import reliquary.items.MobCharmBeltItem;
import reliquary.items.MobCharmFragmentItem;
import reliquary.items.MobCharmItem;
import reliquary.items.MobDropItem;
import reliquary.items.PhoenixDownItem;
import reliquary.items.PotionEssenceItem;
import reliquary.items.PotionItem;
import reliquary.items.PotionItemBase;
import reliquary.items.PyromancerStaffItem;
import reliquary.items.RendingGaleItem;
import reliquary.items.RodOfLyssaItem;
import reliquary.items.SalamanderEyeItem;
import reliquary.items.SerpentStaffItem;
import reliquary.items.ShearsOfWinterItem;
import reliquary.items.SojournerStaffItem;
import reliquary.items.ThrownPotionItem;
import reliquary.items.TippedArrowItem;
import reliquary.items.TwilightCloakItem;
import reliquary.items.VoidTearItem;
import reliquary.items.WitchHatItem;
import reliquary.items.WitherlessRoseItem;
import reliquary.reference.Colors;
import reliquary.reference.Reference;
import reliquary.reference.Settings;
import reliquary.util.RegistryHelper;

public class ModItems {
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MOD_ID);
	private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Reference.MOD_ID);
	private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Reference.MOD_ID);
	public static final RegistryObject<AlkahestryTomeItem> ALKAHESTRY_TOME = ITEMS.register("alkahestry_tome", AlkahestryTomeItem::new);
	public static final RegistryObject<MercyCrossItem> MERCY_CROSS = ITEMS.register("mercy_cross", MercyCrossItem::new);
	public static final RegistryObject<AngelheartVialItem> ANGELHEART_VIAL = ITEMS.register("angelheart_vial", AngelheartVialItem::new);
	public static final RegistryObject<AngelicFeatherItem> ANGELIC_FEATHER = ITEMS.register("angelic_feather", AngelicFeatherItem::new);
	public static final RegistryObject<AphroditePotionItem> APHRODITE_POTION = ITEMS.register("aphrodite_potion", AphroditePotionItem::new);
	public static final RegistryObject<PotionEssenceItem> POTION_ESSENCE = ITEMS.register("potion_essence", PotionEssenceItem::new);
	public static final RegistryObject<DestructionCatalystItem> DESTRUCTION_CATALYST = ITEMS.register("destruction_catalyst", DestructionCatalystItem::new);
	public static final RegistryObject<EmperorChaliceItem> EMPEROR_CHALICE = ITEMS.register("emperor_chalice", EmperorChaliceItem::new);
	public static final RegistryObject<EnderStaffItem> ENDER_STAFF = ITEMS.register("ender_staff", EnderStaffItem::new);
	public static final RegistryObject<FertilePotionItem> FERTILE_POTION = ITEMS.register("fertile_potion", FertilePotionItem::new);
	public static final RegistryObject<FortuneCoinItem> FORTUNE_COIN = ITEMS.register("fortune_coin", FortuneCoinItem::new);
	public static final RegistryObject<GlacialStaffItem> GLACIAL_STAFF = ITEMS.register("glacial_staff", GlacialStaffItem::new);
	public static final RegistryObject<GlowingWaterItem> GLOWING_WATER = ITEMS.register("glowing_water", GlowingWaterItem::new);
	public static final RegistryObject<HolyHandGrenadeItem> HOLY_HAND_GRENADE = ITEMS.register("holy_hand_grenade", HolyHandGrenadeItem::new);
	public static final RegistryObject<HandgunItem> HANDGUN = ITEMS.register("handgun", HandgunItem::new);
	public static final RegistryObject<ItemBase> GRIP_ASSEMBLY = ITEMS.register("grip_assembly", () -> new ItemBase(new Item.Properties().stacksTo(4), Settings.COMMON.disable.disableHandgun));
	public static final RegistryObject<ItemBase> BARREL_ASSEMBLY = ITEMS.register("barrel_assembly", () -> new ItemBase(new Item.Properties().stacksTo(4), Settings.COMMON.disable.disableHandgun));
	public static final RegistryObject<ItemBase> HAMMER_ASSEMBLY = ITEMS.register("hammer_assembly", () -> new ItemBase(new Item.Properties().stacksTo(4), Settings.COMMON.disable.disableHandgun));
	public static final RegistryObject<HarvestRodItem> HARVEST_ROD = ITEMS.register("harvest_rod", HarvestRodItem::new);
	public static final RegistryObject<MobCharmFragmentItem> MOB_CHARM_FRAGMENT = ITEMS.register("mob_charm_fragment", MobCharmFragmentItem::new);
	public static final RegistryObject<HeroMedallionItem> HERO_MEDALLION = ITEMS.register("hero_medallion", HeroMedallionItem::new);
	public static final RegistryObject<IceMagusRodItem> ICE_MAGUS_ROD = ITEMS.register("ice_magus_rod", IceMagusRodItem::new);
	public static final RegistryObject<InfernalChaliceItem> INFERNAL_CHALICE = ITEMS.register("infernal_chalice", InfernalChaliceItem::new);
	public static final RegistryObject<InfernalClawsItem> INFERNAL_CLAWS = ITEMS.register("infernal_claws", InfernalClawsItem::new);
	public static final RegistryObject<InfernalTearItem> INFERNAL_TEAR = ITEMS.register("infernal_tear", InfernalTearItem::new);
	public static final RegistryObject<KrakenShellItem> KRAKEN_SHELL = ITEMS.register("kraken_shell", KrakenShellItem::new);
	public static final RegistryObject<MidasTouchstoneItem> MIDAS_TOUCHSTONE = ITEMS.register("midas_touchstone", MidasTouchstoneItem::new);
	public static final RegistryObject<PhoenixDownItem> PHOENIX_DOWN = ITEMS.register("phoenix_down", PhoenixDownItem::new);
	public static final RegistryObject<PyromancerStaffItem> PYROMANCER_STAFF = ITEMS.register("pyromancer_staff", PyromancerStaffItem::new);
	public static final RegistryObject<RendingGaleItem> RENDING_GALE = ITEMS.register("rending_gale", RendingGaleItem::new);
	public static final RegistryObject<RodOfLyssaItem> ROD_OF_LYSSA = ITEMS.register("rod_of_lyssa", RodOfLyssaItem::new);
	public static final RegistryObject<SojournerStaffItem> SOJOURNER_STAFF = ITEMS.register("sojourner_staff", SojournerStaffItem::new);
	public static final RegistryObject<TippedArrowItem> TIPPED_ARROW = ITEMS.register("tipped_arrow", TippedArrowItem::new);
	public static final RegistryObject<VoidTearItem> VOID_TEAR = ITEMS.register("void_tear", VoidTearItem::new);
	public static final RegistryObject<WitchHatItem> WITCH_HAT = ITEMS.register("witch_hat", WitchHatItem::new);
	public static final RegistryObject<WitherlessRoseItem> WITHERLESS_ROSE = ITEMS.register("witherless_rose", WitherlessRoseItem::new);
	public static final RegistryObject<ItemBase> EMPTY_POTION_VIAL = ITEMS.register("empty_potion_vial", () -> new ItemBase(Settings.COMMON.disable.disablePotions));
	public static final RegistryObject<PotionItemBase> POTION = ITEMS.register("potion", PotionItem::new);
	public static final RegistryObject<PotionItemBase> SPLASH_POTION = ITEMS.register("splash_potion", ThrownPotionItem::new);
	public static final RegistryObject<PotionItemBase> LINGERING_POTION = ITEMS.register("lingering_potion", ThrownPotionItem::new);
	public static final RegistryObject<MobCharmBeltItem> MOB_CHARM_BELT = ITEMS.register("mob_charm_belt", MobCharmBeltItem::new);
	public static final RegistryObject<MobCharmItem> MOB_CHARM = ITEMS.register("mob_charm", MobCharmItem::new);
	public static final RegistryObject<MagazineItem> EMPTY_MAGAZINE = ITEMS.register("magazines/empty_magazine", () ->
			new MagazineItem(false, Integer.parseInt(Colors.DARKEST, 16)));
	public static final RegistryObject<MagazineItem> NEUTRAL_MAGAZINE = ITEMS.register("magazines/neutral_magazine", () ->
			new MagazineItem(true, Integer.parseInt(Colors.NEUTRAL_SHOT_COLOR, 16)));
	public static final RegistryObject<MagazineItem> EXORCISM_MAGAZINE = ITEMS.register("magazines/exorcism_magazine", () ->
			new MagazineItem(false, Integer.parseInt(Colors.EXORCISM_SHOT_COLOR, 16)));
	public static final RegistryObject<MagazineItem> BLAZE_MAGAZINE = ITEMS.register("magazines/blaze_magazine", () ->
			new MagazineItem(false, Integer.parseInt(Colors.BLAZE_SHOT_COLOR, 16)));
	public static final RegistryObject<MagazineItem> ENDER_MAGAZINE = ITEMS.register("magazines/ender_magazine", () ->
			new MagazineItem(false, Integer.parseInt(Colors.ENDER_SHOT_COLOR, 16)));
	public static final RegistryObject<MagazineItem> CONCUSSIVE_MAGAZINE = ITEMS.register("magazines/concussive_magazine", () ->
			new MagazineItem(false, Integer.parseInt(Colors.CONCUSSIVE_SHOT_COLOR, 16)));
	public static final RegistryObject<MagazineItem> BUSTER_MAGAZINE = ITEMS.register("magazines/buster_magazine", () ->
			new MagazineItem(false, Integer.parseInt(Colors.BUSTER_SHOT_COLOR, 16)));
	public static final RegistryObject<MagazineItem> SEEKER_MAGAZINE = ITEMS.register("magazines/seeker_magazine", () ->
			new MagazineItem(false, Integer.parseInt(Colors.SEEKER_SHOT_COLOR, 16)));
	public static final RegistryObject<MagazineItem> SAND_MAGAZINE = ITEMS.register("magazines/sand_magazine", () ->
			new MagazineItem(false, Integer.parseInt(Colors.SAND_SHOT_COLOR, 16)));
	public static final RegistryObject<MagazineItem> STORM_MAGAZINE = ITEMS.register("magazines/storm_magazine", () ->
			new MagazineItem(false, Integer.parseInt(Colors.STORM_SHOT_COLOR, 16)));
	public static final RegistryObject<BulletItem> EMPTY_BULLET = ITEMS.register("bullets/empty_bullet", () ->
			new BulletItem(false, false, Integer.parseInt(Colors.DARKEST, 16)));
	public static final RegistryObject<BulletItem> NEUTRAL_BULLET = ITEMS.register("bullets/neutral_bullet", () ->
			new BulletItem(false, true, Integer.parseInt(Colors.NEUTRAL_SHOT_COLOR, 16)));
	public static final RegistryObject<BulletItem> EXORCISM_BULLET = ITEMS.register("bullets/exorcism_bullet", () ->
			new BulletItem(true, false, Integer.parseInt(Colors.EXORCISM_SHOT_COLOR, 16)));
	public static final RegistryObject<BulletItem> BLAZE_BULLET = ITEMS.register("bullets/blaze_bullet", () ->
			new BulletItem(true, false, Integer.parseInt(Colors.BLAZE_SHOT_COLOR, 16)));
	public static final RegistryObject<BulletItem> ENDER_BULLET = ITEMS.register("bullets/ender_bullet", () ->
			new BulletItem(true, false, Integer.parseInt(Colors.ENDER_SHOT_COLOR, 16)));
	public static final RegistryObject<BulletItem> CONCUSSIVE_BULLET = ITEMS.register("bullets/concussive_bullet", () ->
			new BulletItem(true, false, Integer.parseInt(Colors.CONCUSSIVE_SHOT_COLOR, 16)));
	public static final RegistryObject<BulletItem> BUSTER_BULLET = ITEMS.register("bullets/buster_bullet", () ->
			new BulletItem(true, false, Integer.parseInt(Colors.BUSTER_SHOT_COLOR, 16)));
	public static final RegistryObject<BulletItem> SEEKER_BULLET = ITEMS.register("bullets/seeker_bullet", () ->
			new BulletItem(true, false, Integer.parseInt(Colors.SEEKER_SHOT_COLOR, 16)));
	public static final RegistryObject<BulletItem> SAND_BULLET = ITEMS.register("bullets/sand_bullet", () ->
			new BulletItem(true, false, Integer.parseInt(Colors.SAND_SHOT_COLOR, 16)));
	public static final RegistryObject<BulletItem> STORM_BULLET = ITEMS.register("bullets/storm_bullet", () ->
			new BulletItem(true, false, Integer.parseInt(Colors.STORM_SHOT_COLOR, 16)));
	public static final RegistryObject<ItemBase> ZOMBIE_HEART = ITEMS.register("zombie_heart", MobDropItem::new);
	public static final RegistryObject<ItemBase> SQUID_BEAK = ITEMS.register("squid_beak", MobDropItem::new);
	public static final RegistryObject<ItemBase> RIB_BONE = ITEMS.register("rib_bone", MobDropItem::new);
	public static final RegistryObject<ItemBase> CATALYZING_GLAND = ITEMS.register("catalyzing_gland", MobDropItem::new);
	public static final RegistryObject<ItemBase> CHELICERAE = ITEMS.register("chelicerae", MobDropItem::new);
	public static final RegistryObject<ItemBase> SLIME_PEARL = ITEMS.register("slime_pearl", MobDropItem::new);
	public static final RegistryObject<ItemBase> KRAKEN_SHELL_FRAGMENT = ITEMS.register("kraken_shell_fragment", ItemBase::new);
	public static final RegistryObject<ItemBase> BAT_WING = ITEMS.register("bat_wing", MobDropItem::new);
	public static final RegistryObject<ItemBase> WITHERED_RIB = ITEMS.register("withered_rib", MobDropItem::new);
	public static final RegistryObject<ItemBase> MOLTEN_CORE = ITEMS.register("molten_core", MobDropItem::new);
	public static final RegistryObject<ItemBase> EYE_OF_THE_STORM = ITEMS.register("eye_of_the_storm", MobDropItem::new);
	public static final RegistryObject<ItemBase> FERTILE_ESSENCE = ITEMS.register("fertile_essence", ItemBase::new);
	public static final RegistryObject<ItemBase> FROZEN_CORE = ITEMS.register("frozen_core", MobDropItem::new);
	public static final RegistryObject<ItemBase> NEBULOUS_HEART = ITEMS.register("nebulous_heart", MobDropItem::new);
	public static final RegistryObject<ItemBase> INFERNAL_CLAW = ITEMS.register("infernal_claw", ItemBase::new);
	public static final RegistryObject<ItemBase> GUARDIAN_SPIKE = ITEMS.register("guardian_spike", MobDropItem::new);
	public static final RegistryObject<ItemBase> CRIMSON_CLOTH = ITEMS.register("crimson_cloth", ItemBase::new);
	public static final RegistryObject<LanternOfParanoiaItem> LANTERN_OF_PARANOIA = ITEMS.register("lantern_of_paranoia", LanternOfParanoiaItem::new);
	public static final RegistryObject<MagicbaneItem> MAGICBANE = ITEMS.register("magicbane", MagicbaneItem::new);
	public static final RegistryObject<SalamanderEyeItem> SALAMANDER_EYE = ITEMS.register("salamander_eye", SalamanderEyeItem::new);
	public static final RegistryObject<SerpentStaffItem> SERPENT_STAFF = ITEMS.register("serpent_staff", SerpentStaffItem::new);
	public static final RegistryObject<ShearsOfWinterItem> SHEARS_OF_WINTER = ITEMS.register("shears_of_winter", ShearsOfWinterItem::new);
	public static final RegistryObject<TwilightCloakItem> TWILIGHT_CLOAK = ITEMS.register("twilight_cloak", TwilightCloakItem::new);
	public static final RegistryObject<ItemBase> GLOWING_BREAD = ITEMS.register("glowing_bread", () ->
			new ItemBase(new Item.Properties().rarity(Rarity.RARE).food(new FoodProperties.Builder().nutrition(20).saturationMod(1F).fast().build())));

	public static final RegistryObject<MenuType<AlkahestTomeMenu>> ALKAHEST_TOME_MENU_TYPE = MENU_TYPES.register("alkahest_tome",
			() -> IForgeMenuType.create((windowId, inv, data) -> AlkahestTomeMenu.fromBuffer(windowId)));

	public static final RegistryObject<MenuType<MobCharmBeltMenu>> MOB_CHAR_BELT_MENU_TYPE = MENU_TYPES.register("mob_char_belt",
			() -> IForgeMenuType.create(MobCharmBeltMenu::fromBuffer));

	public static final RegistryObject<RecipeSerializer<?>> MOB_CHARM_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("mob_charm", MobCharmRecipe.Serializer::new);
	public static final RegistryObject<RecipeSerializer<?>> FRAGMENT_TO_SPAWN_EGG_SERIALIZER = RECIPE_SERIALIZERS.register("fragment_to_spawn_egg", FragmentToSpawnEggRecipe.Serializer::new);
	public static final RegistryObject<RecipeSerializer<?>> MOB_CHARM_REPAIR_SERIALIZER = RECIPE_SERIALIZERS.register("mob_charm_repair", () -> new SimpleRecipeSerializer<>(MobCharmRepairRecipe::new));
	public static final RegistryObject<RecipeSerializer<?>> ALKAHESTRY_CHARGING_SERIALIZER = RECIPE_SERIALIZERS.register("alkahestry_charging", AlkahestryChargingRecipe.Serializer::new);
	public static final RegistryObject<RecipeSerializer<?>> ALKAHESTRY_CRAFTING_SERIALIZER = RECIPE_SERIALIZERS.register("alkahestry_crafting", AlkahestryCraftingRecipe.Serializer::new);
	public static final RegistryObject<RecipeSerializer<?>> ALKAHESTRY_DRAIN_SERIALIZER = RECIPE_SERIALIZERS.register("alkahestry_drain", AlkahestryDrainRecipe.Serializer::new);
	public static final RegistryObject<RecipeSerializer<?>> POTION_EFFECTS_SERIALIZER = RECIPE_SERIALIZERS.register("potion_effects", PotionEffectsRecipe.Serializer::new);

	public static void registerContainers(RegisterEvent event) {
		if (!event.getRegistryKey().equals(ForgeRegistries.Keys.MENU_TYPES)) {
			return;
		}

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			MenuScreens.register(ALKAHEST_TOME_MENU_TYPE.get(), AlkahestryTomeGui::new);
			MenuScreens.register(MOB_CHAR_BELT_MENU_TYPE.get(), MobCharmBeltGui::new);
		});
	}

	public static void registerDispenseBehaviors() {
		if (Boolean.FALSE.equals(Settings.COMMON.disable.disablePotions.get())) {
			DispenserBlock.registerBehavior(ModItems.SPLASH_POTION.get(), new BehaviorDefaultProjectileDispense() {
				@Override
				ProjectileEntityFactory getProjectileEntityFactory() {
					return (world, position, stack) -> new ThrownXRPotionEntity(world, position.x(), position.y(), position.z(), stack);
				}
			});

			DispenserBlock.registerBehavior(ModItems.LINGERING_POTION.get(), new BehaviorDefaultProjectileDispense() {
				@Override
				ProjectileEntityFactory getProjectileEntityFactory() {
					return (world, position, stack) -> new ThrownXRPotionEntity(world, position.x(), position.y(), position.z(), stack);
				}
			});

			DispenserBlock.registerBehavior(ModItems.APHRODITE_POTION.get(), new BehaviorDefaultProjectileDispense() {
				@Override
				ProjectileEntityFactory getProjectileEntityFactory() {
					return (world, position, stack) -> new AphroditePotionEntity(world, position.x(), position.y(), position.z());
				}
			});

			DispenserBlock.registerBehavior(ModItems.FERTILE_POTION.get(), new BehaviorDefaultProjectileDispense() {
				@Override
				ProjectileEntityFactory getProjectileEntityFactory() {
					return (world, position, stack) -> new FertilePotionEntity(world, position.x(), position.y(), position.z());
				}
			});

			DispenserBlock.registerBehavior(ModItems.TIPPED_ARROW.get(), new AbstractProjectileDispenseBehavior() {
				@Override
				protected Projectile getProjectile(Level world, Position position, ItemStack stack) {
					XRTippedArrowEntity entitytippedarrow = new XRTippedArrowEntity(world, position.x(), position.y(), position.z());
					entitytippedarrow.setPotionEffect(stack);
					entitytippedarrow.pickup = AbstractArrow.Pickup.ALLOWED;
					return entitytippedarrow;
				}
			});
		}
		DispenserBlock.registerBehavior(ModItems.GLOWING_WATER.get(), new BehaviorDefaultProjectileDispense() {
			@Override
			ProjectileEntityFactory getProjectileEntityFactory() {
				return (world, position, stack) -> new GlowingWaterEntity(world, position.x(), position.y(), position.z());
			}
		});

		DispenserBlock.registerBehavior(ModItems.HOLY_HAND_GRENADE.get(), new BehaviorDefaultProjectileDispense() {
			@Override
			ProjectileEntityFactory getProjectileEntityFactory() {
				return (world, position, stack) -> new HolyHandGrenadeEntity(world, position.x(), position.y(), position.z());
			}
		});
	}

	public static void registerRecipeSerializers(RegisterEvent event) {
		if (event.getRegistryKey().equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS)) {
			CraftingHelper.register(MobDropsCraftableCondition.SERIALIZER);
			CraftingHelper.register(AlkahestryEnabledCondition.SERIALIZER);
			CraftingHelper.register(HandgunEnabledCondition.SERIALIZER);
			CraftingHelper.register(PotionsEnabledCondition.SERIALIZER);
			CraftingHelper.register(PassivePedestalEnabledCondition.SERIALIZER);
			CraftingHelper.register(PedestalEnabledCondition.SERIALIZER);
			CraftingHelper.register(SpawnEggEnabledCondition.SERIALIZER);
		}
	}

	public static void registerHandgunMagazines() {
		HandgunItem handgun = HANDGUN.get();
		handgun.registerMagazine(RegistryHelper.getRegistryName(NEUTRAL_MAGAZINE.get()).toString(), NeutralShotEntity::new, NEUTRAL_BULLET);
		handgun.registerMagazine(RegistryHelper.getRegistryName(EXORCISM_MAGAZINE.get()).toString(), ExorcismShotEntity::new, EXORCISM_BULLET);
		handgun.registerMagazine(RegistryHelper.getRegistryName(BLAZE_MAGAZINE.get()).toString(), BlazeShotEntity::new, BLAZE_BULLET);
		handgun.registerMagazine(RegistryHelper.getRegistryName(ENDER_MAGAZINE.get()).toString(), EnderShotEntity::new, ENDER_BULLET);
		handgun.registerMagazine(RegistryHelper.getRegistryName(CONCUSSIVE_MAGAZINE.get()).toString(), ConcussiveShotEntity::new, CONCUSSIVE_BULLET);
		handgun.registerMagazine(RegistryHelper.getRegistryName(BUSTER_MAGAZINE.get()).toString(), BusterShotEntity::new, BUSTER_BULLET);
		handgun.registerMagazine(RegistryHelper.getRegistryName(SEEKER_MAGAZINE.get()).toString(), SeekerShotEntity::new, SEEKER_BULLET);
		handgun.registerMagazine(RegistryHelper.getRegistryName(SAND_MAGAZINE.get()).toString(), SandShotEntity::new, SAND_BULLET);
		handgun.registerMagazine(RegistryHelper.getRegistryName(STORM_MAGAZINE.get()).toString(), StormShotEntity::new, STORM_BULLET);
	}

	public static void registerListeners(IEventBus modBus) {
		ITEMS.register(modBus);
		MENU_TYPES.register(modBus);
		RECIPE_SERIALIZERS.register(modBus);
		modBus.addListener(ModItems::registerRecipeSerializers);
		modBus.addListener(ModItems::registerContainers);
		MinecraftForge.EVENT_BUS.addListener(ModItems::onResourceReload);
	}

	private abstract static class BehaviorDefaultProjectileDispense implements DispenseItemBehavior {
		abstract ProjectileEntityFactory getProjectileEntityFactory();

		@Override
		public ItemStack dispense(BlockSource source, ItemStack stack) {
			return (new AbstractProjectileDispenseBehavior() {

				@Override
				protected Projectile getProjectile(Level world, Position position, ItemStack stack) {
					return getProjectileEntityFactory().createProjectileEntity(world, position, stack);
				}

				@Override
				protected float getUncertainty() {
					return super.getUncertainty() * 0.5F;
				}

				@Override
				protected float getPower() {
					return super.getPower() * 1.25F;
				}
			}).dispense(source, stack);
		}
	}

	private static void onResourceReload(AddReloadListenerEvent event) {
		MobCharmRecipe.REGISTERED_RECIPES.clear();
	}

	private interface ProjectileEntityFactory {
		Projectile createProjectileEntity(Level world, Position position, ItemStack stack);
	}
}
