package reliquary.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import reliquary.items.util.IPotionItem;
import reliquary.reference.Settings;
import reliquary.util.TooltipBuilder;
import reliquary.util.potions.PotionEssence;
import reliquary.util.potions.PotionMap;
import reliquary.util.potions.XRPotionHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class PotionItemBase extends ItemBase implements IPotionItem {
	public PotionItemBase() {
		super(new Properties());
	}

	// returns an empty vial when used in crafting recipes, unless it's one of
	// the base potion types.
	@Override
	public boolean hasCraftingRemainingItem(ItemStack stack) {
		return !XRPotionHelper.getPotionEffectsFromStack(stack).isEmpty();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack potion, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
		TooltipBuilder.of(tooltip).potionEffects(potion);
	}

	@Override
	public void addCreativeTabItems(Consumer<ItemStack> itemConsumer) {
		if (Boolean.TRUE.equals(Settings.COMMON.disable.disablePotions.get())) {
			return;
		}

		for (PotionEssence essence : PotionMap.uniquePotions) {
			ItemStack potion = new ItemStack(this, 1);
			XRPotionHelper.addPotionEffectsToStack(potion, essence.getEffects());

			itemConsumer.accept(potion);
		}
	}

	@Override
	public List<MobEffectInstance> getEffects(ItemStack stack) {
		return XRPotionHelper.getPotionEffectsFromStack(stack);
	}
}
