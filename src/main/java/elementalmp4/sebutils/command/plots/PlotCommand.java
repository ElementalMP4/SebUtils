package main.java.elementalmp4.sebutils.command.plots;

import main.java.elementalmp4.sebutils.annotation.SebUtilsCommand;
import main.java.elementalmp4.sebutils.command.AbstractCommand;
import main.java.elementalmp4.sebutils.entity.Plot;
import main.java.elementalmp4.sebutils.service.PlotService;
import net.kyori.adventure.text.Component;
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
            commandSender.sendMessage(Component.text("There is no plot at this location", NamedTextColor.GOLD));
            return true;
        }

        Plot p = plot.get();
        if (p.getOwner().equals(commandSender.getName())) {
            Component message = Component.text("You own a plot here with ID ", NamedTextColor.GREEN)
                    .append(Component.text(String.valueOf(p.getId()), NamedTextColor.YELLOW));
            commandSender.sendMessage(message);
        } else {
            Component message = Component.text("This plot belongs to ", NamedTextColor.RED)
                    .append(Component.text(p.getOwner(), NamedTextColor.GOLD));
            commandSender.sendMessage(message);
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
