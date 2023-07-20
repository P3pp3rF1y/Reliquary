package reliquary.items;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import reliquary.Reliquary;
import reliquary.util.TooltipBuilder;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class ItemBase extends Item {
	private final Supplier<Boolean> isDisabled;

	public ItemBase() {
		this(new Properties(), () -> false);
	}

	public ItemBase(Supplier<Boolean> isDisabled) {
		this(new Properties(), isDisabled);
	}

	public ItemBase(Properties properties) {
		this(properties, () -> false);
	}

	public ItemBase(Properties properties, Supplier<Boolean> isDisabled) {
		super(properties.tab(Reliquary.ITEM_GROUP));
		this.isDisabled = isDisabled;
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		if (Boolean.TRUE.equals(isDisabled.get())) {
			return;
		}
		super.fillItemCategory(group, items);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
		TooltipBuilder tooltipBuilder = TooltipBuilder.of(tooltip).itemTooltip(this);

		if (hasMoreInformation(stack)) {
			tooltipBuilder.showMoreInfo();
			if (Screen.hasShiftDown()) {
				addMoreInformation(stack, world, tooltipBuilder);
			}
		}
	}

	@SuppressWarnings("squid:S1172") //parameter used in overrides
	protected boolean hasMoreInformation(ItemStack stack) {
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack stack, @Nullable Level world, TooltipBuilder tooltipBuilder) {
		//overriden in child classes
	}

	@Override
	public MutableComponent getName(ItemStack stack) {
		return Component.translatable(getDescriptionId(stack));
	}
}

