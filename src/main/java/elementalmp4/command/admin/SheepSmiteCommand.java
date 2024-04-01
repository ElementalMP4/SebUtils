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
public class SheepSmiteCommand extends AbstractCommand {
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

    @Override
    public String getCommandName() {
        return "sheepsmite";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return new BooleanTabCompleter();
    }
}
