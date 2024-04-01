package main.java.elementalmp4.command.tpa;

import main.java.elementalmp4.command.AbstractCommand;
import main.java.elementalmp4.annotation.SebUtilsCommand;
import main.java.elementalmp4.service.TeleportService;
import main.java.elementalmp4.utils.TeleportRequest;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Optional;

@SebUtilsCommand
public class TeleportHereCommand extends AbstractCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        Optional<Player> playerOpt = TeleportService.validateTeleportRequest(commandSender, args);
        if (playerOpt.isEmpty()) return true;
        Player player = playerOpt.get();

        commandSender.sendMessage(ChatColor.YELLOW + "Teleport request sent to " + ChatColor.RED + player.getName());
        player.sendMessage(
                ChatColor.GOLD + commandSender.getName()
                        + ChatColor.RED + " would like to teleport to you to their location. Accept with "
                        + ChatColor.GOLD + "/tpaccept "
                        + ChatColor.RED + "or deny with "
                        + ChatColor.GOLD + "/tpdeny "
                        + ChatColor.RED + " - This request will expire in 60 seconds");
        TeleportService.createNewTeleportRequest(new TeleportRequest(player, ((Player) commandSender), player.getName()));
        return true;
    }

    @Override
    public String getCommandName() {
        return "tpahere";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return null;
    }
}
