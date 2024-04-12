package main.java.elementalmp4.command.tpa;

import main.java.elementalmp4.command.AbstractCommand;
import main.java.elementalmp4.annotation.SebUtilsCommand;
import main.java.elementalmp4.service.TeleportService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

@SebUtilsCommand
public class TeleportDenyCommand extends AbstractCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!TeleportService.userIsAlreadyWaiting(commandSender.getName())) {
            commandSender.sendMessage(ChatColor.RED + "You do not have any pending requests to deny");
            return true;
        }
        TeleportService.denyTeleport((Player) commandSender);
        return true;
    }

    @Override
    public String getCommandName() {
        return "tpdeny";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return null;
    }
}
