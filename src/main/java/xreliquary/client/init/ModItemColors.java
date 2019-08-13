package xreliquary.client.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import xreliquary.init.ModItems;
import xreliquary.items.ItemMobCharm;
import xreliquary.reference.Colors;
import xreliquary.util.NBTHelper;
import xreliquary.util.potions.XRPotionHelper;

import java.util.List;

@SideOnly(Side.CLIENT)
public class ModItemColors {
	public static void init() {
		ItemColors itemColors = Minecraft.getMinecraft().getItemColors();

		if (isEnabled(ModItems.mobCharmFragment) && isEnabled(ModItems.mobCharm)) {
			itemColors.registerItemColorHandler((stack, tintIndex) -> {
				if (tintIndex < 1 || tintIndex > 2)
					return -1;

				int type = ModItems.mobCharm.getType(stack);
				EntityList.EntityEggInfo eggInfo = getEntityEggInfo(type);

				if (eggInfo != null) {
					return tintIndex == 1 ? eggInfo.primaryColor : eggInfo.secondaryColor;
				}

				return -1;
			}, ModItems.mobCharm);
		}

		if (isEnabled(ModItems.mobCharmFragment) && isEnabled(ModItems.mobCharm)) {
			itemColors.registerItemColorHandler((stack, tintIndex) -> {
				if (tintIndex < 0 || tintIndex > 1)
					return -1;

				int type = stack.getMetadata();
				EntityList.EntityEggInfo eggInfo = getEntityEggInfo(type);

				if (eggInfo != null) {
					return tintIndex == 0 ? eggInfo.primaryColor : eggInfo.secondaryColor;
				}

				return -1;
			}, ModItems.mobCharmFragment);
		}

		if (isEnabled(ModItems.magazine) && isEnabled(ModItems.bullet)) {
			itemColors.registerItemColorHandler((stack, tintIndex) -> {
				if (stack.getItemDamage() == 0 || tintIndex == 0)
					return Integer.parseInt(Colors.DARKER, 16);
				else if (tintIndex == 1) {
					switch (stack.getItemDamage()) {
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
				} else if (tintIndex == 2) {
					return PotionUtils.getPotionColorFromEffectList(XRPotionHelper.getPotionEffectsFromStack(stack));
				}
				return Integer.parseInt(Colors.DARKER, 16);
			}, ModItems.magazine, ModItems.bullet);
		}

		if (isEnabled(ModItems.potionEssence)) {
			itemColors.registerItemColorHandler((stack, tintIndex) -> {
				//basically we're just using vanillas right now. This is hilarious in comparison to the old method, which is a mile long.
				return PotionUtils.getPotionColorFromEffectList(XRPotionHelper.getPotionEffectsFromStack(stack));
			}, ModItems.potionEssence);
		}
		if (isEnabled(ModItems.potion)) {
			itemColors.registerItemColorHandler((stack, tintIndex) -> {
				if (tintIndex == 1) {

					//used when rendering as thrown entity
					if (NBTHelper.getInteger("renderColor", stack) > 0)
						return NBTHelper.getInteger("renderColor", stack);

					List<PotionEffect> effects = XRPotionHelper.getPotionEffectsFromStack(stack);
					if (effects.isEmpty())
						return Integer.parseInt(Colors.PURE, 16);

					return PotionUtils.getPotionColorFromEffectList(effects);
				} else
					return Integer.parseInt(Colors.PURE, 16);
			}, ModItems.potion);
		}
		if (isEnabled(ModItems.tippedArrow)) {
			itemColors.registerItemColorHandler((stack, tintIndex) -> tintIndex == 0 ? PotionUtils.getPotionColorFromEffectList(XRPotionHelper.getPotionEffectsFromStack(stack)) : -1, ModItems.tippedArrow);
		}

		if (isEnabled(ModItems.voidTear)) {
			itemColors.registerItemColorHandler((stack, tintIndex) -> {
				if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
					ItemStack containedStack = ModItems.voidTear.getContainerItem(stack, true);
					if (!containedStack.isEmpty()) {
						return itemColors.colorMultiplier(containedStack, tintIndex);
					}
				}
				return -1;
			}, ModItems.voidTear);
		}
	}

	private static EntityList.EntityEggInfo getEntityEggInfo(int type) {
		String entityName = "";
		if (ItemMobCharm.CHARM_DEFINITIONS.containsKey(type)) {
			entityName = ItemMobCharm.CHARM_DEFINITIONS.get(type).getEggEntityName();
		}
		return EntityList.ENTITY_EGGS.get(new ResourceLocation(entityName));
	}

	private static boolean isEnabled(Item item) {
		return item != null && item.getRegistryName() != null;
	}
}
