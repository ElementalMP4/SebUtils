package main.java.elementalmp4.command.tpa;

import main.java.elementalmp4.annotation.SebUtilsCommand;
import main.java.elementalmp4.command.AbstractCommand;
import main.java.elementalmp4.service.TeleportService;
import main.java.elementalmp4.utils.TeleportRequest;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Optional;

@SebUtilsCommand
public class TeleportCommand extends AbstractCommand {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        Optional<Player> playerOpt = TeleportService.validateTeleportRequest(commandSender, args);
        if (playerOpt.isEmpty()) return true;
        Player player = playerOpt.get();

        commandSender.sendMessage(ChatColor.YELLOW + "Teleport request sent to " + ChatColor.RED + player.getName());
        TextComponent message = new TextComponent();
        message.addExtra(ChatColor.DARK_BLUE + "=====================================================");
        message.addExtra(ChatColor.YELLOW + commandSender.getName() + ChatColor.WHITE + " has requested to teleport to your location\n\n ");
        message.addExtra(TeleportService.ACCEPT_COMPONENT);
        message.addExtra(ChatColor.WHITE + "          ");
        message.addExtra(TeleportService.DENY_COMPONENT);
        message.addExtra(ChatColor.DARK_BLUE + "\n=====================================================");

        player.spigot().sendMessage(message);
        TeleportService.createNewTeleportRequest(new TeleportRequest(((Player) commandSender), player, player.getName()));
        return true;
    }

    @Override
    public String getCommandName() {
        return "tpa";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return null;
    }
}
