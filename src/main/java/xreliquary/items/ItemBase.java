package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import xreliquary.util.LanguageHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * ItemBase, a helper class for items. Handles language names, language
 * tooltips, and icon registering.
 *
 * @author TheMike
 */
public class ItemBase extends Item {

	public ItemBase(String langName) {
		this.setUnlocalizedName(langName);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flag) {
		this.formatTooltip(null, stack, tooltip);
	}

	/**
	 * Used to format tooltips. Grabs tooltip from language registry with the
	 * entry 'item.unlocalizedName.tooltip'. Has support for Handlebars-style
	 * templating, and line breaking using '\n'.
	 *
	 * @param toFormat An ImmutableMap that has all the regex keys and values. Regex
	 *                 strings are handled on the tooltip by including '{{regexKey}}'
	 *                 with your regex key, of course.
	 * @param stack    The ItemStack passed from addInformation.
	 * @param list     List of description lines passed from addInformation.
	 */
	@SideOnly(Side.CLIENT)
	public void formatTooltip(ImmutableMap<String, String> toFormat, ItemStack stack, List<String> list) {
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
			LanguageHelper.formatTooltip(this.getUnlocalizedNameInefficiently(stack) + ".tooltip", toFormat, list);
	}

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(@Nonnull ItemStack stack) {
		return LanguageHelper.getLocalization(this.getUnlocalizedNameInefficiently(stack) + ".name");
	}
}

