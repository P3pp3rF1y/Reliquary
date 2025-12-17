package reliquary.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;
import reliquary.Reliquary;

public class ReliquaryEnchantmentProvider {

	public static final ResourceKey<Enchantment> SEVERING = ResourceKey.create(Registries.ENCHANTMENT, Reliquary.getIdentifier("severing"));

	public static void bootstrap(BootstrapContext<Enchantment> context) {
		context.register(SEVERING,
				Enchantment.enchantment(
						Enchantment.definition(
								context.lookup(Registries.ITEM).getOrThrow(ItemTags.MELEE_WEAPON_ENCHANTABLE),
								1, //weight
								5, // max level
								Enchantment.dynamicCost(15, 9),
								Enchantment.dynamicCost(65, 9),
								6, // anvil cost
								EquipmentSlotGroup.MAINHAND)
				).build(SEVERING.identifier())
		);
	}
}
