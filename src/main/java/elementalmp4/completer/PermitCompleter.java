package main.java.elementalmp4.completer;

import main.java.elementalmp4.service.PlotService;
import main.java.elementalmp4.utils.Plot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class PermitCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
        if (command.getName().equals("permits")) return getPlotList(commandSender.getName());
        if (args.length == 2) return getPlotList(commandSender.getName());
        return commandSender.getServer().getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
    }

    private List<String> getPlotList(String player) {
        return PlotService.getUserPlots(player).stream()
                .map(Plot::getId)
                .map(String::valueOf)
                .collect(Collectors.toList());
    }
}
