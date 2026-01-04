package main.java.elementalmp4.sebutils.listener;

import main.java.elementalmp4.sebutils.annotation.SebUtilsListener;
import main.java.elementalmp4.sebutils.config.GlobalConfig;
import main.java.elementalmp4.sebutils.modules.DiscordModule;
import main.java.elementalmp4.sebutils.modules.OllamaModule;
import main.java.elementalmp4.sebutils.service.AfkService;
import main.java.elementalmp4.sebutils.service.GlobalConfigService;
import main.java.elementalmp4.sebutils.service.NicknameService;
import main.java.elementalmp4.sebutils.service.PVPToggleService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static main.java.elementalmp4.sebutils.SebUtils.getModuleManager;

@SebUtilsListener
public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        boolean discordRunning = GlobalConfigService.getAsBoolean(GlobalConfig.DISCORD_ENABLED);
        if (discordRunning) getModuleManager().get(DiscordModule.class).sendJoinMessage(event.getPlayer());
        PVPToggleService.cachePlayer(event.getPlayer().getName());

        // Cache profile before sending join message
        NicknameService.cacheProfile(event.getPlayer().getName());
        event.joinMessage(Component.text("[", NamedTextColor.WHITE)
                .append(Component.text("+", NamedTextColor.GREEN))
                .append(Component.text("] ", NamedTextColor.WHITE))
                .append(NicknameService.getPlayerNameCustomised(event.getPlayer().getName()))
        );
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Message must be sent before profile cache is cleared
        event.quitMessage(Component.text("[", NamedTextColor.WHITE)
                .append(Component.text("-", NamedTextColor.RED))
                .append(Component.text("] ", NamedTextColor.WHITE))
                .append(NicknameService.getPlayerNameCustomised(event.getPlayer().getName()))
        );
        AfkService.removeUser(event.getPlayer().getName());
        NicknameService.removeProfileCache(event.getPlayer().getName());
        PVPToggleService.removePlayerCache(event.getPlayer().getName());

        boolean discordRunning = GlobalConfigService.getAsBoolean(GlobalConfig.DISCORD_ENABLED);
        if (discordRunning) getModuleManager().get(DiscordModule.class).sendLeaveMessage(event.getPlayer());

        boolean ollamaRunning = GlobalConfigService.getAsBoolean(GlobalConfig.OLLAMA_ENABLED);
        if (ollamaRunning) getModuleManager().get(OllamaModule.class).clearConversation(event.getPlayer().getName());
    }

}