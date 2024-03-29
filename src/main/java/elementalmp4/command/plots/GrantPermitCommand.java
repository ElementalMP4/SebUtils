package main.java.elementalmp4.command.plots;

import main.java.elementalmp4.SebUtils;
import main.java.elementalmp4.command.AbstractCommand;
import main.java.elementalmp4.command.SebUtilsCommand;
import main.java.elementalmp4.completer.PermitCompleter;
import main.java.elementalmp4.service.PermitService;
import main.java.elementalmp4.service.PlotService;
import main.java.elementalmp4.utils.Converter;
import main.java.elementalmp4.utils.Plot;
import org.bukkit.ChatColor;
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
            commandSender.sendMessage(ChatColor.RED + "Player could not be found!");
            return true;
        }

        Optional<Integer> id = Converter.tryStringToInt(args[1]);
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

        if (PermitService.userHasPermit(id.get(), playerToAdd.getName())) {
            commandSender.sendMessage(ChatColor.RED + playerToAdd.getName() + " is already added to this plot!");
            return true;
        }

        PermitService.grantPermit(id.get(), playerToAdd.getName());
        commandSender.sendMessage(ChatColor.GREEN + "Added " + ChatColor.YELLOW + playerToAdd.getName() + ChatColor.GREEN + " to plot " + ChatColor.YELLOW + id.get());
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
