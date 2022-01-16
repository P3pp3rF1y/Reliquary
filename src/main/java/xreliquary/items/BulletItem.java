package xreliquary.items;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xreliquary.items.util.IPotionItem;
import xreliquary.reference.Settings;
import xreliquary.util.LanguageHelper;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.PotionMap;
import xreliquary.util.potions.XRPotionHelper;

import javax.annotation.Nullable;
import java.util.List;

public class BulletItem extends ItemBase implements IPotionItem {
	private final boolean hasTooltip;
	private final boolean addPotionBulletsInItemGroup;
	private final int color;

	public BulletItem(boolean hasTooltip, boolean addPotionBulletsInItemGroup, int color) {
		super(new Properties().setNoRepair());
		this.hasTooltip = hasTooltip;
		this.addPotionBulletsInItemGroup = addPotionBulletsInItemGroup;
		this.color = color;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
		if (hasTooltip) {
			LanguageHelper.formatTooltip(getDescriptionId() + ".tooltip", null, tooltip);
		}
		XRPotionHelper.addPotionTooltip(stack, tooltip);
	}

	@Override
	public void fillItemCategory(CreativeModeTab itemGroup, NonNullList<ItemStack> items) {
		if (!allowdedIn(itemGroup) || Boolean.TRUE.equals(Settings.COMMON.disable.disableHandgun.get())) {
			return;
		}

		items.add(new ItemStack(this));

		if (!addPotionBulletsInItemGroup || Boolean.TRUE.equals(Settings.COMMON.disable.disablePotions.get())) {
			return;
		}

		for (PotionEssence essence : PotionMap.uniquePotionEssences) {
			ItemStack bullet = new ItemStack(this);
			XRPotionHelper.addPotionEffectsToStack(bullet, XRPotionHelper.changePotionEffectsDuration(essence.getEffects(), 0.2F));

			items.add(bullet);
		}
	}

	@Override
	public List<MobEffectInstance> getEffects(ItemStack stack) {
		return XRPotionHelper.getPotionEffectsFromStack(stack);
	}

	public int getColor() {
		return color;
	}
}
