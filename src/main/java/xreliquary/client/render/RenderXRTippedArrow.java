package xreliquary.client.render;

import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import xreliquary.entities.EntityXRTippedArrow;

import javax.annotation.Nonnull;

public class RenderXRTippedArrow extends RenderArrow<EntityXRTippedArrow> {
	private static final ResourceLocation RES_ARROW = new ResourceLocation("textures/entity/projectiles/arrow.png");
	private static final ResourceLocation RES_TIPPED_ARROW = new ResourceLocation("textures/entity/projectiles/tipped_arrow.png");

	public RenderXRTippedArrow(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(@Nonnull EntityXRTippedArrow entity) {
		return entity.getColor() > 0 ? RES_TIPPED_ARROW : RES_ARROW;
	}
}
