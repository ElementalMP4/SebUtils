package main.java.elementalmp4.sebutils.command.plots;

import main.java.elementalmp4.sebutils.SebUtils;
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
import org.bukkit.entity.Player;

import java.util.Optional;

@SebUtilsCommand
public class GrantPermitCommand extends AbstractCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        if (args.length < 2) {
            return false;
        }

        Player playerToAdd = SebUtils.getPlugin().getServer().getPlayer(args[0]);
        if (playerToAdd == null) {
            commandSender.sendMessage(NamedTextColor.RED + "Player could not be found!");
            return true;
        }

        Optional<Integer> id = Converter.tryStringToInt(args[1]);
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

        if (PermitService.userHasPermit(id.get(), playerToAdd.getName())) {
            commandSender.sendMessage(NamedTextColor.RED + playerToAdd.getName() + " is already added to this plot!");
            return true;
        }

        PermitService.grantPermit(id.get(), playerToAdd.getName());
        commandSender.sendMessage(NamedTextColor.GREEN + "Added " + NamedTextColor.YELLOW + playerToAdd.getName() + NamedTextColor.GREEN + " to plot " + NamedTextColor.YELLOW + id.get());
        return true;
    }

    @Override
    public String getCommandName() {
        return "grantpermit";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return new PermitCompleter();
    }
}
