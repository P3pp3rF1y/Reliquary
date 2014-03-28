package xreliquary.client.util;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.nbt.NBTTagCompound;

public class Particle {

    private static RenderItem renderItem = new RenderItem();

    public int r = 0;
    public int g = 0;
    public int b = 0;
    public int maxA = 255;

    public Particle(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public Particle(int r, int g, int b, int maxA) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.maxA = maxA;
    }

    public Particle(NBTTagCompound tag) {
        this.r = tag.getInteger("r");
        this.g = tag.getInteger("g");
        this.b = tag.getInteger("b");
        this.maxA  = tag.getInteger("maxA");
    }

    public void draw(int x, int y, int a) {
        Tessellator te = Tessellator.instance;
        te.startDrawingQuads();
        te.setColorRGBA(r, g, b, a);

        te.addVertex((double) x, (double) y, renderItem.zLevel + 50.0F);
        te.addVertex((double) x, (double) (y + 1), renderItem.zLevel + 50.0F);
        te.addVertex((double) (x + 1), (double) (y + 1), renderItem.zLevel + 50.0F);
        te.addVertex((double) (x + 1), (double) y, renderItem.zLevel + 50.0F);

        te.draw();
    }

    public void writeToNBT(NBTTagCompound tag) {
        tag.setInteger("r", r);
        tag.setInteger("g", g);
        tag.setInteger("b", b);
        tag.setInteger("maxA", maxA);
    }

    public void setColor(Tessellator te, int stage) {
        te.setColorRGBA(r, g, b, (int) Math.floor(maxA / stage));
    }

}