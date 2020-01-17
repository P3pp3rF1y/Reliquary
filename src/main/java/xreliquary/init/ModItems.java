package xreliquary.init;

import net.minecraft.block.DispenserBlock;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.ProjectileDispenseBehavior;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.AbstractArrowEntity;
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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import xreliquary.Reliquary;
import xreliquary.client.gui.GuiAlkahestTome;
import xreliquary.client.gui.GuiMobCharmBelt;
import xreliquary.common.gui.ContainerAlkahestTome;
import xreliquary.common.gui.ContainerMobCharmBelt;
import xreliquary.crafting.AlkahestryChargingRecipe;
import xreliquary.crafting.AlkahestryCraftingRecipe;
import xreliquary.crafting.AlkahestryDrainRecipe;
import xreliquary.crafting.MobCharmRepairRecipe;
import xreliquary.crafting.MobDropsCraftableCondition;
import xreliquary.crafting.PotionEffectsRecipe;
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
import xreliquary.items.PyromancerStaffItem;
import xreliquary.items.RendingGaleItem;
import xreliquary.items.RodOfLyssaItem;
import xreliquary.items.SalamanderEyeItem;
import xreliquary.items.SerpentStaffItem;
import xreliquary.items.ShearsOfWinterItem;
import xreliquary.items.SojournerStaffItem;
import xreliquary.items.TippedArrowItem;
import xreliquary.items.TwilightCloakItem;
import xreliquary.items.VoidTearItem;
import xreliquary.items.WitchHatItem;
import xreliquary.items.WitherlessRoseItem;
import xreliquary.reference.Colors;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;
import xreliquary.util.InjectionHelper;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Reference.MOD_ID)
public class ModItems {
	//TODO move to names?
	private static final String NEUTRAL_MAGAZINE_REGISTRY_NAME = "magazines/neutral_magazine";
	private static final String EXORCISM_MAGAZINE_REGISTRY_NAME = "magazines/exorcism_magazine";
	private static final String BLAZE_MAGAZINE_REGISTRY_NAME = "magazines/blaze_magazine";
	private static final String ENDER_MAGAZINE_REGISTRY_NAME = "magazines/ender_magazine";
	private static final String CONCUSSIVE_MAGAZINE_REGISTRY_NAME = "magazines/concussive_magazine";
	private static final String BUSTER_MAGAZINE_REGISTRY_NAME = "magazines/buster_magazine";
	private static final String SEEKER_MAGAZINE_REGISTRY_NAME = "magazines/seeker_magazine";
	private static final String SAND_MAGAZINE_REGISTRY_NAME = "magazines/sand_magazine";
	private static final String STORM_MAGAZINE_REGISTRY_NAME = "magazines/storm_magazine";
	private static final String EMPTY_MAGAZINE_REGISTRY_NAME = "magazines/empty_magazine";
	private static final String EMPTY_BULLET_REGISTRY_NAME = "bullets/empty_bullet";
	private static final String NEUTRAL_BULLET_REGISTRY_NAME = "bullets/neutral_bullet";
	private static final String EXORCISM_BULLET_REGISTRY_NAME = "bullets/exorcism_bullet";
	private static final String BLAZE_BULLET_REGISTRY_NAME = "bullets/blaze_bullet";
	private static final String ENDER_BULLET_REGISTRY_NAME = "bullets/ender_bullet";
	private static final String CONCUSSIVE_BULLET_REGISTRY_NAME = "bullets/concussive_bullet";
	private static final String BUSTER_BULLET_REGISTRY_NAME = "bullets/buster_bullet";
	private static final String SEEKER_BULLET_REGISTRY_NAME = "bullets/seeker_bullet";
	private static final String SAND_BULLET_REGISTRY_NAME = "bullets/sand_bullet";
	private static final String STORM_BULLET_REGISTRY_NAME = "bullets/storm_bullet";

