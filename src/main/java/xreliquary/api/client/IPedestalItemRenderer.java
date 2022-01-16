package xreliquary.api.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import xreliquary.blocks.tile.PedestalBlockEntity;

public interface IPedestalItemRenderer {
	void doRender(PedestalBlockEntity te, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight, int packedOverlay);
}
