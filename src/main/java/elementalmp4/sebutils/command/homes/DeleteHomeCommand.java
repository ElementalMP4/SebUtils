package main.java.elementalmp4.sebutils.command.homes;

import main.java.elementalmp4.sebutils.annotation.SebUtilsCommand;
import main.java.elementalmp4.sebutils.command.AbstractCommand;
import main.java.elementalmp4.sebutils.completer.HomesTabCompleter;
import main.java.elementalmp4.sebutils.service.HomeService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

@SebUtilsCommand
public class DeleteHomeCommand extends AbstractCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        String homeName = args.length == 0 ? "default" : args[0];
        if (HomeService.userHasHome(commandSender.getName(), homeName)) {
            HomeService.deleteHome(commandSender.getName(), homeName);
            Component message = Component.text("Deleted your home ", NamedTextColor.RED)
                    .append(Component.text(homeName, NamedTextColor.YELLOW));
            commandSender.sendMessage(message);
        } else {
            commandSender.sendMessage(Component.text("You don't have a home by that name, or you haven't set a default home yet!", NamedTextColor.RED));
        }
        return true;
    }

    @Override
    public String getCommandName() {
        return "delhome";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return new HomesTabCompleter();
    }
}
