package main.java.elementalmp4.sebutils.command.tpa;

import main.java.elementalmp4.sebutils.annotation.SebUtilsCommand;
import main.java.elementalmp4.sebutils.command.AbstractCommand;
import main.java.elementalmp4.sebutils.service.TeleportService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

@SebUtilsCommand
public class TeleportAcceptCommand extends AbstractCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!TeleportService.userIsAlreadyWaiting(commandSender.getName())) {
            commandSender.sendMessage(Component.text("You do not have any pending requests to accept", NamedTextColor.RED));
            return true;
        }
        TeleportService.authoriseTeleport(commandSender.getName());
        return true;
    }

    @Override
    public String getCommandName() {
        return "tpaccept";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return null;
    }
}
