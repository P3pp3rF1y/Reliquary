package xreliquary.items;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xreliquary.Reliquary;
import xreliquary.client.model.WitchHatModel;
import xreliquary.reference.Reference;
import xreliquary.util.LanguageHelper;

import javax.annotation.Nullable;

public class WitchHatItem extends ArmorItem {
	private static final IArmorMaterial hatMaterial = new IArmorMaterial() {
		@Override
		public int getDurability(EquipmentSlotType equipmentSlotType) {
			return 0;
		}

		@Override
		public int getDamageReductionAmount(EquipmentSlotType equipmentSlotType) {
			return 0;
		}

		@Override
		public int getEnchantability() {
			return 0;
		}

		@Override
		public SoundEvent getSoundEvent() {
			return SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
		}

		@Override
		public Ingredient getRepairMaterial() {
			return Ingredient.EMPTY;
		}

		@Override
		public String getName() {
			return "hat_material";
		}

		@Override
		public float getToughness() {
			return 0;
		}
	};

	public WitchHatItem() {
		super(hatMaterial, EquipmentSlotType.HEAD, new Properties().group(Reliquary.ITEM_GROUP));
		setRegistryName(new ResourceLocation(Reference.MOD_ID, "witch_hat"));
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		return new StringTextComponent(LanguageHelper.getLocalization(getTranslationKey(stack)));
	}

	@Nullable
	@Override
	@OnlyIn(Dist.CLIENT)
	public <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, A def) {
		return (A) WitchHatModel.SELF;
	}
}
