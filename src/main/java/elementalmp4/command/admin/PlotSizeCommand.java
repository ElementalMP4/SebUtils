package main.java.elementalmp4.command.admin;

import main.java.elementalmp4.GlobalConfig;
import main.java.elementalmp4.annotation.SebUtilsCommand;
import main.java.elementalmp4.command.AbstractCommand;
import main.java.elementalmp4.service.GlobalConfigService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Optional;

@SebUtilsCommand
public class PlotSizeCommand extends AbstractCommand {
    @Override
    public String getCommandName() {
        return "plotsize";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return null;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        if (args.length == 0) {
            int size = GlobalConfigService.getAsInteger(GlobalConfig.PLOT_MAX_SIZE);
            commandSender.sendMessage(ChatColor.GREEN + "Maximum plot size is currently " + ChatColor.YELLOW + size + " blocks");
            return true;
        }

        Optional<Integer> potentialSize = parseInteger(args[0]);
        if (potentialSize.isEmpty()) {
            commandSender.sendMessage(ChatColor.RED + "Plot size must be a valid whole number!");
            return true;
        }

        int newSize = potentialSize.get();
        if (newSize < 100 || newSize > 100000) {
            commandSender.sendMessage(ChatColor.RED + "Plot size must be between 100 and 100,000 blocks!");
            return true;
        }

        GlobalConfigService.set(GlobalConfig.PLOT_MAX_SIZE, args[0]);
        commandSender.sendMessage(ChatColor.GREEN + "Maximum plot allocation has been set to " + ChatColor.YELLOW + newSize + " blocks");
        return true;
    }

    private Optional<Integer> parseInteger(String in) {
        try {
            return Optional.of(Integer.parseInt(in));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
