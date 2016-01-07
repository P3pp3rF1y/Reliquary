package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.items.ItemBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import xreliquary.Reliquary;
import xreliquary.lib.Names;

import java.util.List;

// @ContentInit
public class ItemElsewhereFlask extends ItemBase {

    public ItemElsewhereFlask() {
        super(Names.elsewhere_flask);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxStackSize(1);
        this.canRepair = false;
    }

    // this is tricky because tooltips have limited real estate.
    // we need to know what's in the flask though.. so we use shortened names.
    // dig, run, jump, hit, breath, fire, heal, cure [from panacea], regen,
    // armor, vanish, vision

    // potion uses are measured in "sips". you always attempt to drink one of
    // every potion in the flask.
    // the potion durations/potency in the mod are all "standard" so the
    // dual-potions just add two effects (sips)
    // instead of one.

    @Override
    public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List list, boolean par4) {

    }

    // due to the added complexity of the flask's tooltip, we need to piggyback
    // on the formatTooltip method
    // to make it easier for localization to rename the potion effects that show
    // up, since they would otherwise
    // be added in code (and thus, in English)
    public void formatPotionList(ImmutableMap<String, String> toFormat, ItemStack stack, List list) {

        // String langTooltip =
        // LanguageHelper.getLocalization(this.getUnlocalizedName(stack) +
        // ".tooltip");
        // if (langTooltip == null)
        // return;
        // if (toFormat != null) {
        // Iterator<Map.Entry<String, String>> entrySet =
        // toFormat.entrySet().iterator();
        // while (entrySet.hasNext()) {
        // Map.Entry<String, String> toReplace = entrySet.next();
        // langTooltip = langTooltip.replace("{{" + toReplace.getKey() + "}}",
        // toReplace.getValue());
        // }
        // }
        //
        // for (String descriptionLine : langTooltip.split(";")) {
        // if (descriptionLine != null && descriptionLine.length() > 0)
        // list.add(descriptionLine);
        // }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }
}
