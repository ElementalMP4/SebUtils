package main.java.elementalmp4.sebutils.entity;

import main.java.elementalmp4.sebutils.service.TeleportService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class TeleportRequest extends EphemeralObject {

    private final Player player;
    private final Player target;
    private final String authority;

    public TeleportRequest(Player player, Player target, String authority) {
        super(60000);
        this.player = player;
        this.target = target;
        this.authority = authority;
    }

    public void authorise() {
        player.teleport(target);
        TeleportService.playTeleportEffects(player);
        TeleportService.playTeleportEffects(target);
        Component message = Component.text("Teleported ", NamedTextColor.GREEN)
                .append(Component.text(player.getName(), NamedTextColor.YELLOW))
                .append(Component.text(" to ", NamedTextColor.GREEN))
                .append(Component.text(target.getName(), NamedTextColor.YELLOW));
        player.sendMessage(message);
        target.sendMessage(message);
    }

    public void deny(Player denier) {
        String denierName = denier.getName().equals(player.getName()) ? "You" : denier.getName();
        Component denyMessage = Component.text(denierName, NamedTextColor.YELLOW)
                .append(Component.text(" denied teleport request", NamedTextColor.RED));
        player.sendMessage(denyMessage);
        
        denierName = denier.getName().equals(target.getName()) ? "You" : denier.getName();
        denyMessage = Component.text(denierName, NamedTextColor.YELLOW)
                .append(Component.text(" denied teleport request", NamedTextColor.RED));
        target.sendMessage(denyMessage);
    }

    public String getAuthority() {
        return authority;
    }

    public boolean playerIsInvolved(String name) {
        return player.getName().equals(name) || target.getName().equals(name) || authority.equals(name);
    }

    @Override
    public void whenExpired() {
        Component expiredMessage = Component.text("Teleport request expired", NamedTextColor.RED);
        player.sendMessage(expiredMessage);
        target.sendMessage(expiredMessage);
    }
}
