package xreliquary.command;

import net.minecraft.command.*;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CommandGenLootChest extends CommandBase {

    private static final List<String> chestTypes = Arrays.asList(ChestGenHooks.BONUS_CHEST, ChestGenHooks.DUNGEON_CHEST, ChestGenHooks.MINESHAFT_CORRIDOR, ChestGenHooks.NETHER_FORTRESS, ChestGenHooks.PYRAMID_DESERT_CHEST, ChestGenHooks.PYRAMID_JUNGLE_CHEST, ChestGenHooks.PYRAMID_JUNGLE_DISPENSER, ChestGenHooks.STRONGHOLD_CORRIDOR, ChestGenHooks.STRONGHOLD_CROSSING, ChestGenHooks.STRONGHOLD_LIBRARY, ChestGenHooks.VILLAGE_BLACKSMITH);

    @Override
    public String getCommandName() {
        return "xreliquary";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "xreliquary loot <chestgenhooks type>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length <2) {
            throw new WrongUsageException("more params needed");
        }
        if (!args[0].equals("loot") || !chestTypes.contains(args[1])) {
            throw new WrongUsageException("invalid parameters");
        }

        String chestType = args[1];

        BlockPos chestPosition = sender.getPosition().offset(sender.getCommandSenderEntity().getHorizontalFacing());

        World world = sender.getEntityWorld();

        if (world.isAirBlock(chestPosition)) {
            world.setBlockState(chestPosition, Blocks.chest.getDefaultState(), 2);
            TileEntity tileEntity = world.getTileEntity(chestPosition);

            if (tileEntity instanceof TileEntityChest) {
                WeightedRandomChestContent.generateChestContents(world.rand,ChestGenHooks.getItems(chestType, world.rand),((TileEntityChest) tileEntity), ChestGenHooks.getCount(chestType, world.rand));
            }
        }

    }
}
