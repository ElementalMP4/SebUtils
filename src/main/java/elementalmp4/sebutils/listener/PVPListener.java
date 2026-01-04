package main.java.elementalmp4.sebutils.listener;

import main.java.elementalmp4.sebutils.config.GlobalConfig;
import main.java.elementalmp4.sebutils.annotation.SebUtilsListener;
import main.java.elementalmp4.sebutils.service.GlobalConfigService;
import main.java.elementalmp4.sebutils.service.PVPToggleService;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@SebUtilsListener
public class PVPListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!isPlayerVersusPlayer(event)) return;
        if (GlobalConfigService.getAsBoolean(GlobalConfig.PVP_TOGGLE_ENABLED)) {
            Player attacker = (Player) event.getDamager();
            Player target = (Player) event.getEntity();

            if (PVPToggleService.playerHasDisabledPvp(attacker.getName())) {
                attacker.sendMessage(ChatColor.RED + "You cannot attack other players if you have disabled PVP!");
                event.setCancelled(true);
            } else if (PVPToggleService.playerHasDisabledPvp(target.getName())) {
                attacker.sendMessage(ChatColor.RED + target.getName() + " has PVP disabled!");
                event.setCancelled(true);
            }
        }
    }

    private boolean isPlayerVersusPlayer(EntityDamageByEntityEvent event) {
        return event.getEntity() instanceof Player && event.getDamager() instanceof Player;
    }

}
