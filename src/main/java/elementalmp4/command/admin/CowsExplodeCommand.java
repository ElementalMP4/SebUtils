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
public class CowsExplodeCommand extends AbstractCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        if (args.length == 0) {
            boolean tntEnabled = GlobalConfigService.getAsBoolean(GlobalConfig.COWS_EXPLODE);
            commandSender.sendMessage("Cows will currently " + format(tntEnabled));
            return true;
        }

        if (!Set.of("true", "false").contains(args[0])) {
            commandSender.sendMessage(ChatColor.RED + "You must specify true or false");
            return true;
        }

        GlobalConfigService.set(GlobalConfig.COWS_EXPLODE, args[0]);
        commandSender.sendMessage("Cows will now " + format(Boolean.parseBoolean(args[0])));
        return true;
    }

    private String format(boolean enabled) {
        return (enabled ? ChatColor.GREEN + "explode" : ChatColor.RED + "not explode");
    }

    @Override
    public String getCommandName() {
        return "cowsexplode";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return new BooleanTabCompleter();
    }
}
