package reliquary.item;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
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

import static reliquary.item.MobCharmDefinition.*;

public class MobCharmRegistry {
	private MobCharmRegistry() {
	}

	private static final AtomicReference<RegistryState> STATE = new AtomicReference<>(RegistryState.empty());

	public static void registerMobCharmDefinition(MobCharmDefinition charmDefinition) {
		STATE.updateAndGet(state -> state.withCharmDefinition(charmDefinition));
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
		return Optional.ofNullable(STATE.get().entityNameCharmDefinitions().get(RegistryHelper.getRegistryName(entity)));
	}

	public static Optional<MobCharmDefinition> getCharmDefinitionFor(ItemStack stack) {
		if (stack.getItem() != ModItems.MOB_CHARM.get()) {
			return Optional.empty();
		}

		return Optional.ofNullable(STATE.get().entityNameCharmDefinitions().get(MobCharmItem.getEntityEggRegistryName(stack)));
	}

	public static Set<Identifier> getRegisteredNames() {
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
		Identifier regName = RegistryHelper.getRegistryName(entity);
		if (!isDynamicallyRegistered(regName)) {
			return;
		}

		HolderLookup.RegistryLookup<Enchantment> registrylookup = entity.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
		int lootingLevel = EnchantmentHelper.getEnchantmentLevel(registrylookup.getOrThrow(Enchantments.LOOTING), player);
		int severingLevel = EnchantmentHelper.getEnchantmentLevel(registrylookup.getOrThrow(ReliquaryEnchantmentProvider.SEVERING), player);

		double dynamicDropChance = Config.COMMON.items.mobCharmFragment.dropChance.get()
				+ lootingLevel * Config.COMMON.items.mobCharmFragment.lootingMultiplier.get()
				+ severingLevel * 3 * Config.COMMON.items.mobCharmFragment.lootingMultiplier.get();

		if (entity.level().getRandom().nextFloat() < dynamicDropChance) {
			ItemEntity fragmentItemEntity = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(),
					ModItems.MOB_CHARM_FRAGMENT.get().getStackFor(regName));
			fragmentItemEntity.setDefaultPickUpDelay();

			evt.getDrops().add(fragmentItemEntity);
		}
	}

	private static boolean isDynamicallyRegistered(Identifier registryName) {
		return STATE.get().dynamicallyRegistered().containsKey(registryName);
	}

	private static RegistryState refreshDynamicDefinitions(RegistryState currentState) {
		Map<Identifier, MobCharmDefinition> registeredDefinitions = new HashMap<>(currentState.registeredCharmDefinitions());
		Map<Identifier, MobCharmDefinition> entityDefinitions = new HashMap<>(currentState.entityNameCharmDefinitions());
		for (Map.Entry<Identifier, MobCharmDefinition> entry : currentState.dynamicallyRegistered().entrySet()) {
			Identifier registryName = entry.getKey();
			MobCharmDefinition charmDefinition = entry.getValue();
			registeredDefinitions.remove(registryName, charmDefinition);
			for (Identifier entityName : charmDefinition.getEntities()) {
				entityDefinitions.remove(entityName, charmDefinition);
			}
		}

		Map<Identifier, MobCharmDefinition> newDynamicDefinitions = new HashMap<>();
		for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
			Identifier registryName = EntityType.getKey(entityType);
			if (!entityDefinitions.containsKey(registryName) && entityType.getCategory() == MobCategory.MONSTER
					&& !Config.COMMON.items.mobCharm.isBlockedEntity(entityType)) {
				MobCharmDefinition charmDefinition = new MobCharmDefinition(entityType);
				addCharmDefinition(registeredDefinitions, entityDefinitions, charmDefinition);
				newDynamicDefinitions.put(registryName, charmDefinition);
			}
		}

		return new RegistryState(Map.copyOf(registeredDefinitions), Map.copyOf(entityDefinitions), Map.copyOf(newDynamicDefinitions));
	}

	private static void addCharmDefinition(Map<Identifier, MobCharmDefinition> registeredDefinitions, Map<Identifier, MobCharmDefinition> entityDefinitions,
			MobCharmDefinition charmDefinition) {
		registeredDefinitions.put(charmDefinition.getRegistryName(), charmDefinition);
		for (Identifier registryName : charmDefinition.getEntities()) {
			entityDefinitions.put(registryName, charmDefinition);
		}
	}

	private record RegistryState(Map<Identifier, MobCharmDefinition> registeredCharmDefinitions, Map<Identifier, MobCharmDefinition> entityNameCharmDefinitions,
			Map<Identifier, MobCharmDefinition> dynamicallyRegistered) {
		private static RegistryState empty() {
			return new RegistryState(Map.of(), Map.of(), Map.of());
		}

		private RegistryState withCharmDefinition(MobCharmDefinition charmDefinition) {
			Map<Identifier, MobCharmDefinition> registeredDefinitions = new HashMap<>(registeredCharmDefinitions);
			Map<Identifier, MobCharmDefinition> entityDefinitions = new HashMap<>(entityNameCharmDefinitions);
			addCharmDefinition(registeredDefinitions, entityDefinitions, charmDefinition);
			return new RegistryState(Map.copyOf(registeredDefinitions), Map.copyOf(entityDefinitions), dynamicallyRegistered);
		}
	}
}
