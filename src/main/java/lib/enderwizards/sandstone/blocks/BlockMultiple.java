package lib.enderwizards.sandstone.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.items.block.ItemBlockMultiple;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class BlockMultiple extends BlockBase {

    public final SubBlock[] blocks;

    public BlockMultiple(Material material, String name, Object[] items) {
        this(material, name, buildSubBlocks(items));
    }

    public BlockMultiple(Material material, String name, String[] names) {
        this(material, name, buildSubBlocks(names));
    }

    public BlockMultiple(Material material, String name, SubBlock[] blocks) {
        super(material, name);
        this.blocks = blocks;
        for (SubBlock block : blocks) {
            block.parent = this;
        }
    }

    private static SubBlock[] buildSubBlocks(String[] names) {
        List<SubBlock> items = new ArrayList<SubBlock>();
        for (String name : names) {
            items.add(new SubBlock(name));
        }
        return items.toArray(new SubBlock[items.size()]);
    }

    private static SubBlock[] buildSubBlocks(Object[] items) {
        List<SubBlock> newItems = new ArrayList<SubBlock>();
        for (Object item : items) {
            if (item instanceof String)
                newItems.add(new SubBlock((String) item));
            if (item instanceof SubBlock)
                newItems.add((SubBlock) item);
        }
        return newItems.toArray(new SubBlock[newItems.size()]);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        world.setBlockMetadataWithNotify(x, y, z, stack.getItemDamage(), 3);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (meta < blocks.length) {
            return blocks[meta].getIcon(side);
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        for (SubBlock block : blocks) {
            block.registerIcons(iconRegister);
        }
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    public int getLength() {
        return blocks.length;
    }

    @Override
    public Class<? extends ItemBlock> getCustomItemBlock() {
        return ItemBlockMultiple.class;
    }

}
