package main.java.elementalmp4.sebutils.entity;

import main.java.elementalmp4.sebutils.SebUtils;
import main.java.elementalmp4.sebutils.service.TeleportService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Grave {

    private final Location location;
    private final UUID uuid;

    public Grave(int x, int y, int z, String worldName) {
        World world = SebUtils.getPlugin().getServer().getWorld(worldName);
        location = new Location(world, x, y, z);
        uuid = UUID.randomUUID();
    }

    public Grave(int x, int y, int z, String worldName, String id) {
        World world = SebUtils.getPlugin().getServer().getWorld(worldName);
        location = new Location(world, x, y, z);
        uuid = UUID.fromString(id);
    }

    public void teleport(Player player) {
        player.teleport(location);
        TeleportService.playTeleportEffects(player);
    }

    public String getId() {
        return uuid.toString();
    }

    public Component toChatComponent() {
        Component coords = Component.text(location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ(), NamedTextColor.RED);
        Component teleportComponent = Component.text(" [TELEPORT]", NamedTextColor.YELLOW, TextDecoration.BOLD)
                .hoverEvent(HoverEvent.showText(Component.text(uuid.toString())))
                .clickEvent(ClickEvent.runCommand("/tpgrave " + uuid));
        return coords.append(teleportComponent);
    }
}