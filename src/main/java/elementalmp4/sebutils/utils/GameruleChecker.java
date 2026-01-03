package main.java.elementalmp4.sebutils.utils;

import org.bukkit.GameRules;
import org.bukkit.World;

public class GameruleChecker {

    public static boolean isKeepInventory(World world) {
        return Boolean.TRUE.equals(world.getGameRuleValue(GameRules.KEEP_INVENTORY));
    }

}
