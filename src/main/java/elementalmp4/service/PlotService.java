package main.java.elementalmp4.service;

import main.java.elementalmp4.GlobalConfig;
import main.java.elementalmp4.SebUtils;
import main.java.elementalmp4.utils.Plot;
import main.java.elementalmp4.utils.PlotCreateRequest;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    private static final String FIND_BOUNDS_CONTAINING_POINT = """
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
                );
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

    public static Optional<Plot> blockIsOwnedBySomeoneElse(String player, int x, int y, String world) {
        try (Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(createSql(FIND_BOUNDS_CONTAINING_POINT_NOT_OWNED, x, y, player, world));
            if (rs.next()) {
                return Optional.of(new Plot(rs));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<Plot> blockIsOwned(int x, int y, String world) {
        try (Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(createSql(FIND_BOUNDS_CONTAINING_POINT, x, y, "", world));
            if (rs.next()) {
                return Optional.of(new Plot(rs));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deletePlot(int id) {
        try (Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement()) {
            stmt.executeUpdate("DELETE FROM block_locker WHERE plot_id = %d".formatted(id));
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
                if (canCreatePlot(request, e.getPlayer())) {
                    createPlot(request);
                    e.getPlayer().sendMessage(ChatColor.GREEN + "Created plot! Used " + ChatColor.YELLOW +
                            Plot.getPlotArea(request) + ChatColor.GREEN + " blocks");
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

    public static boolean canCreatePlot(PlotCreateRequest r, Player p) {
        //Check for overlaps
        List<List<Integer>> permutations = List.of(
                List.of(r.getXA(), r.getYA()),
                List.of(r.getXA(), r.getYB()),
                List.of(r.getXB(), r.getYA()),
                List.of(r.getXB(), r.getYB())
        );

        for (List<Integer> permutation : permutations) {
            Optional<Plot> blockOwner = blockIsOwnedBySomeoneElse(r.getPlayer(), permutation.get(0), permutation.get(1), r.getWorld());
            if (blockOwner.isPresent()) {
                p.sendMessage(ChatColor.RED + "Your plot overlaps " + ChatColor.GOLD
                        + blockOwner.get().getOwner() + ChatColor.RED + "'s plot");
                return false;
            }
        }

        //Check plot size
        int maxPlotSize = GlobalConfigService.getAsInteger(GlobalConfig.PLOT_MAX_SIZE);
        int newPlotSize = Plot.getPlotArea(r);

        if (newPlotSize > maxPlotSize) {
            p.sendMessage(ChatColor.RED + "This plot exceeds the maximum plot size of " + ChatColor.GOLD + maxPlotSize + " blocks");
            return false;
        }

        //Check cumulative plot size
        List<Plot> plots = getUserPlots(p.getName());
        int totalPlotSize = newPlotSize;
        for (Plot plot : plots) {
            totalPlotSize += Plot.getPlotArea(plot);
            if (totalPlotSize > maxPlotSize) {
                p.sendMessage(ChatColor.RED + "This plot, along with your existing plots, will exceed the maximum plot size of " + ChatColor.GOLD + maxPlotSize + " blocks");
                return false;
            }
        }

        return true;
    }

    private static void createPlot(PlotCreateRequest r) {
        try (Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement()) {
            stmt.executeUpdate("INSERT INTO block_locker (owner, world, bound_x_a, bound_y_a, bound_x_b, bound_y_b) VALUES ('%s', '%s', %d, %d, %d, %d)"
                    .formatted(r.getPlayer(), r.getWorld(), r.getXA(), r.getYA(), r.getXB(), r.getYB()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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

    public static Optional<Plot> getPlotByIdAndOwner(int id, String name) {
        try (Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM block_locker WHERE plot_id = %d AND owner = '%s'".formatted(id, name));
            if (rs.next()) return Optional.of(new Plot(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public static List<String> getPlotIdList(String player) {
        return PlotService.getUserPlots(player).stream()
                .map(Plot::getId)
                .map(String::valueOf)
                .collect(Collectors.toList());
    }
}
