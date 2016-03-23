package xreliquary.items;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.client.model.ModelWitchHat;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.util.LanguageHelper;

public class ItemWitchHat extends ItemArmor {

	public static final ItemArmor.ArmorMaterial hatMaterial = EnumHelper.addArmorMaterial("hat_material", Reference.DOMAIN + Names.witch_hat, 0, new int[] {0, 0, 0, 0}, 0, SoundEvents.item_armor_equip_generic);

	public ItemWitchHat() {
		super(hatMaterial, 0, EntityEquipmentSlot.HEAD);
		this.setUnlocalizedName(Names.witch_hat);

		this.setCreativeTab(Reliquary.CREATIVE_TAB);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(ItemStack stack) {
		return LanguageHelper.getLocalization(this.getUnlocalizedNameInefficiently(stack) + ".name");
	}

	@Override
	public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity) {
		return armorType == EntityEquipmentSlot.HEAD;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack stack, EntityEquipmentSlot slotID, ModelBiped _default) {
		return ModelWitchHat.self;
	}

}
