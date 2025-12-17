package reliquary.client.render;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.TippableArrowRenderState;
import net.minecraft.resources.Identifier;
import reliquary.entity.TippedArrow;

public class TippedArrowRenderer extends ArrowRenderer<TippedArrow, TippableArrowRenderState> {
	private static final Identifier RES_ARROW = Identifier.parse("textures/entity/projectiles/arrow.png");
	private static final Identifier RES_TIPPED_ARROW = Identifier.parse("textures/entity/projectiles/tipped_arrow.png");

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
	protected Identifier getTextureLocation(TippableArrowRenderState renderState) {
		return renderState.isTipped ? RES_TIPPED_ARROW : RES_ARROW;
	}
}
