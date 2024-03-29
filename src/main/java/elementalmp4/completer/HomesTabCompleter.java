package main.java.elementalmp4.completer;

import main.java.elementalmp4.service.HomeService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class HomesTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return HomeService.getHomeNames(commandSender.getName());
    }
}
