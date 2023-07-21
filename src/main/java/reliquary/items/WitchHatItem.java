package reliquary.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import reliquary.Reliquary;
import reliquary.client.model.WitchHatModel;
import reliquary.handler.ClientEventHandler;
import reliquary.reference.Reference;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class WitchHatItem extends ArmorItem {
	private static final ArmorMaterial hatMaterial = new ArmorMaterial() {
		@Override
		public int getDurabilityForSlot(EquipmentSlot equipmentSlotType) {
			return 0;
		}

		@Override
		public int getDefenseForSlot(EquipmentSlot equipmentSlotType) {
			return 0;
		}

		@Override
		public int getEnchantmentValue() {
			return 0;
		}

		@Override
		public SoundEvent getEquipSound() {
			return SoundEvents.ARMOR_EQUIP_GENERIC;
		}

		@Override
		public Ingredient getRepairIngredient() {
			return Ingredient.EMPTY;
		}

		@Override
		public String getName() {
			return Reference.MOD_ID + ":witch_hat";
		}

		@Override
		public float getToughness() {
			return 0;
		}

		@Override
		public float getKnockbackResistance() {
			return 0;
		}
	};

	public WitchHatItem() {
		super(hatMaterial, EquipmentSlot.HEAD, new Properties().tab(Reliquary.ITEM_GROUP));
	}

	@Override
	public Component getName(ItemStack stack) {
		return Component.translatable(getDescriptionId(stack)).withStyle(ChatFormatting.YELLOW);
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			private WitchHatModel hatModel = null;

			@Override
			public @Nonnull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
				if (hatModel == null) {
					EntityModelSet entityModels = Minecraft.getInstance().getEntityModels();
					hatModel = new WitchHatModel(entityModels.bakeLayer(ClientEventHandler.WITCH_HAT_LAYER));
				}
				return hatModel;
			}
		});
	}
}
