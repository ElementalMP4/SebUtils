package main.java.elementalmp4.sebutils.command.homes;

import main.java.elementalmp4.sebutils.annotation.SebUtilsCommand;
import main.java.elementalmp4.sebutils.command.AbstractCommand;
import main.java.elementalmp4.sebutils.entity.Home;
import main.java.elementalmp4.sebutils.service.HomeService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
            commandSender.sendMessage(Component.text("You haven't set any homes!", NamedTextColor.RED));
        } else {
            Component message = Component.text("Your homes:\n", NamedTextColor.RED, TextDecoration.BOLD);
            for (int i = 0; i < homes.size(); i++) {
                Home home = homes.get(i);
                message = message.append(home.toChatComponent(false));
                if (i < homes.size() - 1) message = message.append(Component.text("\n"));
            }
            commandSender.sendMessage(message);
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
