package xreliquary.items;

import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemBase;
import lib.enderwizards.sandstone.util.LanguageHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.client.model.ModelWitchHat;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;


@ContentInit
public class ItemWitchHat extends ItemArmor {

    public static final ItemArmor.ArmorMaterial hatMaterial = EnumHelper.addArmorMaterial( "hat_material", Reference.DOMAIN + Names.witch_hat, 0, new int[] { 0, 0, 0, 0 }, 0 );

    public ItemWitchHat() {
        super(hatMaterial, 0, 0);
        this.setUnlocalizedName(Names.witch_hat);

        this.setCreativeTab(Reliquary.CREATIVE_TAB);
    }

    @Override
    @SideOnly( Side.CLIENT)
    public String getItemStackDisplayName(ItemStack stack) {
        return LanguageHelper.getLocalization( this.getUnlocalizedNameInefficiently( stack ) + ".name" );
    }

    @Override
    public boolean isValidArmor(ItemStack stack, int armorType, Entity entity) {
        return armorType == 0;
    }

    @Override
    public ModelBiped getArmorModel( EntityLivingBase entityLiving, ItemStack stack, int slotID )
    {
        return ModelWitchHat.self;
    }

}
