package main.java.elementalmp4.utils;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

public class Home {

    private final int x;
    private final int y;
    private final int z;

    private final String dimension;
    private final String name;

    public Home(String dimension, String name, int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public TextComponent toChatComponent(boolean isAdmin) {
        TextComponent message = new TextComponent();

        TextComponent teleportComponent = new TextComponent("" + ChatColor.YELLOW  + ChatColor.BOLD + name + ": " + ChatColor.RESET);
        teleportComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to teleport!").create()));

        if (isAdmin) teleportComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + x + " " + y + " " + z));
        else teleportComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home " + name));

        message.addExtra(teleportComponent);
        message.addExtra("" + ChatColor.GREEN + x + " " + y + " " + z + " " + ChatColor.AQUA + dimension);
        return message;
    }

}
