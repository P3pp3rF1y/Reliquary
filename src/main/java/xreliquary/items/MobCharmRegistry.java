package xreliquary.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.registries.ForgeRegistries;
import xreliquary.init.ModItems;
import xreliquary.reference.Settings;
import xreliquary.util.RegistryHelper;

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
		registerMobCharmDefinition(PIGLIN);
		registerMobCharmDefinition(PIGLIN_BRUTE);
	}

	static Optional<MobCharmDefinition> getCharmDefinitionFor(Entity entity) {
		return Optional.ofNullable(ENTITY_NAME_CHARM_DEFINITIONS.get(RegistryHelper.getRegistryName(entity.getType()).toString()));
	}

	public static Optional<MobCharmDefinition> getCharmDefinitionFor(ItemStack stack) {
		if (stack.getItem() != ModItems.MOB_CHARM.get()) {
			return Optional.empty();
		}

		return Optional.ofNullable(ENTITY_NAME_CHARM_DEFINITIONS.get(MobCharmItem.getEntityRegistryName(stack)));
	}

	public static Set<String> getRegisteredNames() {
		return REGISTERED_CHARM_DEFINITIONS.keySet();
	}

	public static void registerDynamicCharmDefinitions() {
		for (EntityType<?> entityType : ForgeRegistries.ENTITIES) {
			String registryName = RegistryHelper.getRegistryName(entityType).toString();
			Set<String> blockedEntities = new HashSet<>(Settings.COMMON.items.mobCharm.entityBlockList.get());
			if (!ENTITY_NAME_CHARM_DEFINITIONS.containsKey(registryName) && entityType.getClassification() == EntityClassification.MONSTER && !blockedEntities.contains(registryName)) {
				registerMobCharmDefinition(new MobCharmDefinition(registryName));
				DYNAMICALLY_REGISTERED.add(registryName);
			}
		}
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

		double dynamicDropChance = Settings.COMMON.items.mobCharmFragment.dropChance.get() + evt.getLootingLevel() * Settings.COMMON.items.mobCharmFragment.lootingMultiplier.get();

		if (entity.world.rand.nextFloat() < dynamicDropChance) {
			ItemEntity fragmentItemEntity = new ItemEntity(entity.world, entity.getPosX(), entity.getPosY(), entity.getPosZ(), ModItems.MOB_CHARM_FRAGMENT.get().getStackFor(regName.toString()));
			fragmentItemEntity.setDefaultPickupDelay();

			evt.getDrops().add(fragmentItemEntity);
		}
	}
}
