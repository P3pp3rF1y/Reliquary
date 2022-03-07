package reliquary.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
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
		MeshDefinition meshDefinition = createMesh(CubeDeformation.NONE, 3);
		PartDefinition root = meshDefinition.getRoot();
		PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(),  PartPose.ZERO);
		root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
		root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
		root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
		root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
		root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);
		root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);

		PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -2.0187F, -3.0F, 16.0F, 2.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -5.0313F, -5.0F));
		PartDefinition hat2 = hat.addOrReplaceChild("hat2", CubeListBuilder.create().texOffs(0, 18).addBox(-6.0F, -5.5F, -7.0F, 10.0F, 4.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(0, 32).addBox(-6.0F, -4.5F, -7.0F, 10.0F, 1.0F, 10.0F, new CubeDeformation(0.1F))
				.texOffs(48, 0).addBox(-2.0F, -5.0F, -7.45F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.25F, 0.7813F, 6.5F, -0.096F, 0.0F, 0.0262F));
		PartDefinition hat3 = hat2.addOrReplaceChild("hat3", CubeListBuilder.create().texOffs(40, 23).addBox(-4.25F, -5.0F, -5.5F, 6.0F, 3.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, -0.1047F, 0.0F, 0.0524F));
		PartDefinition hat4 = hat3.addOrReplaceChild("hat4", CubeListBuilder.create().texOffs(0, 9).addBox(-3.5F, -5.0F, -4.0F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, -0.2094F, 0.0F, 0.1047F));
		PartDefinition hat5 = hat4.addOrReplaceChild("hat5", CubeListBuilder.create().texOffs(0, 4).addBox(-3.0F, -3.25F, -3.5F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, -3.5F, -0.25F, -0.303F, 0.0393F, 0.1249F));
		hat5.addOrReplaceChild("hat6", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -3.25F, -1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, -3.0F, -2.0F, -0.3054F, 0.0F, 0.0F));

		return LayerDefinition.create(meshDefinition, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack ms, VertexConsumer buffer, int light, int overlay, float r, float g, float b, float a) {
		setAllVisible(false);
		head.visible = true;
		super.renderToBuffer(ms, buffer, light, overlay, r, g, b, a);
	}
}
