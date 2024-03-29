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
public class EnableBillyCommand extends AbstractCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length == 0) {
            boolean billyEnabled = GlobalConfigService.getAsBoolean(GlobalConfig.BILLY_ENABLED);
            commandSender.sendMessage("Billy's Shop is currently " + format(billyEnabled));
            return true;
        }

        if (!Set.of("true", "false").contains(args[0])) {
            commandSender.sendMessage(ChatColor.RED + "You must specify true or false");
            return true;
        }

        GlobalConfigService.set(GlobalConfig.BILLY_ENABLED, args[0]);
        commandSender.sendMessage("Billy's Shop is now " + format(Boolean.parseBoolean(args[0])));
        return true;
    }

    private String format(boolean enabled) {
        return (enabled ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled");
    }

    @Override
    public String getCommandName() {
        return "enablebilly";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return new BooleanTabCompleter();
    }
}
