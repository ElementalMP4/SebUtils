package main.java.elementalmp4.entity;

import main.java.elementalmp4.SebUtils;
import main.java.elementalmp4.service.TeleportService;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
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

    public TextComponent toChatComponent() {
        TextComponent message = new TextComponent();
        message.addExtra("" + ChatColor.RED + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
        TextComponent teleportComponent = new TextComponent(" " + ChatColor.YELLOW + ChatColor.BOLD + "[TELEPORT]");
        teleportComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(uuid.toString()).create()));
        teleportComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpgrave " + uuid));
        message.addExtra(teleportComponent);
        return message;
    }
}