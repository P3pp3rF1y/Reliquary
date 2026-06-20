package reliquary.item;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import reliquary.init.ModItems;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MobCharmDefinition {
	static final MobCharmDefinition ZOMBIE = new MobCharmDefinition(EntityTypes.ZOMBIE, ModItems.ZOMBIE_HEART.get(), EntityTypes.HUSK, EntityTypes.DROWNED, EntityTypes.ZOMBIE_VILLAGER);
	static final MobCharmDefinition SKELETON = new MobCharmDefinition(EntityTypes.SKELETON, ModItems.RIB_BONE.get(), EntityTypes.STRAY);
	static final MobCharmDefinition WITHER_SKELETON = new MobCharmDefinition(EntityTypes.WITHER_SKELETON, ModItems.WITHERED_RIB.get());
	static final MobCharmDefinition CREEPER = new MobCharmDefinition(EntityTypes.CREEPER, ModItems.CATALYZING_GLAND.get());
	static final MobCharmDefinition WITCH = new MobCharmDefinition(EntityTypes.WITCH, ModItems.WITCH_HAT.get());
	static final MobCharmDefinition ZOMBIFIED_PIGLIN = new MobCharmDefinition(EntityTypes.ZOMBIFIED_PIGLIN, ModItems.ZOMBIE_HEART.get());
	static final MobCharmDefinition CAVE_SPIDER = new MobCharmDefinition(EntityTypes.CAVE_SPIDER, ModItems.CHELICERAE.get());
	static final MobCharmDefinition SPIDER = new MobCharmDefinition(EntityTypes.SPIDER, ModItems.CHELICERAE.get());
	static final MobCharmDefinition ENDERMAN = new MobCharmDefinition(EntityTypes.ENDERMAN, ModItems.NEBULOUS_HEART.get());
	static final MobCharmDefinition GHAST = new MobCharmDefinition(EntityTypes.GHAST, ModItems.CATALYZING_GLAND.get());
	static final MobCharmDefinition SLIME = new MobCharmDefinition(EntityTypes.SLIME, ModItems.SLIME_PEARL.get());
	static final MobCharmDefinition MAGMA_CUBE = new MobCharmDefinition(EntityTypes.MAGMA_CUBE, ModItems.MOLTEN_CORE.get());
	static final MobCharmDefinition BLAZE = new MobCharmDefinition(EntityTypes.BLAZE, ModItems.MOLTEN_CORE.get());
	static final MobCharmDefinition GUARDIAN = new MobCharmDefinition(EntityTypes.GUARDIAN, ModItems.GUARDIAN_SPIKE.get());

	private final Set<Identifier> applicableToEntities = new HashSet<>();
	private final Identifier registryName;
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

	public Identifier getRegistryName() {
		return registryName;
	}

	public Set<Identifier> getEntities() {
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
