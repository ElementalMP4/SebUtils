package main.java.elementalmp4.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Plot {

    private final int x_a;
    private final int x_b;
    private final int y_a;
    private final int y_b;
    private final int plotId;
    private final String world;
    private final String owner;

    public Plot(ResultSet rs) throws SQLException {
        x_a = rs.getInt("bound_x_a");
        x_b = rs.getInt("bound_x_b");
        y_a = rs.getInt("bound_y_a");
        y_b = rs.getInt("bound_y_b");
        world = rs.getString("world");
        plotId = rs.getInt("plot_id");
        owner = rs.getString("owner");
    }

    public String getWorld() {
        return world;
    }

    public String getOwner() {
        return owner;
    };

    public int getXA() {
        return x_a;
    }

    public int getXB() {
        return x_b;
    }

    public int getYA() {
        return y_a;
    }

    public int getYB() {
        return y_b;
    }

    public int getId() {
        return plotId;
    }

    public static int getPlotArea(int x_a, int y_a, int x_b, int y_b) {
        int length = Math.abs(x_b - x_a) + 1;
        int width = Math.abs(y_b - y_a) + 1;
        return length * width;
    }

    public static int getPlotArea(Plot p) {
        return getPlotArea(p.getXA(), p.getYA(), p.getXB(), p.getYB());
    }

    public static int getPlotArea(PlotCreateRequest p) {
        return getPlotArea(p.getXA(), p.getYA(), p.getXB(), p.getYB());
    }
}
