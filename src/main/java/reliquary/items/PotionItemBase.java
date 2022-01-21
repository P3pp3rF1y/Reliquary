package reliquary.items;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import reliquary.items.util.IPotionItem;
import reliquary.reference.Settings;
import reliquary.util.potions.PotionEssence;
import reliquary.util.potions.PotionMap;
import reliquary.util.potions.XRPotionHelper;

import javax.annotation.Nullable;
import java.util.List;

public class PotionItemBase extends ItemBase implements IPotionItem {
	public PotionItemBase() {
		super(new Properties());
	}

	// returns an empty vial when used in crafting recipes, unless it's one of
	// the base potion types.
	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return !XRPotionHelper.getPotionEffectsFromStack(stack).isEmpty();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack potion, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
		XRPotionHelper.addPotionTooltip(potion, tooltip);
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		if (!allowdedIn(group) || Boolean.TRUE.equals(Settings.COMMON.disable.disablePotions.get())) {
			return;
		}
		for (PotionEssence essence : PotionMap.uniquePotions) {
			ItemStack potion = new ItemStack(this, 1);
			XRPotionHelper.addPotionEffectsToStack(potion, essence.getEffects());
			items.add(potion);
		}
	}

	@Override
	public List<MobEffectInstance> getEffects(ItemStack stack) {
		return XRPotionHelper.getPotionEffectsFromStack(stack);
	}
}
