package main.java.elementalmp4.sebutils.utils;

import org.bukkit.Material;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static main.java.elementalmp4.sebutils.SebUtils.getPluginLogger;

public final class BlockColorMap {

    private static final Map<String, Integer> COLORS = new HashMap<>();
    private static final int DEFAULT_COLOR = 0xFF2F2F2F; // dark gray

    static {
        loadFromJson();

        // Overrides for things we can't easily get a colour for

        put(0xFF5DAA3A,
                Material.GRASS_BLOCK,
                Material.SHORT_GRASS,
                Material.TALL_GRASS,
                Material.FERN,
                Material.LARGE_FERN
        );

        put(0xFF3F76E4,
                Material.WATER
        );

        put(0xFFFF4D00,
                Material.LAVA
        );

        put(0xFF4E7F3A, Material.OAK_LEAVES);
        put(0xFF3D5F2E, Material.SPRUCE_LEAVES);
        put(0xFF6BA85E, Material.BIRCH_LEAVES);
        put(0xFF4A8C3B, Material.JUNGLE_LEAVES);
        put(0xFF6E9E4D, Material.ACACIA_LEAVES);
        put(0xFF3A5528, Material.DARK_OAK_LEAVES);
        put(0xFF5A8847, Material.MANGROVE_LEAVES);
        put(0xFFFFB7D5, Material.CHERRY_LEAVES);
        put(0xFF5C8F4D, Material.AZALEA_LEAVES);
        put(0xFFD98FCC, Material.FLOWERING_AZALEA_LEAVES);
    }

    private static void loadFromJson() {
        try {
            InputStream inputStream = BlockColorMap.class.getClassLoader().getResourceAsStream("block_colours.json");

            if (inputStream == null) {
                getPluginLogger().severe("block_colors.json not found!");
                return;
            }

            String jsonString = new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .collect(Collectors.joining("\n"));

            JSONObject jsonObject = new JSONObject(jsonString);
            for (String materialName : jsonObject.keySet()) {
                try {
                    String hexColor = jsonObject.getString(materialName);
                    int color = (int) Long.parseLong(hexColor.substring(2), 16);
                    COLORS.put(materialName, color);
                } catch (IllegalArgumentException e) {
                    getPluginLogger().warning("Skipping unknown material: " + materialName);
                }
            }

            inputStream.close();
            getPluginLogger().info("Loaded " + COLORS.size() + " block colors from JSON");
        } catch (Exception e) {
            getPluginLogger().severe("Error loading block colors: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private BlockColorMap() {
        // utility
    }

    private static void put(int color, Material... materials) {
        for (Material m : materials) {
            COLORS.put(m.name(), color);
        }
    }

    public static int get(Material material) {
        if (!COLORS.containsKey(material.name())) {
            return COLORS.getOrDefault(material.name() + "_TOP", DEFAULT_COLOR);
        } else {
            return COLORS.get(material.name());
        }
    }

}