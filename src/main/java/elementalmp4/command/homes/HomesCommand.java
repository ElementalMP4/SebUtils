package main.java.elementalmp4.command.homes;

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
public class HomesCommand extends AbstractCommand {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        List<Home> homes = HomeService.getHomes(commandSender.getName());
        if (homes.isEmpty()) {
            commandSender.sendMessage(ChatColor.RED + "You haven't set any homes!");
        } else {
            TextComponent message = new TextComponent();
            message.addExtra("" + ChatColor.RED + ChatColor.BOLD + "Your homes:\n");
            for (int i = 0; i < homes.size(); i++) {
                Home home = homes.get(i);
                message.addExtra(home.toChatComponent(false));
                if (i < homes.size() - 1) message.addExtra("\n");
            }
            commandSender.spigot().sendMessage(message);
        }
        return true;
    }

    @Override
    public String getCommandName() {
        return "homes";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return null;
    }
}
