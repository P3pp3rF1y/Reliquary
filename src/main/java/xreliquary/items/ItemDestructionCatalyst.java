package xreliquary.items;

import com.google.common.collect.ImmutableList;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemBase;
import lib.enderwizards.sandstone.util.ContentHelper;
import lib.enderwizards.sandstone.util.InventoryHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.lib.Names;

import java.util.List;

@ContentInit
public class ItemDestructionCatalyst extends ItemBase {

    public static List<String> ids = ImmutableList.of("minecraft:dirt", "minecraft:grass", "minecraft:gravel", "minecraft:cobblestone", "minecraft:stone", "minecraft:sand", "minecraft:sandstone", "minecraft:snow", "minecraft:soul_sand", "minecraft:netherrack", "minecraft:end_stone");

    public ItemDestructionCatalyst() {
        super(Names.destruction_catalyst);
        this.setMaxDamage(0);
        this.setMaxStackSize(1);
        canRepair = false;
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
    }

    @Override
    public boolean onItemUse(ItemStack ist, EntityPlayer player, World world, int x, int y, int z, int side, float xOff, float yOff, float zOff) {
        if (InventoryHelper.getItemQuantity(new ItemStack(Items.gunpowder), player.inventory) >= Reliquary.CONFIG.getInt(Names.destruction_catalyst, "cost") || player.capabilities.isCreativeMode) {
            doExplosion(world, x, y, z, side, player);
        }
        return true;
    }

    public void doExplosion(World world, int x, int y, int z, int side, EntityPlayer player) {
        boolean destroyedSomething = false;
        boolean playOnce = true;
        y = y + (side == 0 ? 1 : side == 1 ? -1 : 0);
        z = z + (side == 2 ? 1 : side == 3 ? -1 : 0);
        x = x + (side == 4 ? 1 : side == 5 ? -1 : 0);
        for (int xD = -1; xD <= 1; xD++) {
            for (int yD = -1; yD <= 1; yD++) {
                for (int zD = -1; zD <= 1; zD++) {
                    if (isBreakable(ContentHelper.getIdent(world.getBlock(x + xD, y + yD, z + zD)))) {
                        world.setBlock(x + xD, y + yD, z + zD, Blocks.air);
                        if (world.rand.nextInt(2) == 0) {
                            world.spawnParticle("largeexplode", x + xD, y + yD, z + zD, 1.0D, 0.0D, 0.0D);
                        }
                        destroyedSomething = true;
                        if (playOnce) {
                            world.playSoundEffect(x, y, z, "random.explode", 4.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
                            playOnce = false;
                        }
                    }
                }
            }
        }
        if (destroyedSomething && !(player.capabilities.isCreativeMode)) {
            InventoryHelper.consumeItem(new ItemStack(Items.gunpowder), player, 0, Reliquary.CONFIG.getInt(Names.destruction_catalyst, "cost"));
        }
    }

    public boolean isBreakable(String id) {
        return ((List<String>) Reliquary.CONFIG.get(Names.destruction_catalyst, "mundane_blocks")).indexOf(id) != -1;
    }
}
