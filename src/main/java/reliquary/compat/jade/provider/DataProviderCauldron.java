package reliquary.compat.jade.provider;

import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.config.IPluginConfig;
import mcp.mobius.waila.api.ui.IBorderStyle;
import mcp.mobius.waila.api.ui.IElement;
import mcp.mobius.waila.api.ui.IElementHelper;
import mcp.mobius.waila.api.ui.IProgressStyle;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import reliquary.blocks.ApothecaryCauldronBlock;
import reliquary.blocks.tile.ApothecaryCauldronBlockEntity;
import reliquary.util.potions.XRPotionHelper;
import snownee.jade.VanillaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataProviderCauldron extends CachedBodyDataProvider {
	@Override
    public List<List<IElement>> getWailaBodyToCache(IElementHelper helper, BlockAccessor accessor, IPluginConfig config) {
		List<List<IElement>> lines = new ArrayList<>();

		if(!(accessor.getBlock() instanceof ApothecaryCauldronBlock &&
				accessor.getBlockEntity() instanceof ApothecaryCauldronBlockEntity cauldron))
			return List.of();

		if(cauldron.getEffects().isEmpty())
			return List.of();

		List<IElement> ingredientHints = new ArrayList<>();
		if(!cauldron.hasNetherwart()) {
			ingredientHints.add(helper.item(Items.NETHER_WART.getDefaultInstance(), JadeHelper.ITEM_ICON_SCALE, JadeHelper.MISSING));
		}
		else {
			ingredientHints.add(helper.item(Items.NETHER_WART.getDefaultInstance(), JadeHelper.ITEM_ICON_SCALE, JadeHelper.SATISFIED));
		}


		if(cauldron.hasDragonBreath()) {
			if(!cauldron.hasGunpowder()) {
				ingredientHints.add(helper.item(Items.GUNPOWDER.getDefaultInstance(), JadeHelper.ITEM_ICON_SCALE, JadeHelper.MISSING));
			}
			else {
				ingredientHints.add(helper.item(Items.GUNPOWDER.getDefaultInstance(), JadeHelper.ITEM_ICON_SCALE, JadeHelper.SATISFIED));
			}
			ingredientHints.add(helper.item(Items.DRAGON_BREATH.getDefaultInstance(), JadeHelper.ITEM_ICON_SCALE, JadeHelper.SATISFIED));
		}

		lines.add(ingredientHints);

		List<IElement> ingredients1 = new ArrayList<>();
		if(cauldron.getRedstoneCount() > 0) {
			ItemStack stack = new ItemStack(Items.REDSTONE, cauldron.getRedstoneCount());
			ingredients1.add(helper.item(stack));
		}
		if(cauldron.getGlowstoneCount() > 0) {
			ItemStack stack = new ItemStack(Items.GLOWSTONE_DUST, cauldron.getGlowstoneCount());
			ingredients1.add(helper.item(stack));
		}
		lines.add(ingredients1);

		FluidStack fluidPlaceHolder = new FluidStack(Fluids.WATER, FluidAttributes.BUCKET_VOLUME * cauldron.getLiquidLevel() / 3);
		Component potionType;
		if (cauldron.hasDragonBreath()) {
			potionType = new TranslatableComponent("waila.reliquary.cauldron.lingering");
		}
		else if(cauldron.hasGunpowder()) {
			potionType = new TranslatableComponent("waila.reliquary.cauldron.splash");
		} else {
			potionType = new TranslatableComponent("waila.reliquary.cauldron.potion");
		}
		lines.add(createTank(helper, fluidPlaceHolder, FluidAttributes.BUCKET_VOLUME, potionType));

		List<Component> components = new ArrayList<>();
		XRPotionHelper.addPotionTooltip(cauldron.getEffects(), components);
		lines.add(components.stream().map(helper::text).collect(Collectors.toList()));
		return lines;
	}

	public static List<IElement> createTank(IElementHelper helper, FluidStack fluidStack, int capacity, Component displayName) {
		if (displayName == Component.EMPTY) {
			displayName = fluidStack.getDisplayName();
		}
		if (capacity <= 0)
			return List.of();
		Component text;
		if (fluidStack.isEmpty()) {
			text = new TranslatableComponent("jade.fluid.empty");
		} else {
			String amountText = VanillaPlugin.getDisplayHelper().humanReadableNumber(fluidStack.getAmount(), "B", true);
			text = new TranslatableComponent("jade.fluid", displayName, amountText);
		}
		IProgressStyle progressStyle = helper.progressStyle();
		progressStyle.overlay(helper.fluid(fluidStack));

		IBorderStyle borderStyle = helper.borderStyle();

		IElement tank = helper.progress((float) fluidStack.getAmount() / capacity, text, progressStyle, borderStyle);
		return List.of(tank);
	}
}
