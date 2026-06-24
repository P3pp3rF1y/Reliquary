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
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import reliquary.api.client.IPedestalItemRenderer;
import reliquary.block.tile.PedestalBlockEntity;
import reliquary.client.registry.PedestalClientRegistry;

import java.util.Optional;

public class PedestalRenderer implements BlockEntityRenderer<PedestalBlockEntity, PedestalRenderer.PedestalRenderState> {
	private final ItemModelResolver itemModelResolver;

	public PedestalRenderer(BlockEntityRendererProvider.Context context) {
		this.itemModelResolver = context.itemModelResolver();
	}

	@Override
	public AABB getRenderBoundingBox(PedestalBlockEntity blockEntity) {
		BlockPos pos = blockEntity.getBlockPos();
		AABB aabb = new AABB(pos.getX() - 1, pos.getY(), pos.getZ() - 1, pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
		blockEntity.executeOnActionItem(ai -> ai.getRenderBoundingBoxOuterPosition().ifPresent(aabb::expandTowards));
		return aabb;
	}

	@Override
	public PedestalRenderState createRenderState() {
		return new PedestalRenderState();
	}

	@Override
	public void extractRenderState(PedestalBlockEntity blockEntity, PedestalRenderState renderState, float partialTick, Vec3 cameraPos,
			@Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPos, crumblingOverlay);

		renderState.pedestalPos = blockEntity.getBlockPos();
		ItemStack item = blockEntity.getItem();
		this.itemModelResolver.updateForTopItem(renderState.item, item, ItemDisplayContext.GROUND, blockEntity.getLevel(), null, 0);
		renderState.itemData = blockEntity.getItemData();
		renderState.pedestalItemRenderer = PedestalClientRegistry.getItemRenderer(item);
		renderState.partialTick = partialTick;
	}

	@Override
	public void submit(PedestalRenderState pedestalRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
			CameraRenderState cameraRenderState) {
		if (pedestalRenderState.item.isEmpty()) {
			return;
		}
		poseStack.pushPose();
		float yDiff = Mth.sin((System.currentTimeMillis() % 86400000) / 1000F) * 0.1F + 0.1F;
		poseStack.translate(0.5D, 0.9D + yDiff, 0.5D);
		float f3 = ((System.currentTimeMillis() % 86400000) / 2000F) * (180F / (float) Math.PI);
		poseStack.mulPose(Axis.YP.rotationDegrees(f3));
		poseStack.scale(0.75F, 0.75F, 0.75F);
		pedestalRenderState.item.submit(poseStack, submitNodeCollector, pedestalRenderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
		poseStack.popPose();
		pedestalRenderState.pedestalItemRenderer.ifPresent(
				extraRenderer -> pedestalRenderState.itemData.ifPresent(itemData -> extraRenderer.submitRender(submitNodeCollector, pedestalRenderState,
						itemData, pedestalRenderState.partialTick, poseStack, pedestalRenderState.lightCoords, OverlayTexture.NO_OVERLAY)));
	}

	public static class PedestalRenderState extends BlockEntityRenderState {
		public ItemStackRenderState item = new ItemStackRenderState();
		public BlockPos pedestalPos = BlockPos.ZERO;
		public Optional<Object> itemData = Optional.empty();
		public Optional<IPedestalItemRenderer> pedestalItemRenderer = Optional.empty();
		public float partialTick = 0f;
	}
}
