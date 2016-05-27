package xreliquary.client.render;

import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

//TODO: get rid of this renderer, doesn't do anything extra on top of what RenderSnowball does already

@SideOnly(Side.CLIENT)
public class RenderThrown<T extends Entity> extends RenderSnowball<T> {
	public RenderThrown(RenderManager renderManager, Item item, RenderItem itemRenderer) {
		super(renderManager, item, itemRenderer);
	}

	@Override
	public ItemStack getStackToRender(T entityIn) {
		ItemStack entity = new ItemStack(this.item, 1);

		return entity;
	}

}
