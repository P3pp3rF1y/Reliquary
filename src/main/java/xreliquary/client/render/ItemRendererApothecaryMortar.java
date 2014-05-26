package xreliquary.client.render;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import xreliquary.client.model.ModelHandgun;
import xreliquary.client.model.ModelMortar;
import xreliquary.init.ContentHandler;
import xreliquary.lib.ClientReference;
import xreliquary.lib.Names;

public class ItemRendererApothecaryMortar implements IItemRenderer {

	protected ModelMortar mortarModel;

	public ItemRendererApothecaryMortar() {
        mortarModel = new ModelMortar();
	}


    /**
     * IItemRenderer implementation *
     */
    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        switch (type) {
            case ENTITY:
                return true;
            case EQUIPPED:
                return true;
            case EQUIPPED_FIRST_PERSON:
                return true;
            case INVENTORY:
                return true;
            default:
                return false;
        }
    }

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glPushMatrix();

        GL11.glTranslatef(0.5F,3F,0.5F);
        GL11.glScalef(2.0F, 2.0F, 2.0F);

        ResourceLocation textureName = ClientReference.MORTAR_TEXTURE;

        FMLClientHandler.instance().getClient().renderEngine.bindTexture(textureName);
        GL11.glPushMatrix();
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        this.mortarModel.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_LIGHTING);
	}

}
