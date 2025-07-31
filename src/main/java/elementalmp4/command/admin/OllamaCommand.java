package main.java.elementalmp4.command.admin;

import main.java.elementalmp4.GlobalConfig;
import main.java.elementalmp4.annotation.SebUtilsCommand;
import main.java.elementalmp4.command.AbstractCommand;
import main.java.elementalmp4.completer.OllamaTabCompleter;
import main.java.elementalmp4.service.GlobalConfigService;
import main.java.elementalmp4.service.OllamaService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Set;

@SebUtilsCommand
public class OllamaCommand extends AbstractCommand {

    private static final Set<String> VALID_SUBCOMMANDS = Set.of("enable", "disable", "model");

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length == 0) {
            boolean ollamaEnabled = GlobalConfigService.getAsBoolean(GlobalConfig.OLLAMA_ENABLED);
            String currentModel = GlobalConfigService.getValue(GlobalConfig.OLLAMA_MODEL);
            commandSender.sendMessage("Ollama is currently " + format(ollamaEnabled));
            commandSender.sendMessage("Current model: " + ChatColor.YELLOW + currentModel);
            return true;
        }

        String subcommand = args[0].toLowerCase();

        if (!VALID_SUBCOMMANDS.contains(subcommand)) {
            commandSender.sendMessage(ChatColor.RED + "Invalid argument. Use enable, disable, or model <name>");
            return true;
        }

        switch (subcommand) {
            case "enable":
                OllamaService.startClient();
                GlobalConfigService.set(GlobalConfig.OLLAMA_ENABLED, "true");
                commandSender.sendMessage("Ollama is now " + format(true));
                break;

            case "disable":
                OllamaService.stopClient();
                GlobalConfigService.set(GlobalConfig.OLLAMA_ENABLED, "false");
                commandSender.sendMessage("Ollama is now " + format(false));
                break;

            case "model":
                if (args.length < 2) {
                    commandSender.sendMessage(ChatColor.RED + "You must specify a model name after 'model'");
                    return true;
                }
                String modelName = args[1];
                GlobalConfigService.set(GlobalConfig.OLLAMA_MODEL, modelName);
                commandSender.sendMessage(ChatColor.GREEN + "Model set to: " + ChatColor.YELLOW + modelName);
                break;
        }

        return true;
    }

    private String getFormattedHost() {
        return ChatColor.YELLOW + GlobalConfigService.getValue(GlobalConfig.OLLAMA_HOST);
    }

    private String format(boolean enabled) {
        return (enabled ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled")
                + " and the host is " + getFormattedHost();
    }

    @Override
    public String getCommandName() {
        return "ollama";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return new OllamaTabCompleter();
    }
}