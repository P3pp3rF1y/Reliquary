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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import reliquary.init.ModItems;
import reliquary.reference.Config;
import reliquary.util.RegistryHelper;

import java.util.*;

public class MobCharmRegistry {
	private MobCharmRegistry() {
	}

	private static final Map<ResourceLocation, MobCharmDefinition> REGISTERED_CHARM_DEFINITIONS = new HashMap<>();
	private static final Map<ResourceLocation, MobCharmDefinition> ENTITY_NAME_CHARM_DEFINITIONS = new HashMap<>();
	private static final Set<ResourceLocation> DYNAMICALLY_REGISTERED = new HashSet<>();

	public static void registerMobCharmDefinition(MobCharmDefinition charmDefinition) {
		REGISTERED_CHARM_DEFINITIONS.put(charmDefinition.getRegistryName(), charmDefinition);
		for (ResourceLocation registryName : charmDefinition.getEntities()) {
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
		return Optional.ofNullable(ENTITY_NAME_CHARM_DEFINITIONS.get(RegistryHelper.getRegistryName(entity)));
	}

	public static Optional<MobCharmDefinition> getCharmDefinitionFor(ItemStack stack) {
		if (stack.getItem() != ModItems.MOB_CHARM.get()) {
			return Optional.empty();
		}

		return Optional.ofNullable(ENTITY_NAME_CHARM_DEFINITIONS.get(MobCharmItem.getEntityEggRegistryName(stack)));
	}

	public static Set<ResourceLocation> getRegisteredNames() {
		return REGISTERED_CHARM_DEFINITIONS.keySet();
	}

	public static void registerDynamicCharmDefinitions() {
		for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
			ResourceLocation registryName = EntityType.getKey(entityType);
			if (!ENTITY_NAME_CHARM_DEFINITIONS.containsKey(registryName) && entityType.getCategory() == MobCategory.MONSTER && !Config.COMMON.items.mobCharm.isBlockedEntity(registryName)) {
				registerMobCharmDefinition(new MobCharmDefinition(entityType));
				DYNAMICALLY_REGISTERED.add(registryName);
			}
		}
	}

	public static void handleAddingFragmentDrops(LivingDropsEvent evt) {
		if (Boolean.TRUE.equals(Config.COMMON.disable.disableCharms.get()) || !evt.getSource().getMsgId().equals("player")) {
			return;
		}

		LivingEntity entity = evt.getEntity();
		ResourceLocation regName = RegistryHelper.getRegistryName(entity);
		if (!DYNAMICALLY_REGISTERED.contains(regName)) {
			return;
		}

		HolderLookup.RegistryLookup<Enchantment> registrylookup = entity.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
		int lootingLevel = EnchantmentHelper.getEnchantmentLevel(registrylookup.getOrThrow(Enchantments.LOOTING), entity);

		double dynamicDropChance = Config.COMMON.items.mobCharmFragment.dropChance.get() + lootingLevel * Config.COMMON.items.mobCharmFragment.lootingMultiplier.get();

		if (entity.level().random.nextFloat() < dynamicDropChance) {
			ItemEntity fragmentItemEntity = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), ModItems.MOB_CHARM_FRAGMENT.get().getStackFor(regName));
			fragmentItemEntity.setDefaultPickUpDelay();

			evt.getDrops().add(fragmentItemEntity);
		}
	}
}
