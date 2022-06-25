package reliquary.compat.jade.provider;

import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import mcp.mobius.waila.api.ui.IElement;
import mcp.mobius.waila.impl.ui.ItemStackElement;
import mcp.mobius.waila.impl.ui.TextElement;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import reliquary.blocks.AlkahestryAltarBlock;
import reliquary.blocks.tile.AlkahestryAltarBlockEntity;
import reliquary.reference.Settings;
import reliquary.util.LanguageHelper;

import java.text.SimpleDateFormat;
import java.util.List;

public class DataProviderAltar implements IComponentProvider, IServerDataProvider<BlockEntity> {
    @Override
    public @Nullable IElement getIcon(BlockAccessor accessor, IPluginConfig config, IElement currentIcon) {
        return IComponentProvider.super.getIcon(accessor, config, currentIcon);
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig pluginConfig) {
        if(Settings.CLIENT.wailaShiftForInfo.get() && !accessor.getPlayer().isCrouching()) {
            tooltip.add(Component.nullToEmpty(ChatFormatting.ITALIC + LanguageHelper.getLocalization("waila.reliquary.shift_for_more") + ChatFormatting.RESET));
            return;
        }

        if(!(accessor.getBlock() instanceof AlkahestryAltarBlock && accessor.getBlockEntity() instanceof AlkahestryAltarBlockEntity))
            return;

        AlkahestryAltarBlockEntity altar = (AlkahestryAltarBlockEntity) accessor.getBlockEntity();

        if(!altar.isActive()) {
            tooltip.add(Component.nullToEmpty(ChatFormatting.RED + LanguageHelper.getLocalization("waila.reliquary.altar.inactive") + ChatFormatting.RESET));
            tooltip.add(List.of(
                    ItemStackElement.of(new ItemStack(Items.REDSTONE, 1), JadeHelper.ITEM_ICON_SCALE),
                    new TextElement(new TextComponent(String.format("%d / %d", altar.getRedstoneCount(), Settings.COMMON.blocks.altar.redstoneCost.get())))
            ));
            return;
        }

        tooltip.add(Component.nullToEmpty(ChatFormatting.GREEN + LanguageHelper.getLocalization("waila.reliquary.altar.active")));
        int cycleTime = accessor.getServerData().getInt("cycleTime"); // altar.getCycleTime(
        tooltip.add(Component.nullToEmpty(LanguageHelper.getLocalization("waila.reliquary.altar.time_remaining", new SimpleDateFormat("mm:ss").format(cycleTime * 50))));
    }

    @Override
    public void appendServerData(CompoundTag data, ServerPlayer player, Level world, BlockEntity t, boolean showDetails) {
        // isActive and redstoneCount is synced, so only cycle time needs to be synced here
        AlkahestryAltarBlockEntity be = (AlkahestryAltarBlockEntity) t;
        data.putInt("cycleTime", be.getCycleTime());
    }
}
