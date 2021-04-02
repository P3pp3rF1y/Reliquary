package xreliquary.init;

import net.minecraft.block.DispenserBlock;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.ProjectileDispenseBehavior;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import xreliquary.client.gui.AlkahestryTomeGui;
import xreliquary.client.gui.MobCharmBeltGui;
import xreliquary.common.gui.ContainerAlkahestTome;
import xreliquary.common.gui.ContainerMobCharmBelt;
import xreliquary.crafting.AlkahestryChargingRecipe;
import xreliquary.crafting.AlkahestryCraftingRecipe;
import xreliquary.crafting.AlkahestryDrainRecipe;
import xreliquary.crafting.FragmentToSpawnEggRecipe;
import xreliquary.crafting.MobCharmRecipe;
import xreliquary.crafting.MobCharmRepairRecipe;
import xreliquary.crafting.PotionEffectsRecipe;
import xreliquary.crafting.conditions.AlkahestryEnabledCondition;
import xreliquary.crafting.conditions.HandgunEnabledCondition;
import xreliquary.crafting.conditions.MobDropsCraftableCondition;
import xreliquary.crafting.conditions.PassivePedestalEnabledCondition;
import xreliquary.crafting.conditions.PedestalEnabledCondition;
import xreliquary.crafting.conditions.PotionsEnabledCondition;
import xreliquary.entities.GlowingWaterEntity;
import xreliquary.entities.HolyHandGrenadeEntity;
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
import xreliquary.items.AlkahestryTomeItem;
import xreliquary.items.AngelheartVialItem;
import xreliquary.items.AngelicFeatherItem;
import xreliquary.items.AttractionPotionItem;
import xreliquary.items.BulletItem;
import xreliquary.items.DestructionCatalystItem;
import xreliquary.items.EmperorChaliceItem;
import xreliquary.items.EnderStaffItem;
import xreliquary.items.FertilePotionItem;
import xreliquary.items.FortuneCoinItem;
import xreliquary.items.GlacialStaffItem;
import xreliquary.items.GlowingWaterItem;
import xreliquary.items.HandgunItem;
import xreliquary.items.HarvestRodItem;
import xreliquary.items.HeroMedallionItem;
import xreliquary.items.HolyHandGrenadeItem;
import xreliquary.items.IceMagusRodItem;
import xreliquary.items.InfernalChaliceItem;
import xreliquary.items.InfernalClawsItem;
import xreliquary.items.InfernalTearItem;
import xreliquary.items.ItemBase;
import xreliquary.items.KrakenShellItem;
import xreliquary.items.LanternOfParanoiaItem;
import xreliquary.items.MagazineItem;
import xreliquary.items.MagicbaneItem;
import xreliquary.items.MercyCrossItem;
import xreliquary.items.MidasTouchstoneItem;
import xreliquary.items.MobCharmBeltItem;
import xreliquary.items.MobCharmFragmentItem;
import xreliquary.items.MobCharmItem;
import xreliquary.items.PhoenixDownItem;
import xreliquary.items.PotionEssenceItem;
import xreliquary.items.PotionItem;
import xreliquary.items.PotionItemBase;
import xreliquary.items.PyromancerStaffItem;
import xreliquary.items.RendingGaleItem;
import xreliquary.items.RodOfLyssaItem;
import xreliquary.items.SalamanderEyeItem;
import xreliquary.items.SerpentStaffItem;
import xreliquary.items.ShearsOfWinterItem;
import xreliquary.items.SojournerStaffItem;
import xreliquary.items.ThrownPotionItem;
import xreliquary.items.TippedArrowItem;
import xreliquary.items.TwilightCloakItem;
import xreliquary.items.VoidTearItem;
import xreliquary.items.WitchHatItem;
import xreliquary.items.WitherlessRoseItem;
import xreliquary.reference.Colors;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;
import xreliquary.util.RegistryHelper;

