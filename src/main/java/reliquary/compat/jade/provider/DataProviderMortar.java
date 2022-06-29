package reliquary.compat.jade.provider;

import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.config.IPluginConfig;
import mcp.mobius.waila.api.ui.IElement;
import mcp.mobius.waila.api.ui.IElementHelper;
import mcp.mobius.waila.impl.ui.ProgressArrowElement;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import reliquary.blocks.ApothecaryMortarBlock;
import reliquary.blocks.tile.ApothecaryMortarBlockEntity;
import reliquary.init.ModItems;
import reliquary.util.potions.PotionIngredient;
import reliquary.util.potions.XRPotionHelper;

import java.util.ArrayList;
import java.util.List;

public class DataProviderMortar extends CachedBodyDataProvider implements IServerDataProvider<BlockEntity> {
    private List<MobEffectInstance> effects;

    @Override
    public List<List<IElement>> getWailaBodyToCache(IElementHelper helper, BlockAccessor accessor, IPluginConfig config) {
        List<List<IElement>> lines = new ArrayList<>();

        if(!(accessor.getBlock() instanceof ApothecaryMortarBlock &&
                accessor.getBlockEntity() instanceof ApothecaryMortarBlockEntity mortar))
            return lines;

        NonNullList<ItemStack> ingredientStacks = mortar.getItemStacks();
        List<IElement> ingredients = new ArrayList<>();
        List<PotionIngredient> potionIngredients = new ArrayList<>();
        for (ItemStack ingredientStack : ingredientStacks) {
            if (ingredientStack.isEmpty()) continue;
            ingredients.add(helper.item(ingredientStack));
            potionIngredients.add(XRPotionHelper.getIngredient(ingredientStack).get());
        }
        lines.add(ingredients);

        effects = XRPotionHelper.combineIngredients(potionIngredients);
        List<Component> effectTooltips = new ArrayList<>();

        if(!effects.isEmpty()) {
            int pestleUsedCounter = accessor.getServerData().getInt("pestleUsedCounter");
            lines.add(createPestleProgress(helper, pestleUsedCounter));

            XRPotionHelper.addPotionTooltip(effects, effectTooltips);
            lines.addAll(effectTooltips.stream().map(text -> List.of(helper.text(text))).toList());
        }
        return lines;
    }

    public List<IElement> createPestleProgress(IElementHelper helper, int pestleUsedCounter)
    {
        ItemStack stack = ModItems.POTION_ESSENCE.get().getDefaultInstance();
        XRPotionHelper.addPotionEffectsToStack(stack, effects);

        return List.of(
                new ProgressArrowElement((float)pestleUsedCounter / ApothecaryMortarBlockEntity.PESTLE_USAGE_MAX),
                helper.item(stack)
        );
    }

    @Override
    public List<List<IElement>> updateCache(IElementHelper helper, BlockAccessor accessor, List<List<IElement>> cached) {
        if (cached.size() > 1) {
            int pestleUsedCounter = accessor.getServerData().getInt("pestleUsedCounter");
            cached.set(1, createPestleProgress(helper, pestleUsedCounter));
        }
        return cached;
    }

    @Override
    public void appendServerData(CompoundTag data, ServerPlayer player, Level world, BlockEntity t, boolean showDetails) {
        ApothecaryMortarBlockEntity be = (ApothecaryMortarBlockEntity) t;
        data.putInt("pestleUsedCounter", be.getPestleUsedCounter());
    }
}