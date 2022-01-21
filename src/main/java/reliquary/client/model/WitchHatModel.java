package reliquary.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;

public class WitchHatModel extends HumanoidModel<LivingEntity> {
	public WitchHatModel(ModelPart part) {
		super(part);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshDefinition = new MeshDefinition();
		PartDefinition root = meshDefinition.getRoot();
		PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
		root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
		root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
		root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
		root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
		root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);
		root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);

		PartDefinition hatMain = head.addOrReplaceChild("main", CubeListBuilder.create()
						.texOffs(0, 64)
						.addBox(0, 0, 0, 10, 2, 10)
				, PartPose.offset(-5.0F, -8F, -5.0F)
		);
		hatMain.addOrReplaceChild("second_cube", CubeListBuilder.create()
						.texOffs(0, 76)
						.addBox(0, 0, 0, 7, 4, 7)
				, PartPose.offsetAndRotation(1.75F, -4.0F, 2.0F, -0.05235988F, 0, 0.02617994F)
		);

		return LayerDefinition.create(meshDefinition, 64, 128);
	}

	@Override
	public void renderToBuffer(PoseStack ms, VertexConsumer buffer, int light, int overlay, float r, float g, float b, float a) {
		setAllVisible(false);
		head.visible = true;
		super.renderToBuffer(ms, buffer, light, overlay, r, g, b, a);
	}
}
