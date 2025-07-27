package reliquary.client.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import reliquary.item.BulletItem;
import reliquary.item.MobCharmFragmentItem;
import reliquary.item.MobCharmItem;
import reliquary.item.VoidTearItem;

import java.util.Optional;

import static reliquary.init.ModItems.*;

public class ModItemColors {
	private ModItemColors() {
	}

	public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
		registerMobCharmItemColors(event);

		registerBulletItemColors(event);

		registerPotionItemColors(event);

		registerVoidTearItemColors(event);
	}

	private static void registerVoidTearItemColors(RegisterColorHandlersEvent.Item event) {
		event.register((stack, tintIndex) -> {
			if (Screen.hasShiftDown()) {
				ItemStack containedStack = VoidTearItem.getTearContents(stack);
				if (!containedStack.isEmpty()) {
					return Minecraft.getInstance().getItemColors().getColor(containedStack, tintIndex);
				}
			}
			return -1;
		}, VOID_TEAR.get());
	}

	private static void registerPotionItemColors(RegisterColorHandlersEvent.Item event) {
		event.register((stack, tintIndex) -> getColor(stack), POTION_ESSENCE.get());

		event.register((stack, tintIndex) -> {
			if (tintIndex == 1) {
				return stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getColor();

			} else {
				return -1;
			}
		}, POTION.get(), SPLASH_POTION.get(), LINGERING_POTION.get());

		event.register((stack, tintIndex) -> tintIndex == 0 ? stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getColor() : -1, TIPPED_ARROW.get());
	}

	private static void registerBulletItemColors(RegisterColorHandlersEvent.Item event) {
		event.register((stack, tintIndex) -> {
					if (tintIndex == 0) {
						return -1;
					} else if (tintIndex == 1) {
						return ((BulletItem) stack.getItem()).getColor();
					} else if (tintIndex == 2) {
						return stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getColor();
					}
					return -1;
				}, EMPTY_MAGAZINE.get(), NEUTRAL_MAGAZINE.get(), EXORCISM_MAGAZINE.get(), BLAZE_MAGAZINE.get(), ENDER_MAGAZINE.get(), CONCUSSIVE_MAGAZINE.get(),
				BUSTER_MAGAZINE.get(), SEEKER_MAGAZINE.get(), SAND_MAGAZINE.get(), STORM_MAGAZINE.get(), EMPTY_BULLET.get(), NEUTRAL_BULLET.get(), EXORCISM_BULLET.get(),
				BLAZE_BULLET.get(), ENDER_BULLET.get(), CONCUSSIVE_BULLET.get(), BUSTER_BULLET.get(), SEEKER_BULLET.get(), SAND_BULLET.get(), STORM_BULLET.get());
	}

	private static void registerMobCharmItemColors(RegisterColorHandlersEvent.Item event) {
		event.register((stack, tintIndex) -> {
			if (tintIndex < 1 || tintIndex > 2) {
				return -1;
			}

			ResourceLocation entityName = MobCharmItem.getEntityEggRegistryName(stack);
			return getEgg(entityName).map(egg -> tintIndex == 1 ? FastColor.ARGB32.opaque(egg.getColor(0)) : FastColor.ARGB32.opaque(egg.getColor(1))).orElse(-1);
		}, MOB_CHARM.get());

		event.register((stack, tintIndex) -> {
			if (tintIndex < 0 || tintIndex > 1) {
				return -1;
			}

			ResourceLocation entityName = MobCharmFragmentItem.getEntityRegistryName(stack);
			return getEgg(entityName).map(egg -> tintIndex == 0 ? FastColor.ARGB32.opaque(egg.getColor(0)) : FastColor.ARGB32.opaque(egg.getColor(1))).orElse(-1);
		}, MOB_CHARM_FRAGMENT.get());
	}

	private static Optional<SpawnEggItem> getEgg(ResourceLocation entityName) {
		return Optional.ofNullable(SpawnEggItem.byId(BuiltInRegistries.ENTITY_TYPE.get(entityName)));
	}

	private static int getColor(ItemStack stack) {
		return stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getColor();
	}
}
