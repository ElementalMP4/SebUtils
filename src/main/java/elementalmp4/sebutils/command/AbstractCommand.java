package main.java.elementalmp4.sebutils.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

public abstract class AbstractCommand implements CommandExecutor {

    public abstract String getCommandName();
    public abstract TabCompleter getTabCompleter();

}
