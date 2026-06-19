package main.java.elementalmp4.sebutils.listener;

import main.java.elementalmp4.sebutils.config.GlobalConfig;
import main.java.elementalmp4.sebutils.service.GlobalConfigService;
import main.java.elementalmp4.sebutils.service.PendingAccessService;
import main.java.elementalmp4.sebutils.utils.WhitelistCache;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

import static org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST;

public class LoginListener implements Listener {

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        // Skip if the whitelist is disabled
        if (!Bukkit.hasWhitelist()) {
            return;
        }

        // Skip if the user is already whitelisted
        UUID userId = event.getPlayerProfile().getId();
        if (WhitelistCache.isWhitelisted(userId)) {
            return;
        }

        // Skip if Discord integration is turned off
        boolean discordRunning = GlobalConfigService.getAsBoolean(GlobalConfig.DISCORD_ENABLED);
        if (!discordRunning) {
            return;
        }

        // Create an access request if one does not exist
        boolean hasRequest = PendingAccessService.accessRequestPending(userId);
        if (hasRequest) {
            PendingAccessService.createAccessRequest(userId, event.getPlayerProfile().getName());
        }

        // Send disallowed message
        Component message = Component.text()
                .append(Component.text("Your application is still awaiting approval.\n"))
                .append(Component.text("Please check back later!"))
                .build();
        event.disallow(KICK_WHITELIST, message);
    }
}