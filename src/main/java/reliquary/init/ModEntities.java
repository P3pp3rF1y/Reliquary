package reliquary.init;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import reliquary.entities.EnderStaffProjectileEntity;
import reliquary.entities.GlowingWaterEntity;
import reliquary.entities.HolyHandGrenadeEntity;
import reliquary.entities.KrakenSlimeEntity;
import reliquary.entities.LyssaHook;
import reliquary.entities.SpecialSnowballEntity;
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
import reliquary.entities.shot.ShotEntityBase;
import reliquary.entities.shot.StormShotEntity;
import reliquary.reference.Reference;

public class ModEntities {
	private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Reference.MOD_ID);

	public static final RegistryObject<EntityType<AphroditePotionEntity>> APHRODITE_POTION = ENTITY_TYPES.register("aphrodite_potion", () -> getDefaultSizeEntityType(AphroditePotionEntity::new));
	public static final RegistryObject<EntityType<FertilePotionEntity>> FERTILE_POTION = ENTITY_TYPES.register("fertile_potion", () -> getDefaultSizeEntityType(FertilePotionEntity::new));
	public static final RegistryObject<EntityType<ThrownXRPotionEntity>> THROWN_POTION = ENTITY_TYPES.register("thrown_potion", () -> getDefaultSizeEntityType(ThrownXRPotionEntity::new));
	public static final RegistryObject<EntityType<BlazeShotEntity>> BLAZE_SHOT = ENTITY_TYPES.register("blaze_shot", () -> getShotEntityType(BlazeShotEntity::new));
	public static final RegistryObject<EntityType<BusterShotEntity>> BUSTER_SHOT = ENTITY_TYPES.register("buster_shot", () -> getShotEntityType(BusterShotEntity::new));
	public static final RegistryObject<EntityType<ConcussiveShotEntity>> CONCUSSIVE_SHOT = ENTITY_TYPES.register("concussive_shot", () -> getShotEntityType(ConcussiveShotEntity::new));
	public static final RegistryObject<EntityType<EnderShotEntity>> ENDER_SHOT = ENTITY_TYPES.register("ender_shot", () -> getShotEntityType(EnderShotEntity::new));
	public static final RegistryObject<EntityType<ExorcismShotEntity>> EXORCISM_SHOT = ENTITY_TYPES.register("exorcism_shot", () -> getShotEntityType(ExorcismShotEntity::new));
	public static final RegistryObject<EntityType<NeutralShotEntity>> NEUTRAL_SHOT = ENTITY_TYPES.register("neutral_shot", () -> getShotEntityType(NeutralShotEntity::new));
	public static final RegistryObject<EntityType<SandShotEntity>> SAND_SHOT = ENTITY_TYPES.register("sand_shot", () -> getShotEntityType(SandShotEntity::new));
	public static final RegistryObject<EntityType<SeekerShotEntity>> SEEKER_SHOT = ENTITY_TYPES.register("seeker_shot", () -> getShotEntityType(SeekerShotEntity::new));
	public static final RegistryObject<EntityType<StormShotEntity>> STORM_SHOT = ENTITY_TYPES.register("storm_shot", () -> getShotEntityType(StormShotEntity::new));
	public static final RegistryObject<EntityType<EnderStaffProjectileEntity>> ENDER_STAFF_PROJECTILE = ENTITY_TYPES.register("ender_staff_projectile", () -> getEntityType(EnderStaffProjectileEntity::new, 0.25F, 0.25F, 256));
	public static final RegistryObject<EntityType<GlowingWaterEntity>> GLOWING_WATER = ENTITY_TYPES.register("glowing_water", () -> getDefaultSizeEntityType(GlowingWaterEntity::new));
	public static final RegistryObject<EntityType<HolyHandGrenadeEntity>> HOLY_HAND_GRENADE = ENTITY_TYPES.register("holy_hand_grenade", () -> getDefaultSizeEntityType(HolyHandGrenadeEntity::new));
	public static final RegistryObject<EntityType<KrakenSlimeEntity>> KRAKEN_SLIME = ENTITY_TYPES.register("kraken_slime", () -> getDefaultSizeEntityType(KrakenSlimeEntity::new));
	public static final RegistryObject<EntityType<LyssaHook>> LYSSA_HOOK = ENTITY_TYPES.register("lyssa_hook", () -> getDefaultSizeEntityType(LyssaHook::new));
	public static final RegistryObject<EntityType<XRTippedArrowEntity>> TIPPED_ARROW = ENTITY_TYPES.register("tipped_arrow", () -> getDefaultSizeEntityType(XRTippedArrowEntity::new));
	public static final RegistryObject<EntityType<SpecialSnowballEntity>> SPECIAL_SNOWBALL = ENTITY_TYPES.register("special_snowball", () -> getEntityType(SpecialSnowballEntity::new, 0.01F, 0.01F));

	private ModEntities() {}

	public static void registerListeners(IEventBus modBus) {
		ENTITY_TYPES.register(modBus);
	}

	private static <T extends Entity> EntityType<T> getDefaultSizeEntityType(EntityType.EntityFactory<T> factory) {
		return getEntityType(factory, 0.25F, 0.25F);
	}

	private static <T extends ShotEntityBase> EntityType<T> getShotEntityType(EntityType.EntityFactory<T> factory) {
		return getEntityType(factory, 0.01F, 0.01F);
	}

	private static <T extends Entity> EntityType<T> getEntityType(EntityType.EntityFactory<T> factory, float width, float height) {
		return getEntityType(factory, width, height, 128);
	}

	private static <T extends Entity> EntityType<T> getEntityType(EntityType.EntityFactory<T> factory, float width, float height, int trackingRange) {
		return EntityType.Builder.of(factory, MobCategory.MISC)
				.sized(width, height).updateInterval(5).setTrackingRange(trackingRange).setShouldReceiveVelocityUpdates(true)
				.build("");
	}
}
