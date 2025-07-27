package reliquary.client.render;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import reliquary.entity.TippedArrow;

public class TippedArrowRenderer extends ArrowRenderer<TippedArrow> {
	private static final ResourceLocation RES_ARROW = ResourceLocation.parse("textures/entity/projectiles/arrow.png");
	private static final ResourceLocation RES_TIPPED_ARROW = ResourceLocation.parse("textures/entity/projectiles/tipped_arrow.png");

	public TippedArrowRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public ResourceLocation getTextureLocation(TippedArrow entity) {
		return entity.getColor() > 0 ? RES_TIPPED_ARROW : RES_ARROW;
	}
}
