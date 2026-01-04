package main.java.elementalmp4.sebutils.command.admin;

import main.java.elementalmp4.sebutils.annotation.SebUtilsCommand;
import main.java.elementalmp4.sebutils.command.AbstractCommand;
import main.java.elementalmp4.sebutils.entity.Home;
import main.java.elementalmp4.sebutils.service.HomeService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

@SebUtilsCommand
public class ListHomesCommand extends AbstractCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage(Component.text("You must specify a player!", NamedTextColor.RED));
            return true;
        }

        List<Home> homes = HomeService.getHomes(args[0]);
        if (homes.isEmpty()) {
            Component message = Component.text("No homes found for player ", NamedTextColor.RED)
                    .append(Component.text(args[0], NamedTextColor.YELLOW));
            commandSender.sendMessage(message);
        } else {
            Component message = Component.text(args[0], NamedTextColor.YELLOW)
                    .append(Component.text("'s homes:\n", NamedTextColor.RED));
            for (int i = 0; i < homes.size(); i++) {
                Home home = homes.get(i);
                message = message.append(home.toChatComponent(true));
                if (i < homes.size() - 1) message = message.append(Component.text("\n"));
            }
            commandSender.sendMessage(message);
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
