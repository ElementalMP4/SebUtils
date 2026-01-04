package main.java.elementalmp4.sebutils.command.admin;

import main.java.elementalmp4.sebutils.annotation.SebUtilsCommand;
import main.java.elementalmp4.sebutils.command.AbstractCommand;
import main.java.elementalmp4.sebutils.completer.BooleanTabCompleter;
import main.java.elementalmp4.sebutils.config.GlobalConfig;
import main.java.elementalmp4.sebutils.service.GlobalConfigService;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Set;

@SebUtilsCommand
public class EnableGravesCommand extends AbstractCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length == 0) {
            boolean gravesEnabled = GlobalConfigService.getAsBoolean(GlobalConfig.GRAVES_ENABLED);
            commandSender.sendMessage("Graves are currently " + format(gravesEnabled));
            return true;
        }

        if (!Set.of("true", "false").contains(args[0])) {
            commandSender.sendMessage(NamedTextColor.RED + "You must specify true or false");
            return true;
        }

        GlobalConfigService.set(GlobalConfig.GRAVES_ENABLED, args[0]);
        commandSender.sendMessage("Graves are now " + format(Boolean.parseBoolean(args[0])));
        return true;
    }

    private String format(boolean enabled) {
        return (enabled ? NamedTextColor.GREEN + "enabled" : NamedTextColor.RED + "disabled");
    }

    @Override
    public String getCommandName() {
        return "enablegraves";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return new BooleanTabCompleter();
    }
}
