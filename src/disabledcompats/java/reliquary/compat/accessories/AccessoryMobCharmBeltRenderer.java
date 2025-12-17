package reliquary.compat.accessories;

import com.mojang.blaze3d.vertex.PoseStack;
import io.wispforest.accessories.api.client.AccessoryRenderState;
import io.wispforest.accessories.api.client.renderers.AccessoryRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MaterialMapper;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.AtlasManager;
import net.minecraft.client.resources.model.Material;
import reliquary.Reliquary;

public class AccessoryMobCharmBeltRenderer implements AccessoryRenderer {
	public static final ModelLayerLocation MOB_CHARM_BELT_LAYER = new ModelLayerLocation(Reliquary.getIdentifier("mob_charm_belt"), "main");
	public static final MaterialMapper MAPPER = new MaterialMapper(TextureAtlas.LOCATION_BLOCKS, "entity");
	public static final Material ON_BODY_TEXTURE = MAPPER.apply(Reliquary.getIdentifier("equipment/humanoid/mob_charm_belt"));
	private final HumanoidModel<HumanoidRenderState> beltModel;

	public AccessoryMobCharmBeltRenderer() {
		EntityModelSet entityModels = Minecraft.getInstance().getEntityModels();
		beltModel = new HumanoidModel<>(entityModels.bakeLayer(MOB_CHARM_BELT_LAYER));
	}

	@Override
	public <S extends LivingEntityRenderState> void render(AccessoryRenderState accessoryState, S entityState, EntityModel<S> model, PoseStack poseStack, SubmitNodeCollector collector) {
		if (entityState instanceof HumanoidRenderState humanoidRenderState) {
			beltModel.setupAnim(humanoidRenderState);
		}
		beltModel.setAllVisible(false);
		beltModel.body.visible = true;
		AtlasManager atlasManager = Minecraft.getInstance().getAtlasManager();
		collector.submitModelPart(beltModel.body, poseStack, ON_BODY_TEXTURE.renderType(RenderTypes::entityCutoutNoCull), entityState.lightCoords, OverlayTexture.NO_OVERLAY, atlasManager.get(ON_BODY_TEXTURE));
	}
}
