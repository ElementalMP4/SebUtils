package main.java.elementalmp4.command.admin;

import main.java.elementalmp4.GlobalConfig;
import main.java.elementalmp4.annotation.SebUtilsCommand;
import main.java.elementalmp4.command.AbstractCommand;
import main.java.elementalmp4.completer.DiscordTabCompleter;
import main.java.elementalmp4.service.DiscordService;
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
public class DiscordCommand extends AbstractCommand {

    private static final CommandRunnable configSubCommand = (CommandSender sender, List<String> args) -> {
        TextComponent message = new TextComponent();

        boolean enabled = GlobalConfigService.getAsBoolean(GlobalConfig.DISCORD_ENABLED);
        TextComponent statusComponent = new TextComponent();
        statusComponent.addExtra(ChatColor.YELLOW + "Enabled? " + format(enabled) + "\n");

        String channel = GlobalConfigService.getValue(GlobalConfig.DISCORD_CHANNEL);
        TextComponent channelComponent = new TextComponent();
        statusComponent.addExtra(ChatColor.YELLOW + "Channel: " + ChatColor.AQUA + channel + "\n");

        String token = GlobalConfigService.getValue(GlobalConfig.DISCORD_TOKEN);
        TextComponent tokenComponent = new TextComponent();
        statusComponent.addExtra(ChatColor.YELLOW + "Token: " + ChatColor.AQUA + token);

        message.addExtra(statusComponent);
        message.addExtra(channelComponent);
        message.addExtra(tokenComponent);

        sender.spigot().sendMessage(message);

    };

    private static final CommandRunnable enableSubCommand = (CommandSender sender, List<String> args) -> {
        if (GlobalConfigService.getAsBoolean(GlobalConfig.DISCORD_ENABLED)) {
            sender.sendMessage(ChatColor.RED + "Discord integration is already enabled!");
        } else {
            String token = GlobalConfigService.getValue(GlobalConfig.DISCORD_TOKEN);
            String channel = GlobalConfigService.getValue(GlobalConfig.DISCORD_CHANNEL);
            if (token.equals("Not set") || channel.equals("Not set")) {
                sender.sendMessage(ChatColor.RED + "Message channel and token both need to be set!");
            } else {
                GlobalConfigService.set(GlobalConfig.DISCORD_ENABLED, "true");
                DiscordService.startDiscordClient(false);
                sender.sendMessage(ChatColor.GREEN + "Discord integration enabled!");
            }
        }
    };

    private static final CommandRunnable disableSubCommand = (CommandSender sender, List<String> args) -> {
        if (!GlobalConfigService.getAsBoolean(GlobalConfig.DISCORD_ENABLED)) {
            sender.sendMessage(ChatColor.RED + "Discord integration is already disabled!");
        } else {
            GlobalConfigService.set(GlobalConfig.DISCORD_ENABLED, "false");
            DiscordService.close(false);
            sender.sendMessage(ChatColor.GREEN + "Discord integration has been disabled!");
        }
    };

    private static final CommandRunnable tokenSubCommand = (CommandSender sender, List<String> args) -> {
        if (args.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "You need to specify a valid Discord Bot token!");
        } else {
            String token = args.get(0);
            GlobalConfigService.set(GlobalConfig.DISCORD_TOKEN, token);
            if (GlobalConfigService.getAsBoolean(GlobalConfig.DISCORD_ENABLED)) {
                DiscordService.close(false);
                DiscordService.startDiscordClient(false);
                sender.sendMessage(ChatColor.GREEN + "Set token to " + token + " and restarted Discord client");
            } else {
                sender.sendMessage(ChatColor.GREEN + "Set token to " + token + " - Please note that the Discord client is currently disabled!");
            }
        }
    };

    private static final CommandRunnable channelSubCommand = (CommandSender sender, List<String> args) -> {
        if (args.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "You need to specify a valid Discord Text Channel!");
        } else {
            String channel = args.get(0);
            GlobalConfigService.set(GlobalConfig.DISCORD_CHANNEL, channel);
            if (GlobalConfigService.getAsBoolean(GlobalConfig.DISCORD_ENABLED)) {
                sender.sendMessage(ChatColor.GREEN + "Set channel to " + channel);
            } else {
                sender.sendMessage(ChatColor.GREEN + "Set channel to " + channel + " - Please note that the Discord client is currently disabled!");
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
            "token", tokenSubCommand,
            "channel", channelSubCommand
    );

    public static Set<String> getValidCommands() {
        return VALID_COMMANDS.keySet();
    }

    @Override
    public String getCommandName() {
        return "discord";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return new DiscordTabCompleter();
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
