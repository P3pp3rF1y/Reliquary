package xreliquary.client.render;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import xreliquary.client.util.Particle;

/*
    A small item renderer for inventory particles on items. Fancy. -TheMike
 */
public class ItemRendererParticle implements IItemRenderer {

    private static RenderItem renderItem = new RenderItem();
    private static Particle part;

    public ItemRendererParticle(Particle particle) {
        this.part = particle;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return type == ItemRenderType.INVENTORY;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return false;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if(type != ItemRenderType.INVENTORY)
            return;
        GL11.glPushMatrix();
        renderItem.renderIcon(0, 0, item.getIconIndex(), 16, 16);
        /*
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        part.draw(1, 1, 200);
        part.draw(14, 14, 200);
        part.draw(1, 14, 200);
        part.draw(14, 1, 200);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        */
        GL11.glPopMatrix();
    }
}