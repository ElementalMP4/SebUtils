package main.java.elementalmp4.completer;

import main.java.elementalmp4.service.GraveService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class GraveCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return GraveService.getGraveIds(commandSender.getName());
    }
}