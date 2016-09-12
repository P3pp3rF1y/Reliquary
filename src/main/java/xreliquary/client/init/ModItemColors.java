package xreliquary.client.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionUtils;
import xreliquary.init.ModItems;
import xreliquary.reference.Colors;
import xreliquary.reference.Reference;
import xreliquary.util.NBTHelper;
import xreliquary.util.potions.PotionEssence;

public class ModItemColors {
	public static void init() {
		ItemColors itemColors = Minecraft.getMinecraft().getItemColors();

		if(isEnabled(ModItems.mobCharmFragment) && isEnabled(ModItems.heartZhu) && isEnabled(ModItems.mobCharm)) {
			itemColors.registerItemColorHandler((stack, tintIndex) -> {
				int meta = stack.getItemDamage();
				switch(meta) {
					case Reference.MOB_CHARM.ZOMBIE_META:
						return Integer.parseInt(Colors.HEART_ZHU.ZOMBIE, 16);
					case Reference.MOB_CHARM.SKELETON_META:
						return Integer.parseInt(Colors.HEART_ZHU.SKELETON, 16);
					case Reference.MOB_CHARM.WITHER_SKELETON_META:
						return Integer.parseInt(Colors.HEART_ZHU.WITHER_SKELETON, 16);
					case Reference.MOB_CHARM.CREEPER_META:
						return Integer.parseInt(Colors.HEART_ZHU.CREEPER, 16);
					case Reference.MOB_CHARM.WITCH_META:
						return Integer.parseInt(Colors.HEART_ZHU.WITCH, 16);
					case Reference.MOB_CHARM.ZOMBIE_PIGMAN_META:
						return Integer.parseInt(Colors.HEART_ZHU.ZOMBIE_PIGMAN, 16);
					case Reference.MOB_CHARM.CAVE_SPIDER_META:
						return Integer.parseInt(Colors.HEART_ZHU.CAVE_SPIDER, 16);
					case Reference.MOB_CHARM.SPIDER_META:
						return Integer.parseInt(Colors.HEART_ZHU.SPIDER, 16);
					case Reference.MOB_CHARM.ENDERMAN_META:
						return Integer.parseInt(Colors.HEART_ZHU.ENDERMAN, 16);
					case Reference.MOB_CHARM.GHAST_META:
						return Integer.parseInt(Colors.HEART_ZHU.GHAST, 16);
					case Reference.MOB_CHARM.SLIME_META:
						return Integer.parseInt(Colors.HEART_ZHU.SLIME, 16);
					case Reference.MOB_CHARM.MAGMA_CUBE_META:
						return Integer.parseInt(Colors.HEART_ZHU.MAGMA_CUBE, 16);
					case Reference.MOB_CHARM.BLAZE_META:
						return Integer.parseInt(Colors.HEART_ZHU.BLAZE, 16);
				}
				return Integer.parseInt(Colors.PURE, 16);
			}, ModItems.heartZhu);
		}

		if(isEnabled(ModItems.mobCharmFragment) && isEnabled(ModItems.mobCharm)) {
			itemColors.registerItemColorHandler((stack, tintIndex) -> {
				if(tintIndex < 1 || tintIndex > 2)
					return -1;

				int type = ModItems.mobCharm.getType(stack);
				String entityName = "";
				switch(type) {
					case Reference.MOB_CHARM.ZOMBIE_META:
						entityName = "Zombie";
						break;
					case Reference.MOB_CHARM.SKELETON_META:
						entityName = "Skeleton";
						break;
					case Reference.MOB_CHARM.WITHER_SKELETON_META:
						return tintIndex == 1 ? Integer.parseInt(Colors.WITHER_COLOR, 16) : Integer.parseInt(Colors.LIGHT_GRAY, 16);
					case Reference.MOB_CHARM.CREEPER_META:
						entityName = "Creeper";
						break;
					case Reference.MOB_CHARM.WITCH_META:
						entityName = "Witch";
						break;
					case Reference.MOB_CHARM.ZOMBIE_PIGMAN_META:
						entityName = "PigZombie";
						break;
					case Reference.MOB_CHARM.CAVE_SPIDER_META:
						entityName = "CaveSpider";
						break;
					case Reference.MOB_CHARM.SPIDER_META:
						entityName = "Spider";
						break;
					case Reference.MOB_CHARM.ENDERMAN_META:
						entityName = "Enderman";
						break;
					case Reference.MOB_CHARM.GHAST_META:
						entityName = "Ghast";
						break;
					case Reference.MOB_CHARM.SLIME_META:
						entityName = "Slime";
						break;
					case Reference.MOB_CHARM.MAGMA_CUBE_META:
						entityName = "LavaSlime";
						break;
					case Reference.MOB_CHARM.BLAZE_META:
						entityName = "Blaze";
						break;
					case Reference.MOB_CHARM.GUARDIAN_META:
						entityName = "Guardian";
						break;
				}

				EntityList.EntityEggInfo eggInfo = EntityList.ENTITY_EGGS.get(entityName);

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
				String entityName = "";
				switch(type) {
					case Reference.MOB_CHARM.ZOMBIE_META:
						entityName = "Zombie";
						break;
					case Reference.MOB_CHARM.SKELETON_META:
						entityName = "Skeleton";
						break;
					case Reference.MOB_CHARM.WITHER_SKELETON_META:
						return tintIndex == 0 ? Integer.parseInt(Colors.WITHER_COLOR, 16) : Integer.parseInt(Colors.LIGHT_GRAY, 16);
					case Reference.MOB_CHARM.CREEPER_META:
						entityName = "Creeper";
						break;
					case Reference.MOB_CHARM.WITCH_META:
						entityName = "Witch";
						break;
					case Reference.MOB_CHARM.ZOMBIE_PIGMAN_META:
						entityName = "PigZombie";
						break;
					case Reference.MOB_CHARM.CAVE_SPIDER_META:
						entityName = "CaveSpider";
						break;
					case Reference.MOB_CHARM.SPIDER_META:
						entityName = "Spider";
						break;
					case Reference.MOB_CHARM.ENDERMAN_META:
						entityName = "Enderman";
						break;
					case Reference.MOB_CHARM.GHAST_META:
						entityName = "Ghast";
						break;
					case Reference.MOB_CHARM.SLIME_META:
						entityName = "Slime";
						break;
					case Reference.MOB_CHARM.MAGMA_CUBE_META:
						entityName = "LavaSlime";
						break;
					case Reference.MOB_CHARM.BLAZE_META:
						entityName = "Blaze";
						break;
					case Reference.MOB_CHARM.GUARDIAN_META:
						entityName = "Guardian";
						break;
				}

				EntityList.EntityEggInfo eggInfo = EntityList.ENTITY_EGGS.get(entityName);

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

	private static boolean isEnabled(Item item) {
		return item.getRegistryName() != null;
	}
}
