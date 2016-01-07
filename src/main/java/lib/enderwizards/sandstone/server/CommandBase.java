package lib.enderwizards.sandstone.server;

import net.minecraft.command.ICommand;

public abstract class CommandBase extends net.minecraft.command.CommandBase {

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(Object o) {
        if (o instanceof ICommand) {
            return this.compareTo((ICommand) o);
        } else {
            return 0;
        }
    }

}