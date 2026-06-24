package reliquary.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import reliquary.block.tile.PassivePedestalBlockEntity;

public class PassivePedestalRenderer implements BlockEntityRenderer<PassivePedestalBlockEntity, PassivePedestalRenderer.PedestalRenderState> {
	private final ItemModelResolver itemModelResolver;

	public PassivePedestalRenderer(BlockEntityRendererProvider.Context context) {
		this.itemModelResolver = context.itemModelResolver();
	}

	@Override
	public PedestalRenderState createRenderState() {
		return new PedestalRenderState();
	}

	@Override
	public void extractRenderState(PassivePedestalBlockEntity blockEntity, PedestalRenderState renderState, float partialTick, Vec3 cameraPos,
			ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPos, crumblingOverlay);

		ItemStack item = blockEntity.getItem();
		this.itemModelResolver.updateForTopItem(renderState.item, item, ItemDisplayContext.GROUND, blockEntity.getLevel(), null, 0);
	}

	@Override
	public void submit(PedestalRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
		if (renderState.item.isEmpty()) {
			return;
		}

		poseStack.pushPose();
		float yDiff = Mth.sin((System.currentTimeMillis() % 86400000) / 1000F) * 0.1F + 0.1F;
		poseStack.translate(0.5D, 0.9D + yDiff, 0.5D);
		float f3 = ((System.currentTimeMillis() % 86400000) / 2000F) * (180F / (float) Math.PI);
		poseStack.mulPose(Axis.YP.rotationDegrees(f3));
		poseStack.scale(0.75F, 0.75F, 0.75F);
		renderState.item.submit(poseStack, submitNodeCollector, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
		poseStack.popPose();
	}

	public static class PedestalRenderState extends BlockEntityRenderState {
		public ItemStackRenderState item = new ItemStackRenderState();
	}
}
