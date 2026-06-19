package main.java.elementalmp4.sebutils.command.plots;

import main.java.elementalmp4.sebutils.annotation.SebUtilsCommand;
import main.java.elementalmp4.sebutils.command.AbstractCommand;
import main.java.elementalmp4.sebutils.config.GlobalConfig;
import main.java.elementalmp4.sebutils.entity.Plot;
import main.java.elementalmp4.sebutils.service.GlobalConfigService;
import main.java.elementalmp4.sebutils.service.PlotService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

@SebUtilsCommand
public class PlotsCommand extends AbstractCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        List<Plot> plots = PlotService.getUserPlots(commandSender.getName());
        if (plots.isEmpty()) {
            commandSender.sendMessage(Component.text("You don't have any plots!", NamedTextColor.RED));
        } else {
            Component message = Component.empty();
            for (int i = 0; i < plots.size(); i++) {
                Plot plot = plots.get(i);
                Component plotLine = Component.text(plot.getId(), NamedTextColor.GREEN)
                        .append(Component.text(" ", NamedTextColor.WHITE))
                        .append(Component.text(plot.getXA() + " " + plot.getYA(), NamedTextColor.YELLOW))
                        .append(Component.text(" > ", NamedTextColor.AQUA))
                        .append(Component.text(plot.getXB() + " " + plot.getYB(), NamedTextColor.YELLOW))
                        .append(Component.text(" - ", NamedTextColor.AQUA))
                        .append(Component.text(plot.getWorld(), NamedTextColor.GOLD));
                message = message.append(plotLine);
                if (i < plots.size() - 1) message = message.append(Component.text("\n"));
            }
            int usedArea = plots.stream().map(Plot::getPlotArea).reduce(0, Integer::sum);
            int remainingArea = GlobalConfigService.getAsInteger(GlobalConfig.PLOT_MAX_SIZE) - usedArea;
            Component summary = Component.text("\nUsed: ", NamedTextColor.GREEN)
                    .append(Component.text(usedArea + " blocks", NamedTextColor.YELLOW))
                    .append(Component.text(" Remaining: ", NamedTextColor.GREEN))
                    .append(Component.text(remainingArea + "", NamedTextColor.YELLOW));
            message = message.append(summary);
            commandSender.sendMessage(message);
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