public class ModItems {
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MOD_ID);
	private static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, Reference.MOD_ID);

	public static final RegistryObject<AlkahestryTomeItem> ALKAHESTRY_TOME = ITEMS.register("alkahestry_tome", AlkahestryTomeItem::new);
	public static final RegistryObject<MercyCrossItem> MERCY_CROSS = ITEMS.register("mercy_cross", MercyCrossItem::new);
	public static final RegistryObject<AngelheartVialItem> ANGELHEART_VIAL = ITEMS.register("angelheart_vial", AngelheartVialItem::new);
	public static final RegistryObject<AngelicFeatherItem> ANGELIC_FEATHER = ITEMS.register("angelic_feather", AngelicFeatherItem::new);
	public static final RegistryObject<AttractionPotionItem> ATTRACTION_POTION = ITEMS.register("attraction_potion", AttractionPotionItem::new);
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
	public static final RegistryObject<ItemBase> GRIP_ASSEMBLY = ITEMS.register("grip_assembly", () -> new ItemBase(new Item.Properties().maxStackSize(4)));
	public static final RegistryObject<ItemBase> BARREL_ASSEMBLY = ITEMS.register("barrel_assembly", () -> new ItemBase(new Item.Properties().maxStackSize(4)));
	public static final RegistryObject<ItemBase> HAMMER_ASSEMBLY = ITEMS.register("hammer_assembly", () -> new ItemBase(new Item.Properties().maxStackSize(4)));
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
	public static final RegistryObject<ItemBase> EMPTY_POTION_VIAL = ITEMS.register("empty_potion_vial", ItemBase::new);
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
	public static final RegistryObject<ItemBase> ZOMBIE_HEART = ITEMS.register("zombie_heart", ItemBase::new);
	public static final RegistryObject<ItemBase> SQUID_BEAK = ITEMS.register("squid_beak", ItemBase::new);
	public static final RegistryObject<ItemBase> RIB_BONE = ITEMS.register("rib_bone", ItemBase::new);
	public static final RegistryObject<ItemBase> CATALYZING_GLAND = ITEMS.register("catalyzing_gland", ItemBase::new);
	public static final RegistryObject<ItemBase> CHELICERAE = ITEMS.register("chelicerae", ItemBase::new);
	public static final RegistryObject<ItemBase> SLIME_PEARL = ITEMS.register("slime_pearl", ItemBase::new);
	public static final RegistryObject<ItemBase> KRAKEN_SHELL_FRAGMENT = ITEMS.register("kraken_shell_fragment", ItemBase::new);
	public static final RegistryObject<ItemBase> BAT_WING = ITEMS.register("bat_wing", ItemBase::new);
	public static final RegistryObject<ItemBase> WITHERED_RIB = ITEMS.register("withered_rib", ItemBase::new);
	public static final RegistryObject<ItemBase> MOLTEN_CORE = ITEMS.register("molten_core", ItemBase::new);
	public static final RegistryObject<ItemBase> EYE_OF_THE_STORM = ITEMS.register("eye_of_the_storm", ItemBase::new);
	public static final RegistryObject<ItemBase> FERTILE_ESSENCE = ITEMS.register("fertile_essence", ItemBase::new);
	public static final RegistryObject<ItemBase> FROZEN_CORE = ITEMS.register("frozen_core", ItemBase::new);
	public static final RegistryObject<ItemBase> NEBULOUS_HEART = ITEMS.register("nebulous_heart", ItemBase::new);
	public static final RegistryObject<ItemBase> INFERNAL_CLAW = ITEMS.register("infernal_claw", ItemBase::new);
	public static final RegistryObject<ItemBase> GUARDIAN_SPIKE = ITEMS.register("guardian_spike", ItemBase::new);
	public static final RegistryObject<ItemBase> CRIMSON_CLOTH = ITEMS.register("crimson_cloth", ItemBase::new);
	public static final RegistryObject<LanternOfParanoiaItem> LANTERN_OF_PARANOIA = ITEMS.register("lantern_of_paranoia", LanternOfParanoiaItem::new);
	public static final RegistryObject<MagicbaneItem> MAGICBANE = ITEMS.register("magicbane", MagicbaneItem::new);
	public static final RegistryObject<SalamanderEyeItem> SALAMANDER_EYE = ITEMS.register("salamander_eye", SalamanderEyeItem::new);
	public static final RegistryObject<SerpentStaffItem> SERPENT_STAFF = ITEMS.register("serpent_staff", SerpentStaffItem::new);
	public static final RegistryObject<ShearsOfWinterItem> SHEARS_OF_WINTER = ITEMS.register("shears_of_winter", ShearsOfWinterItem::new);
	public static final RegistryObject<TwilightCloakItem> TWILIGHT_CLOAK = ITEMS.register("twilight_cloak", TwilightCloakItem::new);
	public static final RegistryObject<ItemBase> GLOWING_BREAD = ITEMS.register("glowing_bread", () ->
			new ItemBase(new Item.Properties().rarity(Rarity.RARE).food(new Food.Builder().hunger(20).saturation(1F).fastToEat().build())));

	public static final RegistryObject<ContainerType<ContainerAlkahestTome>> ALKAHEST_TOME_CONTAINER_TYPE = CONTAINERS.register("alkahest_tome",
			() -> IForgeContainerType.create((windowId, inv, data) -> ContainerAlkahestTome.fromBuffer(windowId)));

	public static final RegistryObject<ContainerType<ContainerMobCharmBelt>> MOB_CHAR_BELT_CONTAINER_TYPE = CONTAINERS.register("mob_char_belt",
			() -> IForgeContainerType.create(ContainerMobCharmBelt::fromBuffer));

	public static void registerContainers(RegistryEvent.Register<ContainerType<?>> evt) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			ScreenManager.registerFactory(ALKAHEST_TOME_CONTAINER_TYPE.get(), AlkahestryTomeGui::new);
			ScreenManager.registerFactory(MOB_CHAR_BELT_CONTAINER_TYPE.get(), MobCharmBeltGui::new);
		});
	}

	public static void registerDispenseBehaviors() {
		if (Boolean.FALSE.equals(Settings.COMMON.disable.disablePotions.get())) {
			DispenserBlock.registerDispenseBehavior(ModItems.SPLASH_POTION.get(), new BehaviorDefaultProjectileDispense() {
				@Override
				ProjectileEntityFactory getProjectileEntityFactory() {
					return (world, position, stack) -> new ThrownXRPotionEntity(world, position.getX(), position.getY(), position.getZ(), stack);
				}
			});

			DispenserBlock.registerDispenseBehavior(ModItems.LINGERING_POTION.get(), new BehaviorDefaultProjectileDispense() {
				@Override
				ProjectileEntityFactory getProjectileEntityFactory() {
					return (world, position, stack) -> new ThrownXRPotionEntity(world, position.getX(), position.getY(), position.getZ(), stack);
				}
			});

			DispenserBlock.registerDispenseBehavior(ModItems.ATTRACTION_POTION.get(), new BehaviorDefaultProjectileDispense() {
				@Override
				ProjectileEntityFactory getProjectileEntityFactory() {
					return (world, position, stack) -> new AttractionPotionEntity(world, position.getX(), position.getY(), position.getZ());
				}
			});

			DispenserBlock.registerDispenseBehavior(ModItems.FERTILE_POTION.get(), new BehaviorDefaultProjectileDispense() {
				@Override
				ProjectileEntityFactory getProjectileEntityFactory() {
					return (world, position, stack) -> new FertilePotionEntity(world, position.getX(), position.getY(), position.getZ());
				}
			});

			DispenserBlock.registerDispenseBehavior(ModItems.TIPPED_ARROW.get(), new ProjectileDispenseBehavior() {

				@Override
				protected ProjectileEntity getProjectileEntity(World world, IPosition position, ItemStack stack) {
					XRTippedArrowEntity entitytippedarrow = new XRTippedArrowEntity(world, position.getX(), position.getY(), position.getZ());
					entitytippedarrow.setPotionEffect(stack);
					entitytippedarrow.pickupStatus = AbstractArrowEntity.PickupStatus.ALLOWED;
					return entitytippedarrow;
				}
			});
		}
		DispenserBlock.registerDispenseBehavior(ModItems.GLOWING_WATER.get(), new BehaviorDefaultProjectileDispense() {
			@Override
			ProjectileEntityFactory getProjectileEntityFactory() {
				return (world, position, stack) -> new GlowingWaterEntity(world, position.getX(), position.getY(), position.getZ());
			}
		});

		DispenserBlock.registerDispenseBehavior(ModItems.HOLY_HAND_GRENADE.get(), new BehaviorDefaultProjectileDispense() {
			@Override
			ProjectileEntityFactory getProjectileEntityFactory() {
				return (world, position, stack) -> new HolyHandGrenadeEntity(world, position.getX(), position.getY(), position.getZ());
			}
		});
	}



	public static void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> evt) {
		CraftingHelper.register(MobDropsCraftableCondition.SERIALIZER);
		CraftingHelper.register(AlkahestryEnabledCondition.SERIALIZER);
		CraftingHelper.register(HandgunEnabledCondition.SERIALIZER);
		CraftingHelper.register(PotionsEnabledCondition.SERIALIZER);
		CraftingHelper.register(PassivePedestalEnabledCondition.SERIALIZER);
		CraftingHelper.register(PedestalEnabledCondition.SERIALIZER);

		IForgeRegistry<IRecipeSerializer<?>> reg = evt.getRegistry();
		reg.register(MobCharmRecipe.SERIALIZER.setRegistryName(new ResourceLocation(Reference.MOD_ID, "mob_charm")));
		reg.register(FragmentToSpawnEggRecipe.SERIALIZER.setRegistryName(new ResourceLocation(Reference.MOD_ID, "fragment_to_spawn_egg")));
		reg.register(MobCharmRepairRecipe.SERIALIZER.setRegistryName(new ResourceLocation(Reference.MOD_ID, "mob_charm_repair")));
		reg.register(AlkahestryChargingRecipe.SERIALIZER.setRegistryName(new ResourceLocation(Reference.MOD_ID, "alkahestry_charging")));
		reg.register(AlkahestryCraftingRecipe.SERIALIZER.setRegistryName(new ResourceLocation(Reference.MOD_ID, "alkahestry_crafting")));
		reg.register(AlkahestryDrainRecipe.SERIALIZER.setRegistryName(new ResourceLocation(Reference.MOD_ID, "alkahestry_drain")));
		reg.register(PotionEffectsRecipe.SERIALIZER.setRegistryName(new ResourceLocation(Reference.MOD_ID, "potion_effects")));
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

	public static boolean isEnabled(Item... items) {
		for (Item item : items) {
			if (item == null || item.getRegistryName() == null) {
				return false;
			}
		}
		return true;
	}

	public static void registerListeners(IEventBus modBus) {
		ITEMS.register(modBus);
		CONTAINERS.register(modBus);
		modBus.addGenericListener(IRecipeSerializer.class, ModItems::registerRecipeSerializers);
		modBus.addGenericListener(ContainerType.class, ModItems::registerContainers);
	}

	private abstract static class BehaviorDefaultProjectileDispense implements IDispenseItemBehavior {
		abstract ProjectileEntityFactory getProjectileEntityFactory();

		@Override
		public ItemStack dispense(IBlockSource source, ItemStack stack) {
			return (new ProjectileDispenseBehavior() {

				@Override
				protected ProjectileEntity getProjectileEntity(World world, IPosition position, ItemStack stack) {
					return getProjectileEntityFactory().createProjectileEntity(world, position, stack);
				}

				@Override
				protected float getProjectileInaccuracy() {
					return super.getProjectileInaccuracy() * 0.5F;
				}

				@Override
				protected float getProjectileVelocity() {
					return super.getProjectileVelocity() * 1.25F;
				}
			}).dispense(source, stack);
		}
	}

	private interface ProjectileEntityFactory {
		ProjectileEntity createProjectileEntity(World world, IPosition position, ItemStack stack);
	}
}
