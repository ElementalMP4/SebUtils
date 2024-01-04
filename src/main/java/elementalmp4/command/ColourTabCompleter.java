package main.java.elementalmp4.command;

import main.java.elementalmp4.service.NicknameService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class ColourTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return NicknameService.getColourNamesList().stream().toList();
    }
}
