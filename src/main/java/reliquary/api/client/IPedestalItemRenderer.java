package reliquary.api.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import reliquary.client.render.PedestalRenderer;

public interface IPedestalItemRenderer {
	void submitRender(SubmitNodeCollector submitNodeCollector, PedestalRenderer.PedestalRenderState renderState, Object itemData, float partialTicks, PoseStack poseStack, int packedLight, int packedOverlay);
}
