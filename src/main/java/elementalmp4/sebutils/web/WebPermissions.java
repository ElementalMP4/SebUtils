package main.java.elementalmp4.sebutils.web;

import io.javalin.http.Context;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public final class WebPermissions {

    private static boolean isOp(String username) {
        if (username == null) return false;

        OfflinePlayer player = Bukkit.getOfflinePlayer(username);
        return player.isOp();
    }

    public static boolean requireOp(Context ctx) {
        String user = ctx.attribute("authenticatedUser");
        if (!WebPermissions.isOp(user)) {
            ctx.status(403).result("OP required");
            return false;
        }
        return true;
    }

}
