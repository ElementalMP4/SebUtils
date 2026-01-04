package main.java.elementalmp4.sebutils.utils;

import main.java.elementalmp4.sebutils.SebUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class AfkStatus {

    private static final long AFK_TIMEOUT = 10 * 60 * 1000; //10 Minutes

    private final String playerName;
    private long lastActivityTime;
    private boolean isAfk;

    public AfkStatus(String playerName) {
        this.playerName = playerName;
        updateTime();
    }

    public void updateTime() {
        this.lastActivityTime = System.currentTimeMillis();
        if (this.isAfk) {
            this.isAfk = false;
            sendReturnMessage();
        }
    }

    public void checkActivity() {
        if (!this.isAfk && this.lastActivityTime <= System.currentTimeMillis() - AFK_TIMEOUT) {
            this.isAfk = true;
            sendAwayMessage();
        }
    }

    private void sendReturnMessage() {
        SebUtils.getPlugin().getServer().broadcast(Component.text(this.playerName + " is no longer AFK", NamedTextColor.GRAY));
    }

    private void sendAwayMessage() {
        SebUtils.getPlugin().getServer().broadcast(Component.text(this.playerName + " is AFK", NamedTextColor.GRAY));
    }

}
