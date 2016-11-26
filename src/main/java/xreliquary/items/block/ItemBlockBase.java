package xreliquary.items.block;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import xreliquary.util.LanguageHelper;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * ItemBlockBase, a helper class for item blocks. Handles language names and language
 * parsing (see LanguageHelper).
 * <p>
 * In most ways, completely similar to ItemBase.
 *
 * @author TheMike
 */
public class ItemBlockBase extends ItemBlock {

	public ItemBlockBase(Block block) {
		super(block);
	}

	/**
	 * Just a call to formatTooltip(). If you are overriding this function, call
	 * formatTooltip() directly and DO NOT call super.addInformation().
	 */
	@Override
	public void addInformation(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, @Nonnull List<String> list, boolean whatDoesThisEvenDo) {
		this.formatTooltip(stack, list);
	}

	/**
	 * Used to format tooltips. Grabs tooltip from language registry with the
	 * entry 'item.unlocalizedName.tooltip'. Has support for Handlebars-style
	 * templating, and line breaking using '\n'.
	 *  @param stack    The ItemStack passed from addInformation.
	 * @param list     List of description lines passed from addInformation.
	 */
	public void formatTooltip(ItemStack stack, List<String> list) {
		if(showTooltipsAlways() || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
			LanguageHelper.formatTooltip(this.getUnlocalizedNameInefficiently(stack) + ".tooltip", null, list);
	}

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(@Nonnull ItemStack stack) {
		return LanguageHelper.getLocalization(this.getUnlocalizedNameInefficiently(stack) + ".name");
	}

	private boolean showTooltipsAlways() {
		return false;
	}
}
