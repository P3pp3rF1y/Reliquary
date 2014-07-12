package xreliquary.client.render;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import xreliquary.client.model.ModelMortar;
import xreliquary.lib.ClientReference;

public class ItemRendererApothecaryMortar implements IItemRenderer {

	protected ModelMortar mortarModel;
	float modifier = 0.0625f;

	public ItemRendererApothecaryMortar() {
		mortarModel = new ModelMortar();
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		if (type != ItemRenderType.FIRST_PERSON_MAP)
			return true;
		return false;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		GL11.glPushMatrix();
		GL11.glTranslatef(0.5F, 3F, 0.5F);
		GL11.glScalef(2.0F, 2.0F, 2.0F);

		ResourceLocation textureName = ClientReference.MORTAR_TEXTURE;
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(textureName);

		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		this.mortarModel.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, modifier);
		this.mortarModel.pestle.render(modifier);
		this.mortarModel.pestleKnob.render(modifier);
		GL11.glPopMatrix();
	}

}
