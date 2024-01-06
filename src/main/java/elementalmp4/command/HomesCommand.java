package main.java.elementalmp4.command;

import main.java.elementalmp4.service.HomeService;
import main.java.elementalmp4.utils.Home;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class HomesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        List<Home> homes = HomeService.getHomes(commandSender.getName());
        if (homes.isEmpty()) {
            commandSender.sendMessage(ChatColor.RED + "You haven't set any homes!");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Home home : homes) {
                sb.append(home.toString()).append("\n");
            }
            commandSender.sendMessage(sb.toString());
        }
        return true;
    }
    
}
