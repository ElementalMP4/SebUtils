package main.java.elementalmp4.sebutils.command.pvp;

import main.java.elementalmp4.sebutils.annotation.SebUtilsCommand;
import main.java.elementalmp4.sebutils.command.AbstractCommand;
import main.java.elementalmp4.sebutils.completer.BooleanTabCompleter;
import main.java.elementalmp4.sebutils.service.PVPToggleService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Set;

@SebUtilsCommand
public class PVPToggleCommand extends AbstractCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        if (args.length == 0) {
            boolean toggleEnabled = PVPToggleService.playerHasDisabledPvp(commandSender.getName());
            commandSender.sendMessage(Component.text("You currently have PVP ", NamedTextColor.WHITE)
                    .append(format(toggleEnabled)));
            return true;
        }

        if (!Set.of("true", "false").contains(args[0])) {
            commandSender.sendMessage(Component.text("You must specify true or false", NamedTextColor.RED));
            return true;
        }

        PVPToggleService.updatePlayerToggle(commandSender.getName(), Boolean.parseBoolean(args[0]));
        commandSender.sendMessage(Component.text("PVP Toggle is now ", NamedTextColor.WHITE)
                .append(format(Boolean.parseBoolean(args[0]))));
        return true;
    }

    private Component format(boolean toggledOff) {
        return Component.text(toggledOff ? "disabled" : "enabled", toggledOff ? NamedTextColor.RED : NamedTextColor.GREEN);
    }

    @Override
    public String getCommandName() {
        return "pvptoggle";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return new BooleanTabCompleter();
    }
}
