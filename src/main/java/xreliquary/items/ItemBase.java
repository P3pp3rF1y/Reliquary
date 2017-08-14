package xreliquary.items;

import com.google.common.collect.Lists;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import xreliquary.reference.Reference;
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
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		if (LanguageHelper.localizationExists(this.getUnlocalizedNameInefficiently(stack) + ".tooltip")) {
			LanguageHelper.formatTooltip(this.getUnlocalizedNameInefficiently(stack) + ".tooltip", tooltip);
		}

		List<String> detailTooltip = Lists.newArrayList();
		this.addMoreInformation(stack, world, detailTooltip);
		if (!detailTooltip.isEmpty()) {
			tooltip.add("");
			if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
				tooltip.addAll(detailTooltip);
			} else {
				tooltip.add(TextFormatting.WHITE + TextFormatting.ITALIC.toString() + I18n.format(Reference.MOD_ID + ".tooltip.shift_for_more_info") + TextFormatting.RESET);
			}
		}
	}

	protected void addMoreInformation(ItemStack stack, @Nullable World world, List<String> tooltip) {

	}

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(@Nonnull ItemStack stack) {
		return LanguageHelper.getLocalization(this.getUnlocalizedNameInefficiently(stack) + ".name");
	}
}

