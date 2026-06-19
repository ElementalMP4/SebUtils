package main.java.elementalmp4.sebutils.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class WhitelistCache {

    private static final Set<UUID> WHITELIST = ConcurrentHashMap.newKeySet();

    public synchronized static boolean isWhitelisted(UUID uuid) {
        return WHITELIST.contains(uuid);
    }

    public synchronized static void refresh() {
        WHITELIST.clear();

        for (OfflinePlayer player : Bukkit.getWhitelistedPlayers()) {
            WHITELIST.add(player.getUniqueId());
        }
    }
}