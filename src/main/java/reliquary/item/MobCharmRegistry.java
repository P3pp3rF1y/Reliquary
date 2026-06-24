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
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import reliquary.data.ReliquaryEnchantmentProvider;
import reliquary.init.ModItems;
import reliquary.reference.Config;
import reliquary.util.RegistryHelper;

import java.util.*;

import static reliquary.item.MobCharmDefinition.*;

public class MobCharmRegistry {
	private MobCharmRegistry() {
	}

	private static final Map<Identifier, MobCharmDefinition> REGISTERED_CHARM_DEFINITIONS = new HashMap<>();
	private static final Map<Identifier, MobCharmDefinition> ENTITY_NAME_CHARM_DEFINITIONS = new HashMap<>();
	private static final Set<Identifier> DYNAMICALLY_REGISTERED = new HashSet<>();

	public static void registerMobCharmDefinition(MobCharmDefinition charmDefinition) {
		REGISTERED_CHARM_DEFINITIONS.put(charmDefinition.getRegistryName(), charmDefinition);
		for (Identifier registryName : charmDefinition.getEntities()) {
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
		return Optional.ofNullable(ENTITY_NAME_CHARM_DEFINITIONS.get(RegistryHelper.getRegistryName(entity)));
	}

	public static Optional<MobCharmDefinition> getCharmDefinitionFor(ItemStack stack) {
		if (stack.getItem() != ModItems.MOB_CHARM.get()) {
			return Optional.empty();
		}

		return Optional.ofNullable(ENTITY_NAME_CHARM_DEFINITIONS.get(MobCharmItem.getEntityEggRegistryName(stack)));
	}

	public static Set<Identifier> getRegisteredNames() {
		return REGISTERED_CHARM_DEFINITIONS.keySet();
	}

	public static void registerDynamicCharmDefinitions() {
		for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
			Identifier registryName = EntityType.getKey(entityType);
			if (!ENTITY_NAME_CHARM_DEFINITIONS.containsKey(registryName) && entityType.getCategory() == MobCategory.MONSTER
					&& !Config.COMMON.items.mobCharm.isBlockedEntity(registryName)) {
				registerMobCharmDefinition(new MobCharmDefinition(entityType));
				DYNAMICALLY_REGISTERED.add(registryName);
			}
		}
	}

	public static void handleAddingFragmentDrops(LivingDropsEvent evt) {
		if (Boolean.TRUE.equals(Config.COMMON.disable.disableCharms.get()) || !(evt.getSource().getDirectEntity() instanceof Player player)) {
			return;
		}

		LivingEntity entity = evt.getEntity();
		Identifier regName = RegistryHelper.getRegistryName(entity);
		if (!DYNAMICALLY_REGISTERED.contains(regName)) {
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
}
