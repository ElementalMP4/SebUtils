package main.java.elementalmp4.command.admin;

import main.java.elementalmp4.service.HomeService;
import main.java.elementalmp4.utils.Home;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ListHomesCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage(ChatColor.RED + "You must specify a player!");
            return true;
        }

        List<Home> homes = HomeService.getHomes(args[0]);
        if (homes.isEmpty()) {
            commandSender.sendMessage(ChatColor.RED + "No homes found for player " + ChatColor.YELLOW + args[0]);
        } else {
            List<String> homeList = new ArrayList<>();
            for (Home home : homes) {
                homeList.add(home.toString());
            }
            commandSender.sendMessage(String.join("\n", homeList));
        }
        return true;
    }
}
