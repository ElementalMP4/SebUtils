package main.java.elementalmp4.command.homes;

import main.java.elementalmp4.command.AbstractCommand;
import main.java.elementalmp4.annotation.SebUtilsCommand;
import main.java.elementalmp4.completer.HomesTabCompleter;
import main.java.elementalmp4.service.HomeService;
import org.bukkit.ChatColor;
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
            commandSender.sendMessage(ChatColor.GOLD + "Teleporting...");
            HomeService.teleportUserHome(player, homeName);
        } else {
            if (args.length == 0) commandSender.sendMessage(ChatColor.RED + "You haven't set any homes!");
            else commandSender.sendMessage(ChatColor.RED + "You don't have a home with that name!");
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
