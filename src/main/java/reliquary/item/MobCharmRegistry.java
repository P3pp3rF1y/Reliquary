package reliquary.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.registries.ForgeRegistries;
import reliquary.init.ModItems;
import reliquary.reference.Settings;
import reliquary.util.RegistryHelper;

import java.util.*;

public class MobCharmRegistry {
	private MobCharmRegistry() {
	}

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
		registerMobCharmDefinition(MobCharmDefinition.ZOMBIE);
		registerMobCharmDefinition(MobCharmDefinition.SKELETON);
		registerMobCharmDefinition(MobCharmDefinition.WITHER_SKELETON);
		registerMobCharmDefinition(MobCharmDefinition.CREEPER);
		registerMobCharmDefinition(MobCharmDefinition.WITCH);
		registerMobCharmDefinition(MobCharmDefinition.ZOMBIFIED_PIGLIN);
		registerMobCharmDefinition(MobCharmDefinition.CAVE_SPIDER);
		registerMobCharmDefinition(MobCharmDefinition.SPIDER);
		registerMobCharmDefinition(MobCharmDefinition.ENDERMAN);
		registerMobCharmDefinition(MobCharmDefinition.GHAST);
		registerMobCharmDefinition(MobCharmDefinition.SLIME);
		registerMobCharmDefinition(MobCharmDefinition.MAGMA_CUBE);
		registerMobCharmDefinition(MobCharmDefinition.BLAZE);
		registerMobCharmDefinition(MobCharmDefinition.GUARDIAN);
	}

	static Optional<MobCharmDefinition> getCharmDefinitionFor(Entity entity) {
		return Optional.ofNullable(ENTITY_NAME_CHARM_DEFINITIONS.get(RegistryHelper.getRegistryName(entity).toString()));
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
		for (EntityType<?> entityType : ForgeRegistries.ENTITY_TYPES) {
			String registryName = RegistryHelper.getRegistryName(entityType).toString();
			Set<String> blockedEntities = new HashSet<>(Settings.COMMON.items.mobCharm.entityBlockList.get());
			if (!ENTITY_NAME_CHARM_DEFINITIONS.containsKey(registryName) && entityType.getCategory() == MobCategory.MONSTER && !blockedEntities.contains(registryName)) {
				registerMobCharmDefinition(new MobCharmDefinition(registryName));
				DYNAMICALLY_REGISTERED.add(registryName);
			}
		}
	}

	public static void handleAddingFragmentDrops(LivingDropsEvent evt) {
		if (Boolean.TRUE.equals(Settings.COMMON.disable.disableCharms.get()) || !evt.getSource().getMsgId().equals("player")) {
			return;
		}

		LivingEntity entity = evt.getEntity();
		ResourceLocation regName = RegistryHelper.getRegistryName(entity);
		if (!DYNAMICALLY_REGISTERED.contains(regName.toString())) {
			return;
		}

		double dynamicDropChance = Settings.COMMON.items.mobCharmFragment.dropChance.get() + evt.getLootingLevel() * Settings.COMMON.items.mobCharmFragment.lootingMultiplier.get();

		if (entity.level().random.nextFloat() < dynamicDropChance) {
			ItemEntity fragmentItemEntity = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), ModItems.MOB_CHARM_FRAGMENT.get().getStackFor(regName.toString()));
			fragmentItemEntity.setDefaultPickUpDelay();

			evt.getDrops().add(fragmentItemEntity);
		}
	}
}
