package xreliquary.client.render;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import xreliquary.entities.XRTippedArrowEntity;

public class XRTippedArrowRenderer extends ArrowRenderer<XRTippedArrowEntity> {
	private static final ResourceLocation RES_ARROW = new ResourceLocation("textures/entity/projectiles/arrow.png");
	private static final ResourceLocation RES_TIPPED_ARROW = new ResourceLocation("textures/entity/projectiles/tipped_arrow.png");

	public XRTippedArrowRenderer(EntityRendererManager renderManager) {
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture( XRTippedArrowEntity entity) {
		return entity.getColor() > 0 ? RES_TIPPED_ARROW : RES_ARROW;
	}
}
