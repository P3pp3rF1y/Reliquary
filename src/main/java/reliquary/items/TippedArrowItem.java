package reliquary.items;

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
import reliquary.Reliquary;
import reliquary.entities.XRTippedArrowEntity;
import reliquary.items.util.IPotionItem;
import reliquary.reference.Settings;
import reliquary.util.TooltipBuilder;
import reliquary.util.potions.PotionEssence;
import reliquary.util.potions.PotionMap;
import reliquary.util.potions.XRPotionHelper;

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
		if (!allowedIn(group) || Boolean.TRUE.equals(Settings.COMMON.disable.disablePotions.get())) {
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
		TooltipBuilder.of(tooltip).potionEffects(arrow);
	}

	@Override
	public List<MobEffectInstance> getEffects(ItemStack stack) {
		return XRPotionHelper.getPotionEffectsFromStack(stack);
	}
}
