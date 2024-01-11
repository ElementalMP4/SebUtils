package main.java.elementalmp4.service;

import org.bukkit.entity.Cow;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class CowExploder {
    public static void tryExplodeCow(PlayerInteractEntityEvent e) {
        if (GlobalConfigService.getOrDefault("cows_explode", false)) {
            e.getPlayer().getWorld().createExplosion(e.getRightClicked().getLocation(), 0, false, false);
            ((Cow) e.getRightClicked()).setHealth(0);
        }
    }
}