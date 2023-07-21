package reliquary.compat.jade.provider;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import reliquary.blocks.ApothecaryCauldronBlock;
import reliquary.blocks.tile.ApothecaryCauldronBlockEntity;
import reliquary.reference.Reference;
import reliquary.util.TooltipBuilder;
import snownee.jade.addon.vanilla.VanillaPlugin;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IBorderStyle;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.api.ui.IProgressStyle;

import java.util.ArrayList;
import java.util.List;

public class DataProviderCauldron extends CachedBodyDataProvider {
	@Override
	public List<List<IElement>> getWailaBodyToCache(IElementHelper helper, BlockAccessor accessor, IPluginConfig config) {
		List<List<IElement>> lines = new ArrayList<>();

		if (!(accessor.getBlock() instanceof ApothecaryCauldronBlock &&
				accessor.getBlockEntity() instanceof ApothecaryCauldronBlockEntity cauldron)) {
			return List.of();
		}

		if (cauldron.getEffects().isEmpty()) {
			return List.of();
		}

		List<IElement> ingredientHints = new ArrayList<>();
		if (!cauldron.hasNetherwart()) {
			ingredientHints.add(helper.item(Items.NETHER_WART.getDefaultInstance(), JadeHelper.ITEM_ICON_SCALE, JadeHelper.MISSING));
		} else {
			ingredientHints.add(helper.item(Items.NETHER_WART.getDefaultInstance(), JadeHelper.ITEM_ICON_SCALE, JadeHelper.SATISFIED));
		}

		if (cauldron.hasDragonBreath()) {
			if (!cauldron.hasGunpowder()) {
				ingredientHints.add(helper.item(Items.GUNPOWDER.getDefaultInstance(), JadeHelper.ITEM_ICON_SCALE, JadeHelper.MISSING));
			} else {
				ingredientHints.add(helper.item(Items.GUNPOWDER.getDefaultInstance(), JadeHelper.ITEM_ICON_SCALE, JadeHelper.SATISFIED));
			}
			ingredientHints.add(helper.item(Items.DRAGON_BREATH.getDefaultInstance(), JadeHelper.ITEM_ICON_SCALE, JadeHelper.SATISFIED));
		}

		lines.add(ingredientHints);

		List<IElement> ingredients1 = new ArrayList<>();
		if (cauldron.getRedstoneCount() > 0) {
			ItemStack stack = new ItemStack(Items.REDSTONE, cauldron.getRedstoneCount());
			ingredients1.add(helper.item(stack));
		}
		if (cauldron.getGlowstoneCount() > 0) {
			ItemStack stack = new ItemStack(Items.GLOWSTONE_DUST, cauldron.getGlowstoneCount());
			ingredients1.add(helper.item(stack));
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
		lines.add(createTank(helper, fluidPlaceHolder, FluidType.BUCKET_VOLUME, potionType));

		List<Component> components = new ArrayList<>();
		TooltipBuilder.of(components).potionEffects(cauldron.getEffects());
		lines.add(components.stream().map(helper::text).toList());
		return lines;
	}

	public static List<IElement> createTank(IElementHelper helper, FluidStack fluidStack, int capacity, Component displayName) {
		if (displayName == FormattedText.EMPTY) {
			displayName = fluidStack.getDisplayName();
		}
		if (capacity <= 0) {return List.of();}
		Component text;
		if (fluidStack.isEmpty()) {
			text = Component.translatable("jade.fluid.empty");
		} else {
			String amountText = VanillaPlugin.getDisplayHelper().humanReadableNumber(fluidStack.getAmount(), "B", true);
			text = Component.translatable("jade.fluid", displayName, amountText);
		}
		IProgressStyle progressStyle = helper.progressStyle();
		progressStyle.overlay(helper.fluid(fluidStack));

		IBorderStyle borderStyle = helper.borderStyle();

		IElement tank = helper.progress((float) fluidStack.getAmount() / capacity, text, progressStyle, borderStyle);
		return List.of(tank);
	}

	@Override
	public ResourceLocation getUid() {
		return new ResourceLocation(Reference.MOD_ID, "cauldron");
	}
}
