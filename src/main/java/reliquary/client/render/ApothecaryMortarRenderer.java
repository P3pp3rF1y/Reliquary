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
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.Nullable;
import reliquary.block.ApothecaryMortarBlock;
import reliquary.block.tile.ApothecaryMortarBlockEntity;

import java.util.ArrayList;
import java.util.List;

public class ApothecaryMortarRenderer implements BlockEntityRenderer<ApothecaryMortarBlockEntity, ApothecaryMortarRenderer.ApothecaryMortarRenderState> {
	private final ItemModelResolver itemModelResolver;

	public ApothecaryMortarRenderer(BlockEntityRendererProvider.Context context) {
		this.itemModelResolver = context.itemModelResolver();
	}

	private void submitMortarItem(SubmitNodeCollector submitNodeCollector, PoseStack poseStack, int packedLight, Runnable processRotationTransforms, ItemStackRenderState itemToRender, float translateX, float translateZ) {
		poseStack.pushPose();
		poseStack.translate(translateX, 0F, translateZ);
		processRotationTransforms.run();
		poseStack.scale(0.60F, 0.60F, 0.60F);
		itemToRender.submit(poseStack, submitNodeCollector, packedLight, OverlayTexture.NO_OVERLAY, 0);
		poseStack.popPose();
	}

	@Override
	public ApothecaryMortarRenderState createRenderState() {
		return new ApothecaryMortarRenderState();
	}

	@Override
	public void extractRenderState(ApothecaryMortarBlockEntity blockEntity, ApothecaryMortarRenderState renderState, float partialTick, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPos, crumblingOverlay);

		renderState.direction = blockEntity.getBlockState().getValue(ApothecaryMortarBlock.FACING);
		List<ItemStackRenderState> itemStackRenderStates = new ArrayList<>();
		ResourceHandler<ItemResource> items = blockEntity.getItems();
		for (int slot = 0; slot < items.size(); slot++) {
			ItemResource resource = items.getResource(slot);
			if (resource.isEmpty()) {
				continue;
			}
			ItemStackRenderState itemStackRenderState = new ItemStackRenderState();
			this.itemModelResolver.updateForTopItem(itemStackRenderState, resource.toStack(items.getAmountAsInt(slot)), ItemDisplayContext.GROUND, blockEntity.getLevel(), null, 0);
			itemStackRenderStates.add(itemStackRenderState);
		}
		renderState.items = itemStackRenderStates;
	}

	@Override
	public void submit(ApothecaryMortarRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
		Direction direction = renderState.direction;
		float horizontalRotation = direction == Direction.UP ? 0F : direction.get2DDataValue() * 90F;

		poseStack.pushPose();
		poseStack.translate(0.5D, 0.3D, 0.5D);
		poseStack.mulPose(Axis.YN.rotationDegrees(horizontalRotation));

		if (!renderState.items.isEmpty()) {
			submitMortarItem(submitNodeCollector, poseStack, renderState.lightCoords, () -> {
				poseStack.mulPose(Axis.ZP.rotationDegrees(40F));
				poseStack.mulPose(Axis.YP.rotationDegrees(90F));
			}, renderState.items.getFirst(), -0.09F, 0F);
		}

		if (renderState.items.size() > 1) {
			submitMortarItem(submitNodeCollector, poseStack, renderState.lightCoords, () -> {
				poseStack.mulPose(Axis.XP.rotationDegrees(40F));
				poseStack.mulPose(Axis.YP.rotationDegrees(180F));
			}, renderState.items.get(1), 0F, 0.09F);
		}

		if (renderState.items.size() > 2) {
			submitMortarItem(submitNodeCollector, poseStack, renderState.lightCoords, () -> {
				poseStack.mulPose(Axis.ZN.rotationDegrees(40F));
				poseStack.mulPose(Axis.YP.rotationDegrees(270F));
			}, renderState.items.get(2), 0.09F, 0F);
		}

		poseStack.popPose();
	}

	public static class ApothecaryMortarRenderState extends BlockEntityRenderState {
		public Direction direction = Direction.NORTH;
		public List<ItemStackRenderState> items = new ArrayList<>();
	}
}
