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

import javax.annotation.Nonnull;

public class ItemWitchHat extends ItemArmor {

	private static final ItemArmor.ArmorMaterial hatMaterial = EnumHelper.addArmorMaterial("hat_material", Reference.DOMAIN + Names.Items.WITCH_HAT, 0, new int[] {0, 0, 0, 0}, 0, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0.0F);

	public ItemWitchHat() {
		super(hatMaterial, 0, EntityEquipmentSlot.HEAD);
		this.setUnlocalizedName(Names.Items.WITCH_HAT);

		this.setCreativeTab(Reliquary.CREATIVE_TAB);
	}

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(@Nonnull ItemStack stack) {
		return LanguageHelper.getLocalization(this.getUnlocalizedNameInefficiently(stack) + ".name");
	}

	@Override
	public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity) {
		return armorType == EntityEquipmentSlot.HEAD;
	}

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack stack, EntityEquipmentSlot slotID, ModelBiped _default) {
		return ModelWitchHat.self;
	}

}
