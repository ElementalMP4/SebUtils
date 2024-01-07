package main.java.elementalmp4.listener;

import main.java.elementalmp4.service.AfkService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (playerHasMoved(event)) {
            AfkService.updatePlayerMovement(event.getPlayer().getName());
        }
    }

    private boolean playerHasMoved(PlayerMoveEvent e) {
        return e.getTo().getBlockX() != e.getFrom().getBlockX()
                || e.getTo().getBlockY() != e.getFrom().getBlockY()
                || e.getTo().getBlockZ() != e.getFrom().getBlockZ();
    }

}
