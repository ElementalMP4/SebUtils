package main.java.elementalmp4.sebutils.command.graves;

import main.java.elementalmp4.sebutils.annotation.SebUtilsCommand;
import main.java.elementalmp4.sebutils.command.AbstractCommand;
import main.java.elementalmp4.sebutils.completer.GraveCompleter;
import main.java.elementalmp4.sebutils.entity.Grave;
import main.java.elementalmp4.sebutils.service.GraveService;
import main.java.elementalmp4.sebutils.utils.GameruleChecker;
import net.kyori.adventure.text.format.NamedTextColor;
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
            commandSender.sendMessage(NamedTextColor.RED + "You need to specify a grave ID!");
            return true;
        }

        Optional<Grave> grave = GraveService.getGrave(commandSender.getName(), args[0]);
        if (grave.isEmpty()) {
            commandSender.sendMessage(NamedTextColor.RED + "Grave not found!");
            return true;
        }

        grave.get().teleport((Player) commandSender);

        if (GameruleChecker.isKeepInventory(((Player) commandSender).getWorld())) {
            GraveService.removeGrave(grave.get().getId());
        }

        return true;
    }
}
