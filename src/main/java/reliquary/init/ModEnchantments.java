package reliquary.init;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import reliquary.reference.Reference;

public class ModEnchantments {
	private ModEnchantments() {}

	private static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Reference.MOD_ID);
	public static final RegistryObject<Enchantment> SEVERING = ENCHANTMENTS.register("severing", SeveringEnchantment::new);

	public static void register(IEventBus modBus) {
		ENCHANTMENTS.register(modBus);
	}

	public static class SeveringEnchantment extends Enchantment {
		protected SeveringEnchantment() {
			super(Rarity.RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
		}

		@Override
		public int getMaxLevel() {
			return 5;
		}

		@Override
		public int getMinCost(int level) {
			return 15 + (level - 1) * 9;
		}

		@Override
		public int getMaxCost(int level) {
			return super.getMinCost(level) + 50;
		}

	}

}
