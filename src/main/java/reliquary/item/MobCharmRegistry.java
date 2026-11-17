package reliquary.item;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import reliquary.data.ReliquaryEnchantmentProvider;
import reliquary.init.ModItems;
import reliquary.reference.Config;
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
		return Optional.ofNullable(STATE.get().entityNameCharmDefinitions().get(RegistryHelper.getRegistryName(entity)));
	}

	public static Optional<MobCharmDefinition> getCharmDefinitionFor(ItemStack stack) {
		if (stack.getItem() != ModItems.MOB_CHARM.get()) {
			return Optional.empty();
		}

		return Optional.ofNullable(STATE.get().entityNameCharmDefinitions().get(MobCharmItem.getEntityEggRegistryName(stack)));
	}

	public static Set<ResourceLocation> getRegisteredNames() {
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
		if (Boolean.TRUE.equals(Config.COMMON.disable.disableCharms.get()) || !(evt.getSource().getDirectEntity() instanceof Player player)) {
			return;
		}

		LivingEntity entity = evt.getEntity();
		ResourceLocation regName = RegistryHelper.getRegistryName(entity);
		if (!isDynamicallyRegistered(regName)) {
			return;
		}

		HolderLookup.RegistryLookup<Enchantment> registrylookup = entity.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
		int lootingLevel = EnchantmentHelper.getEnchantmentLevel(registrylookup.getOrThrow(Enchantments.LOOTING), player);
		int severingLevel = EnchantmentHelper.getEnchantmentLevel(registrylookup.getOrThrow(ReliquaryEnchantmentProvider.SEVERING), player);

		double dynamicDropChance = Config.COMMON.items.mobCharmFragment.dropChance.get()
				+ lootingLevel * Config.COMMON.items.mobCharmFragment.lootingMultiplier.get()
				+ severingLevel * 3 * Config.COMMON.items.mobCharmFragment.lootingMultiplier.get();

		if (entity.level().random.nextFloat() < dynamicDropChance) {
			ItemEntity fragmentItemEntity = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(),
					ModItems.MOB_CHARM_FRAGMENT.get().getStackFor(regName));
			fragmentItemEntity.setDefaultPickUpDelay();

			evt.getDrops().add(fragmentItemEntity);
		}
	}

	private static boolean isDynamicallyRegistered(ResourceLocation registryName) {
		return STATE.get().dynamicallyRegistered().containsKey(registryName);
	}

	private static RegistryState refreshDynamicDefinitions(RegistryState currentState) {
		Map<ResourceLocation, MobCharmDefinition> registeredDefinitions = new HashMap<>(currentState.registeredCharmDefinitions());
		Map<ResourceLocation, MobCharmDefinition> entityDefinitions = new HashMap<>(currentState.entityNameCharmDefinitions());
		for (Map.Entry<ResourceLocation, MobCharmDefinition> entry : currentState.dynamicallyRegistered().entrySet()) {
			ResourceLocation registryName = entry.getKey();
			MobCharmDefinition charmDefinition = entry.getValue();
			registeredDefinitions.remove(registryName, charmDefinition);
			for (ResourceLocation entityName : charmDefinition.getEntities()) {
				entityDefinitions.remove(entityName, charmDefinition);
			}
		}

		Map<ResourceLocation, MobCharmDefinition> newDynamicDefinitions = new HashMap<>();
		for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
			ResourceLocation registryName = EntityType.getKey(entityType);
			if (!entityDefinitions.containsKey(registryName) && entityType.getCategory() == MobCategory.MONSTER
					&& !Config.COMMON.items.mobCharm.isBlockedEntity(entityType)) {
				MobCharmDefinition charmDefinition = new MobCharmDefinition(entityType);
				addCharmDefinition(registeredDefinitions, entityDefinitions, charmDefinition);
				newDynamicDefinitions.put(registryName, charmDefinition);
			}
		}

		return new RegistryState(Map.copyOf(registeredDefinitions), Map.copyOf(entityDefinitions), Map.copyOf(newDynamicDefinitions));
	}

	private static void addCharmDefinition(Map<ResourceLocation, MobCharmDefinition> registeredDefinitions,
			Map<ResourceLocation, MobCharmDefinition> entityDefinitions, MobCharmDefinition charmDefinition) {
		registeredDefinitions.put(charmDefinition.getRegistryName(), charmDefinition);
		for (ResourceLocation registryName : charmDefinition.getEntities()) {
			entityDefinitions.put(registryName, charmDefinition);
		}
	}

	private record RegistryState(Map<ResourceLocation, MobCharmDefinition> registeredCharmDefinitions,
			Map<ResourceLocation, MobCharmDefinition> entityNameCharmDefinitions, Map<ResourceLocation, MobCharmDefinition> dynamicallyRegistered) {
		private static RegistryState empty() {
			return new RegistryState(Map.of(), Map.of(), Map.of());
		}

		private RegistryState withCharmDefinition(MobCharmDefinition charmDefinition) {
			Map<ResourceLocation, MobCharmDefinition> registeredDefinitions = new HashMap<>(registeredCharmDefinitions);
			Map<ResourceLocation, MobCharmDefinition> entityDefinitions = new HashMap<>(entityNameCharmDefinitions);
			addCharmDefinition(registeredDefinitions, entityDefinitions, charmDefinition);
			return new RegistryState(Map.copyOf(registeredDefinitions), Map.copyOf(entityDefinitions), dynamicallyRegistered);
		}
	}
}
