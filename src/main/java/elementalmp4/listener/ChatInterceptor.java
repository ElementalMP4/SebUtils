package main.java.elementalmp4.listener;

import main.java.elementalmp4.annotation.SebUtilsListener;
import main.java.elementalmp4.service.NicknameService;
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
    }

}
