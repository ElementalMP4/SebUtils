package main.java.elementalmp4.command.plots;

import main.java.elementalmp4.SebUtils;
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
import org.bukkit.entity.Player;

import java.util.Optional;

@SebUtilsCommand
public class RevokePermitCommand extends AbstractCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length < 2) {
            return false;
        }

        Player playerToRevoke = SebUtils.getPlugin().getServer().getPlayer(args[0]);
        if (playerToRevoke == null) {
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

        if (!PermitService.userHasPermit(id.get(), playerToRevoke.getName())) {
            commandSender.sendMessage(ChatColor.RED + playerToRevoke.getName() + " does not have a permit on this plot!");
            return true;
        }

        PermitService.revokePermit(id.get(), playerToRevoke.getName());
        commandSender.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.YELLOW + playerToRevoke.getName() + ChatColor.GREEN + " from plot " + ChatColor.YELLOW + id.get());
        return true;
    }

    @Override
    public String getCommandName() {
        return "revokepermit";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return new PermitCompleter();
    }
}
