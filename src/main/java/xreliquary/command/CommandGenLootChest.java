package xreliquary.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

import java.util.Arrays;
import java.util.List;

public class CommandGenLootChest extends CommandBase {

	private static final List<String> chestTypes = Arrays.asList(LootTableList.CHESTS_SPAWN_BONUS_CHEST.toString(), LootTableList.CHESTS_SIMPLE_DUNGEON.toString(), LootTableList.CHESTS_ABANDONED_MINESHAFT.toString(), LootTableList.CHESTS_NETHER_BRIDGE.toString(), LootTableList.CHESTS_DESERT_PYRAMID.toString(), LootTableList.CHESTS_JUNGLE_TEMPLE.toString(), LootTableList.CHESTS_STRONGHOLD_CORRIDOR.toString(), LootTableList.CHESTS_STRONGHOLD_CROSSING.toString(), LootTableList.CHESTS_STRONGHOLD_LIBRARY.toString(), LootTableList.CHESTS_VILLAGE_BLACKSMITH.toString());

	@Override
	public String getCommandName() {
		return "xreliquary";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "xreliquary loot <chestgenhooks type>";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(args.length < 2) {
			throw new WrongUsageException("more params needed");
		}
		if(!args[0].equals("loot") || !chestTypes.contains(args[1])) {
			throw new WrongUsageException("invalid parameters");
		}

		String chestType = args[1];

		BlockPos chestPosition = sender.getPosition().offset(sender.getCommandSenderEntity().getHorizontalFacing());

		World world = sender.getEntityWorld();

		if(world.isAirBlock(chestPosition)) {
			world.setBlockState(chestPosition, Blocks.CHEST.getDefaultState(), 2);
			TileEntity tileEntity = world.getTileEntity(chestPosition);

			if(tileEntity instanceof TileEntityChest) {
				((TileEntityChest) tileEntity).setLootTable(new ResourceLocation(chestType), world.rand.nextLong());
			}
		}

	}
}
