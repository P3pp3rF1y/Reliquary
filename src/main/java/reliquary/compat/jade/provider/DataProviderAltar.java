package reliquary.compat.jade.provider;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import reliquary.blocks.AlkahestryAltarBlock;
import reliquary.blocks.tile.AlkahestryAltarBlockEntity;
import reliquary.reference.Reference;
import reliquary.reference.Settings;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

import java.text.SimpleDateFormat;
import java.util.List;

public class DataProviderAltar implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

	private static final ResourceLocation ALTAR_UID = new ResourceLocation(Reference.MOD_ID, "altar");

	@Override
	public @Nullable IElement getIcon(BlockAccessor accessor, IPluginConfig config, IElement currentIcon) {
		return IBlockComponentProvider.super.getIcon(accessor, config, currentIcon);
	}

	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig pluginConfig) {
		if (Boolean.TRUE.equals(Settings.CLIENT.wailaShiftForInfo.get()) && !accessor.getPlayer().isCrouching()) {
			tooltip.add(Component.translatable("waila.reliquary.shift_for_more").withStyle(ChatFormatting.ITALIC));
			return;
		}

		if (!(accessor.getBlock() instanceof AlkahestryAltarBlock && accessor.getBlockEntity() instanceof AlkahestryAltarBlockEntity altar)) {
			return;
		}

		IElementHelper helper = tooltip.getElementHelper();
		if (!altar.isActive()) {
			tooltip.add(Component.translatable("waila.reliquary.altar.inactive").withStyle(ChatFormatting.RED));

			Vec2 delta = new Vec2(0, -4);
			IElement redstoneIcon = helper.item(Items.REDSTONE.getDefaultInstance(), JadeHelper.ITEM_ICON_SCALE);
			IElement requirementText = helper.text(Component.literal(String.format("%d / %d", altar.getRedstoneCount(), Settings.COMMON.blocks.altar.redstoneCost.get())));
			redstoneIcon.size(redstoneIcon.getSize().add(delta)).translate(delta);
			requirementText.size(requirementText.getSize().add(delta)).translate(delta.add(new Vec2(0, (redstoneIcon.getSize().y - requirementText.getSize().y) / 2)));

			tooltip.add(List.of(
					redstoneIcon,
					requirementText
			));
			return;
		}

		tooltip.add(Component.translatable("waila.reliquary.altar.active").withStyle(ChatFormatting.GREEN));
		int cycleTime = accessor.getServerData().getInt("cycleTime"); // altar.getCycleTime(
		tooltip.add(Component.translatable("waila.reliquary.altar.time_remaining", new SimpleDateFormat("mm:ss").format(cycleTime * 50)));
	}

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
}
