package xreliquary.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.registries.ForgeRegistries;
import xreliquary.init.ModItems;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static xreliquary.items.MobCharmDefinition.*;

public class MobCharmRegistry {
	private MobCharmRegistry() {}

	private static final Map<String, MobCharmDefinition> REGISTERED_CHARM_DEFINITIONS = new HashMap<>();
	private static final Map<String, MobCharmDefinition> ENTITY_NAME_CHARM_DEFINITIONS = new HashMap<>();
	private static final Set<String> DYNAMICALLY_REGISTERED = new HashSet<>();

	public static void registerMobCharmDefinition(MobCharmDefinition charmDefinition) {
		REGISTERED_CHARM_DEFINITIONS.put(charmDefinition.getRegistryName(), charmDefinition);
		for (String registryName : charmDefinition.getEntities()) {
			ENTITY_NAME_CHARM_DEFINITIONS.put(registryName, charmDefinition);
		}
	}

	static {
		registerMobCharmDefinition(ZOMBIE);
		registerMobCharmDefinition(SKELETON);
		registerMobCharmDefinition(WITHER_SKELETON);
		registerMobCharmDefinition(CREEPER);
		registerMobCharmDefinition(WITCH);
		registerMobCharmDefinition(ZOMBIFIED_PIGLIN);
		registerMobCharmDefinition(CAVE_SPIDER);
		registerMobCharmDefinition(SPIDER);
		registerMobCharmDefinition(ENDERMAN);
		registerMobCharmDefinition(GHAST);
		registerMobCharmDefinition(SLIME);
		registerMobCharmDefinition(MAGMA_CUBE);
		registerMobCharmDefinition(BLAZE);
		registerMobCharmDefinition(GUARDIAN);
	}

	static Optional<MobCharmDefinition> getCharmDefinitionFor(Entity entity) {
		//noinspection ConstantConditions
		return Optional.ofNullable(ENTITY_NAME_CHARM_DEFINITIONS.get(entity.getType().getRegistryName().toString()));
	}

	public static Optional<MobCharmDefinition> getCharmDefinitionFor(ItemStack stack) {
		if (stack.getItem() != ModItems.MOB_CHARM) {
			return Optional.empty();
		}

		return Optional.ofNullable(ENTITY_NAME_CHARM_DEFINITIONS.get(MobCharmItem.getEntityRegistryName(stack)));
	}

	public static Set<String> getRegisteredNames() {
		return REGISTERED_CHARM_DEFINITIONS.keySet();
	}

	public static void registerDynamicCharmDefinitions(WorldEvent.Load event) {
		for (EntityType<?> entityType : ForgeRegistries.ENTITIES) {
			IWorld world = event.getWorld();
			//noinspection ConstantConditions
			String registryName = entityType.getRegistryName().toString();
			if (!ENTITY_NAME_CHARM_DEFINITIONS.containsKey(registryName) && entityType.getClassification() == EntityClassification.MONSTER
					&& world instanceof World && isNonBoss(entityType, (World) world)) {
				registerMobCharmDefinition(new MobCharmDefinition(registryName));
				DYNAMICALLY_REGISTERED.add(registryName);
			}
		}
	}

	private static boolean isNonBoss(EntityType<?> entityType, World world) {
		Entity e = entityType.create(world);
		return e != null && e.isNonBoss();
	}

	public static void handleAddingFragmentDrops(LivingDropsEvent evt) {
		if (!evt.getSource().getDamageType().equals("player")) {
			return;
		}

		LivingEntity entity = evt.getEntityLiving();
		ResourceLocation regName = entity.getType().getRegistryName();
		if (regName == null || !DYNAMICALLY_REGISTERED.contains(regName.toString())) {
			return;
		}

		double skeletonDropChance = 0.1 * evt.getLootingLevel() * 0.05;
		double dynamicDropChance = skeletonDropChance / 6d;

		if (entity.world.rand.nextFloat() < dynamicDropChance) {
			ItemEntity fragmentItemEntity = new ItemEntity(entity.world, entity.getPosX(), entity.getPosY(), entity.getPosZ(), ModItems.MOB_CHARM_FRAGMENT.getStackFor(regName.toString()));
			fragmentItemEntity.setDefaultPickupDelay();

			evt.getDrops().add(fragmentItemEntity);
		}
	}
}
