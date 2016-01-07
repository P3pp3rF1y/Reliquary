package xreliquary.blocks;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.blocks.BlockBase;
import lib.enderwizards.sandstone.init.ContentInit;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.blocks.tile.TileEntityMortar;
import xreliquary.lib.Names;

import java.util.List;
import java.util.Random;

@ContentInit
public class BlockApothecaryMortar extends BlockBase {

    public BlockApothecaryMortar() {
        super(Material.rock, Names.apothecary_mortar);
        this.setHardness(1.5F);
        this.setResistance(2.0F);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.3125F, 0.75F);
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List cbList, Entity collisionEntity) {
        this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.3125F, 0.75F);
        super.addCollisionBoxesToList(world, x, y, z, aabb, cbList, collisionEntity);
        this.setBlockBoundsForItemRender();
    }

    public void setBlockBoundsForItemRender() {
        this.setBlockBounds(0.25F, 0F, 0.25F, 0.75F, 0.3F, 0.75F);
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public int getRenderType() {
        return -1;
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metaMaybe, float playerX, float playerY, float playerZ) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity == null || !(tileEntity instanceof TileEntityMortar))
            return false;
        TileEntityMortar mortar = (TileEntityMortar) tileEntity;
        ItemStack heldItem = player.getCurrentEquippedItem();
        if (heldItem == null) {
            mortar.usePestle();
            world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, this.stepSound.getStepResourcePath(), (this.stepSound.getVolume() + 1.0F) / 2.0F, this.stepSound.getPitch() * 0.8F);
            player.swingItem();
            return false;
        }
        ItemStack[] mortarItems = mortar.getItemStacks();
        boolean putItemInSlot = false;
        for (int slot = 0; slot < mortarItems.length; slot++) {
            ItemStack item = new ItemStack(player.getCurrentEquippedItem().getItem(), 1, player.getCurrentEquippedItem().getItemDamage());
            item.setTagCompound(player.getCurrentEquippedItem().getTagCompound());
            if (mortarItems[slot] == null && mortar.isItemValidForSlot(slot, item)) {
                player.getCurrentEquippedItem().stackSize--;
                if (player.getCurrentEquippedItem().stackSize == 0)
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                mortar.setInventorySlotContents(slot, item);
                putItemInSlot = true;
                break;
            }
        }
        if (!putItemInSlot) {
            mortar.usePestle();
            return false;
        }
        return true;
    }

    @Override
    public Item getItemDropped(int someInt, Random unusedRandom, int fortuneEnchantLevelIThink) {
        // this might destroy the universe
        return ItemBlock.getItemFromBlock(Reliquary.CONTENT.getBlock(Names.apothecary_mortar));
    }

    @SideOnly(Side.CLIENT)
    public Item getItem(World world, int x, int y, int z) {
        return ItemBlock.getItemFromBlock(Reliquary.CONTENT.getBlock(Names.apothecary_mortar));
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int dunnoWhatThisIs) {
        return new TileEntityMortar();
    }

}
