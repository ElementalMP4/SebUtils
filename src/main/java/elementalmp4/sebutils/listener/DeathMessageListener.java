package main.java.elementalmp4.sebutils.listener;

import main.java.elementalmp4.sebutils.annotation.SebUtilsListener;
import main.java.elementalmp4.sebutils.modules.DiscordModule;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import static main.java.elementalmp4.sebutils.SebUtils.getModuleManager;

@SebUtilsListener
public class DeathMessageListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.deathMessage() == null) {
            return;
        }

        Player player = event.getEntity();
        PlainTextComponentSerializer plainSerializer = PlainTextComponentSerializer.plainText();
        String message = plainSerializer.serialize(event.deathMessage());
        getModuleManager().get(DiscordModule.class).sendDeathMessage(player, message);
    }
}
