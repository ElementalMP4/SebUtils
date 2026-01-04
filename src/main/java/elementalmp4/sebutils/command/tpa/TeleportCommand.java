package main.java.elementalmp4.sebutils.command.tpa;

import main.java.elementalmp4.sebutils.annotation.SebUtilsCommand;
import main.java.elementalmp4.sebutils.command.AbstractCommand;
import main.java.elementalmp4.sebutils.entity.TeleportRequest;
import main.java.elementalmp4.sebutils.service.TeleportService;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.chat.TextComponent;
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

        commandSender.sendMessage(NamedTextColor.YELLOW + "Teleport request sent to " + NamedTextColor.RED + player.getName());
        TextComponent message = new TextComponent();
        message.addExtra(NamedTextColor.DARK_BLUE + "=====================================================");
        message.addExtra(NamedTextColor.YELLOW + commandSender.getName() + NamedTextColor.WHITE + " has requested to teleport to your location\n\n ");
        message.addExtra(TeleportService.ACCEPT_COMPONENT);
        message.addExtra(NamedTextColor.WHITE + "          ");
        message.addExtra(TeleportService.DENY_COMPONENT);
        message.addExtra(NamedTextColor.DARK_BLUE + "\n=====================================================");

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
