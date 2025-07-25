package reliquary.client.render;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.TippableArrowRenderState;
import net.minecraft.resources.ResourceLocation;
import reliquary.entity.TippedArrow;

public class TippedArrowRenderer extends ArrowRenderer<TippedArrow, TippableArrowRenderState> {
	private static final ResourceLocation RES_ARROW = ResourceLocation.parse("textures/entity/projectiles/arrow.png");
	private static final ResourceLocation RES_TIPPED_ARROW = ResourceLocation.parse("textures/entity/projectiles/tipped_arrow.png");

	public TippedArrowRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public TippableArrowRenderState createRenderState() {
		return new TippableArrowRenderState();
	}

	@Override
	public void extractRenderState(TippedArrow arrow, TippableArrowRenderState renderState, float partialTick) {
		super.extractRenderState(arrow, renderState, partialTick);
		renderState.isTipped = arrow.getColor() > 0;
	}

	@Override
	protected ResourceLocation getTextureLocation(TippableArrowRenderState renderState) {
		return renderState.isTipped ? RES_TIPPED_ARROW : RES_ARROW;
	}
}
