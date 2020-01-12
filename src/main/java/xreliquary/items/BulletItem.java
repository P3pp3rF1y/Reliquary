package xreliquary.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
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

	public BulletItem(String name, boolean hasTooltip, boolean addPotionBulletsInItemGroup, int color) {
		super(name, new Properties().setNoRepair());
		this.hasTooltip = hasTooltip;
		this.addPotionBulletsInItemGroup = addPotionBulletsInItemGroup;
		this.color = color;
		addPropertyOverride(new ResourceLocation("potion"), (stack, world, livingEntity) -> isPotionAttached(stack) ? 1 : 0);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		if (hasTooltip) {
			LanguageHelper.formatTooltip(getRegistryName() + ".tooltip", null, tooltip);
		}
		XRPotionHelper.addPotionTooltip(stack, tooltip);
	}

	private boolean isPotionAttached(ItemStack stack) {
		return !XRPotionHelper.getPotionEffectsFromStack(stack).isEmpty();
	}

	@Override
	public void fillItemGroup(ItemGroup itemGroup, NonNullList<ItemStack> items) {
		if (!isInGroup(itemGroup)) {
			return;
		}

		items.add(new ItemStack(this));

		if (!addPotionBulletsInItemGroup) {
			return;
		}

		for (PotionEssence essence : PotionMap.uniquePotionEssences) {
			ItemStack neutralBulletWithPotion = new ItemStack(this);
			XRPotionHelper.addPotionEffectsToStack(neutralBulletWithPotion, XRPotionHelper.changePotionEffectsDuration(essence.getEffects(), 0.2F));

			items.add(neutralBulletWithPotion);
		}
	}

	@Override
	public List<EffectInstance> getEffects(ItemStack stack) {
		return XRPotionHelper.getPotionEffectsFromStack(stack);
	}

	public int getColor() {
		return color;
	}
}
