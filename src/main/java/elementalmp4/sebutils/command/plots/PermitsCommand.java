package main.java.elementalmp4.sebutils.command.plots;

import main.java.elementalmp4.sebutils.annotation.SebUtilsCommand;
import main.java.elementalmp4.sebutils.command.AbstractCommand;
import main.java.elementalmp4.sebutils.completer.PermitCompleter;
import main.java.elementalmp4.sebutils.entity.Plot;
import main.java.elementalmp4.sebutils.service.PermitService;
import main.java.elementalmp4.sebutils.service.PlotService;
import main.java.elementalmp4.sebutils.utils.Converter;
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
            commandSender.sendMessage(NamedTextColor.RED + "You need to specify a plot ID!");
            return true;
        }

        Optional<Integer> id = Converter.tryStringToInt(args[0]);
        if (id.isEmpty()) {
            commandSender.sendMessage(NamedTextColor.RED + "Plot ID must be a valid number!");
            return true;
        }

        Optional<Plot> plot = PlotService.getPlotByIdAndOwner(id.get(), commandSender.getName());
        if (plot.isEmpty()) {
            commandSender.sendMessage(NamedTextColor.RED + "Plot not found - Try the " + NamedTextColor.YELLOW + "/plots" + NamedTextColor.RED
                    + " command to see a list of your plots, and use " + NamedTextColor.YELLOW + "/plot" + NamedTextColor.RED
                    + " to see details about the plot you are currently in");
            return true;
        }

        List<String> playersPermitted = PermitService.getPermits(id.get());
        if (playersPermitted.isEmpty()) {
            commandSender.sendMessage(NamedTextColor.RED + "There are no permits for this plot!");
            return true;
        }
        commandSender.sendMessage(NamedTextColor.GREEN + "Player permits for plot " + NamedTextColor.YELLOW + id.get()
                + NamedTextColor.WHITE + "\n" + String.join(", ", playersPermitted));

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
