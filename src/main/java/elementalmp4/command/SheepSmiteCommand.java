package main.java.elementalmp4.command;

import main.java.elementalmp4.service.GlobalConfigService;
import main.java.elementalmp4.GlobalConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Set;

public class SheepSmiteCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        if (args.length == 0) {
            boolean tntEnabled = GlobalConfigService.getAsBoolean(GlobalConfig.SHEEP_SMITE);
            commandSender.sendMessage("Sheep will currently " + format(tntEnabled));
            return true;
        }

        if (!Set.of("true", "false").contains(args[0])) {
            commandSender.sendMessage(ChatColor.RED + "You must specify true or false");
            return true;
        }

        GlobalConfigService.set(GlobalConfig.SHEEP_SMITE, args[0]);
        commandSender.sendMessage("Sheep will now " + format(Boolean.parseBoolean(args[0])));
        return true;
    }

    private String format(boolean enabled) {
        return (enabled ? ChatColor.GREEN + "be smited" : ChatColor.RED + "not be smited");
    }
}
