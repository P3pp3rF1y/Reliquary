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
		TooltipBuilder tooltipBuilder = TooltipBuilder.of(tooltip);
		if (hasTooltip) {
			tooltipBuilder.itemTooltip(this);
		}
		tooltipBuilder.potionEffects(stack);
	}

	@Override
	public void addCreativeTabItems(Consumer<ItemStack> itemConsumer) {
		if (Boolean.TRUE.equals(Settings.COMMON.disable.disableHandgun.get())) {
			return;
		}

		itemConsumer.accept(new ItemStack(this));

		if (!addPotionBulletsInItemGroup || Boolean.TRUE.equals(Settings.COMMON.disable.disablePotions.get())) {
			return;
		}

		for (PotionEssence essence : PotionMap.uniquePotionEssences) {
			ItemStack bullet = new ItemStack(this);
			XRPotionHelper.addPotionEffectsToStack(bullet, XRPotionHelper.changePotionEffectsDuration(essence.getEffects(), 0.2F));

			itemConsumer.accept(bullet);
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
