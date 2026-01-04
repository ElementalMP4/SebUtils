package main.java.elementalmp4.sebutils.command.admin;

import main.java.elementalmp4.sebutils.annotation.SebUtilsCommand;
import main.java.elementalmp4.sebutils.command.AbstractCommand;
import main.java.elementalmp4.sebutils.entity.Home;
import main.java.elementalmp4.sebutils.service.HomeService;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

@SebUtilsCommand
public class ListHomesCommand extends AbstractCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage(NamedTextColor.RED + "You must specify a player!");
            return true;
        }

        List<Home> homes = HomeService.getHomes(args[0]);
        if (homes.isEmpty()) {
            commandSender.sendMessage(NamedTextColor.RED + "No homes found for player " + NamedTextColor.YELLOW + args[0]);
        } else {
            TextComponent message = new TextComponent();
            message.addExtra(NamedTextColor.YELLOW + args[0] + NamedTextColor.RED + "'s homes:\n");
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
