package xreliquary.client.render;

import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import xreliquary.blocks.BlockApothecaryCauldron;

/**
 * Created by Xeno on 5/25/14.
 */
public class RenderApothecaryCauldron extends RenderBlocks {

    /**
     * Render block cauldron
     */
    public boolean renderBlockCauldron(BlockApothecaryCauldron cauldron, int x, int y, int z)
    {
        this.renderStandardBlock(cauldron, x, y, z);
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(cauldron.getMixedBrightnessForBlock(this.blockAccess, x, y, z));
        int l = cauldron.colorMultiplier(this.blockAccess, x, y, z);
        float f = (float)(l >> 16 & 255) / 255.0F;
        float f1 = (float)(l >> 8 & 255) / 255.0F;
        float f2 = (float)(l & 255) / 255.0F;
        float f4;

        if (EntityRenderer.anaglyphEnable)
        {
            float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
            f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            f = f3;
            f1 = f4;
            f2 = f5;
        }

        tessellator.setColorOpaque_F(f, f1, f2);
        IIcon sideTexture = cauldron.getBlockTextureFromSide(2);
        f4 = 0.125F;
        this.renderFaceXPos(cauldron, (double)((float)x - 1.0F + f4), (double)y, (double)z, sideTexture);
        this.renderFaceXNeg(cauldron, (double)((float)x + 1.0F - f4), (double)y, (double)z, sideTexture);
        this.renderFaceZPos(cauldron, (double)x, (double)y, (double)((float)z - 1.0F + f4), sideTexture);
        this.renderFaceZNeg(cauldron, (double)x, (double)y, (double)((float)z + 1.0F - f4), sideTexture);
        IIcon innerTexture = BlockApothecaryCauldron.getCauldronIcon("inner");
        this.renderFaceYPos(cauldron, (double)x, (double)((float)y - 1.0F + 0.25F), (double)z, innerTexture);
        this.renderFaceYNeg(cauldron, (double)x, (double)((float)y + 1.0F - 0.75F), (double)z, innerTexture);
        int i1 = this.blockAccess.getBlockMetadata(x, y, z);

        if (i1 > 0)
        {
            IIcon liquidTexture = BlockLiquid.getLiquidIcon("water_still");
            this.renderFaceYPos(cauldron, (double)x, (double)((float)y - 1.0F + BlockCauldron.getRenderLiquidLevel(i1)), (double)z, liquidTexture);
        }

        return true;
    }
}
