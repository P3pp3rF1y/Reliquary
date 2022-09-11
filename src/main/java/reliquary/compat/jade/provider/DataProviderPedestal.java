package reliquary.compat.jade.provider;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import reliquary.blocks.PedestalBlock;
import reliquary.blocks.tile.PedestalBlockEntity;
import reliquary.reference.Reference;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class DataProviderPedestal implements IBlockComponentProvider {

	private static final ResourceLocation PEDESTAL_UID = new ResourceLocation(Reference.MOD_ID, "pedestal");

	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig pluginConfig) {
		if (!(accessor.getBlock() instanceof PedestalBlock && accessor.getBlockEntity() instanceof PedestalBlockEntity pedestal)) {
			return;
		}

		BlockState pedestalState = accessor.getBlockState();

		if (Boolean.TRUE.equals(pedestalState.getValue(PedestalBlock.ENABLED))) {
			tooltip.add(Component.translatable("waila.reliquary.pedestal.state", Component.translatable("waila.reliquary.pedestal.state.on").withStyle(ChatFormatting.GREEN)));

			if (pedestal.switchedOn()) {
				tooltip.add(Component.translatable("waila.reliquary.pedestal.switched_on"));
			}
			if (pedestal.isPowered()) {
				tooltip.add(Component.translatable("waila.reliquary.pedestal.redstone_powered"));
			}
			if (!pedestal.getOnSwitches().isEmpty()) {
				for (long loc : pedestal.getOnSwitches()) {
					BlockPos pos = BlockPos.of(loc);
					tooltip.add(Component.translatable("waila.reliquary.pedestal.remote_at", pos.getX(), pos.getY(), pos.getZ()));
				}
			}
		} else {
			tooltip.add(Component.translatable("waila.reliquary.pedestal.state", Component.translatable("waila.reliquary.pedestal.state.off").withStyle(ChatFormatting.RED)));
		}
	}

	@Override
	public ResourceLocation getUid() {
		return PEDESTAL_UID;
	}
}