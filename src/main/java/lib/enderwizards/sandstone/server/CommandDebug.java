package lib.enderwizards.sandstone.server;

import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import lib.enderwizards.sandstone.init.Content;
import lib.enderwizards.sandstone.mod.ModRegistry;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

public class CommandDebug extends CommandBase {

    @Override
    public String getCommandName() {
        return "sdebug";
    }

    @Override
    public String getCommandUsage(ICommandSender player) {
        return "/sdebug [give|mods]";
    }

    @Override
    public void processCommand(ICommandSender player, String[] args) {
        if (player.getCommandSenderEntity().getName().equals("Rcon"))
            return;
        if (args.length <= 0) {
            player.addChatMessage(new ChatComponentText(this.getCommandUsage(player)));
            return;
        }
        if (args[0].equals("mods")) {
            String modList = EnumChatFormatting.UNDERLINE + "Mods (" + (Loader.instance().getActiveModList().size() - 3) + "):" + EnumChatFormatting.RESET;
            int count = 0;
            for (ModContainer mod : Loader.instance().getActiveModList()) {
                String modName = " " + (ModRegistry.hasMod(mod) ? EnumChatFormatting.YELLOW + mod.getName() + EnumChatFormatting.RESET : mod.getName()) + ",";
                if (count > 2)
                    modList += modName;
                count++;
            }

            player.addChatMessage(new ChatComponentText(modList.substring(0, modList.length() - 1)));
        }
        if (args[0].equals("give")) {
            if (args.length < 4) {
                Item item = Content.DEFAULT.getItem(args[1].contains(":") ? args[1] : "minecraft:" + args[1]);
                if (item != null) {
                    List<ItemStack> stacks = new ArrayList<ItemStack>();
                    item.getSubItems(item, item.getCreativeTab(), stacks);
                    ItemStack stack = stacks.get(0);

                    int amount = item.getItemStackLimit(stack);
                    if (args.length > 2) {
                        try {
                            amount = Integer.valueOf(args[2]);
                        } catch (NumberFormatException e) {
                            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + args[2] + " isn't a valid amount! Using the maximum stack size (" + amount + ") instead."));
                        }
                    }

                    if (item.getCreativeTab() == null) {
                        player.addChatMessage(new ChatComponentText("The item you were given isn't in a creative tab! That means it might not work as intended, or is intended to be used via a different item."));
                    }

                    BlockPos playerPosition = player.getPosition();
                    EntityItem itemEntity = new EntityItem(player.getEntityWorld(), playerPosition.getX(), playerPosition.getY(), playerPosition.getZ(), stack);
                    player.getEntityWorld().spawnEntityInWorld(itemEntity);
                }
            } else {
                player.addChatMessage(new ChatComponentText(this.getCommandUsage(player)));
            }
        }
    }


}
