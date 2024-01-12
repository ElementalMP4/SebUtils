package main.java.elementalmp4.command.admin;

import main.java.elementalmp4.SebUtils;
import main.java.elementalmp4.service.HomeService;
import main.java.elementalmp4.utils.Home;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ListHomesCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage(ChatColor.RED + "You must specify a player!");
            return true;
        }

        Player player = SebUtils.getPlugin().getServer().getPlayer(args[0]);
        if (player == null) {
            commandSender.sendMessage(ChatColor.RED + "Player could not be found!");
            return true;
        }

        List<Home> homes = HomeService.getHomes(player.getName());
        if (homes.isEmpty()) {
            commandSender.sendMessage(ChatColor.RED + "User hasn't set any homes");
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
