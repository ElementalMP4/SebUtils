package main.java.elementalmp4.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlotCreateRequest extends TimedRequest {

    private final int x_a;
    private final int y_a;
    private int x_b;
    private int y_b;

    private final Player owner;
    private final String world;

    public PlotCreateRequest(Player owner, String world, int x_a, int y_a) {
        super(120000);
        this.x_a = x_a;
        this.y_a = y_a;
        this.owner = owner;
        this.world = world;
    }

    public boolean addSecondPoint(int x_b, int y_b) {
        this.x_b = x_b;
        this.y_b = y_b;
        return validateCoords();
    }

    private boolean validateCoords() {
        return x_a != x_b && y_a != y_b;
    }

    public String getPlayerName() {
        return this.owner.getName();
    }

    public String getWorld() {
        return this.world;
    }

    public int getXA() {
        return x_a;
    }

    public int getXB() {
        return x_b;
    }

    public int getYA() {
        return y_a;
    }

    public int getYB() {
        return y_b;
    }

    @Override
    public void whenExpired() {
        this.owner.sendMessage(ChatColor.RED + "Plot creation request expired");
    }
}