package xreliquary.items;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemToggleable;
import lib.enderwizards.sandstone.util.ContentHelper;
import lib.enderwizards.sandstone.util.InventoryHelper;
import lib.enderwizards.sandstone.util.LanguageHelper;
import lib.enderwizards.sandstone.util.NBTHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;
import xreliquary.Reliquary;
import xreliquary.reference.Names;

import java.util.List;

@ContentInit
public class ItemDestructionCatalyst extends ItemToggleable {

    public static List<String> ids = ImmutableList.of("minecraft:dirt", "minecraft:grass", "minecraft:gravel", "minecraft:cobblestone", "minecraft:stone", "minecraft:sand", "minecraft:sandstone", "minecraft:snow", "minecraft:soul_sand", "minecraft:netherrack", "minecraft:end_stone");

    public ItemDestructionCatalyst() {
        super(Names.destruction_catalyst);
        this.setMaxStackSize(1);
        canRepair = false;
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
    }

    @Override
    public void addInformation(ItemStack ist, EntityPlayer player, List list, boolean par4) {
        if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
            return;
        this.formatTooltip(ImmutableMap.of("charge", Integer.toString(NBTHelper.getInteger("gunpowder", ist))), ist, list);
        if(this.isEnabled(ist))
            LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", EnumChatFormatting.GRAY + Items.gunpowder.getItemStackDisplayName(new ItemStack(Items.gunpowder))), ist, list);
        LanguageHelper.formatTooltip("tooltip.absorb", null, ist, list);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (NBTHelper.getInteger("gunpowder", stack) > gunpowderCost() || player.capabilities.isCreativeMode) {
            doExplosion(world, pos, side, player, stack);
        }
        return true;
    }


    @Override
    public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean b) {
        if (world.isRemote)
            return;
        EntityPlayer player = null;
        if (e instanceof EntityPlayer) {
            player = (EntityPlayer) e;
        }
        if (player == null)
            return;

        if (this.isEnabled(ist)) {
            if (NBTHelper.getInteger("gunpowder", ist) + gunpowderWorth() < gunpowderLimit()) {
                if (InventoryHelper.consumeItem(new ItemStack(Items.gunpowder), player)) {
                    NBTHelper.setInteger("gunpowder", ist, NBTHelper.getInteger("gunpowder", ist) + gunpowderWorth());
                }
            }
        }
    }

    public int getExplosionRadius() {
        return Reliquary.CONFIG.getInt(Names.destruction_catalyst, "explosion_radius");
    }

    public boolean centeredExplosion() {
        return Reliquary.CONFIG.getBool(Names.destruction_catalyst, "centered_explosion");
    }

    public boolean perfectCube() {
        return Reliquary.CONFIG.getBool(Names.destruction_catalyst, "perfect_cube");
    }

    public void doExplosion(World world, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack ist) {
        boolean destroyedSomething = false;
        boolean playOnce = true;
        //TODO: make sure that the explosion happens on the correct side
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        if (!centeredExplosion()) {
            y = pos.getY() + (side == EnumFacing.DOWN ? getExplosionRadius() : side == EnumFacing.UP ? -getExplosionRadius() : 0);
            z = pos.getZ() + (side == EnumFacing.NORTH ? getExplosionRadius() : side == EnumFacing.SOUTH ? -getExplosionRadius() : 0);
            x = pos.getX() + (side == EnumFacing.WEST ? getExplosionRadius() : side == EnumFacing.EAST ? -getExplosionRadius() : 0);
        }
        for (int xD = -getExplosionRadius(); xD <= getExplosionRadius(); xD++) {
            for (int yD = -getExplosionRadius(); yD <= getExplosionRadius(); yD++) {
                for (int zD = -getExplosionRadius(); zD <= getExplosionRadius(); zD++) {
                    if (!perfectCube()) {
                        BlockPos origin = new BlockPos(x, y, z);
                        BlockPos target = new BlockPos(x + xD, y + yD, z + zD);
                        double distance = origin.distanceSq( target );
                        if (distance >= getExplosionRadius())
                            continue;
                    }

                    if (isBreakable(ContentHelper.getIdent(world.getBlockState( new BlockPos(x + xD, y + yD, z + zD )).getBlock()))) {
                        world.setBlockState( new BlockPos(x + xD, y + yD, z + zD), Blocks.air.getDefaultState() );
                        if (world.rand.nextInt(2) == 0) {
                            world.spawnParticle( EnumParticleTypes.EXPLOSION_LARGE, x + xD + (world.rand.nextFloat() - 0.5F), y + yD + (world.rand.nextFloat() - 0.5F), z + zD + (world.rand.nextFloat() - 0.5F), 0.0D, 0.0D, 0.0D);
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
        if (destroyedSomething) {
            NBTHelper.setInteger("gunpowder", ist, NBTHelper.getInteger("gunpowder", ist) - gunpowderCost());
        }
    }

    public boolean isBreakable(String id) {
        return ((List<String>) Reliquary.CONFIG.get(Names.destruction_catalyst, "mundane_blocks")).indexOf(id) != -1;
    }

    private int gunpowderCost() {return Reliquary.CONFIG.getInt(Names.destruction_catalyst, "gunpowder_cost"); }
    private int gunpowderWorth() {return Reliquary.CONFIG.getInt(Names.destruction_catalyst, "gunpowder_worth"); }
    private int gunpowderLimit() { return Reliquary.CONFIG.getInt(Names.destruction_catalyst, "gunpowder_limit"); }
}
