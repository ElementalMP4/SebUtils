package main.java.elementalmp4.sebutils.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static main.java.elementalmp4.sebutils.SebUtils.getPluginLogger;

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
        Set<UUID> newWhitelist = ConcurrentHashMap.newKeySet();
        Set<UUID> newBanlist = ConcurrentHashMap.newKeySet();

        for (OfflinePlayer player : Bukkit.getWhitelistedPlayers()) {
            if (!WHITELIST.contains(player.getUniqueId())) {
                getPluginLogger().info("Adding %s to whitelist cache".formatted(player.getUniqueId()));
            }
            newWhitelist.add(player.getUniqueId());
        }

        for (OfflinePlayer player : Bukkit.getBannedPlayers()) {
            BANNED.add(player.getUniqueId());
            if (!BANNED.contains(player.getUniqueId())) {
                getPluginLogger().info("Adding %s to whitelist cache".formatted(player.getUniqueId()));
            }
            newBanlist.add(player.getUniqueId());
        }

        WHITELIST.clear();
        BANNED.clear();
        WHITELIST.addAll(newWhitelist);
        BANNED.addAll(newBanlist);
    }
}