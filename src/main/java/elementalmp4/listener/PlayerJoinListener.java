package main.java.elementalmp4.listener;

import main.java.elementalmp4.annotation.SebUtilsListener;
import main.java.elementalmp4.service.AfkService;
import main.java.elementalmp4.service.NicknameService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@SebUtilsListener
public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!NicknameService.userConfigExists(event.getPlayer().getName())) {
            NicknameService.addUser(event.getPlayer().getName());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        AfkService.removeUser(event.getPlayer().getName());
    }

}