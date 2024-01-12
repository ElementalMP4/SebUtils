package main.java.elementalmp4.command.tpa;

import main.java.elementalmp4.service.TeleportService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TeleportAcceptCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!TeleportService.userIsAlreadyWaiting(commandSender.getName())) {
            commandSender.sendMessage(ChatColor.RED + "You do not have any pending requests to accept");
            return true;
        }
        TeleportService.authoriseTeleport(commandSender.getName());
        return true;
    }
}
