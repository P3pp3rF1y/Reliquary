package reliquary.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import reliquary.Reliquary;
import reliquary.entity.*;
import reliquary.entity.potion.AphroditePotion;
import reliquary.entity.potion.FertilePotion;
import reliquary.entity.potion.ThrownPotion;
import reliquary.entity.shot.*;

import java.util.function.Supplier;

public class ModEntities {
	public static final TagKey<EntityType<?>> IGNORED_BY_INTERDICTION_TORCH_TAG = TagKey.create(Registries.ENTITY_TYPE,
			Reliquary.getRL("ignored_by_interdiction_torch"));

	private static final DeferredRegister.Entities ENTITY_TYPES = DeferredRegister.createEntities(Reliquary.MOD_ID);

	public static final Supplier<EntityType<AphroditePotion>> APHRODITE_POTION = ENTITY_TYPES.registerEntityType("aphrodite_potion", AphroditePotion::new,
			MobCategory.MISC, builder -> builder.sized(0.25F, 0.25F));
	public static final Supplier<EntityType<FertilePotion>> FERTILE_POTION = ENTITY_TYPES.registerEntityType("fertile_potion", FertilePotion::new,
			MobCategory.MISC, builder -> builder.sized(0.25F, 0.25F));
	public static final Supplier<EntityType<ThrownPotion>> THROWN_POTION = ENTITY_TYPES.registerEntityType("thrown_potion", ThrownPotion::new, MobCategory.MISC,
			builder -> builder.sized(0.25F, 0.25F));
	public static final Supplier<EntityType<BlazeShot>> BLAZE_SHOT = ENTITY_TYPES.registerEntityType("blaze_shot", BlazeShot::new, MobCategory.MISC,
			builder -> builder.sized(0.01F, 0.01F));
	public static final Supplier<EntityType<BusterShot>> BUSTER_SHOT = ENTITY_TYPES.registerEntityType("buster_shot", BusterShot::new, MobCategory.MISC,
			builder -> builder.sized(0.01F, 0.01F));
	public static final Supplier<EntityType<ConcussiveShot>> CONCUSSIVE_SHOT = ENTITY_TYPES.registerEntityType("concussive_shot", ConcussiveShot::new,
			MobCategory.MISC, builder -> builder.sized(0.01F, 0.01F));
	public static final Supplier<EntityType<EnderShot>> ENDER_SHOT = ENTITY_TYPES.registerEntityType("ender_shot", EnderShot::new, MobCategory.MISC,
			builder -> builder.sized(0.01F, 0.01F));
	public static final Supplier<EntityType<ExorcismShot>> EXORCISM_SHOT = ENTITY_TYPES.registerEntityType("exorcism_shot", ExorcismShot::new, MobCategory.MISC,
			builder -> builder.sized(0.01F, 0.01F));
	public static final Supplier<EntityType<NeutralShot>> NEUTRAL_SHOT = ENTITY_TYPES.registerEntityType("neutral_shot", NeutralShot::new, MobCategory.MISC,
			builder -> builder.sized(0.01F, 0.01F));
	public static final Supplier<EntityType<SandShot>> SAND_SHOT = ENTITY_TYPES.registerEntityType("sand_shot", SandShot::new, MobCategory.MISC,
			builder -> builder.sized(0.01F, 0.01F));
	public static final Supplier<EntityType<SeekerShot>> SEEKER_SHOT = ENTITY_TYPES.registerEntityType("seeker_shot", SeekerShot::new, MobCategory.MISC,
			builder -> builder.sized(0.01F, 0.01F));
	public static final Supplier<EntityType<StormShot>> STORM_SHOT = ENTITY_TYPES.registerEntityType("storm_shot", StormShot::new, MobCategory.MISC,
			builder -> builder.sized(0.01F, 0.01F));
	public static final Supplier<EntityType<EnderStaffProjectile>> ENDER_STAFF_PROJECTILE = ENTITY_TYPES.registerEntityType("ender_staff_projectile",
			EnderStaffProjectile::new, MobCategory.MISC, builder -> builder.sized(0.25F, 0.25F).setTrackingRange(256));
	public static final Supplier<EntityType<GlowingWater>> GLOWING_WATER = ENTITY_TYPES.registerEntityType("glowing_water", GlowingWater::new, MobCategory.MISC,
			builder -> builder.sized(0.25F, 0.25F));
	public static final Supplier<EntityType<HolyHandGrenade>> HOLY_HAND_GRENADE = ENTITY_TYPES.registerEntityType("holy_hand_grenade", HolyHandGrenade::new,
			MobCategory.MISC, builder -> builder.sized(0.25F, 0.25F));
	public static final Supplier<EntityType<KrakenSlime>> KRAKEN_SLIME = ENTITY_TYPES.registerEntityType("kraken_slime", KrakenSlime::new, MobCategory.MISC,
			builder -> builder.sized(0.25F, 0.25F));
	public static final Supplier<EntityType<LyssaHook>> LYSSA_HOOK = ENTITY_TYPES.registerEntityType("lyssa_hook", LyssaHook::new, MobCategory.MISC,
			builder -> builder.sized(0.25F, 0.25F));
	public static final Supplier<EntityType<TippedArrow>> TIPPED_ARROW = ENTITY_TYPES.registerEntityType("tipped_arrow", TippedArrow::new, MobCategory.MISC,
			builder -> builder.sized(0.25F, 0.25F));
	public static final Supplier<EntityType<SpecialSnowball>> SPECIAL_SNOWBALL = ENTITY_TYPES.registerEntityType("special_snowball", SpecialSnowball::new,
			MobCategory.MISC, builder -> builder.sized(0.01F, 0.01F));

	public static final ResourceKey<DamageType> BULLET_DAMAGE_TYPE = ResourceKey.create(Registries.DAMAGE_TYPE, Reliquary.getRL("bullet"));

	private ModEntities() {
	}

	public static void registerListeners(IEventBus modBus) {
		ENTITY_TYPES.register(modBus);
	}
}
