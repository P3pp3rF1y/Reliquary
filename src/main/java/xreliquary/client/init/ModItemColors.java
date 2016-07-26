package xreliquary.client.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import xreliquary.init.ModItems;
import xreliquary.reference.Colors;
import xreliquary.reference.Reference;
import xreliquary.util.NBTHelper;
import xreliquary.util.potions.PotionEssence;

public class ModItemColors {
	public static void init() {
		ItemColors itemColors = Minecraft.getMinecraft().getItemColors();

		if(isEnabled(ModItems.heartPearl) && isEnabled(ModItems.heartZhu) && isEnabled(ModItems.nianZhu)) {
			itemColors.registerItemColorHandler(new IItemColor() {
				@Override
				public int getColorFromItemstack(ItemStack stack, int tintIndex) {
					int meta = stack.getItemDamage();
					switch(meta) {
						case Reference.NIAN_ZHU.ZOMBIE_META:
							return Integer.parseInt(Colors.NIAN_ZHU.ZOMBIE, 16);
						case Reference.NIAN_ZHU.SKELETON_META:
							return Integer.parseInt(Colors.NIAN_ZHU.SKELETON, 16);
						case Reference.NIAN_ZHU.WITHER_SKELETON_META:
							return Integer.parseInt(Colors.NIAN_ZHU.WITHER_SKELETON, 16);
						case Reference.NIAN_ZHU.CREEPER_META:
							return Integer.parseInt(Colors.NIAN_ZHU.CREEPER, 16);
						case Reference.NIAN_ZHU.WITCH_META:
							return Integer.parseInt(Colors.NIAN_ZHU.WITCH, 16);
						case Reference.NIAN_ZHU.ZOMBIE_PIGMAN_META:
							return Integer.parseInt(Colors.NIAN_ZHU.ZOMBIE_PIGMAN, 16);
						case Reference.NIAN_ZHU.CAVE_SPIDER_META:
							return Integer.parseInt(Colors.NIAN_ZHU.CAVE_SPIDER, 16);
						case Reference.NIAN_ZHU.SPIDER_META:
							return Integer.parseInt(Colors.NIAN_ZHU.SPIDER, 16);
						case Reference.NIAN_ZHU.ENDERMAN_META:
							return Integer.parseInt(Colors.NIAN_ZHU.ENDERMAN, 16);
						case Reference.NIAN_ZHU.GHAST_META:
							return Integer.parseInt(Colors.NIAN_ZHU.GHAST, 16);
						case Reference.NIAN_ZHU.SLIME_META:
							return Integer.parseInt(Colors.NIAN_ZHU.SLIME, 16);
						case Reference.NIAN_ZHU.MAGMA_CUBE_META:
							return Integer.parseInt(Colors.NIAN_ZHU.MAGMA_CUBE, 16);
						case Reference.NIAN_ZHU.BLAZE_META:
							return Integer.parseInt(Colors.NIAN_ZHU.BLAZE, 16);
					}
					return Integer.parseInt(Colors.PURE, 16);
				}
			}, new Item[] {ModItems.heartPearl, ModItems.heartZhu});
		}

		if(isEnabled(ModItems.heartPearl) && isEnabled(ModItems.nianZhu)) {
			itemColors.registerItemColorHandler(new IItemColor() {
				@Override
				public int getColorFromItemstack(ItemStack stack, int tintIndex) {
					if (tintIndex < 1 || tintIndex > 2)
						return -1;

					int type = ModItems.nianZhu.getType(stack);
					String entityName = "";
					switch(type) {
						case Reference.NIAN_ZHU.ZOMBIE_META:
							entityName = "Zombie";
							break;
						case Reference.NIAN_ZHU.SKELETON_META:
							entityName = "Skeleton";
							break;
						case Reference.NIAN_ZHU.WITHER_SKELETON_META:
							return tintIndex == 1 ? Integer.parseInt(Colors.WITHER_COLOR, 16) : Integer.parseInt(Colors.LIGHT_GRAY, 16);
						case Reference.NIAN_ZHU.CREEPER_META:
							entityName = "Creeper";
							break;
						case Reference.NIAN_ZHU.WITCH_META:
							entityName = "Witch";
							break;
						case Reference.NIAN_ZHU.ZOMBIE_PIGMAN_META:
							entityName = "PigZombie";
							break;
						case Reference.NIAN_ZHU.CAVE_SPIDER_META:
							entityName = "CaveSpider";
							break;
						case Reference.NIAN_ZHU.SPIDER_META:
							entityName = "Spider";
							break;
						case Reference.NIAN_ZHU.ENDERMAN_META:
							entityName = "Enderman";
							break;
						case Reference.NIAN_ZHU.GHAST_META:
							entityName = "Ghast";
							break;
						case Reference.NIAN_ZHU.SLIME_META:
							entityName = "Slime";
							break;
						case Reference.NIAN_ZHU.MAGMA_CUBE_META:
							entityName = "LavaSlime";
							break;
						case Reference.NIAN_ZHU.BLAZE_META:
							entityName = "Blaze";
							break;
					}

					EntityList.EntityEggInfo eggInfo = EntityList.ENTITY_EGGS.get(entityName);

					if (eggInfo != null) {
						return tintIndex == 1 ? eggInfo.primaryColor : eggInfo.secondaryColor;
					}

					return -1;
				}
			}, new Item[] {ModItems.nianZhu});
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
