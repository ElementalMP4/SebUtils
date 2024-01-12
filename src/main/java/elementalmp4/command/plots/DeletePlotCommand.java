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

public class DeletePlotCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Location loc = ((Player) commandSender).getLocation();
        Optional<Plot> deletedPlot = PlotService.deletePlotIfInside(commandSender.getName(), loc.getWorld().getName(), loc.getBlockX(), loc.getBlockZ());

        if (deletedPlot.isPresent()) {
            commandSender.sendMessage(ChatColor.GREEN + "Deleted plot " + ChatColor.YELLOW + deletedPlot.get().getId());
        } else {
            commandSender.sendMessage(ChatColor.RED + "You are not standing inside one of your plots!");
        }

        return true;
    }
}
