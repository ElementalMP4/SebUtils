package main.java.elementalmp4.command.admin;

import main.java.elementalmp4.GlobalConfig;
import main.java.elementalmp4.annotation.SebUtilsCommand;
import main.java.elementalmp4.command.AbstractCommand;
import main.java.elementalmp4.completer.SlackTabCompleter;
import main.java.elementalmp4.service.GlobalConfigService;
import main.java.elementalmp4.utils.CommandRunnable;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

@SebUtilsCommand
public class SlackCommand extends AbstractCommand {

    private static final CommandRunnable configSubCommand = (CommandSender sender, List<String> args) -> {
        TextComponent message = new TextComponent();

        boolean enabled = GlobalConfigService.getAsBoolean(GlobalConfig.SLACK_INTEGRATION_ENABLED);
        TextComponent statusComponent = new TextComponent();
        statusComponent.addExtra(ChatColor.YELLOW + "Enabled? " + format(enabled) + "\n");

        String token = GlobalConfigService.getValue(GlobalConfig.SLACK_WEBHOOK);
        TextComponent tokenComponent = new TextComponent();
        statusComponent.addExtra(ChatColor.YELLOW + "Webhook: " + ChatColor.AQUA + token);

        message.addExtra(statusComponent);
        message.addExtra(tokenComponent);

        sender.spigot().sendMessage(message);

    };

    private static final CommandRunnable enableSubCommand = (CommandSender sender, List<String> args) -> {
        if (GlobalConfigService.getAsBoolean(GlobalConfig.SLACK_INTEGRATION_ENABLED)) {
            sender.sendMessage(ChatColor.RED + "Slack integration is already enabled!");
        } else {
            String webhook = GlobalConfigService.getValue(GlobalConfig.SLACK_INTEGRATION_ENABLED);
            if (webhook.equals("Not set")) {
                sender.sendMessage(ChatColor.RED + "Slack webhook needs to be set!");
            } else {
                GlobalConfigService.set(GlobalConfig.SLACK_INTEGRATION_ENABLED, "true");
                sender.sendMessage(ChatColor.GREEN + "Slack integration enabled!");
            }
        }
    };

    private static final CommandRunnable disableSubCommand = (CommandSender sender, List<String> args) -> {
        if (!GlobalConfigService.getAsBoolean(GlobalConfig.SLACK_INTEGRATION_ENABLED)) {
            sender.sendMessage(ChatColor.RED + "Slack integration is already disabled!");
        } else {
            GlobalConfigService.set(GlobalConfig.SLACK_INTEGRATION_ENABLED, "false");
            sender.sendMessage(ChatColor.GREEN + "Slack integration has been disabled!");
        }
    };

    private static final CommandRunnable webhookSubCommand = (CommandSender sender, List<String> args) -> {
        if (args.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "You need to specify a Slack webhook URL!");
        } else {
            String webhook = args.get(0);
            GlobalConfigService.set(GlobalConfig.SLACK_WEBHOOK, webhook);
            if (GlobalConfigService.getAsBoolean(GlobalConfig.SLACK_INTEGRATION_ENABLED)) {
                sender.sendMessage(ChatColor.GREEN + "Set webhook to " + webhook);
            } else {
                sender.sendMessage(ChatColor.GREEN + "Set webhook to " + webhook + " - Please note that the Slack integration is currently disabled!");
            }
        }
    };

    private static String format(boolean enabled) {
        return (enabled ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No");
    }

    private static final Map<String, CommandRunnable> VALID_COMMANDS = Map.of(
            "config", configSubCommand,
            "enable", enableSubCommand,
            "disable", disableSubCommand,
            "webhook", webhookSubCommand
    );

    public static Set<String> getValidCommands() {
        return VALID_COMMANDS.keySet();
    }

    @Override
    public String getCommandName() {
        return "slack";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return new SlackTabCompleter();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage(ChatColor.RED + "You must specify one of " + String.join(", ", getValidCommands()));
            return true;
        }

        if (!VALID_COMMANDS.containsKey(args[0])) {
            commandSender.sendMessage(ChatColor.RED + "Command must be one of " + String.join(", ", getValidCommands()));
            return true;
        }

        CommandRunnable subCommand = VALID_COMMANDS.get(args[0]);
        subCommand.run(commandSender, List.of(args).subList(1, args.length));

        return true;
    }
}
