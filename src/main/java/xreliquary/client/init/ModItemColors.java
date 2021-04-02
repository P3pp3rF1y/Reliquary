package xreliquary.client.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import xreliquary.items.BulletItem;
import xreliquary.items.VoidTearItem;
import xreliquary.items.util.IPotionItem;
import xreliquary.items.MobCharmFragmentItem;
import xreliquary.items.MobCharmItem;
import xreliquary.reference.Colors;
import xreliquary.util.NBTHelper;

import java.util.List;
import java.util.Optional;

import static xreliquary.init.ModItems.*;

@OnlyIn(Dist.CLIENT)
public class ModItemColors {
	private ModItemColors() {}

	public static void init() {
		ItemColors itemColors = Minecraft.getInstance().getItemColors();

		registerMobCharmItemColors();

		registerBulletItemColors();

		registerPotionItemColors();

		registerVoidTearItemColors(itemColors);
	}

	private static void registerVoidTearItemColors(ItemColors itemColors) {
		registerItemColor((stack, tintIndex) -> {
			if (Screen.hasShiftDown()) {
				ItemStack containedStack = VoidTearItem.getTearContents(stack, true);
				if (!containedStack.isEmpty()) {
					return itemColors.getColor(containedStack, tintIndex);
				}
			}
			return -1;
		}, VOID_TEAR.get());
	}

	private static void registerPotionItemColors() {
		registerItemColor((stack, tintIndex) -> getColor(stack), POTION_ESSENCE.get());

		registerItemColor((stack, tintIndex) -> {
			if (tintIndex == 1) {

				//used when rendering as thrown entity
				if (NBTHelper.getInt("renderColor", stack) > 0) {
					return NBTHelper.getInt("renderColor", stack);
				}

				List<EffectInstance> effects = ((IPotionItem) stack.getItem()).getEffects(stack);
				if (effects.isEmpty()) {
					return Integer.parseInt(Colors.PURE, 16);
				}

				return PotionUtils.getPotionColorFromEffectList(effects);
			} else {
				return Integer.parseInt(Colors.PURE, 16);
			}
		}, POTION.get(), SPLASH_POTION.get(), LINGERING_POTION.get());

		registerItemColor((stack, tintIndex) -> tintIndex == 0 ? PotionUtils.getPotionColorFromEffectList(((IPotionItem) stack.getItem()).getEffects(stack)) : -1, TIPPED_ARROW.get());
	}

	private static void registerBulletItemColors() {
		registerItemColor((stack, tintIndex) -> {
					if (tintIndex == 0) {
						return Integer.parseInt(Colors.DARKER, 16);
					} else if (tintIndex == 1) {
						return ((BulletItem) stack.getItem()).getColor();
					} else if (tintIndex == 2) {
						return PotionUtils.getPotionColorFromEffectList(((IPotionItem) stack.getItem()).getEffects(stack));
					}
					return Integer.parseInt(Colors.DARKER, 16);
				}, EMPTY_MAGAZINE.get(), NEUTRAL_MAGAZINE.get(), EXORCISM_MAGAZINE.get(), BLAZE_MAGAZINE.get(), ENDER_MAGAZINE.get(), CONCUSSIVE_MAGAZINE.get(),
				BUSTER_MAGAZINE.get(), SEEKER_MAGAZINE.get(), SAND_MAGAZINE.get(), STORM_MAGAZINE.get(), EMPTY_BULLET.get(), NEUTRAL_BULLET.get(), EXORCISM_BULLET.get(),
				BLAZE_BULLET.get(), ENDER_BULLET.get(), CONCUSSIVE_BULLET.get(), BUSTER_BULLET.get(), SEEKER_BULLET.get(), SAND_BULLET.get(), STORM_BULLET.get());
	}

	private static void registerMobCharmItemColors() {
		registerItemColor((stack, tintIndex) -> {
			if (tintIndex < 1 || tintIndex > 2) {
				return -1;
			}

			ResourceLocation entityName = MobCharmItem.getEntityEggRegistryName(stack);
			return getEgg(entityName).map(egg -> tintIndex == 1 ? egg.getColor(0) : egg.getColor(1)).orElse(-1);
		}, MOB_CHARM.get());

		registerItemColor((stack, tintIndex) -> {
			if (tintIndex < 0 || tintIndex > 1) {
				return -1;
			}

			ResourceLocation entityName = MobCharmFragmentItem.getEntityEggRegistryName(stack);
			return getEgg(entityName).map(egg -> tintIndex == 0 ? egg.getColor(0) : egg.getColor(1)).orElse(-1);
		}, MOB_CHARM_FRAGMENT.get());
	}

	private static void registerItemColor(IItemColor itemColor, Item... items) {
		if (isEnabled(items)) {
			Minecraft.getInstance().getItemColors().register(itemColor, items);
		}
	}

	private static Optional<SpawnEggItem> getEgg(ResourceLocation entityName) {
		return Optional.ofNullable(SpawnEggItem.getEgg(ForgeRegistries.ENTITIES.getValue(entityName)));
	}

	private static int getColor(ItemStack stack) {return PotionUtils.getPotionColorFromEffectList(((IPotionItem) stack.getItem()).getEffects(stack));}
}
