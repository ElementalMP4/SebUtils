package main.java.elementalmp4.command;

import main.java.elementalmp4.service.HomeService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHomeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        Player player = ((Player)commandSender);
        int x = player.getLocation().getBlockX();
        int y = player.getLocation().getBlockY();
        int z = player.getLocation().getBlockZ();
        HomeService.setOrUpdateHome(commandSender.getName(), player.getWorld().getName(), x, y, z);
        commandSender.sendMessage("Home has been set to " + ChatColor.YELLOW + x + " " + y + " " + z);
        return true;
    }
}
