package reliquary.compat.jade.provider;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec2;
import reliquary.Reliquary;
import reliquary.block.AlkahestryAltarBlock;
import reliquary.block.tile.AlkahestryAltarBlockEntity;
import reliquary.reference.Config;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.Element;
import snownee.jade.api.ui.JadeUI;

import javax.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.List;

public class DataProviderAltar implements IServerDataProvider<BlockAccessor> {

	private static final ResourceLocation ALTAR_UID = Reliquary.getRL("altar");

	@Override
	public ResourceLocation getUid() {
		return ALTAR_UID;
	}

	@Override
	public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
		// isActive and redstoneCount is synced, so only cycle time needs to be synced here
		AlkahestryAltarBlockEntity altar = (AlkahestryAltarBlockEntity) blockAccessor.getBlockEntity();
		compoundTag.putInt("cycleTime", altar.getCycleTime());
	}

	public static class Client implements IBlockComponentProvider {
		public static final Client INSTANCE = new Client();

		@Override
		public @Nullable Element getIcon(BlockAccessor accessor, IPluginConfig config, Element currentIcon) {
			return IBlockComponentProvider.super.getIcon(accessor, config, currentIcon);
		}

		@Override
		public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig pluginConfig) {
			if (Boolean.TRUE.equals(Config.CLIENT.wailaShiftForInfo.get()) && !accessor.getPlayer().isCrouching()) {
				tooltip.add(Component.translatable("waila.reliquary.shift_for_more").withStyle(ChatFormatting.ITALIC));
				return;
			}

			if (!(accessor.getBlock() instanceof AlkahestryAltarBlock && accessor.getBlockEntity() instanceof AlkahestryAltarBlockEntity altar)) {
				return;
			}

			if (!altar.isActive()) {
				tooltip.add(Component.translatable("waila.reliquary.altar.inactive").withStyle(ChatFormatting.RED));

				Vec2 delta = new Vec2(0, -4);
				Element redstoneIcon = JadeUI.item(Items.REDSTONE.getDefaultInstance(), JadeHelper.ITEM_ICON_SCALE);
				Element requirementText = JadeUI
						.text(Component.literal(String.format("%d / %d", altar.getRedstoneCount(), Config.COMMON.blocks.altar.redstoneCost.get())))
						.offset(0, 4);
				tooltip.add(List.of(redstoneIcon, requirementText));
				return;
			}

			tooltip.add(Component.translatable("waila.reliquary.altar.active").withStyle(ChatFormatting.GREEN));
			int cycleTime = accessor.getServerData().getIntOr("cycleTime", 0);
			tooltip.add(Component.translatable("waila.reliquary.altar.time_remaining", new SimpleDateFormat("mm:ss").format(cycleTime * 50)));
		}

		@Override
		public ResourceLocation getUid() {
			return ALTAR_UID;
		}
	}
}
