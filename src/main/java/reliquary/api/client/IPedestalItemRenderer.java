package reliquary.api.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import reliquary.block.tile.PedestalBlockEntity;

public interface IPedestalItemRenderer {
	void doRender(PedestalBlockEntity te, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay);
}