	public static final AlkahestryTomeItem ALKAHESTRY_TOME = InjectionHelper.nullValue();
	public static final MercyCrossItem MERCY_CROSS = InjectionHelper.nullValue();
	public static final AngelheartVialItem ANGELHEART_VIAL = InjectionHelper.nullValue();
	public static final AngelicFeatherItem ANGELIC_FEATHER = InjectionHelper.nullValue();
	public static final AttractionPotionItem ATTRACTION_POTION = InjectionHelper.nullValue();
	public static final PotionEssenceItem POTION_ESSENCE = InjectionHelper.nullValue();
	public static final DestructionCatalystItem DESTRUCTION_CATALYST = InjectionHelper.nullValue();
	public static final EmperorChaliceItem EMPEROR_CHALICE = InjectionHelper.nullValue();
	public static final EnderStaffItem ENDER_STAFF = InjectionHelper.nullValue();
	public static final FertilePotionItem FERTILE_POTION = InjectionHelper.nullValue();
	public static final FortuneCoinItem FORTUNE_COIN = InjectionHelper.nullValue();
	public static final GlacialStaffItem GLACIAL_STAFF = InjectionHelper.nullValue();
	public static final GlowingWaterItem GLOWING_WATER = InjectionHelper.nullValue();
	public static final HolyHandGrenadeItem HOLY_HAND_GRENADE = InjectionHelper.nullValue();
	public static final HandgunItem HANDGUN = InjectionHelper.nullValue();
	public static final HarvestRodItem HARVEST_ROD = InjectionHelper.nullValue();
	public static final MobCharmFragmentItem MOB_CHARM_FRAGMENT = InjectionHelper.nullValue();
	public static final HeroMedallionItem HERO_MEDALLION = InjectionHelper.nullValue();
	public static final IceMagusRodItem ICE_MAGUS_ROD = InjectionHelper.nullValue();
	public static final InfernalChaliceItem INFERNAL_CHALICE = InjectionHelper.nullValue();
	public static final InfernalClawsItem INFERNAL_CLAWS = InjectionHelper.nullValue();
	public static final KrakenShellItem KRAKEN_SHELL = InjectionHelper.nullValue();
	public static final MidasTouchstoneItem MIDAS_TOUCHSTONE = InjectionHelper.nullValue();
	public static final PhoenixDownItem PHOENIX_DOWN = InjectionHelper.nullValue();
	public static final PyromancerStaffItem PYROMANCER_STAFF = InjectionHelper.nullValue();
	public static final RendingGaleItem RENDING_GALE = InjectionHelper.nullValue();
	public static final RodOfLyssaItem ROD_OF_LYSSA = InjectionHelper.nullValue();
	public static final SojournerStaffItem SOJOURNER_STAFF = InjectionHelper.nullValue();
	public static final TippedArrowItem TIPPED_ARROW = InjectionHelper.nullValue();
	public static final VoidTearItem VOID_TEAR = InjectionHelper.nullValue();
	public static final WitchHatItem WITCH_HAT = InjectionHelper.nullValue();
	public static final WitherlessRoseItem WITHERLESS_ROSE = InjectionHelper.nullValue();
	public static final PotionItem POTION = InjectionHelper.nullValue();
	public static final MobCharmBeltItem MOB_CHARM_BELT = InjectionHelper.nullValue();
	public static final MobCharmItem MOB_CHARM = InjectionHelper.nullValue();
	@ObjectHolder(EMPTY_MAGAZINE_REGISTRY_NAME)
	public static final MagazineItem EMPTY_MAGAZINE = InjectionHelper.nullValue();
	@ObjectHolder(NEUTRAL_MAGAZINE_REGISTRY_NAME)
	public static final MagazineItem NEUTRAL_MAGAZINE = InjectionHelper.nullValue();
	@ObjectHolder(EXORCISM_MAGAZINE_REGISTRY_NAME)
	public static final MagazineItem EXORCISM_MAGAZINE = InjectionHelper.nullValue();
	@ObjectHolder(BLAZE_MAGAZINE_REGISTRY_NAME)
	public static final MagazineItem BLAZE_MAGAZINE = InjectionHelper.nullValue();
	@ObjectHolder(ENDER_MAGAZINE_REGISTRY_NAME)
	public static final MagazineItem ENDER_MAGAZINE = InjectionHelper.nullValue();
	@ObjectHolder(CONCUSSIVE_MAGAZINE_REGISTRY_NAME)
	public static final MagazineItem CONCUSSIVE_MAGAZINE = InjectionHelper.nullValue();
	@ObjectHolder(BUSTER_MAGAZINE_REGISTRY_NAME)
	public static final MagazineItem BUSTER_MAGAZINE = InjectionHelper.nullValue();
	@ObjectHolder(SEEKER_MAGAZINE_REGISTRY_NAME)
	public static final MagazineItem SEEKER_MAGAZINE = InjectionHelper.nullValue();
	@ObjectHolder(SAND_MAGAZINE_REGISTRY_NAME)
	public static final MagazineItem SAND_MAGAZINE = InjectionHelper.nullValue();
	@ObjectHolder(STORM_MAGAZINE_REGISTRY_NAME)
	public static final MagazineItem STORM_MAGAZINE = InjectionHelper.nullValue();
	@ObjectHolder(EMPTY_BULLET_REGISTRY_NAME)
	public static final BulletItem EMPTY_BULLET = InjectionHelper.nullValue();
	@ObjectHolder(NEUTRAL_BULLET_REGISTRY_NAME)
	public static final BulletItem NEUTRAL_BULLET = InjectionHelper.nullValue();
	@ObjectHolder(EXORCISM_BULLET_REGISTRY_NAME)
	public static final BulletItem EXORCISM_BULLET = InjectionHelper.nullValue();
	@ObjectHolder(BLAZE_BULLET_REGISTRY_NAME)
	public static final BulletItem BLAZE_BULLET = InjectionHelper.nullValue();
	@ObjectHolder(ENDER_BULLET_REGISTRY_NAME)
	public static final BulletItem ENDER_BULLET = InjectionHelper.nullValue();
	@ObjectHolder(CONCUSSIVE_BULLET_REGISTRY_NAME)
	public static final BulletItem CONCUSSIVE_BULLET = InjectionHelper.nullValue();
	@ObjectHolder(BUSTER_BULLET_REGISTRY_NAME)
	public static final BulletItem BUSTER_BULLET = InjectionHelper.nullValue();
	@ObjectHolder(SEEKER_BULLET_REGISTRY_NAME)
	public static final BulletItem SEEKER_BULLET = InjectionHelper.nullValue();
	@ObjectHolder(SAND_BULLET_REGISTRY_NAME)
	public static final BulletItem SAND_BULLET = InjectionHelper.nullValue();
	@ObjectHolder(STORM_BULLET_REGISTRY_NAME)
	public static final BulletItem STORM_BULLET = InjectionHelper.nullValue();
	public static final ItemBase ZOMBIE_HEART = InjectionHelper.nullValue();
	public static final ItemBase SQUID_BEAK = InjectionHelper.nullValue();
	public static final ItemBase RIB_BONE = InjectionHelper.nullValue();
	public static final ItemBase CATALYZING_GLAND = InjectionHelper.nullValue();
	public static final ItemBase CHELICERAE = InjectionHelper.nullValue();
	public static final ItemBase SLIME_PEARL = InjectionHelper.nullValue();
	public static final ItemBase KRAKEN_SHELL_FRAGMENT = InjectionHelper.nullValue();
	public static final ItemBase BAT_WING = InjectionHelper.nullValue();
	public static final ItemBase WITHERED_RIB = InjectionHelper.nullValue();
	public static final ItemBase MOLTEN_CORE = InjectionHelper.nullValue();
	public static final ItemBase EYE_OF_THE_STORM = InjectionHelper.nullValue();
	public static final ItemBase FERTILE_ESSENCE = InjectionHelper.nullValue();
	public static final ItemBase FROZEN_CORE = InjectionHelper.nullValue();
	public static final ItemBase NEBULOUS_HEART = InjectionHelper.nullValue();
	public static final ItemBase INFERNAL_CLAW = InjectionHelper.nullValue();
	public static final ItemBase GUARDIAN_SPIKE = InjectionHelper.nullValue();

