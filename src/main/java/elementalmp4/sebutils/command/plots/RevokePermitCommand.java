package main.java.elementalmp4.sebutils.command.plots;

import main.java.elementalmp4.sebutils.SebUtils;
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
            commandSender.sendMessage(Component.text("Player could not be found!", NamedTextColor.RED));
            return true;
        }

        Optional<Integer> id = Converter.tryStringToInt(args[1]);
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

        if (!PermitService.userHasPermit(id.get(), playerToRevoke.getName())) {
            commandSender.sendMessage(Component.text(playerToRevoke.getName() + " does not have a permit on this plot!", NamedTextColor.RED));
            return true;
        }

        PermitService.revokePermit(id.get(), playerToRevoke.getName());
        Component message = Component.text("Removed ", NamedTextColor.GREEN)
                .append(Component.text(playerToRevoke.getName(), NamedTextColor.YELLOW))
                .append(Component.text(" from plot ", NamedTextColor.GREEN))
                .append(Component.text(String.valueOf(id.get()), NamedTextColor.YELLOW));
        commandSender.sendMessage(message);
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
