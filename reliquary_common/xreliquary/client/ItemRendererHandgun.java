package xreliquary.client;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import xreliquary.client.model.ModelHandgun;
import xreliquary.lib.Reference;
import cpw.mods.fml.client.FMLClientHandler;

public class ItemRendererHandgun implements IItemRenderer {

    protected ModelHandgun handgunModel;

    public ItemRendererHandgun() {
        handgunModel = new ModelHandgun();
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        switch (type) {
        case EQUIPPED:
            return true;
        default:
            return false;
        }
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
            ItemRendererHelper helper) {
        return false;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        switch (type) {
        case EQUIPPED: {
            GL11.glPushMatrix();

            FMLClientHandler.instance().getClient().renderEngine
                    .bindTexture(Reference.HANDGUN_TEXTURE);

            GL11.glRotatef(0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(176F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(145F, 0.0F, 0.0F, 1.0F);

            GL11.glTranslatef(0.8F, .20F, 0.08F);

            float scale = 0.5F;
            GL11.glScalef(scale, scale, scale);

            handgunModel.render((Entity) data[1], 0.0F, 0.0F, 0.0F, 0.0F, 0.0F,
                    0.0625F);

            GL11.glPopMatrix();
        }
        default:
            break;
        }

    }

}
