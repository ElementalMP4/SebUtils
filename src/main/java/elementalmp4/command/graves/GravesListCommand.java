package main.java.elementalmp4.command.graves;

import main.java.elementalmp4.annotation.SebUtilsCommand;
import main.java.elementalmp4.command.AbstractCommand;
import main.java.elementalmp4.entity.Grave;
import main.java.elementalmp4.service.GraveService;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
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
            commandSender.sendMessage(ChatColor.RED + "No graves were found!");
            return true;
        }

        TextComponent message = new TextComponent();

        for (int i = 0; i < graves.size(); i++) {
            Grave grave = graves.get(i);
            message.addExtra(grave.toChatComponent());
            if (i < graves.size() - 1) message.addExtra("\n");
        }

        commandSender.spigot().sendMessage(message);
        return true;
    }
}
