package reliquary.compat.jade.provider;

import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.block.state.BlockState;
import reliquary.blocks.PedestalBlock;
import reliquary.blocks.tile.PedestalBlockEntity;

public class DataProviderPedestal implements IComponentProvider {

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig pluginConfig) {
        if(!(accessor.getBlock() instanceof PedestalBlock && accessor.getBlockEntity() instanceof PedestalBlockEntity pedestal)) {
            return;
        }

        BlockState pedestalState = accessor.getBlockState();

        if(pedestalState.getValue(PedestalBlock.ENABLED)) {
            tooltip.add(new TranslatableComponent("tooltip.waila.state", new TranslatableComponent("tooltip.waila.state_on").withStyle(ChatFormatting.GREEN)));

            if(pedestal.switchedOn()) {
                tooltip.add(new TranslatableComponent("waila.reliquary.pedestal.switched_on"));
            }
            if(pedestal.isPowered()) {
                tooltip.add(new TranslatableComponent("waila.reliquary.pedestal.redstone_powered"));
            }
            if(pedestal.getOnSwitches().size() > 0) {
                for(long loc : pedestal.getOnSwitches()) {
                    BlockPos pos = BlockPos.of(loc);
                    tooltip.add(new TranslatableComponent("waila.reliquary.pedestal.remote_at", pos.getX(), pos.getY(), pos.getZ()));
                }
            }
        } else {
            tooltip.add(new TranslatableComponent("tooltip.waila.state", new TranslatableComponent("tooltip.waila.state_off").withStyle(ChatFormatting.RED)));
        }
    }

}