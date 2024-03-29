package main.java.elementalmp4.command.homes;

import main.java.elementalmp4.service.HomeService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        String homeName = args.length == 0 ? "default" : args[0];
        if (HomeService.userHasHome(commandSender.getName(), homeName)) {
            Player player = (Player) commandSender;
            commandSender.sendMessage(ChatColor.GOLD + "Teleporting...");
            HomeService.teleportUserHome(player, homeName);
        } else {
            commandSender.sendMessage(ChatColor.RED + "You don't have a home with that name, or you haven't set a default home yet");
        }
        return true;
    }

}
