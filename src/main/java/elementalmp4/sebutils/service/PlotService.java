package main.java.elementalmp4.sebutils.service;

import main.java.elementalmp4.sebutils.config.GlobalConfig;
import main.java.elementalmp4.sebutils.entity.Plot;
import main.java.elementalmp4.sebutils.entity.PlotCreateRequest;
import main.java.elementalmp4.sebutils.utils.NamedThreadFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static main.java.elementalmp4.sebutils.SebUtils.getDatabaseConnection;

public class PlotService {

    private static final Map<String, PlotCreateRequest> requests = new HashMap<>();

    static {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1, new NamedThreadFactory("plotreq"));
        executor.scheduleAtFixedRate(PlotService::expireRequests, 0, 1, TimeUnit.SECONDS);
    }

    private static void expireRequests() {
        List<PlotCreateRequest> expired = requests.values().stream()
                .filter(PlotCreateRequest::expired)
                .toList();
        for (PlotCreateRequest t : expired) {
            requests.remove(t.getPlayerName());
        }
    }

    public static Optional<Plot> blockIsOwnedBySomeoneElse(String player, int x, int y, String world) {
        String sql = """
                SELECT * FROM block_locker
                WHERE ((? <= bound_x_a AND ? >= bound_x_b) OR (? <= bound_x_b AND ? >= bound_x_a))
                  AND ((? <= bound_y_a AND ? >= bound_y_b) OR (? <= bound_y_b AND ? >= bound_y_a))
                  AND owner != ?
                  AND world = ?;
                """;

        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 1; i <= 4; i++) ps.setInt(i, x); // x positions
            for (int i = 5; i <= 8; i++) ps.setInt(i, y); // y positions
            ps.setString(9, player);
            ps.setString(10, world);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(new Plot(rs));
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<Plot> blockIsOwned(int x, int y, String world) {
        String sql = """
                SELECT * FROM block_locker
                WHERE ((? <= bound_x_a AND ? >= bound_x_b) OR (? <= bound_x_b AND ? >= bound_x_a))
                  AND ((? <= bound_y_a AND ? >= bound_y_b) OR (? <= bound_y_b AND ? >= bound_y_a))
                  AND world = ?;
                """;

        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 1; i <= 4; i++) ps.setInt(i, x);
            for (int i = 5; i <= 8; i++) ps.setInt(i, y);
            ps.setString(9, world);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(new Plot(rs));
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deletePlot(int id) {
        String sql = "DELETE FROM block_locker WHERE plot_id = ?";

        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createPlot(PlotCreateRequest r) {
        String sql = "INSERT INTO block_locker (owner, world, bound_x_a, bound_y_a, bound_x_b, bound_y_b) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, r.getPlayerName());
            ps.setString(2, r.getWorld());
            ps.setInt(3, r.getXA());
            ps.setInt(4, r.getYA());
            ps.setInt(5, r.getXB());
            ps.setInt(6, r.getYB());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Plot> getUserPlots(String player) {
        String sql = "SELECT * FROM block_locker WHERE owner = ?";
        List<Plot> plots = new ArrayList<>();

        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, player);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) plots.add(new Plot(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return plots;
    }

    public static Optional<Plot> getPlotByIdAndOwner(int id, String name) {
        String sql = "SELECT * FROM block_locker WHERE plot_id = ? AND owner = ?";

        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setString(2, name);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(new Plot(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    public static List<String> getPlotIdList(String player) {
        return getUserPlots(player).stream()
                .map(Plot::getId)
                .map(String::valueOf)
                .collect(Collectors.toList());
    }

    // The PlayerInteractEvent logic remains unchanged since it doesn’t use SQL directly
    public static void handleIronNuggetClick(PlayerInteractEvent e) {
        if (requests.containsKey(e.getPlayer().getName())) {
            PlotCreateRequest request = requests.get(e.getPlayer().getName());
            boolean plotIsValid = request.addSecondPoint(e.getClickedBlock().getX(), e.getClickedBlock().getZ());
            if (plotIsValid) {
                if (canCreatePlot(request, e.getPlayer())) {
                    createPlot(request);
                    Component message = Component.text("Created plot! Used ", NamedTextColor.GREEN)
                            .append(Component.text(Plot.getPlotArea(request) + " blocks", NamedTextColor.YELLOW));
                    e.getPlayer().sendMessage(message);
                }
            } else {
                e.getPlayer().sendMessage(Component.text("Your plot is invalid! Try making it larger", NamedTextColor.RED));
            }
            requests.remove(e.getPlayer().getName());
        } else {
            int x = e.getClickedBlock().getX();
            int y = e.getClickedBlock().getZ();
            requests.put(e.getPlayer().getName(), new PlotCreateRequest(
                    e.getPlayer(),
                    e.getPlayer().getWorld().getName(),
                    x, y
            ));
            e.getPlayer().sendMessage(Component.text("Started a new plot at ", NamedTextColor.GOLD)
                    .append(Component.text(x + ", " + y, NamedTextColor.YELLOW)));
        }
    }

    public static boolean canCreatePlot(PlotCreateRequest r, Player p) {
        List<List<Integer>> permutations = List.of(
                List.of(r.getXA(), r.getYA()),
                List.of(r.getXA(), r.getYB()),
                List.of(r.getXB(), r.getYA()),
                List.of(r.getXB(), r.getYB())
        );

        for (List<Integer> permutation : permutations) {
            Optional<Plot> blockOwner = blockIsOwnedBySomeoneElse(r.getPlayerName(), permutation.get(0), permutation.get(1), r.getWorld());
            if (blockOwner.isPresent()) {
                Component message = Component.text("Your plot overlaps ", NamedTextColor.RED)
                        .append(Component.text(blockOwner.get().getOwner(), NamedTextColor.GOLD))
                        .append(Component.text("'s plot", NamedTextColor.RED));
                p.sendMessage(message);
                return false;
            }
        }

        int maxPlotSize = GlobalConfigService.getAsInteger(GlobalConfig.PLOT_MAX_SIZE);
        int newPlotSize = Plot.getPlotArea(r);

        int totalPlotSize = newPlotSize + getUserPlots(p.getName()).stream()
                .mapToInt(Plot::getPlotArea)
                .sum();

        if (totalPlotSize > maxPlotSize) {
            Component message = Component.text("This plot, along with your existing plots, will exceed the maximum plot size of ", NamedTextColor.RED)
                    .append(Component.text(maxPlotSize + " blocks", NamedTextColor.GOLD));
            p.sendMessage(message);
            return false;
        }

        return true;
    }
}
