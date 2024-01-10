package main.java.elementalmp4.utils;

import main.java.elementalmp4.SebUtils;
import org.bukkit.ChatColor;

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
        SebUtils.getPlugin().getServer().broadcastMessage(ChatColor.GRAY + this.playerName + " is no longer AFK");
    }

    private void sendAwayMessage() {
        SebUtils.getPlugin().getServer().broadcastMessage(ChatColor.GRAY + this.playerName + " is AFK");
    }

}
