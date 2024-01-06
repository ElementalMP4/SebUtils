package main.java.elementalmp4.utils;

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

    @Override
    public String toString() {
        return ChatColor.YELLOW + this.name + ": " + ChatColor.GREEN + this.x + " " + this.y + " " + this.z + " " + ChatColor.AQUA + this.dimension;
    }

}
