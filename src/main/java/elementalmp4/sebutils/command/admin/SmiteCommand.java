package main.java.elementalmp4.sebutils.command.admin;

import main.java.elementalmp4.sebutils.SebUtils;
import main.java.elementalmp4.sebutils.annotation.SebUtilsCommand;
import main.java.elementalmp4.sebutils.command.AbstractCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

@SebUtilsCommand
public class SmiteCommand extends AbstractCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage(Component.text("You must specify a player!", NamedTextColor.RED));
            return true;
        }

        Player player = SebUtils.getPlugin().getServer().getPlayer(args[0]);
        if (player == null) {
            commandSender.sendMessage(Component.text("Player could not be found!", NamedTextColor.RED));
            return true;
        }
        player.getWorld().strikeLightning(player.getLocation());
        player.setHealth(0);
        Component message = Component.text(player.getName(), NamedTextColor.YELLOW)
                .append(Component.text(" has been smited", NamedTextColor.RED));
        commandSender.sendMessage(message);
        return true;
    }

    @Override
    public String getCommandName() {
        return "smite";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return null;
    }
}
