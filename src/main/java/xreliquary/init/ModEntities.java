package xreliquary.init;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
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
import xreliquary.entities.shot.ShotEntityBase;
import xreliquary.entities.shot.StormShotEntity;
import xreliquary.reference.Reference;
import xreliquary.util.InjectionHelper;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Reference.MOD_ID)
public class ModEntities {
	public static final EntityType<AttractionPotionEntity> APHRODITE_POTION = InjectionHelper.nullValue();
	public static final EntityType<FertilePotionEntity> FERTILE_POTION = InjectionHelper.nullValue();
	public static final EntityType<ThrownXRPotionEntity> THROWN_POTION = InjectionHelper.nullValue();
	public static final EntityType<BlazeShotEntity> BLAZE_SHOT = InjectionHelper.nullValue();
	public static final EntityType<BusterShotEntity> BUSTER_SHOT = InjectionHelper.nullValue();
	public static final EntityType<ConcussiveShotEntity> CONCUSSIVE_SHOT = InjectionHelper.nullValue();
	public static final EntityType<EnderShotEntity> ENDER_SHOT = InjectionHelper.nullValue();
	public static final EntityType<ExorcismShotEntity> EXORCISM_SHOT = InjectionHelper.nullValue();
	public static final EntityType<NeutralShotEntity> NEUTRAL_SHOT = InjectionHelper.nullValue();
	public static final EntityType<SandShotEntity> SAND_SHOT = InjectionHelper.nullValue();
	public static final EntityType<SeekerShotEntity> SEEKER_SHOT = InjectionHelper.nullValue();
	public static final EntityType<StormShotEntity> STORM_SHOT = InjectionHelper.nullValue();
	public static final EntityType<EnderStaffProjectileEntity> ENDER_STAFF_PROJECTILE = InjectionHelper.nullValue();
	public static final EntityType<GlowingWaterEntity> GLOWING_WATER = InjectionHelper.nullValue();
	public static final EntityType<HolyHandGrenadeEntity> HOLY_HAND_GRENADE = InjectionHelper.nullValue();
	public static final EntityType<KrakenSlimeEntity> KRAKEN_SLIME = InjectionHelper.nullValue();
	public static final EntityType<LyssaBobberEntity> LYSSA_HOOK = InjectionHelper.nullValue();
	public static final EntityType<XRTippedArrowEntity> TIPPED_ARROW = InjectionHelper.nullValue();
	public static final EntityType<SpecialSnowballEntity> SPECIAL_SNOWBALL = InjectionHelper.nullValue();

	private ModEntities() {}

	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityType<?>> evt) {
		IForgeRegistry<EntityType<?>> registry = evt.getRegistry();

		ModEntities.<AttractionPotionEntity>registerDefaultSizeEntity(registry, AttractionPotionEntity::new, "aphrodite_potion");
		ModEntities.<FertilePotionEntity>registerDefaultSizeEntity(registry, FertilePotionEntity::new, "fertile_potion");
		ModEntities.<ThrownXRPotionEntity>registerDefaultSizeEntity(registry, ThrownXRPotionEntity::new, "thrown_potion");

		ModEntities.<BlazeShotEntity>registerShotEntity(registry, BlazeShotEntity::new, "blaze_shot");
		ModEntities.<BusterShotEntity>registerShotEntity(registry, BusterShotEntity::new, "buster_shot");
		ModEntities.<ConcussiveShotEntity>registerShotEntity(registry, ConcussiveShotEntity::new, "concussive_shot");
		ModEntities.<EnderShotEntity>registerShotEntity(registry, EnderShotEntity::new, "ender_shot");
		ModEntities.<ExorcismShotEntity>registerShotEntity(registry, ExorcismShotEntity::new, "exorcism_shot");
		ModEntities.<NeutralShotEntity>registerShotEntity(registry, NeutralShotEntity::new, "neutral_shot");
		ModEntities.<SandShotEntity>registerShotEntity(registry, SandShotEntity::new, "sand_shot");
		ModEntities.<SeekerShotEntity>registerShotEntity(registry, SeekerShotEntity::new, "seeker_shot");
		ModEntities.<StormShotEntity>registerShotEntity(registry, StormShotEntity::new, "storm_shot");

		ModEntities.<EnderStaffProjectileEntity>registerEntity(registry, EnderStaffProjectileEntity::new, "ender_staff_projectile", 0.25F, 0.25F, 256);
		ModEntities.<GlowingWaterEntity>registerDefaultSizeEntity(registry, GlowingWaterEntity::new, "glowing_water");
		ModEntities.<HolyHandGrenadeEntity>registerDefaultSizeEntity(registry, HolyHandGrenadeEntity::new, "holy_hand_grenade");
		ModEntities.<KrakenSlimeEntity>registerDefaultSizeEntity(registry, KrakenSlimeEntity::new, "kraken_slime");
		ModEntities.<LyssaBobberEntity>registerDefaultSizeEntity(registry, LyssaBobberEntity::new, "lyssa_hook");
		ModEntities.<XRTippedArrowEntity>registerDefaultSizeEntity(registry, XRTippedArrowEntity::new, "tipped_arrow");
		ModEntities.<SpecialSnowballEntity>registerEntity(registry, SpecialSnowballEntity::new, "special_snowball", 0.01F, 0.01F);
	}

	private static <T extends Entity> void registerDefaultSizeEntity(IForgeRegistry<EntityType<?>> registry, EntityType.IFactory<T> factory, String registryName) {
		registerEntity(registry, factory, registryName, 0.25F, 0.25F);
	}

	private static <T extends ShotEntityBase> void registerShotEntity(IForgeRegistry<EntityType<?>> registry, EntityType.IFactory<T> factory, String registryName) {
		registerEntity(registry, factory, registryName, 0.01F, 0.01F);
	}

	private static <T extends Entity> void registerEntity(IForgeRegistry<EntityType<?>> registry, EntityType.IFactory<T> factory, String registryName, float width, float height) {
		registerEntity(registry, factory, registryName, width, height, 128);
	}

	private static <T extends Entity> void registerEntity(IForgeRegistry<EntityType<?>> registry, EntityType.IFactory<T> factory, String registryName, float width, float height, int trackingRange) {
		registry.register(EntityType.Builder.create(factory, EntityClassification.MISC)
				.size(width, height).setUpdateInterval(5).setTrackingRange(trackingRange).setShouldReceiveVelocityUpdates(true)
				.build("").setRegistryName(new ResourceLocation(Reference.MOD_ID, registryName)));
	}
}
