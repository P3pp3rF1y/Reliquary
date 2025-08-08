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
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.Element;
import snownee.jade.api.ui.JadeUI;
import snownee.jade.api.view.ProgressView;

import java.util.ArrayList;
import java.util.List;

public class DataProviderMortar implements IServerDataProvider<BlockAccessor> {
	private static final String PESTLE_USED_COUNTER = "pestleUsedCounter";
	public static final ResourceLocation UID = Reliquary.getRL("mortar");

	@Override
	public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
		ApothecaryMortarBlockEntity be = (ApothecaryMortarBlockEntity) blockAccessor.getBlockEntity();
		compoundTag.putInt(PESTLE_USED_COUNTER, be.getPestleUsedCounter());
	}

	@Override
	public ResourceLocation getUid() {
		return UID;
	}

	public static class Client extends CachedComponentProvider {
		public static final Client INSTANCE = new Client();
		private PotionContents potionContents;

		@Override
		public List<List<Element>> getWailaBodyToCache(BlockAccessor accessor, IPluginConfig config) {
			List<List<Element>> lines = new ArrayList<>();

			if (!(accessor.getBlock() instanceof ApothecaryMortarBlock && accessor.getBlockEntity() instanceof ApothecaryMortarBlockEntity mortar)) {
				return lines;
			}

			NonNullList<ItemStack> ingredientStacks = mortar.getItemStacks();
			List<Element> ingredients = new ArrayList<>();
			List<PotionIngredient> potionIngredients = new ArrayList<>();
			for (ItemStack ingredientStack : ingredientStacks) {
				if (ingredientStack.isEmpty()) {
					continue;
				}
				ingredients.add(JadeUI.item(ingredientStack));
				PotionHelper.getIngredient(ingredientStack).ifPresent(potionIngredients::add);
			}
			lines.add(ingredients);

			potionContents = PotionHelper.combineIngredients(potionIngredients);
			List<Component> effectTooltips = new ArrayList<>();

			if (potionContents.hasEffects()) {
				int pestleUsedCounter = accessor.getServerData().getIntOr(PESTLE_USED_COUNTER, 0);
				lines.add(createPestleProgress(pestleUsedCounter));

				TooltipBuilder.of(effectTooltips::add, Item.TooltipContext.of(mortar.getLevel())).potionEffects(potionContents);
				lines.addAll(effectTooltips.stream().map(text -> List.<Element>of(JadeUI.text(text))).toList());
			}
			return lines;
		}

		@Override
		public List<List<Element>> updateCache(BlockAccessor accessor, List<List<Element>> cached) {
			if (cached.size() > 1) {
				int pestleUsedCounter = accessor.getServerData().getIntOr(PESTLE_USED_COUNTER, 0);
				cached.set(1, createPestleProgress(pestleUsedCounter));
			}
			return cached;
		}

		public List<Element> createPestleProgress(int pestleUsedCounter) {
			ItemStack stack = ModItems.POTION_ESSENCE.get().getDefaultInstance();
			PotionHelper.addPotionContentsToStack(stack, potionContents);

			return List.of(
					JadeUI.progressArrow((float) pestleUsedCounter / ApothecaryMortarBlockEntity.PESTLE_USAGE_MAX),
					JadeUI.item(stack)
			);
		}

		@Override
		public ResourceLocation getUid() {
			return UID;
		}
	}
}