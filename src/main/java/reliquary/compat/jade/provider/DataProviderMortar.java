package reliquary.compat.jade.provider;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import reliquary.blocks.ApothecaryMortarBlock;
import reliquary.blocks.tile.ApothecaryMortarBlockEntity;
import reliquary.init.ModItems;
import reliquary.reference.Reference;
import reliquary.util.potions.PotionIngredient;
import reliquary.util.potions.XRPotionHelper;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.impl.ui.ProgressArrowElement;

import java.util.ArrayList;
import java.util.List;

public class DataProviderMortar extends CachedBodyDataProvider implements IServerDataProvider<BlockEntity> {
	private static final String PESTLE_USED_COUNTER = "pestleUsedCounter";
	private List<MobEffectInstance> effects;

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
			XRPotionHelper.getIngredient(ingredientStack).ifPresent(potionIngredients::add);
		}
		lines.add(ingredients);

		effects = XRPotionHelper.combineIngredients(potionIngredients);
		List<Component> effectTooltips = new ArrayList<>();

		if (!effects.isEmpty()) {
			int pestleUsedCounter = accessor.getServerData().getInt(PESTLE_USED_COUNTER);
			lines.add(createPestleProgress(helper, pestleUsedCounter));

			XRPotionHelper.addPotionTooltip(effects, effectTooltips);
			lines.addAll(effectTooltips.stream().map(text -> List.of(helper.text(text))).toList());
		}
		return lines;
	}

	public List<IElement> createPestleProgress(IElementHelper helper, int pestleUsedCounter) {
		ItemStack stack = ModItems.POTION_ESSENCE.get().getDefaultInstance();
		XRPotionHelper.addPotionEffectsToStack(stack, effects);

		return List.of(
				new ProgressArrowElement((float) pestleUsedCounter / ApothecaryMortarBlockEntity.PESTLE_USAGE_MAX),
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
	public void appendServerData(CompoundTag data, ServerPlayer player, Level world, BlockEntity t, boolean showDetails) {
		ApothecaryMortarBlockEntity be = (ApothecaryMortarBlockEntity) t;
		data.putInt(PESTLE_USED_COUNTER, be.getPestleUsedCounter());
	}

	@Override
	public ResourceLocation getUid() {
		return new ResourceLocation(Reference.MOD_ID, "mortar");
	}
}