package main.java.elementalmp4.service;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class PVPToggleService {

    private static final HashMap<String, Boolean> PVP_TOGGLE_CACHE = new HashMap<>();

    public static boolean playerHasDisabledPvp(Player player) {
        if (PVP_TOGGLE_CACHE.containsKey(player.getName())) return false;
        else return PVP_TOGGLE_CACHE.get(player.getName());
    }

}