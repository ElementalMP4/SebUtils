package main.java.elementalmp4.utils;

import main.java.elementalmp4.service.TeleportService;
import org.bukkit.entity.Player;

public class TeleportRequest extends TimedRequest {

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
    }

    public String getAuthority() {
        return authority;
    }

    public boolean playerIsInvolved(String name) {
        return player.getName().equals(name) || target.getName().equals(name) || authority.equals(name);
    }
}
