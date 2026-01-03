package main.java.elementalmp4.sebutils.listener;

import main.java.elementalmp4.sebutils.annotation.SebUtilsListener;
import main.java.elementalmp4.sebutils.service.DiscordService;
import main.java.elementalmp4.sebutils.service.NicknameService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@SebUtilsListener
public class ChatInterceptor implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }
        NicknameService.adaptMessageNickname(event);
        DiscordService.forwardPlayerMessage(event.getPlayer(), event.getMessage());
    }

}
