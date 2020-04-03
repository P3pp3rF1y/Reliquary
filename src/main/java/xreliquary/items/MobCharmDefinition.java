package xreliquary.items;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import xreliquary.init.ModItems;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class MobCharmDefinition {
	static final MobCharmDefinition ZOMBIE = new MobCharmDefinition("minecraft:zombie", s -> s.getItem() == ModItems.ZOMBIE_HEART, "minecraft:zombie", "minecraft:husk", "minecraft:drowned");
	static final MobCharmDefinition SKELETON = new MobCharmDefinition("minecraft:skeleton", s -> s.getItem() == ModItems.RIB_BONE, "minecraft:skeleton", "minecraft:stray");
	static final MobCharmDefinition WITHER_SKELETON = new MobCharmDefinition("minecraft:wither_skeleton", s -> s.getItem() == ModItems.WITHERED_RIB, "minecraft:wither_skeleton");
	static final MobCharmDefinition CREEPER = new MobCharmDefinition("minecraft:creeper", s -> s.getItem() == ModItems.CATALYZING_GLAND, "minecraft:creeper");
	static final MobCharmDefinition WITCH = new MobCharmDefinition("minecraft:witch", s -> s.getItem() == ModItems.WITCH_HAT, "minecraft:witch");
	static final MobCharmDefinition ZOMBIE_PIGMAN = new MobCharmDefinition("minecraft:zombie_pigman", s -> s.getItem() == ModItems.ZOMBIE_HEART, "minecraft:zombie_pigman").setResetTargetInLivingUpdateEvent(true);
	static final MobCharmDefinition CAVE_SPIDER = new MobCharmDefinition("minecraft:cave_spider", s -> s.getItem() == ModItems.CHELICERAE, "minecraft:cave_spider");
	static final MobCharmDefinition SPIDER = new MobCharmDefinition("minecraft:spider", s -> s.getItem() == ModItems.CHELICERAE, "minecraft:spider");
	static final MobCharmDefinition ENDERMAN = new MobCharmDefinition("minecraft:enderman", s -> s.getItem() == ModItems.NEBULOUS_HEART, "minecraft:enderman").setResetTargetInLivingUpdateEvent(true);
	static final MobCharmDefinition GHAST = new MobCharmDefinition("minecraft:ghast", s -> s.getItem() == ModItems.CATALYZING_GLAND, "minecraft:ghast").setResetTargetInLivingUpdateEvent(true);
	static final MobCharmDefinition SLIME = new MobCharmDefinition("minecraft:slime", s -> s.getItem() == ModItems.SLIME_PEARL, "minecraft:slime").setResetTargetInLivingUpdateEvent(true);
	static final MobCharmDefinition MAGMA_CUBE = new MobCharmDefinition("minecraft:magma_cube", s -> s.getItem() == ModItems.MOLTEN_CORE, "minecraft:magma_cube").setResetTargetInLivingUpdateEvent(true);
	static final MobCharmDefinition BLAZE = new MobCharmDefinition("minecraft:blaze", s -> s.getItem() == ModItems.MOLTEN_CORE, "minecraft:blaze");
	static final MobCharmDefinition GUARDIAN = new MobCharmDefinition("minecraft:guardian", s -> s.getItem() == ModItems.GUARDIAN_SPIKE, "minecraft:guardian");

	private final Set<String> applicableToEntities = new HashSet<>();
	private boolean resetTargetInLivingUpdateEvent = false;
	private final String registryName;
	private final Predicate<ItemStack> isRepairItem;

	MobCharmDefinition(String registryName, Predicate<ItemStack> isRepairItem, String... applicableTo) {
		this.registryName = registryName;
		this.isRepairItem = isRepairItem;
		Collections.addAll(applicableToEntities, applicableTo);
	}

	public boolean appliesTo(Entity e) {
		ResourceLocation registryName = e.getType().getRegistryName();
		return registryName != null && applicableToEntities.contains(registryName.toString());
	}

	public String getRegistryName() {
		return registryName;
	}

	//TODO figure out if there are still that many entities that need this apart from Pigman where it's obvious
	public MobCharmDefinition setResetTargetInLivingUpdateEvent(boolean resetTargetInLivingUpdateEvent) {
		this.resetTargetInLivingUpdateEvent = resetTargetInLivingUpdateEvent;
		return this;
	}

	public boolean resetTargetInLivingUpdateEvent() {
		return resetTargetInLivingUpdateEvent;
	}

	public Set<String> getEntities() {
		return applicableToEntities;
	}

	public boolean isRepairItem(ItemStack item) {
		return isRepairItem.test(item);
	}
}
