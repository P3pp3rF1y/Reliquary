package xreliquary.client.render;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;
import xreliquary.blocks.BlockApothecaryCauldron;

public class RenderApothecaryCauldron implements ISimpleBlockRenderingHandler {
    public static int renderID = RenderingRegistry.getNextAvailableRenderId();

    /**
     * Render block cauldron
     */
    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        if (modelId == renderID) {
            renderer.renderStandardBlock(block, x, y, z);
            Tessellator tessellator = Tessellator.instance;
            tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
            int l = block.colorMultiplier(world, x, y, z);
            float f = (float)(l >> 16 & 255) / 255.0F;
            float f1 = (float)(l >> 8 & 255) / 255.0F;
            float f2 = (float)(l & 255) / 255.0F;
            float f4;

            tessellator.setColorOpaque_F(f, f1, f2);
            f4 = 0.125F;
            IIcon insideTexture = BlockApothecaryCauldron.getCauldronIcon("inside");
            renderer.renderFaceXPos(block, (double) ((float) x - 1.0F + f4), (double) y, (double) z, insideTexture);
            renderer.renderFaceZPos(block, (double) x, (double) y, (double) ((float) z - 1.0F + f4), insideTexture);
            renderer.renderFaceZNeg(block, (double) x, (double) y, (double) ((float) z + 1.0F - f4), insideTexture);
            renderer.renderFaceXNeg(block, (double) ((float) x + 1.0F - f4), (double) y, (double) z, insideTexture);
            IIcon innerTexture = BlockApothecaryCauldron.getCauldronIcon("inner");
            renderer.renderFaceYPos(block, (double) x, (double) ((float) y - 1.0F + 0.25F), (double) z, innerTexture);
            renderer.renderFaceYNeg(block, (double) x, (double) ((float) y + 1.0F - 0.75F), (double) z, innerTexture);
            int i1 = world.getBlockMetadata(x, y, z);

            if (i1 > 0)
            {
                //obviously wrong, but this will be what determines the liquid texture within the cauldron.
                IIcon liquidTexture = BlockLiquid.getLiquidIcon("water_still");
                renderer.renderFaceYPos(block, (double) x, (double) ((float) y - 1.0F + BlockCauldron.getRenderLiquidLevel(i1)), (double) z, liquidTexture);
            }
        }
        return true;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
        this.renderStandardBlock(block, metadata, renderer);
        float f4 = 0.125F;

        Tessellator tessellator = Tessellator.instance;
        IIcon insideTexture = BlockApothecaryCauldron.getCauldronIcon("inside");

        renderer.renderFromInside = true;
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F + f4);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, insideTexture);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F - f4);
        renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, insideTexture);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F + f4, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, insideTexture);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F - f4, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, insideTexture);
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        renderer.renderFromInside = false;
    }


    private void renderStandardBlock (Block block, int meta, RenderBlocks renderer) {
        Tessellator tessellator = Tessellator.instance;
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(1, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, meta));
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return renderID;
    }
}
