package reliquary.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.registries.ForgeRegistries;
import reliquary.init.ModEnchantments;
import reliquary.init.ModItems;
import reliquary.reference.Settings;
import reliquary.util.RegistryHelper;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class MobCharmRegistry {
	private MobCharmRegistry() {
	}

	private static final AtomicReference<RegistryState> STATE = new AtomicReference<>(RegistryState.empty());

	public static void registerMobCharmDefinition(MobCharmDefinition charmDefinition) {
		STATE.updateAndGet(state -> state.withCharmDefinition(charmDefinition));
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
		return Optional.ofNullable(STATE.get().entityNameCharmDefinitions().get(RegistryHelper.getRegistryName(entity).toString()));
	}

	public static Optional<MobCharmDefinition> getCharmDefinitionFor(ItemStack stack) {
		if (stack.getItem() != ModItems.MOB_CHARM.get()) {
			return Optional.empty();
		}

		return Optional.ofNullable(STATE.get().entityNameCharmDefinitions().get(MobCharmItem.getEntityRegistryName(stack)));
	}

	public static Set<String> getRegisteredNames() {
		return STATE.get().registeredCharmDefinitions().keySet();
	}

	public static void registerDynamicCharmDefinitions() {
		STATE.updateAndGet(MobCharmRegistry::refreshDynamicDefinitions);
	}

	public static void onTagsUpdated(TagsUpdatedEvent event) {
		if (event.shouldUpdateStaticData()) {
			registerDynamicCharmDefinitions();
		}
	}

	public static void handleAddingFragmentDrops(LivingDropsEvent evt) {
		if (Boolean.TRUE.equals(Settings.COMMON.disable.disableCharms.get()) || !(evt.getSource().getDirectEntity() instanceof Player player)) {
			return;
		}

		LivingEntity entity = evt.getEntity();
		ResourceLocation regName = RegistryHelper.getRegistryName(entity);
		if (!isDynamicallyRegistered(regName.toString())) {
			return;
		}

		int severingLevel = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.SEVERING.get(), player);
		if (player.getMainHandItem().getItem() == ModItems.MAGICBANE.get()) {
			severingLevel += 2;
		}

		double dynamicDropChance = Settings.COMMON.items.mobCharmFragment.dropChance.get()
				+ evt.getLootingLevel() * Settings.COMMON.items.mobCharmFragment.lootingMultiplier.get()
				+ severingLevel * 3 * Settings.COMMON.items.mobCharmFragment.lootingMultiplier.get();

		if (entity.level().random.nextFloat() < dynamicDropChance) {
			ItemEntity fragmentItemEntity = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(),
					ModItems.MOB_CHARM_FRAGMENT.get().getStackFor(regName.toString()));
			fragmentItemEntity.setDefaultPickUpDelay();

			evt.getDrops().add(fragmentItemEntity);
		}
	}

	private static boolean isDynamicallyRegistered(String registryName) {
		return STATE.get().dynamicallyRegistered().containsKey(registryName);
	}

	private static RegistryState refreshDynamicDefinitions(RegistryState currentState) {
		Map<String, MobCharmDefinition> registeredDefinitions = new HashMap<>(currentState.registeredCharmDefinitions());
		Map<String, MobCharmDefinition> entityDefinitions = new HashMap<>(currentState.entityNameCharmDefinitions());
		for (Map.Entry<String, MobCharmDefinition> entry : currentState.dynamicallyRegistered().entrySet()) {
			String registryName = entry.getKey();
			MobCharmDefinition charmDefinition = entry.getValue();
			registeredDefinitions.remove(registryName, charmDefinition);
			for (String entityName : charmDefinition.getEntities()) {
				entityDefinitions.remove(entityName, charmDefinition);
			}
		}

		Map<String, MobCharmDefinition> newDynamicDefinitions = new HashMap<>();
		for (EntityType<?> entityType : ForgeRegistries.ENTITY_TYPES) {
			String registryName = RegistryHelper.getRegistryName(entityType).toString();
			if (!entityDefinitions.containsKey(registryName) && entityType.getCategory() == MobCategory.MONSTER
					&& !Settings.COMMON.items.mobCharm.isBlockedEntity(entityType)) {
				MobCharmDefinition charmDefinition = new MobCharmDefinition(registryName);
				addCharmDefinition(registeredDefinitions, entityDefinitions, charmDefinition);
				newDynamicDefinitions.put(registryName, charmDefinition);
			}
		}

		return new RegistryState(Map.copyOf(registeredDefinitions), Map.copyOf(entityDefinitions), Map.copyOf(newDynamicDefinitions));
	}

	private static void addCharmDefinition(Map<String, MobCharmDefinition> registeredDefinitions, Map<String, MobCharmDefinition> entityDefinitions,
			MobCharmDefinition charmDefinition) {
		registeredDefinitions.put(charmDefinition.getRegistryName(), charmDefinition);
		for (String registryName : charmDefinition.getEntities()) {
			entityDefinitions.put(registryName, charmDefinition);
		}
	}

	private record RegistryState(Map<String, MobCharmDefinition> registeredCharmDefinitions, Map<String, MobCharmDefinition> entityNameCharmDefinitions,
			Map<String, MobCharmDefinition> dynamicallyRegistered) {
		private static RegistryState empty() {
			return new RegistryState(Map.of(), Map.of(), Map.of());
		}

		private RegistryState withCharmDefinition(MobCharmDefinition charmDefinition) {
			Map<String, MobCharmDefinition> registeredDefinitions = new HashMap<>(registeredCharmDefinitions);
			Map<String, MobCharmDefinition> entityDefinitions = new HashMap<>(entityNameCharmDefinitions);
			addCharmDefinition(registeredDefinitions, entityDefinitions, charmDefinition);
			return new RegistryState(Map.copyOf(registeredDefinitions), Map.copyOf(entityDefinitions), dynamicallyRegistered);
		}
	}
}
