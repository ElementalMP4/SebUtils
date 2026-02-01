package main.java.elementalmp4.sebutils.utils;

import org.bukkit.Material;

import java.util.EnumMap;
import java.util.Map;

public final class BlockColorMap {

    private static final Map<Material, Integer> COLORS =
            new EnumMap<>(Material.class);

    private static final int DEFAULT_COLOR = 0xFF2F2F2F; // dark gray

    static {
        // Natural terrain
        put(0xFF5DAA3A,
                Material.GRASS_BLOCK,
                Material.SHORT_GRASS,
                Material.TALL_GRASS,
                Material.FERN,
                Material.LARGE_FERN);

        put(0xFF8B5A2B,
                Material.DIRT,
                Material.COARSE_DIRT,
                Material.ROOTED_DIRT,
                Material.MUD);

        put(0x946a2f,
                Material.DIRT_PATH
        );

        put(0x472e0b,
                Material.FARMLAND
        );

        put(0xFF7F7F7F,
                Material.STONE,
                Material.COBBLESTONE,
                Material.ANDESITE,
                Material.DIORITE,
                Material.GRANITE,
                Material.DEEPSLATE,
                Material.COBBLED_DEEPSLATE);

        put(0xFF8A8A8A,
                Material.GRAVEL);

        // Badlands
        put(0xFF985E44, Material.TERRACOTTA);
        put(0xFFD07E56, Material.ORANGE_TERRACOTTA);
        put(0xFF8E3C2E, Material.RED_TERRACOTTA);
        put(0xFF997056, Material.BROWN_TERRACOTTA);

        put(0xFFE5D18C,
                Material.SAND,
                Material.RED_SAND,
                Material.SANDSTONE,
                Material.CUT_SANDSTONE,
                Material.SMOOTH_SANDSTONE);

        put(0xFF3F76E4,
                Material.WATER
        );

        // Wood
        put(0xFF8A5A2D,
                Material.OAK_LOG, Material.OAK_WOOD, Material.STRIPPED_OAK_LOG, Material.STRIPPED_OAK_WOOD,
                Material.OAK_PLANKS, Material.OAK_FENCE, Material.OAK_FENCE_GATE, Material.OAK_DOOR, Material.OAK_TRAPDOOR,
                Material.OAK_STAIRS, Material.OAK_SLAB
        );

        put(0xFF7B5830,
                Material.SPRUCE_LOG, Material.SPRUCE_WOOD, Material.STRIPPED_SPRUCE_LOG, Material.STRIPPED_SPRUCE_WOOD,
                Material.SPRUCE_PLANKS, Material.SPRUCE_FENCE, Material.SPRUCE_FENCE_GATE, Material.SPRUCE_DOOR, Material.SPRUCE_TRAPDOOR,
                Material.SPRUCE_STAIRS, Material.SPRUCE_SLAB
        );

        put(0xFFDECA9C,
                Material.BIRCH_LOG, Material.BIRCH_WOOD, Material.STRIPPED_BIRCH_LOG, Material.STRIPPED_BIRCH_WOOD,
                Material.BIRCH_PLANKS, Material.BIRCH_FENCE, Material.BIRCH_FENCE_GATE, Material.BIRCH_DOOR, Material.BIRCH_TRAPDOOR,
                Material.BIRCH_STAIRS, Material.BIRCH_SLAB
        );

        put(0xFF9B6B48,
                Material.JUNGLE_LOG, Material.JUNGLE_WOOD, Material.STRIPPED_JUNGLE_LOG, Material.STRIPPED_JUNGLE_WOOD,
                Material.JUNGLE_PLANKS, Material.JUNGLE_FENCE, Material.JUNGLE_FENCE_GATE, Material.JUNGLE_DOOR, Material.JUNGLE_TRAPDOOR,
                Material.JUNGLE_STAIRS, Material.JUNGLE_SLAB
        );

        put(0xFFD87B49,
                Material.ACACIA_LOG, Material.ACACIA_WOOD, Material.STRIPPED_ACACIA_LOG, Material.STRIPPED_ACACIA_WOOD,
                Material.ACACIA_PLANKS, Material.ACACIA_FENCE, Material.ACACIA_FENCE_GATE, Material.ACACIA_DOOR, Material.ACACIA_TRAPDOOR,
                Material.ACACIA_STAIRS, Material.ACACIA_SLAB
        );

        put(0xFF6B3B26,
                Material.DARK_OAK_LOG, Material.DARK_OAK_WOOD, Material.STRIPPED_DARK_OAK_LOG, Material.STRIPPED_DARK_OAK_WOOD,
                Material.DARK_OAK_PLANKS, Material.DARK_OAK_FENCE, Material.DARK_OAK_FENCE_GATE, Material.DARK_OAK_DOOR, Material.DARK_OAK_TRAPDOOR,
                Material.DARK_OAK_STAIRS, Material.DARK_OAK_SLAB
        );

        put(0xFF7D5A48,
                Material.MANGROVE_LOG, Material.MANGROVE_WOOD, Material.STRIPPED_MANGROVE_LOG, Material.STRIPPED_MANGROVE_WOOD,
                Material.MANGROVE_PLANKS, Material.MANGROVE_FENCE, Material.MANGROVE_FENCE_GATE, Material.MANGROVE_DOOR, Material.MANGROVE_TRAPDOOR,
                Material.MANGROVE_STAIRS, Material.MANGROVE_SLAB
        );

        put(0xFFD59A8B,
                Material.CHERRY_LOG, Material.CHERRY_WOOD, Material.STRIPPED_CHERRY_LOG, Material.STRIPPED_CHERRY_WOOD,
                Material.CHERRY_PLANKS, Material.CHERRY_FENCE, Material.CHERRY_FENCE_GATE, Material.CHERRY_DOOR, Material.CHERRY_TRAPDOOR,
                Material.CHERRY_STAIRS, Material.CHERRY_SLAB
        );

        // Leaves - each type with distinct color
        put(0xFF4E7F3A, Material.OAK_LEAVES);           // Standard green
        put(0xFF3D5F2E, Material.SPRUCE_LEAVES);        // Dark green
        put(0xFF6BA85E, Material.BIRCH_LEAVES);         // Bright green
        put(0xFF4A8C3B, Material.JUNGLE_LEAVES);        // Rich green
        put(0xFF6E9E4D, Material.ACACIA_LEAVES);        // Olive green
        put(0xFF3A5528, Material.DARK_OAK_LEAVES);      // Very dark green
        put(0xFF5A8847, Material.MANGROVE_LEAVES);      // Medium green
        put(0xFFFFB7D5, Material.CHERRY_LEAVES);        // Pink
        put(0xFF5C8F4D, Material.AZALEA_LEAVES);        // Greenish
        put(0xFFD98FCC, Material.FLOWERING_AZALEA_LEAVES); // Light pink-purple

        // Flowers and petals
        put(0xFFFFE4E1, Material.PINK_PETALS);          // Light pink
        put(0xFFFFD700, Material.DANDELION);            // Yellow
        put(0xFFFF6B6B, Material.POPPY);                // Red
        put(0xFF6BA3FF, Material.BLUE_ORCHID);          // Blue
        put(0xFFB5B5B5, Material.ALLIUM);               // Purple-gray
        put(0xFFFF9999, Material.AZURE_BLUET);          // Light pink-white
        put(0xFFFF4444, Material.RED_TULIP);            // Red
        put(0xFFFFAA66, Material.ORANGE_TULIP);         // Orange
        put(0xFFE5E5E5, Material.WHITE_TULIP);          // White
        put(0xFFFFB5C5, Material.PINK_TULIP);           // Pink
        put(0xFFFFE5B4, Material.OXEYE_DAISY);          // White-yellow
        put(0xFFFF8888, Material.CORNFLOWER);           // Blue
        put(0xFFFF6666, Material.LILY_OF_THE_VALLEY);   // White
        put(0xFFD4AF37, Material.SUNFLOWER);            // Yellow
        put(0xFFCC88DD, Material.LILAC);                // Purple
        put(0xFFFF99AA, Material.ROSE_BUSH);            // Pink-red
        put(0xFFCC99DD, Material.PEONY);                // Pink-purple

        // Ores & metals
        put(0xFFB87333,
                Material.COPPER_ORE,
                Material.DEEPSLATE_COPPER_ORE,
                Material.COPPER_BLOCK,
                Material.CUT_COPPER,
                Material.EXPOSED_COPPER,
                Material.WEATHERED_COPPER,
                Material.OXIDIZED_COPPER
        );

        put(0xFFAAAAAA,
                Material.IRON_ORE,
                Material.DEEPSLATE_IRON_ORE,
                Material.IRON_BLOCK
        );

        put(0xFFE6C84F,
                Material.GOLD_ORE,
                Material.DEEPSLATE_GOLD_ORE,
                Material.GOLD_BLOCK
        );

        // Man-made
        put(0xFF8C8C8C,
                Material.STONE_BRICKS,
                Material.CRACKED_STONE_BRICKS,
                Material.MOSSY_STONE_BRICKS,
                Material.BRICKS,
                Material.NETHER_BRICKS
        );

        put(0xFF6B6B6B,
                Material.POLISHED_ANDESITE,
                Material.POLISHED_DIORITE,
                Material.POLISHED_GRANITE,
                Material.SMOOTH_STONE
        );

        // Nether
        put(0xFF8E3B2D,
                Material.NETHERRACK,
                Material.NETHER_WART_BLOCK,
                Material.CRIMSON_NYLIUM,
                Material.WARPED_NYLIUM
        );

        put(0xFF4C6E5A,
                Material.WARPED_WART_BLOCK,
                Material.WARPED_STEM
        );

        // End
        put(0xFFEDE3A1,
                Material.END_STONE
        );

        put(0xFFAB53E6,
                Material.PURPUR_BLOCK,
                Material.PURPUR_PILLAR,
                Material.PURPUR_SLAB,
                Material.PURPUR_STAIRS
        );

        // Special
        put(0xFF3A3A3A,
                Material.BEDROCK
        );

        // Snow
        put(0xFFFFFFFF,
                Material.SNOW_BLOCK,
                Material.SNOW
        );

        // Ice
        put(0xFFAFD8FF, Material.ICE);
        put(0xFF8CB4D4, Material.PACKED_ICE);
        put(0xFF9DC3E0, Material.FROSTED_ICE);
        put(0xFF4D7FBF, Material.BLUE_ICE);

    }

    private static void put(int color, Material... materials) {
        for (Material m : materials) {
            COLORS.put(m, color);
        }
    }

    private BlockColorMap() {
        // utility
    }

    public static int get(Material material) {
        return COLORS.getOrDefault(material, DEFAULT_COLOR);
    }
}