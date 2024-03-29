package main.java.elementalmp4.command.admin;

import main.java.elementalmp4.GlobalConfig;
import main.java.elementalmp4.command.AbstractCommand;
import main.java.elementalmp4.command.SebUtilsCommand;
import main.java.elementalmp4.completer.BooleanTabCompleter;
import main.java.elementalmp4.service.GlobalConfigService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Set;

@SebUtilsCommand
public class EnableAfkCommand extends AbstractCommand {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        if (args.length == 0) {
            boolean tntEnabled = GlobalConfigService.getAsBoolean(GlobalConfig.AFK_ENABLED);
            commandSender.sendMessage("AFK is currently " + format(tntEnabled));
            return true;
        }

        if (!Set.of("true", "false").contains(args[0])) {
            commandSender.sendMessage(ChatColor.RED + "You must specify true or false");
            return true;
        }

        GlobalConfigService.set(GlobalConfig.AFK_ENABLED, args[0]);
        commandSender.sendMessage("AFK is now " + format(Boolean.parseBoolean(args[0])));
        return true;
    }

    private String format(boolean enabled) {
        return (enabled ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled");
    }

    @Override
    public String getCommandName() {
        return "enableafk";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return new BooleanTabCompleter();
    }
}
