package main.java.elementalmp4.command.admin;

import main.java.elementalmp4.annotation.SebUtilsCommand;
import main.java.elementalmp4.command.AbstractCommand;
import main.java.elementalmp4.entity.Home;
import main.java.elementalmp4.service.HomeService;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

@SebUtilsCommand
public class ListHomesCommand extends AbstractCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage(ChatColor.RED + "You must specify a player!");
            return true;
        }

        List<Home> homes = HomeService.getHomes(args[0]);
        if (homes.isEmpty()) {
            commandSender.sendMessage(ChatColor.RED + "No homes found for player " + ChatColor.YELLOW + args[0]);
        } else {
            TextComponent message = new TextComponent();
            message.addExtra(ChatColor.YELLOW + args[0] + ChatColor.RED + "'s homes:\n");
            for (int i = 0; i < homes.size(); i++) {
                Home home = homes.get(i);
                message.addExtra(home.toChatComponent(true));
                if (i < homes.size() - 1) message.addExtra("\n");
            }
            commandSender.spigot().sendMessage(message);
        }
        return true;
    }

    @Override
    public String getCommandName() {
        return "listhomes";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return null;
    }
}
