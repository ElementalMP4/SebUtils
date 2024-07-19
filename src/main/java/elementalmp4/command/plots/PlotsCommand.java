package main.java.elementalmp4.command.plots;

import main.java.elementalmp4.GlobalConfig;
import main.java.elementalmp4.annotation.SebUtilsCommand;
import main.java.elementalmp4.command.AbstractCommand;
import main.java.elementalmp4.entity.Plot;
import main.java.elementalmp4.service.GlobalConfigService;
import main.java.elementalmp4.service.PlotService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

@SebUtilsCommand
public class PlotsCommand extends AbstractCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        List<Plot> plots = PlotService.getUserPlots(commandSender.getName());
        if (plots.isEmpty()) {
            commandSender.sendMessage(ChatColor.RED + "You don't have any plots!");
        } else {
            List<String> out = new ArrayList<>();
            for (Plot plot : plots) {
                out.add(ChatColor.GREEN.toString() + plot.getId()
                        + " " + ChatColor.YELLOW + plot.getXA() + " " + plot.getYA() + ChatColor.AQUA
                        + " > " + ChatColor.YELLOW + plot.getXB() + " " + plot.getYB() + ChatColor.AQUA
                        + " - " + ChatColor.GOLD +  plot.getWorld());
            }
            int usedArea = plots.stream().map(Plot::getPlotArea).reduce(0, Integer::sum);
            int remainingArea = GlobalConfigService.getAsInteger(GlobalConfig.PLOT_MAX_SIZE) - usedArea;
            out.add(ChatColor.GREEN + "Used: " + ChatColor.YELLOW + usedArea + " blocks" + ChatColor.GREEN + " Remaining: " + ChatColor.YELLOW + remainingArea);
            commandSender.sendMessage(String.join("\n", out));
        }
        return true;
    }

    @Override
    public String getCommandName() {
        return "plots";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return null;
    }
}
