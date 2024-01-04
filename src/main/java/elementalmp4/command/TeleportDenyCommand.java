package main.java.elementalmp4.command;

import main.java.elementalmp4.service.TeleportService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TeleportDenyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!TeleportService.userIsAlreadyWaiting(commandSender.getName())) {
            commandSender.sendMessage(ChatColor.RED + "You do not have any pending requests to deny");
            return true;
        }
        TeleportService.denyTeleport(commandSender.getName());
        return true;
    }
}
