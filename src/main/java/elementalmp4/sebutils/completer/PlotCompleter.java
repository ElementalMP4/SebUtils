package main.java.elementalmp4.sebutils.completer;

import main.java.elementalmp4.sebutils.service.PlotService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class PlotCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return PlotService.getPlotIdList(commandSender.getName());
    }
}
