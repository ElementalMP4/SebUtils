package main.java.elementalmp4.sebutils.command.tpa;

import main.java.elementalmp4.sebutils.annotation.SebUtilsCommand;
import main.java.elementalmp4.sebutils.command.AbstractCommand;
import main.java.elementalmp4.sebutils.entity.TeleportRequest;
import main.java.elementalmp4.sebutils.service.TeleportService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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

        Component senderMessage = Component.text("Teleport request sent to ", NamedTextColor.YELLOW)
                .append(Component.text(player.getName(), NamedTextColor.RED));
        commandSender.sendMessage(senderMessage);

        Component message = Component.text("=====================================================", NamedTextColor.DARK_BLUE)
                .append(Component.text("\n", NamedTextColor.WHITE))
                .append(Component.text(commandSender.getName(), NamedTextColor.YELLOW))
                .append(Component.text(" has requested to teleport you to their location\n\n ", NamedTextColor.WHITE))
                .append(TeleportService.ACCEPT_COMPONENT)
                .append(Component.text("          ", NamedTextColor.WHITE))
                .append(TeleportService.DENY_COMPONENT)
                .append(Component.text("\n=====================================================", NamedTextColor.DARK_BLUE));

        player.sendMessage(message);
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
