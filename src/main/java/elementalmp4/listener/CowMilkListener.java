package main.java.elementalmp4.listener;

import main.java.elementalmp4.service.CowExploder;
import org.bukkit.Material;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class CowMilkListener implements Listener {

    @EventHandler
    public void onCowMilk(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();
        Entity possiblyCow = e.getRightClicked();
        if (!(possiblyCow instanceof Cow))
            return;
        if (!(player.getInventory().getItemInMainHand().getType().equals(Material.BUCKET)))
            return;

        e.setCancelled(true);
        CowExploder.tryExplodeCow(e);

    }

}
