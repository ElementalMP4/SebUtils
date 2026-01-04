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

import net.kyori.adventure.text.Component;

@SebUtilsCommand
public class AdminPlotOverrideCommand extends AbstractCommand {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length == 0) {
            boolean adminOverride = GlobalConfigService.getAsBoolean(GlobalConfig.ADMIN_PLOT_OVERRIDE);
            commandSender.sendMessage(
                    Component.text("Admin plot override is currently ")
                            .append(format(adminOverride))
            );
            return true;
        }

        if (!Set.of("true", "false").contains(args[0].toLowerCase())) {
            commandSender.sendMessage(
                    Component.text("You must specify true or false", NamedTextColor.RED)
            );
            return true;
        }

        boolean enabled = Boolean.parseBoolean(args[0]);
        GlobalConfigService.set(GlobalConfig.ADMIN_PLOT_OVERRIDE, args[0]);

        commandSender.sendMessage(
                Component.text("Admin plot override is now ")
                        .append(format(enabled))
        );
        return true;
    }

    private Component format(boolean enabled) {
        return Component.text(
                enabled ? "enabled" : "disabled",
                enabled ? NamedTextColor.GREEN : NamedTextColor.RED
        );
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


