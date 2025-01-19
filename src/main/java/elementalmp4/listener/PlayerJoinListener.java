package main.java.elementalmp4.listener;

import main.java.elementalmp4.annotation.SebUtilsListener;
import main.java.elementalmp4.service.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@SebUtilsListener
public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        NicknameService.cacheProfile(event.getPlayer().getName());
        DiscordService.sendJoinMessage(event.getPlayer());
        PVPToggleService.cachePlayer(event.getPlayer().getName());
        SlackService.sendJoinMessage(event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        AfkService.removeUser(event.getPlayer().getName());
        NicknameService.removeProfileCache(event.getPlayer().getName());
        DiscordService.sendLeaveMessage(event.getPlayer());
        PVPToggleService.removePlayerCache(event.getPlayer().getName());
        SlackService.sendLeaveMessage(event.getPlayer().getName());
        OllamaService.clearConversation(event.getPlayer().getName());
    }

}