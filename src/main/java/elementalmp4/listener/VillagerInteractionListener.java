package main.java.elementalmp4.listener;

import main.java.elementalmp4.annotation.SebUtilsListener;
import main.java.elementalmp4.service.MerchantService;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

@SebUtilsListener
public class VillagerInteractionListener implements Listener {

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType() == EntityType.VILLAGER) {
            Villager villager = (Villager) event.getRightClicked();
            if (villager.getProfession() == Villager.Profession.NITWIT) {
                Player player = event.getPlayer();
                MerchantService.showSuperSecretShop(player);
            }
        }
    }

}
