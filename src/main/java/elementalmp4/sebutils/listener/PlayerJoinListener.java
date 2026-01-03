package main.java.elementalmp4.sebutils.listener;

import main.java.elementalmp4.sebutils.annotation.SebUtilsListener;
import main.java.elementalmp4.sebutils.service.*;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@SebUtilsListener
public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        DiscordService.sendJoinMessage(event.getPlayer());
        PVPToggleService.cachePlayer(event.getPlayer().getName());
        SlackService.sendJoinMessage(event.getPlayer().getName());

        // Cache profile before sending join message
        NicknameService.cacheProfile(event.getPlayer().getName());
        event.setJoinMessage(ChatColor.WHITE + "[" + ChatColor.GREEN + "+" + ChatColor.WHITE + "] " + NicknameService.getPlayerNameCustomised(event.getPlayer().getName()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Message must be sent before profile cache is cleared
        event.setQuitMessage(ChatColor.WHITE + "[" + ChatColor.RED + "-" + ChatColor.WHITE + "] " + NicknameService.getPlayerNameCustomised(event.getPlayer().getName()));
        AfkService.removeUser(event.getPlayer().getName());
        NicknameService.removeProfileCache(event.getPlayer().getName());
        DiscordService.sendLeaveMessage(event.getPlayer());
        PVPToggleService.removePlayerCache(event.getPlayer().getName());
        SlackService.sendLeaveMessage(event.getPlayer().getName());
        OllamaService.clearConversation(event.getPlayer().getName());
    }

}