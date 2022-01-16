package xreliquary.items;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import xreliquary.Reliquary;
import xreliquary.entities.XRTippedArrowEntity;
import xreliquary.items.util.IPotionItem;
import xreliquary.reference.Settings;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.PotionMap;
import xreliquary.util.potions.XRPotionHelper;

import javax.annotation.Nullable;
import java.util.List;

public class TippedArrowItem extends ArrowItem implements IPotionItem {
	public TippedArrowItem() {
		super(new Properties().tab(Reliquary.ITEM_GROUP));
	}

	@Override
	public AbstractArrow createArrow(Level world, ItemStack stack, LivingEntity shooter) {
		XRTippedArrowEntity entitytippedarrow = new XRTippedArrowEntity(world, shooter);
		entitytippedarrow.setPotionEffect(stack);
		return entitytippedarrow;
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		if (!allowdedIn(group) || Boolean.TRUE.equals(Settings.COMMON.disable.disablePotions.get())) {
			return;
		}

		for (PotionEssence essence : PotionMap.uniquePotionEssences) {
			ItemStack tippedArrow = new ItemStack(this);
			XRPotionHelper.addPotionEffectsToStack(tippedArrow, XRPotionHelper.changePotionEffectsDuration(essence.getEffects(), 0.125F));

			items.add(tippedArrow);
		}
	}

	@Override
	public void appendHoverText(ItemStack arrow, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
		XRPotionHelper.addPotionTooltip(arrow, tooltip);
	}

	@Override
	public List<MobEffectInstance> getEffects(ItemStack stack) {
		return XRPotionHelper.getPotionEffectsFromStack(stack);
	}
}
