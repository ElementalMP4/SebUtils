package main.java.elementalmp4.command.plots;

import main.java.elementalmp4.annotation.SebUtilsCommand;
import main.java.elementalmp4.command.AbstractCommand;
import main.java.elementalmp4.completer.PermitCompleter;
import main.java.elementalmp4.entity.Plot;
import main.java.elementalmp4.service.PermitService;
import main.java.elementalmp4.service.PlotService;
import main.java.elementalmp4.utils.Converter;
import org.bukkit.ChatColor;
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

        List<String> playersPermitted = PermitService.getPermits(id.get());
        if (playersPermitted.isEmpty()) {
            commandSender.sendMessage(ChatColor.RED + "There are no permits for this plot!");
            return true;
        }
        commandSender.sendMessage(ChatColor.GREEN + "Player permits for plot " + ChatColor.YELLOW + id.get()
                + ChatColor.RESET + "\n" + String.join(", ", playersPermitted));

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
