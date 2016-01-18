package xreliquary.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import xreliquary.entities.potion.EntityThrownPotion;
import xreliquary.entities.potion.EntityThrownXRPotion;
import xreliquary.init.ModItems;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import xreliquary.util.potions.PotionEssence;

public class RenderThrownXRPotion extends RenderSnowball<EntityThrownXRPotion> {
    public RenderThrownXRPotion(RenderManager renderManager, RenderItem itemRenderer)
    {
        super( renderManager, ModItems.potionEssence, itemRenderer );
    }

    public ItemStack func_177082_d(EntityThrownXRPotion entityIn)
    {
        ItemStack potion = new ItemStack(this.field_177084_a, 1);
        potion.readFromNBT(entityIn.getNBTTagCompound());

        return potion;
    }

}
