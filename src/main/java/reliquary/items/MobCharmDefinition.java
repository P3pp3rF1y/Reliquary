package reliquary.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import reliquary.init.ModItems;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MobCharmDefinition {
	static final MobCharmDefinition ZOMBIE = new MobCharmDefinition("minecraft:zombie", ModItems.ZOMBIE_HEART.get(), "minecraft:zombie", "minecraft:husk", "minecraft:drowned", "minecraft:zombie_villager");
	static final MobCharmDefinition SKELETON = new MobCharmDefinition("minecraft:skeleton", ModItems.RIB_BONE.get(), "minecraft:skeleton", "minecraft:stray");
	static final MobCharmDefinition WITHER_SKELETON = new MobCharmDefinition("minecraft:wither_skeleton", ModItems.WITHERED_RIB.get(), "minecraft:wither_skeleton");
	static final MobCharmDefinition CREEPER = new MobCharmDefinition("minecraft:creeper", ModItems.CATALYZING_GLAND.get(), "minecraft:creeper");
	static final MobCharmDefinition WITCH = new MobCharmDefinition("minecraft:witch", ModItems.WITCH_HAT.get(), "minecraft:witch");
	static final MobCharmDefinition ZOMBIFIED_PIGLIN = new MobCharmDefinition("minecraft:zombified_piglin", ModItems.ZOMBIE_HEART.get(), "minecraft:zombified_piglin").setResetTargetInLivingTickEvent(true);
	static final MobCharmDefinition CAVE_SPIDER = new MobCharmDefinition("minecraft:cave_spider", ModItems.CHELICERAE.get(), "minecraft:cave_spider");
	static final MobCharmDefinition SPIDER = new MobCharmDefinition("minecraft:spider", ModItems.CHELICERAE.get(), "minecraft:spider");
	static final MobCharmDefinition ENDERMAN = new MobCharmDefinition("minecraft:enderman", ModItems.NEBULOUS_HEART.get(), "minecraft:enderman").setResetTargetInLivingTickEvent(true);
	static final MobCharmDefinition GHAST = new MobCharmDefinition("minecraft:ghast", ModItems.CATALYZING_GLAND.get(), "minecraft:ghast").setResetTargetInLivingTickEvent(true);
	static final MobCharmDefinition SLIME = new MobCharmDefinition("minecraft:slime", ModItems.SLIME_PEARL.get(), "minecraft:slime").setResetTargetInLivingTickEvent(true);
	static final MobCharmDefinition MAGMA_CUBE = new MobCharmDefinition("minecraft:magma_cube", ModItems.MOLTEN_CORE.get(), "minecraft:magma_cube").setResetTargetInLivingTickEvent(true);
	static final MobCharmDefinition BLAZE = new MobCharmDefinition("minecraft:blaze", ModItems.MOLTEN_CORE.get(), "minecraft:blaze");
	static final MobCharmDefinition GUARDIAN = new MobCharmDefinition("minecraft:guardian", ModItems.GUARDIAN_SPIKE.get(), "minecraft:guardian");
	static final MobCharmDefinition PIGLIN = new MobCharmDefinition("minecraft:piglin", null, "minecraft:piglin").setResetTargetInLivingTickEvent(true);
	static final MobCharmDefinition PIGLIN_BRUTE = new MobCharmDefinition("minecraft:piglin_brute", null, "minecraft:piglin_brute").setResetTargetInLivingTickEvent(true);
	static final MobCharmDefinition HOGLIN = new MobCharmDefinition("minecraft:hoglin", null, "minecraft:hoglin").setResetTargetInLivingTickEvent(true);

	private final Set<String> applicableToEntities = new HashSet<>();
	private boolean resetTargetInLivingTickEvent = false;
	private final String registryName;
	private final Item repairItem;
	private boolean dynamicallyCreated = false;

	public MobCharmDefinition(String registryName) {
		this(registryName, null, registryName);
		dynamicallyCreated = true;
	}

	public MobCharmDefinition(String registryName, @Nullable Item repairItem, String... applicableTo) {
		this.registryName = registryName;
		this.repairItem = repairItem;
		Collections.addAll(applicableToEntities, applicableTo);
	}

	public String getRegistryName() {
		return registryName;
	}

	public MobCharmDefinition setResetTargetInLivingTickEvent(boolean resetTargetInLivingTickEvent) {
		this.resetTargetInLivingTickEvent = resetTargetInLivingTickEvent;
		return this;
	}

	public boolean resetTargetInLivingTickEvent() {
		return resetTargetInLivingTickEvent;
	}

	public Set<String> getEntities() {
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
