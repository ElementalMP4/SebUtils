package main.java.elementalmp4.sebutils.command.graves;

import main.java.elementalmp4.sebutils.annotation.SebUtilsCommand;
import main.java.elementalmp4.sebutils.command.AbstractCommand;
import main.java.elementalmp4.sebutils.entity.Grave;
import main.java.elementalmp4.sebutils.service.GraveService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

@SebUtilsCommand
public class GravesListCommand extends AbstractCommand {
    @Override
    public String getCommandName() {
        return "listgraves";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return null;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        String target = commandSender.getName();
        List<Grave> graves = GraveService.getGraves(target);

        if (graves.isEmpty()) {
            commandSender.sendMessage(Component.text("No graves were found!", NamedTextColor.RED));
            return true;
        }

        Component message = Component.empty();
        for (int i = 0; i < graves.size(); i++) {
            Grave grave = graves.get(i);
            message = message.append(grave.toChatComponent());
            if (i < graves.size() - 1) message = message.append(Component.text("\n"));
        }

        commandSender.sendMessage(message);
        return true;
    }
}
