package main.java.elementalmp4.sebutils.modules;

import io.javalin.http.Context;
import main.java.elementalmp4.sebutils.entity.ChunkPosition;
import main.java.elementalmp4.sebutils.utils.BlockColorMap;
import main.java.elementalmp4.sebutils.utils.NamedThreadFactory;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.concurrent.*;

import static main.java.elementalmp4.sebutils.SebUtils.getPlugin;
import static main.java.elementalmp4.sebutils.SebUtils.getPluginLogger;

public class MapModule extends AbstractModule {

    private final Map<String, World> worlds = new ConcurrentHashMap<>();

    private ExecutorService ioPool;
    private BukkitTask scanTask;

    @Override
    protected void onStart() {
        Plugin plugin = getPlugin();

        for (World world : Bukkit.getWorlds()) {
            worlds.put(world.getName(), world);
            try {
                Files.createDirectories(tileDirFor(world.getName()));
            } catch (IOException e) {
                getPluginLogger().severe("Failed to create tile dir for " + world.getName());
            }
        }

        ioPool = Executors.newFixedThreadPool(Math.max(8, Runtime.getRuntime().availableProcessors() / 2), new NamedThreadFactory("map-writer"));
        scanTask = Bukkit.getScheduler().runTaskTimer(plugin, this::scanForUnrenderedChunks, 200L, 1200L);

        getPluginLogger().info("Map module started");
    }

    @Override
    protected void onStop() {
        if (scanTask != null) scanTask.cancel();
        if (ioPool != null) ioPool.shutdownNow();
        getPluginLogger().info("Map module disabled");
    }

    public void markForRender(Chunk chunk) {
        ioPool.submit(() -> writeBaseTile(chunk));
    }

    private void writeBaseTile(Chunk chunk) {
        int[] pixels = computeChunkPixels(chunk);
        ChunkPosition pos = new ChunkPosition(chunk.getWorld().getName(), chunk.getX(), chunk.getZ(), 0);

        try {
            Path dir = tileDirFor(pos.world());
            Files.createDirectories(dir);

            BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
            img.setRGB(0, 0, 16, 16, pixels, 0, 16);

            BufferedImage scaled = scaleToTileSize(img);

            Path tmp = dir.resolve(getTileName(pos.x(), pos.z()) + ".tmp");
            Path outPath = dir.resolve(getTileName(pos.x(), pos.z()));

            ImageIO.write(scaled, "PNG", tmp.toFile());
            Files.move(tmp, outPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            getPluginLogger().warning("Failed to write base tile for chunk " + pos.x() + "," + pos.z() + ": " + e.getMessage());
        }
    }

    private int[] computeChunkPixels(Chunk chunk) {
        int[] pixels = new int[16 * 16];
        int baseX = chunk.getX() << 4;
        int baseZ = chunk.getZ() << 4;
        World world = chunk.getWorld();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = world.getMaxHeight() - 1; y >= world.getMinHeight(); y--) {
                    Block block = world.getBlockAt(baseX + x, y, baseZ + z);
                    if (!block.isEmpty()) {
                        pixels[z * 16 + x] = BlockColorMap.get(block.getType());
                        break;
                    }
                }
            }
        }
        return pixels;
    }

    private BufferedImage scaleToTileSize(BufferedImage src) {
        BufferedImage out = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(src, 0, 0, 128, 128, null);
        g.dispose();
        return out;
    }

    public void serveTile(Context ctx) {
        String world = ctx.pathParam("world");
        int zoom = Integer.parseInt(ctx.pathParam("zoom"));
        int x = Integer.parseInt(ctx.pathParam("x"));
        int z = Integer.parseInt(ctx.pathParam("z"));

        try {
            BufferedImage tile = renderZoomTileOnTheFly(world, zoom, x, z);
            ctx.header("Cache-Control", "public, max-age=0");
            ctx.header("Content-Type", "image/png");
            ImageIO.write(tile, "PNG", ctx.res().getOutputStream());
        } catch (IOException e) {
            getPluginLogger().warning("Failed to render tile " + world + "/" + zoom + "/" + x + "_" + z + ": " + e.getMessage());
            ctx.status(500);
        }
    }

    private BufferedImage renderZoomTileOnTheFly(String world, int zoom, int x, int z) throws IOException {
        int tilesPerZoom = 1 << zoom;  // 2^zoom
        int outSize = 128;
        int baseTileSize = outSize / tilesPerZoom;

        BufferedImage out = new BufferedImage(outSize, outSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();

        // Fill missing tiles with black then overwrite with tile if exists
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, outSize, outSize);

        for (int dx = 0; dx < tilesPerZoom; dx++) {
            for (int dz = 0; dz < tilesPerZoom; dz++) {
                int bx = (x << zoom) + dx;
                int bz = (z << zoom) + dz;

                Path tilePath = tileDirFor(world).resolve(getTileName(bx, bz));
                if (!Files.exists(tilePath)) continue;

                BufferedImage baseTile = ImageIO.read(tilePath.toFile());
                if (baseTile == null) continue;

                g.drawImage(baseTile, dx * baseTileSize, dz * baseTileSize, baseTileSize, baseTileSize, null);
            }
        }

        g.dispose();
        return out;
    }

    private void scanForUnrenderedChunks() {
        for (World world : worlds.values()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                Path tile = tileDirFor(world.getName()).resolve(getTileName(chunk.getX(), chunk.getZ()));
                if (!Files.exists(tile)) {
                    markForRender(chunk);
                }
            }
        }
    }

    private Path tileDirFor(String world) {
        return getPlugin().getDataFolder().toPath().resolve("tiles").resolve(world);
    }

    private static String getTileName(int x, int z) {
        return "%d_%d.png".formatted(x, z);
    }

    public static boolean tileExists(Path worldDir, int chunkX, int chunkZ) {
        return Files.exists(worldDir.resolve(getTileName(chunkX, chunkZ)));
    }
}
