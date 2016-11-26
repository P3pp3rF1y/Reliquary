/* TODO add elsewhereflask
package xreliquary.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.reference.Names;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemElsewhereFlask extends ItemBase {

	public ItemElsewhereFlask() {
		super(Names.Items.ELSEWHERE_FLASK);
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
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean par4) {

	}

*/
/*
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
*//*


	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
	}
}
*/
