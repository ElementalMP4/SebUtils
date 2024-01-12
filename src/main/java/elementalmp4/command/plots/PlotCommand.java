package main.java.elementalmp4.command.plots;

import main.java.elementalmp4.service.PlotService;
import main.java.elementalmp4.utils.Plot;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class PlotCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        Location loc = ((Player) commandSender).getLocation();
        Optional<Plot> plot = PlotService.blockIsOwned(loc.getBlockX(), loc.getBlockZ(), loc.getWorld().getName());

        if (plot.isEmpty()) {
            commandSender.sendMessage(ChatColor.GOLD + "There is no plot at this location");
            return true;
        }

        Plot p = plot.get();
        if (p.getOwner().equals(commandSender.getName())) {
            commandSender.sendMessage(ChatColor.GREEN + "You own a plot here with ID " + ChatColor.YELLOW + p.getId());
        } else {
            commandSender.sendMessage(ChatColor.RED + "This plot belongs to " + ChatColor.GOLD + p.getOwner());
        }
        return true;
    }
}
