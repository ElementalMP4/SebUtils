package main.java.elementalmp4.sebutils.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class StatusCache {

    private static final Set<UUID> WHITELIST = ConcurrentHashMap.newKeySet();
    private static final Set<UUID> BANNED = ConcurrentHashMap.newKeySet();

    public synchronized static boolean isWhitelisted(UUID uuid) {
        return WHITELIST.contains(uuid);
    }

    public synchronized static boolean isBanned(UUID uuid) {
        return BANNED.contains(uuid);
    }

    public synchronized static void refresh() {
        WHITELIST.clear();
        BANNED.clear();

        for (OfflinePlayer player : Bukkit.getWhitelistedPlayers()) {
            WHITELIST.add(player.getUniqueId());
        }

        for (OfflinePlayer player : Bukkit.getBannedPlayers()) {
            BANNED.add(player.getUniqueId());
        }
    }
}