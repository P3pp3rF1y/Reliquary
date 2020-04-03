package xreliquary.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xreliquary.items.util.IPotionItem;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.PotionMap;
import xreliquary.util.potions.XRPotionHelper;

import javax.annotation.Nullable;
import java.util.List;

public class PotionItemBase extends ItemBase implements IPotionItem {
	public PotionItemBase(String registryName) {
		super(registryName, new Properties());
	}

	// returns an empty vial when used in crafting recipes, unless it's one of
	// the base potion types.
	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return !XRPotionHelper.getPotionEffectsFromStack(stack).isEmpty();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack potion, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		XRPotionHelper.addPotionTooltip(potion, tooltip);
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if (!isInGroup(group)) {
			return;
		}
		for (PotionEssence essence : PotionMap.uniquePotions) {
			ItemStack potion = new ItemStack(this, 1);
			XRPotionHelper.addPotionEffectsToStack(potion, essence.getEffects());
			items.add(potion);
		}
	}

	@Override
	public List<EffectInstance> getEffects(ItemStack stack) {
		return XRPotionHelper.getPotionEffectsFromStack(stack);
	}
}
