package main.java.elementalmp4.sebutils.command.plots;

import main.java.elementalmp4.sebutils.annotation.SebUtilsCommand;
import main.java.elementalmp4.sebutils.command.AbstractCommand;
import main.java.elementalmp4.sebutils.config.GlobalConfig;
import main.java.elementalmp4.sebutils.entity.Plot;
import main.java.elementalmp4.sebutils.service.GlobalConfigService;
import main.java.elementalmp4.sebutils.service.PlotService;
import net.kyori.adventure.text.format.NamedTextColor;
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
            commandSender.sendMessage(NamedTextColor.RED + "You don't have any plots!");
        } else {
            List<String> out = new ArrayList<>();
            for (Plot plot : plots) {
                out.add(NamedTextColor.GREEN.toString() + plot.getId()
                        + " " + NamedTextColor.YELLOW + plot.getXA() + " " + plot.getYA() + NamedTextColor.AQUA
                        + " > " + NamedTextColor.YELLOW + plot.getXB() + " " + plot.getYB() + NamedTextColor.AQUA
                        + " - " + NamedTextColor.GOLD +  plot.getWorld());
            }
            int usedArea = plots.stream().map(Plot::getPlotArea).reduce(0, Integer::sum);
            int remainingArea = GlobalConfigService.getAsInteger(GlobalConfig.PLOT_MAX_SIZE) - usedArea;
            out.add(NamedTextColor.GREEN + "Used: " + NamedTextColor.YELLOW + usedArea + " blocks" + NamedTextColor.GREEN + " Remaining: " + NamedTextColor.YELLOW + remainingArea);
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
