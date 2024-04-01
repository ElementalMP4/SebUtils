package main.java.elementalmp4.command.admin;

import main.java.elementalmp4.GlobalConfig;
import main.java.elementalmp4.command.AbstractCommand;
import main.java.elementalmp4.annotation.SebUtilsCommand;
import main.java.elementalmp4.completer.BooleanTabCompleter;
import main.java.elementalmp4.service.GlobalConfigService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Set;

@SebUtilsCommand
public class AdminPlotOverrideCommand extends AbstractCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length == 0) {
            boolean adminOverride = GlobalConfigService.getAsBoolean(GlobalConfig.ADMIN_PLOT_OVERRIDE);
            commandSender.sendMessage("Admin plot override is currently " + format(adminOverride));
            return true;
        }

        if (!Set.of("true", "false").contains(args[0])) {
            commandSender.sendMessage(ChatColor.RED + "You must specify true or false");
            return true;
        }

        GlobalConfigService.set(GlobalConfig.ADMIN_PLOT_OVERRIDE, args[0]);
        commandSender.sendMessage("Admin plot override is now " + format(Boolean.parseBoolean(args[0])));
        return true;
    }

    private String format(boolean enabled) {
        return (enabled ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled");
    }

    @Override
    public String getCommandName() {
        return "adminplotoverride";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return new BooleanTabCompleter();
    }
}

