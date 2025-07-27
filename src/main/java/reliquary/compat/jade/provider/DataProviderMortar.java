package reliquary.compat.jade.provider;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import reliquary.Reliquary;
import reliquary.block.ApothecaryMortarBlock;
import reliquary.block.tile.ApothecaryMortarBlockEntity;
import reliquary.init.ModItems;
import reliquary.util.TooltipBuilder;
import reliquary.util.potions.PotionHelper;
import reliquary.util.potions.PotionIngredient;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

import java.util.ArrayList;
import java.util.List;

public class DataProviderMortar extends CachedBodyDataProvider implements IServerDataProvider<BlockAccessor> {
	private static final String PESTLE_USED_COUNTER = "pestleUsedCounter";
	private PotionContents potionContents;

	@Override
	public List<List<IElement>> getWailaBodyToCache(IElementHelper helper, BlockAccessor accessor, IPluginConfig config) {
		List<List<IElement>> lines = new ArrayList<>();

		if (!(accessor.getBlock() instanceof ApothecaryMortarBlock && accessor.getBlockEntity() instanceof ApothecaryMortarBlockEntity mortar)) {
			return lines;
		}

		NonNullList<ItemStack> ingredientStacks = mortar.getItemStacks();
		List<IElement> ingredients = new ArrayList<>();
		List<PotionIngredient> potionIngredients = new ArrayList<>();
		for (ItemStack ingredientStack : ingredientStacks) {
			if (ingredientStack.isEmpty()) {
				continue;
			}
			ingredients.add(helper.item(ingredientStack));
			PotionHelper.getIngredient(ingredientStack).ifPresent(potionIngredients::add);
		}
		lines.add(ingredients);

		potionContents = PotionHelper.combineIngredients(potionIngredients);
		List<Component> effectTooltips = new ArrayList<>();

		if (potionContents.hasEffects()) {
			int pestleUsedCounter = accessor.getServerData().getInt(PESTLE_USED_COUNTER);
			lines.add(createPestleProgress(helper, pestleUsedCounter));

			TooltipBuilder.of(effectTooltips, Item.TooltipContext.of(mortar.getLevel())).potionEffects(potionContents);
			lines.addAll(effectTooltips.stream().map(text -> List.<IElement>of(helper.text(text))).toList());
		}
		return lines;
	}

	public List<IElement> createPestleProgress(IElementHelper helper, int pestleUsedCounter) {
		ItemStack stack = ModItems.POTION_ESSENCE.get().getDefaultInstance();
		PotionHelper.addPotionContentsToStack(stack, potionContents);

		return List.of(
				helper.progress((float) pestleUsedCounter / ApothecaryMortarBlockEntity.PESTLE_USAGE_MAX),
				helper.item(stack)
		);
	}

	@Override
	public List<List<IElement>> updateCache(IElementHelper helper, BlockAccessor accessor, List<List<IElement>> cached) {
		if (cached.size() > 1) {
			int pestleUsedCounter = accessor.getServerData().getInt(PESTLE_USED_COUNTER);
			cached.set(1, createPestleProgress(helper, pestleUsedCounter));
		}
		return cached;
	}

	@Override
	public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
		ApothecaryMortarBlockEntity be = (ApothecaryMortarBlockEntity) blockAccessor.getBlockEntity();
		compoundTag.putInt(PESTLE_USED_COUNTER, be.getPestleUsedCounter());
	}

	@Override
	public ResourceLocation getUid() {
		return Reliquary.getRL("mortar");
	}
}