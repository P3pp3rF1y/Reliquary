package xreliquary.client.render;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class RenderThrown<T extends Entity> extends RenderSnowball<T> {
    public RenderThrown(RenderManager renderManager, Item item, RenderItem itemRenderer)
    {
        super( renderManager, item, itemRenderer );
    }

    public ItemStack func_177082_d(T entityIn)
    {
        ItemStack entity = new ItemStack(this.field_177084_a, 1);

        return entity;
    }

}
