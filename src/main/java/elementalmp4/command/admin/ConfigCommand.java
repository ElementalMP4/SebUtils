package main.java.elementalmp4.command.admin;

import main.java.elementalmp4.GlobalConfig;
import main.java.elementalmp4.annotation.SebUtilsCommand;
import main.java.elementalmp4.command.AbstractCommand;
import main.java.elementalmp4.completer.ConfigCompleter;
import main.java.elementalmp4.service.GlobalConfigService;
import main.java.elementalmp4.utils.CommandRunnable;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SebUtilsCommand
public class ConfigCommand extends AbstractCommand {

    private static final CommandRunnable importCommand = (CommandSender sender, List<String> args) -> {
        boolean success = GlobalConfigService.importConfig();
        if (success) {
            sender.sendMessage(ChatColor.GREEN + "Imported config successfully!");
        } else {
            sender.sendMessage(ChatColor.RED + "Failed to import config! Check server logs for details");
        }
    };

    private static final CommandRunnable exportCommand = (CommandSender sender, List<String> args) -> {
        boolean success = GlobalConfigService.exportConfig();
        if (success) {
            sender.sendMessage(ChatColor.GREEN + "Exported config successfully!");
        } else {
            sender.sendMessage(ChatColor.RED + "Failed to export config! Check server logs for details");
        }
    };

    private static final CommandRunnable listCommand = (CommandSender sender, List<String> args) -> {
        Map<String, String> config = GlobalConfigService.getAllConfig();
        List<String> out = new ArrayList<>();
        out.add(ChatColor.RED + "SebUtils Config");
        out.add(ChatColor.WHITE + "Some items are redacted for security. To view them, export the config.");
        for (GlobalConfig configItem : GlobalConfig.values()) {
            String value = configItem.isVisible() ? config.get(configItem.getKey()) : "REDACTED";
            out.add(ChatColor.AQUA + configItem.getDisplayName() + ChatColor.RESET + " - " + ChatColor.YELLOW + value);
        }
        sender.sendMessage(String.join("\n", out));
    };

    private static final Map<String, CommandRunnable> VALID_COMMANDS = Map.of(
            "import", importCommand,
            "export", exportCommand,
            "list", listCommand
    );

    public static List<String> getValidCommands() {
        return new ArrayList<>(VALID_COMMANDS.keySet());
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        if (args.length == 0) {
            listCommand.run(commandSender, new ArrayList<>());
        } else {
            if (!VALID_COMMANDS.containsKey(args[0])) {
                commandSender.sendMessage(ChatColor.RED + "Command must be one of " + String.join(", ", getValidCommands()));
                return true;
            }
            CommandRunnable subCommand = VALID_COMMANDS.get(args[0]);
            subCommand.run(commandSender, List.of(args).subList(1, args.length));
        }
        return true;
    }

    @Override
    public String getCommandName() {
        return "config";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return new ConfigCompleter();
    }
}
