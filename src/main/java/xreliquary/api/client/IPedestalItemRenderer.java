package xreliquary.api.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import xreliquary.blocks.tile.PedestalTileEntity;

public interface IPedestalItemRenderer {
	void doRender(PedestalTileEntity te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, int packedOverlay);
}
