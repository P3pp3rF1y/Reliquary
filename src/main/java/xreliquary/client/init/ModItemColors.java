package xreliquary.client.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import xreliquary.init.ModItems;
import xreliquary.reference.Colors;
import xreliquary.util.NBTHelper;
import xreliquary.util.potions.PotionEssence;

public class ModItemColors {
	public static void init() {
		ItemColors itemColors = Minecraft.getMinecraft().getItemColors();

		if(isEnabled(ModItems.heartPearl) && isEnabled(ModItems.heartZhu)) {
			itemColors.registerItemColorHandler(new IItemColor() {
				@Override
				public int getColorFromItemstack(ItemStack stack, int tintIndex) {
					int meta = stack.getItemDamage();
					switch(meta) {
						case 0:
							return Integer.parseInt(Colors.ZOMBIE_HEART_ZHU_COLOR, 16);
						case 1:
							return Integer.parseInt(Colors.SKELETON_HEART_ZHU_COLOR, 16);
						case 2:
							return Integer.parseInt(Colors.WITHER_SKELETON_HEART_ZHU_COLOR, 16);
						case 3:
							return Integer.parseInt(Colors.CREEPER_HEART_ZHU_COLOR, 16);
					}
					return Integer.parseInt(Colors.PURE, 16);
				}
			}, new Item[] {ModItems.heartPearl, ModItems.heartZhu});
		}

		if(isEnabled(ModItems.magazine) && isEnabled(ModItems.bullet)) {
			itemColors.registerItemColorHandler(new IItemColor() {
				@Override
				public int getColorFromItemstack(ItemStack stack, int tintIndex) {
					if(stack.getItemDamage() == 0 || tintIndex != 1)
						return Integer.parseInt(Colors.DARKER, 16);
					else {
						switch(stack.getItemDamage()) {
							case 1:
								return Integer.parseInt(Colors.NEUTRAL_SHOT_COLOR, 16);
							case 2:
								return Integer.parseInt(Colors.EXORCISM_SHOT_COLOR, 16);
							case 3:
								return Integer.parseInt(Colors.BLAZE_SHOT_COLOR, 16);
							case 4:
								return Integer.parseInt(Colors.ENDER_SHOT_COLOR, 16);
							case 5:
								return Integer.parseInt(Colors.CONCUSSIVE_SHOT_COLOR, 16);
							case 6:
								return Integer.parseInt(Colors.BUSTER_SHOT_COLOR, 16);
							case 7:
								return Integer.parseInt(Colors.SEEKER_SHOT_COLOR, 16);
							case 8:
								return Integer.parseInt(Colors.SAND_SHOT_COLOR, 16);
							case 9:
								return Integer.parseInt(Colors.STORM_SHOT_COLOR, 16);
						}
						return Integer.parseInt(Colors.DARKEST, 16);
					}
				}
			}, new Item[] {ModItems.magazine, ModItems.bullet});
		}
		if(isEnabled(ModItems.potionEssence)) {
			itemColors.registerItemColorHandler(new IItemColor() {
				@Override
				public int getColorFromItemstack(ItemStack stack, int tintIndex) {
					//basically we're just using vanillas right now. This is hilarious in comparison to the old method, which is a mile long.
					return PotionUtils.getPotionColorFromEffectList(new PotionEssence(stack.getTagCompound()).getEffects());
				}
			}, ModItems.potionEssence);
		}
		if(isEnabled(ModItems.potion)) {
			itemColors.registerItemColorHandler(new IItemColor() {
				@Override
				public int getColorFromItemstack(ItemStack stack, int tintIndex) {
					if(tintIndex == 1) {

						//used when rendering as thrown entity
						if(NBTHelper.getInteger("renderColor", stack) > 0)
							return NBTHelper.getInteger("renderColor", stack);

						PotionEssence essence = new PotionEssence(stack.getTagCompound());
						boolean hasEffect = essence.getEffects().size() > 0;
						if(!hasEffect)
							return Integer.parseInt(Colors.PURE, 16);

						return PotionUtils.getPotionColorFromEffectList(new PotionEssence(stack.getTagCompound()).getEffects());
					} else
						return Integer.parseInt(Colors.PURE, 16);
				}
			}, new Item[] {ModItems.potion});
		}
	}

	private static boolean isEnabled(Item item) {
		return item.getRegistryName() != null;
	}
}
