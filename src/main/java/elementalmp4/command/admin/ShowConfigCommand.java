package main.java.elementalmp4.command.admin;

import main.java.elementalmp4.command.AbstractCommand;
import main.java.elementalmp4.annotation.SebUtilsCommand;
import main.java.elementalmp4.service.GlobalConfigService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SebUtilsCommand
public class ShowConfigCommand extends AbstractCommand {
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

    @Override
    public String getCommandName() {
        return "showconfig";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return null;
    }
}
