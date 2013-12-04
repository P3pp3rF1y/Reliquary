package xreliquary.items;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

public class ItemDestructionCatalyst extends ItemXR {
	
	public static List<Integer> ids = new ArrayList<Integer>();

    protected ItemDestructionCatalyst(int par1) {
        super(par1);
        this.setMaxDamage(0);
        this.setMaxStackSize(1);
        canRepair = false;
        this.setCreativeTab(Reliquary.tabsXR);
        this.setUnlocalizedName(Names.DESTRUCTION_CATALYST_NAME);
    }

    @Override
    public void addInformation(ItemStack ist, EntityPlayer par2EntityPlayer,
            List par3List, boolean par4) {
        par3List.add("Consumes 3 Gunpowder on right click, no less.");
        par3List.add("Blows up a 3x3x3 Cube behind target cube.");
        par3List.add("Destroys mundane vanilla blocks only.");
    }

    @Override
    public boolean onItemUse(ItemStack ist, EntityPlayer player, World world,
            int x, int y, int z, int side, float xOff, float yOff, float zOff) {
        // if you can reduce gunpowder count by
        // Constants.DESTRUCTION_CATALYST_COST...
        if (hasGunpowder(player)) {
            doExplosion(world, x, y, z, side, player);
        }
        return true;
    }

    public void doExplosion(World world, int x, int y, int z, int side,
            EntityPlayer player) {
        boolean destroyedSomething = false;
        boolean playOnce = true;
        y = y + (side == 0 ? 1 : side == 1 ? -1 : 0);
        z = z + (side == 2 ? 1 : side == 3 ? -1 : 0);
        x = x + (side == 4 ? 1 : side == 5 ? -1 : 0);
        for (int xD = -1; xD <= 1; xD++) {
            for (int yD = -1; yD <= 1; yD++) {
                for (int zD = -1; zD <= 1; zD++) {
                    if (isBreakable(world.getBlockId(x + xD, y + yD, z + zD))) {
                        world.setBlock(x + xD, y + yD, z + zD, 0);
                        if (world.rand.nextInt(2) == 0) {
                            world.spawnParticle("largeexplode", x + xD, y + yD,
                                    z + zD, 1.0D, 0.0D, 0.0D);
                        }
                        destroyedSomething = true;
                        if (playOnce) {
                            world.playSoundEffect(
                                    x,
                                    y,
                                    z,
                                    "random.explode",
                                    4.0F,
                                    (1.0F + (world.rand.nextFloat() - world.rand
                                            .nextFloat()) * 0.2F) * 0.7F);
                            playOnce = false;
                        }
                    }
                }
            }
        }
        if (destroyedSomething) {
            consumeGunpowder(player);
        }
    }

    public boolean isBreakable(int id) {
        return id == Block.dirt.blockID || id == Block.grass.blockID
                || id == Block.gravel.blockID
                || id == Block.cobblestone.blockID || id == Block.stone.blockID
                || id == Block.sand.blockID || id == Block.sandStone.blockID
                || id == Block.snow.blockID || id == Block.slowSand.blockID
                || id == Block.netherrack.blockID || id == Block.whiteStone.blockID
                || ids.contains(id);
    }

    public boolean consumeGunpowder(EntityPlayer player) {
        int gunPowderCost = Reference.DESTRUCTION_CATALYST_COST;
        IInventory inventory = player.inventory;
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            if (inventory.getStackInSlot(slot) == null) {
                continue;
            }
            if (inventory.getStackInSlot(slot).getItem() == Item.gunpowder) {
                while (gunPowderCost > 0
                        && player.inventory.getStackInSlot(slot) != null) {
                    player.inventory.decrStackSize(slot, 1);
                    gunPowderCost--;
                }
                if (gunPowderCost == 0)
                    return true;
            }
        }
        return false;
    }

    public boolean hasGunpowder(EntityPlayer player) {
        int gunPowderCount = 0;
        IInventory inventory = player.inventory;
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            if (inventory.getStackInSlot(slot) == null) {
                continue;
            }
            if (inventory.getStackInSlot(slot).getItem() == Item.gunpowder) {
                gunPowderCount += inventory.getStackInSlot(slot).stackSize;
                if (gunPowderCount >= Reference.DESTRUCTION_CATALYST_COST)
                    return true;
            }
        }
        return false;
    }
}
