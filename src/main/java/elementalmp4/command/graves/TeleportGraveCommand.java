package main.java.elementalmp4.command.graves;

import main.java.elementalmp4.annotation.SebUtilsCommand;
import main.java.elementalmp4.command.AbstractCommand;
import main.java.elementalmp4.completer.GraveCompleter;
import main.java.elementalmp4.entity.Grave;
import main.java.elementalmp4.service.GraveService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Optional;

@SebUtilsCommand
public class TeleportGraveCommand extends AbstractCommand {
    @Override
    public String getCommandName() {
        return "tpgrave";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return new GraveCompleter();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage(ChatColor.RED + "You need to specify a grave ID!");
            return true;
        }

        Optional<Grave> grave = GraveService.getGrave(commandSender.getName(), args[0]);
        if (grave.isEmpty()) {
            commandSender.sendMessage(ChatColor.RED + "Grave not found!");
            return true;
        }

        grave.get().teleport((Player) commandSender);
        return true;
    }
}
