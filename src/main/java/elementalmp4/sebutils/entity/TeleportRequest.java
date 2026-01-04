package main.java.elementalmp4.sebutils.entity;

import main.java.elementalmp4.sebutils.service.TeleportService;
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
        player.sendMessage(NamedTextColor.GREEN + "Teleported " + NamedTextColor.YELLOW + player.getName() + NamedTextColor.GREEN + " to " + NamedTextColor.YELLOW + target.getName());
        target.sendMessage(NamedTextColor.GREEN + "Teleported " + NamedTextColor.YELLOW + player.getName() + NamedTextColor.GREEN + " to " + NamedTextColor.YELLOW + target.getName());
    }

    public void deny(Player denier) {
        player.sendMessage(NamedTextColor.YELLOW + (denier.getName().equals(player.getName()) ? "You" : denier.getName())
                + NamedTextColor.RED + " denied teleport request");
        target.sendMessage(NamedTextColor.YELLOW + (denier.getName().equals(target.getName()) ? "You" : denier.getName())
                + NamedTextColor.RED + " denied teleport request");
    }

    public String getAuthority() {
        return authority;
    }

    public boolean playerIsInvolved(String name) {
        return player.getName().equals(name) || target.getName().equals(name) || authority.equals(name);
    }

    @Override
    public void whenExpired() {
        player.sendMessage(NamedTextColor.RED + "Teleport request expired");
        target.sendMessage(NamedTextColor.RED + "Teleport request expired");
    }
}
