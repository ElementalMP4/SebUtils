package main.java.elementalmp4.sebutils.command.plots;

import main.java.elementalmp4.sebutils.annotation.SebUtilsCommand;
import main.java.elementalmp4.sebutils.command.AbstractCommand;
import main.java.elementalmp4.sebutils.entity.Plot;
import main.java.elementalmp4.sebutils.service.PlotService;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Optional;

@SebUtilsCommand
public class PlotCommand extends AbstractCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        Location loc = ((Player) commandSender).getLocation();
        Optional<Plot> plot = PlotService.blockIsOwned(loc.getBlockX(), loc.getBlockZ(), loc.getWorld().getName());

        if (plot.isEmpty()) {
            commandSender.sendMessage(NamedTextColor.GOLD + "There is no plot at this location");
            return true;
        }

        Plot p = plot.get();
        if (p.getOwner().equals(commandSender.getName())) {
            commandSender.sendMessage(NamedTextColor.GREEN + "You own a plot here with ID " + NamedTextColor.YELLOW + p.getId());
        } else {
            commandSender.sendMessage(NamedTextColor.RED + "This plot belongs to " + NamedTextColor.GOLD + p.getOwner());
        }
        return true;
    }

    @Override
    public String getCommandName() {
        return "plot";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return null;
    }
}
