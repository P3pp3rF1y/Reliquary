package reliquary.items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.client.IItemRenderProperties;
import reliquary.Reliquary;
import reliquary.client.model.WitchHatModel;
import reliquary.handler.ClientEventHandler;
import reliquary.reference.Reference;
import reliquary.util.LanguageHelper;

import java.util.function.Consumer;

public class WitchHatItem extends ArmorItem implements IItemRenderProperties {
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
		return new TextComponent(LanguageHelper.getLocalization(getDescriptionId(stack)));
	}

	@Override
	public void initializeClient(Consumer<IItemRenderProperties> consumer) {
		consumer.accept(new IItemRenderProperties() {
			private WitchHatModel hatModel = null;

			@Override
			public HumanoidModel<?> getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default) {
				if (hatModel == null) {
					EntityModelSet entityModels = Minecraft.getInstance().getEntityModels();
					hatModel = new WitchHatModel(entityModels.bakeLayer(ClientEventHandler.WITCH_HAT_LAYER));
				}
				return hatModel;
			}
		});
	}
}
