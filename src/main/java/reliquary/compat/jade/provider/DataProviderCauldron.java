package reliquary.compat.jade.provider;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import reliquary.Reliquary;
import reliquary.block.ApothecaryCauldronBlock;
import reliquary.block.tile.ApothecaryCauldronBlockEntity;
import reliquary.util.TooltipBuilder;
import reliquary.util.potions.PotionHelper;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.fluid.JadeFluidObject;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.Element;
import snownee.jade.api.ui.IDisplayHelper;
import snownee.jade.api.ui.JadeUI;
import snownee.jade.api.view.ProgressView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataProviderCauldron extends CachedComponentProvider {
	@Override
	public List<List<Element>> getWailaBodyToCache(BlockAccessor accessor, IPluginConfig config) {
		List<List<Element>> lines = new ArrayList<>();

		if (!(accessor.getBlock() instanceof ApothecaryCauldronBlock && accessor.getBlockEntity() instanceof ApothecaryCauldronBlockEntity cauldron)) {
			return List.of();
		}

		if (!cauldron.getPotionContents().hasEffects()) {
			return List.of();
		}

		List<Element> ingredientHints = new ArrayList<>();
		if (!cauldron.hasNetherwart()) {
			ingredientHints.add(JadeUI.item(Items.NETHER_WART.getDefaultInstance(), JadeHelper.ITEM_ICON_SCALE, JadeHelper.MISSING));
		} else {
			ingredientHints.add(JadeUI.item(Items.NETHER_WART.getDefaultInstance(), JadeHelper.ITEM_ICON_SCALE, JadeHelper.SATISFIED));
		}

		if (cauldron.hasDragonBreath()) {
			if (!cauldron.hasGunpowder()) {
				ingredientHints.add(JadeUI.item(Items.GUNPOWDER.getDefaultInstance(), JadeHelper.ITEM_ICON_SCALE, JadeHelper.MISSING));
			} else {
				ingredientHints.add(JadeUI.item(Items.GUNPOWDER.getDefaultInstance(), JadeHelper.ITEM_ICON_SCALE, JadeHelper.SATISFIED));
			}
			ingredientHints.add(JadeUI.item(Items.DRAGON_BREATH.getDefaultInstance(), JadeHelper.ITEM_ICON_SCALE, JadeHelper.SATISFIED));
		}

		lines.add(ingredientHints);

		List<Element> ingredients1 = new ArrayList<>();
		if (cauldron.getRedstoneCount() > 0) {
			ItemStack stack = new ItemStack(Items.REDSTONE, cauldron.getRedstoneCount());
			ingredients1.add(JadeUI.item(stack));
		}
		if (cauldron.getGlowstoneCount() > 0) {
			ItemStack stack = new ItemStack(Items.GLOWSTONE_DUST, cauldron.getGlowstoneCount());
			ingredients1.add(JadeUI.item(stack));
		}
		lines.add(ingredients1);

		FluidStack fluidPlaceHolder = new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME * cauldron.getLiquidLevel() / 3);
		Component potionType;
		if (cauldron.hasDragonBreath()) {
			potionType = Component.translatable("waila.reliquary.cauldron.lingering");
		} else if (cauldron.hasGunpowder()) {
			potionType = Component.translatable("waila.reliquary.cauldron.splash");
		} else {
			potionType = Component.translatable("waila.reliquary.cauldron.potion");
		}
		lines.add(createTank(fluidPlaceHolder, FluidType.BUCKET_VOLUME, potionType));

		List<Component> components = new ArrayList<>();
		TooltipBuilder.of(components::add, Item.TooltipContext.of(cauldron.getLevel()))
				.potionEffects(PotionHelper.augmentPotionContents(cauldron.getPotionContents(), cauldron.getRedstoneCount(), cauldron.getGlowstoneCount()));

		lines.add(components.stream().map(JadeUI::text).collect(Collectors.toList()));
		return lines;
	}

	public static List<Element> createTank(FluidStack fluidStack, int capacity, Component displayName) {
		if (displayName == FormattedText.EMPTY) {
			displayName = fluidStack.getHoverName();
		}
		if (capacity <= 0) {
			return List.of();
		}
		Component text;
		if (fluidStack.isEmpty()) {
			text = Component.translatable("jade.fluid.empty");
		} else {
			String amountText = IDisplayHelper.get().humanReadableNumber(fluidStack.getAmount(), "B", true);
			text = Component.translatable("jade.fluid", displayName, amountText);
		}

		ProgressView progressView = new ProgressView(
				ProgressView.Part.of((float) fluidStack.getAmount() / capacity, JadeUI.fluid(JadeFluidObject.of(fluidStack.getFluid()))), text,
				JadeUI.progressStyle(), BoxStyle.nestedBox());

		Element tank = JadeUI.progress(progressView);
		return List.of(tank);
	}

	@Override
	public ResourceLocation getUid() {
		return Reliquary.getRL("cauldron");
	}
}
