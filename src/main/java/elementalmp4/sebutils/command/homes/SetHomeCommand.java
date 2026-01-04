package main.java.elementalmp4.sebutils.command.homes;

import main.java.elementalmp4.sebutils.annotation.SebUtilsCommand;
import main.java.elementalmp4.sebutils.command.AbstractCommand;
import main.java.elementalmp4.sebutils.completer.EmptyTabCompleter;
import main.java.elementalmp4.sebutils.service.HomeService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

@SebUtilsCommand
public class SetHomeCommand extends AbstractCommand {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        String homeName = args.length == 0 ? "default" : args[0];
        Player player = ((Player) commandSender);
        int x = player.getLocation().getBlockX();
        int y = player.getLocation().getBlockY();
        int z = player.getLocation().getBlockZ();
        HomeService.setHome(commandSender.getName(), player.getWorld().getName(), x, y, z, homeName);
        Component message = Component.text("Home ", NamedTextColor.RED)
                .append(Component.text(homeName, NamedTextColor.YELLOW))
                .append(Component.text(" has been set to ", NamedTextColor.RED))
                .append(Component.text(x + " " + y + " " + z, NamedTextColor.YELLOW));
        commandSender.sendMessage(message);
        return true;
    }

    @Override
    public String getCommandName() {
        return "sethome";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return new EmptyTabCompleter();
    }
}
