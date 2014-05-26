package xreliquary.client.render;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import xreliquary.blocks.tile.TileEntityMortar;
import xreliquary.client.model.ModelMortar;
import xreliquary.lib.ClientReference;

public class RenderApothecaryMortar extends TileEntitySpecialRenderer {
    private ModelMortar modelMortar = new ModelMortar();

    float modifier = 0.0625f;

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float var8) {
        if (tile instanceof TileEntityMortar) {
            GL11.glPushMatrix();
            GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
            ResourceLocation textureName = ClientReference.MORTAR_TEXTURE;

            FMLClientHandler.instance().getClient().renderEngine.bindTexture(textureName);
            GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
            this.modelMortar.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, modifier);
            // TODO: Paving the way for the model rendering.
            this.modelMortar.pestle.render(modifier);
            this.modelMortar.pestleKnob.render(modifier);
            GL11.glPopMatrix();
        }
    }
}
