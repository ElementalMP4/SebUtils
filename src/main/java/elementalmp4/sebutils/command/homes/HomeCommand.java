package main.java.elementalmp4.sebutils.command.homes;

import main.java.elementalmp4.sebutils.annotation.SebUtilsCommand;
import main.java.elementalmp4.sebutils.command.AbstractCommand;
import main.java.elementalmp4.sebutils.completer.HomesTabCompleter;
import main.java.elementalmp4.sebutils.service.HomeService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

@SebUtilsCommand
public class HomeCommand extends AbstractCommand {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        String homeName = args.length == 0 ? "default" : args[0];
        if (HomeService.userHasHome(commandSender.getName(), homeName)) {
            Player player = (Player) commandSender;
            commandSender.sendMessage(Component.text("Teleporting...", NamedTextColor.GOLD));
            HomeService.teleportUserHome(player, homeName);
        } else {
            if (args.length == 0) commandSender.sendMessage(Component.text("You haven't set any homes!", NamedTextColor.RED));
            else commandSender.sendMessage(Component.text("You don't have a home with that name!", NamedTextColor.RED));
        }
        return true;
    }

    @Override
    public String getCommandName() {
        return "home";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return new HomesTabCompleter();
    }
}