	@SuppressWarnings("ConstantConditions")
	@SubscribeEvent
	public static void registerContainers(RegistryEvent.Register<ContainerType<?>> evt) {
		IForgeRegistry<ContainerType<?>> r = evt.getRegistry();

		ContainerType<ContainerAlkahestTome> tome = IForgeContainerType.create(ContainerAlkahestTome::fromBuffer);
		r.register(tome.setRegistryName(ALKAHESTRY_TOME.getRegistryName()));

		ContainerType<ContainerMobCharmBelt> belt = IForgeContainerType.create(ContainerMobCharmBelt::fromBuffer);
		r.register(belt.setRegistryName(MOB_CHARM_BELT.getRegistryName()));

		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			ScreenManager.registerFactory(tome, GuiAlkahestTome::new);
			ScreenManager.registerFactory(belt, GuiMobCharmBelt::new);
		});
	}

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();
		if (Boolean.FALSE.equals(Settings.COMMON.disable.disableAlkahestry.get())) {
			registerItem(registry, new AlkahestryTomeItem());
		}
		registerItem(registry, new MercyCrossItem());
		registerItem(registry, new AngelheartVialItem());
		registerItem(registry, new AngelicFeatherItem());

		registerItem(registry, new DestructionCatalystItem());
		registerItem(registry, new EmperorChaliceItem());
		registerItem(registry, new EnderStaffItem());
		registerItem(registry, new FortuneCoinItem());
		registerItem(registry, new GlacialStaffItem());
		registerItem(registry, new ItemBase("glowing_bread", new Item.Properties().rarity(Rarity.RARE).food(new Food.Builder().hunger(20).saturation(1F).fastToEat().build())));
		registerItem(registry, new GlowingWaterItem());
		registerItem(registry, new HarvestRodItem());
		registerItem(registry, new HeroMedallionItem());
		registerItem(registry, new HolyHandGrenadeItem());
		registerItem(registry, new IceMagusRodItem());
		registerItem(registry, new InfernalChaliceItem());
		registerItem(registry, new InfernalClawsItem());
		registerItem(registry, new InfernalTearItem());
		registerItem(registry, new KrakenShellItem());
		registerItem(registry, new LanternOfParanoiaItem());
		registerItem(registry, new MagicbaneItem());
		registerItem(registry, new MidasTouchstoneItem());

		registerItem(registry, new ItemBase("rib_bone", new Item.Properties()));
		registerItem(registry, new ItemBase("withered_rib", new Item.Properties()));
		registerItem(registry, new ItemBase("chelicerae", new Item.Properties()));
		registerItem(registry, new ItemBase("catalyzing_gland", new Item.Properties()));
		registerItem(registry, new ItemBase("slime_pearl", new Item.Properties()));
		registerItem(registry, new ItemBase("bat_wing", new Item.Properties()));
		registerItem(registry, new ItemBase("zombie_heart", new Item.Properties()));
		registerItem(registry, new ItemBase("molten_core", new Item.Properties()));
		registerItem(registry, new ItemBase("eye_of_the_storm", new Item.Properties()));
		registerItem(registry, new ItemBase("fertile_essence", new Item.Properties()));
		registerItem(registry, new ItemBase("frozen_core", new Item.Properties()));
		registerItem(registry, new ItemBase("nebulous_heart", new Item.Properties()));
		registerItem(registry, new ItemBase("squid_beak", new Item.Properties()));
		registerItem(registry, new ItemBase("infernal_claw", new Item.Properties()));
		registerItem(registry, new ItemBase("kraken_shell_fragment", new Item.Properties()));
		registerItem(registry, new ItemBase("crimson_cloth", new Item.Properties()));
		registerItem(registry, new ItemBase("guardian_spike", new Item.Properties()));
		registerItem(registry, new MobCharmFragmentItem());
		registerItem(registry, new MobCharmItem(), false);
		registerItem(registry, new MobCharmBeltItem());

		registerItem(registry, new PhoenixDownItem());
		registerItem(registry, new PyromancerStaffItem());
		registerItem(registry, new RendingGaleItem());
		registerItem(registry, new RodOfLyssaItem());
		registerItem(registry, new SalamanderEyeItem());
		registerItem(registry, new SerpentStaffItem());
		registerItem(registry, new ShearsOfWinterItem());
		registerItem(registry, new SojournerStaffItem());
		registerItem(registry, new TwilightCloakItem());
		registerItem(registry, new VoidTearItem());
		registerItem(registry, new WitchHatItem());
		registerItem(registry, new WitherlessRoseItem());
		if (Boolean.FALSE.equals(Settings.COMMON.disable.disableHandgun.get())) {
			registerItem(registry, new BulletItem(EMPTY_BULLET_REGISTRY_NAME, false, false, Integer.parseInt(Colors.DARKEST, 16)));
			registerItem(registry, new BulletItem(NEUTRAL_BULLET_REGISTRY_NAME, false, true, Integer.parseInt(Colors.NEUTRAL_SHOT_COLOR, 16)));
			registerItem(registry, new BulletItem(EXORCISM_BULLET_REGISTRY_NAME, true, false, Integer.parseInt(Colors.EXORCISM_SHOT_COLOR, 16)));
			registerItem(registry, new BulletItem(BLAZE_BULLET_REGISTRY_NAME, true, false, Integer.parseInt(Colors.BLAZE_SHOT_COLOR, 16)));
			registerItem(registry, new BulletItem(ENDER_BULLET_REGISTRY_NAME, true, false, Integer.parseInt(Colors.ENDER_SHOT_COLOR, 16)));
			registerItem(registry, new BulletItem(CONCUSSIVE_BULLET_REGISTRY_NAME, true, false, Integer.parseInt(Colors.CONCUSSIVE_SHOT_COLOR, 16)));
			registerItem(registry, new BulletItem(BUSTER_BULLET_REGISTRY_NAME, true, false, Integer.parseInt(Colors.BUSTER_SHOT_COLOR, 16)));
			registerItem(registry, new BulletItem(SEEKER_BULLET_REGISTRY_NAME, true, false, Integer.parseInt(Colors.SEEKER_SHOT_COLOR, 16)));
			registerItem(registry, new BulletItem(SAND_BULLET_REGISTRY_NAME, true, false, Integer.parseInt(Colors.SAND_SHOT_COLOR, 16)));
			registerItem(registry, new BulletItem(STORM_BULLET_REGISTRY_NAME, true, false, Integer.parseInt(Colors.STORM_SHOT_COLOR, 16)));

			registerItem(registry, new MagazineItem(EMPTY_MAGAZINE_REGISTRY_NAME, false, Integer.parseInt(Colors.DARKEST, 16)));
			registerItem(registry, new MagazineItem(NEUTRAL_MAGAZINE_REGISTRY_NAME, true, Integer.parseInt(Colors.NEUTRAL_SHOT_COLOR, 16)));
			registerItem(registry, new MagazineItem(EXORCISM_MAGAZINE_REGISTRY_NAME, false, Integer.parseInt(Colors.EXORCISM_SHOT_COLOR, 16)));
			registerItem(registry, new MagazineItem(BLAZE_MAGAZINE_REGISTRY_NAME, false, Integer.parseInt(Colors.BLAZE_SHOT_COLOR, 16)));
			registerItem(registry, new MagazineItem(ENDER_MAGAZINE_REGISTRY_NAME, false, Integer.parseInt(Colors.ENDER_SHOT_COLOR, 16)));
			registerItem(registry, new MagazineItem(CONCUSSIVE_MAGAZINE_REGISTRY_NAME, false, Integer.parseInt(Colors.CONCUSSIVE_SHOT_COLOR, 16)));
			registerItem(registry, new MagazineItem(BUSTER_MAGAZINE_REGISTRY_NAME, false, Integer.parseInt(Colors.BUSTER_SHOT_COLOR, 16)));
			registerItem(registry, new MagazineItem(SEEKER_MAGAZINE_REGISTRY_NAME, false, Integer.parseInt(Colors.SEEKER_SHOT_COLOR, 16)));
			registerItem(registry, new MagazineItem(SAND_MAGAZINE_REGISTRY_NAME, false, Integer.parseInt(Colors.SAND_SHOT_COLOR, 16)));
			registerItem(registry, new MagazineItem(STORM_MAGAZINE_REGISTRY_NAME, false, Integer.parseInt(Colors.STORM_SHOT_COLOR, 16)));

			registerItem(registry, new ItemBase("grip_assembly", new Item.Properties().maxStackSize(4)));
			registerItem(registry, new ItemBase("barrel_assembly", new Item.Properties().maxStackSize(4)));
			registerItem(registry, new ItemBase("hammer_assembly", new Item.Properties().maxStackSize(4)));
			registerItem(registry, new HandgunItem());
		}
		if (Boolean.FALSE.equals(Settings.COMMON.disable.disablePotions.get())) {
			registerItem(registry, new AttractionPotionItem());
			registerItem(registry, new FertilePotionItem());
			registerItem(registry, new PotionEssenceItem(), false);
			registerItem(registry, new PotionItem(), false);
			registerItem(registry, new TippedArrowItem(), false);
		}
	}

	public static void registerDispenseBehaviors() {
		if (Boolean.FALSE.equals(Settings.COMMON.disable.disablePotions.get())) {
			DispenserBlock.registerDispenseBehavior(ModItems.POTION, new BehaviorDefaultProjectileDispense() {
				@Override
				ProjectileEntityFactory getProjectileEntityFactory() {
					return (world, position, stack) -> new ThrownXRPotionEntity(world, position.getX(), position.getY(), position.getZ(), stack);
				}

				@Override
				boolean canShoot(ItemStack stack) {
					return ModItems.POTION.isSplash(stack) || ModItems.POTION.isLingering(stack);
				}
			});

			DispenserBlock.registerDispenseBehavior(ModItems.ATTRACTION_POTION, new BehaviorDefaultProjectileDispense() {
				@Override
				ProjectileEntityFactory getProjectileEntityFactory() {
					return (world, position, stack) -> new AttractionPotionEntity(world, position.getX(), position.getY(), position.getZ());
				}
			});

			DispenserBlock.registerDispenseBehavior(ModItems.FERTILE_POTION, new BehaviorDefaultProjectileDispense() {
				@Override
				ProjectileEntityFactory getProjectileEntityFactory() {
					return (world, position, stack) -> new FertilePotionEntity(world, position.getX(), position.getY(), position.getZ());
				}
			});

			DispenserBlock.registerDispenseBehavior(ModItems.TIPPED_ARROW, new ProjectileDispenseBehavior() {

				@Override
				protected IProjectile getProjectileEntity(World world, IPosition position, ItemStack stack) {
					XRTippedArrowEntity entitytippedarrow = new XRTippedArrowEntity(world, position.getX(), position.getY(), position.getZ());
					entitytippedarrow.setPotionEffect(stack);
					entitytippedarrow.pickupStatus = AbstractArrowEntity.PickupStatus.ALLOWED;
					return entitytippedarrow;
				}
			});
		}
		DispenserBlock.registerDispenseBehavior(ModItems.GLOWING_WATER, new BehaviorDefaultProjectileDispense() {
			@Override
			ProjectileEntityFactory getProjectileEntityFactory() {
				return (world, position, stack) -> new GlowingWaterEntity(world, position.getX(), position.getY(), position.getZ());
			}
		});

		DispenserBlock.registerDispenseBehavior(ModItems.HOLY_HAND_GRENADE, new BehaviorDefaultProjectileDispense() {
			@Override
			ProjectileEntityFactory getProjectileEntityFactory() {
				return (world, position, stack) -> new HolyHandGrenadeEntity(world, position.getX(), position.getY(), position.getZ());
			}
		});

		DispenserBlock.registerDispenseBehavior(ModItems.HOLY_HAND_GRENADE, new BehaviorDefaultProjectileDispense() {
			@Override
			ProjectileEntityFactory getProjectileEntityFactory() {
				return (world, position, stack) -> new HolyHandGrenadeEntity(world, position.getX(), position.getY(), position.getZ());
			}
		});

	}

	private static void registerItem(IForgeRegistry<Item> registry, Item item) {
		registerItem(registry, item, true);
	}

	private static void registerItem(IForgeRegistry<Item> registry, Item item, boolean registerInJEI) {
		registry.register(item);

		if (registerInJEI) {
			//noinspection ConstantConditions
			Reliquary.proxy.registerJEI(item, item.getRegistryName().getPath());
		}
	}

	@SubscribeEvent
	public static void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> evt) {
		CraftingHelper.register(MobDropsCraftableCondition.Serializer.INSTANCE);

		evt.getRegistry().register(MobCharmRepairRecipe.SERIALIZER.setRegistryName(new ResourceLocation(Reference.MOD_ID, "mob_charm_repair")));
		evt.getRegistry().register(AlkahestryChargingRecipe.SERIALIZER.setRegistryName(new ResourceLocation(Reference.MOD_ID, "alkahestry_charging")));
		evt.getRegistry().register(AlkahestryCraftingRecipe.SERIALIZER.setRegistryName(new ResourceLocation(Reference.MOD_ID, "alkahestry_crafting")));
		evt.getRegistry().register(AlkahestryDrainRecipe.SERIALIZER.setRegistryName(new ResourceLocation(Reference.MOD_ID, "alkahestry_drain")));
		evt.getRegistry().register(PotionEffectsRecipe.SERIALIZER.setRegistryName(new ResourceLocation(Reference.MOD_ID, "potion_effects")));
	}

	public static void registerHandgunMagazines() {
		HANDGUN.registerMagazine(Reference.MOD_ID + ":" + NEUTRAL_MAGAZINE_REGISTRY_NAME, NeutralShotEntity::new, () -> NEUTRAL_BULLET);
		HANDGUN.registerMagazine(Reference.MOD_ID + ":" + EXORCISM_MAGAZINE_REGISTRY_NAME, ExorcismShotEntity::new, () -> EXORCISM_BULLET);
		HANDGUN.registerMagazine(Reference.MOD_ID + ":" + BLAZE_MAGAZINE_REGISTRY_NAME, BlazeShotEntity::new, () -> BLAZE_BULLET);
		HANDGUN.registerMagazine(Reference.MOD_ID + ":" + ENDER_MAGAZINE_REGISTRY_NAME, EnderShotEntity::new, () -> ENDER_BULLET);
		HANDGUN.registerMagazine(Reference.MOD_ID + ":" + CONCUSSIVE_MAGAZINE_REGISTRY_NAME, ConcussiveShotEntity::new, () -> CONCUSSIVE_BULLET);
		HANDGUN.registerMagazine(Reference.MOD_ID + ":" + BUSTER_MAGAZINE_REGISTRY_NAME, BusterShotEntity::new, () -> BUSTER_BULLET);
		HANDGUN.registerMagazine(Reference.MOD_ID + ":" + SEEKER_MAGAZINE_REGISTRY_NAME, SeekerShotEntity::new, () -> SEEKER_BULLET);
		HANDGUN.registerMagazine(Reference.MOD_ID + ":" + SAND_MAGAZINE_REGISTRY_NAME, SandShotEntity::new, () -> SAND_BULLET);
		HANDGUN.registerMagazine(Reference.MOD_ID + ":" + STORM_MAGAZINE_REGISTRY_NAME, StormShotEntity::new, () -> STORM_BULLET);
	}

	public static boolean isEnabled(Item... items) {
		for (Item item : items) {
			if (item == null || item.getRegistryName() == null) {
				return false;
			}
		}
		return true;
	}

	private abstract static class BehaviorDefaultProjectileDispense implements IDispenseItemBehavior {

		abstract ProjectileEntityFactory getProjectileEntityFactory();

		@SuppressWarnings("squid:S1172")
		boolean canShoot(ItemStack stack) {
			return true;
		}

		@Override
		public ItemStack dispense(IBlockSource source, ItemStack stack) {
			if (!canShoot(stack)) {
				return new DefaultDispenseItemBehavior().dispense(source, stack);
			}

			return (new ProjectileDispenseBehavior() {

				@Override
				protected IProjectile getProjectileEntity(World world, IPosition position, ItemStack stack) {
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
		IProjectile createProjectileEntity(World world, IPosition position, ItemStack stack);
	}

}
