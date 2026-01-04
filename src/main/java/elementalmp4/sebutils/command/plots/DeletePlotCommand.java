package main.java.elementalmp4.sebutils.command.plots;

import main.java.elementalmp4.sebutils.annotation.SebUtilsCommand;
import main.java.elementalmp4.sebutils.command.AbstractCommand;
import main.java.elementalmp4.sebutils.completer.PlotCompleter;
import main.java.elementalmp4.sebutils.entity.Plot;
import main.java.elementalmp4.sebutils.service.PlotService;
import main.java.elementalmp4.sebutils.utils.Converter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Optional;

@SebUtilsCommand
public class DeletePlotCommand extends AbstractCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage(Component.text("You need to specify a plot ID!", NamedTextColor.RED));
            return true;
        }

        Optional<Integer> id = Converter.tryStringToInt(args[0]);
        if (id.isEmpty()) {
            commandSender.sendMessage(Component.text("Plot ID must be a valid number!", NamedTextColor.RED));
            return true;
        }

        Optional<Plot> plot = PlotService.getPlotByIdAndOwner(id.get(), commandSender.getName());
        if (plot.isEmpty()) {
            Component message = Component.text("Plot not found - Try the ", NamedTextColor.RED)
                    .append(Component.text("/plots", NamedTextColor.YELLOW))
                    .append(Component.text(" command to see a list of your plots, and use ", NamedTextColor.RED))
                    .append(Component.text("/plot", NamedTextColor.YELLOW))
                    .append(Component.text(" to see details about the plot you are currently in", NamedTextColor.RED));
            commandSender.sendMessage(message);
            return true;
        }

        PlotService.deletePlot(plot.get().getId());
        Component message = Component.text("Deleted plot ", NamedTextColor.GREEN)
                .append(Component.text(String.valueOf(plot.get().getId()), NamedTextColor.YELLOW))
                .append(Component.text(" - Freed up ", NamedTextColor.GREEN))
                .append(Component.text(String.valueOf(Plot.getPlotArea(plot.get())), NamedTextColor.YELLOW))
                .append(Component.text(" blocks", NamedTextColor.GREEN));
        commandSender.sendMessage(message);

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
