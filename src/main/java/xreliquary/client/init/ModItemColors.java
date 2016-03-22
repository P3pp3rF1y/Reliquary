package xreliquary.client.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xreliquary.init.ModItems;
import xreliquary.reference.Colors;

public class ModItemColors {
	public static void init() {
		ItemColors itemColors = Minecraft.getMinecraft().getItemColors();

		itemColors.registerItemColorHandler(new IItemColor() {
			@Override
			public int getColorFromItemstack(ItemStack stack, int tintIndex) {
				//TODO figure out if this is actually needed for attraction potion, looks like it doesn't use tintindex
				if(tintIndex == 1)
					return Integer.parseInt(Colors.APHRODITE_COLOR, 16);
				else
					return Integer.parseInt(Colors.PURE, 16);
			}
		},new Item[] {ModItems.attractionPotion});
		itemColors.registerItemColorHandler(new IItemColor() {
			@Override
			public int getColorFromItemstack(ItemStack stack, int tintIndex) {
				if (tintIndex == 1)
					return ModItems.bullet.getColor(stack);
				else
					return Integer.parseInt(Colors.PURE, 16);

			}
		}, new Item[] {ModItems.bullet});
	}
}
