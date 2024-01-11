package main.java.elementalmp4.command;

import main.java.elementalmp4.service.GlobalConfigService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShowConfigCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        Map<String, String> config = GlobalConfigService.getAllConfig();
        List<String> out = new ArrayList<>();
        out.add(ChatColor.RED + "SebUtils Config");
        for (String key : config.keySet()) {
            out.add(ChatColor.AQUA + key + ChatColor.RESET + " - " + ChatColor.YELLOW + config.get(key));
        }
        commandSender.sendMessage(String.join("\n", out));
        return true;
    }
}
