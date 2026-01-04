package main.java.elementalmp4.sebutils.command.plots;

import main.java.elementalmp4.sebutils.annotation.SebUtilsCommand;
import main.java.elementalmp4.sebutils.command.AbstractCommand;
import main.java.elementalmp4.sebutils.completer.PermitCompleter;
import main.java.elementalmp4.sebutils.entity.Plot;
import main.java.elementalmp4.sebutils.service.PermitService;
import main.java.elementalmp4.sebutils.service.PlotService;
import main.java.elementalmp4.sebutils.utils.Converter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;
import java.util.Optional;

@SebUtilsCommand
public class PermitsCommand extends AbstractCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
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
            commandSender.sendMessage(Component.text("Plot not found - Try the ", NamedTextColor.RED)
                    .append(Component.text("/plots", NamedTextColor.YELLOW))
                    .append(Component.text(" command to see a list of your plots, and use ", NamedTextColor.RED))
                    .append(Component.text("/plot", NamedTextColor.YELLOW))
                    .append(Component.text(" to see details about the plot you are currently in", NamedTextColor.RED)));
            return true;
        }

        List<String> playersPermitted = PermitService.getPermits(id.get());
        if (playersPermitted.isEmpty()) {
            commandSender.sendMessage(Component.text("There are no permits for this plot!", NamedTextColor.RED));
            return true;
        }
        commandSender.sendMessage(Component.text("Player permits for plot ", NamedTextColor.GREEN)
                .append(Component.text(String.valueOf(id.get()), NamedTextColor.YELLOW))
                .append(Component.text("\n" + String.join(", ", playersPermitted), NamedTextColor.WHITE)));

        return true;
    }

    @Override
    public String getCommandName() {
        return "permits";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return new PermitCompleter();
    }
}
