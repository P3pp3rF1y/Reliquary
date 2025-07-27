package reliquary.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import reliquary.init.ModItems;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MobCharmDefinition {
	static final MobCharmDefinition ZOMBIE = new MobCharmDefinition(EntityType.ZOMBIE, ModItems.ZOMBIE_HEART.get(), EntityType.HUSK, EntityType.DROWNED, EntityType.ZOMBIE_VILLAGER);
	static final MobCharmDefinition SKELETON = new MobCharmDefinition(EntityType.SKELETON, ModItems.RIB_BONE.get(), EntityType.STRAY);
	static final MobCharmDefinition WITHER_SKELETON = new MobCharmDefinition(EntityType.WITHER_SKELETON, ModItems.WITHERED_RIB.get());
	static final MobCharmDefinition CREEPER = new MobCharmDefinition(EntityType.CREEPER, ModItems.CATALYZING_GLAND.get());
	static final MobCharmDefinition WITCH = new MobCharmDefinition(EntityType.WITCH, ModItems.WITCH_HAT.get());
	static final MobCharmDefinition ZOMBIFIED_PIGLIN = new MobCharmDefinition(EntityType.ZOMBIFIED_PIGLIN, ModItems.ZOMBIE_HEART.get());
	static final MobCharmDefinition CAVE_SPIDER = new MobCharmDefinition(EntityType.CAVE_SPIDER, ModItems.CHELICERAE.get());
	static final MobCharmDefinition SPIDER = new MobCharmDefinition(EntityType.SPIDER, ModItems.CHELICERAE.get());
	static final MobCharmDefinition ENDERMAN = new MobCharmDefinition(EntityType.ENDERMAN, ModItems.NEBULOUS_HEART.get());
	static final MobCharmDefinition GHAST = new MobCharmDefinition(EntityType.GHAST, ModItems.CATALYZING_GLAND.get());
	static final MobCharmDefinition SLIME = new MobCharmDefinition(EntityType.SLIME, ModItems.SLIME_PEARL.get());
	static final MobCharmDefinition MAGMA_CUBE = new MobCharmDefinition(EntityType.MAGMA_CUBE, ModItems.MOLTEN_CORE.get());
	static final MobCharmDefinition BLAZE = new MobCharmDefinition(EntityType.BLAZE, ModItems.MOLTEN_CORE.get());
	static final MobCharmDefinition GUARDIAN = new MobCharmDefinition(EntityType.GUARDIAN, ModItems.GUARDIAN_SPIKE.get());

	private final Set<ResourceLocation> applicableToEntities = new HashSet<>();
	private final ResourceLocation registryName;
	private final Item repairItem;
	private boolean dynamicallyCreated = false;

	public MobCharmDefinition(EntityType<?> entityType) {
		this(entityType, null, entityType);
		dynamicallyCreated = true;
	}

	public MobCharmDefinition(EntityType<?> mainEntityType, @Nullable Item repairItem, EntityType<?>... additionalApplicableTo) {
		this.registryName = EntityType.getKey(mainEntityType);
		this.repairItem = repairItem;
		applicableToEntities.add(registryName);
		Arrays.stream(additionalApplicableTo).map(EntityType::getKey).forEach(applicableToEntities::add);
	}

	public ResourceLocation getRegistryName() {
		return registryName;
	}

	public Set<ResourceLocation> getEntities() {
		return applicableToEntities;
	}

	public boolean isRepairItem(ItemStack item) {
		return repairItem != null ? item.getItem() == repairItem :
				item.getItem() == ModItems.MOB_CHARM_FRAGMENT.get() && applicableToEntities.contains(MobCharmFragmentItem.getEntityRegistryName(item));
	}

	public boolean isDynamicallyCreated() {
		return dynamicallyCreated;
	}
}
