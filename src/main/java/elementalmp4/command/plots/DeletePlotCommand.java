package main.java.elementalmp4.command.plots;

import main.java.elementalmp4.annotation.SebUtilsCommand;
import main.java.elementalmp4.command.AbstractCommand;
import main.java.elementalmp4.completer.PlotCompleter;
import main.java.elementalmp4.entity.Plot;
import main.java.elementalmp4.service.PlotService;
import main.java.elementalmp4.utils.Converter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Optional;

@SebUtilsCommand
public class DeletePlotCommand extends AbstractCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage(ChatColor.RED + "You need to specify a plot ID!");
            return true;
        }

        Optional<Integer> id = Converter.tryStringToInt(args[0]);
        if (id.isEmpty()) {
            commandSender.sendMessage(ChatColor.RED + "Plot ID must be a valid number!");
            return true;
        }

        Optional<Plot> plot = PlotService.getPlotByIdAndOwner(id.get(), commandSender.getName());
        if (plot.isEmpty()) {
            commandSender.sendMessage(ChatColor.RED + "Plot not found - Try the " + ChatColor.YELLOW + "/plots" + ChatColor.RED
                    + " command to see a list of your plots, and use " + ChatColor.YELLOW + "/plot" + ChatColor.RED
                    + " to see details about the plot you are currently in");
            return true;
        }

        PlotService.deletePlot(plot.get().getId());
        commandSender.sendMessage(ChatColor.GREEN + "Deleted plot " + ChatColor.YELLOW + plot.get().getId()
                + ChatColor.GREEN + " - Freed up " + ChatColor.YELLOW + Plot.getPlotArea(plot.get())
                + ChatColor.GREEN + " blocks");

        return true;
    }

    @Override
    public String getCommandName() {
        return "deleteplot";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return new PlotCompleter();
    }
}
