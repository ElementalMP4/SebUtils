package main.java.elementalmp4.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Plot {

    private final int x_a;
    private final int x_b;
    private final int y_a;
    private final int y_b;
    private final int plotId;
    private final String world;

    public Plot(ResultSet rs) throws SQLException {
        x_a = rs.getInt("bound_x_a");
        x_b = rs.getInt("bound_x_b");
        y_a = rs.getInt("bound_y_a");
        y_b = rs.getInt("bound_y_b");
        world = rs.getString("world");
        plotId = rs.getInt("plot_id");
    }

    public String getWorld() {
        return world;
    }

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
}
