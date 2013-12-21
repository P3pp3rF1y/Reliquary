package mods.themike.core.item;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableMap;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * ItemBase, a helper class for items. Handles language names, language
 * tooltips, and icon registering.
 * 
 * @author TheMike
 */
public class ItemBase extends Item {

	String modName, textureName;

	public ItemBase(int itemID, String modName, String textureName) {
		super(itemID);

		this.modName = modName;
		this.textureName = textureName;
		this.setUnlocalizedName(textureName);
		GameRegistry.registerItem(this, modName + ":" + textureName);
	}

	/**
	 * Just a call to formatTooltip(). If you are overriding this function, call
	 * formatTooltip() directly and DO NOT call super.addInformation().
	 */
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean whatDoesThisEvenDo) {
		this.formatTooltip(null, stack, list);
	}

	/**
	 * Used to format tooltips. Grabs tooltip from language registry with the
	 * entry 'item.unlocalizedName.tooltip'. Has support for Handlebars-style
	 * templating, and line breaking using '\n'.
	 * 
	 * @param toFormat
	 *            An ImmutableMap that has all the regex keys and values. Regex
	 *            strings are handled on the tooltip by including '{{regexKey}}'
	 *            with your regex key, of course.
	 * @param stack
	 *            The ItemStack passed from addInformation.
	 * @param list
	 *            List of description lines passed from addInformation.
	 */
	public void formatTooltip(ImmutableMap<String, String> toFormat, ItemStack stack, List list) {
		String langTooltip = LanguageRegistry.instance().getStringLocalization(this.getUnlocalizedName(stack) + ".tooltip");
		if (langTooltip == null)
			return;
		if (toFormat != null) {
			Iterator<Entry<String, String>> entrySet = toFormat.entrySet().iterator();
			while (entrySet.hasNext()) {
				Entry<String, String> toReplace = entrySet.next();
				langTooltip = langTooltip.replace("{{" + toReplace.getKey() + "}}", toReplace.getValue());
			}
		}

		for (String descriptionLine : langTooltip.split("\n")) {
			if (descriptionLine != null && descriptionLine.length() > 0)
				list.add(descriptionLine);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		itemIcon = iconRegister.registerIcon(this.modName + ":" + this.textureName);
	}

}