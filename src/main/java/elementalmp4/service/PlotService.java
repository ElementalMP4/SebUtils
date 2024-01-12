package main.java.elementalmp4.service;

import main.java.elementalmp4.SebUtils;
import main.java.elementalmp4.utils.Plot;
import main.java.elementalmp4.utils.PlotCreateRequest;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerInteractEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlotService {

    private static final String FIND_BOUNDS_CONTAINING_POINT_NOT_OWNED = """
            SELECT
                *
            FROM
                block_locker
            WHERE
                (
                    (
                        {{input_pos_x}} <= bound_x_a
                        AND {{input_pos_x}} >= bound_x_b
                    )
                    OR (
                        {{input_pos_x}} <= bound_x_b
                        AND {{input_pos_x}} >= bound_x_a
                    )
                )
                AND (
                    (
                        {{input_pos_y}} <= bound_y_a
                        AND {{input_pos_y}} >= bound_y_b
                    )
                    OR (
                        {{input_pos_y}} <= bound_y_b
                        AND {{input_pos_y}} >= bound_y_a
                    )
                ) AND owner != '{{username}}'
                AND world = '{{world}}';
            """;

    private static final String FIND_BOUNDS_CONTAINING_POINT_OWNED = """
            SELECT
                *
            FROM
                block_locker
            WHERE
                (
                    (
                        {{input_pos_x}} <= bound_x_a
                        AND {{input_pos_x}} >= bound_x_b
                    )
                    OR (
                        {{input_pos_x}} <= bound_x_b
                        AND {{input_pos_x}} >= bound_x_a
                    )
                )
                AND (
                    (
                        {{input_pos_y}} <= bound_y_a
                        AND {{input_pos_y}} >= bound_y_b
                    )
                    OR (
                        {{input_pos_y}} <= bound_y_b
                        AND {{input_pos_y}} >= bound_y_a
                    )
                ) AND owner = '{{username}}'
                AND world = '{{world}}';
            """;

    private static final Map<String, PlotCreateRequest> requests = new HashMap<>();

    static {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(PlotService::expireRequests, 0, 1, TimeUnit.SECONDS);
    }

    private static void expireRequests() {
        List<PlotCreateRequest> expired = requests.keySet().stream()
                .map(PlotService.requests::get)
                .filter(PlotCreateRequest::expired).toList();
        for (PlotCreateRequest t : expired) {
            requests.remove(t.getPlayer());
        }
    }

    public static Optional<String> blockIsOwned(String player, int x, int y, String world) {
        try (Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(createSql(FIND_BOUNDS_CONTAINING_POINT_NOT_OWNED, x, y, player, world));
            if (rs.next()) {
                return Optional.of(rs.getString("owner"));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<Plot> deletePlotIfInside(String player, String world, int x, int y) {
        try (Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(createSql(FIND_BOUNDS_CONTAINING_POINT_NOT_OWNED, x, y, player, world));
            if (rs.next()) {
                Plot p = new Plot(rs);
                stmt.executeUpdate("DELETE FROM block_locker WHERE plot_id = %d".formatted(rs.getInt("plot_id")));
                return Optional.of(p);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static String createSql(String sql, int x, int y, String username, String world) {
        return sql
                .replace("{{input_pos_x}}", String.valueOf(x))
                .replace("{{input_pos_y}}", String.valueOf(y))
                .replace("{{username}}", username)
                .replace("{{world}}", world);
    }

    public static void handleIronNuggetClick(PlayerInteractEvent e) {
        if (requests.containsKey(e.getPlayer().getName())) {
            PlotCreateRequest request = requests.get(e.getPlayer().getName());
            boolean plotIsValid = request.addSecondPoint(e.getClickedBlock().getX(), e.getClickedBlock().getZ());
            if (plotIsValid) {
                Optional<String> possibleCollisionOwner = tryCreatePlot(request);
                if (possibleCollisionOwner.isPresent()) {
                    e.getPlayer().sendMessage(ChatColor.RED + "Your plot overlaps " + ChatColor.GOLD
                            + possibleCollisionOwner.get() + ChatColor.RED + "'s plot");
                } else {
                    e.getPlayer().sendMessage(ChatColor.GREEN + "Created plot from " + ChatColor.YELLOW +
                            request.getXA() + " " + request.getYA() + ChatColor.GREEN + " to " + ChatColor.YELLOW +
                            request.getXB() + " " + request.getYB());
                }
            } else {
                e.getPlayer().sendMessage(ChatColor.RED + "Your plot is invalid! Try making it larger");
            }
            requests.remove(e.getPlayer().getName());
        } else {
            int x = e.getClickedBlock().getX();
            int y = e.getClickedBlock().getZ();
            requests.put(e.getPlayer().getName(), new PlotCreateRequest(
                    e.getPlayer().getName(),
                    e.getPlayer().getWorld().getName(),
                    x, y
            ));
            e.getPlayer().sendMessage(ChatColor.GOLD + "Started a new plot at " + ChatColor.YELLOW + x + ", " + y);
        }
    }

    private static Optional<String> tryCreatePlot(PlotCreateRequest r) {
        List<List<Integer>> permutations = List.of(
                List.of(r.getXA(), r.getYA()),
                List.of(r.getXA(), r.getYB()),
                List.of(r.getXB(), r.getYA()),
                List.of(r.getXB(), r.getYB())
        );

        for (List<Integer> permutation : permutations) {
            Optional<String> blockOwner = blockIsOwned(r.getPlayer(), permutation.get(0), permutation.get(1), r.getWorld());
            if (blockOwner.isPresent()) return blockOwner;
        }

        try (Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement()) {
            stmt.executeUpdate("INSERT INTO block_locker(owner, world, bound_x_a, bound_y_a, bound_x_b, bound_y_b) VALUES ('%s', '%s', %d, %d, %d, %d)"
                    .formatted(r.getPlayer(), r.getWorld(), r.getXA(), r.getYA(), r.getXB(), r.getYB()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    public static List<Plot> getUserPlots(String player) {
        try (Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM block_locker WHERE owner = '%s'".formatted(player));
            List<Plot> plots = new ArrayList<>();
            while (rs.next()) {
                plots.add(new Plot(rs));
            }
            return plots;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
