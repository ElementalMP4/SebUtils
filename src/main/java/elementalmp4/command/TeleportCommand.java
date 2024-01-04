package main.java.elementalmp4.command;

import main.java.elementalmp4.SebUtils;
import main.java.elementalmp4.service.TeleportService;
import main.java.elementalmp4.utils.TeleportRequest;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (TeleportService.userIsAlreadyWaiting(commandSender.getName())) {
            commandSender.sendMessage(ChatColor.RED + "You cannot send a teleport request whilst you have pending incoming or outgoing teleport requests");
            return true;
        }

        if (args.length == 0) {
            commandSender.sendMessage(ChatColor.RED + "You must specify another player!");
            return true;
        }

        Player player = SebUtils.getPlugin().getServer().getPlayer(args[0]);
        if (player == null) {
            commandSender.sendMessage(ChatColor.RED + "Player could not be found!");
            return true;
        }

        if (player.getName().equals(commandSender.getName())) {
            commandSender.sendMessage(ChatColor.RED + "You cannot teleport to yourself!");
            return true;
        }

        commandSender.sendMessage(ChatColor.YELLOW + "Teleport request sent to " + ChatColor.RED + player.getName());
        player.sendMessage(
                ChatColor.YELLOW + commandSender.getName()
                        + ChatColor.RED + " would like to teleport to your location. Accept with "
                        + ChatColor.YELLOW + "/tpaccept "
                        + ChatColor.RED + "or deny with "
                        + ChatColor.YELLOW + "/tpdeny "
                        + ChatColor.RED + " - This request will expire in 60 seconds");
        TeleportService.createNewTeleportRequest(new TeleportRequest(((Player) commandSender), player, player.getName()));
        return true;
    }
}
