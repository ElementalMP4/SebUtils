package main.java.elementalmp4.sebutils.command.pvp;

import main.java.elementalmp4.sebutils.annotation.SebUtilsCommand;
import main.java.elementalmp4.sebutils.command.AbstractCommand;
import main.java.elementalmp4.sebutils.completer.BooleanTabCompleter;
import main.java.elementalmp4.sebutils.service.PVPToggleService;
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
            commandSender.sendMessage("You currently have PVP " + format(toggleEnabled));
            return true;
        }

        if (!Set.of("true", "false").contains(args[0])) {
            commandSender.sendMessage(NamedTextColor.RED + "You must specify true or false");
            return true;
        }

        PVPToggleService.updatePlayerToggle(commandSender.getName(), Boolean.parseBoolean(args[0]));
        commandSender.sendMessage("PVP Toggle is now " + format(Boolean.parseBoolean(args[0])));
        return true;
    }

    private String format(boolean toggledOff) {
        return (toggledOff ? NamedTextColor.RED + "disabled" : NamedTextColor.GREEN + "enabled");
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
