package main.java.elementalmp4.sebutils.command.admin;

import main.java.elementalmp4.sebutils.annotation.SebUtilsCommand;
import main.java.elementalmp4.sebutils.command.AbstractCommand;
import main.java.elementalmp4.sebutils.completer.BooleanTabCompleter;
import main.java.elementalmp4.sebutils.config.GlobalConfig;
import main.java.elementalmp4.sebutils.service.GlobalConfigService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
            Component message = Component.text("AFK is currently ").append(format(tntEnabled));
            commandSender.sendMessage(message);
            return true;
        }

        if (!Set.of("true", "false").contains(args[0])) {
            commandSender.sendMessage(Component.text("You must specify true or false", NamedTextColor.RED));
            return true;
        }

        GlobalConfigService.set(GlobalConfig.AFK_ENABLED, args[0]);
        Component message = Component.text("AFK is now ").append(format(Boolean.parseBoolean(args[0])));
        commandSender.sendMessage(message);
        return true;
    }

    private Component format(boolean enabled) {
        return Component.text(enabled ? "enabled" : "disabled", enabled ? NamedTextColor.GREEN : NamedTextColor.RED);
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
