package main.java.elementalmp4.sebutils.entity;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

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

    public Component toChatComponent(boolean isAdmin) {
        Component teleportComponent = Component.text(name, NamedTextColor.YELLOW, TextDecoration.BOLD)
                .append(Component.text(": ", NamedTextColor.WHITE))
                .hoverEvent(HoverEvent.showText(Component.text("Click to teleport!")))
                .clickEvent(isAdmin ?
                    ClickEvent.runCommand("/tp " + x + " " + y + " " + z) :
                    ClickEvent.runCommand("/home " + name));

        Component coords = Component.text(x + " " + y + " " + z, NamedTextColor.GREEN)
                .append(Component.text(" " + dimension, NamedTextColor.AQUA));

        return teleportComponent.append(coords);
    }

}
