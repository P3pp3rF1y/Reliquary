package xreliquary.client.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import xreliquary.init.ModItems;
import xreliquary.reference.Colors;
import xreliquary.reference.Reference;
import xreliquary.util.NBTHelper;
import xreliquary.util.potions.PotionEssence;

public class ModItemColors {
	public static void init() {
		ItemColors itemColors = Minecraft.getMinecraft().getItemColors();

		if(isEnabled(ModItems.mobCharmFragment) && isEnabled(ModItems.mobCharm)) {
			itemColors.registerItemColorHandler((stack, tintIndex) -> {
				if(tintIndex < 1 || tintIndex > 2)
					return -1;

				int type = ModItems.mobCharm.getType(stack);
				EntityList.EntityEggInfo eggInfo = getEntityEggInfo(type);

				if(eggInfo != null) {
					return tintIndex == 1 ? eggInfo.primaryColor : eggInfo.secondaryColor;
				}

				return -1;
			}, ModItems.mobCharm);
		}

		if(isEnabled(ModItems.mobCharmFragment) && isEnabled(ModItems.mobCharm)) {
			itemColors.registerItemColorHandler((stack, tintIndex) -> {
				if(tintIndex < 0 || tintIndex > 1)
					return -1;

				int type = stack.getMetadata();
				EntityList.EntityEggInfo eggInfo = getEntityEggInfo(type);

				if(eggInfo != null) {
					return tintIndex == 0 ? eggInfo.primaryColor : eggInfo.secondaryColor;
				}

				return -1;
			}, ModItems.mobCharmFragment);
		}

		if(isEnabled(ModItems.magazine) && isEnabled(ModItems.bullet)) {
			itemColors.registerItemColorHandler((stack, tintIndex) -> {
				if(stack.getItemDamage() == 0 || tintIndex == 0)
					return Integer.parseInt(Colors.DARKER, 16);
				else if(tintIndex == 1) {
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
				} else if(tintIndex == 2) {
					return PotionUtils.getPotionColorFromEffectList(PotionUtils.getEffectsFromStack(stack));
				}
				return Integer.parseInt(Colors.DARKER, 16);
			}, ModItems.magazine, ModItems.bullet);
		}

		if(isEnabled(ModItems.potionEssence)) {
			itemColors.registerItemColorHandler((stack, tintIndex) -> {
				//basically we're just using vanillas right now. This is hilarious in comparison to the old method, which is a mile long.
				return PotionUtils.getPotionColorFromEffectList(new PotionEssence(stack.getTagCompound()).getEffects());
			}, ModItems.potionEssence);
		}
		if(isEnabled(ModItems.potion)) {
			itemColors.registerItemColorHandler((stack, tintIndex) -> {
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
			}, ModItems.potion);
		}
		if(isEnabled(ModItems.tippedArrow)) {
			itemColors.registerItemColorHandler((stack, tintIndex) -> tintIndex == 0 ? PotionUtils.getPotionColorFromEffectList(PotionUtils.getEffectsFromStack(stack)) : -1, ModItems.tippedArrow);
		}
	}

	private static EntityList.EntityEggInfo getEntityEggInfo(int type) {
		String entityName = "";
		switch(type) {
			case Reference.MOB_CHARM.ZOMBIE_META:
				entityName = "zombie";
				break;
			case Reference.MOB_CHARM.SKELETON_META:
				entityName = "skeleton";
				break;
			case Reference.MOB_CHARM.WITHER_SKELETON_META:
				entityName = "wither_skeleton";
				break;
			case Reference.MOB_CHARM.CREEPER_META:
				entityName = "creeper";
				break;
			case Reference.MOB_CHARM.WITCH_META:
				entityName = "witch";
				break;
			case Reference.MOB_CHARM.ZOMBIE_PIGMAN_META:
				entityName = "zombie_pigman";
				break;
			case Reference.MOB_CHARM.CAVE_SPIDER_META:
				entityName = "cave_spider";
				break;
			case Reference.MOB_CHARM.SPIDER_META:
				entityName = "spider";
				break;
			case Reference.MOB_CHARM.ENDERMAN_META:
				entityName = "enderman";
				break;
			case Reference.MOB_CHARM.GHAST_META:
				entityName = "ghast";
				break;
			case Reference.MOB_CHARM.SLIME_META:
				entityName = "slime";
				break;
			case Reference.MOB_CHARM.MAGMA_CUBE_META:
				entityName = "magma_cube";
				break;
			case Reference.MOB_CHARM.BLAZE_META:
				entityName = "blaze";
				break;
			case Reference.MOB_CHARM.GUARDIAN_META:
				entityName = "guardian";
				break;
		}

		return EntityList.ENTITY_EGGS.get(new ResourceLocation(entityName));
	}

	private static boolean isEnabled(Item item) {
		return item.getRegistryName() != null;
	}
}
